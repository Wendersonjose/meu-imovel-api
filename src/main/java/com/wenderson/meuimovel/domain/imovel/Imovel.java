package com.wenderson.meuimovel.domain.imovel;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "imovel")
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

    protected Imovel() {
    }

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

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getTipoImovel() {
        return tipoImovel;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getCnm() {
        return cnm;
    }

    public String getCartorio() {
        return cartorio;
    }

    public String getCadastroMunicipal() {
        return cadastroMunicipal;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getCidade() {
        return cidade;
    }

    public String getUf() {
        return uf;
    }

    public String getLote() {
        return lote;
    }

    public String getQuadra() {
        return quadra;
    }

    public String getLoteamento() {
        return loteamento;
    }

    public BigDecimal getAreaTerreno() {
        return areaTerreno;
    }

    public BigDecimal getAreaConstruida() {
        return areaConstruida;
    }

    public String getNumeroHabiteSe() {
        return numeroHabiteSe;
    }

    public LocalDate getDataHabiteSe() {
        return dataHabiteSe;
    }

    public BigDecimal getValorImovel() {
        return valorImovel;
    }

    public BigDecimal getValorFinanciado() {
        return valorFinanciado;
    }

    public BigDecimal getValorEntrada() {
        return valorEntrada;
    }

    public LocalDate getDataCompra() {
        return dataCompra;
    }

    public String getObservacao() {
        return observacao;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}