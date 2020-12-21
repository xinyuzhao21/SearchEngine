package scorer;

import indexer.Postings;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ConjunctiveSumFreqScorer extends Scorer {
    Queue<Postings> queue= new LinkedList<Postings>();
    int score =0;

    public ConjunctiveSumFreqScorer(){

    }

    @Override
    public String generate_gib() {
        return null;
    }


    public double score(List<String> Q, Postings l, Map<String, Integer> params) {
        queue.add(l);
        if(queue.size()>2) {
            queue.poll();
        }
        else if(queue.size()<2) {
            return 0;
        }
        Postings p1 = queue.poll();
        Postings p2 = queue.poll();
        if(p1.getDocID()!=p2.getDocID()) {
            queue.add(p2);
            return 0;
        }

        while(true){
            if(p2.getCurr_pos()<0||p1.getCurr_pos()<0) break;
            int pos = Math.max(p1.getCurr_pos(),p2.getCurr_pos());
            p1.skipTo(pos-1);
            p2.skipTo(pos);
            if(p2.getCurr_pos()<0||p1.getCurr_pos()<0) break;
            if(p1.getCurr_pos()==p2.getCurr_pos()){
                p1.movePastPos(pos);
            }
            if(p1.getCurr_pos()==p2.getCurr_pos()-1){
                score++;
                p1.movePastPos(pos);
            }
        }
            return score;
    }
}
