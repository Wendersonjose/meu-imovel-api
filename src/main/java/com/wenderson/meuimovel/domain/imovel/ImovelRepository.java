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
}