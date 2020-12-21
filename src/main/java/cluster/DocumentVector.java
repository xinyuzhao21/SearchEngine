package cluster;

import indexer.SimpleIndex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DocumentVector {
    private int id;
    private Map<String,Double> vector;
    private Map<String,Double> idf;
    public DocumentVector(int id,Map<String,Double> vector ){
        this.id = id;
        this.vector= vector;
    }
    public int getId(){
        return  id;
    }

    public Map<String, Double> getVector() {
        return vector;
    }

    public Set<String> getKeySet(){
        return vector.keySet();
    }

    public void setIdf(Map<String, Double> idf) {
        this.idf = idf;
    }

    public double getidf(String term){
        return idf.getOrDefault(term,0.0);
    }

    public double getOrDefault(String string, Double d){
        return vector.getOrDefault(string,d);
    }
    public static DocumentVector  map2dv(int id, Map<String,Double> vector, SimpleIndex index){
        Map<String,Double> idf = new HashMap<>();
        double N = index.getDocNum();
        for(String k: vector.keySet()){
            double n = index.getDocFreq(k);
            idf.put(k,Math.log((N+1)/(n+0.5)));

        }
        DocumentVector dv = new DocumentVector(id,vector);
        dv.setIdf(idf);
        return dv;
    }
}
