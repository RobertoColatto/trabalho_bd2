/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import model.Aluno;
/**
 *
 * @author Roberto Augusto
 */
public class AlunoDAO {
    
    private final String URL = "jdbc:mysql://localhost:3306/academia";
    private final String USER = "root";
    private final String PASSWORD = "sonic06kkk";
    
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public boolean inserir(Aluno aluno) {
        String sql = "{CALL inserir_aluno(?, ?, ?, ?, ?)}";
        try(Connection conn = conectar();
            CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getEmail());
            stmt.setDouble(3, aluno.getPeso());
            stmt.setDouble(4, aluno.getAltura());
            stmt.setString(5, String.valueOf(aluno.getSexo()));
            stmt.execute();
            System.out.println("Aluno cadastrado com sucesso.");
            try(ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                    int idGerado = rs.getInt(1);
                    aluno.setAlunoid(idGerado);
                    System.out.println("ID gerado: " + idGerado);
                }
            }
            return true;
        } catch(SQLException e) {
            System.out.println("Erro ao cadastrar aluno: " + e.getMessage());
        }
        return false;
    }
}
