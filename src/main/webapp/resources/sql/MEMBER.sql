CREATE TABLE MEMBER
(
	MEMBER_ID VARCHAR2(100) PRIMARY KEY,
	MEMBER_PWD VARCHAR2(100) NOT NULL,
	MEMBER_LEVEL NUMBER(5)
)

SELECT	 *
FROM	 MEMBER
WHERE	 MEMBER_ID	= 'admin'

INSERT INTO MEMBER
VALUES
(
	'admin',
	'1',
	'10'
)
;

INSERT INTO MEMBER
VALUES
(
	'mem1',
	'1',
	'20'
)
;

INSERT INTO MEMBER
VALUES
(
	'mem2',
	'1',
	'30'
)
;
DELETE FROM MEMBER
DROP TABLE MEMBER
SELECT *
FROM MEMBER;