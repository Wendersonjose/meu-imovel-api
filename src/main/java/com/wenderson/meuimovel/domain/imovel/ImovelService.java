package com.wenderson.meuimovel.domain.imovel;

import org.springframework.stereotype.Service;

@Service
public class ImovelService {

    private final ImovelRepository repository;
    private final ValidadorCadastroImovel validador;

    public ImovelService(
            ImovelRepository repository,
            ValidadorCadastroImovel validador
    ) {
        this.repository = repository;
        this.validador = validador;
    }

    public Imovel cadastrar(DadosCadastroImovel dados) {

        validador.validar(dados);

        var imovel = new Imovel(dados);

        return repository.save(imovel);
    }
}