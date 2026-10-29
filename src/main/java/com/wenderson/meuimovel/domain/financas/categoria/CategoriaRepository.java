
package com.wenderson.meuimovel.domain.financas.categoria;

import com.wenderson.meuimovel.enums.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    List<Categoria> findByFamiliaIdAndAtivaTrueOrderByNomeAsc(
            UUID familiaId
    );

    Optional<Categoria> findByIdAndFamiliaId(
            UUID id,
            UUID familiaId
    );

    boolean existsByFamiliaIdAndTipoAndNomeIgnoreCase(
            UUID familiaId,
            TipoCategoria tipo,
            String nome
    );
}
