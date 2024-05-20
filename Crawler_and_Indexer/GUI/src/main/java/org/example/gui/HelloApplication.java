package org.example.gui;
import invertedIndex.Index5;
import invertedIndex.Index5_BiWord;
import invertedIndex.Index5_Positional;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloApplication extends Application {
    public  static Index5 index = new invertedIndex.Index5();
    public  static Index5_BiWord index_biWord = new invertedIndex.Index5_BiWord();
    public  static Index5_Positional index_Positional = new invertedIndex.Index5_Positional();

    public static int terms1,terms2,terms3;
    public static String dict1,dict2,dict3;
    public static  Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Image applicationIcon = new Image("file:///D:/MO3 LAP/Information retrieval/Crawler_and_Indexer/GUI/icons8-app-64.png"); // Replace "file:///D:/path_to_your_icon/icon.png" with the path to your icon file
        scene = new Scene(fxmlLoader.load(), 1000, 1000);
        stage.getIcons().add(applicationIcon);
        stage.setTitle("Crawler & Indexer");
        stage.setScene(scene);
        stage.show();
    }
    @Override
    public void init() throws Exception{
        String files = "D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\ALL_Indexing_methods\\tmp11\\rl\\collection\\";
        File file = new File(files);
        String[] fileList = file.list();
        fileList = index.sort(fileList);
        index.setN(fileList.length);
        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = files + fileList[i];
        }
        // PRINT DICTIONARIES
        index.buildIndex(fileList);
        index.store("index");
        terms1 = index.printDictionary();
        index_biWord.buildIndex(fileList);
        index_biWord.store("index");
        terms2 = index_biWord.printDictionary();
        index_Positional.buildIndex(fileList);
        index_Positional.store("index");
        terms3 = index_Positional.printDictionary();
        dict1 = index.dict;
        dict2 = index_biWord.dict;
        dict3 = index_Positional.dict;
    }
    public static void main(String[] args) {

        launch();
    }
}