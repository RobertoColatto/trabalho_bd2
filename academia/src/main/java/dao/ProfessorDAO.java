/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Professor;
import constants.Constantes;

/**
 *
 * @author Roberto Augusto
 */
public class ProfessorDAO {
    
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(Constantes.URL, Constantes.USER, Constantes.PASSWORD);
    }
    
    public boolean inserir(Professor professor) {
        String sql = "{CALL inserir_professor(?, ?)}";
        try(Connection conn = conectar();
            CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, professor.getNome());
            stmt.setString(2, String.valueOf(professor.getSexo()));
            stmt.execute();
            System.out.println("Professor inserido com sucesso.");
            try(ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                    int idGerado = rs.getInt(1);
                    professor.setIdprofessor(idGerado);
                    System.out.println("ID gerado: " + idGerado);
                }
            }
            return true;
        } catch(SQLException e) {
            System.out.println("Erro ao inserir o professor: " + e.getMessage());
        }
        return false;
    }
}
