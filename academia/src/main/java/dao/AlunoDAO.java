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
    
    public void inserir(Aluno aluno) {
        String sql = "INSERT INTO user (email, name, peso, tamanho, sexo) VALUES (?, ?, ?, ?, ?)";
        try(Connection conn = conectar();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, aluno.getEmail());
            //stmt.setTimestamp(2, aluno.getCreate_time());
            stmt.setString(2, aluno.getNome());
            //stmt.setInt(4, aluno.getAluno_id());
            stmt.setDouble(3, aluno.getPeso());
            stmt.setDouble(4, aluno.getAltura());
            stmt.setString(5, String.valueOf(aluno.getSexo()));
            stmt.executeUpdate();
            System.out.println("Aluno cadastrado com sucesso.");
            try(ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                    int idGerado = rs.getInt(1);
                    aluno.setAluno_id(idGerado);
                    System.out.println("ID gerado: " + idGerado);
                }
            }
        } catch(SQLException e) {
            System.out.println("Erro ao inserir aluno: " + e.getMessage());
        }
    }
}
