/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 *
 * @author Roberto Augusto
 */
public class Aluno {
    
    private String email;
    //private Timestamp create_time;
    private String nome;
    private int aluno_id;
    private double peso;
    private double altura;
    private char sexo;
    
    public Aluno() {};
    
    public Aluno(String email, String nome, double peso, double altura, char sexo) {
        this.email = email;
        //this.create_time = create_time;
        this.nome = nome;
        //this.aluno_id = aluno_id;
        this.peso = peso;
        this.altura = altura;
        this.sexo = sexo;
    }

    public String getEmail() {
        return email;
    }
    /*
    public Timestamp getCreate_time() {
        return create_time;
    }
    */
    public String getNome() {
        return nome;
    }

    public int getAluno_id() {
        return aluno_id;
    }

    public double getPeso() {
        return peso;
    }

    public double getAltura() {
        return altura;
    }

    public char getSexo() {
        return sexo;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    /*
    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }
    */
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAluno_id(int aluno_id) {
        this.aluno_id = aluno_id;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public void setSexo(char sexo) {
        this.sexo = sexo;
    }
    
    
}
