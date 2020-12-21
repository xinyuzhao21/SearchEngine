package cluster;

import indexer.SimpleIndex;

import java.util.List;
import java.util.Map;

public class SimiliarityFunctions {
    private SimpleIndex index;
    public SimiliarityFunctions(SimpleIndex index){
        this.index = index;
    }
    public double scoreCos(DocumentVector a, DocumentVector b){
        double score = dotProduct(a,b)/Math.sqrt(dotProduct(a,a)*dotProduct(b,b));
        if(score>1){
            System.out.println(score);
        }
        return score;
    }
    private double dotProduct(DocumentVector a,DocumentVector b){
        double score = 0;
        for(String key: a.getKeySet()){
            double a1 = a.getidf(key)*a.getOrDefault(key,0.0);
            double b2 = b.getidf(key)*b.getOrDefault(key,0.0);
            score += a1*b2;
        }
        return score;
    }
}
