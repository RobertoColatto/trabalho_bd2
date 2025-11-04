/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalTime;
/**
 *
 * @author Roberto Augusto
 */
public class Treino {
    
    private String nome;
    private String musculo;
    private int treinoid;
    private LocalTime horario;
    
    public Treino() {}
    
    public Treino(String nome, String musculo, LocalTime horario) {
        this.nome = nome;
        this.musculo = musculo;
        this.horario = horario;
    }

    public String getNome() {
        return nome;
    }

    public String getMusculo() {
        return musculo;
    }

    public int getTreinoid() {
        return treinoid;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setMusculo(String musculo) {
        this.musculo = musculo;
    }

    public void setTreinoid(int treinoid) {
        this.treinoid = treinoid;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }
}
