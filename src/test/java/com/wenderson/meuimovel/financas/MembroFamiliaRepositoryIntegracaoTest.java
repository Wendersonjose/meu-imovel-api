
package com.wenderson.meuimovel.domain.financas;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        // Utiliza exclusivamente o PostgreSQL local.
        "spring.datasource.url=jdbc:postgresql://localhost:5432/meu_imovel_dev",

        // Impede migrations e alteracoes automaticas no schema.
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none"
})
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Import(MembroFamiliaRepository.class)
class MembroFamiliaRepositoryIntegracaoTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MembroFamiliaRepository repository;

    @Test
    void deveEncontrarVinculoAtivoNoPostgreSQL() {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        // Cria uma familia ficticia.
        entityManager.createNativeQuery("""
                INSERT INTO financas.fin_familia (id, nome)
                VALUES (:familiaId, 'Familia Teste JPA')
                """)
                .setParameter("familiaId", familiaId)
                .executeUpdate();

        // Cria um membro ativo nessa familia.
        entityManager.createNativeQuery("""
                INSERT INTO financas.fin_membro (
                    familia_id,
                    usuario_id,
                    nome_exibicao,
                    papel,
                    ativo
                )
                VALUES (
                    :familiaId,
                    :usuarioId,
                    'Usuario Teste JPA',
                    'ADMIN',
                    TRUE
                )
                """)
                .setParameter("familiaId", familiaId)
                .setParameter("usuarioId", usuarioId)
                .executeUpdate();

        // Executa a consulta real do Repository.
        boolean possuiVinculo = repository.existeVinculoAtivo(
                familiaId,
                usuarioId
        );

        // Confirma que o PostgreSQL retornou true.
        assertTrue(possuiVinculo);
    }


    @Test
    void deveRetornarFalseQuandoMembroEstaInativo() {

        UUID familiaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();

        // Cria uma familia ficticia.
        entityManager.createNativeQuery("""
            INSERT INTO financas.fin_familia (id, nome)
            VALUES (:familiaId, 'Familia Teste Inativo')
            """)
                .setParameter("familiaId", familiaId)
                .executeUpdate();

        // Cadastra um membro com vinculo inativo.
        entityManager.createNativeQuery("""
            INSERT INTO financas.fin_membro (
                familia_id,
                usuario_id,
                nome_exibicao,
                papel,
                ativo
            )
            VALUES (
                :familiaId,
                :usuarioId,
                'Usuario Inativo',
                'MEMBRO',
                FALSE
            )
            """)
                .setParameter("familiaId", familiaId)
                .setParameter("usuarioId", usuarioId)
                .executeUpdate();

        // Consulta o vinculo pelo Repository real.
        boolean possuiVinculoAtivo =
                repository.existeVinculoAtivo(
                        familiaId,
                        usuarioId
                );

        // Um membro inativo nao possui acesso autorizado.
        assertFalse(possuiVinculoAtivo);
    }

}
