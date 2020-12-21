package inferenceNetWork.belief;

import inferenceNetWork.QueryNode;

public class BeliefSum extends BeliefNode{

    @Override
    public Double score(int docID) {
        double scores = 0;
        double count =0;
        for(QueryNode q: children){
            count++;
            scores+=Math.exp(q.score(docID));
        }
        return Math.log(scores/count);
    }
}
