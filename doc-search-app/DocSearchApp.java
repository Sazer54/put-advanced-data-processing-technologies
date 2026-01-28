import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.ingest.Processor;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DocSearchApp extends JFrame {

    private JTextField pathField;
    private JTextArea logArea;
    private JTextField searchField;
    private JEditorPane resultsPane;
    private JButton btnIndex;
    private JButton btnClear;
    private JProgressBar progressBar;

    private ElasticsearchClient client;
    private RestClient restClient;
    private final String INDEX_NAME = "dokumenty";
    private final String PIPELINE_NAME = "pdf-attachment";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DocSearchApp frame = new DocSearchApp();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public DocSearchApp() {
        initElasticSearch();
        initGUI();
    }

    private void initElasticSearch() {
        try {
            restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
            RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
            client = new ElasticsearchClient(transport);

            createPipelineIfNotExists();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Błąd połączenia z ES: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void createPipelineIfNotExists() {
        try {
            System.out.println("Konfigurowanie potoku przetwarzania (Pipeline)...");

            client.ingest().putPipeline(p -> p
                    .id(PIPELINE_NAME)
                    .description("Wyciąga tekst z PDF i usuwa surowe dane Base64")
                    .processors(proc -> proc
                            .attachment(a -> a
                                    .field("data")
                                    .targetField("attachment")
                                    .indexedChars(-1L)
                            )
                    )
                    .processors(proc -> proc
                            .remove(r -> r.field("data"))
                    )
            );

            System.out.println("Pipeline '" + PIPELINE_NAME + "' został utworzony/zaktualizowany pomyślnie.");

        } catch (Exception e) {
            System.err.println("Błąd podczas tworzenia Pipeline: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initGUI() {
        setTitle("Inteligentna Wyszukiwarka (Tika + Stempel)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 950, 750);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout(0, 10));
        setContentPane(contentPane);

        JPanel indexPanel = new JPanel(new BorderLayout(5, 5));
        indexPanel.setBorder(BorderFactory.createTitledBorder("Zarządzanie Indeksem"));

        pathField = new JTextField();
        pathField.setEditable(false);
        indexPanel.add(pathField, BorderLayout.CENTER);

        JButton btnChoose = new JButton("Wybierz folder...");
        btnChoose.addActionListener(e -> chooseFolder());
        indexPanel.add(btnChoose, BorderLayout.WEST);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        btnClear = new JButton("Wyczyść Indeks");
        btnClear.setForeground(Color.RED);
        btnClear.addActionListener(e -> clearIndexAction());
        actionsPanel.add(btnClear);

        btnIndex = new JButton("Start Indeksowania");
        btnIndex.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnIndex.setEnabled(false);
        btnIndex.addActionListener(e -> startIndexing());
        actionsPanel.add(btnIndex);

        indexPanel.add(actionsPanel, BorderLayout.EAST);

        JPanel indexStatusPanel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        indexStatusPanel.add(progressBar, BorderLayout.NORTH);

        logArea = new JTextArea(5, 20);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        indexStatusPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        indexPanel.add(indexStatusPanel, BorderLayout.SOUTH);

        contentPane.add(indexPanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Wyszukiwanie"));

        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        searchField.addActionListener(e -> performSearch());
        searchPanel.add(searchField, BorderLayout.CENTER);

        JButton btnSearch = new JButton("Szukaj");
        btnSearch.addActionListener(e -> performSearch());
        searchPanel.add(btnSearch, BorderLayout.EAST);

        contentPane.add(searchPanel, BorderLayout.CENTER);

        resultsPane = new JEditorPane();
        resultsPane.setContentType("text/html");
        resultsPane.setEditable(false);

        JScrollPane scrollResults = new JScrollPane(resultsPane);
        scrollResults.setBorder(BorderFactory.createTitledBorder("Wyniki"));
        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.add(searchPanel, BorderLayout.NORTH);
        bottomWrapper.add(scrollResults, BorderLayout.CENTER);

        contentPane.add(bottomWrapper, BorderLayout.CENTER);
    }

    private void clearIndexAction() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Czy na pewno chcesz usunąć wszystkie zaindeksowane dokumenty?",
                "Potwierdzenie", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    SwingUtilities.invokeLater(() -> logArea.append("Usuwanie indeksu...\n"));

                    try {
                        client.indices().delete(DeleteIndexRequest.of(d -> d.index(INDEX_NAME)));
                    } catch (Exception ignored) {

                    }

                    createIndexWithSettings();

                    SwingUtilities.invokeLater(() -> {
                        logArea.append("Indeks został wyczyszczony i utworzony na nowo.\n");
                        JOptionPane.showMessageDialog(this, "Indeks wyczyszczony!");
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> logArea.append("Błąd czyszczenia: " + e.getMessage() + "\n"));
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void createIndexWithSettings() throws IOException {
        System.out.println("Tworzenie indeksu z polskim analizatorem...");

        client.indices().create(CreateIndexRequest.of(c -> c
                .index(INDEX_NAME)
                .mappings(m -> m
                        .properties("attachment", p -> p
                                .object(o -> o
                                        .properties("content", t -> t
                                                .text(txt -> txt.analyzer("polish"))
                                        )
                                )
                        )
                        .properties("filename", p -> p.keyword(k -> k))
                )
        ));
    }

    private void chooseFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            pathField.setText(chooser.getSelectedFile().getAbsolutePath());
            btnIndex.setEnabled(true);
        }
    }

    private void startIndexing() {
        String path = pathField.getText();
        if (path.isEmpty()) return;

        btnIndex.setEnabled(false);
        btnClear.setEnabled(false);
        progressBar.setIndeterminate(true);
        logArea.setText("Rozpoczynam skanowanie: " + path + "\n");

        new Thread(() -> {
            try (Stream<Path> paths = Files.walk(Paths.get(path))) {
                paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().toLowerCase().endsWith(".pdf"))
                        .forEach(p -> {
                            try {
                                indexSingleFile(p.toFile());
                                SwingUtilities.invokeLater(() ->
                                        logArea.append("Zaktualizowano/Dodano: " + p.getFileName() + "\n"));
                            } catch (Exception ex) {
                                SwingUtilities.invokeLater(() ->
                                        logArea.append("BŁĄD [" + p.getFileName() + "]: " + ex.getMessage() + "\n"));
                            }
                        });

                SwingUtilities.invokeLater(() -> {
                    logArea.append("--- ZAKOŃCZONO --- \n");
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    btnIndex.setEnabled(true);
                    btnClear.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Gotowe!");
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void indexSingleFile(File file) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(file);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        Map<String, Object> document = new HashMap<>();
        document.put("filename", file.getName());
        document.put("fullpath", file.getAbsolutePath());
        document.put("data", encodedString);

        String docId = file.getAbsolutePath().replace("\\", "/");

        client.index(i -> i
                .index(INDEX_NAME)
                .id(docId)
                .pipeline(PIPELINE_NAME)
                .document(document)
        );
    }

    private void performSearch() {
        String query = searchField.getText();
        if (query.trim().isEmpty()) return;

        try {
            SearchResponse<DocSource> response = client.search(s -> s
                            .index(INDEX_NAME)
                            .query(q -> q
                                    .match(m -> m
                                            .field("attachment.content")
                                            .query(query)
                                    )
                            )
                            .highlight(h -> h
                                    .fields("attachment.content", f -> f
                                            .preTags("<span style='background-color: #FFFF00; font-weight: bold;'>")
                                            .postTags("</span>")
                                            .numberOfFragments(3)
                                    )
                            ),
                    DocSource.class
            );

            StringBuilder html = new StringBuilder();
            html.append("<html><body style='font-family: sans-serif;'>");
            html.append("<h3>Znaleziono: ").append(response.hits().total().value()).append(" dokumentów</h3>");

            for (Hit<DocSource> hit : response.hits().hits()) {
                DocSource source = hit.source();
                html.append("<div style='border-bottom: 1px solid #ccc; padding: 10px; margin-bottom: 10px;'>");

                if (source != null) {
                    html.append("<div style='color: #0000AA; font-size: 14px;'><b>")
                            .append(source.filename).append("</b></div>");
                    html.append("<div style='color: #666; font-size: 10px;'>")
                            .append(source.fullpath).append("</div>");
                }

                if (hit.highlight().get("attachment.content") != null) {
                    html.append("<div style='margin-top: 5px; font-style: italic;'>");
                    List<String> fragments = hit.highlight().get("attachment.content");
                    for (String frag : fragments) {
                        html.append("... ").append(frag).append(" ...<br>");
                    }
                    html.append("</div>");
                }
                html.append("</div>");
            }
            html.append("</body></html>");
            resultsPane.setText(html.toString());
            resultsPane.setCaretPosition(0);

        } catch (Exception e) {
            if (e.getMessage().contains("index_not_found_exception")) {
                resultsPane.setText("<html><body><h3>Indeks jest pusty. Dodaj pliki.</h3></body></html>");
            } else {
                JOptionPane.showMessageDialog(this, "Błąd szukania: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DocSource {
        public String filename;
        public String fullpath;
    }
}