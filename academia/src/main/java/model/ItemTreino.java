package model;

import java.math.BigDecimal;

public class ItemTreino {
    
    private int id;
    private int idTreino;
    private int idExercicio;
    private int idEquipamento;
    private int series;
    private int repeticoes;
    private BigDecimal carga;
    
    public ItemTreino() {}
    
    public ItemTreino(int idTreino, int idExercicio, int idEquipamento, int series, int repeticoes, BigDecimal carga) {
        this.idTreino = idTreino;
        this.idExercicio = idExercicio;
        this.idEquipamento = idEquipamento;
        this.series = series;
        this.repeticoes = repeticoes;
        this.carga = carga;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTreino() {
        return idTreino;
    }

    public void setIdTreino(int idTreino) {
        this.idTreino = idTreino;
    }

    public int getIdExercicio() {
        return idExercicio;
    }

    public void setIdExercicio(int idExercicio) {
        this.idExercicio = idExercicio;
    }

    public int getIdEquipamento() {
        return idEquipamento;
    }

    public void setIdEquipamento(int idEquipamento) {
        this.idEquipamento = idEquipamento;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public int getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(int repeticoes) {
        this.repeticoes = repeticoes;
    }

    public BigDecimal getCarga() {
        return carga;
    }

    public void setCarga(BigDecimal carga) {
        this.carga = carga;
    }
}
