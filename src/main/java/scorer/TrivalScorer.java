package scorer;

import indexer.Postings;

import java.util.List;
import java.util.Map;

public class TrivalScorer extends Scorer {

    public TrivalScorer(){


    }

    @Override
    public String generate_gib() {
        return null;
    }

    public double score(List<String> Q, Postings l, Map<String, Integer> params) {
        return 1;
    }
}
