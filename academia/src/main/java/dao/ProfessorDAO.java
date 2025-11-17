package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Professor;

public class ProfessorDAO {
    
    private Connection conectar() throws SQLException {
        return DatabaseConnection.getConnection();
    }
    
    public boolean inserir(Professor professor) {
        String sql = "INSERT INTO professor (nome, sexo, cref, telefone, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, professor.getNome());
            stmt.setString(2, String.valueOf(professor.getSexo()));
            stmt.setString(3, professor.getCref());
            stmt.setString(4, professor.getTelefone());
            stmt.setString(5, professor.getEmail());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    professor.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar o professor: " + e.getMessage());
        }
        return false;
    }
    
    public List<Professor> listar() {
        List<Professor> professores = new ArrayList<>();
        String sql = "SELECT * FROM professor ORDER BY nome";
        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Professor professor = new Professor();
                professor.setId(rs.getInt("id"));
                professor.setNome(rs.getString("nome"));
                professor.setSexo(rs.getString("sexo").charAt(0));
                professor.setCref(rs.getString("cref"));
                professor.setTelefone(rs.getString("telefone"));
                professor.setEmail(rs.getString("email"));
                professores.add(professor);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar professores: " + e.getMessage());
        }
        return professores;
    }
    
    public boolean atualizar(Professor professor) {
        String sql = "UPDATE professor SET nome = ?, sexo = ?, cref = ?, telefone = ?, email = ? WHERE id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, professor.getNome());
            stmt.setString(2, String.valueOf(professor.getSexo()));
            stmt.setString(3, professor.getCref());
            stmt.setString(4, professor.getTelefone());
            stmt.setString(5, professor.getEmail());
            stmt.setInt(6, professor.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar professor: " + e.getMessage());
            return false;
        }
    }
    
    public boolean excluir(int id) {
        String sql = "DELETE FROM professor WHERE id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir professor: " + e.getMessage());
            return false;
        }
    }
}
