--
-- PostgreSQL database dump
--

\restrict KT7axBzwGRoCiNBL8JR0a6BSucJiyPY3QLjMwHrct4cz668RCrhD1HR5IhLohlc

-- Dumped from database version 18.1
-- Dumped by pg_dump version 18.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: inver; Type: SCHEMA; Schema: -; Owner: ravon
--

CREATE SCHEMA inver;


ALTER SCHEMA inver OWNER TO ravon;

--
-- Name: action_type_enum; Type: TYPE; Schema: inver; Owner: ravon
--

CREATE TYPE inver.action_type_enum AS ENUM (
    'like',
    'dislike'
);


ALTER TYPE inver.action_type_enum OWNER TO ravon;

--
-- Name: authority_type_enum; Type: TYPE; Schema: inver; Owner: ravon
--

CREATE TYPE inver.authority_type_enum AS ENUM (
    'normal',
    'teacher',
    'admin',
    'infinite'
);


ALTER TYPE inver.authority_type_enum OWNER TO ravon;

--
-- Name: item_type_enum; Type: TYPE; Schema: inver; Owner: ravon
--

CREATE TYPE inver.item_type_enum AS ENUM (
    'course',
    'files'
);


ALTER TYPE inver.item_type_enum OWNER TO ravon;

--
-- Name: note_type_enum; Type: TYPE; Schema: inver; Owner: ravon
--

CREATE TYPE inver.note_type_enum AS ENUM (
    'bookmark',
    'highlight',
    'note'
);


ALTER TYPE inver.note_type_enum OWNER TO ravon;

--
-- Name: provider_type_enum; Type: TYPE; Schema: inver; Owner: ravon
--

CREATE TYPE inver.provider_type_enum AS ENUM (
    'ravon',
    'google',
    'apple',
    'microsoft',
    'other'
);


ALTER TYPE inver.provider_type_enum OWNER TO ravon;

--
-- Name: related_type_enum; Type: TYPE; Schema: inver; Owner: ravon
--

CREATE TYPE inver.related_type_enum AS ENUM (
    'course',
    'files',
    'collection',
    'comment'
);


ALTER TYPE inver.related_type_enum OWNER TO ravon;

--
-- Name: trigger_set_updated_at(); Type: FUNCTION; Schema: inver; Owner: ravon
--

CREATE FUNCTION inver.trigger_set_updated_at() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
  NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$;


ALTER FUNCTION inver.trigger_set_updated_at() OWNER TO ravon;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: annotations; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.annotations (
    note_id bigint NOT NULL,
    user_id bigint NOT NULL,
    book_id bigint NOT NULL,
    note_type inver.note_type_enum NOT NULL,
    note_cfi character varying(256) NOT NULL,
    note_content character varying(256),
    note_color character varying(28) DEFAULT '#ffd54f'::character varying,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE inver.annotations OWNER TO ravon;

--
-- Name: annotations_note_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.annotations_note_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.annotations_note_id_seq OWNER TO ravon;

--
-- Name: annotations_note_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.annotations_note_id_seq OWNED BY inver.annotations.note_id;


--
-- Name: answers; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.answers (
    answer_id bigint NOT NULL,
    question_id bigint NOT NULL,
    submitter_id bigint NOT NULL,
    answer_content text,
    answer_score real NOT NULL,
    submission_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE inver.answers OWNER TO ravon;

--
-- Name: answers_answer_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.answers_answer_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.answers_answer_id_seq OWNER TO ravon;

--
-- Name: answers_answer_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.answers_answer_id_seq OWNED BY inver.answers.answer_id;


--
-- Name: class; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.class (
    class_id bigint NOT NULL,
    class_name character varying(100) NOT NULL
);


ALTER TABLE inver.class OWNER TO ravon;

--
-- Name: class_class_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.class_class_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.class_class_id_seq OWNER TO ravon;

--
-- Name: class_class_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.class_class_id_seq OWNED BY inver.class.class_id;


--
-- Name: collection_items; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.collection_items (
    collection_item_id bigint NOT NULL,
    item_id bigint NOT NULL,
    item_type inver.item_type_enum NOT NULL,
    collection_id bigint NOT NULL,
    create_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE inver.collection_items OWNER TO ravon;

--
-- Name: collection_items_collection_item_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.collection_items_collection_item_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.collection_items_collection_item_id_seq OWNER TO ravon;

--
-- Name: collection_items_collection_item_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.collection_items_collection_item_id_seq OWNED BY inver.collection_items.collection_item_id;


--
-- Name: collections; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.collections (
    collection_id bigint NOT NULL,
    user_id bigint NOT NULL,
    collection_name character varying(128) NOT NULL,
    collection_description character varying(512),
    create_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE inver.collections OWNER TO ravon;

--
-- Name: collections_collection_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.collections_collection_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.collections_collection_id_seq OWNER TO ravon;

--
-- Name: collections_collection_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.collections_collection_id_seq OWNED BY inver.collections.collection_id;


--
-- Name: comments; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.comments (
    comment_id bigint NOT NULL,
    user_id bigint NOT NULL,
    comment_content character varying(500) NOT NULL,
    related_type inver.related_type_enum NOT NULL,
    related_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE inver.comments OWNER TO ravon;

--
-- Name: comments_comment_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.comments_comment_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.comments_comment_id_seq OWNER TO ravon;

--
-- Name: comments_comment_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.comments_comment_id_seq OWNED BY inver.comments.comment_id;


--
-- Name: courses; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.courses (
    course_id bigint NOT NULL,
    course_name character varying(100) NOT NULL,
    course_info character varying(300)
);


ALTER TABLE inver.courses OWNER TO ravon;

--
-- Name: courses_course_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.courses_course_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.courses_course_id_seq OWNER TO ravon;

--
-- Name: courses_course_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.courses_course_id_seq OWNED BY inver.courses.course_id;


--
-- Name: files; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.files (
    file_id bigint NOT NULL,
    file_name character varying(100) NOT NULL,
    file_type character varying(20) NOT NULL,
    course_id bigint,
    file_remark character varying(2000),
    upload_user bigint,
    upload_date date,
    file_path character varying(255) NOT NULL
);


ALTER TABLE inver.files OWNER TO ravon;

--
-- Name: files_file_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.files_file_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.files_file_id_seq OWNER TO ravon;

--
-- Name: files_file_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.files_file_id_seq OWNED BY inver.files.file_id;


--
-- Name: questions; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.questions (
    question_id bigint NOT NULL,
    test_id bigint NOT NULL,
    submitter_id bigint NOT NULL,
    question_score real NOT NULL,
    submission_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    question_content text NOT NULL,
    question_answer text NOT NULL
);


ALTER TABLE inver.questions OWNER TO ravon;

--
-- Name: questions_question_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.questions_question_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.questions_question_id_seq OWNER TO ravon;

--
-- Name: questions_question_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.questions_question_id_seq OWNED BY inver.questions.question_id;


--
-- Name: reactions; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.reactions (
    like_id bigint NOT NULL,
    user_id bigint NOT NULL,
    related_type inver.related_type_enum NOT NULL,
    related_id bigint NOT NULL,
    action inver.action_type_enum NOT NULL
);


ALTER TABLE inver.reactions OWNER TO ravon;

--
-- Name: reactions_like_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.reactions_like_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.reactions_like_id_seq OWNER TO ravon;

--
-- Name: reactions_like_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.reactions_like_id_seq OWNED BY inver.reactions.like_id;


--
-- Name: test; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.test (
    test_id bigint NOT NULL,
    test_name character varying(100) NOT NULL,
    course_id bigint NOT NULL,
    test_introduction text NOT NULL
);


ALTER TABLE inver.test OWNER TO ravon;

--
-- Name: test_test_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.test_test_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.test_test_id_seq OWNER TO ravon;

--
-- Name: test_test_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.test_test_id_seq OWNED BY inver.test.test_id;


--
-- Name: third_party_accounts; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.third_party_accounts (
    third_id bigint NOT NULL,
    user_id bigint NOT NULL,
    third_party_id character varying(255) NOT NULL,
    provider_type inver.provider_type_enum NOT NULL
);


ALTER TABLE inver.third_party_accounts OWNER TO ravon;

--
-- Name: third_party_accounts_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.third_party_accounts_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.third_party_accounts_id_seq OWNER TO ravon;

--
-- Name: third_party_accounts_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.third_party_accounts_id_seq OWNED BY inver.third_party_accounts.third_id;


--
-- Name: users; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.users (
    user_id bigint NOT NULL,
    username character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    phone character varying(16) NOT NULL,
    password character varying(512) NOT NULL,
    class_id bigint,
    authority inver.authority_type_enum NOT NULL,
    avatar_url character varying(255) DEFAULT '/image/system/avatar/default.png'::character varying
);


ALTER TABLE inver.users OWNER TO ravon;

--
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.users_user_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.users_user_id_seq OWNER TO ravon;

--
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.users_user_id_seq OWNED BY inver.users.user_id;


--
-- Name: version; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.version (
    commit_id character(36) NOT NULL,
    version character varying(35) NOT NULL,
    update_info character varying(1000) DEFAULT 'Fix Bugs'::character varying,
    release_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE inver.version OWNER TO ravon;

--
-- Name: view_records; Type: TABLE; Schema: inver; Owner: ravon
--

CREATE TABLE inver.view_records (
    record_id bigint NOT NULL,
    file_id bigint NOT NULL,
    user_id bigint NOT NULL,
    view_duration integer NOT NULL,
    view_date timestamp without time zone NOT NULL,
    view_count integer DEFAULT 1 NOT NULL,
    first_view timestamp without time zone NOT NULL
);


ALTER TABLE inver.view_records OWNER TO ravon;

--
-- Name: view_records_record_id_seq; Type: SEQUENCE; Schema: inver; Owner: ravon
--

CREATE SEQUENCE inver.view_records_record_id_seq
    START WITH 10000000000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE inver.view_records_record_id_seq OWNER TO ravon;

--
-- Name: view_records_record_id_seq; Type: SEQUENCE OWNED BY; Schema: inver; Owner: ravon
--

ALTER SEQUENCE inver.view_records_record_id_seq OWNED BY inver.view_records.record_id;


--
-- Name: annotations note_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.annotations ALTER COLUMN note_id SET DEFAULT nextval('inver.annotations_note_id_seq'::regclass);


--
-- Name: answers answer_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.answers ALTER COLUMN answer_id SET DEFAULT nextval('inver.answers_answer_id_seq'::regclass);


--
-- Name: class class_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.class ALTER COLUMN class_id SET DEFAULT nextval('inver.class_class_id_seq'::regclass);


--
-- Name: collection_items collection_item_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.collection_items ALTER COLUMN collection_item_id SET DEFAULT nextval('inver.collection_items_collection_item_id_seq'::regclass);


--
-- Name: collections collection_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.collections ALTER COLUMN collection_id SET DEFAULT nextval('inver.collections_collection_id_seq'::regclass);


--
-- Name: comments comment_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.comments ALTER COLUMN comment_id SET DEFAULT nextval('inver.comments_comment_id_seq'::regclass);


--
-- Name: courses course_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.courses ALTER COLUMN course_id SET DEFAULT nextval('inver.courses_course_id_seq'::regclass);


--
-- Name: files file_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.files ALTER COLUMN file_id SET DEFAULT nextval('inver.files_file_id_seq'::regclass);


--
-- Name: questions question_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.questions ALTER COLUMN question_id SET DEFAULT nextval('inver.questions_question_id_seq'::regclass);


--
-- Name: reactions like_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.reactions ALTER COLUMN like_id SET DEFAULT nextval('inver.reactions_like_id_seq'::regclass);


--
-- Name: test test_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.test ALTER COLUMN test_id SET DEFAULT nextval('inver.test_test_id_seq'::regclass);


--
-- Name: third_party_accounts third_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.third_party_accounts ALTER COLUMN third_id SET DEFAULT nextval('inver.third_party_accounts_id_seq'::regclass);


--
-- Name: users user_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.users ALTER COLUMN user_id SET DEFAULT nextval('inver.users_user_id_seq'::regclass);


--
-- Name: view_records record_id; Type: DEFAULT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.view_records ALTER COLUMN record_id SET DEFAULT nextval('inver.view_records_record_id_seq'::regclass);


--
-- Data for Name: annotations; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.annotations (note_id, user_id, book_id, note_type, note_cfi, note_content, note_color, created_at, updated_at) FROM stdin;
32	10000000000	10000000033	highlight	epubcfi(/6/102[item50]!/4,/6/1:78,/18/1:8)		rgba(224, 224, 0, 0.4)	2025-12-17 21:55:30	2025-12-17 21:55:30
40	10000000000	10000000033	highlight	epubcfi(/6/100[item49]!/4,/4/1:16,/6/1:49)		rgba(224, 224, 0, 0.4)	2025-12-20 21:30:13	2025-12-20 21:30:13
\.


--
-- Data for Name: answers; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.answers (answer_id, question_id, submitter_id, answer_content, answer_score, submission_date) FROM stdin;
\.


--
-- Data for Name: class; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.class (class_id, class_name) FROM stdin;
\.


--
-- Data for Name: collection_items; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.collection_items (collection_item_id, item_id, item_type, collection_id, create_at) FROM stdin;
4	10000000002	course	1	2026-01-03 14:46:14.29366
\.


--
-- Data for Name: collections; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.collections (collection_id, user_id, collection_name, collection_description, create_at) FROM stdin;
1	10000000000	Default	Default Collection	2026-01-03 14:10:18.416103
\.


--
-- Data for Name: comments; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.comments (comment_id, user_id, comment_content, related_type, related_id, created_at) FROM stdin;
\.


--
-- Data for Name: courses; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.courses (course_id, course_name, course_info) FROM stdin;
10000000000	OOSA&D	Object-Oriented System Analysis and Design.
10000000001	Computer Network	Computer networking refers to connected computing devices and an ever-expanding array of IoT devices that communicate with one another.
10000000002	Music	Listen, just feel your heart!
10000000003	Genshin Impact	Step Into a Vast Magical World of Adventure
10000000004	English	\N
10000000006	Temp Recourse	\N
10000000005	Language and Literatures	\N
\.


--
-- Data for Name: files; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.files (file_id, file_name, file_type, course_id, file_remark, upload_user, upload_date, file_path) FROM stdin;
10000000000	Ferryman	pdf	10000000004	Life, death, love - which would you choose?	10000000001	2024-07-18	/doc/Ferryman.pdf
10000000001	The Development and Prospects of Passive Optical Networks (Chinese)	mp4	10000000001	PON, developed in the mid-1990s, was oniginally designed to alow Intemet Serice Providers (ISPs) to deliver broadband triple-play senices (data,voice, and video) to residential\r\nusers.	10000000001	2024-07-18	/media/PON.mp4
10000000002	RFC 9114	pdf	10000000001	After5 years, HTTP 3 was finaly standardized as RFC 9114. A new chapter in the web will be opened with RFC 9204 (QPACK header compression) and RFC 9218 (Extensible\r\n Prioritization)!	10000000001	2024-07-18	/doc/rfc9114.pdf
10000000003	Adapter Java Example	java	10000000000	The user has purchased a new three-phase socket and wants to use the newly purchased three-phase socket to use both three-phase and two-phase appliances.	10000000001	2024-07-18	/code/AdapterExample.java
10000000004	Command Java Example	java	10000000000	The customer asked the waiter to order Mutton shashlik or chicken, and the chef was responsible for the barbecue.	10000000001	2024-07-18	/code/CommandExample.java
10000000005	Singleton Java Example	java	10000000000	The print pool is an application that manages print tasks, allowing a print pool user to delete, abort, or change the priority of the print tasks, only one print pool object can run in a system.	10000000001	2024-07-18	/code/SingletonExample.java
10000000006	Character Demo - Cyno	mp4	10000000003	Counsel of Condemnation | Genshin Impact	10000000001	2024-07-18	/media/Character Demo - Cyno Counsel of Condemnation Genshin Impact.mp4
10000000007	Character Teaser - Cyno	mp4	10000000003	A Just Punishment | Genshin Impact	10000000001	2024-07-18	/media/Character Teaser - Cyno A Just Punishment Genshin Impact.mp4
10000000008	最后时刻 - Li Jian	m4a	10000000002	On May 12, 2008, the Wenchuan earthquake. As a singer, the only thing I can do at this moment is to use music to express my care. Li Jian created such a song in the shortest possible time, but it has become the most warm and restrained work among all disaster relief songs. The small love between lovers and relatives replaces the big love in the mainstream voice, and expresses the sadness and love hidden deep in the heart with warm melodies and lyrics.	10000000001	2024-07-18	/media/01 最后时刻.m4a
10000000009	Character Picture - Cyno	png	10000000003	Genshin Impact	10000000001	2024-07-18	/image/4d708230-877f-42c0-8cee-fde3304f5278.png
10000000010	VORTEX - 白鲨JAWS	mp3	10000000002	The song "VORTEX" is from the album titled "Link Click Season 2 Original Soundtrack" released in 2023. It is produced by Bilibili and written by Michael Yu, who is also the lyricist. The relatable lyrics and powerful melodies transport me to a world where anything is possible. This song reminds me to embrace the uncertainties of life and to face challenges head-on, knowing that there is always something to hold onto, even in the darkest of times. It is a reminder that we are all part of a larger narrative and that our actions today can shape a better tomorrow.	10000000001	2024-07-18	/media/白鲨JAWS - VORTEX.mp3
10000000011	Shadow Assassins - 王舜禾	mp3	10000000002	SCISSOR SEVEN Season 3 (Animation Original Soundtracks)	10000000001	2024-07-18	/media/王舜禾 - 暗影刺客.mp3
10000000013	Do I Matter To Me - 赵寒	mp3	10000000002	"One day, when the people and things around you are gone, is it still important to you?" "Do I Matter To Me" is the first officially published English lyrics by Zhang Jiacheng, and the whole lyrics use the end of the world as a metaphorical background, describing a person's reflection after losing everything when he is most lonely and lost. Using musicians from Beijing, Hong Kong, Canada and the United States, Zhang Jiacheng ripped apart the worldview of nothingness and an instrumental solo before the final chorus.	10000000001	2024-07-18	/media/赵寒 - Do I Matter To Me.mp3
10000000014	I Will Never Get Loved - Milk Coffee	mp3	10000000002	SCISSOR SEVEN Season 4 (Animation Original Soundtracks)	10000000001	2024-07-18	/media/牛奶咖啡 - 怀抱的温柔并不属于我.mp3
10000000015	Symphony No. 5 in C minor, Op. 67 (Fate Symphony)	mp4	10000000002	The Fifth Symphony in C minor opens with a musical short-short-short-long rhythmic motive. It is said that Beethoven once interpreted the motive of the four tones as "the god of fate is knocking at the door". It dominates the first movement and plays a rather important role throughout the symphony. The whole symphony can be seen as an emotional development, from the conflict and struggle of the first movement in C minor to the triumph and joy of the final movement in C major. The final movement is the climax of the work, which is longer and more powerful in sound than the first movement.	10000000001	2024-07-18	/media/331334530-1-208.mp4
10000000016	Character Picture - Ororon	jpg	10000000003	Genshin Impact 5.2	10000000000	2024-12-18	/image/20241208_221123833_iOS.jpg
10000000018	风吹过的晨曦	flac	10000000002		10000000000	2025-12-04	/media/风吹过的晨曦_1.flac
10000000020	你一定能看见	flac	10000000002		10000000000	2025-12-07	/media/你一定能看见.flac
10000000021	Dive Back In Time	m4a	10000000002		10000000000	2025-12-07	/media/019af7a7-6009-79a1-b2d6-66b5046dc450.m4a
10000000033	Test ePub Book	epub	10000000006		10000000000	2025-12-15	/doc/019b2280-57d2-7120-99e5-07fcc11c920c.epub
10000000035	Test Text	txt	10000000006		10000000000	2025-12-18	/doc/019b2d0b-addf-7bb0-809b-ddeaef0d782d.txt
10000000036	Test Word	docx	10000000006		10000000000	2025-12-18	/doc/019b2d0d-63c5-7a8a-94b3-1bba31804bca.docx
10000000037	Test PPT	pptx	10000000006		10000000000	2025-12-21	/doc/019b405e-cebb-7c53-b01e-d94e064a1d8a.pptx
10000000038	Test Excel	xlsx	10000000006		10000000000	2025-12-25	/doc/019b5409-cc93-7653-977b-9d5071a349f8.xlsx
10000000039	Test TXT	txt	10000000006		10000000000	2025-12-18	/doc/019b3168-7a58-7727-9931-dce156b00829.txt
10000000040	test video	mp4	10000000006		10000000000	2025-12-20	/media/019b3bf5-6d96-79ba-9252-84cdf4f7c322.mp4
10000000041	Test PDF	pdf	10000000006		10000000000	2025-12-21	/doc/019b418a-ef06-7a3c-8174-2d009966cd44.pdf
10000000042	Test Python	py	10000000000		10000000000	2025-12-28	/code/019b6583-dee9-7d7b-9513-8cd99caf5cbf.py
10000000044	The divine comedy by Dante Alighieri	epub	10000000005	"The divine comedy" by Dante Alighieri is an Italian narrative poem written between 1308 and 1321. The work follows Dante's journey through the three realms of the afterlife: Hell, Purgatory, and Heaven. Guided by the poet Virgil and his idealized woman Beatrice, Dante encounters souls receiving divine justice based on their earthly actions. The poem allegorically represents the soul's journey toward God through recognition of sin, penance, and spiritual ascent, drawing on medieval Catholic theology and philosophy. (This is an automatically generated summary.)	10000000000	2026-01-12	/doc/019ba5c1-1065-7faf-a70b-9340edf52c4b.epub
10000000046	The Time Machine by H. G. Wells	epub	10000000005	"The Time Machine" by H. G. Wells is a science fiction novella published in 1895. A Victorian scientist known as the Time Traveller journeys to the year 802,701, where he discovers humanity has evolved into two distinct species: the childlike Eloi and the savage Morlocks. This groundbreaking work popularized the concept of time travel and coined the term "time machine." Wells crafts a haunting vision of the future that explores class divisions	10000000000	2026-01-12	/doc/019bb14a-2037-7698-b6f4-c7102136b8ae.epub
10000000048	Test Word by Word Lyrics	flac	10000000006		10000000000	2026-01-16	/media/019bc744-cafc-75be-92f8-48cfbff96ea9.flac
10000000052	東坡樂府 by Shi Su	epub	10000000005	"東坡樂府" by Shi Su is a collection of poetic works written in the late 11th century. This compilation honors the renowned Chinese poet and statesman Su Shi and reflects his passion for life, nature, and artistry. The poems capture various themes such as love, nostalgia, and the beauty of spring, often set against the backdrop of scenic landscapes. The opening of the collection features a series of verses that convey the poet's observations and reflections during seasons like spring and autumn. Su Shi's lyrical style shines through as he explores the emotions tied to nature and personal experiences with friends and loved ones. The poems, rich with imagery, evoke a sense of longing and appreciation for transient moments and relationships, setting the stage for a deeper exploration of the poet's thoughts and feelings in the subsequent sections of the collection. (This is an automatically generated summary.)	10000000000	2026-01-17	/doc/019bcbb7-5d1b-7a65-a711-737902183e26.epub
10000000047	Good Luck, Babe! - Chappell Roan	flac	10000000002	In April 2024, Roan sent an email to fans stating that "Good Luck, Babe!" would be released on April 5, writing that the song is "about wishing good luck to someone who is denying fate".	10000000000	2026-01-18	/media/019bc9a6-a0bf-750d-a8d2-e7b87050ea22.flac
10000000053	李太白集 by Bai Li	epub	10000000005	"李太白集" by Bai Li is a collection of poetry written in the 8th century. This work presents a rich anthology of poems crafted by the famed Tang dynasty poet Li Bai, known for his vivid imagery, emotional depth, and connection to nature. The collection showcases Li Bai’s themes of romance, nature, and the pursuit of a transcendent existence. At the start of the anthology, the introductory information details the historical context of Li Bai's life, establishing his birth in the year 701 and providing references to significant events during the Tang dynasty that shaped his poetry. The opening portion includes a glimpse of poetic forms and themes that resonate within Li Bai’s works, such as the carefree spirit of youth, the influence of nature, and allusions to his philosophical musings on life, love, and the cosmos. Through these preliminary introductions and selected poems, readers are invited to explore the profound reflections and aesthetic beauty that characterize Li Bai’s poetry. (This is an automatically generated summary.)	10000000000	2026-01-17	/doc/019bcbcd-698f-7716-82a1-f03152ef3b92.epub
10000000054	The King James Version of the Bible	epub	10000000005	"The King James Version of the Bible" is an English translation of the Christian Bible commissioned in 1604 and published in 1611 under King James VI and I. This translation for the Church of England contains 80 books and was created to address controversies surrounding existing versions. Celebrated for its majestic style, it has profoundly shaped English literature, Christian thought, and everyday speech for over four centuries. The translation became the unchallenged standard in English Protestant churches and remains one of the most widely read versions today. (This is an automatically generated summary.)	10000000000	2026-01-17	/doc/019bcbd5-8253-73eb-93c1-27258255ed5a.epub
10000000019	絵本 - Sān-Z & HOYO-MiX	flac	10000000002	Set beneath a heavy, leaden sky where "happiness never poured down," the song tracks the movement of a solitary figure defined by the single shadow at their feet. It is a story of internal fracture—where wings shatter and the soul plunges into a bottomless dark—only to be reclaimed through the sheer mechanics of persistence and the mutual recognition between companions who have been walking together all along and its solitude.	10000000000	2026-01-17	/media/019bcb47-5784-7e95-a562-8b78a9d4cb48.flac
10000000050	山海經 by Anonymous	epub	10000000005	"山海經" by Anonymous is a Chinese classic text compiled during the early Han dynasty, though early versions may have existed since the 4th century BCE. This mythic compilation describes over 550 mountains and 300 channels across pre-Qin China, blending fabulous geography with cultural accounts and mythology. The work catalogs medicines, animals, geological features, and short myths through detailed descriptions organized by cardinal directions—mixing mundane observations with fanciful and strange creatures in a repetitious, encyclopedic format. (This is an automatically generated summary.)	10000000000	2026-01-17	/doc/019bcbb5-48a8-73eb-a6cf-00ea4f90beee.epub
10000000051	道德經 by Laozi	epub	10000000005	"道德經" by Laozi is an ancient Chinese text from the late Warring States period (475-221 BCE). Traditionally attributed to the sage Laozi, this foundational work of Taoism explores the Way and its virtue through philosophical teachings. The text has profoundly influenced Chinese philosophy, religion, and culture, while also becoming one of the most translated works in world literature. Its authorship remains debated, with archaeological discoveries continuing to reveal earlier manuscript versions that reshape understanding of this classic. (This is an automatically generated summary.)	10000000000	2026-01-17	/doc/019bcbb6-4da0-78a7-9aef-61f14d44f2ee.epub
10000000049	A Thousand Year - Christina Perri	flac	10000000002	Christina Perri's "A Thousand Years" is about timeless, enduring love, patient waiting, and overcoming fear for a deep, destined connection, popularized for Twilight's Bella and Edward but resonating universally for weddings and profound devotion, expressing a belief in a soulmate found after lifetimes of waiting. The lyrics convey a powerful commitment, highlighting themes of bravery, hope, and an eternal bond that feels predestined, making every moment of past longing worthwhile. 	10000000000	2026-01-18	/media/019bc7f6-1180-780f-b6ff-cc867cfbd3be.flac
10000000012	What are you waiting for? - Nickelback	flac	10000000002	"What Are You Waiting For?" is a high‐octane pop-rock rally cry from Nickelback, co‐written by Chad Kroeger and Mike Kroeger. Driven by urgent drums, throbbing bass, and shimmering synth layers, its punchy chorus—"What are you waiting for?"—cuts through complacency like a clarion call. A brief piano-led interlude before the final chorus offers a moment of introspection, only to give way to a full-band explosion that propels listeners from hesitation into action. Since its release as the breakout single from No Fixed Address, it has become a live-show staple, uniting crowds in a shared vow to stop waiting and start living.	10000000000	2026-01-18	/media/019bd05c-c782-7a1b-bf09-6701a07e70f3.flac
10000000055	Moshimonogatari - Tani Yuuki	flac	10000000002	"Moshimonogatari" (もしものがたり) by Tani Yuuki means "What If Story" or "Hypothetical Tale" and is a gentle, perspective-shifting song for the Doraemon anime, encouraging seeing life's challenges differently, while Tani Yuuki is the popular Japanese singer known for heartfelt, relatable tracks, and the title references the Monogatari series' unique storytelling, blending fantasy with deep emotion.	10000000000	2026-01-18	/media/019bd0dc-f4fd-7da1-977e-b3169c685abd.flac
10000000056	test music	flac	10000000006		10000000000	2026-01-19	/media/019bd42b-7379-7a54-8810-0c79c4e52e5b.flac
\.


--
-- Data for Name: questions; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.questions (question_id, test_id, submitter_id, question_score, submission_date, question_content, question_answer) FROM stdin;
\.


--
-- Data for Name: reactions; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.reactions (like_id, user_id, related_type, related_id, action) FROM stdin;
\.


--
-- Data for Name: test; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.test (test_id, test_name, course_id, test_introduction) FROM stdin;
\.


--
-- Data for Name: third_party_accounts; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.third_party_accounts (third_id, user_id, third_party_id, provider_type) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.users (user_id, username, email, phone, password, class_id, authority, avatar_url) FROM stdin;
10000000000	Ravon Gonzales	ravongonzales@gmail.com	+14152836259	$argon2id$v=19$m=65536,t=3,p=1$55CmHLytIqlWdQguB+chGw$uHhLgR88RwGNhxu35KmFSQSfW2VwLotYWjHUtfdDEXI	\N	infinite	/image/system/avatar/default.png
10000000001	114	1078959112@qq.com	+8615640993693	$argon2id$v=19$m=65536,t=3,p=1$0nckdWH+mGAO1TtbWJQQuQ$U7S2iuL56DyaHYsyWdIIntqnsJenaWTACBQYhJwCgY0	\N	normal	/image/system/avatar/default.png
10000000002	长青	1243637340@qq.com	17347140977	$argon2id$v=19$m=65536,t=3,p=1$0nckdWH+mGAO1TtbWJQQuQ$U7S2iuL56DyaHYsyWdIIntqnsJenaWTACBQYhJwCgY0	\N	normal	/image/system/avatar/default.png
10000000003	Tester	Tester	0000000000	$argon2id$v=19$m=65536,t=3,p=1$mH60WgbcZmHsxhzaexLdbg$5qRcBGu1bdso0xgBXZT5e7N8EGKB4V0ua9lEu+0NEvw	\N	normal	/image/system/avatar/default.png
10000000004	Test	ivadmin@test.ravon.tech	+8618141192117	$argon2id$v=19$m=65536,t=3,p=1$OnrXIvvI9Lsu/nBm3DXjkw$U6ZpOa6d9yANMlzRM/5fLURw0Ylih7Dte+Au/f4grDc	\N	admin	/image/system/avatar/default.png
\.


--
-- Data for Name: version; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.version (commit_id, version, update_info, release_date) FROM stdin;
ac1f6117-9cfa-11f0-922a-e3cd01e5ca3b	1.1.7.1000.zeta.0.aic	1. Added multi-language options (currently only supports English); 2. Fixed some known bugs.	2025-09-29 06:11:26+08
2f2c0b4c-a367-11f0-a218-325096b39f47	1.2.51.1000.beta.1.aic	1. Added support for 23 languages ​including Simplified Chinese, French, German, etc.	2025-10-07 10:40:00+08
cbd2af32-a37b-11f0-9865-325096b39f47	1.2.55.1000.beta.2.aic	1. Fixed errors caused by the navigation bar on some pages; 2. Fixed the error that administrators could not modify or delete courses on the course page; 3. Fixed the error that administrators jumped back to the course page after deleting a course on the search page; 4. Adjusted the style of the language selection page.	2025-10-07 12:49:12+08
019b1da5-6a40-7deb-89d3-c1f6040f828f	1.3.71.1000.alpha.4.aic	1. Added DOCX, XLSX, and PPT reader	2025-12-14 16:23:49+08
019b2d26-adaf-72f8-bd47-e90776065c16	1.3.80.1000.alpha.7.aic	1. Added epub reader, pdf reader and text reader	2025-12-17 16:34:24+08
0528d0f9-1037-4669-9c98-b04a4f207260	1.3.83.1100.beta.1.aix	1. Fix Bugs	2025-05-28 14:23:48+08
2d36395a-434a-11ef-acd1-005056c00001	1.0.29.5000.alpha.6	1. Added the function of course; 2. Adjust view history; 3. Support more type of viewer; 4. Support admin operation; 5. Fix some known bugs.	2024-06-25 10:00:00+08
019b5484-eb1a-7a90-bfb0-f86bfb3b6206	1.3.86.1116.beta.1.aic	1. Update some reader page's style design	2025-12-23 10:00:00+08
019b5b05-92d8-7139-abbf-5248d7772f19	1.3.89.1132.beta.2.aic	1. Update and unify reader styles	2025-12-26 14:20:36+08
1792e216-3863-11ef-921f-005056c00001	1.0.16.1000.alpha.0	1. Initial release	2024-04-10 10:00:00+08
179356c8-3863-11ef-921f-005056c00001	1.0.16.1000.alpha.1	1. Optimize code logic and add the function of modifying personal information	2024-04-20 10:00:00+08
1793ca14-3863-11ef-921f-005056c00001	1.0.18.1100.alpha.2	1. Fixed some known bugs	2024-05-05 10:00:00+08
019bc5a2-7f1c-7ef0-84d5-3a573402a6c0	1.4.132.1200.alpha.3.aic	1. Optimized lyric scrolling logic; 2. Updated the About page; 3. Fixed Hindi language errors.	2026-01-16 07:10:12+08
17942f51-3863-11ef-921f-005056c00001	1.0.19.1000.alpha.3	1. Adjusted the logout page logic and add account unregsiter	2024-05-20 10:00:00+08
17949a88-3863-11ef-921f-005056c00001	1.0.21.1000.alpha.4	1. Adjusted web page architecture	2024-06-05 10:00:00+08
9dd2f0a9-4349-11ef-acd1-005056c00001	1.0.22.5000.alpha.5	1. Adjusted web architecture	2024-06-15 10:00:00+08
7aff4c00-3bbb-11f0-8b9c-a51ae60b576a	1.0.34.9000.beta.3.aic	1. Dynamically display the system version; 2. Optimize user history storage; 3. Display user browsing times, first and last browsing time; 4. Fix some known bugs.	2025-05-28 14:09:43+08
019b8331-2cdd-7eca-bbdc-fbc4ff135f06	1.4.128.1000.alpha.2.aic	1. Update the database schema and add some new tables; 2. Add course favorites to the default favorites folder.	2026-01-03 09:31:52+08
019b751b-a2ad-73c6-970c-e561bcb11b5d	1.4.111.1000.alpha.1.aic	1. Switch to using the Postgres database; 2. Optimize data transmission and update logic.	2025-12-31 16:12:32+08
019b6ac6-a8d1-71a9-adce-e5bffbbe1c72	1.3.97.1200.release.aic	1. Completely updated and unified the global page style; 2. Updated support for compiling C, C++, Python, and Rust code.	2025-12-29 15:56:03+08
49cfc210-d367-11f0-877c-752b2124c36f	1.3.63.1000.alpha.1.aic	1. Introduce the navigation bar designed by Waveflux; 2. Redesign the video playback page.	2025-12-07 13:22:41+08
51a47186-178b-4371-97ce-db371d66c87a	1.0.34.9100.beta.3.aix	1. Fix Bugs	2025-05-28 14:23:48+08
1009d1c0-3c4e-11f0-8b9c-a51ae60b576a	1.1.0.1000.beta.1.aic	1. Update package name; 2. Fix some bugs in history records; 3. Add AiBot page.	2025-05-29 05:28:46+08
2439cb90-3c90-11f0-a211-1b03c85e0ae4	1.1.4.1000.beta.2.aic	1. Added AiBot function to support exporting chat history; 2. Optimized AiBot page; 3. Adjusted AiBot Server logic.	2025-05-29 13:24:13+08
f6b92df1-cc43-4e8d-bcc8-fd48de1b7cf1	1.1.7.1000.rc.1.aix	1. Fix Bugs	2025-05-28 14:23:48+08
019bd0bd-c182-78af-b4dc-ec139e0240c1	1.4.134.1210.beta.1.aic	1. Fixed the issue where the cover colors were too dark and the lyrics were too dark to read.	2026-01-18 18:54:27.38684+08
e24e0920-3b9b-11f0-8b9c-a51ae60b576a	1.0.30.1000.beta.1.aic	1. Added automatic database backup for Linux servers	2025-05-15 10:00:00+08
67cc5000-3b9e-11f0-8b9c-a51ae60b576a	1.0.34.1000.beta.2.aic	1. Fixed the bug that database automatic backup was not saved; 2. Fixed the bug that users would get an error when directly turning pages on the browsing history page; 3. Optimized the user history structure.	2025-05-25 10:00:00+08
0a54bac6-9861-11f0-a41f-6b3c1ccfef93	1.1.4.1100.beta.2.aic	1. Fixed an error when the logged in user does not exist	2025-05-30 10:00:00+08
9bc4dd30-d32b-11f0-9dbd-d964415d14f3	1.2.59.100.release.aic	1. Adjusted audio player interface	2025-11-07 10:00:00+08
\.


--
-- Data for Name: view_records; Type: TABLE DATA; Schema: inver; Owner: ravon
--

COPY inver.view_records (record_id, file_id, user_id, view_duration, view_date, view_count, first_view) FROM stdin;
10000000000	10000000000	10000000001	0	2024-07-13 03:00:40	1	2024-07-13 04:00:40
10000000001	10000000001	10000000001	0	2024-07-13 03:00:52	1	2024-07-13 04:00:52
10000000002	10000000002	10000000001	0	2024-07-13 06:16:37	1	2024-07-13 07:16:37
10000000003	10000000003	10000000001	0	2024-07-14 03:33:03	1	2024-07-14 04:33:03
10000000004	10000000004	10000000001	0	2024-07-14 06:50:52	1	2024-07-14 07:50:52
10000000005	10000000006	10000000001	0	2024-07-14 18:19:25	1	2024-07-14 19:19:25
10000000006	10000000007	10000000001	0	2024-07-14 18:20:18	1	2024-07-14 19:20:18
10000000007	10000000008	10000000001	0	2024-07-14 19:23:48	1	2024-07-14 20:23:48
10000000008	10000000009	10000000001	0	2024-07-14 21:41:14	1	2024-07-14 22:41:14
10000000010	10000000010	10000000001	0	2024-07-16 02:22:44	1	2024-07-16 03:22:44
10000000011	10000000001	10000000001	0	2024-07-16 02:23:43	1	2024-07-16 03:23:43
10000000012	10000000000	10000000001	0	2024-07-16 02:25:16	1	2024-07-16 03:25:16
10000000014	10000000010	10000000001	0	2024-07-16 02:57:49	1	2024-07-16 03:57:49
10000000015	10000000012	10000000001	0	2024-07-16 03:22:10	1	2024-07-16 04:22:10
10000000016	10000000013	10000000001	0	2024-07-16 03:23:10	1	2024-07-16 04:23:10
10000000017	10000000011	10000000001	0	2024-07-16 03:23:18	1	2024-07-16 04:23:18
10000000018	10000000014	10000000001	0	2024-07-16 03:24:47	1	2024-07-16 04:24:47
10000000019	10000000014	10000000002	0	2024-07-16 07:53:15	1	2024-07-16 08:53:15
10000000020	10000000010	10000000002	0	2024-07-16 07:56:28	1	2024-07-16 08:56:28
10000000021	10000000003	10000000003	0	2024-07-16 17:29:08	1	2024-07-16 18:29:08
10000000022	10000000001	10000000003	0	2024-07-16 17:30:26	1	2024-07-16 18:30:26
10000000023	10000000002	10000000003	0	2024-07-16 17:32:43	1	2024-07-16 18:32:43
10000000024	10000000008	10000000003	0	2024-07-16 17:34:04	1	2024-07-16 18:34:04
10000000025	10000000003	10000000000	0	2025-12-28 11:36:04	9	2024-07-17 00:24:51
10000000026	10000000004	10000000000	0	2025-10-07 04:30:51	2	2024-07-17 00:24:53
10000000027	10000000005	10000000000	0	2025-10-07 04:30:55	2	2024-07-17 00:24:54
10000000028	10000000001	10000000000	0	2025-10-07 04:31:02	3	2024-07-17 00:24:57
10000000032	10000000000	10000000000	0	2025-12-07 05:26:57	4	2024-07-17 00:30:37
10000000034	10000000009	10000000000	0	2025-12-20 13:32:57	4	2024-07-17 00:33:42
10000000035	10000000015	10000000001	0	2024-07-17 17:16:54	1	2024-07-17 18:16:54
10000000036	10000000000	10000000002	0	2024-11-13 23:56:30	1	2024-11-13 23:56:30
10000000037	10000000013	10000000002	0	2024-11-13 23:58:24	1	2024-11-13 23:58:24
10000000038	10000000006	10000000002	0	2024-11-13 23:58:38	1	2024-11-13 23:58:38
10000000039	10000000015	10000000002	0	2024-11-13 23:58:52	1	2024-11-13 23:58:52
10000000040	10000000016	10000000000	0	2025-12-27 09:36:32	6	2024-12-18 19:47:51
10000000041	10000000003	10000000003	0	2024-12-19 00:40:57	1	2024-12-19 00:40:57
10000000042	10000000007	10000000000	0	2025-10-07 04:31:41	2	2025-05-28 22:27:51
10000000044	10000000015	10000000000	0	2025-10-07 04:31:29	1	2025-10-07 20:31:29
10000000045	10000000006	10000000000	0	2025-10-07 04:31:37	1	2025-10-07 20:31:37
10000000048	10000000003	10000000004	0	2025-12-04 05:58:56	1	2025-12-04 21:58:56
10000000049	10000000002	10000000004	0	2025-12-04 05:59:15	1	2025-12-04 21:59:15
10000000050	10000000018	10000000004	0	2025-12-04 06:02:37	2	2025-12-04 22:02:08
10000000052	10000000020	10000000000	0	2025-12-06 19:36:22	1	2025-12-07 11:36:22
10000000053	10000000021	10000000000	0	2025-12-07 07:18:00	6	2025-12-07 15:12:15
10000000065	10000000041	10000000000	0	2026-01-01 12:06:28.804207	31	2025-12-21 23:33:04
10000000059	10000000033	10000000000	0	2026-01-01 12:07:25.232995	83	2025-12-15 22:53:16
10000000066	10000000039	10000000000	0	2026-01-01 12:56:30.88126	5	2025-12-25 12:11:31
10000000051	10000000019	10000000000	0	2026-01-17 17:26:51.530684	14	2025-12-07 11:35:41
10000000074	10000000050	10000000000	0	2026-01-17 19:26:56.030804	1	2026-01-17 19:26:56.030804
10000000075	10000000051	10000000000	0	2026-01-17 19:28:02.819509	1	2026-01-17 19:28:02.819509
10000000068	10000000044	10000000000	0	2026-01-10 10:36:50.077605	4	2026-01-10 09:30:31.947214
10000000070	10000000046	10000000000	0	2026-01-17 19:28:08.399267	2	2026-01-12 16:20:34.85406
10000000076	10000000052	10000000000	0	2026-01-17 19:29:12.97781	1	2026-01-17 19:29:12.97781
10000000077	10000000053	10000000000	0	2026-01-17 19:53:17.490462	1	2026-01-17 19:53:17.490462
10000000029	10000000002	10000000000	0	2026-01-11 18:11:25.177625	3	2024-07-17 00:24:59
10000000078	10000000054	10000000000	0	2026-01-17 20:02:10.939542	1	2026-01-17 20:02:10.939542
10000000062	10000000035	10000000000	0	2026-01-11 18:13:44.703278	12	2025-12-18 00:07:41
10000000061	10000000036	10000000000	0	2026-01-11 18:14:01.725822	33	2025-12-18 00:03:32
10000000063	10000000037	10000000000	0	2026-01-11 18:14:07.755808	89	2025-12-18 19:06:15
10000000064	10000000038	10000000000	0	2026-01-11 18:14:22.104084	14	2025-12-18 19:47:22
10000000072	10000000048	10000000000	0	2026-01-16 22:46:12.239101	2	2026-01-16 22:45:35.417435
10000000073	10000000049	10000000000	0	2026-01-18 17:08:34.091575	10	2026-01-17 01:51:50.826951
10000000013	10000000010	10000000000	0	2026-01-17 09:58:05.869237	3	2024-07-16 03:30:40
10000000033	10000000011	10000000000	0	2026-01-17 09:58:10.207047	4	2024-07-17 00:31:41
10000000031	10000000013	10000000000	0	2026-01-17 09:58:19.147673	6	2024-07-17 00:25:11
10000000030	10000000012	10000000000	0	2026-01-18 17:08:37.969068	13	2024-07-17 00:25:02
10000000043	10000000014	10000000000	0	2026-01-17 09:58:33.593717	3	2025-10-07 20:31:27
10000000047	10000000018	10000000000	0	2026-01-17 09:58:37.536501	5	2025-12-04 21:24:40
10000000071	10000000047	10000000000	0	2026-01-17 09:59:08.827933	8	2026-01-16 11:30:00.883509
10000000009	10000000008	10000000000	0	2026-01-19 10:29:26.604559	10	2024-07-16 03:06:36
10000000079	10000000055	10000000000	0	2026-01-19 10:29:32.373252	2	2026-01-18 19:28:22.97629
10000000080	10000000056	10000000000	0	2026-01-19 10:52:57.470218	1	2026-01-19 10:52:57.470218
\.


--
-- Name: annotations_note_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.annotations_note_id_seq', 41, false);


--
-- Name: answers_answer_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.answers_answer_id_seq', 10000000001, false);


--
-- Name: class_class_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.class_class_id_seq', 10000000001, false);


--
-- Name: collection_items_collection_item_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.collection_items_collection_item_id_seq', 5, true);


--
-- Name: collections_collection_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.collections_collection_id_seq', 2, false);


--
-- Name: comments_comment_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.comments_comment_id_seq', 10000000001, false);


--
-- Name: courses_course_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.courses_course_id_seq', 10000000007, true);


--
-- Name: files_file_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.files_file_id_seq', 10000000056, true);


--
-- Name: questions_question_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.questions_question_id_seq', 10000000001, false);


--
-- Name: reactions_like_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.reactions_like_id_seq', 10000000001, false);


--
-- Name: test_test_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.test_test_id_seq', 10000000001, false);


--
-- Name: third_party_accounts_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.third_party_accounts_id_seq', 10000000001, false);


--
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.users_user_id_seq', 10000000005, false);


--
-- Name: view_records_record_id_seq; Type: SEQUENCE SET; Schema: inver; Owner: ravon
--

SELECT pg_catalog.setval('inver.view_records_record_id_seq', 10000000080, true);


--
-- Name: annotations annotations_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.annotations
    ADD CONSTRAINT annotations_pkey PRIMARY KEY (note_id);


--
-- Name: answers answers_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.answers
    ADD CONSTRAINT answers_pkey PRIMARY KEY (answer_id);


--
-- Name: class class_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.class
    ADD CONSTRAINT class_pkey PRIMARY KEY (class_id);


--
-- Name: collection_items collection_items_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.collection_items
    ADD CONSTRAINT collection_items_pkey PRIMARY KEY (collection_item_id);


--
-- Name: collections collections_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.collections
    ADD CONSTRAINT collections_pkey PRIMARY KEY (collection_id);


--
-- Name: comments comments_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.comments
    ADD CONSTRAINT comments_pkey PRIMARY KEY (comment_id);


--
-- Name: courses courses_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.courses
    ADD CONSTRAINT courses_pkey PRIMARY KEY (course_id);


--
-- Name: files files_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.files
    ADD CONSTRAINT files_pkey PRIMARY KEY (file_id);


--
-- Name: questions question_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.questions
    ADD CONSTRAINT question_pkey PRIMARY KEY (question_id);


--
-- Name: reactions reactions_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.reactions
    ADD CONSTRAINT reactions_pkey PRIMARY KEY (like_id);


--
-- Name: test test_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.test
    ADD CONSTRAINT test_pkey PRIMARY KEY (test_id);


--
-- Name: third_party_accounts third_party_accounts_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.third_party_accounts
    ADD CONSTRAINT third_party_accounts_pkey PRIMARY KEY (third_id);


--
-- Name: annotations uk_user_book_type_cfi; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.annotations
    ADD CONSTRAINT uk_user_book_type_cfi UNIQUE (user_id, book_id, note_type, note_cfi);


--
-- Name: files uq_file_path; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.files
    ADD CONSTRAINT uq_file_path UNIQUE (file_path);


--
-- Name: reactions uq_user_content_action; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.reactions
    ADD CONSTRAINT uq_user_content_action UNIQUE (user_id, related_type, related_id, action);


--
-- Name: users uq_users_email; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.users
    ADD CONSTRAINT uq_users_email UNIQUE (email);


--
-- Name: users uq_users_phone; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.users
    ADD CONSTRAINT uq_users_phone UNIQUE (phone);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: version version_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.version
    ADD CONSTRAINT version_pkey PRIMARY KEY (commit_id);


--
-- Name: view_records view_records_pkey; Type: CONSTRAINT; Schema: inver; Owner: ravon
--

ALTER TABLE ONLY inver.view_records
    ADD CONSTRAINT view_records_pkey PRIMARY KEY (record_id);


--
-- Name: idx_annotations_user_book; Type: INDEX; Schema: inver; Owner: ravon
--

CREATE INDEX idx_annotations_user_book ON inver.annotations USING btree (user_id, book_id);


--
-- Name: idx_collections_user_id; Type: INDEX; Schema: inver; Owner: ravon
--

CREATE INDEX idx_collections_user_id ON inver.collections USING btree (user_id);


--
-- Name: idx_file_course_id; Type: INDEX; Schema: inver; Owner: ravon
--

CREATE INDEX idx_file_course_id ON inver.files USING btree (course_id);


--
-- Name: idx_file_upload_user; Type: INDEX; Schema: inver; Owner: ravon
--

CREATE INDEX idx_file_upload_user ON inver.files USING btree (upload_user);


--
-- Name: idx_item_lookup; Type: INDEX; Schema: inver; Owner: ravon
--

CREATE INDEX idx_item_lookup ON inver.collection_items USING btree (item_id, item_type, collection_id);


--
-- Name: idx_question_submitter_id; Type: INDEX; Schema: inver; Owner: ravon
--

CREATE INDEX idx_question_submitter_id ON inver.questions USING btree (submitter_id);


--
-- Name: idx_question_test_id; Type: INDEX; Schema: inver; Owner: ravon
--

CREATE INDEX idx_question_test_id ON inver.questions USING btree (test_id);


--
-- Name: idx_test_course_id; Type: INDEX; Schema: inver; Owner: ravon
--

CREATE INDEX idx_test_course_id ON inver.test USING btree (course_id);


--
-- Name: idx_third_party_accounts_user_id; Type: INDEX; Schema: inver; Owner: ravon
--

CREATE INDEX idx_third_party_accounts_user_id ON inver.third_party_accounts USING btree (user_id);


--
-- Name: idx_users_class_id; Type: INDEX; Schema: inver; Owner: ravon
--

CREATE INDEX idx_users_class_id ON inver.users USING btree (class_id);


--
-- Name: idx_view_records_file_id; Type: INDEX; Schema: inver; Owner: ravon
--

CREATE INDEX idx_view_records_file_id ON inver.view_records USING btree (file_id);


--
-- Name: idx_view_records_user_id; Type: INDEX; Schema: inver; Owner: ravon
--

CREATE INDEX idx_view_records_user_id ON inver.view_records USING btree (user_id);


--
-- Name: annotations trg_annotations_updated_at; Type: TRIGGER; Schema: inver; Owner: ravon
--

CREATE TRIGGER trg_annotations_updated_at BEFORE UPDATE ON inver.annotations FOR EACH ROW EXECUTE FUNCTION inver.trigger_set_updated_at();


--
-- PostgreSQL database dump complete
--

\unrestrict KT7axBzwGRoCiNBL8JR0a6BSucJiyPY3QLjMwHrct4cz668RCrhD1HR5IhLohlc

