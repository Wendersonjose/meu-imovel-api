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

    private boolean preenchido(String valor) {
        return valor != null && !valor.isBlank();
    }
}