package indexer;

import compression.Delta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PostingsList {
    private List<Postings> plist = new ArrayList<Postings>();
    private Postings tail_pos;
    private Postings curr_pos;
    private int df;
    private Iterator<Postings> iterator;
    private String term =null;

    public PostingsList(){

    }

    public PostingsList(List<Postings> plist){
        this.plist = plist;
        this.iterator = plist.listIterator();
        this.curr_pos=nextPosting();
    }
    public Postings getCurr_pos() {
        return curr_pos;
    }



    public Postings getTail_pos() {
        return tail_pos;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTerm() {
        return term;
    }

    public List<Postings> getPlist() {
        return plist;
    }

    public int getCf() {
        int result =0;
        for(Postings p: plist){
            result+=p.getTermFreq();
        }
        return result;
    }

    public int getDf() {
        return plist.size();
    }

    public void addPostings(Postings p){
        plist.add(p);
    }
    public void add(int docID, int pos){
        if (tail_pos==null||tail_pos.getDocID()!=docID){
            Postings newPos = new Postings(docID);
            plist.add(newPos);
            tail_pos =newPos;
            this.df++;
        }
        tail_pos.add(pos);
    }

    public void resetIterator(){
        this.iterator = plist.listIterator();
        this.curr_pos=nextPosting();
    }
    public int skipTo(int docID){
        if(curr_pos==null){
            return -1;
        }
        while (curr_pos.getDocID()<docID){
            curr_pos = nextPosting();
            if(curr_pos==null){
                return -1;
            }
        }
        return 0;
    }

    public int movePastDocument(int docID){
        if(curr_pos==null){
            return -1;
        }
        while (curr_pos.getDocID()<=docID){
            curr_pos = nextPosting();
            if(curr_pos==null){
                return -1;
            }
        }
        return 0;
    }

    public int doclength(){
        return 0;
    }

    public ArrayList<Integer> toIntegerList(int compress_flag){
        ArrayList<Integer> longList = new ArrayList<Integer>();
        Delta delta = new Delta();
        for(Postings p:plist){
            ArrayList<Integer> temp = p.toIntegerList(compress_flag);
            if(compress_flag==1) {
                temp.set(0, delta.encode(temp.get(0)));
            }
            longList.addAll(temp);
        }
        return longList;
    }
    public static PostingsList fromIntegerList(List<Integer> list,int compress_flag){
        Delta delta =new Delta();
        List<Postings> postingsList = new ArrayList<Postings>();
        int context = 0, docID=0,pcount=0;
        for(int i =0;i<list.size();i++){
            if(context==0){
                docID=list.get(i);
                if(compress_flag==1){
                    docID = delta.decode(docID);
                }
                list.set(i,docID);
            }
            if(context==1){
                pcount=list.get(i);
            }
            if(context==2){
               postingsList.add( Postings.fromIntegerList(list.subList(i-2,i+pcount),compress_flag));
               i--;
               i+=pcount;
            }
            context++;
            context%=3;
        }
        return new PostingsList(postingsList);
    }

    public Postings nextPosting(){
        if(!iterator.hasNext()){
            curr_pos=null;
            return null;
        }
        curr_pos =iterator.next();
        return curr_pos;
    }
}
