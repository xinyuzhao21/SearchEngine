package retriever;

import indexer.PostingsList;
import indexer.SimpleIndex;
import scorer.Scorer;

import java.io.IOException;
import java.util.*;

public class ConjucntiveDocumentAtATimeRetrieval extends Retriever {
    public List<Entry> retrieval(SimpleIndex index, List<String> Q, int I, Scorer G, Scorer F, int k) throws IOException {
        List<PostingsList> L = new ArrayList<PostingsList>();
        PriorityQueue<Entry> R = new PriorityQueue();
        for(String term: Q){
            L.add(index.get(term));
        }
        int docID = -1;
        Set<String> exhaustedTermSet = new HashSet<String>();
        while(exhaustedTermSet.size()==0&&exhaustedTermSet.size()!=L.size()) {
                int sd = 0;
                for (PostingsList l : L) {
                    if(l.getCurr_pos()==null) continue;
                    if (l.getCurr_pos().getDocID() > docID) {
                        docID = l.getCurr_pos().getDocID();
                    }
                }
                for(int i =0; i<L.size();i++){
                    PostingsList l = L.get(i);
                    l.skipTo(docID);
                    if (l.getCurr_pos()==null){
                        exhaustedTermSet.add(i+"");
                        continue;
                    }
                    if (l.getCurr_pos().getDocID()==docID){
                        sd+=G.score(Q,l.getCurr_pos(),null)*F.score(Q,l.getCurr_pos(),null);
                        l.movePastDocument(docID);
                        if(l.getCurr_pos()==null){
                            exhaustedTermSet.add(i+"");
                        }
                    }
                    else{
                        docID=-1;
                        break;
                    }
                }
                if(docID>-1){
                    R.add(new Entry(docID,sd));
                    if(R.size()>k){
                        R.poll();
                    }
                }

        }
        List<Entry> output = new ArrayList<Entry>();
        if(R.isEmpty()){
            R.add(new Entry(0,0));
        }
        while(!R.isEmpty()){
            output.add(R.poll());
        }
        Collections.reverse(output);
        return output;
    }

}
