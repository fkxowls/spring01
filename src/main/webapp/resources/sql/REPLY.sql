ALTER TABLE ARTICLE_REPLY DROP CONSTRAINT FK_ARTICLE_NO
DELETE FROM ARTICLE_REPLY
DROP TABLE ARTICLE_REPLY
ALTER TABLE ARTICLE_REPLY ADD FOREIGN KEY(ARTICLE_NO) REFERENCES ARTICLE(ARTICLE_NO)
SELECT * FROM ARTICLE_REPLY WHERE ARTICLE_ID = 10004
SELECT CONSTRAINT_NAME, STATUS
FROM ALL_CONSTRAINTS
WHERE TABLE_NAME = 'ARTICLE_REPLY';
ALTER TABLE ARTICLE_REPLY ADD(SECRET_TYPE_CD NUMBER(3));
ALTER TABLE ARTICLE_REPLY MODIFY (SECRET_CHK_FLAG DEFAULT 0);
ALTER TABLE ARTICLE_REPLY RENAME COLUMN PARENT_IO TO PARENT_ID;
ALTER TABLE ARTICLE_REPLY RENAME COLUMN REPLY_NO TO REPLY_ID;
ALTER TABLE ARTICLE_REPLY DROP CONSTRAINT FK_ARTICLE_NO;
select * from ARTICLE_REPLY where article_id = 10083

update article_reply set SECRET_TYPE_CD = 10 where reply_id = 125

CREATE TABLE ARTICLE_REPLY
(
	REPLY_ID NUMBER(10)	PRIMARY KEY
	,ARTICLE_ID NUMBER(10) NOT NULL
	,PARENT_ID NUMBER(10) DEFAULT 0
	,CONTENT VARCHAR2(500) NOT NULL
	,WRITE_MEMBER_ID VARCHAR2(50) NOT NULL
	,WRITE_DATE DATE DEFAULT SYSDATE
	,MODIFY_MEMBER_ID  VARCHAR2(50)
	,MODIFY_DATE DATE DEFAULT SYSDATE NOT NULL
	,SECRET_CHK_FLAG NUMBER(1) DEFAULT 0
	,CONSTRAINT FK_ARTICLE_NO FOREIGN KEY(ARTICLE_ID) REFERENCES ARTICLE(ARTICLE_ID)
)
;

CREATE SEQUENCE COMMENT_SEQ
 	   START WITH 3
   	   INCREMENT BY 1
	   MAXVALUE 10000
       MINVALUE 0
       NOCYCLE;

SELECT
  	    		  A.*
			FROM    (
  	          SELECT	ROWNUM as rNum
    	                ,LEVEL AS LVL
        	            , ARTICLE_NO
            	        , REPLY_NO
                	    , PARENT_NO
                    	, CONTENT
	                    , WRITE_MEMBER_ID
    	               
        	    FROM    ARTICLE_REPLY
            	START WITH
                	    PARENT_NO           = 0 
            	CONNECT BY
            	        PRIOR ARTICLE_NO    = PARENT_NO 
	            ORDER SIBLINGS BY
    	                ARTICLE_NO DESC
       			 ) A
       			 WHERE ARTICLE_NO = 119
       
DROP SEQUENCE COMMENT_SEQ

INSERT INTO ARTICLE_REPLY
VALUES
(
	2
	,31
	,0
	,'Comment Test1'
	,'admin'
	,sysdate
)
;

insert into board_reply(replyNO,articleNO,r_parentNO,replyID,r_content,writedate)
	values(1,1,0,'admin','댓글1',sysdate);

SELECT
  	    		  A.*
			FROM    (
  	          SELECT	ROWNUM as rNum
    	                ,LEVEL
        	            , ARTICLE_NO
            	        , REPLY_NO
                	    , PARENT_NO
                    	, CONTENT
	                    , WRITE_MEMBER_ID
    	               
        	    FROM    ARTICLE_REPLY
            	START WITH
                	    PARENT_NO           = 0 
            	CONNECT BY
            	        PRIOR ARTICLE_NO    = PARENT_NO 
	            ORDER SIBLINGS BY
    	                ARTICLE_NO DESC
       			 ) A
       			 WHERE ARTICLE_NO = 22
	
	
SELECT
  	    		  A.*
			FROM    (
  	          SELECT	ROWNUM as rNum
    	                ,LEVEL AS LVL
        	            , ARTICLE_NO
            	        , REPLY_NO
                	    , PARENT_NO
                    	, CONTENT
	                    , WRITE_MEMBER_ID
    	               
        	    FROM    ARTICLE_REPLY
            	START WITH
                	    PARENT_NO           = 0 
            	CONNECT BY
            	        PRIOR ARTICLE_NO    = PARENT_NO 
	            ORDER SIBLINGS BY
    	                ARTICLE_NO DESC
       			 ) A
       			 WHERE ARTICLE_NO in (
	       			 SELECT regexp_substr(10004, '[^,]+', 1, LEVEL)
					FROM DUAL
					CONNECT BY LEVEL = length(regexp_replace(10004, '[^,]+', '')) + 1
       			 )	
	
       			 
       			 
SELECT
COMMENT_SEQ.NEXTVAL
FROM DUAL

	INSERT INTO ARTICLE_REPLY
		(
			REPLY_ID
			,ARTICLE_ID
			,PARENT_ID
			,CONTENT
			,WRITE_MEMBER_ID
			,SECRET_CHK_FLAG
			,WRITE_DATE
		)
		VALUES
		(
		 	 COMMENT_SEQ.NEXTVAL
		 	 ,'10087'
			 ,'0'
			 ,'tttt'
			 ,'admin'
			 ,1
			 ,sysdate
		)
SELECT	 A.*
		FROM      (
  	                  SELECT	
        	                    B.*
    	               
        	   		  FROM     (
        	   		  			SELECT   ROW_NUMBER() OVER(PARTITION BY ARTICLE_ID  ORDER BY REPLY_ID DESC) AS RANK_NUMBER
  	                  		   			, LEVEL AS LVL
        	   		  					, ARTICLE_ID
            	        	   			, REPLY_ID
	                         	  		, PARENT_ID
                    		 	 	    , CONTENT
	                         	  		, WRITE_MEMBER_ID
	                          			, SECRET_CHK_FLAG
	                          	FROM     ARTICLE_REPLY
       				 			WHERE    ARTICLE_ID in (
	       												SELECT regexp_substr('10087', '[^,]+', 1, LEVEL)
														FROM DUAL
														CONNECT BY LEVEL >= length(regexp_replace('10087', '[^,]+', '')) + 1
       			 						     			) 
            					CONNECT BY
            	    		  			 PRIOR ARTICLE_ID    = PARENT_ID 
        	   				  ) B
		where  B.RANK_NUMBER <=10
        	   				  
       			 ) A	
WHERE  A.RANK_NUMBER >= 1

SELECT   A.*
        FROM      (
                      SELECT    
                                B.*
                       
                      FROM     (
                                SELECT   ROW_NUMBER() OVER(PARTITION BY ARTICLE_ID  ORDER BY REPLY_ID DESC) AS RANK_NUMBER
                                        , LEVEL AS LVL
                                        , ARTICLE_ID
                                        , REPLY_ID
                                        , PARENT_ID
                                        , CONTENT
                                        , WRITE_MEMBER_ID
                                        , SECRET_CHK_FLAG
                                        <!-- /* IS_SRECET(boolean-true,false) , SECRET_YN(String-"Y","N"), SECRET_TYPE_CD(String-"10","20") S */ -->
                                FROM     ARTICLE_REPLY
                                WHERE    ARTICLE_ID in (
                                                            SELECT regexp_substr('10087', '[^,]+', 1, LEVEL)
                                                            FROM DUAL
                                                            CONNECT BY LEVEL >= length(regexp_replace('10087', '[^,]+', '')) + 1
                                                        ) 
                                CONNECT BY
                                         PRIOR ARTICLE_ID   = PARENT_ID 
                              ) B
                    where  B.RANK_NUMBER <= 10
                              
                 ) A    
        WHERE  A.RANK_NUMBER >= 1