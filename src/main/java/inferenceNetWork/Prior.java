package inferenceNetWork;

import indexer.SimpleIndex;

public class Prior extends QueryNode{
    private String type = null;
    private SimpleIndex index;
    public Prior(String type, SimpleIndex index){
        this.type =type;
        this.index = index;
    }
    @Override
    public Double score(int docID) {
        Double score = null;
        try {
            score=index.getPrior(type,docID);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return score;
    }

    @Override
    public int nextCandidate() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void skipTo(int docID) {

    }
}
