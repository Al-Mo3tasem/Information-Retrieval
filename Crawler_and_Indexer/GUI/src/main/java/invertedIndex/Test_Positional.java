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
public class Test_Positional {

    public static void main(String args[]) throws IOException {
        Index5_Positional index = new Index5_Positional();
        //|**  change it to your collection directory 
        //|**  in windows "C:\\tmp11\\rl\\collection\\"       
        String files = "D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\ALL_Indexing_methods\\tmp11_positional\\rl\\collection\\";


        File file = new File(files);
        //|** String[] 	list()
        //|**  Returns an array of strings naming the files and directories in the directory denoted by this abstract pathname.
        String[] fileList = file.list();
        index.N = fileList.length;

        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = files + fileList[i];
        }
        index.buildIndex(fileList);
        index.store("index");
        int terms = index.printDictionary();
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + terms);
        //String test3 = "data  should plain greatest comif"; // data  should plain greatest comif
        //System.out.println("Boo0lean Model result = \n" + index.find_24_01(test3));

        String phrase = "";

        do {
            System.out.println("\n" + "Print search phrase: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            phrase = in.readLine();
            // Perform search and display results only if a phrase is entered
            if (!phrase.isEmpty()) {
                System.out.println("\n"+"Search results:");
                String searchResult = index.find_24_01(phrase);
                System.out.println(searchResult+"\n");
            }

/// -3- **** complete here ****
        } while (!phrase.isEmpty());
    }
}