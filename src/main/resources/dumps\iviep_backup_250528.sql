-- MySQL dump 10.13  Distrib 9.3.0, for Linux (x86_64)
--
-- Host: localhost    Database: iviep
-- ------------------------------------------------------
-- Server version	9.3.0

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

--
-- Table structure for table `class`
--

DROP TABLE IF EXISTS `class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class` (
  `class_id` int NOT NULL AUTO_INCREMENT,
  `class_name` varchar(100) NOT NULL,
  PRIMARY KEY (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `course_id` int NOT NULL AUTO_INCREMENT,
  `course_name` varchar(100) NOT NULL,
  `course_info` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100017 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES (100000,'OOSA&D','Object-Oriented System Analysis and Design.'),(100001,'Computer Network','Computer networking refers to connected computing devices and an ever-expanding array of IoT devices that communicate with one another.'),(100002,'Music','Listen, just feel your heart!'),(100003,'Genshin Impact','Step Into a Vast Magical World of Adventure'),(100004,'English',NULL);
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file`
--

DROP TABLE IF EXISTS `file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file` (
  `file_id` int NOT NULL AUTO_INCREMENT,
  `file_name` varchar(100) NOT NULL,
  `type` varchar(20) NOT NULL,
  `course_id` int DEFAULT NULL,
  `remarks` varchar(999) DEFAULT NULL,
  `upload_user` int DEFAULT NULL,
  `upload_date` date DEFAULT NULL,
  `file_path` varchar(255) NOT NULL,
  PRIMARY KEY (`file_id`),
  UNIQUE KEY `file_path` (`file_path`),
  KEY `course_id` (`course_id`),
  KEY `upload_user` (`upload_user`),
  CONSTRAINT `file_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`),
  CONSTRAINT `file_ibfk_2` FOREIGN KEY (`upload_user`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100076 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file`
--

LOCK TABLES `file` WRITE;
/*!40000 ALTER TABLE `file` DISABLE KEYS */;
INSERT INTO `file` VALUES (100000,'Ferryman','pdf',100004,'Life, death, love - which would you choose?',1000000001,'2024-07-18','/doc/Ferryman.pdf'),(100001,'The Development and Prospects of Passive Optical Networks (Chinese)','mp4',100001,'PON, developed in the mid-1990s, was oniginally designed to alow Intemet Serice Providers (ISPs) to deliver broadband triple-play senices (data,voice, and video) to residential\nusers.',1000000001,'2024-07-18','/media/PON.mp4'),(100002,'RFC 9114','pdf',100001,'After5 years, HTTP 3 was finaly standardized as RFC 9114. A new chapter in the web will be opened with RFC 9204 (QPACK header compression) and RFC 9218 (Extensible\r Prioritization)!',1000000001,'2024-07-18','/doc/rfc9114.pdf'),(100003,'Adapter Java Example','java',100000,'The user has purchased a new three-phase socket and wants to use the newly purchased three-phase socket to use both three-phase and two-phase appliances.',1000000001,'2024-07-18','/code/AdapterExample.java'),(100004,'Command Java Example','java',100000,'The customer asked the waiter to order Mutton shashlik or chicken, and the chef was responsible for the barbecue.',1000000001,'2024-07-18','/code/CommandExample.java'),(100005,'Singleton Java Example','java',100000,'The print pool is an application that manages print tasks, allowing a print pool user to delete, abort, or change the priority of the print tasks, only one print pool object can run in a system.',1000000001,'2024-07-18','/code/SingletonExample.java'),(100006,'Character Demo - Cyno','mp4',100003,'Counsel of Condemnation | Genshin Impact',1000000001,'2024-07-18','/media/Character Demo - Cyno Counsel of Condemnation Genshin Impact.mp4'),(100007,'Character Teaser - Cyno','mp4',100003,'A Just Punishment | Genshin Impact',1000000001,'2024-07-18','/media/Character Teaser - Cyno A Just Punishment Genshin Impact.mp4'),(100011,'最后时刻 - Li Jian','m4a',100002,'On May 12, 2008, the Wenchuan earthquake. As a singer, the only thing I can do at this moment is to use music to express my care. Li Jian created such a song in the shortest possible time, but it has become the most warm and restrained work among all disaster relief songs. The small love between lovers and relatives replaces the big love in the mainstream voice, and expresses the sadness and love hidden deep in the heart with warm melodies and lyrics.',1000000001,'2024-07-18','/media/01 最后时刻.m4a'),(100015,'Character Picture - Cyno','png',100003,'Genshin Impact',1000000001,'2024-07-18','/image/4d708230-877f-42c0-8cee-fde3304f5278.png'),(100018,'VORTEX - 白鲨JAWS','mp3',100002,'The song \"VORTEX\" is from the album titled \"Link Click Season 2 Original Soundtrack\" released in 2023. It is produced by Bilibili and written by Michael Yu, who is also the lyricist. The song falls under the genres of rock, TV soundtrack, and theme song. The relatable lyrics and powerful melodies transport me to a world where anything is possible. This song reminds me to embrace the uncertainties of life and to face challenges head-on, knowing that there is always something to hold onto, even in the darkest of times. It is a reminder that we are all part of a larger narrative and that our actions today can shape a better tomorrow.',1000000001,'2024-07-18','/media/白鲨JAWS - VORTEX.mp3'),(100021,'Shadow Assassins - 王舜禾','mp3',100002,'SCISSOR SEVEN Season 3 (Animation Original Soundtracks)',1000000001,'2024-07-18','/media/王舜禾 - 暗影刺客.mp3'),(100022,'What are you waiting for? - Nickelback','mp3',100002,'“What Are You Waiting For?” is a high‐octane pop-rock rally cry from Nickelback, co‐written by Chad Kroeger and Mike Kroeger. Driven by urgent drums, throbbing bass, and shimmering synth layers, its punchy chorus—“What are you waiting for?”—cuts through complacency like a clarion call. A brief piano-led interlude before the final chorus offers a moment of introspection, only to give way to a full-band explosion that propels listeners from hesitation into action. Since its release as the breakout single from No Fixed Address, it has become a live-show staple, uniting crowds in a shared vow to stop waiting and start living.',1000000001,'2024-07-18','/media/Nickelback - What Are You Waiting For_.mp3'),(100023,'Do I Matter To Me - 赵寒','mp3',100002,'\"One day, when the people and things around you are gone, is it still important to you?\" \"Do I Matter To Me\" is the first officially published English lyrics by Zhang Jiacheng, and the whole lyrics use the end of the world as a metaphorical background, describing a person\'s reflection after losing everything when he is most lonely and lost. Using musicians from Beijing, Hong Kong, Canada and the United States, Zhang Jiacheng ripped apart the worldview of nothingness and an instrumental solo before the final chorus.',1000000001,'2024-07-18','/media/赵寒 - Do I Matter To Me.mp3'),(100024,'I Will Never Get Loved - Milk Coffee','mp3',100002,'SCISSOR SEVEN Season 4 (Animation Original Soundtracks)',1000000001,'2024-07-18','/media/牛奶咖啡 - 怀抱的温柔并不属于我.mp3'),(100062,'Symphony No. 5 in C minor, Op. 67 (Fate Symphony)','mp4',100002,'The Fifth Symphony in C minor opens with a musical short-short-short-long rhythmic motive. It is said that Beethoven once interpreted the motive of the four tones as \"the god of fate is knocking at the door\". It dominates the first movement and plays a rather important role throughout the symphony. The whole symphony can be seen as an emotional development, from the conflict and struggle of the first movement in C minor to the triumph and joy of the final movement in C major. The final movement is the climax of the work, which is longer and more powerful in sound than the first movement.',1000000001,'2024-07-18','/media/331334530-1-208.mp4'),(100064,'Character Picture - Ororon','jpg',100003,'Genshin Impact 5.2',1000000000,'2024-12-18','/image/20241208_221123833_iOS.jpg');
/*!40000 ALTER TABLE `file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fileviewrecords`
--

DROP TABLE IF EXISTS `fileviewrecords`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fileviewrecords` (
  `record_id` int NOT NULL AUTO_INCREMENT,
  `file_id` int NOT NULL,
  `user_id` int NOT NULL,
  `view_duration` int NOT NULL,
  `view_date` timestamp NOT NULL,
  `view_count` int NOT NULL DEFAULT '1',
  `first_view` datetime NOT NULL,
  PRIMARY KEY (`record_id`),
  KEY `file_id` (`file_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fileviewrecords_ibfk_1` FOREIGN KEY (`file_id`) REFERENCES `file` (`file_id`),
  CONSTRAINT `fileviewrecords_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fileviewrecords`
--

LOCK TABLES `fileviewrecords` WRITE;
/*!40000 ALTER TABLE `fileviewrecords` DISABLE KEYS */;
INSERT INTO `fileviewrecords` VALUES (4,100000,1000000001,0,'2024-07-13 11:00:40',1,'2024-07-13 04:00:40'),(5,100001,1000000001,0,'2024-07-13 11:00:52',1,'2024-07-13 04:00:52'),(6,100002,1000000001,0,'2024-07-13 14:16:37',1,'2024-07-13 07:16:37'),(7,100003,1000000001,0,'2024-07-14 11:33:03',1,'2024-07-14 04:33:03'),(8,100004,1000000001,0,'2024-07-14 14:50:52',1,'2024-07-14 07:50:52'),(9,100006,1000000001,0,'2024-07-15 02:19:25',1,'2024-07-14 19:19:25'),(10,100007,1000000001,0,'2024-07-15 02:20:18',1,'2024-07-14 19:20:18'),(13,100011,1000000001,0,'2024-07-15 03:23:48',1,'2024-07-14 20:23:48'),(17,100015,1000000001,0,'2024-07-15 05:41:14',1,'2024-07-14 22:41:14'),(19,100011,1000000000,0,'2024-07-16 10:06:36',1,'2024-07-16 03:06:36'),(21,100018,1000000018,0,'2024-07-16 10:22:44',1,'2024-07-16 03:22:44'),(22,100001,1000000018,0,'2024-07-16 10:23:43',1,'2024-07-16 03:23:43'),(23,100000,1000000018,0,'2024-07-16 10:25:16',1,'2024-07-16 03:25:16'),(24,100018,1000000000,0,'2024-07-16 10:30:40',1,'2024-07-16 03:30:40'),(27,100018,1000000001,0,'2024-07-16 10:57:49',1,'2024-07-16 03:57:49'),(28,100022,1000000001,0,'2024-07-16 11:22:10',1,'2024-07-16 04:22:10'),(29,100023,1000000001,0,'2024-07-16 11:23:10',1,'2024-07-16 04:23:10'),(30,100021,1000000001,0,'2024-07-16 11:23:18',1,'2024-07-16 04:23:18'),(31,100024,1000000001,0,'2024-07-16 11:24:47',1,'2024-07-16 04:24:47'),(66,100024,1000000017,0,'2024-07-16 15:53:15',1,'2024-07-16 08:53:15'),(68,100018,1000000017,0,'2024-07-16 15:56:28',1,'2024-07-16 08:56:28'),(71,100003,1000000016,0,'2024-07-17 01:29:08',1,'2024-07-16 18:29:08'),(72,100001,1000000016,0,'2024-07-17 01:30:26',1,'2024-07-16 18:30:26'),(73,100002,1000000016,0,'2024-07-17 01:32:43',1,'2024-07-16 18:32:43'),(74,100011,1000000016,0,'2024-07-17 01:34:04',1,'2024-07-16 18:34:04'),(75,100003,1000000000,0,'2024-07-17 07:24:51',1,'2024-07-17 00:24:51'),(76,100004,1000000000,0,'2024-07-17 07:24:53',1,'2024-07-17 00:24:53'),(77,100005,1000000000,0,'2024-07-17 07:24:54',1,'2024-07-17 00:24:54'),(78,100001,1000000000,0,'2024-07-17 07:24:57',2,'2024-07-17 00:24:57'),(79,100002,1000000000,0,'2024-07-17 07:24:59',1,'2024-07-17 00:24:59'),(80,100022,1000000000,0,'2025-05-28 11:10:41',7,'2024-07-17 00:25:02'),(81,100023,1000000000,0,'2024-07-17 07:25:11',2,'2024-07-17 00:25:11'),(82,100000,1000000000,0,'2024-07-17 07:30:37',1,'2024-07-17 00:30:37'),(83,100021,1000000000,0,'2024-07-17 07:31:41',2,'2024-07-17 00:31:41'),(85,100015,1000000000,0,'2024-07-17 07:33:42',1,'2024-07-17 00:33:42'),(87,100062,1000000001,0,'2024-07-18 01:16:54',1,'2024-07-17 18:16:54'),(88,100000,1000000022,0,'2024-11-14 07:56:30',1,'2024-11-13 23:56:30'),(89,100023,1000000022,0,'2024-11-14 07:58:24',1,'2024-11-13 23:58:24'),(90,100006,1000000022,0,'2024-11-14 07:58:38',1,'2024-11-13 23:58:38'),(91,100062,1000000022,0,'2024-11-14 07:58:52',1,'2024-11-13 23:58:52'),(92,100064,1000000000,0,'2024-12-19 03:47:51',1,'2024-12-18 19:47:51'),(93,100003,1000000190,0,'2024-12-19 08:40:57',1,'2024-12-19 00:40:57');
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
INSERT INTO `ivversion` VALUES ('1792e216-3863-11ef-921f-005056c00001','1.0.16.1000.alpha.0','Initial release'),('179356c8-3863-11ef-921f-005056c00001','1.0.16.1000.alpha.1','Optimize code logic and add the function of modifying personal information'),('1793ca14-3863-11ef-921f-005056c00001','1.0.18.1100.alpha.2','Fix some known bugs'),('17942f51-3863-11ef-921f-005056c00001','1.0.19.1000.alpha.3','Adjust the logout page logic and add account unregsiter'),('17949a88-3863-11ef-921f-005056c00001','1.0.21.1000.alpha.4','Adjusting web page architecture'),('2d36395a-434a-11ef-acd1-005056c00001','1.0.29.5000.alpha.6','1. Add the function of course; 2. Adjust view history; 3. Support more type of viewer; 4. Support admin operation; 5. Fix some known bugs.'),('67cc5000-3b9e-11f0-8b9c-a51ae60b576a','1.0.34.1000.beta.2.aix','Fixed the bug that database automatic backup was not saved; Fixed the bug that users would get an error when directly turning pages on the browsing history page; Optimized the user history structure.'),('7aff4c00-3bbb-11f0-8b9c-a51ae60b576a','1.0.34.9000.beta.3.aix','Dynamically display the system version; Optimize user history storage; Display user browsing times, first and last browsing time; Fix some known bugs.'),('9dd2f0a9-4349-11ef-acd1-005056c00001','1.0.22.5000.alpha.5','Adjusting web architecture'),('e24e0920-3b9b-11f0-8b9c-a51ae60b576a','1.0.30.1000.beta.1.aix','Automatic database backup for Linux servers');
/*!40000 ALTER TABLE `ivversion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stuassignments`
--

DROP TABLE IF EXISTS `stuassignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stuassignments` (
  `submission_id` int NOT NULL AUTO_INCREMENT,
  `test_id` int NOT NULL,
  `user_id` int NOT NULL,
  `submission_date` datetime NOT NULL,
  `grade` int DEFAULT NULL,
  PRIMARY KEY (`submission_id`),
  KEY `test_id` (`test_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `stuassignments_ibfk_1` FOREIGN KEY (`test_id`) REFERENCES `test` (`test_id`),
  CONSTRAINT `stuassignments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `test_id` int NOT NULL AUTO_INCREMENT,
  `test_name` varchar(100) NOT NULL,
  `course_id` int NOT NULL,
  `test_content` longtext NOT NULL,
  `test_answer` longtext NOT NULL,
  PRIMARY KEY (`test_id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `test_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
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
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(16) NOT NULL,
  `password` varchar(512) NOT NULL,
  `class_id` int DEFAULT NULL,
  `eagloxis_id` int DEFAULT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=1000000193 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1000000000,'Arnold Lopez','arnold-lopez@auet.onmicrosoft.com','+14086414215','$argon2id$v=19$m=65536,t=3,p=1$55CmHLytIqlWdQguB+chGw$uHhLgR88RwGNhxu35KmFSQSfW2VwLotYWjHUtfdDEXI',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'infinite'),(1000000021,'114','1078959112@qq.com','+8615640993693','$argon2id$v=19$m=65536,t=3,p=1$0nckdWH+mGAO1TtbWJQQuQ$U7S2iuL56DyaHYsyWdIIntqnsJenaWTACBQYhQJwMY0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'normal'),(1000000022,'长青','1243637340@qq.com','17347140977','$argon2id$v=19$m=65536,t=3,p=1$0nckdWH+mGAO1TtbWJQQuQ$U7S2iuL56DyaHYsyWdIIntqnsJenaWTACBQYhQJwMY0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'normal'),(1000000190,'Tester','Tester','0000000000','$argon2id$v=19$m=65536,t=3,p=1$mH60WgbcZmHsxhzaexLdbg$5qRcBGu1bdso0xgBXZT5e7N8EGKB4V0ua9lEu+0NEvw',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'normal');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-28  7:06:48
