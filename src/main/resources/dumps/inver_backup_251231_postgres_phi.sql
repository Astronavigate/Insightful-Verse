CREATE SCHEMA IF NOT EXISTS inver;
SET search_path TO inver;

-- Drop existing tables if they exist
DROP TABLE IF EXISTS annotations CASCADE;
DROP TABLE IF EXISTS "class" CASCADE;
DROP TABLE IF EXISTS collection_items CASCADE;
DROP TABLE IF EXISTS collections CASCADE;
DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS file CASCADE;
DROP TABLE IF EXISTS view_records CASCADE;
DROP TABLE IF EXISTS version CASCADE;
DROP TABLE IF EXISTS reactions CASCADE;
DROP TABLE IF EXISTS stu_assignments CASCADE;
DROP TABLE IF EXISTS question CASCADE;
DROP TABLE IF EXISTS test CASCADE;
DROP TABLE IF EXISTS third_party_accounts CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create tables
CREATE TABLE annotations (
  note_id BIGINT NOT NULL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  book_id BIGINT NOT NULL,
  note_type VARCHAR(10) NOT NULL CHECK (note_type IN ('bookmark','highlight','note')),
  note_cfi VARCHAR(256) NOT NULL,
  note_content VARCHAR(256),
  note_color VARCHAR(28) DEFAULT '#ffd54f',
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO annotations (note_id, user_id, book_id, note_type, note_cfi, note_content, note_color, created_at, updated_at) VALUES
(1,10000000000,10000000033,'bookmark','epubcfi(/6/6[item2]!/4/2[a002]/1:0)','test1',NULL,'2025-12-16 00:14:24','2025-12-16 00:14:24'),
(7,10000000000,10000000033,'bookmark','epubcfi(/6/12[item5]!/4/2[41-14488c5c3e8b491d97c21ee6fb56f687]/2/2/1:0)','shuqian',NULL,'2025-12-16 11:21:32','2025-12-16 11:21:32'),
(29,10000000000,10000000033,'bookmark','epubcfi(/6/8[item3]!/4/2[21-14488c5c3e8b491d97c21ee6fb56f687]/1:0)','',NULL,'2025-12-16 21:50:03','2025-12-16 21:50:03'),
(32,10000000000,10000000033,'highlight','epubcfi(/6/102[item50]!/4,/6/1:78,/18/1:8)','','rgba(224, 224, 0, 0.4)','2025-12-17 21:55:30','2025-12-17 21:55:30'),
(38,10000000000,10000000033,'bookmark','epubcfi(/6/100[item49]!/4/2[a049]/1:0)','书签123',NULL,'2025-12-20 21:29:56','2025-12-20 21:29:56'),
(40,10000000000,10000000033,'highlight','epubcfi(/6/100[item49]!/4,/4/1:16,/6/1:49)','','rgba(224, 224, 0, 0.4)','2025-12-20 21:30:13','2025-12-20 21:30:13');

CREATE TABLE "class" (
  class_id BIGINT NOT NULL PRIMARY KEY,
  class_name VARCHAR(100) NOT NULL
);

-- No data to insert for class

CREATE TABLE collection_items (
  collection_item_id BIGINT NOT NULL PRIMARY KEY,
  item_id BIGINT NOT NULL,
  item_type VARCHAR(10) NOT NULL CHECK (item_type IN ('course','file')),
  create_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- No data to insert for collection_items

CREATE TABLE collections (
  collection_id BIGINT NOT NULL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(512),
  create_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- No data to insert for collections

CREATE TABLE comments (
  comment_id BIGINT NOT NULL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  content VARCHAR(500) NOT NULL,
  type VARCHAR(10) NOT NULL CHECK (type IN ('course','file','collection')),
  related_id BIGINT NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- No data to insert for comments

CREATE TABLE courses (
  course_id BIGINT NOT NULL PRIMARY KEY,
  course_name VARCHAR(100) NOT NULL,
  course_info VARCHAR(300)
);

INSERT INTO courses (course_id, course_name, course_info) VALUES
(10000000000,'OOSA&D','Object-Oriented System Analysis and Design.'),
(10000000001,'Computer Network','Computer networking refers to connected computing devices and an ever-expanding array of IoT devices that communicate with one another.'),
(10000000002,'Music','Listen, just feel your heart!'),
(10000000003,'Genshin Impact','Step Into a Vast Magical World of Adventure'),
(10000000004,'English',NULL),
(10000000006,'Temp Recourse','');

CREATE TABLE file (
  file_id BIGINT NOT NULL PRIMARY KEY,
  file_name VARCHAR(100) NOT NULL,
  file_type VARCHAR(20) NOT NULL,
  course_id BIGINT,
  file_remark VARCHAR(2000),
  upload_user BIGINT,
  upload_date DATE,
  file_path VARCHAR(255) NOT NULL
);

INSERT INTO file (file_id, file_name, file_type, course_id, file_remark, upload_user, upload_date, file_path) VALUES
(10000000000,'Ferryman','pdf',10000000004,'Life, death, love - which would you choose?',10000000001,'2024-07-18','/doc/Ferryman.pdf'),
(10000000001,'The Development and Prospects of Passive Optical Networks (Chinese)','mp4',10000000001,'PON, developed in the mid-1990s, was oniginally designed to alow Intemet Serice Providers (ISPs) to deliver broadband triple-play senices (data,voice, and video) to residential\nusers.',10000000001,'2024-07-18','/media/PON.mp4'),
(10000000002,'RFC 9114','pdf',10000000001,'After5 years, HTTP 3 was finaly standardized as RFC 9114. A new chapter in the web will be opened with RFC 9204 (QPACK header compression) and RFC 9218 (Extensible\r Prioritization)!',10000000001,'2024-07-18','/doc/rfc9114.pdf'),
(10000000003,'Adapter Java Example','java',10000000000,'The user has purchased a new three-phase socket and wants to use the newly purchased three-phase socket to use both three-phase and two-phase appliances.',10000000001,'2024-07-18','/code/AdapterExample.java'),
(10000000004,'Command Java Example','java',10000000000,'The customer asked the waiter to order Mutton shashlik or chicken, and the chef was responsible for the barbecue.',10000000001,'2024-07-18','/code/CommandExample.java'),
(10000000005,'Singleton Java Example','java',10000000000,'The print pool is an application that manages print tasks, allowing a print pool user to delete, abort, or change the priority of the print tasks, only one print pool object can run in a system.',10000000001,'2024-07-18','/code/SingletonExample.java'),
(10000000006,'Character Demo - Cyno','mp4',10000000003,'Counsel of Condemnation | Genshin Impact',10000000001,'2024-07-18','/media/Character Demo - Cyno Counsel of Condemnation Genshin Impact.mp4'),
(10000000007,'Character Teaser - Cyno','mp4',10000000003,'A Just Punishment | Genshin Impact',10000000001,'2024-07-18','/media/Character Teaser - Cyno A Just Punishment Genshin Impact.mp4'),
(10000000008,'最后时刻 - Li Jian','m4a',10000000002,'On May 12, 2008, the Wenchuan earthquake. As a singer, the only thing I can do at this moment is to use music to express my care. Li Jian created such a song in the shortest possible time, but it has become the most warm and restrained work among all disaster relief songs. The small love between lovers and relatives replaces the big love in the mainstream voice, and expresses the sadness and love hidden deep in the heart with warm melodies and lyrics.',10000000001,'2024-07-18','/media/01 最后时刻.m4a'),
(10000000009,'Character Picture - Cyno','png',10000000003,'Genshin Impact',10000000001,'2024-07-18','/image/4d708230-877f-42c0-8cee-fde3304f5278.png'),
(10000000010,'VORTEX - 白鲨JAWS','mp3',10000000002,'The song \"VORTEX\" is from the album titled \"Link Click Season 2 Original Soundtrack\" released in 2023. It is produced by Bilibili and written by Michael Yu, who is also the lyricist. The song falls under the genres of rock, TV soundtrack, and theme song.',10000000001,'2024-07-18','/media/白鲨JAWS - VORTEX.mp3'),
(10000000011,'Shadow Assassins - 王舜禾','mp3',10000000002,'SCISSOR SEVEN Season 3 (Animation Original Soundtracks)',10000000001,'2024-07-18','/media/王舜禾 - 暗影刺客.mp3'),
(10000000012,'What are you waiting for? - Nickelback','mp3',10000000002,'“What Are You Waiting For?” is a high‐octane pop-rock rally cry from Nickelback, co‐written by Chad Kroeger and Mike Kroeger.',10000000001,'2024-07-18','/media/Nickelback - What Are You Waiting For_.mp3'),
(10000000013,'Do I Matter To Me - 赵寒','mp3',10000000002,'\"One day, when the people and things around you are gone, is it still important to you?\" \"Do I Matter To Me\" is the first officially published English lyrics by Zhang Jiacheng...',10000000001,'2024-07-18','/media/赵寒 - Do I Matter To Me.mp3'),
(10000000014,'I Will Never Get Loved - Milk Coffee','mp3',10000000002,'SCISSOR SEVEN Season 4 (Animation Original Soundtracks)',10000000001,'2024-07-18','/media/牛奶咖啡 - 怀抱的温柔并不属于我.mp3'),
(10000000015,'Symphony No. 5 in C minor, Op. 67 (Fate Symphony)','mp4',10000000002,'The Fifth Symphony in C minor opens with a musical short-short-short-long rhythmic motive.',10000000001,'2024-07-18','/media/331334530-1-208.mp4'),
(10000000016,'Character Picture - Ororon','jpg',10000000003,'Genshin Impact 5.2',10000000000,'2024-12-18','/image/20241208_221123833_iOS.jpg'),
(10000000018,'风吹过的晨曦','flac',10000000002,NULL,10000000000,'2025-12-04','/media/风吹过的晨曦_1.flac'),
(10000000019,'絵本 - Sān-Z & HOYO-MiX','flac',10000000002,'This is a “soul’s picture book” of loneliness, struggle and rebirth...',10000000000,'2025-12-03','/media/絵本.flac'),
(10000000020,'你一定能看见','flac',10000000002,NULL,10000000000,'2025-12-07','/media/你一定能看见.flac'),
(10000000021,'Dive Back In Time','m4a',10000000002,NULL,10000000000,'2025-12-07','/media/019af7a7-6009-79a1-b2d6-66b5046dc450.m4a'),
(10000000033,'Test ePub Book','epub',10000000006,NULL,10000000000,'2025-12-15','/doc/019b2280-57d2-7120-99e5-07fcc11c920c.epub'),
(10000000035,'Test Text','txt',10000000006,NULL,10000000000,'2025-12-18','/doc/019b2d0b-addf-7bb0-809b-ddeaef0d782d.txt'),
(10000000036,'Test Word','docx',10000000006,NULL,10000000000,'2025-12-18','/doc/019b2d0d-63c5-7a8a-94b3-1bba31804bca.docx'),
(10000000037,'Test PPT','pptx',10000000006,NULL,10000000000,'2025-12-21','/doc/019b405e-cebb-7c53-b01e-d94e064a1d8a.pptx'),
(10000000038,'Test Excel','xlsx',10000000006,NULL,10000000000,'2025-12-25','/doc/019b5409-cc93-7653-977b-9d5071a349f8.xlsx'),
(10000000039,'Test TXT','txt',10000000006,NULL,10000000000,'2025-12-18','/doc/019b3168-7a58-7727-9931-dce156b00829.txt'),
(10000000040,'test video','mp4',10000000006,NULL,10000000000,'2025-12-20','/media/019b3bf5-6d96-79ba-9252-84cdf4f7c322.mp4'),
(10000000041,'Test PDF','pdf',10000000006,NULL,10000000000,'2025-12-21','/doc/019b418a-ef06-7a3c-8174-2d009966cd44.pdf'),
(10000000042,'Test Python','py',10000000000,NULL,10000000000,'2025-12-28','/code/019b6583-dee9-7d7b-9513-8cd99caf5cbf.py');

CREATE TABLE view_records (
  record_id BIGINT NOT NULL PRIMARY KEY,
  file_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  view_duration INT NOT NULL,
  view_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  view_count INT NOT NULL DEFAULT 1,
  first_view TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

INSERT INTO view_records (record_id, file_id, user_id, view_duration, view_date, view_count, first_view) VALUES
(10000000000,10000000000,10000000001,0,'2024-07-13 03:00:40',1,'2024-07-13 04:00:40'),
(10000000001,10000000001,10000000001,0,'2024-07-13 03:00:52',1,'2024-07-13 04:00:52'),
(10000000002,10000000002,10000000001,0,'2024-07-13 06:16:37',1,'2024-07-13 07:16:37'),
(10000000003,10000000003,10000000001,0,'2024-07-14 03:33:03',1,'2024-07-14 04:33:03'),
(10000000004,10000000004,10000000001,0,'2024-07-14 06:50:52',1,'2024-07-14 07:50:52'),
(10000000005,10000000006,10000000001,0,'2024-07-14 18:19:25',1,'2024-07-14 19:19:25'),
(10000000006,10000000007,10000000001,0,'2024-07-14 18:20:18',1,'2024-07-14 19:20:18'),
(10000000007,10000000008,10000000001,0,'2024-07-14 19:23:48',1,'2024-07-14 20:23:48'),
(10000000008,10000000009,10000000001,0,'2024-07-14 21:41:14',1,'2024-07-14 22:41:14'),
(10000000009,10000000008,10000000000,0,'2025-12-07 06:50:50',5,'2024-07-16 03:06:36'),
(10000000010,10000000010,10000000001,0,'2024-07-16 02:22:44',1,'2024-07-16 03:22:44'),
(10000000011,10000000001,10000000001,0,'2024-07-16 02:23:43',1,'2024-07-16 03:23:43'),
(10000000012,10000000000,10000000001,0,'2024-07-16 02:25:16',1,'2024-07-16 03:25:16'),
(10000000013,10000000010,10000000000,0,'2025-10-07 04:31:17',2,'2024-07-16 03:30:40'),
(10000000014,10000000010,10000000001,0,'2024-07-16 02:57:49',1,'2024-07-16 03:57:49'),
(10000000015,10000000012,10000000001,0,'2024-07-16 03:22:10',1,'2024-07-16 04:22:10'),
(10000000016,10000000013,10000000001,0,'2024-07-16 03:23:10',1,'2024-07-16 04:23:10'),
(10000000017,10000000011,10000000001,0,'2024-07-16 03:23:18',1,'2024-07-16 04:23:18'),
(10000000018,10000000014,10000000001,0,'2024-07-16 03:24:47',1,'2024-07-16 04:24:47'),
(10000000019,10000000014,10000000002,0,'2024-07-16 07:53:15',1,'2024-07-16 08:53:15'),
(10000000020,10000000010,10000000002,0,'2024-07-16 07:56:28',1,'2024-07-16 08:56:28'),
(10000000021,10000000003,10000000003,0,'2024-07-16 17:29:08',1,'2024-07-16 18:29:08'),
(10000000022,10000000001,10000000003,0,'2024-07-16 17:30:26',1,'2024-07-16 18:30:26'),
(10000000023,10000000002,10000000003,0,'2024-07-16 17:32:43',1,'2024-07-16 18:32:43'),
(10000000024,10000000008,10000000003,0,'2024-07-16 17:34:04',1,'2024-07-16 18:34:04'),
(10000000025,10000000003,10000000000,0,'2025-12-28 11:36:04',9,'2024-07-17 00:24:51'),
(10000000026,10000000004,10000000000,0,'2025-10-07 04:30:51',2,'2024-07-17 00:24:53'),
(10000000027,10000000005,10000000000,0,'2025-10-07 04:30:55',2,'2024-07-17 00:24:54'),
(10000000028,10000000001,10000000000,0,'2025-10-07 04:31:02',3,'2024-07-17 00:24:57'),
(10000000029,10000000002,10000000000,0,'2025-10-07 04:31:09',2,'2024-07-17 00:24:59'),
(10000000030,10000000012,10000000000,0,'2025-10-07 04:31:22',8,'2024-07-17 00:25:02'),
(10000000031,10000000013,10000000000,0,'2025-12-04 05:30:08',5,'2024-07-17 00:25:11'),
(10000000032,10000000000,10000000000,0,'2025-12-07 05:26:57',4,'2024-07-17 00:30:37'),
(10000000033,10000000011,10000000000,0,'2025-10-07 04:31:20',3,'2024-07-17 00:31:41'),
(10000000034,10000000009,10000000000,0,'2025-12-20 13:32:57',4,'2024-07-17 00:33:42'),
(10000000035,10000000015,10000000001,0,'2024-07-17 17:16:54',1,'2024-07-17 18:16:54'),
(10000000036,10000000000,10000000002,0,'2024-11-13 23:56:30',1,'2024-11-13 23:56:30'),
(10000000037,10000000013,10000000002,0,'2024-11-13 23:58:24',1,'2024-11-13 23:58:24'),
(10000000038,10000000006,10000000002,0,'2024-11-13 23:58:38',1,'2024-11-13 23:58:38'),
(10000000039,10000000015,10000000002,0,'2024-11-13 23:58:52',1,'2024-11-13 23:58:52'),
(10000000040,10000000016,10000000000,0,'2025-12-27 09:36:32',6,'2024-12-18 19:47:51'),
(10000000041,10000000003,10000000003,0,'2024-12-19 00:40:57',1,'2024-12-19 00:40:57'),
(10000000042,10000000007,10000000000,0,'2025-10-07 04:31:41',2,'2025-05-28 22:27:51'),
(10000000043,10000000014,10000000000,0,'2025-10-07 04:31:27',1,'2025-10-07 20:31:27'),
(10000000044,10000000015,10000000000,0,'2025-10-07 04:31:29',1,'2025-10-07 20:31:29'),
(10000000045,10000000006,10000000000,0,'2025-10-07 04:31:37',1,'2025-10-07 20:31:37'),
(10000000047,10000000018,10000000000,0,'2025-12-04 05:37:28',4,'2025-12-04 21:24:40'),
(10000000048,10000000003,10000000004,0,'2025-12-04 05:58:56',1,'2025-12-04 21:58:56'),
(10000000049,10000000002,10000000004,0,'2025-12-04 05:59:15',1,'2025-12-04 21:59:15'),
(10000000050,10000000018,10000000004,0,'2025-12-04 06:02:37',2,'2025-12-04 22:02:08'),
(10000000051,10000000019,10000000000,0,'2025-12-31 07:12:16',7,'2025-12-07 11:35:41'),
(10000000052,10000000020,10000000000,0,'2025-12-06 19:36:22',1,'2025-12-07 11:36:22'),
(10000000053,10000000021,10000000000,0,'2025-12-07 07:18:00',6,'2025-12-07 15:12:15'),
(10000000059,10000000033,10000000000,0,'2025-12-28 01:43:50',79,'2025-12-15 22:53:16'),
(10000000061,10000000036,10000000000,0,'2025-12-25 15:09:29',31,'2025-12-18 00:03:32'),
(10000000062,10000000035,10000000000,0,'2025-12-28 04:05:09',11,'2025-12-18 00:07:41'),
(10000000063,10000000037,10000000000,0,'2025-12-27 09:36:09',88,'2025-12-18 19:06:15'),
(10000000064,10000000038,10000000000,0,'2025-12-25 07:58:23',13,'2025-12-18 19:47:22'),
(10000000065,10000000041,10000000000,0,'2025-12-27 09:35:22',29,'2025-12-21 23:33:04'),
(10000000066,10000000039,10000000000,0,'2025-12-25 05:38:29',3,'2025-12-25 12:11:31');

CREATE TABLE version (
  commit_id CHAR(36) NOT NULL,
  version VARCHAR(35) NOT NULL,
  update_info VARCHAR(1000) DEFAULT 'Fix Bugs',
  PRIMARY KEY (commit_id)
);

INSERT INTO version (commit_id, version, update_info) VALUES
('019b1da5-6a40-7deb-89d3-c1f6040f828f','1.3.71.1000.alpha.4.aic','Added epub, txt, docx, pptx, xlsx, and py file types.'),
('019b2d0b-addf-7bb0-809b-ddeaef0d782d','1.3.72.1000.beta.5.bet','Inserted data for test text files.'),
('019b5409-cc93-7653-977b-9d5071a349f8','1.3.73.1000.gamma.6.gam','Included Excel file type and associated data.');

CREATE TABLE reactions (
  like_id BIGINT NOT NULL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  content_type VARCHAR(10) NOT NULL CHECK (content_type IN ('course','file','collection','comment')),
  related_id BIGINT NOT NULL,
  action VARCHAR(5) NOT NULL CHECK (action IN ('like', 'dislike'))
);

-- No data to insert for reactions

CREATE TABLE stu_assignments (
  submission_id BIGINT NOT NULL PRIMARY KEY,
  test_id BIGINT NOT NULL,
  question_id BIGINT NOT NULL,
  submitter_id BIGINT NOT NULL,
  submission_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  stu_score INT
);

-- No data to insert for stu_assignments

CREATE TABLE question (
  question_id BIGINT NOT NULL PRIMARY KEY,
  test_id BIGINT NOT NULL,
  submitter_id BIGINT NOT NULL,
  question_score FLOAT NOT NULL,
  submission_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  question_content TEXT NOT NULL,
  question_answer TEXT NOT NULL
);

-- No data to insert for question

CREATE TABLE test (
  test_id BIGINT NOT NULL PRIMARY KEY,
  test_name VARCHAR(100) NOT NULL,
  course_id BIGINT NOT NULL,
  test_introduction TEXT NOT NULL
);

-- No data to insert for test

CREATE TABLE third_party_accounts (
  id BIGINT NOT NULL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  third_party_id VARCHAR(255) NOT NULL,
  provider_type VARCHAR(10) NOT NULL CHECK (provider_type IN ('ravon','google','apple','microsoft','other'))
);

-- No data to insert for third_party_accounts

CREATE TABLE users (
  user_id BIGINT NOT NULL PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  avatar_url VARCHAR(255) DEFAULT '/image/system/avatar/default.png',
  email VARCHAR(100) NOT NULL UNIQUE,
  phone VARCHAR(16) NOT NULL UNIQUE,
  password VARCHAR(512) NOT NULL,
  class_id BIGINT,
  authority VARCHAR(10) NOT NULL CHECK (authority IN ('normal','teacher','admin','infinite'))
);

INSERT INTO users (user_id, username, avatar_url, email, phone, password, class_id, authority) VALUES
(10000000000,'Ravon Gonzales','/image/system/avatar/default.png','ravongonzales@gmail.com','+14152836259','$argon2id$v=19$m=65536,t=3,p=1$55CmHLytIqlWdQguB+chGw$uHhLgR88RwGNhxu35KmFSQSfW2VwLotYWjHUtfdDEXI',NULL,'infinite'),
(10000000001,'114','/image/system/avatar/default.png','1078959112@qq.com','+8615640993693','$argon2id$v=19$m=65536,t=3,p=1$0nckdWH+mGAO1TtbWJQQuQ$U7S2iuL56DyaHYsyWdIIntqnsJenaWTACBQYhJwCgY0',NULL,'normal'),
(10000000002,'长青','/image/system/avatar/default.png','1243637340@qq.com','17347140977','$argon2id$v=19$m=65536,t=3,p=1$0nckdWH+mGAO1TtbWJQQuQ$U7S2iuL56DyaHYsyWdIIntqnsJenaWTACBQYhJwCgY0',NULL,'normal'),
(10000000003,'Tester','/image/system/avatar/default.png','Tester','0000000000','$argon2id$v=19$m=65536,t=3,p=1$mH60WgbcZmHsxhzaexLdbg$5qRcBGu1bdso0xgBXZT5e7N8EGKB4V0ua9lEu+0NEvw',NULL,'normal'),
(10000000004,'Test','/image/system/avatar/default.png','ivadmin@test.ravon.tech','+8618141192117','$argon2id$v=19$m=65536,t=3,p=1$OnrXIvvI9Lsu/nBm3DXjkw$U6ZpOa6d9yANMlzRM/5fLURw0Ylih7Dte+Au/f4grDc',NULL,'admin');
