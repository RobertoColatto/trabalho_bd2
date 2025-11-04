/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import constants.Constantes;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Equipamento;

/**
 *
 * @author Roberto Augusto
 */
public class EquipamentoDAO {
    
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(Constantes.URL, Constantes.USER, Constantes.PASSWORD);
    }
    
    public boolean inserir(Equipamento equipamento) {
        String sql = "{CALL inserir_equipamento(?)}";
        try(Connection conn = conectar();
            CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, equipamento.getNome());
            stmt.execute();
            System.out.println("Equipamento inserido com sucesso.");
            try(ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                    int idGerado = rs.getInt(1);
                    equipamento.setIdequipamentos(idGerado);
                    System.out.println("ID gerado: " + idGerado);
                }
            }
            return true;
        } catch(SQLException e) {
            System.out.println("Erro ao inserir o equipamento: " + e.getMessage());
        }
        return false;
    }
}
