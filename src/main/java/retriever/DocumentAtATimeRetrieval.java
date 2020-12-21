package retriever;

import indexer.PostingsList;
import indexer.SimpleIndex;
import scorer.Scorer;

import java.io.IOException;
import java.util.*;


public class DocumentAtATimeRetrieval extends Retriever{
    public List<Entry> retrieval(SimpleIndex index, List<String> Q, int I, Scorer G, Scorer F, int k) throws IOException {
        List<PostingsList> L = new ArrayList<PostingsList>();
        ArrayList<Map<Integer, Integer>> result = new ArrayList<Map<Integer, Integer>>();
        PriorityQueue<Entry> R = new PriorityQueue();
        for(String term: Q){
            L.add(index.get(term));
        }
        for(int docID = 1; docID<=I;docID++){
            int sd = 0;
            for(PostingsList l:L){
                if(l.getCurr_pos()==null) continue;
                if(l.getCurr_pos().getDocID()==docID){
                    sd += G.score(Q,l.getCurr_pos(),null)*F.score(Q,l.getCurr_pos(),null);
                }
                l.movePastDocument(docID);
            }
            R.add(new Entry(docID,sd));
            if(R.size()>k){
                R.poll();
            }
        }
        List<Entry> output = new ArrayList<Entry>();
        while (!R.isEmpty()){
            output.add(R.poll());
        }
        Collections.reverse(output);
        return output;
    }
}
