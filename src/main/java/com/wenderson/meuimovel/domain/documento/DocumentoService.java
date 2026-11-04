
package com.wenderson.meuimovel.domain.documento;

import com.wenderson.meuimovel.domain.financas.AutorizacaoFamiliaService;
import com.wenderson.meuimovel.infra.storage.SupabaseStorageClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@Service
public class DocumentoService {

    private static final String BUCKET = "documentos-imovel";
    private static final long TAMANHO_MAXIMO = 10L * 1024 * 1024;

    private final DocumentoRepository repository;
    private final SupabaseStorageClient storageClient;
    private final AutorizacaoFamiliaService autorizacao;

    public DocumentoService(
            DocumentoRepository repository,
            SupabaseStorageClient storageClient,
            AutorizacaoFamiliaService autorizacao) {

        this.repository = repository;
        this.storageClient = storageClient;
        this.autorizacao = autorizacao;
    }

    public DadosDetalhamentoDocumento cadastrar(
            UUID familiaId,
            UUID usuarioId,
            Long imovelId,
            UUID lancamentoId,
            String tipoDocumento,
            String descricao,
            MultipartFile arquivo) {

        // Primeiro verificamos se o usuário pertence à família.
        autorizacao.verificarAcesso(familiaId, usuarioId);

        // Um documento deve possuir exatamente um vínculo principal.
        if ((imovelId == null) == (lancamentoId == null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe um imóvel ou um lançamento"
            );
        }

        if (imovelId != null &&
                !repository.existeImovelVinculado(familiaId, imovelId)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Imóvel não vinculado à família"
            );
        }

        if (lancamentoId != null &&
                !repository.existeLancamentoVinculado(
                        familiaId, lancamentoId)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Lançamento não encontrado nesta família"
            );
        }

        if (tipoDocumento == null ||
                !tipoDocumento.matches("[A-Z][A-Z0-9_]{0,39}")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de documento inválido"
            );
        }

        if (descricao == null ||
                descricao.isBlank() ||
                descricao.length() > 255) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Descrição inválida"
            );
        }

        if (arquivo == null ||
                arquivo.isEmpty() ||
                arquivo.getSize() > TAMANHO_MAXIMO) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Arquivo vazio ou maior que 10 MB"
            );
        }

        String mimeType = arquivo.getContentType();

        if (!"application/pdf".equals(mimeType) &&
                !"image/jpeg".equals(mimeType) &&
                !"image/png".equals(mimeType)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Formato não permitido. Use PDF, JPG ou PNG"
            );
        }

        String nomeOriginal = arquivo.getOriginalFilename();

        if (nomeOriginal == null || nomeOriginal.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nome do arquivo obrigatório"
            );
        }

        nomeOriginal = nomeOriginal.replace('\\', '/');
        nomeOriginal = nomeOriginal.substring(
                nomeOriginal.lastIndexOf('/') + 1
        );

        if (nomeOriginal.isBlank() || nomeOriginal.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nome do arquivo inválido"
            );
        }

        byte[] conteudo;

        try {
            conteudo = arquivo.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não foi possível ler o arquivo",
                    e
            );
        }

        validarAssinatura(conteudo, mimeType);

        UUID documentoId = UUID.randomUUID();

        // Envia o arquivo para o Supabase Storage.
        String caminho = storageClient.enviar(
                familiaId,
                documentoId,
                conteudo,
                mimeType
        );


        // Registra os metadados no PostgreSQL.
        try {
            return repository.cadastrar(
                    documentoId,
                    familiaId,
                    usuarioId,
                    imovelId,
                    lancamentoId,
                    tipoDocumento,
                    descricao.trim(),
                    nomeOriginal,
                    mimeType,
                    conteudo.length,
                    BUCKET,
                    caminho
            );

        } catch (RuntimeException erroPersistencia) {

            // Se o banco falhar, tenta remover o arquivo enviado.
            try {
                storageClient.excluir(
                        familiaId,
                        documentoId,
                        mimeType
                );

            } catch (RuntimeException erroExclusao) {

                // Preserva ambos os erros para diagnóstico.
                erroPersistencia.addSuppressed(erroExclusao);
            }

            throw erroPersistencia;
        }

    }

    private void validarAssinatura(
            byte[] conteudo,
            String mimeType) {

        boolean valido = switch (mimeType) {

            case "application/pdf" ->
                    conteudo.length >= 5 &&
                            conteudo[0] == '%' &&
                            conteudo[1] == 'P' &&
                            conteudo[2] == 'D' &&
                            conteudo[3] == 'F' &&
                            conteudo[4] == '-';

            case "image/jpeg" ->
                    conteudo.length >= 3 &&
                            (conteudo[0] & 0xFF) == 0xFF &&
                            (conteudo[1] & 0xFF) == 0xD8 &&
                            (conteudo[2] & 0xFF) == 0xFF;

            case "image/png" ->
                    conteudo.length >= 8 &&
                            (conteudo[0] & 0xFF) == 0x89 &&
                            conteudo[1] == 'P' &&
                            conteudo[2] == 'N' &&
                            conteudo[3] == 'G' &&
                            (conteudo[4] & 0xFF) == 0x0D &&
                            (conteudo[5] & 0xFF) == 0x0A &&
                            (conteudo[6] & 0xFF) == 0x1A &&
                            (conteudo[7] & 0xFF) == 0x0A;

            default -> false;
        };

        if (!valido) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O conteúdo não corresponde ao tipo do arquivo"
            );
        }
    }

}
