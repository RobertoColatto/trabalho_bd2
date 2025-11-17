package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Exercicio;

public class ExercicioDAO {
    
    public boolean inserir(Exercicio exercicio) {
        String sql = "INSERT INTO exercicio (nome) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, exercicio.getNome());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    exercicio.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir exercício: " + e.getMessage());
            return false;
        }
    }
    
    public List<Exercicio> listar() {
        List<Exercicio> exercicios = new ArrayList<>();
        String sql = "SELECT * FROM exercicio ORDER BY nome";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Exercicio ex = new Exercicio();
                ex.setId(rs.getInt("id"));
                ex.setNome(rs.getString("nome"));
                exercicios.add(ex);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar exercícios: " + e.getMessage());
        }
        return exercicios;
    }
    
    public boolean atualizar(Exercicio exercicio) {
        String sql = "UPDATE exercicio SET nome = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, exercicio.getNome());
            stmt.setInt(2, exercicio.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar exercício: " + e.getMessage());
            return false;
        }
    }
    
    public boolean excluir(int id) {
        String sql = "DELETE FROM exercicio WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir exercício: " + e.getMessage());
            return false;
        }
    }
}
