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
import constants.Constantes;

/**
 *
 * @author Roberto Augusto
 */
public class TreinoDAO {
    
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(Constantes.URL, Constantes.USER, Constantes.PASSWORD);
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
