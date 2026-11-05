
package com.wenderson.meuimovel.infra.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.UUID;

@Component
public class SupabaseStorageClient {

    private static final String BUCKET = "documentos-imovel";
    private static final int TAMANHO_MAXIMO = 10 * 1024 * 1024;

    private final RestClient restClient;
    private final String supabaseUrl;

    public SupabaseStorageClient(
            @Value("${SUPABASE_URL}") String supabaseUrl,
            @Value("${SUPABASE_SECRET_KEY}") String secretKey) {

        this.supabaseUrl = supabaseUrl.replaceAll("/+$", "");

        this.restClient = RestClient.builder()
                .defaultHeader("apikey", secretKey)
                .build();
    }

    public String enviar(
            UUID familiaId,
            UUID documentoId,
            byte[] conteudo,
            String mimeType) {

        if (conteudo == null || conteudo.length == 0) {
            throw new IllegalArgumentException(
                    "O arquivo não pode estar vazio"
            );
        }

        if (conteudo.length > TAMANHO_MAXIMO) {
            throw new IllegalArgumentException(
                    "O arquivo não pode ultrapassar 10 MB"
            );
        }

        String caminho = gerarCaminho(
                familiaId, documentoId, mimeType
        );

        URI uri = URI.create(
                supabaseUrl
                        + "/storage/v1/object/"
                        + BUCKET + "/" + caminho
        );

        restClient.post()
                .uri(uri)
                .contentType(MediaType.parseMediaType(mimeType))
                .header("x-upsert", "false")
                .body(conteudo)
                .retrieve()
                .toBodilessEntity();

        return caminho;
    }

    public void excluir(
            UUID familiaId,
            UUID documentoId,
            String mimeType) {

        String caminho = gerarCaminho(
                familiaId, documentoId, mimeType
        );

        URI uri = URI.create(
                supabaseUrl
                        + "/storage/v1/object/"
                        + BUCKET + "/" + caminho
        );

        restClient.delete()
                .uri(uri)
                .retrieve()
                .toBodilessEntity();
    }


    public byte[] baixar(
            UUID familiaId,
            UUID documentoId,
            String mimeType) {

        String caminho = gerarCaminho(
                familiaId,
                documentoId,
                mimeType
        );

        URI uri = URI.create(
                supabaseUrl
                        + "/storage/v1/object/authenticated/"
                        + BUCKET + "/" + caminho
        );

        byte[] arquivo = restClient.get()
                .uri(uri)
                .retrieve()
                .body(byte[].class);

        if (arquivo == null || arquivo.length == 0) {
            throw new IllegalStateException(
                    "O Supabase Storage retornou um arquivo vazio"
            );
        }

        return arquivo;
    }


    private String gerarCaminho(
            UUID familiaId,
            UUID documentoId,
            String mimeType) {

        if (familiaId == null || documentoId == null) {
            throw new IllegalArgumentException(
                    "Família e documento são obrigatórios"
            );
        }

        String extensao = switch (mimeType) {
            case "application/pdf" -> "pdf";
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            default -> throw new IllegalArgumentException(
                    "Tipo de arquivo não permitido"
            );
        };

        return "familias/" + familiaId
                + "/documentos/" + documentoId
                + "." + extensao;
    }
}
