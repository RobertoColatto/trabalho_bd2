package model;

import java.math.BigDecimal;
import java.sql.Date;

public class AvaliacaoFisica {
    
    private int id;
    private int idAluno;
    private int idProfessor;
    private Date dataAvaliacao;
    private BigDecimal altura;
    private BigDecimal peso;
    private BigDecimal percentualGordura;
    private BigDecimal imc;
    
    public AvaliacaoFisica() {}
    
    public AvaliacaoFisica(int idAluno, int idProfessor, Date dataAvaliacao, BigDecimal altura, BigDecimal peso, BigDecimal percentualGordura, BigDecimal imc) {
        this.idAluno = idAluno;
        this.idProfessor = idProfessor;
        this.dataAvaliacao = dataAvaliacao;
        this.altura = altura;
        this.peso = peso;
        this.percentualGordura = percentualGordura;
        this.imc = imc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAluno() {
        return idAluno;
    }

    public void setIdAluno(int idAluno) {
        this.idAluno = idAluno;
    }

    public int getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(int idProfessor) {
        this.idProfessor = idProfessor;
    }

    public Date getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(Date dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public BigDecimal getAltura() {
        return altura;
    }

    public void setAltura(BigDecimal altura) {
        this.altura = altura;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public BigDecimal getPercentualGordura() {
        return percentualGordura;
    }

    public void setPercentualGordura(BigDecimal percentualGordura) {
        this.percentualGordura = percentualGordura;
    }

    public BigDecimal getImc() {
        return imc;
    }

    public void setImc(BigDecimal imc) {
        this.imc = imc;
    }
}
