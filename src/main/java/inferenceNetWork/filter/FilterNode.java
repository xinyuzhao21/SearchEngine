package inferenceNetWork.filter;

import inferenceNetWork.QueryNode;
import inferenceNetWork.proximity.ProximityNode;

public abstract class FilterNode extends QueryNode {
    protected ProximityNode filter;
    protected QueryNode query;
    @Override
    public abstract int nextCandidate();

    @Override
    public void skipTo(int docID) {
        children.get(0).skipTo(docID);
        filter.skipTo(docID);
    }
}
