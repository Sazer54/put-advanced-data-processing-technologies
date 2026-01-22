# LAB 12.12 - XSL

## Zadania 1-14
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- Zadanie 3  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" encoding="UTF-8"/>

    <!-- Zadanie 11  -->
    <xsl:variable name="wszyscy_pracownicy" select="//PRACOWNICY/ROW"/>

    <xsl:template match="/">
        <html>
            <body>
                <!-- Zadanie 5  -->
                <h1>ZESPOŁY:</h1>

                <!-- Zadanie 6b  -->
                <ol>
                    <xsl:apply-templates select="ZESPOLY/ROW" mode="lista"/>
                </ol>

                <!-- Zadanie 7  -->
                <xsl:apply-templates select="ZESPOLY/ROW" mode="szczegoly"/>
            </body>
        </html>
    </xsl:template>

    <!-- Zadanie 6b  -->
    <xsl:template match="ROW" mode="lista">
        <li>
            <!-- Zadanie 9  -->
            <a href="#{ID_ZESP}">
                <xsl:value-of select="NAZWA"/>
            </a>
        </li>
    </xsl:template>

    <xsl:template match="ROW" mode="szczegoly">
        <!-- Zadanie 7  -->
        <h2 id="{ID_ZESP}">NAZWA: <xsl:value-of select="NAZWA"/></h2>
        <p>ADRES: <xsl:value-of select="ADRES"/></p>
        
        <!-- Zadanie 14  -->
        <xsl:if test="count(PRACOWNICY/ROW) > 0">
            <!-- Zadanie 8  -->
            <table border="1">
                <tr>
                    <th>Nazwisko</th>
                    <th>Etat</th>
                    <th>Zatrudniony</th>
                    <th>Płaca pod.</th>
                    <th>Szef</th> </tr>
                <xsl:apply-templates select="PRACOWNICY/ROW">
                    <!-- Zadanie 10  -->
                    <xsl:sort select="NAZWISKO"/>
                </xsl:apply-templates>
            </table>
        </xsl:if>

        <!-- Zadanie 13  -->
        <p>Liczba pracowników: <xsl:value-of select="count(PRACOWNICY/ROW)"/></p>
        <hr/>
    </xsl:template>

    <!-- Zadanie 8  -->
    <xsl:template match="ROW">
        <tr>
            <td><xsl:value-of select="NAZWISKO"/></td>
            <td><xsl:value-of select="ETAT"/></td>
            <td><xsl:value-of select="ZATRUDNIONY"/></td>
            <td><xsl:value-of select="PLACA_POD"/></td>
            <!-- Zadanie 11  -->
            <td>
                <xsl:variable name="id_szefa" select="ID_SZEFA"/>
                <xsl:choose>
                    <xsl:when test="$id_szefa != ''">
                        <xsl:value-of select="$wszyscy_pracownicy[ID_PRAC = $id_szefa]/NAZWISKO"/>
                    </xsl:when>
                    <!-- Zadanie 12  -->
                    <xsl:otherwise>
                        brak
                    </xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
```

## Zadanie 15
### Konwerter
```xml
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:template match="/">
        <PRACOWNICY>
            <xsl:for-each select="//PRACOWNICY/ROW">
                <xsl:sort select="ID_PRAC" data-type="number"/>

                <PRACOWNIK>
                    <xsl:attribute name="ID_PRAC">
                        <xsl:value-of select="ID_PRAC"/>
                    </xsl:attribute>
                    <xsl:attribute name="ID_ZESP">
                        <xsl:value-of select="../../ID_ZESP"/>
                    </xsl:attribute>
                    <xsl:attribute name="ID_SZEFA">
                        <xsl:value-of select="ID_SZEFA"/>
                    </xsl:attribute>

                    <xsl:copy-of select="NAZWISKO | ETAT | ZATRUDNIONY | PLACA_POD | PLACA_DOD"/>
                </PRACOWNIK>
            </xsl:for-each>
        </PRACOWNICY>
    </xsl:template>
</xsl:stylesheet>
```
### Plik wynikowy
```xml
<?xml version="1.0" encoding="UTF-8"?>
<PRACOWNICY>
    <PRACOWNIK ID_PRAC="100" ID_ZESP="10" ID_SZEFA="">
        <NAZWISKO>WEGLARZ</NAZWISKO>
        <ETAT>DYREKTOR</ETAT>
        <ZATRUDNIONY>01.01.1968</ZATRUDNIONY>
        <PLACA_POD>1730</PLACA_POD>
        <PLACA_DOD>420.5</PLACA_DOD>
    </PRACOWNIK>
    <PRACOWNIK ID_PRAC="110" ID_ZESP="40" ID_SZEFA="100">
        <NAZWISKO>BLAZEWICZ</NAZWISKO>
        <ETAT>PROFESOR</ETAT>
        <ZATRUDNIONY>01.05.1973</ZATRUDNIONY>
        <PLACA_POD>1350</PLACA_POD>
        <PLACA_DOD>210</PLACA_DOD>
    </PRACOWNIK>
    <PRACOWNIK ID_PRAC="120" ID_ZESP="30" ID_SZEFA="100">
        <NAZWISKO>SLOWINSKI</NAZWISKO>
        <ETAT>PROFESOR</ETAT>
        <ZATRUDNIONY>01.09.1977</ZATRUDNIONY>
        <PLACA_POD>1070</PLACA_POD>
    </PRACOWNIK>
    <PRACOWNIK ID_PRAC="130" ID_ZESP="20" ID_SZEFA="100">
        <NAZWISKO>BRZEZINSKI</NAZWISKO>
        <ETAT>PROFESOR</ETAT>
        <ZATRUDNIONY>01.07.1968</ZATRUDNIONY>
        <PLACA_POD>960</PLACA_POD>
    </PRACOWNIK>
    <PRACOWNIK ID_PRAC="140" ID_ZESP="20" ID_SZEFA="130">
        <NAZWISKO>MORZY</NAZWISKO>
        <ETAT>PROFESOR</ETAT>
        <ZATRUDNIONY>15.09.1975</ZATRUDNIONY>
        <PLACA_POD>830</PLACA_POD>
        <PLACA_DOD>105</PLACA_DOD>
    </PRACOWNIK>
    <PRACOWNIK ID_PRAC="150" ID_ZESP="20" ID_SZEFA="130">
        <NAZWISKO>KROLIKOWSKI</NAZWISKO>
        <ETAT>ADIUNKT</ETAT>
        <ZATRUDNIONY>01.09.1977</ZATRUDNIONY>
        <PLACA_POD>645.5</PLACA_POD>
    </PRACOWNIK>
    <PRACOWNIK ID_PRAC="160" ID_ZESP="20" ID_SZEFA="130">
        <NAZWISKO>KOSZLAJDA</NAZWISKO>
        <ETAT>ADIUNKT</ETAT>
        <ZATRUDNIONY>01.03.1985</ZATRUDNIONY>
        <PLACA_POD>590</PLACA_POD>
    </PRACOWNIK>
    <PRACOWNIK ID_PRAC="170" ID_ZESP="20" ID_SZEFA="130">
        <NAZWISKO>JEZIERSKI</NAZWISKO>
        <ETAT>ASYSTENT</ETAT>
        <ZATRUDNIONY>01.10.1992</ZATRUDNIONY>
        <PLACA_POD>439.7</PLACA_POD>
        <PLACA_DOD>80.5</PLACA_DOD>
    </PRACOWNIK>
    <PRACOWNIK ID_PRAC="180" ID_ZESP="10" ID_SZEFA="100">
        <NAZWISKO>MAREK</NAZWISKO>
        <ETAT>SEKRETARKA</ETAT>
        <ZATRUDNIONY>20.02.1985</ZATRUDNIONY>
        <PLACA_POD>410.2</PLACA_POD>
    </PRACOWNIK>
    <PRACOWNIK ID_PRAC="190" ID_ZESP="20" ID_SZEFA="140">
        <NAZWISKO>MATYSIAK</NAZWISKO>
        <ETAT>ASYSTENT</ETAT>
        <ZATRUDNIONY>01.09.1993</ZATRUDNIONY>
        <PLACA_POD>371</PLACA_POD>
    </PRACOWNIK>
    <PRACOWNIK ID_PRAC="200" ID_ZESP="30" ID_SZEFA="140">
        <NAZWISKO>ZAKRZEWICZ</NAZWISKO>
        <ETAT>STAZYSTA</ETAT>
        <ZATRUDNIONY>15.07.1994</ZATRUDNIONY>
        <PLACA_POD>208</PLACA_POD>
    </PRACOWNIK>
    <PRACOWNIK ID_PRAC="210" ID_ZESP="30" ID_SZEFA="130">
        <NAZWISKO>BIALY</NAZWISKO>
        <ETAT>STAZYSTA</ETAT>
        <ZATRUDNIONY>15.10.1993</ZATRUDNIONY>
        <PLACA_POD>250</PLACA_POD>
        <PLACA_DOD>170.6</PLACA_DOD>
    </PRACOWNIK>
    <PRACOWNIK ID_PRAC="220" ID_ZESP="20" ID_SZEFA="110">
        <NAZWISKO>KONOPKA</NAZWISKO>
        <ETAT>ASYSTENT</ETAT>
        <ZATRUDNIONY>01.10.1993</ZATRUDNIONY>
        <PLACA_POD>480</PLACA_POD>
    </PRACOWNIK>
    <PRACOWNIK ID_PRAC="230" ID_ZESP="30" ID_SZEFA="120">
        <NAZWISKO>HAPKE</NAZWISKO>
        <ETAT>ASYSTENT</ETAT>
        <ZATRUDNIONY>01.09.1992</ZATRUDNIONY>
        <PLACA_POD>480</PLACA_POD>
        <PLACA_DOD>90</PLACA_DOD>
    </PRACOWNIK>
</PRACOWNICY>

```