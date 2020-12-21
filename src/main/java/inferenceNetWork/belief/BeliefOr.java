package inferenceNetWork.belief;

import inferenceNetWork.QueryNode;

public class BeliefOr extends BeliefNode{

    @Override
    public Double score(int docID) {
        double score = 0;
        for(QueryNode q: children){
            double s = q.score(docID);
            double p = Math.exp(s);
            score+=Math.log(1-p);
        }
        return Math.log(1-Math.exp(score));
    }
}
