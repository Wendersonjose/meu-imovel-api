
package com.wenderson.meuimovel.domain.financas.categoria;

import com.wenderson.meuimovel.enums.TipoCategoria;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fin_categoria", schema = "financas")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "familia_id", nullable = false)
    private UUID familiaId;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private TipoCategoria tipo;

    @Column(nullable = false)
    private boolean ativa = true;

    @Column(name = "criado_em", nullable = false,
            insertable = false, updatable = false)
    private Instant criadoEm;

    public Categoria(UUID familiaId, DadosCadastroCategoria dados) {
        this.familiaId = familiaId;
        this.nome = dados.nome().trim();
        this.tipo = dados.tipo();
        this.ativa = true;
    }

    public void atualizar(DadosAtualizacaoCategoria dados) {
        this.nome = dados.nome().trim();
    }

    public void desativar() {
        this.ativa = false;
    }
}
