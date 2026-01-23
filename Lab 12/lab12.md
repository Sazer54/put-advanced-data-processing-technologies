# LAB 16.01 - Lucene

## Zadanie 7 i 9
Wyniki dla `StandardAnalyzer`
```
dummy: "Dummy and yummy title"
and: "Dummy and yummy title"
```
Wyniki dla `EnglishAnalyzer`
```
dummy: "Dummy and yummy title", "Lucene for Dummies"
and: brak
```
> Odmienione `dummies` zostało znalezione przez `EnglishAnalyzer` dzięki zastosowanemu w nim stemmingu dla angielskich słów. Zdanie zawierające `and` nie zostało natomiast znalezione, bo znajduje ono się na liście "stop words".

## Zadanie do samodzielnego wykonania
> W katalogu znajdują się pliki z rozszerzeniami `.cfe`, `.cfs`, `.si`, `.lock`.

> Kolejne uruchomienie doda te same dokumenty ponownie z innym ID, a wyniki są zduplikowane.