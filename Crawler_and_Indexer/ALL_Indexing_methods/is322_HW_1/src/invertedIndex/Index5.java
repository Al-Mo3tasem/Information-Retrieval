/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Math.log10;
import static java.lang.Math.sqrt;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import java.io.PrintWriter;

/**
 *
 * @author ehab
 */
public class Index5 {

    //--------------------------------------------
    int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    // Replace HashMap with TreeMap to make it sorted by default
    public TreeMap<String, DictEntry> index; // THe inverted index
    public TreeMap<Integer, String> docs_names;

    //--------------------------------------------

    public Index5() {
        // Replace HashMap with TreeMap to make it sorted by default
        sources = new HashMap<>();
        index = new TreeMap<String, DictEntry>();
        docs_names = new TreeMap <Integer, String>();

        // Initialize the dictionary
        for (int i = 0; i <= 6; i++) {
            docs_names.put(i, "p" + (i + 1));
        }
    }

    public void setN(int n) {
        N = n;
    }


    //---------------------------------------------
    // This function Iterates over the posting list of a word and print it
    public void printPostingList(Posting p) {
        // Iterator<Integer> it2 = hset.iterator();

        System.out.print("[");
        while (p != null) {
            /// -4- **** complete here ****
            // fix get rid of the last comma
            if(p.next == null){
                System.out.print("" + p.docId);

            }
            else{
                System.out.print("" + p.docId + "," );
            }
            p = p.next;
        }
        System.out.println("]");
    }

    //---------------------------------------------
    public int printDictionary() {
        Iterator it = index.entrySet().iterator();


        System.out.print("**** START Basic inverted Index ****");

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> ");
            printPostingList(dd.pList);
        }
        System.out.print("****** DONE Basic inverted Index *******");

        return index.size();
    }
 
    //-----------------------------------------------
    // Here we store the files info such as length and id in sources object where fid is the key and (Filename,Filename, "notext") is the value
    public void buildIndex(String[] files) {  // from disk not from the internet
        int fid = 0;
        for (String fileName : files) {
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
    // Intersect function gets the intersect files that contain a pair of words in them
    // The intersection means the files that appear in both POSTING LISTS of the two words.
    // it does so by iterating over the two posting lists and when the two iterators be on same file id then add that file id to the final answer
    // if no intersection files were found, then it will return null
    Posting intersect(Posting pL1, Posting pL2) {
///****  -1-   complete after each comment ****
//   INTERSECT ( p1 , p2 )
//          1  answer ←      {}
        Posting answer = null;
        Posting last = null;

        // While neither posting list is exhausted
        while (pL1 != null && pL2 != null) {
            // If doc IDs are equal, add it to the answer list and move to the next element in both lists
            if (pL1.docId == pL2.docId) {
                if (answer == null) {
                    answer = new Posting(pL1.docId, pL1.dtf);
                    last = answer;
                } else {
                    //last = last.next;
                    last.next = new Posting(pL1.docId, pL1.dtf);
                    last = last.next;
                }
                pL1 = pL1.next;
                pL2 = pL2.next;
                // If pL1's docID is less than pL2's, move forward in pL1
            } else if (pL1.docId < pL2.docId) {
                pL1 = pL1.next;
                // Otherwise, move forward in pL2
            } else {
                pL2 = pL2.next;
            }
        }

        return answer;
    }

    public String find_24_01(String phrase) { // any mumber of terms non-optimized search 
        //String result = "";
        String result = "The query found in: ";

        String[] words = phrase.split("\\W+");
        int len = words.length;
        if (len== 0){
            return "No such a document! Did you a query filled of stop words or numbers?";
        }
        DictEntry temp = index.get(words[0].toLowerCase());
        if (temp==null) {
            return String.format("No intersection between words because the word '%s' doesn't exist in any document!", words[0]);
        }
        //else{
        //    System.out.println(temp+"HERE");
        //}
        //fix this if word is not in the hash table will crash...
        Posting posting = temp.pList;

        int i = 1;
        while (i < len) {
            DictEntry temp2 = index.get(words[i].toLowerCase());
            if (temp2==null) {
                return String.format("No intersection between words because the word '%s' doesn't exist in any document!", words[i]);
            }
            posting = intersect(posting, temp2.pList);
            i++;
        }
        while (posting != null) {
            //System.out.println("\t" + sources.get(num));
            //result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
            result += String.format("** document: %s **", docs_names.get(posting.docId));

            posting = posting.next;
        }
        return result;
    }

    //---------------------------------
    String[] sort(String[] words) {  //bubble sort
        boolean sorted = false;
        String sTmp;
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                int compare = words[i].compareTo(words[i + 1]);
                if (compare > 0) {
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
            String pathToStorage = "D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\ALL_Indexing_methods\\tmp11\\rl\\"+storageName;
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
            System.out.println(index.entrySet());



            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                //  System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    //    System.out.print( p.docId + "," + p.dtf + ":");
                    wr.write(p.docId + "," + p.dtf + ":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============END STORE Basic Index=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//=========================================    
    public boolean storageFileExists(String storageName){
        java.io.File f = new java.io.File("D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\ALL_Indexing_methods\\tmp11\\rl\\"+storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;
            
    }
//----------------------------------------------------    
    public void createStore(String storageName) {
        try {
            String pathToStorage = "D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\ALL_Indexing_methods\\tmp11\\"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            wr.write("end" + "\n");
            wr.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//----------------------------------------------------      
     //load index from hard disk into memory
    public TreeMap<String, DictEntry> load(String storageName) {
        try {
            String pathToStorage = "D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\ALL_Indexing_methods\\tmp11\\rl\\"+storageName;
            sources = new TreeMap<Integer, SourceRecord>();
            index = new TreeMap<String, DictEntry>();
            BufferedReader file = new BufferedReader(new FileReader(pathToStorage));
            String ln = "";
            int flen = 0;
            while ((ln = file.readLine()) != null) {
                if (ln.equalsIgnoreCase("section2")) {
                    break;
                }
                String[] ss = ln.split(",");
                int fid = Integer.parseInt(ss[0]);
                try {
                    System.out.println("**>>" + fid + " " + ss[1] + " " + ss[2].replace('~', ',') + " " + ss[3] + " [" + ss[4] + "]   " + ss[5].replace('~', ','));

                    SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ','));
                    //   System.out.println("**>>"+fid+" "+ ss[1]+" "+ ss[2]+" "+ ss[3]+" ["+ Double.parseDouble(ss[4])+ "]  \n"+ ss[5]);
                    sources.put(fid, sr);
                } catch (Exception e) {

                    System.out.println(fid + "  ERROR  " + e.getMessage());
                    e.printStackTrace();
                }
            }
            while ((ln = file.readLine()) != null) {
                //     System.out.println(ln);
                if (ln.equalsIgnoreCase("end")) {
                    break;
                }
                String[] ss1 = ln.split(";");
                String[] ss1a = ss1[0].split(",");
                String[] ss1b = ss1[1].split(":");
                index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
                String[] ss1bx;   //posting
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");
                    if (index.get(ss1a[0]).pList == null) {
                        index.get(ss1a[0]).pList = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).pList;
                    } else {
                        index.get(ss1a[0]).last.next = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).last.next;
                    }
                }
            }
            System.out.println("============= END LOAD =============");
            //    printDictionary();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
}

//=====================================================================
