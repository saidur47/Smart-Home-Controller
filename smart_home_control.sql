-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Apr 09, 2017 at 11:02 PM
-- Server version: 10.1.16-MariaDB
-- PHP Version: 5.6.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `smart_home_control`
--
CREATE DATABASE IF NOT EXISTS `smart_home_control` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE `smart_home_control`;

-- --------------------------------------------------------

--
-- Table structure for table `client_credential`
--

DROP TABLE IF EXISTS `client_credential`;
CREATE TABLE IF NOT EXISTS `client_credential` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL,
  `username` varchar(45) COLLATE utf8_bin NOT NULL,
  `password` text COLLATE utf8_bin NOT NULL,
  `email` varchar(100) COLLATE utf8_bin NOT NULL,
  `cgid` int(11) NOT NULL,
  `usertype` tinyint(2) NOT NULL DEFAULT '0',
  `activation_status` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `userid_UNIQUE` (`userid`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `client_credential`
--

INSERT INTO `client_credential` (`id`, `userid`, `username`, `password`, `email`, `cgid`, `usertype`, `activation_status`) VALUES
(1, 2001, 'test2', '$2y$10$F/QsTQedVJ93UTkvGPLGmuNwLLGh05eKN/tRuccmqNqdbtaXA6X/K', 'test2@test.com', 2, 0, 0),
(2, 2002, 'test', '$2y$10$oeEQqxN6QphGz13SbcXyDOIIcSYn7Oani03gEnEmBjW3IoZ4OGOUe', 'test@test.cim', 2, 0, 0),
(4, 2004, 'saif', '$2y$10$wq4A9/qJERWxSRn.t2oSgeolFuKYWgZ2XWA/QODprfmI01wuKfYjW', 'saif@rahman.com', 2, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `client_details`
--

DROP TABLE IF EXISTS `client_details`;
CREATE TABLE IF NOT EXISTS `client_details` (
  `user_id` int(11) DEFAULT NULL,
  `name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `address` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  UNIQUE KEY `user_id_UNIQUE` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `client_details`
--

INSERT INTO `client_details` (`user_id`, `name`, `address`) VALUES
(2001, 'Test Name', 'NO address'),
(2002, 'Second Son', 'Of Second Son'),
(2004, 'saifur', '');

-- --------------------------------------------------------

--
-- Table structure for table `client_group`
--

DROP TABLE IF EXISTS `client_group`;
CREATE TABLE IF NOT EXISTS `client_group` (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `cgid` int(10) UNSIGNED NOT NULL,
  `owner_id` int(11) DEFAULT NULL,
  `activation_status` tinyint(3) DEFAULT '0',
  `activation` varchar(10) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `cgid_UNIQUE` (`cgid`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `client_group`
--

INSERT INTO `client_group` (`id`, `cgid`, `owner_id`, `activation_status`, `activation`) VALUES
(1, 1, 1001, 0, '11001'),
(2, 2, 2001, 0, '22001'),
(3, 3, 3001, 0, '33001');

-- --------------------------------------------------------

--
-- Table structure for table `device_change_log`
--

DROP TABLE IF EXISTS `device_change_log`;
CREATE TABLE IF NOT EXISTS `device_change_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cgid` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `house_id` int(11) NOT NULL,
  `room_id` int(11) NOT NULL,
  `device_id` int(11) NOT NULL,
  `status` tinyint(4) NOT NULL,
  `log_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `device_change_log`
--

INSERT INTO `device_change_log` (`id`, `cgid`, `user_id`, `house_id`, `room_id`, `device_id`, `status`, `log_time`) VALUES
(1, 2, 2001, 2001, 2001, 1, 0, '2017-04-09 23:12:52'),
(2, 2, 2001, 2001, 2001, 1, 1, '2017-04-10 01:07:39'),
(3, 2, 2001, 2001, 2001, 2, 1, '2017-04-10 01:54:47'),
(4, 2, 2001, 2001, 2001, 1, 1, '2017-04-10 02:09:30'),
(5, 2, 2001, 2001, 2001, 1, 1, '2017-04-10 02:09:51'),
(6, 2, 2001, 2001, 2001, 2, 0, '2017-04-10 02:09:51'),
(7, 2, 2001, 2001, 2001, 3, 1, '2017-04-10 02:09:51'),
(8, 2, 2001, 2001, 2001, 4, 1, '2017-04-10 02:09:51'),
(9, 2, 2001, 2001, 2001, 1, 0, '2017-04-10 02:10:00'),
(10, 2, 2001, 2001, 2001, 2, 0, '2017-04-10 02:10:00'),
(11, 2, 2001, 2001, 2001, 3, 1, '2017-04-10 02:10:00'),
(12, 2, 2001, 2001, 2001, 4, 1, '2017-04-10 02:10:00'),
(13, 2, 2001, 2001, 2001, 1, 1, '2017-04-10 02:10:08'),
(14, 2, 2001, 2001, 2001, 2, 0, '2017-04-10 02:10:08'),
(15, 2, 2001, 2001, 2001, 3, 1, '2017-04-10 02:10:08'),
(16, 2, 2001, 2001, 2001, 4, 1, '2017-04-10 02:10:08'),
(17, 2, 2001, 2001, 2001, 1, 1, '2017-04-10 02:10:10'),
(18, 2, 2001, 2001, 2001, 2, 1, '2017-04-10 02:10:10'),
(19, 2, 2001, 2001, 2001, 3, 1, '2017-04-10 02:10:10'),
(20, 2, 2001, 2001, 2001, 4, 1, '2017-04-10 02:10:10'),
(21, 2, 2001, 2001, 2001, 1, 0, '2017-04-10 02:10:11'),
(22, 2, 2001, 2001, 2001, 2, 1, '2017-04-10 02:10:11'),
(23, 2, 2001, 2001, 2001, 3, 1, '2017-04-10 02:10:11'),
(24, 2, 2001, 2001, 2001, 4, 1, '2017-04-10 02:10:11'),
(25, 2, 2001, 2001, 2001, 1, 0, '2017-04-10 02:10:53'),
(26, 2, 2001, 2001, 2001, 2, 0, '2017-04-10 02:10:53'),
(27, 2, 2001, 2001, 2001, 3, 1, '2017-04-10 02:10:53'),
(28, 2, 2001, 2001, 2001, 4, 1, '2017-04-10 02:10:53'),
(29, 2, 2001, 2001, 2001, 1, 0, '2017-04-10 02:11:08'),
(30, 2, 2001, 2001, 2001, 2, 0, '2017-04-10 02:11:08'),
(31, 2, 2001, 2001, 2001, 3, 0, '2017-04-10 02:11:08'),
(32, 2, 2001, 2001, 2001, 4, 1, '2017-04-10 02:11:08'),
(33, 2, 2001, 2001, 2001, 1, 0, '2017-04-10 02:11:08'),
(34, 2, 2001, 2001, 2001, 2, 0, '2017-04-10 02:11:08'),
(35, 2, 2001, 2001, 2001, 3, 0, '2017-04-10 02:11:08'),
(36, 2, 2001, 2001, 2001, 4, 0, '2017-04-10 02:11:08'),
(37, 2, 2002, 2001, 2001, 1, 0, '2017-04-10 02:11:32'),
(38, 2, 2002, 2001, 2001, 2, 0, '2017-04-10 02:11:32'),
(39, 2, 2002, 2001, 2001, 3, 1, '2017-04-10 02:11:32'),
(40, 2, 2002, 2001, 2001, 4, 0, '2017-04-10 02:11:32'),
(41, 2, 2002, 2001, 2001, 1, 0, '2017-04-10 02:11:33'),
(42, 2, 2002, 2001, 2001, 2, 1, '2017-04-10 02:11:33'),
(43, 2, 2002, 2001, 2001, 3, 1, '2017-04-10 02:11:33'),
(44, 2, 2002, 2001, 2001, 4, 0, '2017-04-10 02:11:33'),
(45, 2, 2004, 2001, 2001, 1, 0, '2017-04-10 02:57:29'),
(46, 2, 2004, 2001, 2001, 2, 0, '2017-04-10 02:57:29'),
(47, 2, 2004, 2001, 2001, 3, 1, '2017-04-10 02:57:29'),
(48, 2, 2004, 2001, 2001, 4, 0, '2017-04-10 02:57:29'),
(49, 2, 2004, 2001, 2001, 1, 0, '2017-04-10 02:57:30'),
(50, 2, 2004, 2001, 2001, 2, 0, '2017-04-10 02:57:30'),
(51, 2, 2004, 2001, 2001, 3, 0, '2017-04-10 02:57:30'),
(52, 2, 2004, 2001, 2001, 4, 0, '2017-04-10 02:57:30'),
(53, 2, 2004, 2001, 2001, 1, 0, '2017-04-10 02:57:31'),
(54, 2, 2004, 2001, 2001, 2, 0, '2017-04-10 02:57:31'),
(55, 2, 2004, 2001, 2001, 3, 0, '2017-04-10 02:57:31'),
(56, 2, 2004, 2001, 2001, 4, 1, '2017-04-10 02:57:31'),
(57, 2, 2004, 2001, 2001, 1, 0, '2017-04-10 02:57:32'),
(58, 2, 2004, 2001, 2001, 2, 1, '2017-04-10 02:57:33'),
(59, 2, 2004, 2001, 2001, 3, 0, '2017-04-10 02:57:33'),
(60, 2, 2004, 2001, 2001, 4, 1, '2017-04-10 02:57:33');

-- --------------------------------------------------------

--
-- Table structure for table `device_list`
--

DROP TABLE IF EXISTS `device_list`;
CREATE TABLE IF NOT EXISTS `device_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `device_id` int(11) DEFAULT NULL,
  `room_id` int(11) DEFAULT NULL,
  `house_id` int(11) DEFAULT NULL,
  `cgid` int(11) DEFAULT NULL,
  `name` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `details` text COLLATE utf8_bin,
  `swtich_state` tinyint(1) NOT NULL DEFAULT '0',
  `lastchangedby` int(11) DEFAULT NULL,
  `lastchangedfrom` tinyint(3) DEFAULT NULL,
  `device_type` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `device_list`
--

INSERT INTO `device_list` (`id`, `device_id`, `room_id`, `house_id`, `cgid`, `name`, `details`, `swtich_state`, `lastchangedby`, `lastchangedfrom`, `device_type`) VALUES
(1, 1, 2001, 2001, 2, 'tubeligght', 'Halalujig', 0, 2004, 0, 0),
(2, 2, 2001, 2001, 2, 'Light', 'Flashign dance Light', 1, 2004, 0, 0),
(3, 3, 2001, 2001, 2, 'Fan', 'BATASH', 0, 2004, 0, 0),
(4, 4, 2001, 2001, 2, 'Dim Light', 'green gloomy light', 1, 2004, 0, 0),
(5, 1, 2002, 2001, 2, 'Dim Light', 'green gloomy light', 0, 2001, 0, 0),
(6, 2, 2002, 2001, 2, 'Light Two', 'asdf', 0, 2001, 0, 0),
(7, 3, 2002, 2001, 2, 'Fan', 'asdf', 0, 2001, 0, 0),
(8, 4, 2002, 2001, 2, 'Fridger', 'asdf', 0, 2001, 0, 0),
(9, 1, 2003, 2002, 2, 'Tubelight new', 'Blink Blink Ting', 0, 2001, 0, 0),
(10, 2, 2003, 2002, 2, 'light', 'asdf', 1, 2001, 0, 0),
(11, 3, 2003, 2002, 2, 'Smart Light', 'Ting...', 1, 2001, 0, 0),
(12, 4, 2003, 2002, 2, 'fan', 'asdf', 1, 2001, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `house_list`
--

DROP TABLE IF EXISTS `house_list`;
CREATE TABLE IF NOT EXISTS `house_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `house_id` int(11) NOT NULL,
  `cgid` int(11) NOT NULL,
  `name` varchar(45) COLLATE utf8_bin NOT NULL,
  `details` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `house_id` (`house_id`),
  KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `house_list`
--

INSERT INTO `house_list` (`id`, `house_id`, `cgid`, `name`, `details`) VALUES
(1, 2001, 2, 'My Primary Home', 'The best of the best of the best'),
(2, 2002, 2, 'Another Home', 'Home Sweet Home');

-- --------------------------------------------------------

--
-- Table structure for table `room_list`
--

DROP TABLE IF EXISTS `room_list`;
CREATE TABLE IF NOT EXISTS `room_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `room_id` int(11) NOT NULL,
  `house_id` int(11) NOT NULL,
  `cgid` int(11) NOT NULL,
  `name` varchar(45) COLLATE utf8_bin NOT NULL,
  `details` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `temperature` float DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `room_id` (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `room_list`
--

INSERT INTO `room_list` (`id`, `room_id`, `house_id`, `cgid`, `name`, `details`, `temperature`) VALUES
(1, 2001, 2001, 2, 'FIrst Room', 'My first in second house''s room', 0),
(2, 2002, 2001, 2, 'Second Room', 'Deining Room FOOD', 0),
(3, 2003, 2002, 2, 'Room', 'Room in 2002 house', 0);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
