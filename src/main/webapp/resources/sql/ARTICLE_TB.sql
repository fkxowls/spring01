alter table article_tb drop constraint SYS_C007015
CREATE INDEX MEMBER_ID_IDX ON ARTICLE(WRITE_MEMBER_ID)
SELECT INDEX_NAME FROM USER_INDEXES WHERE TABLE_NAME = 'ARTICLE_TB'
SELECT * FROM NOTICE_TB WHERE PARENT_ID > 0
CREATE TABLE ARTICLE_TB
(
    ARTICLE_ID NUMBER(10) PRIMARY KEY
    ,PARENT_ID NUMBER(10) NOT NULL
    ,TITLE VARCHAR2(200) NOT NULL
    ,CONTENT VARCHAR2(1000) NOT NULL
    ,WRITE_DATE DATE NOT NULL
    ,WRITE_MEMBER_ID VARCHAR2(50) NOT NULL
    ,MODIFY_MEMBER_ID  VARCHAR2(50)
    ,MODIFY_DATE DATE
)

select ARTICLE_SEQ.NEXTVAL from dual 
CREATE SEQUENCE ARTICLE_SEQ
 	   START WITH 10001
   	   INCREMENT BY 1
	   MAXVALUE 99999
       MINVALUE 10001
       NOCYCLE;

SELECT * FROM USER_CONSTRAINTS WHERE TABLE_NAME = 'ARTICLE_TB'
DROP SEQUENCE NO_SEQ


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
 	 

INSERT INTO ARTICLE_TB
	       	(
			ARTICLE_ID
			,PARENT_ID
			,TITLE
			,CONTENT
			,WRITE_MEMBER_ID
			,WRITE_DATE
			,MODIFY_DATE
		)
		VALUES
		(
			10110
			,0
			,'dddddd'
			,'sss'
			,'admin'
			,SYSDATE
			,SYSDATE
		)                              
                                       
     SELECT  X.*
        FROM    (
                    SELECT  
                            A.*   
                    FROM    ( 
                                    
                               SELECT  /*+ INDEX_DESC(B ARTICLE_ID)*/
                                      ROWNUM AS RNUM 
                                      , B.ARTICLE_ID
                                      , B.PARENT_ID
                                      , B.TITLE
                                      , B.CONTENT
                                      , B.WRITE_MEMBER_ID
                                      , B.WRITE_DATE
                                FROM    ARTICLE B
                                WHERE   ARTICLE_ID > 0
                                START WITH
                                        PARENT_ID           = 0 
                                CONNECT BY
                                        PRIOR ARTICLE_ID    = B.PARENT_ID 
                                ORDER SIBLINGS BY
--                                                (
--                                                SELECT COUNT(REPLY_ID)
--                                                FROM   ARTICLE_REPLY
--                                                WHERE  ARTICLE_ID = B.ARTICLE_ID
--                                                )DESC
                                                  (
                                                  SELECT ARTICLE_COUNT
                                                  FROM ARTICLE_ORDER
                                                  )DESC
                                
                            ) A
                    WHERE   A.RNUM         <= 80
                ) X
         WHERE  X.RNUM      >= 1
         
         SELECT COUNT(REPLY_ID)  OVER(PARTITION BY ARTICLE_ID) , ARTICLE_ID FROM ARTICLE_REPLY
        
         SELECT COUNT(ARTICLE_ID) FROM ARTICLE_REPLY GROUP BY ARTICLE_ID
         SELECT COUNT(REPLY_ID)  OVER(PARTITION BY ARTICLE_ID) , ARTICLE_ID FROM ARTICLE_REPLY
         SELECT COUNT(ARTICLE_ID) OVER(PARTITION BY ARTICLE_ID ORDER BY REPLY_ID DESC), ARTICLE_ID FROM ARTICLE_REPLY

SELECT  X.*
        FROM    (
                    SELECT  
                            A.*   
                    FROM    ( 
                               SELECT  /*+ INDEX_DESC(A ARTICLE_ID)*/
                                       ROWNUM AS RNUM
                                     , A.ARTICLE_ID
                                     , A.PARENT_ID
                                     , A.TITLE
                                     , A.CONTENT
                                     , A.WRITE_MEMBER_ID
                                     , A.WRITE_DATE
                                FROM   ARTICLE_TB A
                                WHERE  1                   = 1
--                                AND     (#{writeMemberId} IS NULL OR A.WRITE_MEMBER_ID = #{writeMemberId})
                     
                                START WITH
                                        PARENT_ID           = 0 
                                CONNECT BY
                                        PRIOR ARTICLE_ID    = A.PARENT_ID 
                                ORDER SIBLINGS BY
                                       (
                                           SELECT ARTICLE_COUNT
                                           FROM ARTICLE_COUNT_TB
                                       ) DESC
                            ) A
                    WHERE   A.RNUM         <= 80
                ) X
         WHERE  X.RNUM      >= 1
        
                             SELECT    A.ARTICLE_ID
                                     , A.PARENT_ID
                                     , A.TITLE
                                     , A.CONTENT
                                     , A.WRITE_MEMBER_ID
                                     , A.WRITE_DATE
                             FROM    NOTICE_TB N
                                    ,ARTICLE_TB A
                             WHERE   1                   = 1
                             AND     A.ARTICLE_ID        = N.ARTICLE_ID
                             ORDER BY
                                     A.ARTICLE_ID DESC
                                     
                                     
SELECT  
            /*+ INDEX_DESC(C ARTICLE_ID)*/
                C.ARTICLE_ID
               , C.PARENT_ID
               , C.TITLE
               , C.CONTENT
               , C.WRITE_MEMBER_ID
               , C.WRITE_DATE
FROM   ARTICLE_TB C
WHERE 1                 = 1
AND     NOT EXISTS (
                      SELECT 
                              'X'
                      FROM    NOTICE_TB
                      WHERE   ARTICLE_ID = C.ARTICLE_ID
                    )
                    
 SELECT   /*+ ORDERED USE_NL(N A) INDEX_DESC(N NOTICE_TB_PK) INDEX_DESC(A ARTICLE_PK) */
	      T.ARTICLE_ID
	     , T.PARENT_ID
	     , T.TITLE
	     , T.CONTENT
	     , T.WRITE_MEMBER_ID
	     , T.WRITE_DATE
FROM     NOTICE_TB N
        ,ARTICLE_TB T
WHERE  1                   = 1
AND     T.ARTICLE_ID        = N.ARTICLE_ID
AND     SYSDATE           BETWEEN DISPLAY_START_DATE   AND DISPLAY_END_DATE

SELECT * FROM NOTICE_TB
UPDATE NOTICE_TB SET DISPLAY_END_DATE = SYSDATE+15 WHERE NOTICE_ID = '10005'
                                        