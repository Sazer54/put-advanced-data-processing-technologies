package lucene;

import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        PolishAnalyzer analyzer = new PolishAnalyzer();
        Directory directory = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(directory, config);

        w.addDocument(buildDoc("Lucyna w akcji", "9780062316097"));
        w.addDocument(buildDoc("Akcje rosną i spadają", "9780385545955"));
        w.addDocument(buildDoc("Bo ponieważ", "9781501168007"));
        w.addDocument(buildDoc("Naturalnie      urodzeni       mordercy", "9780316485616"));
        w.addDocument(buildDoc("Druhna rodzi", "9780593301760"));
        w.addDocument(buildDoc("Urodzić się na nowo", "9780679777489"));

        w.close();

        String querystr = "*:*";
        Query q = new QueryParser("title", analyzer).parse(querystr);

        int maxHits = 10;
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, maxHits);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " matching docs.");
        StoredFields storedFields = searcher.storedFields();
        for(int i=0; i<hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = storedFields.document(docId);
            System.out.println((i + 1) + ". " + d.get("isbn")
                    + "\t" + d.get("title"));
        }

        String[] queries = {
                "isbn:9780062316097",
                "title:urodzić",
                "title:rodzić",
                "title:ro*",
                "title:ponieważ",
                "title:Lucyna AND title:akcja",
                "title:akcja NOT title:Lucyna",
                "title:\"naturalnie morderca\"~2",
                "title:\"naturalnie morderca\"~1",
                "title:\"naturalnie morderca\"~0",
                "title:naturalne",
                "title:naturalne~"
        };

        for (int i = 0; i < queries.length; i++) {
            try {
                q = new QueryParser("title", analyzer).parse(queries[i]);

                docs = searcher.search(q, 10);
                hits = docs.scoreDocs;

                System.out.println("Znaleziono: " + hits.length);
                for (ScoreDoc hit : hits) {
                    Document d = searcher.storedFields().document(hit.doc);
                    System.out.println("   -> " + d.get("isbn") + " | " + d.get("title"));
                }
            } catch (Exception e) {
                System.out.println("   Błąd zapytania: " + e.getMessage());
            }
        }

        // Zadanie 7 i Zadanie 12
        /*
        String[] queries = {
                "dummy",
                "and"
        };

        for (String queryStr : queries) {
            q = new QueryParser("title", analyzer).parse(queryStr);
            docs = searcher.search(q, maxHits);

            System.out.println("\nZapytanie: \"" + queryStr + "\"");
            System.out.println("Liczba wyników: " + docs.totalHits);

            for (ScoreDoc hit : docs.scoreDocs) {
                Document d = searcher.storedFields().document(hit.doc);
                System.out.println(" -> " + d.get("title"));
            }
        }*/

        reader.close();
    }

    private static Document buildDoc(String title, String isbn) {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        return doc;
    }
}
