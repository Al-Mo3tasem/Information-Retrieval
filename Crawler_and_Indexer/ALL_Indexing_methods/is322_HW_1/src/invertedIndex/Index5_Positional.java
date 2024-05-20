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
public class Index5_Positional {

    //--------------------------------------------
    int N = 0;
    public Map<Integer, SourceRecord_Positional> sources;  // store the doc_id and the file name.

    // Replace HashMap with TreeMap to make it sorted by default
    public TreeMap<String, DictEntry_Positional> index; // THe inverted index
    public TreeMap<Integer, String> docs_names;

    //--------------------------------------------

    public Index5_Positional() {
        // Replace HashMap with TreeMap to make it sorted by default
        sources = new HashMap<>();
        index = new TreeMap<String, DictEntry_Positional>();
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
    public void printPostingList(Posting_Positional p) {
        System.out.print("[");
        while (p != null) {
            System.out.print("(" + p.docId + ", " + p.freq + ", " + p.positions + ")");
            if (p.next != null) {
                System.out.print(", ");
            }
            p = p.next;
        }
        System.out.println("]");
    }

    public int printDictionary() {
        Iterator<Map.Entry<String, DictEntry_Positional>> it = index.entrySet().iterator();
        System.out.print("**** START Positional inverted Index ****");

        while (it.hasNext()) {
            Map.Entry<String, DictEntry_Positional> pair = it.next();
            String term = pair.getKey();
            DictEntry_Positional dictEntry = pair.getValue();

            System.out.print("** [" + term + ", " + dictEntry.doc_freq + "] =--> ");
            printPostingList(dictEntry.pList);
        }
        System.out.print("**** DONE Positional inverted Index ****");

        return index.size();
    }
 
    //-----------------------------------------------
    // Here we store the files info such as length and id in sources object where fid is the key and (Filename,Filename, "notext") is the value
    public void buildIndex(String[] files) {  // from disk not from the internet
        int fid = 0;
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord_Positional(fid, fileName, fileName, "notext"));
                }
                String ln;
                int flen = 1;
                while ((ln = file.readLine()) != null) {
                    /// -2- **** complete here ****
                    flen = indexOneLine(ln, fid,flen);
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
    public int indexOneLine(String ln, int docId,int l) {
        int position = l; // Initialize position counter for each document

        String[] words = ln.split("\\W+");

        //  REMOVE STOPWORDS
        for (int i = 0; i < words.length; i++){
            if (stopWord(words[i])) {
                List<String> tempList = new ArrayList<>(Arrays.asList(words));
                tempList.subList(i, i + 1).clear();
                words = tempList.toArray(new String[0]);
                i--;
            }
        }

        for (String word : words) {
            word = word.toLowerCase();

            word = stemWord(word);

            if (!index.containsKey(word)) {
                index.put(word, new DictEntry_Positional());
            }

            DictEntry_Positional dictEntry = index.get(word);
            if (!dictEntry.postingListContains(docId)) {
                //dictEntry.doc_freq += 1;
                dictEntry.addPosting(docId, 1, new ArrayList<>(Arrays.asList(position)));
            } else {
                Posting_Positional posting = dictEntry.pList;
                while (posting != null && posting.docId != docId) {
                    posting = posting.next;
                }
                if (posting != null) {
                    posting.freq += 1;
                    posting.positions.add(position);
                }
            }

            dictEntry.term_freq += 1;
            position++; // Increment position for the next word
        }
        return position;
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



    public String find_24_01(String phrase) { // any number of terms non-optimized search
        String result = "The query found in: ";
        String[] words = phrase.split("\\W+");

        //REMOVE STOPWORDS
        for (int i = 0; i < words.length; i++){
            if (stopWord(words[i])) {
                List<String> tempList = new ArrayList<>(Arrays.asList(words));
                tempList.subList(i, i + 1).clear();
                words = tempList.toArray(new String[0]);
                i--;
            }
        }

        int len = words.length;
        if (len== 0){
            return "No such a sentence! Did you a query filled of stop words or numbers?";
        }
        DictEntry_Positional dic1 = index.get(words[0].toLowerCase());
        if (dic1==null) {
            return String.format("No such a sentence because the word '%s' doesn't exist in any document!", words[0]);
        }
        Posting_Positional p1 = dic1.pList;
        if (len == 1){
            while (p1 != null) {
                //System.out.print("(" + p.docId + ", " + p.freq + ", " + p.positions + ")");
                for (int j = 0; j < p1.positions.size(); j++) {
                    p1.positions.set(j, p1.positions.get(j) - len + 1);
                }
                result += String.format("** document: #%s in positions: %s **", docs_names.get(p1.docId),p1.positions);
                p1 = p1.next;
            }
            return result;
        }
        Posting_Positional found_list = p1;
        int i=1;
        while (i<len){
            DictEntry_Positional dic_temp = index.get(words[i].toLowerCase());
            if (dic_temp==null) {
                return String.format("No such a sentence because the word '%s' doesn't exist in any document!", words[0]);
            }
            Posting_Positional p_temp = dic_temp.pList;
            found_list = findOrderedPair(found_list,p_temp);
            if (found_list == null){
                return "No such a sentence!";
            }
            printPostingList(found_list);
            i++;
        }

        while (found_list != null) {
            for (int j = 0; j < found_list.positions.size(); j++) {
                found_list.positions.set(j, found_list.positions.get(j) - len + 1);
            }
            result += String.format("** document: #%s in positions: %s **", docs_names.get(found_list.docId),found_list.positions);
            found_list = found_list.next;
        }

        return result;


    }

    Posting_Positional findOrderedPair(Posting_Positional pList1, Posting_Positional pList2) {
        Posting_Positional result = null;
        Posting_Positional last = null;

        Posting_Positional p1 = pList1;
        Posting_Positional p2 = pList2;

        while (p1 != null && p2 != null) {
            if (p1.docId == p2.docId) {
                List<Integer> positions1 = p1.positions;
                List<Integer> positions2 = p2.positions;
                List<Integer> newPositions = new ArrayList<>();
                for (int pos1 : positions1) {
                    for (int pos2 : positions2) {
                        if (pos1 + 1 == pos2) {
                            newPositions.add(pos2);
                            break;
                        }
                    }
                }
                if (!newPositions.isEmpty()) {
                    Posting_Positional newPosting = new Posting_Positional(p1.docId, newPositions.size(), newPositions);
                    if (result == null) {
                        result = newPosting;
                        last = result;
                    } else {
                        last.next = newPosting;
                        last = last.next;
                    }
                }
                p1 = p1.next;
                p2 = p2.next;
            }
            else if(p1.docId<p2.docId){
                p1 = p1.next;
            }
            else {
                p2 = p2.next;
            }
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
            String pathToStorage = "D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\ALL_Indexing_methods\\tmp11_positional\\rl\\" + storageName;
            Writer wr = new FileWriter(pathToStorage);

            // Write document information
            for (Map.Entry<Integer, SourceRecord_Positional> entry : sources.entrySet()) {
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ",");
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            // Write inverted index information
            for (Map.Entry<String, DictEntry_Positional> entry : index.entrySet()) {
                DictEntry_Positional dd = entry.getValue();
                wr.write(entry.getKey() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting_Positional p = dd.pList;
                while (p != null) {
                    wr.write(p.docId + "," + p.freq + ":");
                    for (int position : p.positions) {
                        wr.write(position + ",");
                    }
                    wr.write(";");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============END STORE Positional Index=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//=========================================    
    public boolean storageFileExists(String storageName){
        java.io.File f = new java.io.File("D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\ALL_Indexing_methods\\tmp11_positional\\rl\\"+storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;
            
    }
//----------------------------------------------------    
    public void createStore(String storageName) {
        try {
            String pathToStorage = "D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\ALL_Indexing_methods\\tmp11_positional\\"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            wr.write("end" + "\n");
            wr.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//----------------------------------------------------      
     //load index from hard disk into memory
    public TreeMap<String, DictEntry_Positional> load(String storageName) {
        try {
            String pathToStorage = "D:\\MO3 LAP\\Information retrieval\\Crawler_and_Indexer\\ALL_Indexing_methods\\tmp11_positional\\rl\\"+storageName;
            sources = new TreeMap<Integer, SourceRecord_Positional>();
            index = new TreeMap<String, DictEntry_Positional>();
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

                    SourceRecord_Positional sr = new SourceRecord_Positional(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ','));
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
                index.put(ss1a[0], new DictEntry_Positional(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
                String[] ss1bx;   //posting
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");
                    if (index.get(ss1a[0]).pList == null) {
                        index.get(ss1a[0]).pList = new Posting_Positional(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).pList;
                    } else {
                        index.get(ss1a[0]).last.next = new Posting_Positional(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
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
