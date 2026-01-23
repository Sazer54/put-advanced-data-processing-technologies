package lucene;

import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;
import java.nio.file.Paths;

public class Index {
    private static final String INDEX_DIR = "index_directory";

    private static Document buildDoc(String title, String isbn) {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        return doc;
    }

    public static void main(String[] args) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIR));

        PolishAnalyzer analyzer = new PolishAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(directory, config);

        System.out.println("Indeksowanie dokumentów...");
        w.addDocument(buildDoc("Lucyna w akcji", "9780062316097"));
        w.addDocument(buildDoc("Akcje rosną i spadają", "9780385545955"));
        w.addDocument(buildDoc("Bo ponieważ", "9781501168007"));
        w.addDocument(buildDoc("Naturalnie urodzeni mordercy", "9780316485616"));

        w.close();
        directory.close();
        System.out.println("Indeksowanie zakończone. Dane w folderze: " + INDEX_DIR);
    }
}