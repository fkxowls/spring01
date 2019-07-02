DROP TABLE ARTICLE
DELETE FROM ARTICLE
ALTER TABLE ARTICLE DROP CONSTRAINT FK_ID01
select * from article

SELECT 
				COUNT(ARTICLE_NO)
		FROM	ARTICLE
		WHERE	
				ARTICLE_NO = 155
	

delete from article
where article_No = (select article_No from ARTICLE_REPLY where article_No = '120' )


SELECT INDEX_NAME FROM USER_INDEXES WHERE TABLE_NAME = 'ARTICLE';


SELECT RN,ARTICLE_NO 
FROM
(
	SELECT /*+ INDEX_DESC(ARTICLE ARTICLE_NO)*/ ROWNUM AS RN,ARTICLE_NO
	FROM ARTICLE
	WHERE ARTICLE_NO > 0
	AND ROWNUM <= 50
) 
WHERE RN > 10
;

WHERE 
CREATE TABLE ARTICLE
(
	ARTICLE_NO NUMBER(10) PRIMARY KEY
	,PARENT_NO NUMBER(10)
	,TITLE VARCHAR2(200) NOT NULL
	,CONTENT VARCHAR2(1000) NOT NULL
	,WRITE_DATE DATE DEFAULT SYSDATE NOT NULL
	,WRITE_MEMBER_ID VARCHAR2(50) NOT NULL
	,CONSTRAINT FK_ID01 FOREIGN KEY(WRITE_MEMBER_ID) REFERENCES MEMBER(MEMBER_ID)
)
;

ALTER TABLE ARTICLE ADD FOREIGN KEY(WRITE_MEMBER_ID) REFERENCES MEMBER(MEMBER_ID)


CREATE SEQUENCE NO_SEQ
 	   START WITH 1
   	   INCREMENT BY 1
	   MAXVALUE 10000
       MINVALUE 0
       NOCYCLE;

DROP SEQUENCE NO_SEQ



insert into ARTICLE
values
(
	NO_SEQ.NEXTVAL
	,21
	,'ARTICLE22'
	,'ARTICLE22.'
	,sysdate
	,'admin'
)


insert into ARTICLE
values
(
	NO_SEQ.NEXTVAL
	,0
	,'ARTICLE02'
	,'ARTICLE02.'
	,sysdate
	,'mem2'
)


insert into ARTICLE
values
(
	NO_SEQ.NEXTVAL
	,0
	,'ffffff'
	,'ffffff.'
	,sysdate
	,'mem1'
)


insert into ARTICLE
values
(
	NO_SEQ.NEXTVAL
	,3
	,'ARTICLE04'
	,'ARTICLE04.'
	,sysdate
	,'admin'
)


insert into ARTICLE
values
(
	NO_SEQ.NEXTVAL
	,2
	,'ARTICLE05'
	,'ARTICLE05.'
	,sysdate
	,'admin'
)

	SELECT
      		  A.*
		FROM    (
            SELECT	ROWNUM as rNum
                    ,LEVEL AS LVL
                    , ARTICLE_NO
                    , PARENT_NO
                    , TITLE
                    , CONTENT
                    , WRITE_MEMBER_ID
                    , WRITE_DATE
            FROM    ARTICLE
			WHERE  ARTICLE_NO > 0  
            START WITH
                    PARENT_NO           = 0 
            CONNECT BY
                    PRIOR ARTICLE_NO    = PARENT_NO 
            ORDER SIBLINGS BY
                    ARTICLE_NO DESC
       		 ) A
		WHERE   rNum  BETWEEN 1  AND 90
		
	<-- X -->
	SELECT * FROM (
		SELECT /*+ INDEX_DESC(ARTICLE ARTICLE_NO)*/ ROWNUM AS RNUM, Z.* FROM(
			SELECT * FROM ARTICLE
		)Z WHERE ROWNUM <=21
	)WHERE RNUM >= 11
		
	
	SELECT 	 X.*
             
 	FROM (
 			SELECT  
 					ROWNUM AS RNUM
 				    , A.*      
         	 FROM (
         	 		SELECT    /*+ INDEX_DESC(ARTICLE ARTICLE_NO)*/
         	 				   ARTICLE_NO
            				 , PARENT_NO
            				 , TITLE
           					 , CONTENT
           					 , WRITE_MEMBER_ID
            		 		 
            		FROM ARTICLE
         	 	  ) A
 	 		 WHERE ROWNUM <= #{endNum}
 	 	  ) X
 	 WHERE   X.RNUM >= #{startNum}
		
 	  SELECT	ROWNUM as rNum
                    ,LEVEL AS LVL
                    , ARTICLE_NO
                    , PARENT_NO
                    , TITLE
                    , CONTENT
                    , WRITE_MEMBER_ID
                    , WRITE_DATE 
            FROM    ARTICLE
			WHERE   ARTICLE_NO > 0
            START WITH
                    PARENT_NO           = 0 
            CONNECT BY
                    PRIOR ARTICLE_NO    = PARENT_NO 
            ORDER SIBLINGS BY
                    ARTICLE_NO DESC
DELETE 	
		FROM	ARTICLE
		WHERE	ARTICLE_NO = 10
