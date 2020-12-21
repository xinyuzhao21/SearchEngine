package inferenceNetWork.proximity.windows;

import indexer.Postings;
import indexer.PostingsList;
import inferenceNetWork.QueryNode;
import inferenceNetWork.proximity.ProximityNode;

public class OrderedWindow extends ProximityNode {
    private int range;

    public OrderedWindow(int range){
        this.range=range;
    }

    public Postings prepare2(Postings pold, Postings pnew,int range){
        if(pold == null){
            return pnew;
        }
        if(pnew==null||pold.getDocID()!=pnew.getDocID())
            return null;
        Postings newPl = null;
        while(pold.getCurr_pos()>=0) {
            int oldpos = pold.getCurr_pos();
            pnew.skipTo(oldpos);
            int newpos = pnew.getCurr_pos();
            if(newpos<0) break;
            if ( oldpos + range >= newpos) {
                if(newPl==null)
                newPl=new Postings(pold.getDocID());
                newPl.add(newpos);
            }
            pold.skipTo(oldpos+1);
        }
        return newPl;
    }

    @Override
    public void prepare() {
        int max = children.get(0).nextCandidate();
        PostingsList psl = new PostingsList();
        while(max>=0) {
            Postings ps = null;
            int c = -1;
            for (QueryNode q : children) {
                 c = q.nextCandidate();
                if (c < 0) {
                    break;
                }
                max = Math.max(max,c);
            }
            if(c<0){
                break;
            }
            for (QueryNode q : children) {
                q.skipTo(max);
                ProximityNode pn = (ProximityNode) q;
                ps=prepare2(ps,pn.getPlist().getCurr_pos(),range);
                if(ps==null) break;
            }

            for (QueryNode q : children) {
                q.skipTo(max+1);
            }
            if(ps == null) continue;
            if(ps.getTermFreq()>0)
            psl.addPostings(ps);
        }
        this.setPlist(psl);
    }
}
