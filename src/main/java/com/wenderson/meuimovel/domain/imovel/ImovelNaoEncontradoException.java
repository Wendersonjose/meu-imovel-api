package com.wenderson.meuimovel.domain.imovel;

public class ImovelNaoEncontradoException extends RuntimeException {

    public ImovelNaoEncontradoException(Long id) {
        super("Imóvel não encontrado com o id: " + id);
    }
}