SELECT   NVL(MIN('Y'), 'N')
FROM    DUAL
WHERE   1           = 1
AND     EXISTS
        (
            SELECT 
                    1
            FROM    NOTICE_ARTICLE
            WHERE   
                    ARTICLE_ID = '10175'
        )