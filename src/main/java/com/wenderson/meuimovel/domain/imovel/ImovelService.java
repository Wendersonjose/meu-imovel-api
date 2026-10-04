package com.wenderson.meuimovel.domain.imovel;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<Imovel> listar() {
        return repository.findAll();
    }

    public Imovel buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ImovelNaoEncontradoException(id));
    }

    @Transactional
    public Imovel atualizar(
            Long id,
            DadosAtualizacaoImovel dados
    ) {

        var imovel = buscarPorId(id);

        validador.validarAtualizacao(imovel, dados);

        imovel.atualizar(dados);

        return imovel;
    }
}