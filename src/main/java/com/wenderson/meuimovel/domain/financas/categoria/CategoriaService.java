
package com.wenderson.meuimovel.domain.financas.categoria;

import com.wenderson.meuimovel.domain.financas.AutorizacaoFamiliaService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;
    private final AutorizacaoFamiliaService autorizacaoFamiliaService;

    public CategoriaService(
            CategoriaRepository repository,
            AutorizacaoFamiliaService autorizacaoFamiliaService
    ) {
        this.repository = repository;
        this.autorizacaoFamiliaService = autorizacaoFamiliaService;
    }

    @Transactional
    public DadosDetalhamentoCategoria cadastrar(
            UUID familiaId,
            UUID usuarioId,
            DadosCadastroCategoria dados
    ) {
        autorizacaoFamiliaService.verificarAcesso(familiaId, usuarioId);

        String nome = dados.nome().trim();

        verificarDuplicidade(familiaId, dados.tipo(), nome);

        Categoria categoria = new Categoria(familiaId, dados);
        repository.saveAndFlush(categoria);

        return detalhar(categoria);
    }

    @Transactional(readOnly = true)
    public List<DadosDetalhamentoCategoria> listar(
            UUID familiaId,
            UUID usuarioId
    ) {
        autorizacaoFamiliaService.verificarAcesso(familiaId, usuarioId);

        return repository
                .findByFamiliaIdAndAtivaTrueOrderByNomeAsc(familiaId)
                .stream()
                .map(this::detalhar)
                .toList();
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoCategoria buscarPorId(
            UUID familiaId,
            UUID usuarioId,
            UUID categoriaId
    ) {
        autorizacaoFamiliaService.verificarAcesso(familiaId, usuarioId);

        Categoria categoria = buscarCategoria(familiaId, categoriaId);
        return detalhar(categoria);
    }

    @Transactional
    public DadosDetalhamentoCategoria atualizar(
            UUID familiaId,
            UUID usuarioId,
            UUID categoriaId,
            DadosAtualizacaoCategoria dados
    ) {
        autorizacaoFamiliaService.verificarAcesso(familiaId, usuarioId);

        Categoria categoria = buscarCategoria(familiaId, categoriaId);

        if (!categoria.isAtiva()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é possível atualizar uma categoria inativa"
            );
        }

        String nome = dados.nome().trim();

        if (!categoria.getNome().equalsIgnoreCase(nome)) {
            verificarDuplicidade(familiaId, categoria.getTipo(), nome);
        }

        categoria.atualizar(dados);

        return detalhar(categoria);
    }

    @Transactional
    public void desativar(
            UUID familiaId,
            UUID usuarioId,
            UUID categoriaId
    ) {
        autorizacaoFamiliaService.verificarAcesso(familiaId, usuarioId);

        Categoria categoria = buscarCategoria(familiaId, categoriaId);
        categoria.desativar();
    }

    private Categoria buscarCategoria(UUID familiaId, UUID categoriaId) {
        return repository.findByIdAndFamiliaId(categoriaId, familiaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoria não encontrada"
                ));
    }

    private void verificarDuplicidade(
            UUID familiaId,
            com.wenderson.meuimovel.enums.TipoCategoria tipo,
            String nome
    ) {
        if (repository.existsByFamiliaIdAndTipoAndNomeIgnoreCase(
                familiaId, tipo, nome
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe uma categoria com esse nome e tipo"
            );
        }
    }

    private DadosDetalhamentoCategoria detalhar(Categoria categoria) {
        return new DadosDetalhamentoCategoria(
                categoria.getId(),
                categoria.getNome(),
                categoria.getTipo(),
                categoria.isAtiva()
        );
    }
}
