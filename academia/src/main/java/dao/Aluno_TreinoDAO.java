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
import model.Aluno;
import model.Treino;

/**
 *
 * @author Roberto Augusto
 */
public class Aluno_TreinoDAO {
    
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(Constantes.URL, Constantes.USER, Constantes.PASSWORD);
    }
    
    public boolean relacionarAlunoTreino(int alunoid, int treinoid) {
        String sql = "{CALL relacionar_aluno_treino(?, ?)}";
        try(Connection conn = conectar();
            CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, alunoid);
            stmt.setInt(2, treinoid);
            stmt.execute();
            System.out.println("Aluno e treino relacionados com sucesso.");
            try(ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                    int idGerado = rs.getInt(1);
                    System.out.println("ID gerado: " + idGerado);
                }
            }
            return true;
        } catch(SQLException e) {
            System.out.println("Erro ao relacionar o aluno e o treino: " + e.getMessage());
        }
        return false;
    }
}
