package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import dao.*;
import model.*;

public class AcademiaGUI extends JFrame {
    
    private JTabbedPane tabbedPane;
    private AlunoDAO alunoDAO;
    private ProfessorDAO professorDAO;
    private TreinoDAO treinoDAO;
    private ExercicioDAO exercicioDAO;
    private EquipamentoDAO equipamentoDAO;
    private ItemTreinoDAO itemTreinoDAO;
    private AvaliacaoFisicaDAO avaliacaoFisicaDAO;
    
    public AcademiaGUI() {
        initDAOs();
        initUI();
    }
    
    private void initDAOs() {
        alunoDAO = new AlunoDAO();
        professorDAO = new ProfessorDAO();
        treinoDAO = new TreinoDAO();
        exercicioDAO = new ExercicioDAO();
        equipamentoDAO = new EquipamentoDAO();
        itemTreinoDAO = new ItemTreinoDAO();
        avaliacaoFisicaDAO = new AvaliacaoFisicaDAO();
    }
    
    private void initUI() {
        setTitle("Sistema de Gerenciamento de Academia");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Criar painel de boas-vindas
        JPanel welcomePanel = createWelcomePanel();
        
        // Criar abas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        tabbedPane.addTab("🏠 Início", welcomePanel);
        tabbedPane.addTab("👥 Alunos", createAlunoPanel());
        tabbedPane.addTab("👨‍🏫 Professores", createProfessorPanel());
        tabbedPane.addTab("💪 Exercícios", createExercicioPanel());
        tabbedPane.addTab("🏋️ Equipamentos", createEquipamentoPanel());
        tabbedPane.addTab("📋 Treinos", createTreinoPanel());
        tabbedPane.addTab("📊 Avaliações Físicas", createAvaliacaoPanel());
        tabbedPane.addTab("📝 Log de Auditoria", createLogPanel());
        
        add(tabbedPane);
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel titleLabel = new JLabel("Sistema de Gerenciamento de Academia");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Gerencie alunos, professores, treinos e muito mais!");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(70, 130, 180));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel iconLabel = new JLabel("💪 🏋️ 🏃");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(iconLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }
    
    // ============= PAINEL DE ALUNOS =============
    private JPanel createAlunoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabela
        String[] columns = {"ID", "Nome", "Data Nasc.", "Sexo", "Telefone", "Email", "Data Cadastro"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addBtn = createStyledButton("➕ Adicionar", new Color(46, 125, 50));
        JButton editBtn = createStyledButton("✏️ Editar", new Color(255, 152, 0));
        JButton deleteBtn = createStyledButton("🗑️ Excluir", new Color(211, 47, 47));
        JButton refreshBtn = createStyledButton("🔄 Atualizar", new Color(25, 118, 210));
        
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Ações dos botões
        addBtn.addActionListener(e -> addAluno(model));
        editBtn.addActionListener(e -> editAluno(table, model));
        deleteBtn.addActionListener(e -> deleteAluno(table, model));
        refreshBtn.addActionListener(e -> loadAlunos(model));
        
        // Carregar dados iniciais
        loadAlunos(model);
        
        return panel;
    }
    
    private void loadAlunos(DefaultTableModel model) {
        model.setRowCount(0);
        List<Aluno> alunos = alunoDAO.listar();
        for (Aluno a : alunos) {
            model.addRow(new Object[]{
                a.getId(), a.getNome(), a.getDataNascimento(), a.getSexo(),
                a.getTelefone(), a.getEmail(), a.getDataCadastro()
            });
        }
    }
    
    private void addAluno(DefaultTableModel model) {
        JTextField nomeField = new JTextField(20);
        JTextField dataNascField = new JTextField(10);
        JComboBox<String> sexoCombo = new JComboBox<>(new String[]{"M", "F"});
        JTextField telefoneField = new JTextField(15);
        JTextField emailField = new JTextField(20);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Data Nascimento (YYYY-MM-DD):"));
        panel.add(dataNascField);
        panel.add(new JLabel("Sexo:"));
        panel.add(sexoCombo);
        panel.add(new JLabel("Telefone:"));
        panel.add(telefoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Adicionar Aluno", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validações básicas
                if (nomeField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "❌ Nome não pode estar vazio!", 
                        "Erro de Validação", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (telefoneField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "❌ Telefone não pode estar vazio!", 
                        "Erro de Validação", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (emailField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "❌ Email não pode estar vazio!", 
                        "Erro de Validação", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Aluno aluno = new Aluno(
                    nomeField.getText().trim(),
                    Date.valueOf(dataNascField.getText()),
                    sexoCombo.getSelectedItem().toString().charAt(0),
                    telefoneField.getText().trim(),
                    emailField.getText().trim(),
                    new Date(System.currentTimeMillis())
                );
                
                if (alunoDAO.inserir(aluno)) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ Aluno cadastrado com sucesso!\n\nID: " + aluno.getId() + "\nNome: " + aluno.getNome(),
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadAlunos(model);
                }
                
            } catch (IllegalArgumentException e) {
                // Erro de formato de data
                JOptionPane.showMessageDialog(this, 
                    "❌ Data inválida!\n\n" +
                    "Formato esperado: YYYY-MM-DD\n" +
                    "Exemplo: 1995-05-20\n\n" +
                    "Erro: " + e.getMessage(),
                    "Erro de Formato",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                // Erro do banco de dados
                String mensagemDetalhada = "❌ Erro ao cadastrar no banco de dados!\n\n";
                
                // Identificar erros comuns
                if (e.getMessage().contains("Duplicate entry")) {
                    mensagemDetalhada += "⚠️ Este aluno já está cadastrado!\n" +
                                        "Verifique se o email ou telefone já existe no sistema.";
                } else if (e.getMessage().contains("cannot be null")) {
                    mensagemDetalhada += "⚠️ Campo obrigatório não foi preenchido!";
                } else if (e.getMessage().contains("Data truncation")) {
                    mensagemDetalhada += "⚠️ Dados muito longos para o campo!";
                } else if (e.getMessage().contains("Connection")) {
                    mensagemDetalhada += "⚠️ Erro de conexão com o banco de dados!\n" +
                                        "Verifique se o MySQL está rodando.";
                } else {
                    mensagemDetalhada += "Detalhes: " + e.getMessage();
                }
                
                JOptionPane.showMessageDialog(this, 
                    mensagemDetalhada,
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
                    
                // Log completo no console
                System.err.println("=== ERRO DETALHADO ===");
                e.printStackTrace();
                
            } catch (Exception e) {
                // Qualquer outro erro
                JOptionPane.showMessageDialog(this, 
                    "❌ Erro inesperado!\n\n" +
                    "Tipo: " + e.getClass().getSimpleName() + "\n" +
                    "Mensagem: " + (e.getMessage() != null ? e.getMessage() : "Sem detalhes") + "\n\n" +
                    "Verifique o console para mais informações.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                    
                // Log completo no console
                System.err.println("=== ERRO INESPERADO ===");
                e.printStackTrace();
            }
        }
    }
    
    private void editAluno(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um aluno para editar!");
            return;
        }
        
        int id = (int) model.getValueAt(selectedRow, 0);
        String nome = (String) model.getValueAt(selectedRow, 1);
        Date dataNasc = (Date) model.getValueAt(selectedRow, 2);
        char sexo = model.getValueAt(selectedRow, 3).toString().charAt(0);
        String telefone = (String) model.getValueAt(selectedRow, 4);
        String email = (String) model.getValueAt(selectedRow, 5);
        
        JTextField nomeField = new JTextField(nome, 20);
        JTextField dataNascField = new JTextField(dataNasc.toString(), 10);
        JComboBox<String> sexoCombo = new JComboBox<>(new String[]{"M", "F"});
        sexoCombo.setSelectedItem(String.valueOf(sexo));
        JTextField telefoneField = new JTextField(telefone, 15);
        JTextField emailField = new JTextField(email, 20);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Data Nascimento (YYYY-MM-DD):"));
        panel.add(dataNascField);
        panel.add(new JLabel("Sexo:"));
        panel.add(sexoCombo);
        panel.add(new JLabel("Telefone:"));
        panel.add(telefoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Aluno", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validações básicas
                if (nomeField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "❌ Nome não pode estar vazio!", 
                        "Erro de Validação", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Aluno aluno = new Aluno(
                    nomeField.getText().trim(),
                    Date.valueOf(dataNascField.getText()),
                    sexoCombo.getSelectedItem().toString().charAt(0),
                    telefoneField.getText().trim(),
                    emailField.getText().trim(),
                    (Date) model.getValueAt(selectedRow, 6)
                );
                aluno.setId(id);
                
                if (alunoDAO.atualizar(aluno)) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ Aluno atualizado com sucesso!\n\nID: " + aluno.getId() + "\nNome: " + aluno.getNome(),
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadAlunos(model);
                }
                
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Data inválida!\n\n" +
                    "Formato esperado: YYYY-MM-DD\n" +
                    "Exemplo: 1995-05-20\n\n" +
                    "Erro: " + e.getMessage(),
                    "Erro de Formato",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                String mensagemDetalhada = "❌ Erro ao atualizar no banco de dados!\n\n";
                
                if (e.getMessage().contains("Duplicate entry")) {
                    mensagemDetalhada += "⚠️ Email ou telefone já existe em outro cadastro!";
                } else if (e.getMessage().contains("Connection")) {
                    mensagemDetalhada += "⚠️ Erro de conexão com o banco de dados!";
                } else {
                    mensagemDetalhada += "Detalhes: " + e.getMessage();
                }
                
                JOptionPane.showMessageDialog(this, 
                    mensagemDetalhada,
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
                    
                System.err.println("=== ERRO AO ATUALIZAR ===");
                e.printStackTrace();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erro inesperado!\n\n" +
                    "Tipo: " + e.getClass().getSimpleName() + "\n" +
                    "Mensagem: " + (e.getMessage() != null ? e.getMessage() : "Sem detalhes"),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                    
                System.err.println("=== ERRO INESPERADO ===");
                e.printStackTrace();
            }
        }
    }
    
    private void deleteAluno(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um aluno para excluir!");
            return;
        }
        
        int id = (int) model.getValueAt(selectedRow, 0);
        String nome = (String) model.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deseja realmente excluir o aluno " + nome + "?", 
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (alunoDAO.excluir(id)) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ Aluno excluído com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadAlunos(model);
                }
            } catch (SQLException e) {
                String mensagemDetalhada = "❌ Erro ao excluir aluno!\n\n";
                
                if (e.getMessage().contains("foreign key constraint")) {
                    mensagemDetalhada += "⚠️ Este aluno possui registros vinculados!\n\n" +
                                        "Não é possível excluir porque existem:\n" +
                                        "- Treinos cadastrados\n" +
                                        "- Avaliações físicas\n\n" +
                                        "Exclua os registros vinculados primeiro.";
                } else if (e.getMessage().contains("Connection")) {
                    mensagemDetalhada += "⚠️ Erro de conexão com o banco de dados!";
                } else {
                    mensagemDetalhada += "Detalhes: " + e.getMessage();
                }
                
                JOptionPane.showMessageDialog(this, 
                    mensagemDetalhada,
                    "Erro de Banco de Dados",
                    JOptionPane.ERROR_MESSAGE);
                    
                System.err.println("=== ERRO AO EXCLUIR ===");
                e.printStackTrace();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Erro inesperado ao excluir!\n\n" +
                    "Tipo: " + e.getClass().getSimpleName() + "\n" +
                    "Mensagem: " + (e.getMessage() != null ? e.getMessage() : "Sem detalhes"),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                    
                System.err.println("=== ERRO INESPERADO ===");
                e.printStackTrace();
            }
        }
    }
    
    // ============= PAINEL DE PROFESSORES =============
    private JPanel createProfessorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"ID", "Nome", "Sexo", "CREF", "Telefone", "Email"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addBtn = createStyledButton("➕ Adicionar", new Color(46, 125, 50));
        JButton editBtn = createStyledButton("✏️ Editar", new Color(255, 152, 0));
        JButton deleteBtn = createStyledButton("🗑️ Excluir", new Color(211, 47, 47));
        JButton refreshBtn = createStyledButton("🔄 Atualizar", new Color(25, 118, 210));
        
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        addBtn.addActionListener(e -> addProfessor(model));
        editBtn.addActionListener(e -> editProfessor(table, model));
        deleteBtn.addActionListener(e -> deleteProfessor(table, model));
        refreshBtn.addActionListener(e -> loadProfessores(model));
        
        loadProfessores(model);
        
        return panel;
    }
    
    private void loadProfessores(DefaultTableModel model) {
        model.setRowCount(0);
        List<Professor> professores = professorDAO.listar();
        for (Professor p : professores) {
            model.addRow(new Object[]{
                p.getId(), p.getNome(), p.getSexo(), p.getCref(),
                p.getTelefone(), p.getEmail()
            });
        }
    }
    
    private void addProfessor(DefaultTableModel model) {
        JTextField nomeField = new JTextField(20);
        JComboBox<String> sexoCombo = new JComboBox<>(new String[]{"M", "F"});
        JTextField crefField = new JTextField(15);
        JTextField telefoneField = new JTextField(15);
        JTextField emailField = new JTextField(20);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Sexo:"));
        panel.add(sexoCombo);
        panel.add(new JLabel("CREF:"));
        panel.add(crefField);
        panel.add(new JLabel("Telefone:"));
        panel.add(telefoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Adicionar Professor", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Professor professor = new Professor(
                    nomeField.getText(),
                    sexoCombo.getSelectedItem().toString().charAt(0),
                    crefField.getText(),
                    telefoneField.getText(),
                    emailField.getText()
                );
                
                if (professorDAO.inserir(professor)) {
                    JOptionPane.showMessageDialog(this, "Professor cadastrado com sucesso!");
                    loadProfessores(model);
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao cadastrar professor!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }
    
    private void editProfessor(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um professor para editar!");
            return;
        }
        
        int id = (int) model.getValueAt(selectedRow, 0);
        String nome = (String) model.getValueAt(selectedRow, 1);
        char sexo = model.getValueAt(selectedRow, 2).toString().charAt(0);
        String cref = (String) model.getValueAt(selectedRow, 3);
        String telefone = (String) model.getValueAt(selectedRow, 4);
        String email = (String) model.getValueAt(selectedRow, 5);
        
        JTextField nomeField = new JTextField(nome, 20);
        JComboBox<String> sexoCombo = new JComboBox<>(new String[]{"M", "F"});
        sexoCombo.setSelectedItem(String.valueOf(sexo));
        JTextField crefField = new JTextField(cref, 15);
        JTextField telefoneField = new JTextField(telefone, 15);
        JTextField emailField = new JTextField(email, 20);
        
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Sexo:"));
        panel.add(sexoCombo);
        panel.add(new JLabel("CREF:"));
        panel.add(crefField);
        panel.add(new JLabel("Telefone:"));
        panel.add(telefoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Professor", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                Professor professor = new Professor(
                    nomeField.getText(),
                    sexoCombo.getSelectedItem().toString().charAt(0),
                    crefField.getText(),
                    telefoneField.getText(),
                    emailField.getText()
                );
                professor.setId(id);
                
                if (professorDAO.atualizar(professor)) {
                    JOptionPane.showMessageDialog(this, "Professor atualizado com sucesso!");
                    loadProfessores(model);
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar professor!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }
    
    private void deleteProfessor(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um professor para excluir!");
            return;
        }
        
        int id = (int) model.getValueAt(selectedRow, 0);
        String nome = (String) model.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deseja realmente excluir o professor " + nome + "?", 
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (professorDAO.excluir(id)) {
                JOptionPane.showMessageDialog(this, "Professor excluído com sucesso!");
                loadProfessores(model);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir professor!");
            }
        }
    }
    
    // ============= PAINEL DE EXERCÍCIOS =============
    private JPanel createExercicioPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"ID", "Nome"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addBtn = createStyledButton("➕ Adicionar", new Color(46, 125, 50));
        JButton editBtn = createStyledButton("✏️ Editar", new Color(255, 152, 0));
        JButton deleteBtn = createStyledButton("🗑️ Excluir", new Color(211, 47, 47));
        JButton refreshBtn = createStyledButton("🔄 Atualizar", new Color(25, 118, 210));
        
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        addBtn.addActionListener(e -> addExercicio(model));
        editBtn.addActionListener(e -> editExercicio(table, model));
        deleteBtn.addActionListener(e -> deleteExercicio(table, model));
        refreshBtn.addActionListener(e -> loadExercicios(model));
        
        loadExercicios(model);
        
        return panel;
    }
    
    private void loadExercicios(DefaultTableModel model) {
        model.setRowCount(0);
        List<Exercicio> exercicios = exercicioDAO.listar();
        for (Exercicio e : exercicios) {
            model.addRow(new Object[]{e.getId(), e.getNome()});
        }
    }
    
    private void addExercicio(DefaultTableModel model) {
        String nome = JOptionPane.showInputDialog(this, "Nome do Exercício:", "Adicionar Exercício", JOptionPane.PLAIN_MESSAGE);
        
        if (nome != null && !nome.trim().isEmpty()) {
            Exercicio exercicio = new Exercicio(nome);
            if (exercicioDAO.inserir(exercicio)) {
                JOptionPane.showMessageDialog(this, "Exercício cadastrado com sucesso!");
                loadExercicios(model);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar exercício!");
            }
        }
    }
    
    private void editExercicio(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um exercício para editar!");
            return;
        }
        
        int id = (int) model.getValueAt(selectedRow, 0);
        String nomeAtual = (String) model.getValueAt(selectedRow, 1);
        
        String novoNome = (String) JOptionPane.showInputDialog(this, "Nome do Exercício:", 
            "Editar Exercício", JOptionPane.PLAIN_MESSAGE, null, null, nomeAtual);
        
        if (novoNome != null && !novoNome.trim().isEmpty()) {
            Exercicio exercicio = new Exercicio(novoNome);
            exercicio.setId(id);
            
            if (exercicioDAO.atualizar(exercicio)) {
                JOptionPane.showMessageDialog(this, "Exercício atualizado com sucesso!");
                loadExercicios(model);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar exercício!");
            }
        }
    }
    
    private void deleteExercicio(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um exercício para excluir!");
            return;
        }
        
        int id = (int) model.getValueAt(selectedRow, 0);
        String nome = (String) model.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deseja realmente excluir o exercício " + nome + "?", 
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (exercicioDAO.excluir(id)) {
                JOptionPane.showMessageDialog(this, "Exercício excluído com sucesso!");
                loadExercicios(model);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir exercício!");
            }
        }
    }
    
    // ============= PAINEL DE EQUIPAMENTOS =============
    private JPanel createEquipamentoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"ID", "Nome", "Tipo"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addBtn = createStyledButton("➕ Adicionar", new Color(46, 125, 50));
        JButton editBtn = createStyledButton("✏️ Editar", new Color(255, 152, 0));
        JButton deleteBtn = createStyledButton("🗑️ Excluir", new Color(211, 47, 47));
        JButton refreshBtn = createStyledButton("🔄 Atualizar", new Color(25, 118, 210));
        
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        addBtn.addActionListener(e -> addEquipamento(model));
        editBtn.addActionListener(e -> editEquipamento(table, model));
        deleteBtn.addActionListener(e -> deleteEquipamento(table, model));
        refreshBtn.addActionListener(e -> loadEquipamentos(model));
        
        loadEquipamentos(model);
        
        return panel;
    }
    
    private void loadEquipamentos(DefaultTableModel model) {
        model.setRowCount(0);
        List<Equipamento> equipamentos = equipamentoDAO.listar();
        for (Equipamento e : equipamentos) {
            model.addRow(new Object[]{e.getId(), e.getNome(), e.getTipo()});
        }
    }
    
    private void addEquipamento(DefaultTableModel model) {
        JTextField nomeField = new JTextField(20);
        JTextField tipoField = new JTextField(20);
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Tipo:"));
        panel.add(tipoField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Adicionar Equipamento", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            Equipamento equipamento = new Equipamento(nomeField.getText(), tipoField.getText());
            if (equipamentoDAO.inserir(equipamento)) {
                JOptionPane.showMessageDialog(this, "Equipamento cadastrado com sucesso!");
                loadEquipamentos(model);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar equipamento!");
            }
        }
    }
    
    private void editEquipamento(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um equipamento para editar!");
            return;
        }
        
        int id = (int) model.getValueAt(selectedRow, 0);
        String nome = (String) model.getValueAt(selectedRow, 1);
        String tipo = (String) model.getValueAt(selectedRow, 2);
        
        JTextField nomeField = new JTextField(nome, 20);
        JTextField tipoField = new JTextField(tipo, 20);
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);
        panel.add(new JLabel("Tipo:"));
        panel.add(tipoField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Equipamento", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            Equipamento equipamento = new Equipamento(nomeField.getText(), tipoField.getText());
            equipamento.setId(id);
            
            if (equipamentoDAO.atualizar(equipamento)) {
                JOptionPane.showMessageDialog(this, "Equipamento atualizado com sucesso!");
                loadEquipamentos(model);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar equipamento!");
            }
        }
    }
    
    private void deleteEquipamento(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um equipamento para excluir!");
            return;
        }
        
        int id = (int) model.getValueAt(selectedRow, 0);
        String nome = (String) model.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deseja realmente excluir o equipamento " + nome + "?", 
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (equipamentoDAO.excluir(id)) {
                JOptionPane.showMessageDialog(this, "Equipamento excluído com sucesso!");
                loadEquipamentos(model);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir equipamento!");
            }
        }
    }
    
    // ============= PAINEL DE TREINOS =============
    private JPanel createTreinoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"ID", "Aluno", "Professor", "Nome", "Foco"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addBtn = createStyledButton("➕ Adicionar", new Color(46, 125, 50));
        JButton editBtn = createStyledButton("✏️ Editar", new Color(255, 152, 0));
        JButton deleteBtn = createStyledButton("🗑️ Excluir", new Color(211, 47, 47));
        JButton refreshBtn = createStyledButton("🔄 Atualizar", new Color(25, 118, 210));
        
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        addBtn.addActionListener(e -> addTreino(model));
        editBtn.addActionListener(e -> editTreino(table, model));
        deleteBtn.addActionListener(e -> deleteTreino(table, model));
        refreshBtn.addActionListener(e -> loadTreinos(model));
        
        loadTreinos(model);
        
        return panel;
    }
    
    private void loadTreinos(DefaultTableModel model) {
        model.setRowCount(0);
        List<Treino> treinos = treinoDAO.listar();
        for (Treino t : treinos) {
            model.addRow(new Object[]{
                t.getId(), t.getIdAluno(), t.getIdProfessor(), t.getNome(), t.getFocoTreino()
            });
        }
    }
    
    private void addTreino(DefaultTableModel model) {
        JComboBox<String> alunoCombo = new JComboBox<>();
        JComboBox<String> professorCombo = new JComboBox<>();
        JTextField nomeField = new JTextField(20);
        JTextField focoField = new JTextField(20);
        
        // Carregar alunos
        List<Aluno> alunos = alunoDAO.listar();
        for (Aluno a : alunos) {
            alunoCombo.addItem(a.getId() + " - " + a.getNome());
        }
        
        // Carregar professores
        List<Professor> professores = professorDAO.listar();
        for (Professor p : professores) {
            professorCombo.addItem(p.getId() + " - " + p.getNome());
        }
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Aluno:"));
        panel.add(alunoCombo);
        panel.add(new JLabel("Professor:"));
        panel.add(professorCombo);
        panel.add(new JLabel("Nome do Treino:"));
        panel.add(nomeField);
        panel.add(new JLabel("Foco:"));
        panel.add(focoField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Adicionar Treino", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String alunoSel = (String) alunoCombo.getSelectedItem();
                String professorSel = (String) professorCombo.getSelectedItem();
                
                int idAluno = Integer.parseInt(alunoSel.split(" - ")[0]);
                int idProfessor = Integer.parseInt(professorSel.split(" - ")[0]);
                
                Treino treino = new Treino(idAluno, idProfessor, nomeField.getText(), focoField.getText());
                
                if (treinoDAO.inserir(treino)) {
                    JOptionPane.showMessageDialog(this, "Treino cadastrado com sucesso!");
                    loadTreinos(model);
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao cadastrar treino!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }
    
    private void editTreino(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um treino para editar!");
            return;
        }
        
        int id = (int) model.getValueAt(selectedRow, 0);
        int idAluno = (int) model.getValueAt(selectedRow, 1);
        int idProfessor = (int) model.getValueAt(selectedRow, 2);
        String nome = (String) model.getValueAt(selectedRow, 3);
        String foco = (String) model.getValueAt(selectedRow, 4);
        
        JComboBox<String> alunoCombo = new JComboBox<>();
        JComboBox<String> professorCombo = new JComboBox<>();
        JTextField nomeField = new JTextField(nome, 20);
        JTextField focoField = new JTextField(foco, 20);
        
        List<Aluno> alunos = alunoDAO.listar();
        for (Aluno a : alunos) {
            alunoCombo.addItem(a.getId() + " - " + a.getNome());
            if (a.getId() == idAluno) {
                alunoCombo.setSelectedIndex(alunoCombo.getItemCount() - 1);
            }
        }
        
        List<Professor> professores = professorDAO.listar();
        for (Professor p : professores) {
            professorCombo.addItem(p.getId() + " - " + p.getNome());
            if (p.getId() == idProfessor) {
                professorCombo.setSelectedIndex(professorCombo.getItemCount() - 1);
            }
        }
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Aluno:"));
        panel.add(alunoCombo);
        panel.add(new JLabel("Professor:"));
        panel.add(professorCombo);
        panel.add(new JLabel("Nome do Treino:"));
        panel.add(nomeField);
        panel.add(new JLabel("Foco:"));
        panel.add(focoField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Treino", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String alunoSel = (String) alunoCombo.getSelectedItem();
                String professorSel = (String) professorCombo.getSelectedItem();
                
                int newIdAluno = Integer.parseInt(alunoSel.split(" - ")[0]);
                int newIdProfessor = Integer.parseInt(professorSel.split(" - ")[0]);
                
                Treino treino = new Treino(newIdAluno, newIdProfessor, nomeField.getText(), focoField.getText());
                treino.setId(id);
                
                if (treinoDAO.atualizar(treino)) {
                    JOptionPane.showMessageDialog(this, "Treino atualizado com sucesso!");
                    loadTreinos(model);
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar treino!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }
    
    private void deleteTreino(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um treino para excluir!");
            return;
        }
        
        int id = (int) model.getValueAt(selectedRow, 0);
        String nome = (String) model.getValueAt(selectedRow, 3);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deseja realmente excluir o treino " + nome + "?", 
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (treinoDAO.excluir(id)) {
                JOptionPane.showMessageDialog(this, "Treino excluído com sucesso!");
                loadTreinos(model);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir treino!");
            }
        }
    }
    
    // ============= PAINEL DE AVALIAÇÕES FÍSICAS =============
    private JPanel createAvaliacaoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"ID", "Aluno", "Professor", "Data", "Altura", "Peso", "% Gordura", "IMC"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addBtn = createStyledButton("➕ Adicionar", new Color(46, 125, 50));
        JButton deleteBtn = createStyledButton("🗑️ Excluir", new Color(211, 47, 47));
        JButton refreshBtn = createStyledButton("🔄 Atualizar", new Color(25, 118, 210));
        
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        addBtn.addActionListener(e -> addAvaliacao(model));
        deleteBtn.addActionListener(e -> deleteAvaliacao(table, model));
        refreshBtn.addActionListener(e -> loadAvaliacoes(model));
        
        loadAvaliacoes(model);
        
        return panel;
    }
    
    private void loadAvaliacoes(DefaultTableModel model) {
        model.setRowCount(0);
        // Carregar todas as avaliações de todos os alunos
        List<Aluno> alunos = alunoDAO.listar();
        for (Aluno aluno : alunos) {
            List<AvaliacaoFisica> avaliacoes = avaliacaoFisicaDAO.listarPorAluno(aluno.getId());
            for (AvaliacaoFisica av : avaliacoes) {
                model.addRow(new Object[]{
                    av.getId(), av.getIdAluno(), av.getIdProfessor(), av.getDataAvaliacao(),
                    av.getAltura(), av.getPeso(), av.getPercentualGordura(), av.getImc()
                });
            }
        }
    }
    
    private void addAvaliacao(DefaultTableModel model) {
        JComboBox<String> alunoCombo = new JComboBox<>();
        JComboBox<String> professorCombo = new JComboBox<>();
        JTextField dataField = new JTextField(10);
        JTextField alturaField = new JTextField(10);
        JTextField pesoField = new JTextField(10);
        JTextField gorduraField = new JTextField(10);
        
        List<Aluno> alunos = alunoDAO.listar();
        for (Aluno a : alunos) {
            alunoCombo.addItem(a.getId() + " - " + a.getNome());
        }
        
        List<Professor> professores = professorDAO.listar();
        for (Professor p : professores) {
            professorCombo.addItem(p.getId() + " - " + p.getNome());
        }
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.add(new JLabel("Aluno:"));
        panel.add(alunoCombo);
        panel.add(new JLabel("Professor:"));
        panel.add(professorCombo);
        panel.add(new JLabel("Data (YYYY-MM-DD):"));
        panel.add(dataField);
        panel.add(new JLabel("Altura (m):"));
        panel.add(alturaField);
        panel.add(new JLabel("Peso (kg):"));
        panel.add(pesoField);
        panel.add(new JLabel("% Gordura:"));
        panel.add(gorduraField);
        
        // Adicionar label informativo sobre IMC
        JLabel imcInfo = new JLabel("ℹ️ IMC será calculado automaticamente");
        imcInfo.setFont(new Font("Arial", Font.ITALIC, 11));
        imcInfo.setForeground(new Color(70, 130, 180));
        
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(imcInfo, BorderLayout.SOUTH);
        
        int result = JOptionPane.showConfirmDialog(this, mainPanel, "Adicionar Avaliação Física", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String alunoSel = (String) alunoCombo.getSelectedItem();
                String professorSel = (String) professorCombo.getSelectedItem();
                
                int idAluno = Integer.parseInt(alunoSel.split(" - ")[0]);
                int idProfessor = Integer.parseInt(professorSel.split(" - ")[0]);
                
                BigDecimal altura = new BigDecimal(alturaField.getText());
                BigDecimal peso = new BigDecimal(pesoField.getText());
                
                // Calcular IMC automaticamente
                BigDecimal imc = peso.divide(altura.multiply(altura), 2, BigDecimal.ROUND_HALF_UP);
                
                AvaliacaoFisica av = new AvaliacaoFisica(
                    idAluno,
                    idProfessor,
                    Date.valueOf(dataField.getText()),
                    altura,
                    peso,
                    new BigDecimal(gorduraField.getText()),
                    imc
                );
                
                if (avaliacaoFisicaDAO.inserir(av)) {
                    String classificacao = classificarIMC(imc.doubleValue());
                    JOptionPane.showMessageDialog(this, 
                        "Avaliação cadastrada com sucesso!\n" +
                        "IMC Calculado: " + imc + "\n" +
                        "Classificação: " + classificacao,
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    loadAvaliacoes(model);
                } else {
                    JOptionPane.showMessageDialog(this, "Erro ao cadastrar avaliação!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }
    
    // Função auxiliar para classificar IMC
    private String classificarIMC(double imc) {
        if (imc < 18.5) return "Abaixo do peso";
        else if (imc < 25) return "Peso normal";
        else if (imc < 30) return "Sobrepeso";
        else if (imc < 35) return "Obesidade Grau I";
        else if (imc < 40) return "Obesidade Grau II";
        else return "Obesidade Grau III";
    }
    
    private void deleteAvaliacao(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma avaliação para excluir!");
            return;
        }
        
        int id = (int) model.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deseja realmente excluir esta avaliação?", 
            "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (avaliacaoFisicaDAO.excluir(id)) {
                JOptionPane.showMessageDialog(this, "Avaliação excluída com sucesso!");
                loadAvaliacoes(model);
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir avaliação!");
            }
        }
    }
    
    // ============= MÉTODOS AUXILIARES =============
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 35));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    // ============= PAINEL DE LOG DE AUDITORIA =============
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel superior com filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtros"));
        
        JLabel tabelaLabel = new JLabel("Tabela:");
        JComboBox<String> tabelaCombo = new JComboBox<>(new String[]{
            "Todas", "aluno", "professor", "treino", "avaliacao_fisica", 
            "exercicio", "equipamento", "item_treino"
        });
        
        JLabel limiteLabel = new JLabel("Limite:");
        JSpinner limiteSpinner = new JSpinner(new SpinnerNumberModel(50, 10, 500, 10));
        
        JButton refreshBtn = createStyledButton("🔄 Atualizar", new Color(25, 118, 210));
        JButton clearBtn = createStyledButton("🗑️ Limpar Logs Antigos", new Color(211, 47, 47));
        JButton statsBtn = createStyledButton("📊 Estatísticas", new Color(255, 152, 0));
        
        filterPanel.add(tabelaLabel);
        filterPanel.add(tabelaCombo);
        filterPanel.add(limiteLabel);
        filterPanel.add(limiteSpinner);
        filterPanel.add(refreshBtn);
        filterPanel.add(statsBtn);
        filterPanel.add(clearBtn);
        
        // Tabela de log
        String[] columns = {"ID", "Tabela", "Operação", "ID Registro", "Usuário", "Data/Hora", "Detalhes"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        
        // Ajustar largura das colunas
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Tabela
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Operação
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // ID Registro
        table.getColumnModel().getColumn(4).setPreferredWidth(150); // Usuário
        table.getColumnModel().getColumn(5).setPreferredWidth(150); // Data/Hora
        table.getColumnModel().getColumn(6).setPreferredWidth(300); // Detalhes
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Painel de estatísticas
        JTextArea statsArea = new JTextArea(3, 50);
        statsArea.setEditable(false);
        statsArea.setBackground(new Color(240, 248, 255));
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane statsScroll = new JScrollPane(statsArea);
        statsScroll.setBorder(BorderFactory.createTitledBorder("Estatísticas"));
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statsScroll, BorderLayout.SOUTH);
        
        // Ações dos botões
        refreshBtn.addActionListener(e -> {
            String tabela = (String) tabelaCombo.getSelectedItem();
            int limite = (Integer) limiteSpinner.getValue();
            loadLogAuditoria(model, tabela, limite);
            updateLogStats(statsArea);
        });
        
        statsBtn.addActionListener(e -> {
            showLogStatistics();
        });
        
        clearBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, 
                "Limpar logs com mais de quantos meses?\n(Digite o número de meses):",
                "Limpar Logs Antigos", JOptionPane.QUESTION_MESSAGE);
            
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int meses = Integer.parseInt(input);
                    if (meses > 0) {
                        int confirm = JOptionPane.showConfirmDialog(this,
                            "Tem certeza que deseja limpar logs com mais de " + meses + " meses?",
                            "Confirmar", JOptionPane.YES_NO_OPTION);
                        
                        if (confirm == JOptionPane.YES_OPTION) {
                            limparLogsAntigos(meses);
                            loadLogAuditoria(model, (String) tabelaCombo.getSelectedItem(), 
                                (Integer) limiteSpinner.getValue());
                            updateLogStats(statsArea);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Digite um número válido!");
                }
            }
        });
        
        // Carregar log inicial
        loadLogAuditoria(model, "Todas", 50);
        updateLogStats(statsArea);
        
        return panel;
    }
    
    private void loadLogAuditoria(DefaultTableModel model, String tabela, int limite) {
        model.setRowCount(0);
        try {
            java.sql.Connection conn = dao.DatabaseConnection.getConnection();
            String sql;
            
            if (tabela.equals("Todas")) {
                sql = "SELECT id, tabela, operacao, id_registro, usuario, " +
                      "DATE_FORMAT(data_hora, '%d/%m/%Y %H:%i:%s') as data_hora_formatada, " +
                      "detalhes FROM log_auditoria ORDER BY data_hora DESC LIMIT ?";
            } else {
                sql = "SELECT id, tabela, operacao, id_registro, usuario, " +
                      "DATE_FORMAT(data_hora, '%d/%m/%Y %H:%i:%s') as data_hora_formatada, " +
                      "detalhes FROM log_auditoria WHERE tabela = ? ORDER BY data_hora DESC LIMIT ?";
            }
            
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            
            if (tabela.equals("Todas")) {
                stmt.setInt(1, limite);
            } else {
                stmt.setString(1, tabela);
                stmt.setInt(2, limite);
            }
            
            java.sql.ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("tabela"),
                    rs.getString("operacao"),
                    rs.getInt("id_registro"),
                    rs.getString("usuario"),
                    rs.getString("data_hora_formatada"),
                    rs.getString("detalhes")
                });
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar log: " + ex.getMessage());
        }
    }
    
    private void updateLogStats(JTextArea statsArea) {
        try {
            java.sql.Connection conn = dao.DatabaseConnection.getConnection();
            String sql = "SELECT COUNT(*) as total, " +
                        "SUM(CASE WHEN operacao = 'INSERT' THEN 1 ELSE 0 END) as inserts, " +
                        "SUM(CASE WHEN operacao = 'UPDATE' THEN 1 ELSE 0 END) as updates, " +
                        "SUM(CASE WHEN operacao = 'DELETE' THEN 1 ELSE 0 END) as deletes " +
                        "FROM log_auditoria";
            
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                int total = rs.getInt("total");
                int inserts = rs.getInt("inserts");
                int updates = rs.getInt("updates");
                int deletes = rs.getInt("deletes");
                
                statsArea.setText(String.format(
                    "📊 ESTATÍSTICAS DO LOG:\n" +
                    "Total de Operações: %d  |  INSERT: %d  |  UPDATE: %d  |  DELETE: %d",
                    total, inserts, updates, deletes
                ));
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception ex) {
            statsArea.setText("Erro ao carregar estatísticas: " + ex.getMessage());
        }
    }
    
    private void showLogStatistics() {
        try {
            java.sql.Connection conn = dao.DatabaseConnection.getConnection();
            String sql = "SELECT tabela, operacao, COUNT(*) as total " +
                        "FROM log_auditoria " +
                        "GROUP BY tabela, operacao " +
                        "ORDER BY tabela, operacao";
            
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(sql);
            
            StringBuilder sb = new StringBuilder();
            sb.append("📊 ESTATÍSTICAS DETALHADAS POR TABELA:\n\n");
            
            String tabelaAtual = "";
            while (rs.next()) {
                String tabela = rs.getString("tabela");
                String operacao = rs.getString("operacao");
                int total = rs.getInt("total");
                
                if (!tabela.equals(tabelaAtual)) {
                    if (!tabelaAtual.isEmpty()) {
                        sb.append("\n");
                    }
                    sb.append("📋 ").append(tabela.toUpperCase()).append(":\n");
                    tabelaAtual = tabela;
                }
                
                sb.append("   ").append(operacao).append(": ").append(total).append("\n");
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            JTextArea textArea = new JTextArea(20, 50);
            textArea.setText(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(this, scrollPane, "Estatísticas Detalhadas", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar estatísticas: " + ex.getMessage());
        }
    }
    
    private void limparLogsAntigos(int meses) {
        try {
            java.sql.Connection conn = dao.DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            String sql = "DELETE FROM log_auditoria WHERE data_hora < DATE_SUB(NOW(), INTERVAL ? MONTH)";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, meses);
            
            int deletados = stmt.executeUpdate();
            
            conn.commit();
            stmt.close();
            conn.close();
            
            JOptionPane.showMessageDialog(this, 
                deletados + " registros de log foram removidos.",
                "Limpeza Concluída", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao limpar logs: " + ex.getMessage());
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            AcademiaGUI gui = new AcademiaGUI();
            gui.setVisible(true);
        });
    }
}
