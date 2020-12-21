package scorer;

import indexer.Postings;

import java.util.List;
import java.util.Map;

public class SumFreqScorer extends Scorer {


    public SumFreqScorer(){

    }

    @Override
    public String generate_gib() {
        return null;
    }

    public double score(List<String> Q, Postings l, Map<String, Integer> params) {
        return l.getTermFreq();
    }
}
