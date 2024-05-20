package org.example.gui;

import invertedIndex.Index5_Positional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import invertedIndex.Inverted_Crawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HelloController {
    @FXML
    TextArea dict1_area,dict2_area,dict3_area,result_area;
    @FXML
    RadioButton method1_button, method2_button,method3_button,method_similarity_button;
    @FXML
    TextField query_field;
    @FXML
    Label terms_area1,terms_area2,terms_area3;
    @FXML
    Button search_button;
    @FXML
    Label crawled_area,indexed_area,CrawlerpageLabel;
    @FXML
    TextField Seed_link,num_pages;
    @FXML
    Button crawl_button,crawl_indexing_button;

    int n = 0,num_of_pages;
    private String crawlFolder = "D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\GUI\\Crawled storage\\";
    public static String urls_file_path = "D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\GUI\\urls.txt";

    private Set<String> visitedUrls = new HashSet<>();
    public  Inverted_Crawler crawl_indexer = new invertedIndex.Inverted_Crawler();
    @FXML
    public void initialize() {
        dict1_area.setText(HelloApplication.dict1);
        dict2_area.setText(HelloApplication.dict2);
        dict3_area.setText(HelloApplication.dict3);
        terms_area1.setText(String.valueOf(HelloApplication.terms1)+" terms");
        terms_area2.setText(String.valueOf(HelloApplication.terms2)+" terms");
        terms_area3.setText(String.valueOf(HelloApplication.terms3)+" terms");
        applyButtonEffects(crawl_button);
        applyButtonEffects(crawl_indexing_button);
        applyButtonEffects(search_button);
    }
    @FXML
    public void checker(ActionEvent event)  {

        // Check if no radio button is selected
        if (!method1_button.isSelected() && !method2_button.isSelected() && !method3_button.isSelected()&& !method_similarity_button.isSelected()) {
            showWarning("Please select a method.");
            return;
        }

        // Check if query field is empty
        if (query_field.getText().trim().isEmpty()) {
            showWarning("Please enter a query.");
            return;
        }
        String query = query_field.getText().trim();
        if (method1_button.isSelected()){

            String searchResult =  HelloApplication.index.find_24_01(query);
            result_area.setText(searchResult);
        }
        else if (method2_button.isSelected()){

            String searchResult =  HelloApplication.index_biWord.find_24_01(query);
            result_area.setText(searchResult);
        }
        else if (method3_button.isSelected()){

            String searchResult =  HelloApplication.index_Positional.find_24_01(query);
            result_area.setText(searchResult);
        }
        else{
            if (crawl_indexer.N>0) {

                new Thread(() -> {
                    Platform.runLater(() -> {
                        result_area.setText("Calculating similarity...");
                    });
                    String scoreResult = crawl_indexer.cosine_similarity(query);

                    Platform.runLater(() -> result_area.setText(scoreResult));
                    String temp="0";
                }).start();
            }
            else {
                new Thread(() -> {

                    File file = new File(crawlFolder);
                    String[] fileList = file.list();
                    crawl_indexer = new invertedIndex.Inverted_Crawler();
                    fileList = crawl_indexer.sort(fileList);
                    crawl_indexer.setN(fileList.length);
                    for (int i = 0; i < fileList.length; i++) {
                        fileList[i] = crawlFolder + fileList[i];
                    }
                    crawl_indexer.setN(fileList.length);
                    crawl_indexer.buildIndex(fileList);
                    crawl_indexer.store("index");
                    Platform.runLater(() -> {
                        result_area.setText("Calculating similarity...");
                    });
                    String scoreResult = crawl_indexer.cosine_similarity(query);
                    Platform.runLater(() -> result_area.setText(scoreResult));
                    String temp="0";
                }).start();
            }
        }
    }
    private Alert currentAlert = null;
    private void showWarning(String message) {
        if (currentAlert != null) {
            currentAlert.close();
        }

        currentAlert = new Alert(AlertType.WARNING);
        currentAlert.setTitle("WARNING");
        currentAlert.setHeaderText(null);
        currentAlert.setContentText(message);

        DialogPane dialogPane = currentAlert.getDialogPane();
        dialogPane.setStyle("-fx-font-size: 25px;-fx-font-weight: bold;");

        dialogPane.setMinHeight(60);
        dialogPane.setMinWidth(550);

        // Add an icon to the dialog
        Image image = new Image("file:///D:/MO3 LAP/Information retrieval/Lamiaa_20200865/GUI/warning_icon2.png"); // Replace "file:///D:/path_to_your_icon/icon.png" with the path to your PNG icon
        ImageView imageView = new ImageView(image);
        dialogPane.setGraphic(imageView);

        Stage stage = (Stage) currentAlert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("file:///D:/MO3 LAP/Information retrieval/Lamiaa_20200865/GUI/warning_icon2.png"));
        currentAlert.showAndWait();
    }

    @FXML
    protected void onCrawlButtonClick(ActionEvent event) throws IOException {

        String num_text = num_pages.getText();
        try {
            String seedUrl = Seed_link.getText().trim();
            if (seedUrl.isEmpty()) {
                showWarning("Please enter a seed URL to crawl.");
                return;
            }
            if (crawlFolder == null) {
                showWarning("Please choose a folder to save crawled content.");
                return;
            }
            File fileToDelete = new File(urls_file_path);
            fileToDelete.delete();
            num_of_pages = Integer.parseInt(num_text);
            if (num_of_pages<0){
                showWarning("Please enter a positive number of pages.");
                return;
            }
            new Thread(() -> {
                try {

                    Platform.runLater(() -> {
                        crawled_area.setText("Crawler is working...");

                    });
                    crawlAndSave(seedUrl);
                    Platform.runLater(() -> {
                        crawled_area.setText("Crawler has finished his job :)");

                    });
                    visitedUrls = new HashSet<>();
                    n =0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (NumberFormatException e) {
            showWarning("Please enter a valid number of pages to crawl.");
            return;
        }

    }
    private void crawlAndSave(String url) throws IOException {
        if (n == num_of_pages) {
            return;
        }

        if (visitedUrls.contains(url)) {
            return;
        }

        visitedUrls.add(url);

        try {
            Document doc = Jsoup.connect(url).get();

            // Extract links for further crawling
            Element contentDiv = doc.select("div#mw-content-text > div.mw-content-ltr.mw-parser-output").first();
            if (contentDiv != null) {
                // Extract text content from the <p> tags within the div
                String textContent = contentDiv.select("p").text();
                // Save crawled text to a file
                String filename = String.format("c%s",n+1) + ".txt";
                Inverted_Crawler.appendOrReplaceLineInFile(urls_file_path, String.format("%s",url), n);
                n+=1;
                saveToFile(crawlFolder  + filename, textContent);
                //System.out.println(n);

                // Extract links for further crawling
                Elements links = contentDiv.select("p > a[href]");
                for (Element link : links) {
                    String href = link.attr("href");
                    if (href.startsWith("/") && !href.startsWith("/wiki/Special:")&& !href.startsWith("/wiki/Wikipedia:")&& !href.startsWith("/wiki/Help:")&&!href.startsWith("//upload")) { // Check for internal Wikipedia links (avoid special pages)
                        String fullUrl = "https://en.wikipedia.org" + href;
                        crawlAndSave(fullUrl);

                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error crawling URL: " + url + "\n" + e);
        }
    }
    private void saveToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content.getBytes());
        }
    }
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Crawling Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    protected void onCrawlIndexingButtonClick(ActionEvent event) throws IOException {
        indexed_area.setText("Indexing...");

        new Thread(() -> {
            // collect files paths
            File file = new File(crawlFolder);
            String[] fileList = file.list();
            crawl_indexer = new invertedIndex.Inverted_Crawler();
            fileList = crawl_indexer.sort(fileList);
            crawl_indexer.setN(fileList.length);
            for (int i = 0; i < fileList.length; i++) {
                fileList[i] = crawlFolder + fileList[i];
            }
            crawl_indexer.buildIndex(fileList);
            crawl_indexer.store("index");

            Platform.runLater(() -> {
                indexed_area.setText("Done indexing :)");
            });
        }).start();
    }
    private void applyButtonEffects(Button button) {
        button.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                button.setStyle("-fx-background-color: #2F4F4F; -fx-text-fill: #FFFFFF; -fx-background-radius: 30;");
            }
        });

        button.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                button.setStyle("-fx-background-color:  #48D1CC; -fx-text-fill: #0b1c67; -fx-background-radius: 30;");
            }
        });

        button.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                button.setStyle("-fx-background-color: #5F9EA0; -fx-text-fill: #FFFFFF; -fx-background-radius: 30;");
                button.setTranslateY(2);
            }
        });

        button.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                button.setStyle("-fx-background-color:  #48D1CC; -fx-text-fill: #0b1c67; -fx-background-radius: 30;");
                button.setTranslateY(0);
            }
        });
    }



}