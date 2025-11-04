/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao; //Data Access Object

import java.sql.*;
import java.util.ArrayList;
import model.Aluno;
import constants.Constantes;
/**
 *
 * @author Roberto Augusto
 */
public class AlunoDAO {
    
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(Constantes.URL, Constantes.USER, Constantes.PASSWORD);
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
            System.out.println("Aluno inserido com sucesso.");
            try(ResultSet rs = stmt.getGeneratedKeys()) {
                if(rs.next()) {
                    int idGerado = rs.getInt(1);
                    aluno.setAlunoid(idGerado);
                    System.out.println("ID gerado: " + idGerado);
                }
            }
            return true;
        } catch(SQLException e) {
            System.out.println("Erro ao inserir o aluno: " + e.getMessage());
        }
        return false;
    }
    
    public String exibirIdNome() {
        String idNomeAlunos = "";
        String sql = "{CALL exibir_id_nome_alunos()}";
        try(Connection conn = conectar();
            CallableStatement stmt = conn.prepareCall(sql)) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                idNomeAlunos += rs.getInt("alunoid") + " - " + rs.getString("nome") + "\n";
            }
        } catch(SQLException e) {
            System.err.println("Erro ao buscar alunos: " + e.getMessage());
        }
        return idNomeAlunos;
    }
    
    public ArrayList<Aluno> buscarPorNome(String nome) throws SQLException {
        ArrayList<Aluno> alunos = new ArrayList<>();

        String sql = "{CALL buscar_alunos_por_nome(?)}";
        try(Connection conn = conectar();
            CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, nome);

            try(ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    Aluno aluno = new Aluno();
                    aluno.setAlunoid(rs.getInt("alunoid"));
                    aluno.setNome(rs.getString("nome"));
                    aluno.setEmail(rs.getString("email"));
                    aluno.setPeso(rs.getDouble("peso"));
                    aluno.setAltura(rs.getDouble("altura"));
                    aluno.setSexo(rs.getString("sexo").charAt(0));
                    aluno.setCreate_time(rs.getTimestamp("create_time"));
                    alunos.add(aluno);
                }
            }
        }
        return alunos;
    }
}
