/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Roberto Augusto
 */
public class Equipamento {
    
    private String nome;
    private int idequipamentos;
    
    public Equipamento(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public int getIdequipamentos() {
        return idequipamentos;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setIdequipamentos(int idequipamentos) {
        this.idequipamentos = idequipamentos;
    }
}
