package com.wenderson.meuimovel.domain.imovel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "imovel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Imovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Column(name = "tipo_imovel")
    private String tipoImovel;

    private String matricula;
    private String cnm;
    private String cartorio;

    @Column(name = "cadastro_municipal")
    private String cadastroMunicipal;

    private String logradouro;
    private String numero;
    private String cidade;
    private String uf;

    private String lote;
    private String quadra;
    private String loteamento;

    @Column(name = "area_terreno", precision = 10, scale = 2)
    private BigDecimal areaTerreno;

    @Column(name = "area_construida", precision = 10, scale = 2)
    private BigDecimal areaConstruida;

    @Column(name = "numero_habite_se")
    private String numeroHabiteSe;

    @Column(name = "data_habite_se")
    private LocalDate dataHabiteSe;

    @Column(name = "valor_imovel", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorImovel;

    @Column(name = "valor_financiado", precision = 12, scale = 2)
    private BigDecimal valorFinanciado;

    @Column(name = "valor_entrada", precision = 12, scale = 2)
    private BigDecimal valorEntrada;

    @Column(name = "data_compra")
    private LocalDate dataCompra;

    private String observacao;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Imovel(DadosCadastroImovel dados) {


        this.descricao = dados.descricao();
        this.tipoImovel = dados.tipoImovel();

        this.matricula = dados.matricula();
        this.cnm = dados.cnm();
        this.cartorio = dados.cartorio();
        this.cadastroMunicipal = dados.cadastroMunicipal();

        this.logradouro = dados.logradouro();
        this.numero = dados.numero();
        this.cidade = dados.cidade();
        this.uf = dados.uf();

        this.lote = dados.lote();
        this.quadra = dados.quadra();
        this.loteamento = dados.loteamento();

        this.areaTerreno = dados.areaTerreno();
        this.areaConstruida = dados.areaConstruida();

        this.numeroHabiteSe = dados.numeroHabiteSe();
        this.dataHabiteSe = dados.dataHabiteSe();

        this.valorImovel = dados.valorImovel();
        this.valorFinanciado = dados.valorFinanciado();
        this.valorEntrada = dados.valorEntrada();

        this.dataCompra = dados.dataCompra();
        this.observacao = dados.observacao();
    }

    public void atualizar(DadosAtualizacaoImovel dados) {
        atualizarDadosCadastrais(dados);
        atualizarEndereco(dados);
        atualizarCaracteristicas(dados);
        atualizarFinanceiro(dados);
    }

    private void atualizarDadosCadastrais(DadosAtualizacaoImovel dados) {

        if (dados.descricao() != null) {
            this.descricao = dados.descricao();
        }

        if (dados.tipoImovel() != null) {
            this.tipoImovel = dados.tipoImovel();
        }

        if (dados.matricula() != null) {
            this.matricula = dados.matricula();
        }

        if (dados.cnm() != null) {
            this.cnm = dados.cnm();
        }

        if (dados.cartorio() != null) {
            this.cartorio = dados.cartorio();
        }

        if (dados.cadastroMunicipal() != null) {
            this.cadastroMunicipal = dados.cadastroMunicipal();
        }
    }

    private void atualizarEndereco(DadosAtualizacaoImovel dados) {

        if (dados.logradouro() != null) {
            this.logradouro = dados.logradouro();
        }

        if (dados.numero() != null) {
            this.numero = dados.numero();
        }

        if (dados.cidade() != null) {
            this.cidade = dados.cidade();
        }

        if (dados.uf() != null) {
            this.uf = dados.uf();
        }

        if (dados.lote() != null) {
            this.lote = dados.lote();
        }

        if (dados.quadra() != null) {
            this.quadra = dados.quadra();
        }

        if (dados.loteamento() != null) {
            this.loteamento = dados.loteamento();
        }
    }

    private void atualizarCaracteristicas(DadosAtualizacaoImovel dados) {

        if (dados.areaTerreno() != null) {
            this.areaTerreno = dados.areaTerreno();
        }

        if (dados.areaConstruida() != null) {
            this.areaConstruida = dados.areaConstruida();
        }

        if (dados.numeroHabiteSe() != null) {
            this.numeroHabiteSe = dados.numeroHabiteSe();
        }

        if (dados.dataHabiteSe() != null) {
            this.dataHabiteSe = dados.dataHabiteSe();
        }
    }

    private void atualizarFinanceiro(DadosAtualizacaoImovel dados) {

        if (dados.valorImovel() != null) {
            this.valorImovel = dados.valorImovel();
        }

        if (dados.valorFinanciado() != null) {
            this.valorFinanciado = dados.valorFinanciado();
        }

        if (dados.valorEntrada() != null) {
            this.valorEntrada = dados.valorEntrada();
        }

        if (dados.dataCompra() != null) {
            this.dataCompra = dados.dataCompra();
        }

        if (dados.observacao() != null) {
            this.observacao = dados.observacao();
        }
    }
}