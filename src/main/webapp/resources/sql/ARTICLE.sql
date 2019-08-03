ALTER TABLE ARTICLE ADD CONSTRAINT WRITE_MEMBER_FK FOREIGN KEY(WRITE_MEMBER_ID) REFERENCES MEMBER(MEMBER_ID)
DROP TABLE ARTICLE
delete from article
ALTER TABLE ARTICLE ADD(NOTICE_CHK_FLAG NUMBER(1));
SELECT * FROM ARTICLE
ALTER TABLE ARTICLE MODIFY (WRITE_DATE DEFAULT null);
SELECT * FROM USER_CONSTRAINTS WHERE TABLE_NAME = 'ARTICLE'
ALTER TABLE ARTICLE DROP CONSTRAINT SYS_C008213;
CREATE INDEX MEMBER_ID_IDX ON ARTICLE(WRITE_MEMBER_ID)
SELECT INDEX_NAME FROM USER_INDEXES WHERE TABLE_NAME = 'ARTICLE'
--NL조인
SELECT  /*+ ordered use_nl(t2)*/
		T2.*
FROM 	NOTICE_ARTICLE T1, ARTICLE T2
WHERE	
		T1.ARTICLE_ID = T2.ARTICLE_ID
		;
		
SELECT /*+ INDEX(MEMBER_ID_IDX)*/
		* 
FROM ARTICLE
WHERE WRITE_MEMBER_ID = 'admin'

SELECT 
		*
FROM 	ARTICLE
WHERE  	1 = 1
and		ARTICLE_ID = 10072
AND		EXISTS
				(
					SELECT 
							1
					FROM	MEMBER
					WHERE	
							MEMBER_ID = 'admin'
					AND		MEMBER_LEVEL = 10
				)
--세미조인		
SELECT 	A.TITLE
FROM  	ARTICLE A
WHERE  	EXISTS
			   (	SELECT *
					FROM NOTICE_ARTICLE B
					WHERE A.ARTICLE_ID = B.ARTICLE_ID
					AND B.NOTICE_ID > 0
			    )

--Multi Insert

			    
SELECT  /*+ ordered use_nl(B)*/
	 A.* 
FROM	 ARTICLE A
		,NOTICE_ARTICLE B
WHERE A.ARTICLE_ID = B.ARTICLE_ID
AND   B.NOTICE_ID = '10054'; 

NOTICE_ID_SEQ.nextval;
SELECT * FROM  ARTICLE
CREATE TABLE ARTICLE
(
	ARTICLE_ID NUMBER(10) PRIMARY KEY
	,PARENT_ID NUMBER(10)
	,TITLE VARCHAR2(200) NOT NULL
	,CONTENT VARCHAR2(1000) NOT NULL
	,WRITE_DATE DATE DEFAULT SYSDATE NOT NULL
	,WRITE_MEMBER_ID VARCHAR2(50) NOT NULL
	,MODIFY_MEMBER_ID  VARCHAR2(50)
	,MODIFY_DATE DATE DEFAULT SYSDATE NOT NULL
)
CONSTRAINT FK_ID01 FOREIGN KEY(WRITE_MEMBER_ID) REFERENCES MEMBER(MEMBER_ID)
;

ALTER TABLE ARTICLE ADD FOREIGN KEY(WRITE_MEMBER_ID) REFERENCES MEMBER(MEMBER_ID)


CREATE SEQUENCE NO_SEQ
 	   START WITH 10001
   	   INCREMENT BY 1
	   MAXVALUE 99999
       MINVALUE 10001
       NOCYCLE;

DROP SEQUENCE NO_SEQ


SELECT RN,ARTICLE_ID
FROM
(
	SELECT /*+ INDEX_DESC(ARTICLE ARTICLE_ID)*/ ROWNUM AS RN,ARTICLE_ID
	FROM ARTICLE
	WHERE ARTICLE_ID > 0
	AND ROWNUM <= 50
) 
WHERE RN > 10
;

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
		  WHERE   ARTICLE_NO > 0  
          START WITH
                    PARENT_NO           = 0 
          CONNECT BY
                    PRIOR ARTICLE_NO    = PARENT_NO 
          ORDER     SIBLINGS BY
                    ARTICLE_NO DESC
       	) A
WHERE   rNum  BETWEEN 1  AND 90
		
	<-- X -->
	SELECT * FROM (
		SELECT /*+ INDEX_DESC(ARTICLE ARTICLE_OD)*/ ROWNUM AS RNUM, Z.* FROM(
			SELECT * FROM ARTICLE
		)Z WHERE ROWNUM <=21
	)WHERE RNUM >= 11
		
	
SELECT 	 X.*
             
 	FROM (
 			SELECT  /*+ INDEX_DESC(SYS_C008216)*/
 					ROWNUM AS RNUM
 				    , A.*      
         	 FROM (
         	 		SELECT    ARTICLE_ID
            				 , PARENT_ID
            				 , TITLE
           					 , CONTENT
           					 , WRITE_MEMBER_ID
            		 		 
            		FROM ARTICLE
            		ORDER BY ARTICLE_ID DESC
         	 	  ) A
 	 		 WHERE ROWNUM <= 10
 	 	  ) X
 	 WHERE   X.RNUM >= 1
 	 
SELECT  X.*
        FROM    (
                    SELECT  
                            A.*   
                    FROM    (  -- TODO WRITE_MEMBER_ID 에 인덱스 추가 (ARTICLE_ID, WRITE_MEMBER_ID 이거 두 컬럼으로) -- 모수가 ARTICLE과 같거나, ARTICLE에서 비중이 많을 때     
                               SELECT  /*+ INDEX_DESC(A ARTICLE_PK)*/
                                       ROW_NUMBER() OVER(PARTITION BY A.ARTICLE_ID ORDER BY A.ARTICLE_ID DESC) AS RNUM
                                      , A.ARTICLE_ID
                                      , A.PARENT_ID
                                      , A.TITLE
                                      , A.CONTENT
                                      , A.WRITE_MEMBER_ID
                                FROM    ARTICLE A
                                WHERE   1                   = 1
                                AND     (#{writeMemberId}   IS NULL OR A.WRITE_MEMBER_ID = #{writeMemberId})<!--   인덱스 추가필요 (ARTICLE에서 WRITE_MEMBER_ID가 중복되는 케이스는 많다) -->
                               <!--  AND     (#{title}           IS NULL OR A.TITLE LIKE '%'||#{title}||'%') --><!--   인덱스 추가 무의미 (ARTICLE에서 TITLE가 중복되는 케이스는 없다고 봐도 무방) -->
                            ) A
                    WHERE   A.RNUM         <= #{endNum:INTEGER}
                ) X
         WHERE  X.RNUM      >= #{startNum:INTEGER}
 	 
SELECT	 NVL(MIN('Y'), 'N')
FROM 	DUAL
WHERE EXISTS
(
		SELECT 
				1
		FROM	ARTICLE
		WHERE	
				WRITE_MEMBER_ID = 'mem2'
		AND		ARTICLE_NO = 871
); 

INSERT INTO ARTICLE
		(
			ARTICLE_ID
			,PARENT_ID
			,TITLE
			,NOTICE_CHK_FLAG
			,CONTENT
			,WRITE_MEMBER_ID
			,WRITE_DATE
		)
		VALUES
		(
			10110
			,0
			,'dddddd'
			,1
			,'sss'
			,'admin'
			,SYSDATE
		)
SELECT 
				* 
FROM 	ARTICLE _reply
WHERE 	ARTICLE_ID = '10086'

SELECT  X.*
        FROM    (   SELECT  A.*   
                    FROM    ( 
                                SELECT  /*+ INDEX_DESC(A ARTICLE_PK)*/
                                        ROW_NUMBER() OVER(ORDER BY ARTICLE_ID DESC) AS RNUM  
                                      , A.ARTICLE_ID
                                      , A.PARENT_ID
                                      , A.TITLE
                                      , A.CONTENT
                                      , A.WRITE_MEMBER_ID
                                FROM    ARTICLE A
                            ) A
                    WHERE   RNUM         <= 10
                               
                ) X
         WHERE  X.RNUM      >= 1
         
SELECT   X.*
             
    FROM (
            SELECT  /*+ INDEX_DESC(ARTICLE ARTICLE_NO)*/
                    ROWNUM AS RNUM
                    , A.*      
             FROM (
                    SELECT    ARTICLE_ID
                             , PARENT_ID
                             , TITLE
                             , CONTENT
                             , WRITE_MEMBER_ID
                             
                    FROM ARTICLE
                    ORDER BY ARTICLE_ID desc
                  ) A
             WHERE ROWNUM <= 10
          ) X
     WHERE   X.RNUM >= 1      
     
SELECT   X.*
             
    FROM (
            SELECT  /*+ INDEX_DESC(ARTICLE ARTICLE_NO)*/
                    ROWNUM AS RNUM
                    , A.*      
             FROM (
                    SELECT    ARTICLE_ID
                             , PARENT_ID
                             , TITLE
                             , CONTENT
                             , WRITE_MEMBER_ID
                             
                    FROM ARTICLE
                    ORDER BY ARTICLE_ID DESC
                  ) A
             WHERE ROWNUM <= #{endNum}
          ) X
     WHERE   X.RNUM >= #{startNum}     