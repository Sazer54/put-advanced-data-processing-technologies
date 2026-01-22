# LAB 5.12 - XPath

## Zadanie 9
```
SWIAT/KRAJE/KRAJ
```

## Zadanie 11
```
SWIAT/KRAJE/KRAJ[@KONTYNENT = 'k1']
```

## Zadanie 12
```
SWIAT/KRAJE/KRAJ[@KONTYNENT = /SWIAT/KONTYNENTY/KONTYNENT[NAZWA='Europe']/@ID]
```

## Zadanie 15
```
Liczba krajów: <xsl:value-of select="count(SWIAT/KRAJE/KRAJ[@KONTYNENT = /SWIAT/KONTYNENTY/KONTYNENT[NAZWA='Europe']/@ID])"/>
```

## Zadanie 17
```
<td><xsl:value-of select="position()"/></td>
```

## Zadanie 21
```
<xsl:sort select="NAZWA"/>
```

## Zadanie 27
```
for $k in doc('swiat.xml')/SWIAT/KRAJE/KRAJ
return <KRAJ>
  {$k/NAZWA, $k/STOLICA}
</KRAJ>
```

## Zadanie 28
```
for $k in doc('swiat.xml')/SWIAT/KRAJE/KRAJ[starts-with(NAZWA, 'A')]
return <KRAJ>
  {$k/NAZWA, $k/STOLICA}
</KRAJ>
```

## Zadanie 29
```
for $k in doc('swiat.xml')/SWIAT/KRAJE/KRAJ[substring(NAZWA,1,1) = substring(STOLICA,1,1)]
return <KRAJ>
  {$k/NAZWA, $k/STOLICA}
</KRAJ>
```

## Zadanie 32
```
for $k in doc('zesp_prac.xml')//ZESPOLY/ROW/PRACOWNICY/ROW
return $k/NAZWISKO
```

## Zadanie 33
```
for $k in doc('zesp_prac.xml')//ZESPOLY/ROW[NAZWA='SYSTEMY EKSPERCKIE']/PRACOWNICY/ROW
return $k/NAZWISKO
```

## Zadanie 34
```
count(
  doc('zesp_prac.xml')//PRACOWNICY/ROW[ID_ZESP=10]
)
```

## Zadanie 35
```
doc('zesp_prac.xml')//PRACOWNICY/ROW[ID_SZEFA=100]/NAZWISKO
```

## Zadanie 36
```
sum(
  doc('zesp_prac.xml')//PRACOWNICY/ROW[
    ID_ZESP = doc('zesp_prac.xml')//PRACOWNICY/ROW[NAZWISKO='BRZEZINSKI']/ID_ZESP
  ]/PLACA_POD
)
```