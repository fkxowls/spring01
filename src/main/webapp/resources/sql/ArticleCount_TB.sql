CREATE TABLE ARTICLEcOUNT_TB
(
    ARTICLE_ID NUMBER(10) PRIMARY KEY
    ,ARTICLE_COUNT NUMBER(10)
)

INSERT INTO ARTICLECOUNT_TB VALUES(10179, 1)
SELECT ARTICLE_ID FROM ARTICLECOUNT_TB

SELECT ARTICLE_ID, ARTICLE_COUNT
FROM ARTICLECOUNT_TB