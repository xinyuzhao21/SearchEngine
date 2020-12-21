package indexer;

import compression.Delta;

import java.util.ArrayList;
import java.util.List;

public class Postings {
    private int docID;
    private ArrayList<Integer> positions = new ArrayList<Integer>();
    private int curr_index;

    public Postings(int docID){
        this.docID = docID;
    }
    public void add( int pos){
        positions.add(pos);
    }
    public void resetPos(){
        curr_index=0;
    }
    public int getCurr_pos() {
        if(curr_index>=positions.size())
            return -1;
        return positions.get(curr_index);
    }

    public static Postings fromIntegerList(List<Integer> list, int compress_flag){
        Postings newPosting= new Postings(list.get(0));
        Delta delta = new Delta();
        for(int i =2; i<list.size();i++){
            int temp = list.get(i);
            if(compress_flag==1){
                temp=delta.decode(temp);
            }
            newPosting.add(temp);
        }
        return  newPosting;
    }

    public ArrayList<Integer> toIntegerList(int compress_flag){
        ArrayList<Integer> newList = new ArrayList<Integer>();
        newList.add(docID);
        newList.add(positions.size());
        if(compress_flag==1){
            Delta delta = new Delta();
            List<Integer> list=(ArrayList<Integer>) delta.encodeInts(positions);
            newList.addAll(list);
        }
        else
            newList.addAll(positions);
        return newList;
    }

    public int getTermFreq(){
        return positions.size();
    }

    public int getDocID() {
        return docID;
    }

    public int skipTo(int pos){
        if(curr_index>=positions.size()){
            return -1;
        }
        while(positions.get(curr_index)<pos){
            curr_index++;
            if(curr_index>=positions.size()){
                return -1;
            }
        }
        return 0;
    }

    public int movePastPos(int pos){
        if(curr_index>=positions.size()){
            return -1;
        }
        while(positions.get(curr_index)<=pos){
            curr_index++;
            if(curr_index>=positions.size()){
                return -1;
            }
        }
        return 0;
    }
}
