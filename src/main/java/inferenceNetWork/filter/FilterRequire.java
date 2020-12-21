package inferenceNetWork.filter;

public class FilterRequire extends FilterNode  {
    @Override
    public Double score(int docID) {
        if(filter.getPlist().getCurr_pos().getDocID() == docID){
            return query.score(docID);
        }
        return null;
    }

    @Override
    public int nextCandidate() {
        return Math.max(filter.nextCandidate(),query.nextCandidate());
    }
}
