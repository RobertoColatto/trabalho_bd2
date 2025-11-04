-- STORED PROCEDURES

DELIMITER $$
CREATE PROCEDURE inserir_aluno(
    IN p_nome varchar(255),
    IN p_email varchar(255),
    IN p_peso double,
    IN p_altura double,
    IN p_sexo char(1)
)
BEGIN
    INSERT INTO aluno (nome, email, peso, altura, sexo)
    VALUES (p_nome, p_email, p_peso, p_altura, p_sexo);

    SELECT LAST_INSERT_ID() AS idGerado;
END$$

DELIMITER $$
CREATE PROCEDURE inserir_professor(IN p_nome varchar(255), IN p_sexo char(1))
BEGIN
    INSERT INTO professor (nome, sexo)
    VALUES (p_nome, p_sexo);

    SELECT LAST_INSERT_ID() AS idGerado;
END$$

DELIMITER $$
CREATE PROCEDURE inserir_treino(IN p_nome varchar(255), IN p_musculo varchar(255), IN p_horario time)
BEGIN
    INSERT INTO treino (nome, musculo, horario)
    VALUES (p_nome, p_musculo, p_horario);

    SELECT LAST_INSERT_ID() AS idGerado;
END$$

DELIMITER $$
CREATE PROCEDURE inserir_equipamento(IN p_nome varchar(255))
BEGIN
    INSERT INTO equipamentos (nome)
    VALUES (p_nome);

    SELECT LAST_INSERT_ID() AS idGerado;
END$$