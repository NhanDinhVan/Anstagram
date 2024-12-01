-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.4.32-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             12.7.0.6850
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for desktopappdb
CREATE DATABASE IF NOT EXISTS `desktopappdb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `desktopappdb`;

-- Dumping structure for table desktopappdb.comments
CREATE TABLE IF NOT EXISTS `comments` (
  `commentID` int(11) NOT NULL AUTO_INCREMENT,
  `postID` int(11) DEFAULT NULL,
  `userID` int(11) DEFAULT NULL,
  `comment_text` text DEFAULT NULL,
  `comment_time` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`commentID`),
  KEY `postID` (`postID`),
  KEY `userID` (`userID`),
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`postID`) REFERENCES `posts` (`post_id`),
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table desktopappdb.comments: ~0 rows (approximately)

-- Dumping structure for table desktopappdb.follows
CREATE TABLE IF NOT EXISTS `follows` (
  `follow_id` int(11) NOT NULL AUTO_INCREMENT,
  `followerID` int(11) DEFAULT NULL,
  `followedID` int(11) DEFAULT NULL,
  `follow_time` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`follow_id`),
  KEY `followerID` (`followerID`),
  KEY `followedID` (`followedID`),
  CONSTRAINT `follows_ibfk_1` FOREIGN KEY (`followerID`) REFERENCES `users` (`userID`),
  CONSTRAINT `follows_ibfk_2` FOREIGN KEY (`followedID`) REFERENCES `users` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table desktopappdb.follows: ~2 rows (approximately)
INSERT IGNORE INTO `follows` (`follow_id`, `followerID`, `followedID`, `follow_time`) VALUES
	(1, 3, 4, '2024-11-25 10:21:50'),
	(2, 5, 3, '2024-11-25 10:32:09');

-- Dumping structure for table desktopappdb.likes
CREATE TABLE IF NOT EXISTS `likes` (
  `likeID` int(11) NOT NULL AUTO_INCREMENT,
  `postID` int(11) DEFAULT NULL,
  `userID` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`likeID`),
  KEY `postID` (`postID`),
  KEY `userID` (`userID`),
  CONSTRAINT `likes_ibfk_1` FOREIGN KEY (`postID`) REFERENCES `posts` (`post_id`),
  CONSTRAINT `likes_ibfk_2` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table desktopappdb.likes: ~0 rows (approximately)

-- Dumping structure for table desktopappdb.messages
CREATE TABLE IF NOT EXISTS `messages` (
  `message_id` int(11) NOT NULL AUTO_INCREMENT,
  `senderID` int(11) DEFAULT NULL,
  `receiverID` int(11) DEFAULT NULL,
  `message_text` text DEFAULT NULL,
  `send_time` datetime DEFAULT current_timestamp(),
  `messageType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`message_id`),
  KEY `senderID` (`senderID`),
  KEY `receiverID` (`receiverID`),
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`senderID`) REFERENCES `users` (`userID`),
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`receiverID`) REFERENCES `users` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table desktopappdb.messages: ~15 rows (approximately)
INSERT IGNORE INTO `messages` (`message_id`, `senderID`, `receiverID`, `message_text`, `send_time`, `messageType`) VALUES
	(10, 3, 5, 'Hello ...', '2024-11-28 14:15:34', 'message_text'),
	(11, 5, 3, 'Nice to meet you ', '2024-11-28 14:17:34', 'message_text'),
	(12, 3, 4, 'Hi guy', '2024-11-28 14:17:44', 'message_text'),
	(13, 4, 3, 'What up', '2024-11-28 14:19:29', 'message_text'),
	(14, 4, 3, 'xin chào Lộc', '2024-11-28 14:28:39', 'message_text'),
	(15, 4, 3, 'BaoCaoDoAn4.docx', '2024-11-28 14:28:55', 'file'),
	(16, 3, 4, 'hi ', '2024-11-28 14:29:13', 'message_text'),
	(17, 3, 4, 'đã download file', '2024-11-28 14:29:49', 'message_text'),
	(18, 4, 3, 'oke\n', '2024-11-28 14:29:55', 'message_text'),
	(19, 3, 4, 'bye', '2024-11-28 14:30:03', 'message_text'),
	(20, 4, 3, 'bye Ngọc', '2024-11-28 14:30:10', 'message_text'),
	(21, 4, 3, 'bye Lộc', '2024-11-28 14:30:30', 'message_text'),
	(22, 3, 4, 'bye Ngọc', '2024-11-28 14:30:36', 'message_text'),
	(23, 3, 4, 'see u again', '2024-11-28 14:30:53', 'message_text'),
	(24, 4, 3, 'oke\n', '2024-11-28 14:31:03', 'message_text');

-- Dumping structure for table desktopappdb.posts
CREATE TABLE IF NOT EXISTS `posts` (
  `post_id` int(11) NOT NULL AUTO_INCREMENT,
  `userID` int(11) DEFAULT NULL,
  `image_url` varchar(255) NOT NULL,
  `caption` text DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`post_id`),
  KEY `userID` (`userID`),
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `users` (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table desktopappdb.posts: ~0 rows (approximately)

-- Dumping structure for table desktopappdb.users
CREATE TABLE IF NOT EXISTS `users` (
  `userID` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `profile_picture` varchar(255) DEFAULT NULL,
  `bio` text DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userID`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table desktopappdb.users: ~6 rows (approximately)
INSERT IGNORE INTO `users` (`userID`, `username`, `password`, `email`, `profile_picture`, `bio`, `created_at`, `name`) VALUES
	(3, 'vietloc209', '$2a$10$cMfEIgPV2m6YEiCmpEZfKeh1p/tJ1CUcmUPyu0cFqdYO/HFRX9dwa', 'OiaLtEeIoLVgGfpA28CIku75n4sn7FSpRi2Rhs5f9/Q=', 'photo-1560674457-12073ed6fae6.jpg', 'dinhvietloc209', '2024-11-25 09:00:52', 'Đinh Viết Lộc'),
	(4, 'Tungoc.07', '$2a$10$tediJkX2sfghIpSLJWgwgejBwFM7XVraPO9Nq5Mb48t9QbtskXbfi', 'TSIatMWCOS2oZa3+8B+17YmF9zDnWT/P91Rwn9XSanQ=', 'premium_photo-1664474619075-644dd191935f.jpg', '07072004', '2024-11-25 10:05:15', 'Tú Ngọc'),
	(5, 'vannhan2409@gmail.com', 'vannhan', 'vannhan2409@gmail.com', NULL, NULL, '2024-11-29 05:35:34', 'Nhan Dinh'),
	(6, 'Loremxe', '$2a$10$H5qnGzMCn8JznPlPrilA7O1N5Zrv5lAEXL9rsdjwsI0IIQLgggL66', '72LbAM+7f8898uuKzDm5Lza/tNCEo06rjQ5Dc0kETh8=', '6e70cf15912f7d4da213cddc6aedccad.jpg', 'hagiang2012', NULL, 'LoremAdr'),
	(8, 'hagiang1909', '$2a$10$JKmN9gE125Jh/7Q32b9yBew2piGr3clBkf5BcYbQ0puspWWj6TD4e', 'A/Fxj7x1xQLwddfjZI9sa4l6VEsSeAfL/ZI1BCti40o=', '07c4707e01672fd4980dde5f60b2754b.jpg', '19092012', '2024-11-25 10:31:03', 'Nguyễn Hà Giang'),
	(9, 'vannhan10102004@gmail.com', 'vannhan', '', NULL, NULL, '2024-11-30 22:08:50', 'Dinh Nhan');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
