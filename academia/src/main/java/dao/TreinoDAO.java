package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Treino;

public class TreinoDAO {
    
    private Connection conectar() throws SQLException {
        return DatabaseConnection.getConnection();
    }
    
    public boolean inserir(Treino treino) {
        String sql = "INSERT INTO treino (id_aluno, id_professor, nome, foco_treino) VALUES (?, ?, ?, ?)";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, treino.getIdAluno());
            stmt.setInt(2, treino.getIdProfessor());
            stmt.setString(3, treino.getNome());
            stmt.setString(4, treino.getFocoTreino());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    treino.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar o treino: " + e.getMessage());
        }
        return false;
    }
    
    public List<Treino> listar() {
        List<Treino> treinos = new ArrayList<>();
        String sql = "SELECT * FROM treino ORDER BY nome";
        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Treino treino = new Treino();
                treino.setId(rs.getInt("id"));
                treino.setIdAluno(rs.getInt("id_aluno"));
                treino.setIdProfessor(rs.getInt("id_professor"));
                treino.setNome(rs.getString("nome"));
                treino.setFocoTreino(rs.getString("foco_treino"));
                treinos.add(treino);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar treinos: " + e.getMessage());
        }
        return treinos;
    }
    
    public List<Treino> listarPorAluno(int idAluno) {
        List<Treino> treinos = new ArrayList<>();
        String sql = "SELECT * FROM treino WHERE id_aluno = ? ORDER BY nome";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAluno);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Treino treino = new Treino();
                    treino.setId(rs.getInt("id"));
                    treino.setIdAluno(rs.getInt("id_aluno"));
                    treino.setIdProfessor(rs.getInt("id_professor"));
                    treino.setNome(rs.getString("nome"));
                    treino.setFocoTreino(rs.getString("foco_treino"));
                    treinos.add(treino);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar treinos do aluno: " + e.getMessage());
        }
        return treinos;
    }
    
    public boolean atualizar(Treino treino) {
        String sql = "UPDATE treino SET id_aluno = ?, id_professor = ?, nome = ?, foco_treino = ? WHERE id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, treino.getIdAluno());
            stmt.setInt(2, treino.getIdProfessor());
            stmt.setString(3, treino.getNome());
            stmt.setString(4, treino.getFocoTreino());
            stmt.setInt(5, treino.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar treino: " + e.getMessage());
            return false;
        }
    }
    
    public boolean excluir(int id) {
        String sql = "DELETE FROM treino WHERE id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir treino: " + e.getMessage());
            return false;
        }
    }
}
