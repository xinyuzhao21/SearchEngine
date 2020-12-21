package inferenceNetWork.belief;

import inferenceNetWork.QueryNode;

import java.util.Map;

public class BeliefWsum extends BeliefNode{
    protected Map<Object,Double> weights;

    public Map<Object, Double> getWeights() {
        return weights;
    }

    public void setWeights(Map<Object,Double> map){
        weights= map;
    }

    public Double score(int docID) {
        double scores = 0;
        double count =0;
        for(QueryNode q: children){
            count+=weights.get(q);
            scores+=weights.get(q)*Math.exp(q.score(docID));
        }
        return Math.log(scores/count);
    }
}
