package com.wenderson.meuimovel.infra.exception;

import com.wenderson.meuimovel.domain.imovel.ImovelDuplicadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(ImovelDuplicadoException.class)
    public ResponseEntity<DadosErro> tratarErroImovelDuplicado(
            ImovelDuplicadoException exception
    ) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new DadosErro(exception.getMessage()));
    }

    private record DadosErro(
            String mensagem
    ) {
    }
}