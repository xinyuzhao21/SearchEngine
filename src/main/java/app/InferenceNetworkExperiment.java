package app;

import IO.JsonReader;
import indexer.SimpleIndex;
import inferenceNetWork.InferenceNetWork;
import inferenceNetWork.QueryNode;
import inferenceNetWork.belief.BeliefMax;
import inferenceNetWork.belief.BeliefOr;
import inferenceNetWork.belief.BeliefSum;
import inferenceNetWork.proximity.ProximityNode;
import inferenceNetWork.proximity.Term;
import inferenceNetWork.proximity.windows.OrderedWindow;
import inferenceNetWork.proximity.windows.UnorderedWindow;
import inferenceNetWork.belief.BeliefAnd;
import org.json.simple.parser.ParseException;
import retriever.Entry;
import scorer.DirichletSmoothing;
import scorer.Scorer;
import tokenizer.BlankSpaceTokenizer;
import tokenizer.Tokenizer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class InferenceNetworkExperiment {
    public static void main(String[] args) throws IOException, ParseException {
        InferenceNetWork inferenceNetWork = new InferenceNetWork();
//        Q1: the king queen royalty
//        Q2: servant guard soldier
//        Q3: hope dream sleep
//        Q4: ghost spirit
//        Q5: fool jester player
//        Q6: to be or not to be
//        Q7: alas
//        Q8: alas poor
//        Q9: alas poor yorick
//        Q10: antony strumpet
        List<String> queries = new ArrayList<>();
        queries.add("the king queen royalty");
        queries.add("servant guard soldier");
        queries.add("hope dream sleep");
        queries.add("ghost spirit");
        queries.add("fool jester player");
        queries.add("to be or not to be");
        queries.add("alas");
        queries.add("alas poor");
        queries.add("alas poor yorick");
        queries.add("antony strumpet");

        JsonReader jsonReader1 = new JsonReader("compressedIndexInfo.json");
        SimpleIndex index = new SimpleIndex(jsonReader1.read());
        index.load();

        Scorer scorer = new DirichletSmoothing(index.getTotalTermsCount(),1500);

        ProximityNode.setIndex(index);
        ProximityNode.setScorer(scorer);
        QueryNode root = new BeliefAnd();
        output(index,inferenceNetWork,root,queries,scorer,"and.trecrun");
        root = new BeliefOr();
        output(index,inferenceNetWork,root,queries,scorer,"or.trecrun");
        root = new BeliefMax();
        output(index,inferenceNetWork,root,queries,scorer,"max.trecrun");
        root = new BeliefSum();
        output(index,inferenceNetWork,root,queries,scorer,"sum.trecrun");
        root = new OrderedWindow(1);
        output(index,inferenceNetWork,root,queries,scorer,"ow.trecrun");

        int counter = 0;
        PrintWriter pw = new PrintWriter("uw.trecrun","UTF-8");
        for(String q: queries){
            Tokenizer parser = new BlankSpaceTokenizer();
            List<String> terms = parser.tokenize(q);
            root = new UnorderedWindow(terms.size());
            counter++;
            build(root, q);
            List<Entry> rl = inferenceNetWork.runQuery(root, 10);
            System.out.println(rl);
            List<String> ll = RetrievalModelComparison.format(rl, counter, index, scorer.generate_gib());
            for(String s: ll) pw.println(s);
        }
        pw.close();
    }

    public static void output(SimpleIndex index,InferenceNetWork inferenceNetWork,QueryNode qn,List<String> queries,Scorer scorer,String filename) throws IOException {
        int counter = 0;
        PrintWriter pw = new PrintWriter(filename,"UTF-8");
        for(String q: queries ) {
            counter++;
            build(qn, q);
            List<Entry> rl = inferenceNetWork.runQuery(qn, 10);
            System.out.println(rl);
            List<String> ll = RetrievalModelComparison.format(rl, counter, index, scorer.generate_gib());
            for(String s: ll) pw.println(s);
        }
        pw.close();
    }

    public static void build(QueryNode qn,String query) throws IOException {
        qn.setChildren(new ArrayList<>());
        Tokenizer parser = new BlankSpaceTokenizer();
        List<String> terms = parser.tokenize(query);
        for(String term : terms){
            Term t = new Term(term);
            qn.addChild(t);
        }
    }

}
