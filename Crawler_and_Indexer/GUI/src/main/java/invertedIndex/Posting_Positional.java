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

public class Posting_Positional {
    public Posting_Positional next = null;
    int docId;
    int freq;
    List<Integer> positions;

    Posting_Positional(int id, int f, List<Integer> p) {
        docId = id;
        freq = f;
        positions = p;
    }

    Posting_Positional(int id, int f) {
        docId = id;
        freq = f;
        positions = new ArrayList<>();
    }

    Posting_Positional(int id) {
        docId = id;
        freq = 1;
        positions = new ArrayList<>();
    }
}