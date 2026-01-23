# LAB 9.01 - Oracle text
## Operator CONTAINS - Podstawy 
### Zadanie 1
```sql
CREATE TABLE CYTATY AS SELECT * FROM ZTPD.CYTATY;
```

### Zadanie 2
```sql
SELECT AUTOR, TEKST 
FROM CYTATY 
WHERE LOWER(TEKST) LIKE '%optymista%' 
AND LOWER(TEKST) LIKE '%pesymista%';
```

### Zadanie 3
```sql
CREATE INDEX cytaty_idx ON CYTATY(TEKST) INDEXTYPE IS CTXSYS.CONTEXT;
```

### Zadanie 4
```sql
SELECT AUTOR, TEKST 
FROM CYTATY 
WHERE CONTAINS(TEKST, 'optymista AND pesymista', 1) > 0;
```

### Zadanie 5
```sql
SELECT AUTOR, TEKST 
FROM CYTATY 
WHERE CONTAINS(TEKST, 'pesymista ~ optymista', 1) > 0;
```

### Zadanie 6
```sql
SELECT AUTOR, TEKST 
FROM CYTATY 
WHERE CONTAINS(TEKST, 'NEAR((optymista, pesymista), 3)', 1) > 0;
```

### Zadanie 7
```sql
SELECT AUTOR, TEKST 
FROM CYTATY 
WHERE CONTAINS(TEKST, 'NEAR((optymista, pesymista), 10)', 1) > 0;
```

### Zadanie 8
```sql
SELECT AUTOR, TEKST 
FROM CYTATY 
WHERE CONTAINS(TEKST, 'życi%', 1) > 0;
```

### Zadanie 9
```sql
SELECT AUTOR, TEKST, SCORE(1) as DOPASOWANIE
FROM CYTATY 
WHERE CONTAINS(TEKST, 'życi%', 1) > 0;
```

### Zadanie 10
```sql
SELECT AUTOR, TEKST, SCORE(1) as DOPASOWANIE
FROM CYTATY 
WHERE CONTAINS(TEKST, 'życi%', 1) > 0
ORDER BY SCORE(1) DESC
FETCH FIRST 1 ROW ONLY;
```

### Zadanie 11
```sql
SELECT AUTOR, TEKST 
FROM CYTATY 
WHERE CONTAINS(TEKST, 'FUZZY(probelm, 60, 2)', 1) > 0;
```

### Zadanie 12
```sql
INSERT INTO CYTATY VALUES (
    (SELECT MAX(ID)+1 FROM CYTATY),
    'Bertrand Russell', 
    'To smutne, że głupcy są tacy pewni siebie, a ludzie rozsądni tacy pełni wątpliwości.'
);
COMMIT;
```

### Zadanie 13
```sql
SELECT AUTOR, TEKST 
FROM CYTATY 
WHERE CONTAINS(TEKST, 'głupcy', 1) > 0;
```

### Zadanie 14
```sql
SELECT TOKEN_TEXT FROM DR$CYTATY_IDX$I WHERE TOKEN_TEXT = 'GŁUPCY';
```

### Zadanie 15
```sql
DROP INDEX cytaty_idx;
CREATE INDEX cytaty_idx ON CYTATY(TEKST) INDEXTYPE IS CTXSYS.CONTEXT;
```

### Zadanie 16
```sql
SELECT AUTOR, TEKST 
FROM CYTATY 
WHERE CONTAINS(TEKST, 'głupcy', 1) > 0;
```

### Zadanie 17
```sql
DROP INDEX cytaty_idx;
DROP TABLE CYTATY;
```

## Zaawansowanie indeksowanie i wyszukiwanie

### Zadanie 1
```sql
CREATE TABLE QUOTES AS SELECT * FROM ZTPD.QUOTES;
```

### Zadanie 2
```sql
CREATE INDEX quotes_idx ON QUOTES(TEXT) INDEXTYPE IS CTXSYS.CONTEXT;
```

### Zadanie 3
```sql
SELECT * FROM QUOTES WHERE CONTAINS(TEXT, 'work', 1) > 0;
SELECT * FROM QUOTES WHERE CONTAINS(TEXT, '$work', 1) > 0;
SELECT * FROM QUOTES WHERE CONTAINS(TEXT, 'working', 1) > 0;
SELECT * FROM QUOTES WHERE CONTAINS(TEXT, '$working', 1) > 0;
```

### Zadanie 4
```sql
SELECT * FROM QUOTES WHERE CONTAINS(TEXT, 'it', 1) > 0;
```

### Zadanie 5
```sql
SELECT * FROM CTX_STOPLISTS;
```

### Zadanie 6
```sql
SELECT * FROM CTX_STOPWORDS;
```

### Zadanie 7
```sql
DROP INDEX quotes_idx;
CREATE INDEX quotes_idx ON QUOTES(TEXT) 
INDEXTYPE IS CTXSYS.CONTEXT 
PARAMETERS ('stoplist CTXSYS.EMPTY_STOPLIST');
```

### Zadanie 8
```sql
SELECT * FROM QUOTES WHERE CONTAINS(TEXT, 'it', 1) > 0;
```

### Zadanie 9
```sql
SELECT * FROM QUOTES WHERE CONTAINS(TEXT, 'fool AND humans', 1) > 0;
```

### Zadanie 10
```sql
SELECT * FROM QUOTES WHERE CONTAINS(TEXT, 'fool AND computer', 1) > 0;
```

### Zadanie 11
```sql
SELECT * FROM QUOTES 
WHERE CONTAINS(TEXT, '(fool AND humans) WITHIN SENTENCE', 1) > 0;
```

### Zadanie 12
```sql
DROP INDEX quotes_idx;
```

### Zadanie 13
```sql
BEGIN
    CTX_DDL.CREATE_SECTION_GROUP('my_sent_group', 'NULL_SECTION_GROUP');
    CTX_DDL.ADD_SPECIAL_SECTION('my_sent_group', 'SENTENCE');
    CTX_DDL.ADD_SPECIAL_SECTION('my_sent_group', 'PARAGRAPH');
END;
/
```

### Zadanie 14
```sql
CREATE INDEX quotes_idx ON QUOTES(TEXT) 
INDEXTYPE IS CTXSYS.CONTEXT 
PARAMETERS ('stoplist CTXSYS.EMPTY_STOPLIST section group my_sent_group');
```

### Zadanie 15
```sql
SELECT * FROM QUOTES WHERE CONTAINS(TEXT, '(fool AND humans) WITHIN SENTENCE', 1) > 0;
SELECT * FROM QUOTES WHERE CONTAINS(TEXT, '(fool AND computer) WITHIN SENTENCE', 1) > 0;
```

### Zadanie 16
```sql
SELECT * FROM QUOTES WHERE CONTAINS(TEXT, 'humans', 1) > 0;
```

### Zadanie 17
```sql
DROP INDEX quotes_idx;

BEGIN
    CTX_DDL.CREATE_PREFERENCE('my_lexer', 'BASIC_LEXER');
    CTX_DDL.SET_ATTRIBUTE('my_lexer', 'printjoins', '-');
END;
/

CREATE INDEX quotes_idx ON QUOTES(TEXT) 
INDEXTYPE IS CTXSYS.CONTEXT 
PARAMETERS ('stoplist CTXSYS.EMPTY_STOPLIST section group my_sent_group LEXER my_lexer');
```

### Zadanie 18
```sql
SELECT * FROM QUOTES WHERE CONTAINS(TEXT, 'humans', 1) > 0;
```

### Zadanie 19
```sql
SELECT * FROM QUOTES WHERE CONTAINS(TEXT, 'non\-humans', 1) > 0;
```

### Zadanie 20
```sql
DROP TABLE QUOTES;
BEGIN
    CTX_DDL.DROP_PREFERENCE('my_lexer');
    CTX_DDL.DROP_SECTION_GROUP('my_sent_group');
END;
/
```