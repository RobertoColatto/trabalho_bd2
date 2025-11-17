package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsultasAvancadasDAO {
    
    private Connection conectar() throws SQLException {
        return DatabaseConnection.getConnection();
    }
    
    // Buscar alunos com filtros avançados
    public List<Map<String, Object>> buscarAlunosFiltros(String nome, String sexo, 
            Date dataInicio, Date dataFim) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String sql = "CALL sp_buscar_alunos(?, ?, ?, ?)";
        
        try (Connection conn = conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setString(1, nome != null && !nome.isEmpty() ? nome : null);
            stmt.setString(2, sexo != null && !sexo.isEmpty() ? sexo : null);
            stmt.setDate(3, dataInicio);
            stmt.setDate(4, dataFim);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("nome", rs.getString("nome"));
                    row.put("data_nascimento", rs.getDate("data_nascimento"));
                    row.put("idade", rs.getInt("idade"));
                    row.put("sexo", rs.getString("sexo"));
                    row.put("telefone", rs.getString("telefone"));
                    row.put("email", rs.getString("email"));
                    row.put("data_cadastro", rs.getDate("data_cadastro"));
                    row.put("total_treinos", rs.getInt("total_treinos"));
                    resultados.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar alunos: " + e.getMessage());
        }
        
        return resultados;
    }
    
    // Alunos por faixa etária
    public List<Map<String, Object>> alunosPorFaixaEtaria(int idadeMin, int idadeMax, String sexo) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String sql = "CALL sp_alunos_por_faixa_etaria(?, ?, ?)";
        
        try (Connection conn = conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, idadeMin);
            stmt.setInt(2, idadeMax);
            stmt.setString(3, sexo);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("nome", rs.getString("nome"));
                    row.put("data_nascimento", rs.getDate("data_nascimento"));
                    row.put("idade", rs.getInt("idade"));
                    row.put("sexo", rs.getString("sexo"));
                    row.put("telefone", rs.getString("telefone"));
                    row.put("email", rs.getString("email"));
                    resultados.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar por faixa etária: " + e.getMessage());
        }
        
        return resultados;
    }
    
    // Evolução do IMC de um aluno
    public List<Map<String, Object>> evolucaoIMCAluno(int idAluno) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String sql = "CALL sp_evolucao_imc_aluno(?)";
        
        try (Connection conn = conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, idAluno);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("data_avaliacao", rs.getDate("data_avaliacao"));
                    row.put("peso", rs.getBigDecimal("peso"));
                    row.put("altura", rs.getBigDecimal("altura"));
                    row.put("imc", rs.getBigDecimal("imc"));
                    row.put("classificacao", rs.getString("classificacao"));
                    row.put("percentual_gordura", rs.getBigDecimal("percentual_gordura"));
                    row.put("professor_avaliador", rs.getString("professor_avaliador"));
                    resultados.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar evolução IMC: " + e.getMessage());
        }
        
        return resultados;
    }
    
    // Treinos por foco
    public List<Map<String, Object>> treinosPorFoco(String foco, Integer idProfessor) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String sql = "CALL sp_treinos_por_foco(?, ?)";
        
        try (Connection conn = conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setString(1, foco);
            if (idProfessor != null) {
                stmt.setInt(2, idProfessor);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("treino", rs.getString("treino"));
                    row.put("aluno", rs.getString("aluno"));
                    row.put("foco_treino", rs.getString("foco_treino"));
                    row.put("total_exercicios", rs.getInt("total_exercicios"));
                    resultados.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar treinos por foco: " + e.getMessage());
        }
        
        return resultados;
    }
    
    // Estatísticas do professor
    public Map<String, Object> estatisticasProfessor(int idProfessor) {
        Map<String, Object> resultado = new HashMap<>();
        String sql = "CALL sp_estatisticas_professor(?)";
        
        try (Connection conn = conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, idProfessor);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    resultado.put("professor", rs.getString("professor"));
                    resultado.put("cref", rs.getString("cref"));
                    resultado.put("total_alunos", rs.getInt("total_alunos"));
                    resultado.put("total_treinos", rs.getInt("total_treinos"));
                    resultado.put("total_avaliacoes", rs.getInt("total_avaliacoes"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar estatísticas: " + e.getMessage());
        }
        
        return resultado;
    }
    
    // Relatório de alunos por período
    public List<Map<String, Object>> relatorioAlunosPeriodo(Date dataInicio, Date dataFim) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String sql = "CALL sp_relatorio_alunos_periodo(?, ?)";
        
        try (Connection conn = conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setDate(1, dataInicio);
            stmt.setDate(2, dataFim);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("nome", rs.getString("nome"));
                    row.put("sexo", rs.getString("sexo"));
                    row.put("data_cadastro", rs.getDate("data_cadastro"));
                    row.put("telefone", rs.getString("telefone"));
                    row.put("email", rs.getString("email"));
                    row.put("total_treinos", rs.getInt("total_treinos"));
                    row.put("total_avaliacoes", rs.getInt("total_avaliacoes"));
                    resultados.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao gerar relatório: " + e.getMessage());
        }
        
        return resultados;
    }
    
    // Ver log de auditoria
    public List<Map<String, Object>> listarLogAuditoria(int limite) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        String sql = "SELECT * FROM log_auditoria ORDER BY data_hora DESC LIMIT ?";
        
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limite);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("tabela", rs.getString("tabela"));
                    row.put("operacao", rs.getString("operacao"));
                    row.put("id_registro", rs.getInt("id_registro"));
                    row.put("usuario", rs.getString("usuario"));
                    row.put("data_hora", rs.getTimestamp("data_hora"));
                    row.put("detalhes", rs.getString("detalhes"));
                    resultados.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar auditoria: " + e.getMessage());
        }
        
        return resultados;
    }
    
    // Transferir treinos com transaction
    public int transferirTreinos(int idProfessorOrigem, int idProfessorDestino) {
        String sql = "CALL sp_transferir_treinos(?, ?, ?)";
        
        try (Connection conn = conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, idProfessorOrigem);
            stmt.setInt(2, idProfessorDestino);
            stmt.registerOutParameter(3, Types.INTEGER);
            
            stmt.execute();
            
            return stmt.getInt(3);
            
        } catch (SQLException e) {
            System.out.println("Erro ao transferir treinos: " + e.getMessage());
            return -1;
        }
    }
    
    // Cadastrar aluno com transaction
    public int cadastrarAlunoCompleto(String nome, Date dataNascimento, String sexo, 
            String telefone, String email) {
        String sql = "CALL sp_cadastrar_aluno_completo(?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = conectar();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setString(1, nome);
            stmt.setDate(2, dataNascimento);
            stmt.setString(3, sexo);
            stmt.setString(4, telefone);
            stmt.setString(5, email);
            stmt.registerOutParameter(6, Types.INTEGER);
            
            stmt.execute();
            
            return stmt.getInt(6);
            
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar aluno: " + e.getMessage());
            return -1;
        }
    }
}
