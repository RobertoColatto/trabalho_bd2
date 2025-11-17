package dao; //Data Access Object

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Aluno;

public class AlunoDAO {
    
    private Connection conectar() throws SQLException {
        return DatabaseConnection.getConnection();
    }
    
    public boolean inserir(Aluno aluno) throws SQLException {
        String sql = "INSERT INTO aluno (nome, data_nascimento, sexo, telefone, email, data_cadastro) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, aluno.getNome());
            stmt.setDate(2, aluno.getDataNascimento());
            stmt.setString(3, String.valueOf(aluno.getSexo()));
            stmt.setString(4, aluno.getTelefone());
            stmt.setString(5, aluno.getEmail());
            stmt.setDate(6, aluno.getDataCadastro());
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    aluno.setId(rs.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar o aluno: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Erro ao cadastrar aluno: " + e.getMessage(), e);
        }
    }
    
    public List<Aluno> listar() {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT * FROM aluno ORDER BY nome";
        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Aluno aluno = new Aluno();
                aluno.setId(rs.getInt("id"));
                aluno.setNome(rs.getString("nome"));
                aluno.setDataNascimento(rs.getDate("data_nascimento"));
                aluno.setSexo(rs.getString("sexo").charAt(0));
                aluno.setTelefone(rs.getString("telefone"));
                aluno.setEmail(rs.getString("email"));
                aluno.setDataCadastro(rs.getDate("data_cadastro"));
                alunos.add(aluno);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar alunos: " + e.getMessage());
        }
        return alunos;
    }
    
    public boolean atualizar(Aluno aluno) throws SQLException {
        String sql = "UPDATE aluno SET nome = ?, data_nascimento = ?, sexo = ?, telefone = ?, email = ? WHERE id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, aluno.getNome());
            stmt.setDate(2, aluno.getDataNascimento());
            stmt.setString(3, String.valueOf(aluno.getSexo()));
            stmt.setString(4, aluno.getTelefone());
            stmt.setString(5, aluno.getEmail());
            stmt.setInt(6, aluno.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar aluno: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Erro ao atualizar aluno: " + e.getMessage(), e);
        }
    }
    
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM aluno WHERE id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir aluno: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Erro ao excluir aluno: " + e.getMessage(), e);
        }
    }
}
