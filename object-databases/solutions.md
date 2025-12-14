# LAB 10.10 - Obiektowe bazy danych

## Zadanie 1
```sql
CREATE TYPE samochod AS OBJECT (
    marka varchar2(20),
    model varchar2(20),
    kilometry number,
    data_produkcji date,
    cena number(10,2)
);

CREATE TABLE SAMOCHODY OF SAMOCHOD;

INSERT INTO SAMOCHODY VALUES (NEW SAMOCHOD('FIAT', 'BRAVA', 60000, TO_DATE('30-11-1999', 'DD-MM-YYYY'), 25000));
INSERT INTO SAMOCHODY VALUES (NEW SAMOCHOD('FORD', 'MONDEO', 80000, TO_DATE('10-05-1997', 'DD-MM-YYYY'), 45000));
INSERT INTO SAMOCHODY VALUES (NEW SAMOCHOD('MAZDA', '323', 12000, TO_DATE('22-09-2000', 'DD-MM-YYYY'), 52000));
```

## Zadanie 2
```sql
ALTER TYPE SAMOCHOD ADD MEMBER FUNCTION wartosc RETURN NUMBER CASCADE;

CREATE OR REPLACE TYPE BODY SAMOCHOD AS
    MEMBER FUNCTION wartosc RETURN NUMBER IS
        wiek NUMBER;
        nowa_cena NUMBER;
    BEGIN
        wiek := EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM DATA_PRODUKCJI);
        nowa_cena := CENA * POWER(0.9, wiek);
        RETURN ROUND(nowa_cena, 2);
    END wartosc;
END;
```

## Zadanie 3
```sql
ALTER TYPE SAMOCHOD ADD MAP MEMBER FUNCTION odwzoruj RETURN NUMBER CASCADE INCLUDING TABLE DATA;

CREATE OR REPLACE TYPE BODY SAMOCHOD AS
    MEMBER FUNCTION wartosc RETURN NUMBER IS
        wiek NUMBER;
    BEGIN
        wiek := EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM DATA_PRODUKCJI);
        RETURN ROUND(self.CENA * POWER(0.9, wiek), 2);
    END wartosc;

    MAP MEMBER FUNCTION odwzoruj RETURN NUMBER IS
        wiek_lata NUMBER;
        zuzycie_z_km NUMBER;
    BEGIN
        wiek_lata := EXTRACT(YEAR FROM SYSDATE) - EXTRACT(YEAR FROM DATA_PRODUKCJI);
        zuzycie_z_km := KILOMETRY / 10000;
        RETURN wiek_lata + zuzycie_z_km;
    END odwzoruj;
END;
```

## Zadanie 4
```sql
CREATE TABLE WLASCICIELE (
    IMIE VARCHAR2(100),
    NAZWISKO VARCHAR2(100),
    AUTO SAMOCHOD
);

INSERT INTO WLASCICIELE VALUES (
    'Szymon', 'Skowronski', 
    NEW SAMOCHOD('Kia', 'Rio', 30000, TO_DATE('02-12-2020', 'DD-MM-YYYY'), 19500)
);

SELECT * FROM WLASCICIELE;
```

## Zadanie 5
```sql
DROP TABLE WLASCICIELE;
```

## Zadanie 6
```sql
CREATE TYPE WLASCICIEL AS OBJECT (
    IMIE VARCHAR2(100),
    NAZWISKO VARCHAR2(100)
);

CREATE TABLE WLASCICIELE OF WLASCICIEL;

INSERT INTO WLASCICIELE VALUES (NEW WLASCICIEL('Szymon', 'Skowronski'));
INSERT INTO WLASCICIELE VALUES (NEW WLASCICIEL('Filip', 'Laskowski'));

SELECT * FROM WLASCICIELE;
```

## Zadanie 7
```sql
ALTER TYPE SAMOCHOD ADD ATTRIBUTE wlasciciel_ref REF WLASCICIEL CASCADE;
```

## Zadanie 8
```sql
DELETE FROM SAMOCHODY;
```

## Zadanie 9
```sql
ALTER TABLE SAMOCHODY ADD SCOPE FOR (wlasciciel_ref) IS WLASCICIELE;
```

## Zadanie 10
```sql
INSERT INTO SAMOCHODY VALUES (NEW SAMOCHOD('FIAT', 'BRAVA', 60000, TO_DATE('30-11-1999', 'DD-MM-YYYY'), 25000, null));
INSERT INTO SAMOCHODY VALUES (NEW SAMOCHOD('FORD', 'MONDEO', 80000, TO_DATE('10-05-1997', 'DD-MM-YYYY'), 45000, null));
INSERT INTO SAMOCHODY VALUES (NEW SAMOCHOD('MAZDA', '323', 12000, TO_DATE('22-09-2000', 'DD-MM-YYYY'), 52000, null));

UPDATE SAMOCHODY 
SET wlasciciel_ref = (SELECT REF(w) FROM wlasciciele w WHERE w.nazwisko = 'Skowronski');

SELECT s.MARKA, s.wlasciciel_ref.NAZWISKO FROM SAMOCHODY s;
```

## Zadanie 11
```sql
DECLARE 
    TYPE t_przedmioty IS VARRAY(10) OF VARCHAR2(20); 
    moje_przedmioty t_przedmioty := t_przedmioty(''); 
BEGIN 
    moje_przedmioty(1) := 'MATEMATYKA'; 
    moje_przedmioty.EXTEND(9); 
    
    FOR i IN 2..10 LOOP 
        moje_przedmioty(i) := 'PRZEDMIOT_' || i; 
    END LOOP; 
    
    FOR i IN moje_przedmioty.FIRST()..moje_przedmioty.LAST() LOOP 
        DBMS_OUTPUT.PUT_LINE(moje_przedmioty(i)); 
    END LOOP; 
    
    moje_przedmioty.TRIM(2); 
    
    FOR i IN moje_przedmioty.FIRST()..moje_przedmioty.LAST() LOOP 
        DBMS_OUTPUT.PUT_LINE(moje_przedmioty(i)); 
    END LOOP; 
    
    DBMS_OUTPUT.PUT_LINE('Limit: ' || moje_przedmioty.LIMIT()); 
    DBMS_OUTPUT.PUT_LINE('Liczba elementow: ' || moje_przedmioty.COUNT()); 
    
    moje_przedmioty.EXTEND(); 
    moje_przedmioty(9) := 9; 
    
    DBMS_OUTPUT.PUT_LINE('Limit: ' || moje_przedmioty.LIMIT()); 
    DBMS_OUTPUT.PUT_LINE('Liczba elementow: ' || moje_przedmioty.COUNT()); 
    
    moje_przedmioty.DELETE(); 
    
    DBMS_OUTPUT.PUT_LINE('Limit: ' || moje_przedmioty.LIMIT()); 
    DBMS_OUTPUT.PUT_LINE('Liczba elementow: ' || moje_przedmioty.COUNT()); 
END; 
```

## Zadanie 12
```sql
DECLARE
    TYPE t_ksiazki IS VARRAY(10) OF VARCHAR2(100);
    moje_ksiazki t_ksiazki := t_ksiazki();
    i NUMBER;
BEGIN
    moje_ksiazki.EXTEND(3);
    
    moje_ksiazki(1) := 'Wiedźmin: Ostatnie Życzenie';
    moje_ksiazki(2) := 'Pan Tadeusz';
    moje_ksiazki(3) := 'Lalka';

    FOR i IN moje_ksiazki.FIRST..moje_ksiazki.LAST LOOP
        DBMS_OUTPUT.PUT_LINE(moje_ksiazki(i));
    END LOOP;

    moje_ksiazki.EXTEND; 
    moje_ksiazki(4) := 'Harry Potter';
    
    DBMS_OUTPUT.PUT_LINE('Dodano: ' || moje_ksiazki(4));
    DBMS_OUTPUT.PUT_LINE('Liczba elementów: ' || moje_ksiazki.COUNT);
    
    moje_ksiazki.TRIM(1);
    
    FOR i IN moje_ksiazki.FIRST..moje_ksiazki.LAST LOOP
        DBMS_OUTPUT.PUT_LINE(moje_ksiazki(i));
    END LOOP;
END;
```

## Zadanie 13
```sql
DECLARE 
    TYPE t_wykladowcy IS TABLE OF VARCHAR2(20); 
    moi_wykladowcy t_wykladowcy := t_wykladowcy(); 
BEGIN 
    moi_wykladowcy.EXTEND(2); 
 
    moi_wykladowcy(1) := 'MORZY'; 
    moi_wykladowcy(2) := 'WOJCIECHOWSKI'; 
 
    moi_wykladowcy.EXTEND(8); 
 
    FOR i IN 3..10 LOOP 
        moi_wykladowcy(i) := 'WYKLADOWCA_' || i; 
    END LOOP; 
 
    FOR i IN moi_wykladowcy.FIRST()..moi_wykladowcy.LAST() LOOP 
        DBMS_OUTPUT.PUT_LINE(moi_wykladowcy(i)); 
    END LOOP; 
 
    moi_wykladowcy.TRIM(2); 
 
    FOR i IN moi_wykladowcy.FIRST()..moi_wykladowcy.LAST() LOOP 
        DBMS_OUTPUT.PUT_LINE(moi_wykladowcy(i)); 
    END LOOP; 
 
    moi_wykladowcy.DELETE(5,7); 
 
    DBMS_OUTPUT.PUT_LINE('Limit: ' || moi_wykladowcy.LIMIT()); 
    DBMS_OUTPUT.PUT_LINE('Liczba elementow: ' || moi_wykladowcy.COUNT()); 
 
    FOR i IN moi_wykladowcy.FIRST()..moi_wykladowcy.LAST() LOOP 
        IF moi_wykladowcy.EXISTS(i) THEN 
            DBMS_OUTPUT.PUT_LINE(moi_wykladowcy(i)); 
        END IF; 
    END LOOP; 
 
    moi_wykladowcy(5) := 'ZAKRZEWICZ'; 
    moi_wykladowcy(6) := 'KROLIKOWSKI'; 
    moi_wykladowcy(7) := 'KOSZLAJDA'; 
  
    FOR i IN moi_wykladowcy.FIRST()..moi_wykladowcy.LAST() LOOP 
        IF moi_wykladowcy.EXISTS(i) THEN 
            DBMS_OUTPUT.PUT_LINE(moi_wykladowcy(i)); 
        END IF; 
    END LOOP; 
 
    DBMS_OUTPUT.PUT_LINE('Limit: ' || moi_wykladowcy.LIMIT()); 
    DBMS_OUTPUT.PUT_LINE('Liczba elementow: ' || moi_wykladowcy.COUNT()); 
END; 
```

## Zadanie 14
```sql
DECLARE
    TYPE t_miesiace IS TABLE OF VARCHAR2(20);
    moje_miesiace t_miesiace := t_miesiace(
        'Styczeń', 'Luty', 'Marzec', 'Kwiecień', 'Maj', 'Czerwiec',
        'Lipiec', 'Sierpień', 'Wrzesień', 'Październik', 'Listopad', 'Grudzień'
    );
    
    i NUMBER;
BEGIN
    FOR i IN moje_miesiace.FIRST..moje_miesiace.LAST LOOP
        DBMS_OUTPUT.PUT_LINE(i || '. ' || moje_miesiace(i));
    END LOOP;

    moje_miesiace.DELETE(7); 
    moje_miesiace.DELETE(8);
    
    DBMS_OUTPUT.PUT_LINE('Usunięto indeksy 7 i 8.');
    DBMS_OUTPUT.PUT_LINE('Liczba elementów (COUNT): ' || moje_miesiace.COUNT); 
    
    DBMS_OUTPUT.PUT_LINE('----------------------------------------');
    DBMS_OUTPUT.PUT_LINE('--- 3. Wyświetlanie po usunięciu (z EXISTS) ---');
    
    FOR i IN moje_miesiace.FIRST..moje_miesiace.LAST LOOP
        IF moje_miesiace.EXISTS(i) THEN
            DBMS_OUTPUT.PUT_LINE('Indeks ' || i || ': ' || moje_miesiace(i));
        ELSE
            DBMS_OUTPUT.PUT_LINE('Indeks ' || i || ': --- PUSTY (USUNIĘTY) ---');
        END IF;
    END LOOP;
END;
```

## Zadanie 15
```sql
CREATE TYPE jezyki_obce AS VARRAY(10) OF VARCHAR2(20); 
/ 

CREATE TYPE stypendium AS OBJECT ( 
    nazwa VARCHAR2(50), 
    kraj  VARCHAR2(30), 
    jezyki jezyki_obce 
); 
/ 

CREATE TABLE stypendia OF stypendium; 

INSERT INTO stypendia VALUES 
('SOKRATES','FRANCJA',jezyki_obce('ANGIELSKI','FRANCUSKI','NIEMIECKI')); 
INSERT INTO stypendia VALUES 
('ERASMUS','NIEMCY',jezyki_obce('ANGIELSKI','NIEMIECKI','HISZPANSKI')); 

SELECT * FROM stypendia; 

SELECT s.jezyki FROM stypendia s; 

UPDATE STYPENDIA 
SET jezyki = jezyki_obce('ANGIELSKI','NIEMIECKI','HISZPANSKI','FRANCUSKI') 
WHERE nazwa = 'ERASMUS'; 

CREATE TYPE lista_egzaminow AS TABLE OF VARCHAR2(20); 
/ 

CREATE TYPE semestr AS OBJECT ( 
    numer NUMBER, 
    egzaminy lista_egzaminow 
); 
/ 

CREATE TABLE semestry OF semestr 
NESTED TABLE egzaminy STORE AS tab_egzaminy; 

INSERT INTO semestry VALUES 
(semestr(1,lista_egzaminow('MATEMATYKA','LOGIKA','ALGEBRA'))); 

INSERT INTO semestry VALUES 
(semestr(2,lista_egzaminow('BAZY DANYCH','SYSTEMY OPERACYJNE')));
 
SELECT s.numer, e.* FROM semestry s, TABLE(s.egzaminy) e; 

SELECT e.* FROM semestry s, TABLE ( s.egzaminy ) e; 

SELECT * FROM TABLE ( SELECT s.egzaminy FROM semestry s WHERE numer=1 ); 

INSERT INTO TABLE ( SELECT s.egzaminy FROM semestry s WHERE numer=2 ) 
VALUES ('METODY NUMERYCZNE'); 

UPDATE TABLE ( SELECT s.egzaminy FROM semestry s WHERE numer=2 ) e 
SET e.column_value = 'SYSTEMY ROZPROSZONE' 
WHERE e.column_value = 'SYSTEMY OPERACYJNE'; 

DELETE FROM TABLE ( SELECT s.egzaminy FROM semestry s WHERE numer=2 ) e 
WHERE e.column_value = 'BAZY DANYCH'; 
```

## Zadanie 16
```sql
CREATE OR REPLACE TYPE PRODUKT AS OBJECT (
  nazwa VARCHAR2(50),
  cena NUMBER
);

CREATE OR REPLACE TYPE KOSZYK_TYP AS TABLE OF PRODUKT;

CREATE TABLE ZAKUPY (
  id_zakupu NUMBER,
  klient VARCHAR2(50),
  koszyk KOSZYK_TYP
) NESTED TABLE koszyk STORE AS koszyk_store;

INSERT INTO ZAKUPY VALUES (
  100, 'Kowalski', 
  KOSZYK_TYP(NEW PRODUKT('Mleko', 3.50), NEW PRODUKT('Chleb', 4.00))
);

DELETE FROM TABLE(SELECT koszyk FROM ZAKUPY WHERE id_zakupu = 100) p
WHERE p.nazwa = 'Mleko';
```

## Zadanie 17
```sql
CREATE TYPE instrument AS OBJECT (
  nazwa VARCHAR2(20),
  dzwiek VARCHAR2(20),
  MEMBER FUNCTION graj RETURN VARCHAR2
) NOT FINAL;
/

CREATE OR REPLACE TYPE BODY instrument AS
  MEMBER FUNCTION graj RETURN VARCHAR2 IS
  BEGIN
    RETURN dzwiek;
  END;
END;
/

CREATE TYPE instrument_dety UNDER instrument (
  material VARCHAR2(20),
  OVERRIDING MEMBER FUNCTION graj RETURN VARCHAR2,
  MEMBER FUNCTION graj(glosnosc VARCHAR2) RETURN VARCHAR2
);
/

CREATE OR REPLACE TYPE BODY instrument_dety AS
  OVERRIDING MEMBER FUNCTION graj RETURN VARCHAR2 IS
  BEGIN
    RETURN 'dmucham: ' || dzwiek;
  END;
  
  MEMBER FUNCTION graj(glosnosc VARCHAR2) RETURN VARCHAR2 IS
  BEGIN
    RETURN glosnosc || ': ' || dzwiek;
  END;
END;
/

DECLARE
  tamburyn instrument := instrument('tamburyn', 'brzdek');
  trabka instrument_dety := instrument_dety('trabka', 'trututu', 'metal');
BEGIN
  DBMS_OUTPUT.PUT_LINE(tamburyn.graj());
  DBMS_OUTPUT.PUT_LINE(trabka.graj());        
  DBMS_OUTPUT.PUT_LINE(trabka.graj('głośno'));
END;
/
```

## Zadanie 18
```sql
CREATE TYPE istota AS OBJECT (
  nazwa VARCHAR2(20),
  NOT INSTANTIABLE MEMBER FUNCTION poluj(ofiara CHAR) RETURN CHAR
) NOT INSTANTIABLE NOT FINAL;
/

CREATE TYPE lew UNDER istota (
  liczba_nog NUMBER,
  OVERRIDING MEMBER FUNCTION poluj(ofiara CHAR) RETURN CHAR
);
/

CREATE OR REPLACE TYPE BODY lew AS
  OVERRIDING MEMBER FUNCTION poluj(ofiara CHAR) RETURN CHAR IS
  BEGIN
    RETURN 'Lew ' || nazwa || ' upolował: ' || ofiara;
  END;
END;
/

DECLARE
  krol_lew lew := lew('Simba', 4);
BEGIN
  DBMS_OUTPUT.PUT_LINE(krol_lew.poluj('antylopa'));
END;
/
```

## Zadanie 19
```sql
DECLARE
  inst instrument;
  saksofon instrument_dety;
BEGIN
  inst := instrument_dety('saksofon', 'tra-ta', 'mosiądz');

  saksofon := TREAT(inst AS instrument_dety);
  
  DBMS_OUTPUT.PUT_LINE('Saksofon gra: ' || saksofon.graj());
END;
/
```

## Zadanie 20
```sql
CREATE TABLE instrumenty OF instrument;

INSERT INTO instrumenty VALUES (NEW instrument('bęben', 'bum-bum'));
INSERT INTO instrumenty VALUES (NEW instrument_dety('trąbka', 'tra-ta-ta', 'metal'));

SELECT i.nazwa, i.graj() FROM instrumenty i;
```
