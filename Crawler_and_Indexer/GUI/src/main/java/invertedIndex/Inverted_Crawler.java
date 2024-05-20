/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;
import org.example.gui.HelloController;

import java.io.*;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.Math.log10;
import static java.lang.Math.sqrt;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.text.DecimalFormat;

/**
 *
 * @author ehab
 */
public class Inverted_Crawler {

    //--------------------------------------------
    public int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    // Replace HashMap with TreeMap to make it sorted by default
    public TreeMap<String, DictEntry> index; // THe inverted index
    public TreeMap<Integer, String> docs_names;
    public String dict="";

    public DecimalFormat df = new DecimalFormat("#.#####");

    //--------------------------------------------

    public Inverted_Crawler() {
        // Replace HashMap with TreeMap to make it sorted by default
        sources = new HashMap<>();
        index = new TreeMap<String, DictEntry>();
        docs_names = new TreeMap <Integer, String>();
        df.setRoundingMode(RoundingMode.CEILING);
        // Initialize the dictionary
    }

    public void setN(int n) {
        N = n;
        for (int i = 0; i < N; i++) {
            docs_names.put(i, "c" + (i + 1));
        }
    }



    //-----------------------------------------------
    // Here we store the files info such as length and id in sources object where fid is the key and (Filename,Filename, "notext") is the value
    public void buildIndex(String[] files) {  // from disk not from the internet
        int fid = 0;
        for (String fileName : files) {
            //System.out.println(fileName);
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln;
                int flen = 0;
                while ((ln = file.readLine()) != null) {
                    /// -2- **** complete here ****
                    flen += indexOneLine(ln, fid);
                    ///**** hint   flen +=  ________________(ln, fid);
                }
                sources.get(fid).length = flen;

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;
        }
        //   printDictionary();
    }

    //----------------------------------------------------------------------------  
    public int indexOneLine(String ln, int fid) {
        int flen = 0;

        String[] words = ln.split("\\W+");
      //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                continue;
            }
            word = stemWord(word);
            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

        }
        return flen;
    }

//----------------------------------------------------------------------------  
    boolean stopWord(String word) {
        // Check for stop words
        String lowerCaseWord = word.toLowerCase(); // Ensure case-insensitive comparison
        Set<String> stopWords = Set.of("the", "to", "be", "for", "from", "in", "a", "into", "by", "or", "and", "that");
        if (stopWords.contains(lowerCaseWord)) {
            return true;
        }

        if (word.length()<2){
            return true;
        }
        // Check for numbers using regular expression
        // REMOVE NUMBERS
        return word.matches("^[0-9]+$");
    }
//----------------------------------------------------------------------------  

    String stemWord(String word) { //skip for now
        return word;
//        Stemmer s = new Stemmer();
//        s.addString(word);
//        s.stem();
//        return s.toString();
    }

    //----------------------------------------------------------------------------
    public static void appendOrReplaceLineInFile(String filePath, String lineToWrite, int lineNumber) throws IOException {

        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile(); // Create the file if it doesn't exist
            FileWriter writer = new FileWriter(file);
            writer.write(lineToWrite + "\n"); // Write the first line directly
            writer.close();

            return;
        }

        StringBuilder newContent = new StringBuilder();
        String currentLine;
        int currentLineNumber = 1;
        BufferedReader reader = new BufferedReader(new FileReader(file));

        while ((currentLine = reader.readLine()) != null) {
            newContent.append(currentLine).append("\n"); // Append existing line

            if (currentLineNumber == lineNumber) {
                newContent.append(lineToWrite).append("\n"); // Append line to write (if target line)
            }

            currentLineNumber++;
        }

        reader.close();

        if (currentLineNumber < lineNumber) {
            // Line number is beyond existing content, append the line
            newContent.append(lineToWrite).append("\n");
        }
        FileWriter writer = new FileWriter(file);
        writer.write(newContent.toString());
        writer.close();
    }
    public String cosine_similarity(String phrase){
        String result ="";

        // split query to words

        String[] words = phrase.split("\\W+");
        //remove stop words
        for (int i = 0; i < words.length; i++){
            if (stopWord(words[i])) {
                List<String> tempList = new ArrayList<>(Arrays.asList(words));
                tempList.subList(i, i + 1).clear();
                words = tempList.toArray(new String[0]);
                i--;
            }
        }
        //length of query
        int len = words.length;
        if (len== 0){
            return "No such a document!!\n\nDon't enter a query filled\nof stop words or numbers!";
        }


        //add docs lengths list
        int[] docLengths = new int[N];
        //store docs lengths
        for (int i=0;  i<N;i++){
            docLengths[i] = sources.get(i).length;
        }

        //initialize scores list
        double scores[] = new double[N];

        boolean anyTermFound = false;

        //loop over the terms and update each document score based on the term.
        int i = 0;
        while (i < len){
            //System.out.println("word:  "+words[i]);

            DictEntry term_data = index.get(words[i].toLowerCase());
            if (term_data == null) {
                // Skip to the next term if not found
                i++;
                continue;
            }

            anyTermFound = true;
            // get document frequency
            int tdf = term_data.doc_freq;

            // loop over posting list to update the scores
            Posting posting = term_data.pList;
            while (posting != null) {
                int docId = posting.docId;// get doc id
                int dtf = posting.dtf; // Term frequency in this document

                // TF-IDF = 1+ [log(DTF(freq of term in a doc)] * [log(Num of docs/num of docs hold the term)]
                // Calculate term weight and add to document score
                double weight = (1 + log10((double) dtf)) * log10(N / (double) tdf); // TF-IDF weight
                //System.out.println("docId: " + weight + "   " + words[i]);
                //System.out.println("weight: "+weight);
                scores[docId] += weight;
                posting = posting.next;
            }



            i++;
        }

        if (!anyTermFound) {
            return "No documents match the query.\n\nAll scores = 0.\n\nThe query terms might not\n exist in any document";
        }
        // Normalize scores by document lengths
        for (int j= 0; j < N; j++) {
            if (docLengths[j] > 0) {
                scores[j] /= docLengths[j];
            }
        }



        List<Map.Entry<Integer, Double>> scoreEntries = new ArrayList<>();
        for (int j = 0; j < N; j++) {
            scoreEntries.add(new AbstractMap.SimpleEntry<>(j, scores[j]));
        }

        // Sort the list in descending order by score
        scoreEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        int topN=10;
        for (Map.Entry<Integer, Double> entry : scoreEntries) {
            Double score = entry.getValue();
            //System.out.println("Document: " + docs_names.get(entry.getKey()) + ", Score: " + score);
            String line="";
            try {
                line = getLineByNumber(HelloController.urls_file_path,entry.getKey()+1);
            } catch (IOException e) {
                line= "NULL";
            }
            result += String.format("%s: %s.txt, Score: %.5f, %s",(11-topN),docs_names.get(entry.getKey()),score,line);
            result+='\n';
            topN-=1;
            if(topN==0){
                break;
            }
        }
        return result;

    }

    public static String getLineByNumber(String filePath, int lineNumber) throws IOException {
        String line;
        int currentLineNumber = 1;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while ((line = reader.readLine()) != null) {
                if (currentLineNumber == lineNumber) {
                    return line;
                }
                currentLineNumber++;
            }
        }

        // Line not found
        return null;
    }



    //---------------------------------
    public String[] sort(String[] words) {
        boolean sorted = false;
        String sTmp;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                // Extract numbers from the strings
                int num1 = Integer.parseInt(words[i].replaceAll("\\D", ""));
                int num2 = Integer.parseInt(words[i + 1].replaceAll("\\D", ""));
                if (num1 > num2) {
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }


    //---------------------------------

    public void store(String storageName) {
        try {
            String pathToStorage = "D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\ALL_Indexing_methods\\tmp11_crawler\\rl\\"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().URL + ", Value = " + entry.getValue().title + ", Value = " + entry.getValue().text);
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); //String formattedDouble = String.format("%.2f", fee );
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            Iterator it = index.entrySet().iterator();



            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    wr.write(p.docId + "," + p.dtf + ":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============END STORE Crawler Index=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//=========================================    

}

//=====================================================================
