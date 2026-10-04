package com.wenderson.meuimovel.domain.imovel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImovelRepository extends JpaRepository<Imovel, Long> {

    boolean existsByCnm(String cnm);

    boolean existsByCartorioAndMatricula(
            String cartorio,
            String matricula
    );

    boolean existsByCidadeAndUfAndCadastroMunicipal(
            String cidade,
            String uf,
            String cadastroMunicipal
    );

    boolean existsByCnmAndIdNot(
            String cnm,
            Long id
    );

    boolean existsByCartorioAndMatriculaAndIdNot(
            String cartorio,
            String matricula,
            Long id
    );

    boolean existsByCidadeAndUfAndCadastroMunicipalAndIdNot(
            String cidade,
            String uf,
            String cadastroMunicipal,
            Long id
    );
}