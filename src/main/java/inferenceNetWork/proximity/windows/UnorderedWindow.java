package inferenceNetWork.proximity.windows;

import indexer.Postings;
import indexer.PostingsList;
import inferenceNetWork.QueryNode;
import inferenceNetWork.proximity.ProximityNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnorderedWindow extends ProximityNode {
    private int range;
    public UnorderedWindow(){
        range = index.getMaxDocLen()[0];
    }
    public UnorderedWindow(int range){
        this.range=range;
    }
    public Postings prepare2(List<Postings> list){
        if(list == null)
            return null;
        Postings newPl = new Postings(list.get(0).getDocID());
        while(true) {
            int min = list.get(0).getCurr_pos();
            int minindex = 0;
            Set<Integer> seenPos = new HashSet<>();
            for (int i = 1; i < list.size(); i++) {
                int temp = list.get(i).getCurr_pos();
                while (temp>0&&seenPos.contains(temp)){
                    list.get(i).skipTo(temp+1);
                    temp = list.get(i).getCurr_pos();
                }
                seenPos.add(temp);
                if (temp < min) {
                    min = temp;
                    minindex = i;
                }
            }
            if (min < 0)
                break;
            int max = -1;
            for (int i = 0; i < list.size(); i++) {
                int temp = list.get(i).getCurr_pos();
                max = Math.max(max, temp);
            }
            if (max < 0)
                break;
            if (min+range > max) {
                newPl.add(max);
                for (int i = 0; i < list.size(); i++) {
                    int temp = list.get(i).getCurr_pos();
                    list.get(i).skipTo(temp+1);
                }
            }
            else
            list.get(minindex).skipTo(min + 1);
        }
        return newPl;
    }

    @Override
    public void prepare() {
        int max = children.get(0).nextCandidate();
        PostingsList psl = new PostingsList();
        while(max>=0) {
            Postings ps = null;
            List<Postings> temp = new ArrayList<>();
            int c = -1;
            for (QueryNode q : children) {
                c = q.nextCandidate();
                if (c < 0)
                    break;
                max = Math.max(max, q.nextCandidate());
            }
            if(c<0){
                break;
            }
            for (QueryNode q : children) {
                q.skipTo(max);
                ProximityNode pn = (ProximityNode) q;
                if(pn.nextCandidate()!=max){
                    temp = null;
                    break;
                }
                temp.add(pn.getPlist().getCurr_pos());
            }
            ps = prepare2(temp);
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
