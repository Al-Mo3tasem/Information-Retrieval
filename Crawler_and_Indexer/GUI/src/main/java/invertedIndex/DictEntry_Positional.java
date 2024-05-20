/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ehab
 */
public class DictEntry_Positional {
    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
    public Posting_Positional pList = null;
    public Posting_Positional last = null;

    boolean postingListContains(int i) {
        boolean found = false;
        Posting_Positional p = pList;
        while (p != null) {
            if (p.docId == i) {
                return true;
            }
            p = p.next;
        }
        return found;
    }

    int getPosting(int i) {

        int found = 0;
        Posting_Positional p = pList;
        while (p != null) {
            if (p.docId >= i) {
                if (p.docId == i) {
                    return p.freq;
                } else {
                    return 0;
                }
            }
            p = p.next;
        }
        return found;
    }

    void addPosting(int i, int f, List<Integer> positions) {
        if (pList == null) {
            pList = new Posting_Positional(i, f, positions);
            last = pList;
        } else {
            last.next = new Posting_Positional(i, f, positions);
            last = last.next;
        }
        doc_freq++;
        term_freq += f;
    }

    DictEntry_Positional() {
    }

    DictEntry_Positional(int df, int tf) {
        doc_freq = df;
        term_freq = tf;
    }
}
