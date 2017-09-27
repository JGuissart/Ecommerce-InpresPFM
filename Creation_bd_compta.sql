-- MySQL dump 10.13  Distrib 5.6.24, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: bd_compta
-- ------------------------------------------------------
-- Server version	5.6.17

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `personnel`
--

DROP TABLE IF EXISTS `personnel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `personnel` (
  `idPersonnel` int(11) NOT NULL AUTO_INCREMENT,
  `Nom` varchar(25) NOT NULL,
  `Prenom` varchar(25) NOT NULL,
  `Login` varchar(20) NOT NULL,
  `Password` varchar(20) NOT NULL,
  `MailInterne` varchar(45) DEFAULT NULL,
  `TelInterne` varchar(10) DEFAULT NULL,
  `MailPrive` varchar(45) DEFAULT NULL,
  `TelPrive` varchar(10) DEFAULT NULL,
  `Affectation` varchar(20) DEFAULT NULL,
  `Fonction` varchar(30) DEFAULT NULL,
  `NiveauBaremique` varchar(45) DEFAULT NULL,
  `DateEntreeSociete` varchar(45) DEFAULT NULL,
  `NumCompteBancaire` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idPersonnel`),
  UNIQUE KEY `Login_UNIQUE` (`Login`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `personnel`
--

LOCK TABLES `personnel` WRITE;
/*!40000 ALTER TABLE `personnel` DISABLE KEYS */;
INSERT INTO `personnel` VALUES (1,'GUISSART','Julien','guissaju','azerty123',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'DESART','Adrien','desartad','qwerty123',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'CHARLET','Christophe','charlech','routeur123',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,'MERCENIER','Denys','MerceD','unix123',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,'VILVENS','Claude','vilvencl','crypto123',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,'WAGNER','Jean-Marc','wagnerje','reseaux123',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `personnel` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-10-22 17:06:50
