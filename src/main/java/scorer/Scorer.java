package scorer;

import indexer.Postings;
import indexer.PostingsList;

import java.util.List;
import java.util.Map;

public abstract class Scorer {
    public Scorer(){

    }
    public abstract String generate_gib();
    public abstract double score(List<String> Q, Postings l, Map<String,Integer> params);
}
