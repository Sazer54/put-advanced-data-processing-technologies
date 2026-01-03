# LAB 14.11 - Oracle Spatial: Przetwarzanie danych

## Zadanie 1A
```sql
INSERT INTO USER_SDO_GEOM_METADATA VALUES (
  'FIGURY',
  'KSZTALT',
  MDSYS.SDO_DIM_ARRAY(
    MDSYS.SDO_DIM_ELEMENT('X', 0, 20, 0.01),
    MDSYS.SDO_DIM_ELEMENT('Y', 0, 20, 0.01)
  ),
  NULL
);
```

## Zadanie 1B
```sql
SELECT SDO_TUNE.ESTIMATE_RTREE_INDEX_SIZE(3000000, 8192, 10, 2, 0) FROM DUAL;
```

## Zadanie 1C
```sql
CREATE INDEX FIGURY_SPATIAL_IDX
ON FIGURY(KSZTALT)
INDEXTYPE IS MDSYS.SPATIAL_INDEX_V2;
```

## Zadania 1D i 1E
```sql
SELECT ID 
FROM FIGURY 
WHERE SDO_FILTER(KSZTALT, 
      SDO_GEOMETRY(2001, NULL, 
          SDO_POINT_TYPE(3, 3, NULL), 
          NULL, NULL)) = 'TRUE'; 

SELECT ID 
FROM FIGURY 
WHERE SDO_RELATE(KSZTALT, 
        SDO_GEOMETRY(2001, NULL, 
          SDO_POINT_TYPE(3, 3, NULL), 
          NULL, NULL), 
        'mask=ANYINTERACT') = 'TRUE'; 
```

> `SDO_FILTER` opiera działanie na pierwszej fazie przetwarzania, wykorzystując aproksymację za pomocą prostokątów otaczających (MBR). Funkcja zwraca obiekty, których MBR pokrywa się z punktem (3,3), także w przypadkach braku fizycznego styku geometrii z punktem.
> 
> `SDO_RELATE` przeprowadza drugą fazę weryfikacji na zbiorze kandydatów. Proces ten pozwala wyselekcjonować obiekty spełniające dokładne kryteria przestrzenne. Figura nr 2 faktycznie przecina punkt (3,3).

## Zadanie 2A
```sql
SELECT B.CITY_NAME AS MIASTO, SDO_NN_DISTANCE(1) AS ODL
FROM MAJOR_CITIES A, MAJOR_CITIES B
WHERE A.CITY_NAME = 'Warsaw'
  AND SDO_NN(B.GEOM, A.GEOM, 'sdo_num_res=10 unit=km', 1) = 'TRUE'
  AND B.CITY_NAME <> 'Warsaw';
```

## Zadanie 2B
```sql
SELECT B.CITY_NAME
FROM MAJOR_CITIES A, MAJOR_CITIES B
WHERE A.CITY_NAME = 'Warsaw'
  AND SDO_WITHIN_DISTANCE(B.GEOM, A.GEOM, 'distance=100 unit=km') = 'TRUE'
  AND B.CITY_NAME <> 'Warsaw';
```

## Zadanie 2C
```sql
SELECT B.CNTRY_NAME AS KRAJ, C.CITY_NAME AS MIASTO
FROM COUNTRY_BOUNDARIES B, MAJOR_CITIES C
WHERE B.CNTRY_NAME = 'Slovakia'
  AND SDO_RELATE(C.GEOM, B.GEOM, 'mask=INSIDE+COVEREDBY') = 'TRUE';
```

## Zadanie 2D
```sql
SELECT B.CNTRY_NAME,
       SDO_GEOM.SDO_DISTANCE(A.GEOM, B.GEOM, 1, 'unit=km') AS ODL
FROM COUNTRY_BOUNDARIES A, COUNTRY_BOUNDARIES B
WHERE A.CNTRY_NAME = 'Poland'
  AND B.CNTRY_NAME <> 'Poland'
  AND NOT SDO_RELATE(A.GEOM, B.GEOM, 'mask=TOUCH') = 'TRUE';
```

## Zadanie 3A
```sql
SELECT B.CNTRY_NAME,
       SDO_GEOM.SDO_LENGTH(
         SDO_GEOM.SDO_INTERSECTION(A.GEOM, B.GEOM, 1),
         1, 'unit=km'
       ) AS DLUGOSC_GRANICY
FROM COUNTRY_BOUNDARIES A, COUNTRY_BOUNDARIES B
WHERE A.CNTRY_NAME = 'Poland'
  AND SDO_RELATE(A.GEOM, B.GEOM, 'mask=TOUCH') = 'TRUE';
```

## Zadanie 3B
```sql
SELECT CNTRY_NAME FROM (
    SELECT CNTRY_NAME
    FROM COUNTRY_BOUNDARIES
    ORDER BY SDO_GEOM.SDO_AREA(GEOM, 1, 'unit=SQ_KM') DESC
) WHERE ROWNUM = 1;
```

## Zadanie 3C
```sql
SELECT SDO_GEOM.SDO_AREA(
         SDO_GEOM.SDO_MBR(
           SDO_GEOM.SDO_UNION(A.GEOM, B.GEOM, 1)
         ), 1, 'unit=SQ_KM'
       ) AS SQ_KM
FROM MAJOR_CITIES A, MAJOR_CITIES B
WHERE A.CITY_NAME = 'Warsaw' AND B.CITY_NAME = 'Lodz';
```

## Zadanie 3D
```sql
SELECT SDO_GEOM.SDO_UNION(A.GEOM, B.GEOM, 1).SDO_GTYPE AS GTYPE
FROM COUNTRY_BOUNDARIES A, MAJOR_CITIES B
WHERE A.CNTRY_NAME = 'Poland'
  AND B.CITY_NAME = 'Prague';
```

## Zadanie 3E
```sql
SELECT C.CITY_NAME, B.CNTRY_NAME
FROM MAJOR_CITIES C, COUNTRY_BOUNDARIES B
WHERE SDO_RELATE(C.GEOM, B.GEOM, 'mask=INSIDE+COVEREDBY') = 'TRUE'
ORDER BY SDO_GEOM.SDO_DISTANCE(
            C.GEOM,
            SDO_GEOM.SDO_CENTROID(B.GEOM, 1),
            1
          ) ASC
FETCH FIRST 1 ROW ONLY;
```

## Zadanie 3F
```sql
SELECT R.NAME,
       SUM(SDO_GEOM.SDO_LENGTH(
         SDO_GEOM.SDO_INTERSECTION(R.GEOM, C.GEOM, 1),
         1, 'unit=km'
       )) AS DLUGOSC
FROM RIVERS R, COUNTRY_BOUNDARIES C
WHERE C.CNTRY_NAME = 'Poland'
  AND SDO_RELATE(R.GEOM, C.GEOM, 'mask=ANYINTERACT') = 'TRUE'
GROUP BY R.NAME;
```