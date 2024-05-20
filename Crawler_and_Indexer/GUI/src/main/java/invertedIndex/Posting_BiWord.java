/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

/**
 *
 * @author ehab
 */

public class Posting_BiWord {

    public Posting_BiWord next = null;
    int docId;
    int dtf = 1;

    Posting_BiWord(int id, int t) {
        docId = id;
        dtf=t;
    }

    Posting_BiWord(int id) {
        docId = id;
    }
}