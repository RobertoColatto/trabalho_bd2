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
import model.Treino;

/**
 *
 * @author Roberto Augusto
 */
public class TreinoDAO {
    
    private final String URL = "jdbc:mysql://localhost:3306/academia";
    private final String USER = "root";
    private final String PASSWORD = "sonic06kkk";
    
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public boolean inserir(Treino treino) {
        String sql = "{CALL inserir_treino(?, ?, ?)}";
        try(Connection conn = conectar();
            CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, treino.getNome());
            stmt.setString(2, treino.getMusculo());
            stmt.setTime(3, java.sql.Time.valueOf(treino.getHorario()));
            stmt.execute();
            System.out.println("Treino cadastrado com sucesso.");
            try(ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                    int idGerado = rs.getInt(1);
                    treino.setTreinoid(idGerado);
                    System.out.println("ID gerado: " + idGerado);
                }
            }
            return true;
        } catch(SQLException e) {
            System.out.println("Erro ao cadastrar o treino: " + e.getMessage());
        }
        return false;
    }
}
