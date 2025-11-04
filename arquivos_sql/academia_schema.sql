/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-12.0.2-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: academia
-- ------------------------------------------------------
-- Server version	12.0.2-MariaDB

CREATE DATABASE IF NOT EXISTS academia;
USE academia;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `aulas_professor`
--

DROP TABLE IF EXISTS `aulas_professor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `aulas_professor` (
  `professor` int(11) NOT NULL,
  `treino` int(11) NOT NULL,
  PRIMARY KEY (`professor`,`treino`),
  KEY `aulas_idx` (`treino`),
  CONSTRAINT `aulas` FOREIGN KEY (`treino`) REFERENCES `treino` (`treinoid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `professor` FOREIGN KEY (`professor`) REFERENCES `professor` (`idprofessor`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cadastro`
--

DROP TABLE IF EXISTS `cadastro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `cadastro` (
  `aluno` int(11) NOT NULL,
  `treino` int(11) NOT NULL,
  PRIMARY KEY (`aluno`,`treino`),
  KEY `treino_idx` (`treino`),
  CONSTRAINT `treino` FOREIGN KEY (`treino`) REFERENCES `treino` (`treinoid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `aluno` FOREIGN KEY (`aluno`) REFERENCES `aluno` (`alunoid`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `equipamentos`
--

DROP TABLE IF EXISTS `equipamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipamentos` (
  `nome` varchar(255) NOT NULL,
  `idequipamentos` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`idequipamentos`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `professor`
--

DROP TABLE IF EXISTS `professor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `professor` (
  `idprofessor` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `sexo` enum('m','f') DEFAULT NULL,
  PRIMARY KEY (`idprofessor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `treino`
--

DROP TABLE IF EXISTS `treino`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `treino` (
  `nome` varchar(255) NOT NULL,
  `musculo` varchar(255) DEFAULT NULL,
  `treinoid` int(11) NOT NULL AUTO_INCREMENT,
  `horario` time NOT NULL,
  PRIMARY KEY (`treinoid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `treino_equipamento`
--

DROP TABLE IF EXISTS `treino_equipamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `treino_equipamento` (
  `treino_relacionamento` int(11) NOT NULL,
  `equipamento_relacionamento` int(11) NOT NULL,
  PRIMARY KEY (`treino_relacionamento`,`equipamento_relacionamento`),
  KEY `fk_treino_equipamento_2_idx` (`equipamento_relacionamento`),
  CONSTRAINT `fk_treino_equipamento_1` FOREIGN KEY (`treino_relacionamento`) REFERENCES `treino` (`treinoid`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_treino_equipamento_2` FOREIGN KEY (`equipamento_relacionamento`) REFERENCES `equipamentos` (`idequipamentos`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `aluno`
--

DROP TABLE IF EXISTS `aluno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `aluno` (
  `alunoid` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `peso` double DEFAULT NULL,
  `altura` double DEFAULT NULL,
  `sexo` enum('m','f') DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`alunoid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2025-11-02 21:32:13