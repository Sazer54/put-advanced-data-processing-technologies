# Inteligentna Wyszukiwarka Dokumentów (PDF)

Aplikacja desktopowa (Java Swing) umożliwiająca pełnotekstowe przeszukiwanie lokalnych zbiorów plików PDF, z uwzględnieniem gramatyki języka polskiego.

### Główne funkcjonalności
* **Indeksowanie treści:** Automatyczna ekstrakcja tekstu z plików PDF (z wykorzystaniem **Apache Tika**).
* **Obsługa języka polskiego:** Dzięki analizatorowi morfologicznemu (**Stempel**) system rozpoznaje odmianę słów (np. szukając *"faktura"*, znajdzie dokument zawierający słowo *"fakturami"*).
* **Highlighting:** Wyświetlanie fragmentów tekstu z podświetloną szukaną frazą.
* **Zarządzanie indeksem:** Możliwość skanowania całych folderów (wraz z podfolderami) oraz czyszczenia bazy.

### Technologia
* **Backend:** ElasticSearch 7.17 (uruchamiany w Dockerze).
* **Pluginy ES:** `ingest-attachment` (parsowanie PDF), `analysis-stempel` (język polski).
* **Frontend:** Java Swing.

### Jak uruchomić?
1.  Upewnij się, że Docker działa i uruchom bazę danych:
    ```bash
    docker-compose up -d
    ```
    *(Wymagane zainstalowanie pluginów w kontenerze: `ingest-attachment` oraz `analysis-stempel`)*.

2.  Uruchom klasę `DocSearchApp.java`.
3.  Wybierz katalog z plikami PDF i kliknij **Start Indeksowania**.
4.  Wpisz szukaną frazę i kliknij **Szukaj**.