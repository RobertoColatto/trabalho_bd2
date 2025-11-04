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

/**
 *
 * @author Roberto Augusto
 */
public class ProfessorDAO {
    
    private final String URL = "jdbc:mysql://localhost:3306/academia";
    private final String USER = "root";
    private final String PASSWORD = "sonic06kkk";
    
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public boolean inserir(Professor professor) {
        String sql = "{CALL inserir_professor(?, ?)}";
        try(Connection conn = conectar();
            CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, professor.getNome());
            stmt.setString(2, String.valueOf(professor.getSexo()));
            stmt.execute();
            System.out.println("Professor cadastrado com sucesso.");
            try(ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                    int idGerado = rs.getInt(1);
                    professor.setIdprofessor(idGerado);
                    System.out.println("ID gerado: " + idGerado);
                }
            }
            return true;
        } catch(SQLException e) {
            System.out.println("Erro ao cadastrar o professor: " + e.getMessage());
        }
        return false;
    }
}
