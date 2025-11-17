-- Criação do banco de dados
CREATE DATABASE IF NOT EXISTS academia;
USE academia;

-- Tabela: professor
CREATE TABLE professor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    sexo CHAR(1),
    cref VARCHAR(20) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    email VARCHAR(100)
);

-- Tabela: aluno
CREATE TABLE aluno (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    data_nascimento DATE,
    sexo CHAR(1),
    telefone VARCHAR(20),
    email VARCHAR(100),
    data_cadastro DATE NOT NULL
);

-- Tabela: equipamento
CREATE TABLE equipamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(50)
);

-- Tabela: exercicio
CREATE TABLE exercicio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

-- Tabela: treino
CREATE TABLE treino (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_aluno INT NOT NULL,
    id_professor INT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    foco_treino VARCHAR(100),
    FOREIGN KEY (id_aluno) REFERENCES aluno(id),
    FOREIGN KEY (id_professor) REFERENCES professor(id)
);

-- Tabela: item_treino
CREATE TABLE item_treino (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_treino INT NOT NULL,
    id_exercicio INT NOT NULL,
    id_equipamento INT NOT NULL,
    series INT,
    repeticoes INT,
    carga DECIMAL(10,2),
    FOREIGN KEY (id_treino) REFERENCES treino(id),
    FOREIGN KEY (id_exercicio) REFERENCES exercicio(id),
    FOREIGN KEY (id_equipamento) REFERENCES equipamento(id)
);

-- Tabela: avaliacao_fisica
CREATE TABLE avaliacao_fisica (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_aluno INT NOT NULL,
    id_professor INT NOT NULL,
    data_avaliacao DATE NOT NULL,
    altura DECIMAL(5,2),
    peso DECIMAL(5,2),
    percentual_gordura DECIMAL(5,2),
    imc DECIMAL(5,2),
    FOREIGN KEY (id_aluno) REFERENCES aluno(id),
    FOREIGN KEY (id_professor) REFERENCES professor(id)
);
-- =============================================
-- SCRIPT AVANÇADO - BANCO DE DADOS ACADEMIA
-- Triggers, Procedures, Functions e Views
-- =============================================

USE academia;

-- =============================================
-- TABELAS AUXILIARES
-- =============================================

-- Tabela de log de auditoria
CREATE TABLE IF NOT EXISTS log_auditoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tabela VARCHAR(50) NOT NULL,
    operacao VARCHAR(20) NOT NULL,
    id_registro INT,
    usuario VARCHAR(100),
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    detalhes TEXT
);

-- Tabela de histórico de preços (caso implementem mensalidades)
CREATE TABLE IF NOT EXISTS historico_mudancas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tabela VARCHAR(50),
    id_registro INT,
    campo_alterado VARCHAR(50),
    valor_antigo TEXT,
    valor_novo TEXT,
    data_mudanca TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- TRIGGERS
-- =============================================

-- Trigger: Auditoria ao inserir aluno
DELIMITER //
CREATE TRIGGER trg_aluno_insert_audit
AFTER INSERT ON aluno
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('aluno', 'INSERT', NEW.id, USER(), 
            CONCAT('Novo aluno cadastrado: ', NEW.nome));
END//
DELIMITER ;

-- Trigger: Auditoria ao atualizar aluno
DELIMITER //
CREATE TRIGGER trg_aluno_update_audit
AFTER UPDATE ON aluno
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('aluno', 'UPDATE', NEW.id, USER(), 
            CONCAT('Aluno atualizado: ', NEW.nome));
    
    -- Registrar mudanças específicas
    IF OLD.telefone != NEW.telefone THEN
        INSERT INTO historico_mudancas (tabela, id_registro, campo_alterado, valor_antigo, valor_novo)
        VALUES ('aluno', NEW.id, 'telefone', OLD.telefone, NEW.telefone);
    END IF;
    
    IF OLD.email != NEW.email THEN
        INSERT INTO historico_mudancas (tabela, id_registro, campo_alterado, valor_antigo, valor_novo)
        VALUES ('aluno', NEW.id, 'email', OLD.email, NEW.email);
    END IF;
END//
DELIMITER ;

-- Trigger: Auditoria ao deletar aluno
DELIMITER //
CREATE TRIGGER trg_aluno_delete_audit
BEFORE DELETE ON aluno
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('aluno', 'DELETE', OLD.id, USER(), 
            CONCAT('Aluno deletado: ', OLD.nome));
END//
DELIMITER ;

-- Trigger: Validar IMC ao inserir avaliação física
DELIMITER //
CREATE TRIGGER trg_validar_imc_insert
BEFORE INSERT ON avaliacao_fisica
FOR EACH ROW
BEGIN
    DECLARE imc_calculado DECIMAL(5,2);
    
    -- Calcular IMC: peso / (altura * altura)
    IF NEW.altura > 0 AND NEW.peso > 0 THEN
        SET imc_calculado = NEW.peso / (NEW.altura * NEW.altura);
        SET NEW.imc = imc_calculado;
    END IF;
    
    -- Validar valores mínimos
    IF NEW.altura < 0.5 OR NEW.altura > 2.5 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Altura deve estar entre 0.5m e 2.5m';
    END IF;
    
    IF NEW.peso < 20 OR NEW.peso > 300 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Peso deve estar entre 20kg e 300kg';
    END IF;
END//
DELIMITER ;

-- Trigger: Atualizar IMC automaticamente
DELIMITER //
CREATE TRIGGER trg_atualizar_imc_update
BEFORE UPDATE ON avaliacao_fisica
FOR EACH ROW
BEGIN
    IF NEW.altura > 0 AND NEW.peso > 0 THEN
        SET NEW.imc = NEW.peso / (NEW.altura * NEW.altura);
    END IF;
END//
DELIMITER ;

-- Trigger: Impedir exclusão de professor com treinos ativos
DELIMITER //
CREATE TRIGGER trg_proteger_professor
BEFORE DELETE ON professor
FOR EACH ROW
BEGIN
    DECLARE total_treinos INT;
    
    SELECT COUNT(*) INTO total_treinos
    FROM treino
    WHERE id_professor = OLD.id;
    
    IF total_treinos > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Não é possível excluir professor com treinos cadastrados';
    END IF;
END//
DELIMITER ;

-- =============================================
-- TRIGGERS PARA PROFESSOR
-- =============================================

-- Trigger: Auditoria ao inserir professor
DELIMITER //
CREATE TRIGGER trg_professor_insert_audit
AFTER INSERT ON professor
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('professor', 'INSERT', NEW.id, USER(), 
            CONCAT('Novo professor cadastrado: ', NEW.nome, ' - CREF: ', NEW.cref));
END//
DELIMITER ;

-- Trigger: Auditoria ao atualizar professor
DELIMITER //
CREATE TRIGGER trg_professor_update_audit
AFTER UPDATE ON professor
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('professor', 'UPDATE', NEW.id, USER(), 
            CONCAT('Professor atualizado: ', NEW.nome));
    
    IF OLD.cref != NEW.cref THEN
        INSERT INTO historico_mudancas (tabela, id_registro, campo_alterado, valor_antigo, valor_novo)
        VALUES ('professor', NEW.id, 'cref', OLD.cref, NEW.cref);
    END IF;
    
    IF OLD.telefone != NEW.telefone THEN
        INSERT INTO historico_mudancas (tabela, id_registro, campo_alterado, valor_antigo, valor_novo)
        VALUES ('professor', NEW.id, 'telefone', OLD.telefone, NEW.telefone);
    END IF;
    
    IF OLD.email != NEW.email THEN
        INSERT INTO historico_mudancas (tabela, id_registro, campo_alterado, valor_antigo, valor_novo)
        VALUES ('professor', NEW.id, 'email', OLD.email, NEW.email);
    END IF;
END//
DELIMITER ;

-- Trigger: Auditoria ao deletar professor
DELIMITER //
CREATE TRIGGER trg_professor_delete_audit
BEFORE DELETE ON professor
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('professor', 'DELETE', OLD.id, USER(), 
            CONCAT('Professor deletado: ', OLD.nome, ' - CREF: ', OLD.cref));
END//
DELIMITER ;

-- =============================================
-- TRIGGERS PARA TREINO
-- =============================================

-- Trigger: Auditoria ao inserir treino
DELIMITER //
CREATE TRIGGER trg_treino_insert_audit
AFTER INSERT ON treino
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('treino', 'INSERT', NEW.id, USER(), 
            CONCAT('Novo treino cadastrado: ', NEW.nome, ' - Foco: ', IFNULL(NEW.foco_treino, 'N/A')));
END//
DELIMITER ;

-- Trigger: Auditoria ao atualizar treino
DELIMITER //
CREATE TRIGGER trg_treino_update_audit
AFTER UPDATE ON treino
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('treino', 'UPDATE', NEW.id, USER(), 
            CONCAT('Treino atualizado: ', NEW.nome));
    
    IF OLD.nome != NEW.nome THEN
        INSERT INTO historico_mudancas (tabela, id_registro, campo_alterado, valor_antigo, valor_novo)
        VALUES ('treino', NEW.id, 'nome', OLD.nome, NEW.nome);
    END IF;
    
    IF OLD.foco_treino != NEW.foco_treino THEN
        INSERT INTO historico_mudancas (tabela, id_registro, campo_alterado, valor_antigo, valor_novo)
        VALUES ('treino', NEW.id, 'foco_treino', OLD.foco_treino, NEW.foco_treino);
    END IF;
END//
DELIMITER ;

-- Trigger: Auditoria ao deletar treino
DELIMITER //
CREATE TRIGGER trg_treino_delete_audit
BEFORE DELETE ON treino
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('treino', 'DELETE', OLD.id, USER(), 
            CONCAT('Treino deletado: ', OLD.nome));
END//
DELIMITER ;

-- =============================================
-- TRIGGERS PARA AVALIAÇÃO FÍSICA
-- =============================================

-- Trigger: Auditoria ao inserir avaliação física
DELIMITER //
CREATE TRIGGER trg_avaliacao_insert_audit
AFTER INSERT ON avaliacao_fisica
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('avaliacao_fisica', 'INSERT', NEW.id, USER(), 
            CONCAT('Nova avaliação física - Aluno ID: ', NEW.id_aluno, 
                   ' - IMC: ', ROUND(NEW.imc, 2), 
                   ' - Peso: ', NEW.peso, 'kg'));
END//
DELIMITER ;

-- Trigger: Auditoria ao atualizar avaliação física
DELIMITER //
CREATE TRIGGER trg_avaliacao_update_audit
AFTER UPDATE ON avaliacao_fisica
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('avaliacao_fisica', 'UPDATE', NEW.id, USER(), 
            CONCAT('Avaliação física atualizada - Aluno ID: ', NEW.id_aluno));
    
    IF OLD.peso != NEW.peso THEN
        INSERT INTO historico_mudancas (tabela, id_registro, campo_alterado, valor_antigo, valor_novo)
        VALUES ('avaliacao_fisica', NEW.id, 'peso', OLD.peso, NEW.peso);
    END IF;
    
    IF OLD.altura != NEW.altura THEN
        INSERT INTO historico_mudancas (tabela, id_registro, campo_alterado, valor_antigo, valor_novo)
        VALUES ('avaliacao_fisica', NEW.id, 'altura', OLD.altura, NEW.altura);
    END IF;
END//
DELIMITER ;

-- Trigger: Auditoria ao deletar avaliação física
DELIMITER //
CREATE TRIGGER trg_avaliacao_delete_audit
BEFORE DELETE ON avaliacao_fisica
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('avaliacao_fisica', 'DELETE', OLD.id, USER(), 
            CONCAT('Avaliação física deletada - Aluno ID: ', OLD.id_aluno, 
                   ' - Data: ', OLD.data_avaliacao));
END//
DELIMITER ;

-- =============================================
-- TRIGGERS PARA EXERCÍCIO
-- =============================================

-- Trigger: Auditoria ao inserir exercício
DELIMITER //
CREATE TRIGGER trg_exercicio_insert_audit
AFTER INSERT ON exercicio
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('exercicio', 'INSERT', NEW.id, USER(), 
            CONCAT('Novo exercício cadastrado: ', NEW.nome));
END//
DELIMITER ;

-- Trigger: Auditoria ao atualizar exercício
DELIMITER //
CREATE TRIGGER trg_exercicio_update_audit
AFTER UPDATE ON exercicio
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('exercicio', 'UPDATE', NEW.id, USER(), 
            CONCAT('Exercício atualizado: ', OLD.nome, ' → ', NEW.nome));
    
    INSERT INTO historico_mudancas (tabela, id_registro, campo_alterado, valor_antigo, valor_novo)
    VALUES ('exercicio', NEW.id, 'nome', OLD.nome, NEW.nome);
END//
DELIMITER ;

-- Trigger: Auditoria ao deletar exercício
DELIMITER //
CREATE TRIGGER trg_exercicio_delete_audit
BEFORE DELETE ON exercicio
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('exercicio', 'DELETE', OLD.id, USER(), 
            CONCAT('Exercício deletado: ', OLD.nome));
END//
DELIMITER ;

-- =============================================
-- TRIGGERS PARA EQUIPAMENTO
-- =============================================

-- Trigger: Auditoria ao inserir equipamento
DELIMITER //
CREATE TRIGGER trg_equipamento_insert_audit
AFTER INSERT ON equipamento
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('equipamento', 'INSERT', NEW.id, USER(), 
            CONCAT('Novo equipamento cadastrado: ', NEW.nome, ' - Tipo: ', IFNULL(NEW.tipo, 'N/A')));
END//
DELIMITER ;

-- Trigger: Auditoria ao atualizar equipamento
DELIMITER //
CREATE TRIGGER trg_equipamento_update_audit
AFTER UPDATE ON equipamento
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('equipamento', 'UPDATE', NEW.id, USER(), 
            CONCAT('Equipamento atualizado: ', NEW.nome));
    
    IF OLD.nome != NEW.nome THEN
        INSERT INTO historico_mudancas (tabela, id_registro, campo_alterado, valor_antigo, valor_novo)
        VALUES ('equipamento', NEW.id, 'nome', OLD.nome, NEW.nome);
    END IF;
    
    IF OLD.tipo != NEW.tipo THEN
        INSERT INTO historico_mudancas (tabela, id_registro, campo_alterado, valor_antigo, valor_novo)
        VALUES ('equipamento', NEW.id, 'tipo', OLD.tipo, NEW.tipo);
    END IF;
END//
DELIMITER ;

-- Trigger: Auditoria ao deletar equipamento
DELIMITER //
CREATE TRIGGER trg_equipamento_delete_audit
BEFORE DELETE ON equipamento
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('equipamento', 'DELETE', OLD.id, USER(), 
            CONCAT('Equipamento deletado: ', OLD.nome));
END//
DELIMITER ;

-- =============================================
-- TRIGGERS PARA ITEM_TREINO
-- =============================================

-- Trigger: Auditoria ao inserir item de treino
DELIMITER //
CREATE TRIGGER trg_item_treino_insert_audit
AFTER INSERT ON item_treino
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('item_treino', 'INSERT', NEW.id, USER(), 
            CONCAT('Novo item adicionado ao treino ID: ', NEW.id_treino, 
                   ' - ', NEW.series, 'x', NEW.repeticoes));
END//
DELIMITER ;

-- Trigger: Auditoria ao atualizar item de treino
DELIMITER //
CREATE TRIGGER trg_item_treino_update_audit
AFTER UPDATE ON item_treino
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('item_treino', 'UPDATE', NEW.id, USER(), 
            CONCAT('Item de treino atualizado - Treino ID: ', NEW.id_treino));
END//
DELIMITER ;

-- Trigger: Auditoria ao deletar item de treino
DELIMITER //
CREATE TRIGGER trg_item_treino_delete_audit
BEFORE DELETE ON item_treino
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (tabela, operacao, id_registro, usuario, detalhes)
    VALUES ('item_treino', 'DELETE', OLD.id, USER(), 
            CONCAT('Item de treino removido - Treino ID: ', OLD.id_treino));
END//
DELIMITER ;

-- =============================================
-- STORED PROCEDURES
-- =============================================

-- Procedure: Cadastrar aluno completo com transaction
DELIMITER //
CREATE PROCEDURE sp_cadastrar_aluno_completo(
    IN p_nome VARCHAR(100),
    IN p_data_nascimento DATE,
    IN p_sexo CHAR(1),
    IN p_telefone VARCHAR(20),
    IN p_email VARCHAR(100),
    OUT p_id_aluno INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_id_aluno = -1;
    END;
    
    START TRANSACTION;
    
    -- Inserir aluno
    INSERT INTO aluno (nome, data_nascimento, sexo, telefone, email, data_cadastro)
    VALUES (p_nome, p_data_nascimento, p_sexo, p_telefone, p_email, CURDATE());
    
    SET p_id_aluno = LAST_INSERT_ID();
    
    COMMIT;
END//
DELIMITER ;

-- Procedure: Criar treino completo com itens
DELIMITER //
CREATE PROCEDURE sp_criar_treino_completo(
    IN p_id_aluno INT,
    IN p_id_professor INT,
    IN p_nome VARCHAR(100),
    IN p_foco VARCHAR(100),
    OUT p_id_treino INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_id_treino = -1;
    END;
    
    START TRANSACTION;
    
    -- Verificar se aluno existe
    IF NOT EXISTS (SELECT 1 FROM aluno WHERE id = p_id_aluno) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Aluno não encontrado';
    END IF;
    
    -- Verificar se professor existe
    IF NOT EXISTS (SELECT 1 FROM professor WHERE id = p_id_professor) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Professor não encontrado';
    END IF;
    
    -- Inserir treino
    INSERT INTO treino (id_aluno, id_professor, nome, foco_treino)
    VALUES (p_id_aluno, p_id_professor, p_nome, p_foco);
    
    SET p_id_treino = LAST_INSERT_ID();
    
    COMMIT;
END//
DELIMITER ;

-- Procedure: Relatório de alunos por período
DELIMITER //
CREATE PROCEDURE sp_relatorio_alunos_periodo(
    IN p_data_inicio DATE,
    IN p_data_fim DATE
)
BEGIN
    SELECT 
        a.id,
        a.nome,
        a.sexo,
        a.data_cadastro,
        a.telefone,
        a.email,
        COUNT(DISTINCT t.id) as total_treinos,
        COUNT(DISTINCT av.id) as total_avaliacoes
    FROM aluno a
    LEFT JOIN treino t ON a.id = t.id_aluno
    LEFT JOIN avaliacao_fisica av ON a.id = av.id_aluno
    WHERE a.data_cadastro BETWEEN p_data_inicio AND p_data_fim
    GROUP BY a.id, a.nome, a.sexo, a.data_cadastro, a.telefone, a.email
    ORDER BY a.data_cadastro DESC;
END//
DELIMITER ;

-- Procedure: Estatísticas do professor
DELIMITER //
CREATE PROCEDURE sp_estatisticas_professor(
    IN p_id_professor INT
)
BEGIN
    SELECT 
        p.nome as professor,
        p.cref,
        COUNT(DISTINCT t.id_aluno) as total_alunos,
        COUNT(DISTINCT t.id) as total_treinos,
        COUNT(DISTINCT av.id) as total_avaliacoes
    FROM professor p
    LEFT JOIN treino t ON p.id = t.id_professor
    LEFT JOIN avaliacao_fisica av ON p.id = av.id_professor
    WHERE p.id = p_id_professor
    GROUP BY p.nome, p.cref;
END//
DELIMITER ;

-- Procedure: Transferir treinos de um professor para outro
DELIMITER //
CREATE PROCEDURE sp_transferir_treinos(
    IN p_id_professor_origem INT,
    IN p_id_professor_destino INT,
    OUT p_total_transferido INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_total_transferido = -1;
    END;
    
    START TRANSACTION;
    
    -- Verificar se ambos professores existem
    IF NOT EXISTS (SELECT 1 FROM professor WHERE id = p_id_professor_origem) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Professor de origem não encontrado';
    END IF;
    
    IF NOT EXISTS (SELECT 1 FROM professor WHERE id = p_id_professor_destino) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Professor de destino não encontrado';
    END IF;
    
    -- Contar treinos a transferir
    SELECT COUNT(*) INTO p_total_transferido
    FROM treino
    WHERE id_professor = p_id_professor_origem;
    
    -- Transferir treinos
    UPDATE treino
    SET id_professor = p_id_professor_destino
    WHERE id_professor = p_id_professor_origem;
    
    COMMIT;
END//
DELIMITER ;

-- =============================================
-- FUNCTIONS
-- =============================================

-- Function: Calcular idade do aluno
DELIMITER //
CREATE FUNCTION fn_calcular_idade(p_data_nascimento DATE)
RETURNS INT
DETERMINISTIC
BEGIN
    RETURN TIMESTAMPDIFF(YEAR, p_data_nascimento, CURDATE());
END//
DELIMITER ;

-- Function: Classificar IMC
DELIMITER //
CREATE FUNCTION fn_classificar_imc(p_imc DECIMAL(5,2))
RETURNS VARCHAR(30)
DETERMINISTIC
BEGIN
    DECLARE classificacao VARCHAR(30);
    
    IF p_imc < 18.5 THEN
        SET classificacao = 'Abaixo do peso';
    ELSEIF p_imc < 25 THEN
        SET classificacao = 'Peso normal';
    ELSEIF p_imc < 30 THEN
        SET classificacao = 'Sobrepeso';
    ELSEIF p_imc < 35 THEN
        SET classificacao = 'Obesidade Grau I';
    ELSEIF p_imc < 40 THEN
        SET classificacao = 'Obesidade Grau II';
    ELSE
        SET classificacao = 'Obesidade Grau III';
    END IF;
    
    RETURN classificacao;
END//
DELIMITER ;

-- Function: Contar treinos do aluno
DELIMITER //
CREATE FUNCTION fn_total_treinos_aluno(p_id_aluno INT)
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE total INT;
    
    SELECT COUNT(*) INTO total
    FROM treino
    WHERE id_aluno = p_id_aluno;
    
    RETURN total;
END//
DELIMITER ;

-- =============================================
-- VIEWS
-- =============================================

-- View: Alunos com informações completas
CREATE OR REPLACE VIEW vw_alunos_completo AS
SELECT 
    a.id,
    a.nome,
    a.data_nascimento,
    fn_calcular_idade(a.data_nascimento) as idade,
    a.sexo,
    a.telefone,
    a.email,
    a.data_cadastro,
    COUNT(DISTINCT t.id) as total_treinos,
    COUNT(DISTINCT av.id) as total_avaliacoes
FROM aluno a
LEFT JOIN treino t ON a.id = t.id_aluno
LEFT JOIN avaliacao_fisica av ON a.id = av.id_aluno
GROUP BY a.id, a.nome, a.data_nascimento, a.sexo, a.telefone, a.email, a.data_cadastro;

-- View: Últimas avaliações físicas com classificação IMC
CREATE OR REPLACE VIEW vw_avaliacoes_recentes AS
SELECT 
    av.id,
    a.nome as aluno,
    p.nome as professor,
    av.data_avaliacao,
    av.altura,
    av.peso,
    av.imc,
    fn_classificar_imc(av.imc) as classificacao_imc,
    av.percentual_gordura
FROM avaliacao_fisica av
INNER JOIN aluno a ON av.id_aluno = a.id
INNER JOIN professor p ON av.id_professor = p.id
ORDER BY av.data_avaliacao DESC;

-- View: Treinos com detalhes
CREATE OR REPLACE VIEW vw_treinos_detalhados AS
SELECT 
    t.id,
    t.nome as treino,
    a.nome as aluno,
    p.nome as professor,
    p.cref,
    t.foco_treino,
    COUNT(it.id) as total_exercicios
FROM treino t
INNER JOIN aluno a ON t.id_aluno = a.id
INNER JOIN professor p ON t.id_professor = p.id
LEFT JOIN item_treino it ON t.id = it.id_treino
GROUP BY t.id, t.nome, a.nome, p.nome, p.cref, t.foco_treino;

-- View: Professores com estatísticas
CREATE OR REPLACE VIEW vw_professores_estatisticas AS
SELECT 
    p.id,
    p.nome,
    p.cref,
    p.sexo,
    p.telefone,
    p.email,
    COUNT(DISTINCT t.id) as total_treinos,
    COUNT(DISTINCT t.id_aluno) as total_alunos,
    COUNT(DISTINCT av.id) as total_avaliacoes
FROM professor p
LEFT JOIN treino t ON p.id = t.id_professor
LEFT JOIN avaliacao_fisica av ON p.id = av.id_professor
GROUP BY p.id, p.nome, p.cref, p.sexo, p.telefone, p.email;

-- View: Equipamentos mais utilizados
CREATE OR REPLACE VIEW vw_equipamentos_uso AS
SELECT 
    e.id,
    e.nome,
    e.tipo,
    COUNT(it.id) as vezes_usado
FROM equipamento e
LEFT JOIN item_treino it ON e.id = it.id_equipamento
GROUP BY e.id, e.nome, e.tipo
ORDER BY vezes_usado DESC;

-- =============================================
-- CONSULTAS COMPLEXAS COM FILTROS
-- =============================================

-- Consulta 1: Alunos por faixa etária e sexo
DELIMITER //
CREATE PROCEDURE sp_alunos_por_faixa_etaria(
    IN p_idade_min INT,
    IN p_idade_max INT,
    IN p_sexo CHAR(1)
)
BEGIN
    SELECT 
        id,
        nome,
        data_nascimento,
        fn_calcular_idade(data_nascimento) as idade,
        sexo,
        telefone,
        email
    FROM aluno
    WHERE sexo = p_sexo
        AND fn_calcular_idade(data_nascimento) BETWEEN p_idade_min AND p_idade_max
    ORDER BY nome;
END//
DELIMITER ;

-- Consulta 2: Evolução do IMC de um aluno
DELIMITER //
CREATE PROCEDURE sp_evolucao_imc_aluno(
    IN p_id_aluno INT
)
BEGIN
    SELECT 
        av.data_avaliacao,
        av.peso,
        av.altura,
        av.imc,
        fn_classificar_imc(av.imc) as classificacao,
        av.percentual_gordura,
        p.nome as professor_avaliador
    FROM avaliacao_fisica av
    INNER JOIN professor p ON av.id_professor = p.id
    WHERE av.id_aluno = p_id_aluno
    ORDER BY av.data_avaliacao;
END//
DELIMITER ;

-- Consulta 3: Treinos por foco e professor
DELIMITER //
CREATE PROCEDURE sp_treinos_por_foco(
    IN p_foco VARCHAR(100),
    IN p_id_professor INT
)
BEGIN
    SELECT 
        t.id,
        t.nome as treino,
        a.nome as aluno,
        t.foco_treino,
        COUNT(it.id) as total_exercicios
    FROM treino t
    INNER JOIN aluno a ON t.id_aluno = a.id
    LEFT JOIN item_treino it ON t.id = it.id_treino
    WHERE t.foco_treino LIKE CONCAT('%', p_foco, '%')
        AND (p_id_professor IS NULL OR t.id_professor = p_id_professor)
    GROUP BY t.id, t.nome, a.nome, t.foco_treino
    ORDER BY t.nome;
END//
DELIMITER ;

-- Consulta 4: Busca avançada de alunos
DELIMITER //
CREATE PROCEDURE sp_buscar_alunos(
    IN p_nome VARCHAR(100),
    IN p_sexo CHAR(1),
    IN p_data_cadastro_inicio DATE,
    IN p_data_cadastro_fim DATE
)
BEGIN
    SELECT 
        a.id,
        a.nome,
        a.data_nascimento,
        fn_calcular_idade(a.data_nascimento) as idade,
        a.sexo,
        a.telefone,
        a.email,
        a.data_cadastro,
        COUNT(DISTINCT t.id) as total_treinos
    FROM aluno a
    LEFT JOIN treino t ON a.id = t.id_aluno
    WHERE (p_nome IS NULL OR a.nome LIKE CONCAT('%', p_nome, '%'))
        AND (p_sexo IS NULL OR a.sexo = p_sexo)
        AND (p_data_cadastro_inicio IS NULL OR a.data_cadastro >= p_data_cadastro_inicio)
        AND (p_data_cadastro_fim IS NULL OR a.data_cadastro <= p_data_cadastro_fim)
    GROUP BY a.id, a.nome, a.data_nascimento, a.sexo, a.telefone, a.email, a.data_cadastro
    ORDER BY a.nome;
END//
DELIMITER ;

-- =============================================
-- ÍNDICES PARA OTIMIZAÇÃO
-- =============================================

CREATE INDEX idx_aluno_nome ON aluno(nome);
CREATE INDEX idx_aluno_data_cadastro ON aluno(data_cadastro);
CREATE INDEX idx_professor_cref ON professor(cref);
CREATE INDEX idx_treino_aluno ON treino(id_aluno);
CREATE INDEX idx_treino_professor ON treino(id_professor);
CREATE INDEX idx_avaliacao_aluno ON avaliacao_fisica(id_aluno);
CREATE INDEX idx_avaliacao_data ON avaliacao_fisica(data_avaliacao);

-- =============================================
-- PROCEDURES PARA LOG E AUDITORIA
-- =============================================

-- Procedure: Ver log de auditoria completo
DELIMITER //
CREATE PROCEDURE sp_ver_log_auditoria(
    IN p_limite INT,
    IN p_tabela VARCHAR(50)
)
BEGIN
    IF p_tabela IS NULL OR p_tabela = '' THEN
        -- Mostrar todas as tabelas
        SELECT 
            id,
            tabela,
            operacao,
            id_registro,
            usuario,
            DATE_FORMAT(data_hora, '%d/%m/%Y %H:%i:%s') as data_hora_formatada,
            detalhes
        FROM log_auditoria
        ORDER BY data_hora DESC
        LIMIT p_limite;
    ELSE
        -- Filtrar por tabela específica
        SELECT 
            id,
            tabela,
            operacao,
            id_registro,
            usuario,
            DATE_FORMAT(data_hora, '%d/%m/%Y %H:%i:%s') as data_hora_formatada,
            detalhes
        FROM log_auditoria
        WHERE tabela = p_tabela
        ORDER BY data_hora DESC
        LIMIT p_limite;
    END IF;
END//
DELIMITER ;

-- Procedure: Ver histórico de mudanças de um registro específico
DELIMITER //
CREATE PROCEDURE sp_ver_historico_mudancas(
    IN p_tabela VARCHAR(50),
    IN p_id_registro INT
)
BEGIN
    SELECT 
        id,
        tabela,
        id_registro,
        campo_alterado,
        valor_antigo,
        valor_novo,
        DATE_FORMAT(data_mudanca, '%d/%m/%Y %H:%i:%s') as data_mudanca_formatada
    FROM historico_mudancas
    WHERE tabela = p_tabela 
        AND id_registro = p_id_registro
    ORDER BY data_mudanca DESC;
END//
DELIMITER ;

-- Procedure: Estatísticas do log de auditoria
DELIMITER //
CREATE PROCEDURE sp_estatisticas_auditoria()
BEGIN
    SELECT 
        tabela,
        operacao,
        COUNT(*) as total_operacoes,
        MIN(data_hora) as primeira_operacao,
        MAX(data_hora) as ultima_operacao
    FROM log_auditoria
    GROUP BY tabela, operacao
    ORDER BY tabela, operacao;
END//
DELIMITER ;

-- Procedure: Limpar logs antigos
DELIMITER //
CREATE PROCEDURE sp_limpar_logs_antigos(
    IN p_meses INT
)
BEGIN
    DECLARE linhas_deletadas INT;
    
    START TRANSACTION;
    
    -- Limpar log_auditoria
    DELETE FROM log_auditoria
    WHERE data_hora < DATE_SUB(NOW(), INTERVAL p_meses MONTH);
    
    SET linhas_deletadas = ROW_COUNT();
    
    -- Limpar historico_mudancas
    DELETE FROM historico_mudancas
    WHERE data_mudanca < DATE_SUB(NOW(), INTERVAL p_meses MONTH);
    
    COMMIT;
    
    SELECT 
        CONCAT('Logs removidos: ', linhas_deletadas, ' registros') as resultado;
END//
DELIMITER ;

-- =============================================
-- PROCEDURE PARA REGISTRAR AVALIAÇÃO FÍSICA COM IMC AUTOMÁTICO
-- =============================================

-- Procedure: Registrar avaliação física (IMC calculado automaticamente)
DELIMITER //
CREATE PROCEDURE sp_registrar_avaliacao_fisica(
    IN p_id_aluno INT,
    IN p_id_professor INT,
    IN p_data_avaliacao DATE,
    IN p_altura DECIMAL(5,2),
    IN p_peso DECIMAL(5,2),
    IN p_percentual_gordura DECIMAL(5,2),
    OUT p_id_avaliacao INT,
    OUT p_imc_calculado DECIMAL(5,2),
    OUT p_classificacao VARCHAR(30)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_id_avaliacao = -1;
        SET p_imc_calculado = 0;
        SET p_classificacao = 'ERRO';
    END;
    
    START TRANSACTION;
    
    -- Validar se aluno existe
    IF NOT EXISTS (SELECT 1 FROM aluno WHERE id = p_id_aluno) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Aluno não encontrado';
    END IF;
    
    -- Validar se professor existe
    IF NOT EXISTS (SELECT 1 FROM professor WHERE id = p_id_professor) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Professor não encontrado';
    END IF;
    
    -- Calcular IMC
    SET p_imc_calculado = p_peso / (p_altura * p_altura);
    
    -- Inserir avaliação (o trigger já calcula e valida o IMC)
    INSERT INTO avaliacao_fisica (
        id_aluno, 
        id_professor, 
        data_avaliacao, 
        altura, 
        peso, 
        percentual_gordura,
        imc
    ) VALUES (
        p_id_aluno,
        p_id_professor,
        p_data_avaliacao,
        p_altura,
        p_peso,
        p_percentual_gordura,
        p_imc_calculado
    );
    
    SET p_id_avaliacao = LAST_INSERT_ID();
    
    -- Obter classificação do IMC
    SET p_classificacao = fn_classificar_imc(p_imc_calculado);
    
    COMMIT;
    
    -- Retornar resumo
    SELECT 
        p_id_avaliacao as id_avaliacao,
        p_imc_calculado as imc,
        p_classificacao as classificacao,
        'Avaliação registrada com sucesso!' as mensagem;
END//
DELIMITER ;

-- Procedure: Atualizar avaliação física (recalcula IMC automaticamente)
DELIMITER //
CREATE PROCEDURE sp_atualizar_avaliacao_fisica(
    IN p_id_avaliacao INT,
    IN p_altura DECIMAL(5,2),
    IN p_peso DECIMAL(5,2),
    IN p_percentual_gordura DECIMAL(5,2),
    OUT p_imc_novo DECIMAL(5,2),
    OUT p_classificacao VARCHAR(30)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_imc_novo = 0;
        SET p_classificacao = 'ERRO';
    END;
    
    START TRANSACTION;
    
    -- Verificar se avaliação existe
    IF NOT EXISTS (SELECT 1 FROM avaliacao_fisica WHERE id = p_id_avaliacao) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Avaliação física não encontrada';
    END IF;
    
    -- Calcular novo IMC
    SET p_imc_novo = p_peso / (p_altura * p_altura);
    
    -- Atualizar (o trigger recalcula o IMC automaticamente)
    UPDATE avaliacao_fisica
    SET altura = p_altura,
        peso = p_peso,
        percentual_gordura = p_percentual_gordura,
        imc = p_imc_novo
    WHERE id = p_id_avaliacao;
    
    -- Obter classificação
    SET p_classificacao = fn_classificar_imc(p_imc_novo);
    
    COMMIT;
    
    -- Retornar resumo
    SELECT 
        p_id_avaliacao as id_avaliacao,
        p_imc_novo as novo_imc,
        p_classificacao as classificacao,
        'Avaliação atualizada com sucesso!' as mensagem;
END//
DELIMITER ;

-- Procedure: Relatório completo de avaliação física de um aluno
DELIMITER //
CREATE PROCEDURE sp_relatorio_avaliacoes_aluno(
    IN p_id_aluno INT
)
BEGIN
    SELECT 
        av.id,
        a.nome as aluno,
        p.nome as professor_avaliador,
        av.data_avaliacao,
        av.altura,
        av.peso,
        av.imc,
        fn_classificar_imc(av.imc) as classificacao_imc,
        av.percentual_gordura,
        -- Calcular variação de peso desde a última avaliação
        (av.peso - LAG(av.peso) OVER (ORDER BY av.data_avaliacao)) as variacao_peso,
        -- Calcular variação de IMC
        (av.imc - LAG(av.imc) OVER (ORDER BY av.data_avaliacao)) as variacao_imc
    FROM avaliacao_fisica av
    INNER JOIN aluno a ON av.id_aluno = a.id
    INNER JOIN professor p ON av.id_professor = p.id
    WHERE av.id_aluno = p_id_aluno
    ORDER BY av.data_avaliacao DESC;
END//
DELIMITER ;

-- =============================================
-- FIM DO SCRIPT
-- =============================================
