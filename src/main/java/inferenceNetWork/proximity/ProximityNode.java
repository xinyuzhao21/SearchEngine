package inferenceNetWork.proximity;

import indexer.PostingsList;
import indexer.SimpleIndex;
import inferenceNetWork.QueryNode;
import scorer.Scorer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ProximityNode extends QueryNode {
    protected PostingsList plist;
    protected static SimpleIndex index;
    protected static Scorer scorer;
    public static Scorer getScorer() {
        return scorer;
    }

    public static void setScorer(Scorer scorer) {
        ProximityNode.scorer = scorer;
    }

    @Override
    public void setChildren(List<QueryNode> children) {
        super.setChildren(children);
        plist=null;
    }

    public SimpleIndex getIndex() {
        return index;
    }

    public static  void setIndex(SimpleIndex Sindex) {
        index = Sindex;
    }

    public PostingsList getPlist() {
        return plist;
    }

    public void setPlist(PostingsList plist) {
        this.plist = plist;
    }

    public abstract void prepare();
    @Override
    public Double score(int docID){
        if (plist == null) {
            prepare();
            plist.resetIterator();
        }
        Map<String,Integer> params = new HashMap<>();
        params.put("tdf",0);
        params.put("tcf",plist.getCf());
        params.put("dl",index.getDocLen(docID));
        if(plist.getCurr_pos()!=null&&plist.getCurr_pos().getDocID()==docID){
            params.put("tdf",plist.getCurr_pos().getTermFreq());
        }
        double score = scorer.score(null,null,params);
        return score;
    }

    @Override
    public int nextCandidate(){
        if (plist == null) {
            prepare();
            if(plist!=null)
                plist.resetIterator();
            else
                return -1;
        }

        if(plist.getCurr_pos()==null)
            return -1;
        return plist.getCurr_pos().getDocID();
    };

    @Override
    public void skipTo(int docID) {
        if (plist == null) {
            prepare();
            if(plist!=null)
                plist.resetIterator();
        }
        plist.skipTo(docID);
    }
}
