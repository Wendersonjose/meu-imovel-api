package com.wenderson.meuimovel.domain.imovel;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.wenderson.meuimovel.infra.security.UsuarioAutenticado;

@Service
public class ImovelService {

    private final UsuarioAutenticado usuarioAutenticado;
    private final ImovelRepository repository;
    private final ValidadorCadastroImovel validador;

    public ImovelService(
            ImovelRepository repository,
            ValidadorCadastroImovel validador,
            UsuarioAutenticado usuarioAutenticado
    ) {
        this.repository = repository;
        this.validador = validador;
        this.usuarioAutenticado = usuarioAutenticado;
    }

    public Imovel cadastrar(DadosCadastroImovel dados) {

        validador.validar(dados);

        var imovel = new Imovel(dados);

        return repository.save(imovel);
    }

    public List<Imovel> listar() {

        var usuarioId = usuarioAutenticado.obterUsuarioId();

        return repository.findAllAutorizados(usuarioId);
    }

    public Imovel buscarPorId(Long id) {

        var usuarioId = usuarioAutenticado.obterUsuarioId();

        return repository.findByIdAutorizado(id, usuarioId)
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