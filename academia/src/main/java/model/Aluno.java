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
    
    private int alunoid;
    private String nome;
    private String email;
    private double peso;
    private double altura;
    private char sexo;
    private Timestamp create_time;
    
    public Aluno() {};
    
    public Aluno(String nome, String email, double peso, double altura, char sexo) {
        this.nome = nome;
        this.email = email;
        this.peso = peso;
        this.altura = altura;
        this.sexo = sexo;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public int getAlunoid() {
        return alunoid;
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

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setAlunoid(int aluno_id) {
        this.alunoid = aluno_id;
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

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }
}
