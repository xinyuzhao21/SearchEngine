package inferenceNetWork;

import retriever.Entry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class InferenceNetWork {
    public List<Entry> runQuery(QueryNode q , int k){
        PriorityQueue<Entry> R = new PriorityQueue();
        while(q.nextCandidate()!=-1){
            int d = q.nextCandidate();
            q.skipTo(d);
            Double score = q.score(d);
            q.skipTo(d+1);
            R.add(new Entry(d,score));
            if(R.size()>k){
                R.poll();
            }
        }

        List<Entry> output = new ArrayList<Entry>();
        while (!R.isEmpty()){
            output.add(R.poll());
        }
        Collections.reverse(output);
        return output;
    }
}
