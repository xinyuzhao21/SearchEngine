package scorer;

import indexer.Postings;

import java.util.List;
import java.util.Map;

public class BM25 extends Scorer {
    private double b;
    private double k1;
    private double k2;
    private double avdl;
    private double N;

    public BM25(double k1, double k2, double avdl,double N,double b){
        this.b = b;
        this.k1 = k1;
        this.k2 = k2;
        this.avdl = avdl;
        this.N = N;
    }
    public String generate_gib(){
        return String.format("xinyuzhao-bm25-%f-%f",k1,k2);
    }
    @Override
    public double score(List<String> Q, Postings l, Map<String, Integer> params) {
        return score(params.get("df"),params.get("tdf"),params.get("qf"),params.get("dl"));

    }
    public double score(int df, int tdf,int qf,int dl){
        double score = 0;
        double K = k1*((1-b)+b*dl/avdl);
        double idf = Math.log(1.0/((df+0.5)/(N-df+0.5)));
        double tf = (k1+1)*tdf/(K+tdf);
        double dlen = (k2+1)*qf/(k2+qf);
        score = idf*tf*dlen;
        return score;
    }
}
