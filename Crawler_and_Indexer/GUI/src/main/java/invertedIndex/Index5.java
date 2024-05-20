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
    public int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    // Replace HashMap with TreeMap to make it sorted by default
    public TreeMap<String, DictEntry> index; // THe inverted index
    public TreeMap<Integer, String> docs_names;
    public String dict="";

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
    public String printPostingList(Posting p) {
        String ans = "";
        ans+="[";
        while (p != null) {
            /// -4- **** complete here ****
            // fix get rid of the last comma
            if(p.next == null){
                ans+=String.format("%s",(p.docId+1));
            }
            else{
                ans+=String.format("%s,",p.docId+1);
            }
            p = p.next;
        }
        ans+="]";
        return ans;
    }

    //---------------------------------------------
    public int printDictionary() {
        Iterator it = index.entrySet().iterator();


        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue();
            dict+=String.format("[ %s, %s ]►►►► ",pair.getKey(),dd.doc_freq);
            dict+= printPostingList(dd.pList);
            dict+='\n';
        }

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
        phrase = phrase.replaceAll("[^a-zA-Z0-9 ]", "");

        String[] words = phrase.split("\\W+");

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
            return "No such a document!!\n\nDon't enter a query filled\nof stop words or numbers!";
        }
        DictEntry temp = index.get(words[0].toLowerCase());
        if (temp==null) {
            return String.format("No such a document :(\n\nthe word '%s'\ndoesn't exist in any document!", words[0]);
        }

        //fix this if word is not in the hash table will crash...
        Posting posting = temp.pList;

        int i = 1;
        while (i < len) {
            DictEntry temp2 = index.get(words[i].toLowerCase());
            if (temp2==null) {
                return String.format("No such a document :(\n\nbecause the word '%s'\ndoesn't exist in any document!", words[i]);
            }
            posting = intersect(posting, temp2.pList);
            i++;
        }
        while (posting != null) {
            //result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
            result += String.format("\n%s", docs_names.get(posting.docId));

            posting = posting.next;
        }
        if (result == "The query found in: "){
            return "No such a document :(";
        }
        return result;
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

//----------------------------------------------------      

}

//=====================================================================
