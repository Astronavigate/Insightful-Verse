-- MySQL dump 10.13  Distrib 9.5.0, for Win64 (x86_64)
--
-- Host: localhost    Database: iviep
-- ------------------------------------------------------
-- Server version	9.5.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '7aa32bcf-d329-11f0-ba51-005056c00001:1-611';

--
-- Table structure for table `annotations`
--

DROP TABLE IF EXISTS `annotations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `annotations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `book_id` bigint NOT NULL,
  `type` enum('bookmark','highlight','note') NOT NULL,
  `cfi` varchar(256) NOT NULL,
  `content` varchar(256) DEFAULT NULL,
  `color` varchar(28) DEFAULT '#ffd54f',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_book_type_cfi` (`user_id`,`book_id`,`type`,`cfi`),
  KEY `idx_user_book` (`user_id`,`book_id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `annotations`
--

LOCK TABLES `annotations` WRITE;
/*!40000 ALTER TABLE `annotations` DISABLE KEYS */;
INSERT INTO `annotations` VALUES (1,10000000000,10000000033,'bookmark','epubcfi(/6/6[item2]!/4/2[a002]/1:0)','test1',NULL,'2025-12-16 00:14:24','2025-12-16 00:14:24'),(7,10000000000,10000000033,'bookmark','epubcfi(/6/12[item5]!/4/2[41-14488c5c3e8b491d97c21ee6fb56f687]/2/2/1:0)','shuqian',NULL,'2025-12-16 11:21:32','2025-12-16 11:21:32'),(29,10000000000,10000000033,'bookmark','epubcfi(/6/8[item3]!/4/2[21-14488c5c3e8b491d97c21ee6fb56f687]/1:0)','',NULL,'2025-12-16 21:50:03','2025-12-16 21:50:03'),(32,10000000000,10000000033,'highlight','epubcfi(/6/102[item50]!/4,/6/1:78,/18/1:8)','','rgba(224, 224, 0, 0.4)','2025-12-17 21:55:30','2025-12-17 21:55:30'),(38,10000000000,10000000033,'bookmark','epubcfi(/6/100[item49]!/4/2[a049]/1:0)','书签123',NULL,'2025-12-20 21:29:56','2025-12-20 21:29:56'),(40,10000000000,10000000033,'highlight','epubcfi(/6/100[item49]!/4,/4/1:16,/6/1:49)','','rgba(224, 224, 0, 0.4)','2025-12-20 21:30:13','2025-12-20 21:30:13');
/*!40000 ALTER TABLE `annotations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class`
--

DROP TABLE IF EXISTS `class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class` (
  `class_id` bigint NOT NULL AUTO_INCREMENT,
  `class_name` varchar(100) NOT NULL,
  PRIMARY KEY (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000000000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class`
--

LOCK TABLES `class` WRITE;
/*!40000 ALTER TABLE `class` DISABLE KEYS */;
/*!40000 ALTER TABLE `class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `course_id` bigint NOT NULL AUTO_INCREMENT,
  `course_name` varchar(100) NOT NULL,
  `course_info` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000000007 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES (10000000000,'OOSA&D','Object-Oriented System Analysis and Design.'),(10000000001,'Computer Network','Computer networking refers to connected computing devices and an ever-expanding array of IoT devices that communicate with one another.'),(10000000002,'Music','Listen, just feel your heart!'),(10000000003,'Genshin Impact','Step Into a Vast Magical World of Adventure'),(10000000004,'English',NULL),(10000000006,'Temp Recourse','');
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file`
--

DROP TABLE IF EXISTS `file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file` (
  `file_id` bigint NOT NULL AUTO_INCREMENT,
  `file_name` varchar(100) NOT NULL,
  `type` varchar(20) NOT NULL,
  `course_id` bigint DEFAULT NULL,
  `remarks` varchar(999) DEFAULT NULL,
  `upload_user` bigint DEFAULT NULL,
  `upload_date` date DEFAULT NULL,
  `file_path` varchar(255) NOT NULL,
  PRIMARY KEY (`file_id`),
  UNIQUE KEY `file_path` (`file_path`),
  KEY `course_id` (`course_id`),
  KEY `upload_user` (`upload_user`),
  CONSTRAINT `file_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`),
  CONSTRAINT `file_ibfk_2` FOREIGN KEY (`upload_user`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000000042 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file`
--

LOCK TABLES `file` WRITE;
/*!40000 ALTER TABLE `file` DISABLE KEYS */;
INSERT INTO `file` VALUES (10000000000,'Ferryman','pdf',10000000004,'Life, death, love - which would you choose?',10000000001,'2024-07-18','/doc/Ferryman.pdf'),(10000000001,'The Development and Prospects of Passive Optical Networks (Chinese)','mp4',10000000001,'PON, developed in the mid-1990s, was oniginally designed to alow Intemet Serice Providers (ISPs) to deliver broadband triple-play senices (data,voice, and video) to residential\nusers.',10000000001,'2024-07-18','/media/PON.mp4'),(10000000002,'RFC 9114','pdf',10000000001,'After5 years, HTTP 3 was finaly standardized as RFC 9114. A new chapter in the web will be opened with RFC 9204 (QPACK header compression) and RFC 9218 (Extensible\r Prioritization)!',10000000001,'2024-07-18','/doc/rfc9114.pdf'),(10000000003,'Adapter Java Example','java',10000000000,'The user has purchased a new three-phase socket and wants to use the newly purchased three-phase socket to use both three-phase and two-phase appliances.',10000000001,'2024-07-18','/code/AdapterExample.java'),(10000000004,'Command Java Example','java',10000000000,'The customer asked the waiter to order Mutton shashlik or chicken, and the chef was responsible for the barbecue.',10000000001,'2024-07-18','/code/CommandExample.java'),(10000000005,'Singleton Java Example','java',10000000000,'The print pool is an application that manages print tasks, allowing a print pool user to delete, abort, or change the priority of the print tasks, only one print pool object can run in a system.',10000000001,'2024-07-18','/code/SingletonExample.java'),(10000000006,'Character Demo - Cyno','mp4',10000000003,'Counsel of Condemnation | Genshin Impact',10000000001,'2024-07-18','/media/Character Demo - Cyno Counsel of Condemnation Genshin Impact.mp4'),(10000000007,'Character Teaser - Cyno','mp4',10000000003,'A Just Punishment | Genshin Impact',10000000001,'2024-07-18','/media/Character Teaser - Cyno A Just Punishment Genshin Impact.mp4'),(10000000008,'最后时刻 - Li Jian','m4a',10000000002,'On May 12, 2008, the Wenchuan earthquake. As a singer, the only thing I can do at this moment is to use music to express my care. Li Jian created such a song in the shortest possible time, but it has become the most warm and restrained work among all disaster relief songs. The small love between lovers and relatives replaces the big love in the mainstream voice, and expresses the sadness and love hidden deep in the heart with warm melodies and lyrics.',10000000001,'2024-07-18','/media/01 最后时刻.m4a'),(10000000009,'Character Picture - Cyno','png',10000000003,'Genshin Impact',10000000001,'2024-07-18','/image/4d708230-877f-42c0-8cee-fde3304f5278.png'),(10000000010,'VORTEX - 白鲨JAWS','mp3',10000000002,'The song \"VORTEX\" is from the album titled \"Link Click Season 2 Original Soundtrack\" released in 2023. It is produced by Bilibili and written by Michael Yu, who is also the lyricist. The song falls under the genres of rock, TV soundtrack, and theme song. The relatable lyrics and powerful melodies transport me to a world where anything is possible. This song reminds me to embrace the uncertainties of life and to face challenges head-on, knowing that there is always something to hold onto, even in the darkest of times. It is a reminder that we are all part of a larger narrative and that our actions today can shape a better tomorrow.',10000000001,'2024-07-18','/media/白鲨JAWS - VORTEX.mp3'),(10000000011,'Shadow Assassins - 王舜禾','mp3',10000000002,'SCISSOR SEVEN Season 3 (Animation Original Soundtracks)',10000000001,'2024-07-18','/media/王舜禾 - 暗影刺客.mp3'),(10000000012,'What are you waiting for? - Nickelback','mp3',10000000002,'“What Are You Waiting For?” is a high‐octane pop-rock rally cry from Nickelback, co‐written by Chad Kroeger and Mike Kroeger. Driven by urgent drums, throbbing bass, and shimmering synth layers, its punchy chorus—“What are you waiting for?”—cuts through complacency like a clarion call. A brief piano-led interlude before the final chorus offers a moment of introspection, only to give way to a full-band explosion that propels listeners from hesitation into action. Since its release as the breakout single from No Fixed Address, it has become a live-show staple, uniting crowds in a shared vow to stop waiting and start living.',10000000001,'2024-07-18','/media/Nickelback - What Are You Waiting For_.mp3'),(10000000013,'Do I Matter To Me - 赵寒','mp3',10000000002,'\"One day, when the people and things around you are gone, is it still important to you?\" \"Do I Matter To Me\" is the first officially published English lyrics by Zhang Jiacheng, and the whole lyrics use the end of the world as a metaphorical background, describing a person\'s reflection after losing everything when he is most lonely and lost. Using musicians from Beijing, Hong Kong, Canada and the United States, Zhang Jiacheng ripped apart the worldview of nothingness and an instrumental solo before the final chorus.',10000000001,'2024-07-18','/media/赵寒 - Do I Matter To Me.mp3'),(10000000014,'I Will Never Get Loved - Milk Coffee','mp3',10000000002,'SCISSOR SEVEN Season 4 (Animation Original Soundtracks)',10000000001,'2024-07-18','/media/牛奶咖啡 - 怀抱的温柔并不属于我.mp3'),(10000000015,'Symphony No. 5 in C minor, Op. 67 (Fate Symphony)','mp4',10000000002,'The Fifth Symphony in C minor opens with a musical short-short-short-long rhythmic motive. It is said that Beethoven once interpreted the motive of the four tones as \"the god of fate is knocking at the door\". It dominates the first movement and plays a rather important role throughout the symphony. The whole symphony can be seen as an emotional development, from the conflict and struggle of the first movement in C minor to the triumph and joy of the final movement in C major. The final movement is the climax of the work, which is longer and more powerful in sound than the first movement.',10000000001,'2024-07-18','/media/331334530-1-208.mp4'),(10000000016,'Character Picture - Ororon','jpg',10000000003,'Genshin Impact 5.2',10000000000,'2024-12-18','/image/20241208_221123833_iOS.jpg'),(10000000018,'风吹过的晨曦','flac',10000000002,'',10000000000,'2025-12-04','/media/风吹过的晨曦_1.flac'),(10000000019,'絵本 - Sān-Z & HOYO-MiX','flac',10000000002,'This is a “soul’s picture book” of loneliness, struggle and rebirth — shadows crying out in silent nights, chained souls still holding onto hope, gradually breaking free through melody, spreading their wings, and soaring toward the boundless sky.',10000000000,'2025-12-03','/media/絵本.flac'),(10000000020,'你一定能看见','flac',10000000002,'',10000000000,'2025-12-07','/media/你一定能看见.flac'),(10000000021,'Dive Back In Time','m4a',10000000002,'',10000000000,'2025-12-07','/media/019af7a7-6009-79a1-b2d6-66b5046dc450.m4a'),(10000000033,'Test ePub Book','epub',10000000006,'',10000000000,'2025-12-15','/doc/019b2280-57d2-7120-99e5-07fcc11c920c.epub'),(10000000035,'Test Text','txt',10000000006,'',10000000000,'2025-12-18','/doc/019b2d0b-addf-7bb0-809b-ddeaef0d782d.txt'),(10000000036,'Test Word','docx',10000000006,'',10000000000,'2025-12-18','/doc/019b2d0d-63c5-7a8a-94b3-1bba31804bca.docx'),(10000000037,'Test PPT','pptx',10000000006,'',10000000000,'2025-12-21','/doc/019b405e-cebb-7c53-b01e-d94e064a1d8a.pptx'),(10000000038,'Test Excel','xlsx',10000000006,'',10000000000,'2025-12-25','/doc/019b5409-cc93-7653-977b-9d5071a349f8.xlsx'),(10000000039,'Test TXT','txt',10000000006,'',10000000000,'2025-12-18','/doc/019b3168-7a58-7727-9931-dce156b00829.txt'),(10000000040,'test video','mp4',10000000006,'',10000000000,'2025-12-20','/media/019b3bf5-6d96-79ba-9252-84cdf4f7c322.mp4'),(10000000041,'Test PDF','pdf',10000000006,'',10000000000,'2025-12-21','/doc/019b418a-ef06-7a3c-8174-2d009966cd44.pdf');
/*!40000 ALTER TABLE `file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fileviewrecords`
--

DROP TABLE IF EXISTS `fileviewrecords`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fileviewrecords` (
  `record_id` bigint NOT NULL AUTO_INCREMENT,
  `file_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `view_duration` int NOT NULL,
  `view_date` timestamp NOT NULL,
  `view_count` int NOT NULL DEFAULT '1',
  `first_view` datetime NOT NULL,
  PRIMARY KEY (`record_id`),
  KEY `file_id` (`file_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fileviewrecords_ibfk_1` FOREIGN KEY (`file_id`) REFERENCES `file` (`file_id`),
  CONSTRAINT `fileviewrecords_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000000067 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fileviewrecords`
--

LOCK TABLES `fileviewrecords` WRITE;
/*!40000 ALTER TABLE `fileviewrecords` DISABLE KEYS */;
INSERT INTO `fileviewrecords` VALUES (10000000000,10000000000,10000000001,0,'2024-07-13 03:00:40',1,'2024-07-13 04:00:40'),(10000000001,10000000001,10000000001,0,'2024-07-13 03:00:52',1,'2024-07-13 04:00:52'),(10000000002,10000000002,10000000001,0,'2024-07-13 06:16:37',1,'2024-07-13 07:16:37'),(10000000003,10000000003,10000000001,0,'2024-07-14 03:33:03',1,'2024-07-14 04:33:03'),(10000000004,10000000004,10000000001,0,'2024-07-14 06:50:52',1,'2024-07-14 07:50:52'),(10000000005,10000000006,10000000001,0,'2024-07-14 18:19:25',1,'2024-07-14 19:19:25'),(10000000006,10000000007,10000000001,0,'2024-07-14 18:20:18',1,'2024-07-14 19:20:18'),(10000000007,10000000008,10000000001,0,'2024-07-14 19:23:48',1,'2024-07-14 20:23:48'),(10000000008,10000000009,10000000001,0,'2024-07-14 21:41:14',1,'2024-07-14 22:41:14'),(10000000009,10000000008,10000000000,0,'2025-12-07 06:50:50',5,'2024-07-16 03:06:36'),(10000000010,10000000010,10000000001,0,'2024-07-16 02:22:44',1,'2024-07-16 03:22:44'),(10000000011,10000000001,10000000001,0,'2024-07-16 02:23:43',1,'2024-07-16 03:23:43'),(10000000012,10000000000,10000000001,0,'2024-07-16 02:25:16',1,'2024-07-16 03:25:16'),(10000000013,10000000010,10000000000,0,'2025-10-07 04:31:17',2,'2024-07-16 03:30:40'),(10000000014,10000000010,10000000001,0,'2024-07-16 02:57:49',1,'2024-07-16 03:57:49'),(10000000015,10000000012,10000000001,0,'2024-07-16 03:22:10',1,'2024-07-16 04:22:10'),(10000000016,10000000013,10000000001,0,'2024-07-16 03:23:10',1,'2024-07-16 04:23:10'),(10000000017,10000000011,10000000001,0,'2024-07-16 03:23:18',1,'2024-07-16 04:23:18'),(10000000018,10000000014,10000000001,0,'2024-07-16 03:24:47',1,'2024-07-16 04:24:47'),(10000000019,10000000014,10000000002,0,'2024-07-16 07:53:15',1,'2024-07-16 08:53:15'),(10000000020,10000000010,10000000002,0,'2024-07-16 07:56:28',1,'2024-07-16 08:56:28'),(10000000021,10000000003,10000000003,0,'2024-07-16 17:29:08',1,'2024-07-16 18:29:08'),(10000000022,10000000001,10000000003,0,'2024-07-16 17:30:26',1,'2024-07-16 18:30:26'),(10000000023,10000000002,10000000003,0,'2024-07-16 17:32:43',1,'2024-07-16 18:32:43'),(10000000024,10000000008,10000000003,0,'2024-07-16 17:34:04',1,'2024-07-16 18:34:04'),(10000000025,10000000003,10000000000,0,'2025-12-20 13:32:25',7,'2024-07-17 00:24:51'),(10000000026,10000000004,10000000000,0,'2025-10-07 04:30:51',2,'2024-07-17 00:24:53'),(10000000027,10000000005,10000000000,0,'2025-10-07 04:30:55',2,'2024-07-17 00:24:54'),(10000000028,10000000001,10000000000,0,'2025-10-07 04:31:02',3,'2024-07-17 00:24:57'),(10000000029,10000000002,10000000000,0,'2025-10-07 04:31:09',2,'2024-07-17 00:24:59'),(10000000030,10000000012,10000000000,0,'2025-10-07 04:31:22',8,'2024-07-17 00:25:02'),(10000000031,10000000013,10000000000,0,'2025-12-04 05:30:08',5,'2024-07-17 00:25:11'),(10000000032,10000000000,10000000000,0,'2025-12-07 05:26:57',4,'2024-07-17 00:30:37'),(10000000033,10000000011,10000000000,0,'2025-10-07 04:31:20',3,'2024-07-17 00:31:41'),(10000000034,10000000009,10000000000,0,'2025-12-20 13:32:57',4,'2024-07-17 00:33:42'),(10000000035,10000000015,10000000001,0,'2024-07-17 17:16:54',1,'2024-07-17 18:16:54'),(10000000036,10000000000,10000000002,0,'2024-11-13 23:56:30',1,'2024-11-13 23:56:30'),(10000000037,10000000013,10000000002,0,'2024-11-13 23:58:24',1,'2024-11-13 23:58:24'),(10000000038,10000000006,10000000002,0,'2024-11-13 23:58:38',1,'2024-11-13 23:58:38'),(10000000039,10000000015,10000000002,0,'2024-11-13 23:58:52',1,'2024-11-13 23:58:52'),(10000000040,10000000016,10000000000,0,'2025-12-20 13:32:55',4,'2024-12-18 19:47:51'),(10000000041,10000000003,10000000003,0,'2024-12-19 00:40:57',1,'2024-12-19 00:40:57'),(10000000042,10000000007,10000000000,0,'2025-10-07 04:31:41',2,'2025-05-28 22:27:51'),(10000000043,10000000014,10000000000,0,'2025-10-07 04:31:27',1,'2025-10-07 20:31:27'),(10000000044,10000000015,10000000000,0,'2025-10-07 04:31:29',1,'2025-10-07 20:31:29'),(10000000045,10000000006,10000000000,0,'2025-10-07 04:31:37',1,'2025-10-07 20:31:37'),(10000000047,10000000018,10000000000,0,'2025-12-04 05:37:28',4,'2025-12-04 21:24:40'),(10000000048,10000000003,10000000004,0,'2025-12-04 05:58:56',1,'2025-12-04 21:58:56'),(10000000049,10000000002,10000000004,0,'2025-12-04 05:59:15',1,'2025-12-04 21:59:15'),(10000000050,10000000018,10000000004,0,'2025-12-04 06:02:37',2,'2025-12-04 22:02:08'),(10000000051,10000000019,10000000000,0,'2025-12-20 13:31:59',5,'2025-12-07 11:35:41'),(10000000052,10000000020,10000000000,0,'2025-12-06 19:36:22',1,'2025-12-07 11:36:22'),(10000000053,10000000021,10000000000,0,'2025-12-07 07:18:00',6,'2025-12-07 15:12:15'),(10000000059,10000000033,10000000000,0,'2025-12-25 05:38:37',78,'2025-12-15 22:53:16'),(10000000061,10000000036,10000000000,0,'2025-12-25 15:09:29',31,'2025-12-18 00:03:32'),(10000000062,10000000035,10000000000,0,'2025-12-25 06:00:32',10,'2025-12-18 00:07:41'),(10000000063,10000000037,10000000000,0,'2025-12-25 06:31:31',87,'2025-12-18 19:06:15'),(10000000064,10000000038,10000000000,0,'2025-12-25 07:58:23',13,'2025-12-18 19:47:22'),(10000000065,10000000041,10000000000,0,'2025-12-25 06:00:14',28,'2025-12-21 23:33:04'),(10000000066,10000000039,10000000000,0,'2025-12-25 05:38:29',3,'2025-12-25 12:11:31');
/*!40000 ALTER TABLE `fileviewrecords` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ivversion`
--

DROP TABLE IF EXISTS `ivversion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ivversion` (
  `commit_id` char(36) NOT NULL,
  `version` varchar(35) NOT NULL,
  `update_info` varchar(9999) DEFAULT 'Fix Bugs',
  PRIMARY KEY (`commit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ivversion`
--

LOCK TABLES `ivversion` WRITE;
/*!40000 ALTER TABLE `ivversion` DISABLE KEYS */;
INSERT INTO `ivversion` VALUES ('019b1da5-6a40-7deb-89d3-c1f6040f828f','1.3.71.1000.alpha.4.aic','Added DOCX, XLSX, and PPT reader'),('019b2d26-adaf-72f8-bd47-e90776065c16','1.3.80.1000.alpha.7.aic','Added epub reader, pdf reader and text reader'),('019b5484-eb1a-7a90-bfb0-f86bfb3b6206','1.3.86.1116.beta.1.aic','Update some reader page\'s style design'),('0a54bac6-9861-11f0-a41f-6b3c1ccfef93','1.1.4.1100.beta.2.aic','Fixed an error when the logged in user does not exist'),('1009d1c0-3c4e-11f0-8b9c-a51ae60b576a','1.1.0.1000.beta.1.aic','Update package name; Fix some bugs in history records; Add AiBot page.'),('1792e216-3863-11ef-921f-005056c00001','1.0.16.1000.alpha.0','Initial release'),('179356c8-3863-11ef-921f-005056c00001','1.0.16.1000.alpha.1','Optimize code logic and add the function of modifying personal information'),('1793ca14-3863-11ef-921f-005056c00001','1.0.18.1100.alpha.2','Fixed some known bugs'),('17942f51-3863-11ef-921f-005056c00001','1.0.19.1000.alpha.3','Adjusted the logout page logic and add account unregsiter'),('17949a88-3863-11ef-921f-005056c00001','1.0.21.1000.alpha.4','Adjusted web page architecture'),('2439cb90-3c90-11f0-a211-1b03c85e0ae4','1.1.4.1000.beta.2.aic','Added AiBot function to support exporting chat history; optimized AiBot page; adjusted AiBot Server logic.'),('2d36395a-434a-11ef-acd1-005056c00001','1.0.29.5000.alpha.6','1. Added the function of course; 2. Adjust view history; 3. Support more type of viewer; 4. Support admin operation; 5. Fix some known bugs.'),('2f2c0b4c-a367-11f0-a218-325096b39f47','1.2.51.1000.beta.1.aic','Added support for 23 languages ​including Simplified Chinese, French, German, etc.'),('49cfc210-d367-11f0-877c-752b2124c36f','1.3.63.1000.alpha.1.aic','1. Introduce the navigation bar designed by Waveflux; 2. Redesign the video playback page.'),('67cc5000-3b9e-11f0-8b9c-a51ae60b576a','1.0.34.1000.beta.2.aic','Fixed the bug that database automatic backup was not saved; Fixed the bug that users would get an error when directly turning pages on the browsing history page; Optimized the user history structure.'),('7aff4c00-3bbb-11f0-8b9c-a51ae60b576a','1.0.34.9000.beta.3.aic','Dynamically display the system version; Optimize user history storage; Display user browsing times, first and last browsing time; Fix some known bugs.'),('9bc4dd30-d32b-11f0-9dbd-d964415d14f3','1.2.59.100.release.aic','Adjusted audio player interface'),('9dd2f0a9-4349-11ef-acd1-005056c00001','1.0.22.5000.alpha.5','Adjusted web architecture'),('ac1f6117-9cfa-11f0-922a-e3cd01e5ca3b','1.1.7.1000.zeta.0.aic','Added multi-language options (currently only supports English); Fixed some known bugs.'),('cbd2af32-a37b-11f0-9865-325096b39f47','1.2.55.1000.beta.2.aic','Fixed errors caused by the navigation bar on some pages; Fixed the error that administrators could not modify or delete courses on the course page; Fixed the error that administrators jumped back to the course page after deleting a course on the search page; Adjusted the style of the language selection page.'),('e24e0920-3b9b-11f0-8b9c-a51ae60b576a','1.0.30.1000.beta.1.aic','Added automatic database backup for Linux servers');
/*!40000 ALTER TABLE `ivversion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stuassignments`
--

DROP TABLE IF EXISTS `stuassignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stuassignments` (
  `submission_id` bigint NOT NULL AUTO_INCREMENT,
  `test_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `submission_date` datetime NOT NULL,
  `grade` int DEFAULT NULL,
  PRIMARY KEY (`submission_id`),
  KEY `test_id` (`test_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `stuassignments_ibfk_1` FOREIGN KEY (`test_id`) REFERENCES `test` (`test_id`),
  CONSTRAINT `stuassignments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000000000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stuassignments`
--

LOCK TABLES `stuassignments` WRITE;
/*!40000 ALTER TABLE `stuassignments` DISABLE KEYS */;
/*!40000 ALTER TABLE `stuassignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test`
--

DROP TABLE IF EXISTS `test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `test` (
  `test_id` bigint NOT NULL AUTO_INCREMENT,
  `test_name` varchar(100) NOT NULL,
  `course_id` bigint NOT NULL,
  `test_content` longtext NOT NULL,
  `test_answer` longtext NOT NULL,
  PRIMARY KEY (`test_id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `test_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000000000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test`
--

LOCK TABLES `test` WRITE;
/*!40000 ALTER TABLE `test` DISABLE KEYS */;
/*!40000 ALTER TABLE `test` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(16) NOT NULL,
  `password` varchar(512) NOT NULL,
  `class_id` bigint DEFAULT NULL,
  `eagloxis_id` bigint DEFAULT NULL,
  `way_1_id` varchar(100) DEFAULT NULL,
  `way_2_id` varchar(100) DEFAULT NULL,
  `way_3_id` varchar(100) DEFAULT NULL,
  `way_4_id` varchar(100) DEFAULT NULL,
  `way_5_id` varchar(100) DEFAULT NULL,
  `way_6_id` varchar(100) DEFAULT NULL,
  `authority` enum('normal',' teacher','admin','infinite') NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`),
  KEY `class_id` (`class_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `class` (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000000005 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (10000000000,'Ravon Gonzales','ravongonzales@gmail.com','+14152836259','$argon2id$v=19$m=65536,t=3,p=1$55CmHLytIqlWdQguB+chGw$uHhLgR88RwGNhxu35KmFSQSfW2VwLotYWjHUtfdDEXI',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'infinite'),(10000000001,'114','1078959112@qq.com','+8615640993693','$argon2id$v=19$m=65536,t=3,p=1$0nckdWH+mGAO1TtbWJQQuQ$U7S2iuL56DyaHYsyWdIIntqnsJenaWTACBQYhJwCgY0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'normal'),(10000000002,'长青','1243637340@qq.com','17347140977','$argon2id$v=19$m=65536,t=3,p=1$0nckdWH+mGAO1TtbWJQQuQ$U7S2iuL56DyaHYsyWdIIntqnsJenaWTACBQYhJwCgY0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'normal'),(10000000003,'Tester','Tester','0000000000','$argon2id$v=19$m=65536,t=3,p=1$mH60WgbcZmHsxhzaexLdbg$5qRcBGu1bdso0xgBXZT5e7N8EGKB4V0ua9lEu+0NEvw',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'normal'),(10000000004,'Test','ivadmin@test.ravon.tech','+8618141192117','$argon2id$v=19$m=65536,t=3,p=1$OnrXIvvI9Lsu/nBm3DXjkw$U6ZpOa6d9yANMlzRM/5fLURw0Ylih7Dte+Au/f4grDc',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-25 23:56:02
