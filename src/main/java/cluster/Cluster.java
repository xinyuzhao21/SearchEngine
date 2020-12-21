package cluster;

import indexer.SimpleIndex;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

import static cluster.Linkage.MEAN;

public  class Cluster {
    private Linkage link;
    private DocumentVector centroid;
    private List<DocumentVector> clusterDV = new ArrayList<>();
    private int id;
    private SimiliarityFunctions scorer;
    protected List<Integer> cluster = new ArrayList<>();
    public Cluster(int id,Linkage link,SimiliarityFunctions scorer){
        this.id = id;
        this.link=link;
        this.scorer = scorer;
    }

    public int getId() {
        return id;
    }

    public void add(DocumentVector dv){
        cluster.add(dv.getId());
        clusterDV.add(dv);
        if (link==MEAN){
            if (centroid == null){
                centroid = dv;
            }
            else{
                for(String k:dv.getKeySet()){
                    double N = cluster.size();
                    centroid.getVector().put(k,((N-1)*centroid.getOrDefault(k,0.0)+dv.getVector().get(k))/N);
                }
            }
        }
    }
    public double score(DocumentVector dv){
        double score = 0;
        switch (link){
            case SINGLE:
                score = scorer.scoreCos(dv,clusterDV.get(0));
                for(DocumentVector d:clusterDV){
                    score = Math.max(score,scorer.scoreCos(dv,d));
                }
                break;
            case COMPELETE:
                score = scorer.scoreCos(dv,clusterDV.get(0));
                for(DocumentVector d:clusterDV){
                    score = Math.min(score,scorer.scoreCos(dv,d));
                }
                break;
            case MEAN:
                score = scorer.scoreCos(centroid,dv);
                break;
            case AVERAGE:
                for(DocumentVector d:clusterDV){
                    score += scorer.scoreCos(d,dv);
                }
                score = score/(double) cluster.size();
                break;
        }
        return score;
    }
    public List<Integer> getDocIds(){
        return cluster;
    }

}
