-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Erstellungszeit: 21. Okt 2015 um 13:38
-- Server Version: 5.6.21
-- PHP-Version: 5.6.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Datenbank: `esa`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `Artikel`
--

DROP TABLE IF EXISTS `Artikel`;
CREATE TABLE IF NOT EXISTS `Artikel` (
  `Id` varbinary(24) NOT NULL,
  `ArtikelID` varchar(255) COLLATE utf8_bin NOT NULL,
  `Datum` date NOT NULL,
  `Titel` varchar(255) COLLATE utf8_bin NOT NULL,
  `Text` text COLLATE utf8_bin NOT NULL,
  `TaggedText` text COLLATE utf8_bin NOT NULL,
  `Wikipedia_OnlyPerson` text COLLATE utf8_bin NOT NULL,
  `Wikipedia_NoPerson` text COLLATE utf8_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `Artikel`
--
ALTER TABLE `Artikel`
 ADD UNIQUE KEY `Id` (`Id`), ADD UNIQUE KEY `artikelID` (`ArtikelID`), ADD KEY `Datum` (`Datum`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
