package inferenceNetWork.belief;

import inferenceNetWork.QueryNode;

public class BeliefMax extends BeliefNode {
    @Override
    public Double score(int docID) {
        double max = children.get(0).score(docID);
        for(QueryNode q: children){
            max = Math.max(max,q.score(docID));
        }
        return max;
    }
}
