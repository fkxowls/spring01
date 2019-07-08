ALTER TABLE ARTICLE_REPLY DROP CONSTRAINT FK_ARTICLE_NO
DELETE FROM ARTICLE_REPLY
ALTER TABLE ARTICLE_REPLY ADD FOREIGN KEY(ARTICLE_NO) REFERENCES ARTICLE(ARTICLE_NO)
SELECT * FROM ARTICLE_REPLY WHERE ARTICLE_NO = 108
CREATE TABLE ARTICLE_REPLY
(
	REPLY_NO NUMBER(10)	PRIMARY KEY
	,ARTICLE_NO NUMBER(10) NOT NULL
	,PARENT_NO NUMBER(10) DEFAULT 0
	,CONTENT VARCHAR2(500) NOT NULL
	,WRITE_MEMBER_ID VARCHAR2(50) NOT NULL
	,WRITE_DATE DATE DEFAULT SYSDATE
	,CONSTRAINT FK_ARTICLE_NO FOREIGN KEY(ARTICLE_NO) REFERENCES ARTICLE(ARTICLE_NO)
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
	
	
	
	
	
	
	
	
	