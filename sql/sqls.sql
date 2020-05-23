/*
  휴지통 만드는 create 문
 */
DROP TABLE TRASH;

CREATE TABLE TRASH
(
    ID INTEGER DEFAULT
    MESSAGE_NAME       VARCHAR(200) NOT NULL,
    REPOSITORY_NAME    VARCHAR(100) NOT NULL,
    MESSAGE_STATE      VARCHAR(30),
    ERROR_MESSAGE      VARCHAR(200) NOT NULL,
    SENDER             VARCHAR(255) NOT NULL,
    RECIPIENTS         TEXT,
    REMOTE_HOST        VARCHAR(255),
    REMOTE_ADDR        VARCHAR(20),
    MESSAGE_BODY       LONGBLOB,
    MESSAGE_ATTRIBUTES LONGBLOB,
    LAST_UPDATED       DATETIME,
    PRIMARY KEY (MESSAGE_NAME)
);
/**
 보낸 메일함
 */
DROP TABLE SENDEDMESSAGES;
CREATE TABLE SENDEDMESSAGES
(
    MESSAGE_ID  VARCHAR(45)  NOT NULL, /* 메일을 구별하기 위해 id가 필요함 자동으로 만들어주니까 신경 안 써도 됨*/
    USERID      VARCHAR(255) NOT NULL, /* 메일 쓴 사람 아이디 */
    TOUSER      TEXT, /* 보내는 대상 */
    CC          TEXT, /* 이메일 참조라고 함? 같이 받는 사람인 거 같음 */
    SUBJECT     TEXT,/* 메일 제목 */
    BODY        TEXT,/* 메일 내용 */
    FILENAME    VARCHAR(255),
    FILE        LONGBLOB, /* 첨부 파일*/
    SENDED_TIME DATETIME DEFAULT NOW(),
    PRIMARY KEY (MESSAGE_ID, USERID)
);
COMMIT;