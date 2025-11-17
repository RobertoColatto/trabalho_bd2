package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ItemTreino;

public class ItemTreinoDAO {
    
    public boolean inserir(ItemTreino item) {
        String sql = "INSERT INTO item_treino (id_treino, id_exercicio, id_equipamento, series, repeticoes, carga) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getIdTreino());
            stmt.setInt(2, item.getIdExercicio());
            stmt.setInt(3, item.getIdEquipamento());
            stmt.setInt(4, item.getSeries());
            stmt.setInt(5, item.getRepeticoes());
            stmt.setBigDecimal(6, item.getCarga());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir item de treino: " + e.getMessage());
            return false;
        }
    }
    
    public List<ItemTreino> listarPorTreino(int idTreino) {
        List<ItemTreino> itens = new ArrayList<>();
        String sql = "SELECT * FROM item_treino WHERE id_treino = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idTreino);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ItemTreino item = new ItemTreino();
                    item.setId(rs.getInt("id"));
                    item.setIdTreino(rs.getInt("id_treino"));
                    item.setIdExercicio(rs.getInt("id_exercicio"));
                    item.setIdEquipamento(rs.getInt("id_equipamento"));
                    item.setSeries(rs.getInt("series"));
                    item.setRepeticoes(rs.getInt("repeticoes"));
                    item.setCarga(rs.getBigDecimal("carga"));
                    itens.add(item);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar itens de treino: " + e.getMessage());
        }
        return itens;
    }
    
    public boolean excluir(int id) {
        String sql = "DELETE FROM item_treino WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir item de treino: " + e.getMessage());
            return false;
        }
    }
}
