# LAB 19.12 - XQuery

## Zadanie 5
```xquery
doc("db/bib/bib.xml")//author/last
```

## Zadanie 6
```xquery
for $b in doc("db/bib/bib.xml")/bib/book
for $a in $b/author
return
  <ksiazka>
    { $a }
    { $b/title }
  </ksiazka>
```

## Zadanie 7
```xquery
for $b in doc("db/bib/bib.xml")/bib/book
for $a in $b/author
return
  <ksiazka>
    <autor>{ concat($a/last, $a/first) }</autor>
    <tytul>{ $b/title/text() }</tytul>
  </ksiazka>
```

## Zadanie 8
```xquery
for $b in doc("db/bib/bib.xml")/bib/book
for $a in $b/author
return
  <ksiazka>
    <autor>{ $a/last || ' ' || $a/first }</autor>
    <tytul>{ $b/title/text() }</tytul>
  </ksiazka>
```

## Zadanie 9
```xquery
<wynik>
{
  for $b in doc("db/bib/bib.xml")/bib/book
  for $a in $b/author
  return
    <ksiazka>
      <autor>{ $a/last || ' ' || $a/first }</autor>
      <tytul>{ $b/title/text() }</tytul>
    </ksiazka>
}
</wynik>
```

## Zadanie 10
```xquery
<imiona>
{
  for $b in doc("db/bib/bib.xml")/bib/book
  where $b/title = "Data on the Web"
  for $a in $b/author
  return <imie>{ $a/first/text() }</imie>
}
</imiona>
```

## Zadanie 11A
```xquery
<DataOnTheWeb>
{ doc("db/bib/bib.xml")/bib/book[title="Data on the Web"] }
</DataOnTheWeb>
```

## Zadanie 11B
```xquery
<DataOnTheWeb>
{
  for $b in doc("db/bib/bib.xml")/bib/book
  where $b/title = "Data on the Web"
  return $b
}
</DataOnTheWeb>
```

## Zadanie 12
```xquery
<Data>
{
  for $b in doc("db/bib/bib.xml")/bib/book
  where contains($b/title, "Data")
  for $a in $b/author
  return <nazwisko>{ $a/last/text() }</nazwisko>
}
</Data>
```

## Zadanie 13
```xquery
<Data>
{
  for $b in doc("db/bib/bib.xml")/bib/book
  where contains($b/title, "Data")
  return (
    $b/title,
    for $a in $b/author return <nazwisko>{ $a/last/text() }</nazwisko>
  )
}
</Data>
```

## Zadanie 14
```xquery
for $b in doc("db/bib/bib.xml")/bib/book
where count($b/author) <= 2
return $b/title
```

## Zadanie 15
```xquery
for $b in doc("db/bib/bib.xml")/bib/book
return
  <ksiazka>
    { $b/title }
    <autorow>{ count($b/author) }</autorow>
  </ksiazka>
```

## Zadanie 16
```xquery
let $years := doc("db/bib/bib.xml")/bib/book/@year
return <przedział>{ min($years) } - { max($years) }</przedział>
```

## Zadanie 17
```xquery
let $prices := doc("db/bib/bib.xml")/bib/book/price
return <różnica>{ max($prices) - min($prices) }</różnica>
```

## Zadanie 18
```xquery
let $minPrice := min(doc("db/bib/bib.xml")/bib/book/price)
return
<najtańsze>
{
  for $b in doc("db/bib/bib.xml")/bib/book
  where $b/price = $minPrice
  return
    <najtańsza>
      { $b/title }
      { $b/author }
    </najtańsza>
}
</najtańsze>
```

## Zadanie 19
```xquery
let $authors := distinct-values(doc("db/bib/bib.xml")//author/last)
for $last in $authors
return
  <autor>
    <last>{ $last }</last>
    {
      for $b in doc("db/bib/bib.xml")/bib/book
      where $b/author/last = $last
      return $b/title
    }
  </autor>
```

## Zadanie 20
```xquery
<wynik>
{
  for $play in collection("db/shakespeare")/PLAY
  return $play/TITLE
}
</wynik>
```

## Zadanie 21
```xquery
for $play in collection("db/shakespeare")/PLAY
where some $line in $play//LINE satisfies contains($line, "or not to be")
return $play/TITLE
```

## Zadanie 22
```xquery
<wynik>
{
  for $play in collection("db/shakespeare")/PLAY
  return
    <sztuka tytul="{ $play/TITLE/text() }">
      <postaci>{ count($play/PERSONAE//PERSONA) }</postaci>
      <aktow>{ count($play/ACT) }</aktow>
      <scen>{ count($play//SCENE) }</scen>
    </sztuka>
}
</wynik>
```