package retriever;

import indexer.PostingsList;
import indexer.SimpleIndex;
import scorer.Scorer;

import java.io.IOException;
import java.util.*;

public class LanguageModelRetrieval extends Retriever {
    @Override
    public List<Entry> retrieval(SimpleIndex index, List<String> Q, int I, Scorer G, Scorer F, int k) throws IOException {
        List<PostingsList> L = new ArrayList<PostingsList>();
        ArrayList<Map<Integer, Integer>> result = new ArrayList<Map<Integer, Integer>>();
        PriorityQueue<Entry> R = new PriorityQueue();
        for(String term: Q){
            PostingsList l = index.get(term);
            l.setTerm(term);
            L.add(l);
        }
        for(int docID = 1; docID<=I;docID++){
            double sd = 0;
            boolean scored = false;
            for(PostingsList l:L){
                Map<String,Integer> params = new HashMap<>();
                params.put("tdf",0);
                params.put("tcf",index.getTermFreq(l.getTerm()));
                params.put("dl",index.getDocLen(docID));
                if(l.getCurr_pos()!=null&&l.getCurr_pos().getDocID()==docID){
                    params.put("tdf",l.getCurr_pos().getTermFreq());
                }
                sd += G.score(Q,l.getCurr_pos(),params)*F.score(Q,l.getCurr_pos(),params);
                scored =true;
                l.movePastDocument(docID);
            }
            if (scored)
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
