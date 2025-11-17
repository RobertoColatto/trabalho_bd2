package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Equipamento;

public class EquipamentoDAO {
    
    public boolean inserir(Equipamento equipamento) {
        String sql = "INSERT INTO equipamento (nome, tipo) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, equipamento.getNome());
            stmt.setString(2, equipamento.getTipo());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    equipamento.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir equipamento: " + e.getMessage());
            return false;
        }
    }
    
    public List<Equipamento> listar() {
        List<Equipamento> equipamentos = new ArrayList<>();
        String sql = "SELECT * FROM equipamento ORDER BY nome";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Equipamento eq = new Equipamento();
                eq.setId(rs.getInt("id"));
                eq.setNome(rs.getString("nome"));
                eq.setTipo(rs.getString("tipo"));
                equipamentos.add(eq);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar equipamentos: " + e.getMessage());
        }
        return equipamentos;
    }
    
    public boolean atualizar(Equipamento equipamento) {
        String sql = "UPDATE equipamento SET nome = ?, tipo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, equipamento.getNome());
            stmt.setString(2, equipamento.getTipo());
            stmt.setInt(3, equipamento.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar equipamento: " + e.getMessage());
            return false;
        }
    }
    
    public boolean excluir(int id) {
        String sql = "DELETE FROM equipamento WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir equipamento: " + e.getMessage());
            return false;
        }
    }
}
