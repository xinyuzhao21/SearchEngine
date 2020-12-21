package inferenceNetWork.belief;

import inferenceNetWork.QueryNode;

import java.util.Map;

public class BeliefWand extends BeliefNode {
    protected Map<Object,Double> weights;

    public Map<Object, Double> getWeights() {
        return weights;
    }

    public void setWeights(Map<Object,Double> map){
        weights= map;
    }

    @Override
    public Double score(int docID) {
        double scores = 0;
        for(QueryNode q: children){
            scores+=weights.getOrDefault(q,1.0)*q.score(docID);
        }
        return scores;
    }
}
