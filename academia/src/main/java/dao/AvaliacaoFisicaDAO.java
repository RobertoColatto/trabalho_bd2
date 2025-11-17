package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.AvaliacaoFisica;

public class AvaliacaoFisicaDAO {
    
    public boolean inserir(AvaliacaoFisica avaliacao) {
        String sql = "INSERT INTO avaliacao_fisica (id_aluno, id_professor, data_avaliacao, altura, peso, percentual_gordura, imc) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, avaliacao.getIdAluno());
            stmt.setInt(2, avaliacao.getIdProfessor());
            stmt.setDate(3, avaliacao.getDataAvaliacao());
            stmt.setBigDecimal(4, avaliacao.getAltura());
            stmt.setBigDecimal(5, avaliacao.getPeso());
            stmt.setBigDecimal(6, avaliacao.getPercentualGordura());
            stmt.setBigDecimal(7, avaliacao.getImc());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    avaliacao.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir avaliação física: " + e.getMessage());
            return false;
        }
    }
    
    public List<AvaliacaoFisica> listarPorAluno(int idAluno) {
        List<AvaliacaoFisica> avaliacoes = new ArrayList<>();
        String sql = "SELECT * FROM avaliacao_fisica WHERE id_aluno = ? ORDER BY data_avaliacao DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAluno);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AvaliacaoFisica av = new AvaliacaoFisica();
                    av.setId(rs.getInt("id"));
                    av.setIdAluno(rs.getInt("id_aluno"));
                    av.setIdProfessor(rs.getInt("id_professor"));
                    av.setDataAvaliacao(rs.getDate("data_avaliacao"));
                    av.setAltura(rs.getBigDecimal("altura"));
                    av.setPeso(rs.getBigDecimal("peso"));
                    av.setPercentualGordura(rs.getBigDecimal("percentual_gordura"));
                    av.setImc(rs.getBigDecimal("imc"));
                    avaliacoes.add(av);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar avaliações físicas: " + e.getMessage());
        }
        return avaliacoes;
    }
    
    public boolean excluir(int id) {
        String sql = "DELETE FROM avaliacao_fisica WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir avaliação física: " + e.getMessage());
            return false;
        }
    }
}
