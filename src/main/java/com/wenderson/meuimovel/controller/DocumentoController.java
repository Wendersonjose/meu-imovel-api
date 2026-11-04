
package com.wenderson.meuimovel.controller;

import com.wenderson.meuimovel.domain.documento.DadosDetalhamentoDocumento;
import com.wenderson.meuimovel.domain.documento.DocumentoService;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
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
}
