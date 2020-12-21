package inferenceNetWork;

import java.util.ArrayList;
import java.util.List;

public abstract class QueryNode {
    protected List<QueryNode> children = new ArrayList<>();

    public void setChildren(List<QueryNode> children) {
        this.children = children;
    }

    public List<QueryNode> getChildren() {
        return children;
    }

    public void addChild(QueryNode q){
        children.add(q);
    }

    public abstract Double score(int docID);
    public abstract int nextCandidate();
    public abstract void skipTo(int docID);
}
