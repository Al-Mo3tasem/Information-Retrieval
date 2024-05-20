/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author ehab
 */
public class Main {

    public static void main(String args[]) throws IOException {
        // OBJECT OF EACH INDEXING TYPE
        Index5 index = new Index5();
        Index5_BiWord index_biWord = new Index5_BiWord();
        Index5_Positional index_Positional = new Index5_Positional();

        // DATA FILES
        String files = "D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\ALL_Indexing_methods\\tmp11\\rl\\collection_test\\";


        File file = new File(files);
        String[] fileList = file.list();
        fileList = index.sort(fileList);
        index.N = fileList.length;

        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = files + fileList[i];
        }

        // PRINT DICTIONARIES
        index.buildIndex(fileList);
        index.store("index");
        int terms1 = index.printDictionary();

        index_biWord.buildIndex(fileList);
        index_biWord.store("index");
        int terms2 = index_biWord.printDictionary();

        index_Positional.buildIndex(fileList);
        index_Positional.store("index");
        int terms3 = index_Positional.printDictionary();

        System.out.println("------------------------------------------------------");
        System.out.println("*** Basic indexing Number of terms = " + terms1);
        System.out.println("------------------------------------------------------");
        System.out.println("*** BiWord indexing Number of terms = " + terms2);
        System.out.println("------------------------------------------------------");
        System.out.println("*** Positional indexing Number of terms = " + terms3);
        System.out.println("------------------------------------------------------");


        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String phrase = "";
        int choice = 0;  // Initialize choice to a default value

        do {
            System.out.println("Enter your choice (1: Basic Indexing, 2: BiWord Indexing, 3: Positional Indexing, 4: Exit): ");
            String input = in.readLine();
            if (input.isEmpty() || (!input.equals("1") && !input.equals("2") && !input.equals("3") && !input.equals("4"))) {
                System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
                continue;
            }
            choice = Integer.parseInt(input);

            switch (choice) {
                case 1:
                case 2:
                case 3:
                    do {
                        System.out.println("\n" + "Enter the query: (!EXIT! --> to exit the program | Empty query --> to choose another method) ");
                        phrase = in.readLine().trim();
                        if (phrase.isEmpty()) {
                            System.out.println("Query cannot be empty. Please re choose the method and enter a valid query.");
                            continue;
                        }
                        if (phrase.equals("!EXIT!")) {
                            System.out.println("Exiting the program.");
                            System.exit(0);
                        }
                        System.out.println("\n" + "Search results:");
                        String searchResult="";
                        if (choice==1){
                             searchResult =  index.find_24_01(phrase);
                        }
                        else if (choice==2){
                             searchResult =  index_biWord.find_24_01(phrase);
                        }
                        else{
                             searchResult =  index_Positional.find_24_01(phrase);
                        }
                        System.out.println(searchResult + "\n");
                    } while (!phrase.isEmpty());
                    break;

                case 4:
                    System.out.println("Exiting the program.");
                    break;
            }
        } while (choice != 4);
    }
}