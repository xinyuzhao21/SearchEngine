package retriever;

import indexer.InvertedList;
import indexer.SimpleIndex;
import scorer.Scorer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class Retriever {
    public abstract List<Entry> retrieval(SimpleIndex index, List<String> Q, int I, Scorer G, Scorer F, int k) throws IOException;
}
