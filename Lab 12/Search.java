package lucene;

import org.apache.lucene.analysis.pl.PolishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Paths;

public class Search {
    private static final String INDEX_DIR = "index_directory";

    public static void main(String[] args) {
        try {
            Directory directory = FSDirectory.open(Paths.get(INDEX_DIR));
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            PolishAnalyzer analyzer = new PolishAnalyzer();

            String querystr = "title:akcja";
            Query q = new QueryParser("title", analyzer).parse(querystr);

            TopDocs docs = searcher.search(q, 10);
            ScoreDoc[] hits = docs.scoreDocs;

            System.out.println("Znaleziono " + hits.length + " dokumentów dla: " + querystr);
            for (ScoreDoc hit : hits) {
                Document d = searcher.storedFields().document(hit.doc);
                System.out.println(d.get("isbn") + "\t" + d.get("title"));
            }

            reader.close();
            directory.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}