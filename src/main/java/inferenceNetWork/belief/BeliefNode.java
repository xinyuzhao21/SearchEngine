package inferenceNetWork.belief;

import inferenceNetWork.QueryNode;

public abstract class BeliefNode extends QueryNode {

    @Override
    public abstract Double score(int docID);

    @Override
    public int nextCandidate() {
        int min = children.get(0).nextCandidate();
        for(QueryNode q:this.children){
            min = Math.min(min,q.nextCandidate());
        }
        return min;
    }

    @Override
    public void skipTo(int docID) {
        for(QueryNode q: this.children){
            q.skipTo(docID);
        }
    }
}
