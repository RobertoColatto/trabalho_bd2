/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Roberto Augusto
 */
public class Treino {
    
    private int id;
    private int idAluno;
    private int idProfessor;
    private String nome;
    private String focoTreino;
    
    public Treino() {}
    
    public Treino(int idAluno, int idProfessor, String nome, String focoTreino) {
        this.idAluno = idAluno;
        this.idProfessor = idProfessor;
        this.nome = nome;
        this.focoTreino = focoTreino;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFocoTreino() {
        return focoTreino;
    }

    public void setFocoTreino(String focoTreino) {
        this.focoTreino = focoTreino;
    }

    @Override
    public String toString() {
        return id + " - " + nome + " (" + focoTreino + ")";
    }
}
