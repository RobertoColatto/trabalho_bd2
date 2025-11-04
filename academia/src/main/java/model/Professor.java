/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Roberto Augusto
 */
public class Professor {
    
    private int idprofessor;
    private String nome;
    private char sexo;
    
    public Professor() {}
    
    public Professor(String nome, char sexo) {
        this.nome = nome;
        this.sexo = sexo;
    }

    public int getIdprofessor() {
        return idprofessor;
    }

    public String getNome() {
        return nome;
    }

    public char getSexo() {
        return sexo;
    }

    public void setIdprofessor(int idprofessor) {
        this.idprofessor = idprofessor;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }
}
