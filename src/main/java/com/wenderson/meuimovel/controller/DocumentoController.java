
package com.wenderson.meuimovel.controller;

import com.wenderson.meuimovel.domain.documento.DadosAtualizacaoDocumento;
import com.wenderson.meuimovel.domain.documento.DadosDetalhamentoDocumento;
import com.wenderson.meuimovel.domain.documento.DocumentoService;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ContentDisposition;
import org.springframework.http.CacheControl;
import java.nio.charset.StandardCharsets;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/familias/{familiaId}/documentos")
public class DocumentoController {

    private final DocumentoService service;
    private final UsuarioAutenticado usuario;

    public DocumentoController(
            DocumentoService service,
            UsuarioAutenticado usuario) {

        this.service = service;
        this.usuario = usuario;
    }

    // CADASTRAR DOCUMENTO
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DadosDetalhamentoDocumento> cadastrar(
            @PathVariable UUID familiaId,
            @RequestParam(required = false) Long imovelId,
            @RequestParam(required = false) UUID lancamentoId,
            @RequestParam String tipoDocumento,
            @RequestParam String descricao,
            @RequestPart("arquivo") MultipartFile arquivo) {

        var documento = service.cadastrar(
                familiaId,
                usuario.obterUsuarioId(),
                imovelId,
                lancamentoId,
                tipoDocumento,
                descricao,
                arquivo
        );

        URI uri = URI.create(
                "/familias/" + familiaId
                        + "/documentos/" + documento.id()
        );

        return ResponseEntity
                .created(uri)
                .body(documento);
    }

    // CONSULTAR DOCUMENTO
    @GetMapping("/{documentoId}")
    public ResponseEntity<DadosDetalhamentoDocumento> buscar(
            @PathVariable UUID familiaId,
            @PathVariable UUID documentoId) {

        var documento = service.buscar(
                familiaId,
                usuario.obterUsuarioId(),
                documentoId
        );

        return ResponseEntity.ok(documento);
    }


    @GetMapping("/{documentoId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable UUID familiaId,
            @PathVariable UUID documentoId) {

        UUID usuarioId = usuario.obterUsuarioId();

        // Consulta os metadados com validação de acesso.
        var documento = service.buscar(
                familiaId,
                usuarioId,
                documentoId
        );

        // Recupera os bytes do arquivo no Storage.
        byte[] conteudo = service.baixar(
                familiaId,
                usuarioId,
                documentoId
        );

        // Prepara o nome original para download.
        var disposicao = ContentDisposition
                .attachment()
                .filename(
                        documento.nomeOriginal(),
                        StandardCharsets.UTF_8
                )
                .build();

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(documento.mimeType())
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposicao.toString()
                )
                .contentLength(conteudo.length)
                .cacheControl(CacheControl.noStore())
                .body(conteudo);
    }


    @GetMapping
    public ResponseEntity<List<DadosDetalhamentoDocumento>> listar(
            @PathVariable UUID familiaId) {

        var documentos = service.listar(
                familiaId,
                usuario.obterUsuarioId()
        );

        return ResponseEntity.ok(documentos);
    }


    // ATUALIZAR DOCUMENTO
    @PutMapping("/{documentoId}")
    public ResponseEntity<DadosDetalhamentoDocumento> atualizar(
            @PathVariable UUID familiaId,
            @PathVariable UUID documentoId,
            @Valid @RequestBody DadosAtualizacaoDocumento dados) {

        var documento = service.atualizar(
                familiaId,
                usuario.obterUsuarioId(),
                documentoId,
                dados
        );

        return ResponseEntity.ok(documento);
    }

    // EXCLUIR DOCUMENTO (EXCLUSÃO LÓGICA)
    @DeleteMapping("/{documentoId}")
    public ResponseEntity<Void> excluir(
            @PathVariable UUID familiaId,
            @PathVariable UUID documentoId) {

        service.excluir(
                familiaId,
                usuario.obterUsuarioId(),
                documentoId
        );

        return ResponseEntity.noContent().build();
    }
}
