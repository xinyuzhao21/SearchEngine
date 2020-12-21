package scorer;

import indexer.Postings;

import java.util.List;
import java.util.Map;

public class DirichletSmoothing extends Scorer {
    private double mu;
    private double C;

    @Override
    public String generate_gib() {
        return String.format("xinyuzhao-ql-dir-%f",mu);
    }

    @Override
    public double score(List<String> Q, Postings l, Map<String, Integer> params) {
        return score(params.get("tdf"),params.get("tcf"),params.get("dl"));
    }

    public DirichletSmoothing(double C, double mu){
        this.C =C;
        this.mu =mu;
    }

    public double score(int tdf, int tcf,int dl){
        double up = tdf+mu*tcf/C;
        double down = dl+mu;
        return Math.log(up/down);
    }
}
