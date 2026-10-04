package com.wenderson.meuimovel.domain.imovel;

import org.springframework.stereotype.Component;

@Component
public class ValidadorCadastroImovel {

    private final ImovelRepository repository;

    public ValidadorCadastroImovel(ImovelRepository repository) {
        this.repository = repository;
    }

    public void validar(DadosCadastroImovel dados) {
        validarCnm(dados);
        validarMatricula(dados);
        validarCadastroMunicipal(dados);
    }

    public void validarAtualizacao(
            Imovel imovel,
            DadosAtualizacaoImovel dados
    ) {
        validarCnmAtualizacao(imovel, dados);
        validarMatriculaAtualizacao(imovel, dados);
        validarCadastroMunicipalAtualizacao(imovel, dados);
    }

    private void validarCnm(DadosCadastroImovel dados) {

        if (preenchido(dados.cnm())
                && repository.existsByCnm(dados.cnm())) {

            throw new ImovelDuplicadoException(
                    "Já existe um imóvel cadastrado com este CNM."
            );
        }
    }

    private void validarMatricula(DadosCadastroImovel dados) {

        if (preenchido(dados.cartorio())
                && preenchido(dados.matricula())
                && repository.existsByCartorioAndMatricula(
                dados.cartorio(),
                dados.matricula())) {

            throw new ImovelDuplicadoException(
                    "Já existe um imóvel cadastrado com esta matrícula neste cartório."
            );
        }
    }

    private void validarCadastroMunicipal(DadosCadastroImovel dados) {

        if (preenchido(dados.cidade())
                && preenchido(dados.uf())
                && preenchido(dados.cadastroMunicipal())
                && repository.existsByCidadeAndUfAndCadastroMunicipal(
                dados.cidade(),
                dados.uf(),
                dados.cadastroMunicipal())) {

            throw new ImovelDuplicadoException(
                    "Já existe um imóvel cadastrado com este cadastro municipal."
            );
        }
    }

    private void validarCnmAtualizacao(
            Imovel imovel,
            DadosAtualizacaoImovel dados
    ) {

        var cnm = dados.cnm() != null
                ? dados.cnm()
                : imovel.getCnm();

        if (preenchido(cnm)
                && repository.existsByCnmAndIdNot(
                cnm,
                imovel.getId())) {

            throw new ImovelDuplicadoException(
                    "Já existe outro imóvel cadastrado com este CNM."
            );
        }
    }

    private void validarMatriculaAtualizacao(
            Imovel imovel,
            DadosAtualizacaoImovel dados
    ) {

        var cartorio = dados.cartorio() != null
                ? dados.cartorio()
                : imovel.getCartorio();

        var matricula = dados.matricula() != null
                ? dados.matricula()
                : imovel.getMatricula();

        if (preenchido(cartorio)
                && preenchido(matricula)
                && repository.existsByCartorioAndMatriculaAndIdNot(
                cartorio,
                matricula,
                imovel.getId())) {

            throw new ImovelDuplicadoException(
                    "Já existe outro imóvel cadastrado com esta matrícula neste cartório."
            );
        }
    }

    private void validarCadastroMunicipalAtualizacao(
            Imovel imovel,
            DadosAtualizacaoImovel dados
    ) {

        var cidade = dados.cidade() != null
                ? dados.cidade()
                : imovel.getCidade();

        var uf = dados.uf() != null
                ? dados.uf()
                : imovel.getUf();

        var cadastroMunicipal = dados.cadastroMunicipal() != null
                ? dados.cadastroMunicipal()
                : imovel.getCadastroMunicipal();

        if (preenchido(cidade)
                && preenchido(uf)
                && preenchido(cadastroMunicipal)
                && repository.existsByCidadeAndUfAndCadastroMunicipalAndIdNot(
                cidade,
                uf,
                cadastroMunicipal,
                imovel.getId())) {

            throw new ImovelDuplicadoException(
                    "Já existe outro imóvel cadastrado com este cadastro municipal."
            );
        }
    }

    private boolean preenchido(String valor) {
        return valor != null && !valor.isBlank();
    }
}