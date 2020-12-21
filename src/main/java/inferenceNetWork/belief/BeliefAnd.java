package inferenceNetWork.belief;

import inferenceNetWork.QueryNode;

import java.util.ArrayList;
import java.util.List;

public class BeliefAnd extends BeliefNode {

    @Override
    public Double score(int docID) {
        double scores = 0;
        for(QueryNode q: children){
            scores+=q.score(docID);
        }
        return scores;
    }
}
