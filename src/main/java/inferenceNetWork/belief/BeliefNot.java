package inferenceNetWork.belief;

public class BeliefNot extends BeliefNode {
    @Override
    public Double score(int docID) {
        double s = this.children.get(0).score(docID);
        double p = Math.exp(s);
        return Math.log(1-p);
    }
}
