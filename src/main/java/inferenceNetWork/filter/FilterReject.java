package inferenceNetWork.filter;

public class FilterReject extends FilterNode {

    public Double score(int docId){
        if(filter.getPlist().getCurr_pos().getDocID() != docId){
            return query.score(docId);
        }
        return null;
    }


    @Override
    public int nextCandidate() {
        return query.nextCandidate();
    }
}
