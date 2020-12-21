package app;

import IO.BinaryFileWriter;
import IO.JsonReader;

import indexer.SimpleIndex;
import inferenceNetWork.InferenceNetWork;
import inferenceNetWork.Prior;
import inferenceNetWork.QueryNode;
import inferenceNetWork.belief.BeliefAnd;
import inferenceNetWork.proximity.ProximityNode;
import inferenceNetWork.proximity.Term;
import org.json.simple.parser.ParseException;
import retriever.Entry;
import scorer.DirichletSmoothing;
import scorer.Scorer;
import tokenizer.BlankSpaceTokenizer;
import tokenizer.Tokenizer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PriorTesting {

    public static void main(String[] args) throws IOException, ParseException {
        JsonReader jsonReader1 = new JsonReader("IndexInfo.json");
        SimpleIndex index = new SimpleIndex(jsonReader1.read());
        index.load();
        String uniform = "uniform.prior";
        String random = "random.prior";
        double N = (double)index.getDocNum();
        RandomAccessFile writer1 = new RandomAccessFile(uniform,"rw");
        RandomAccessFile writer2 = new RandomAccessFile(random,"rw");
        Random rand = new Random(1024);
        double uprior = Math.log(1/N);
        for(int i =0; i<N;i++){
            writer1.writeDouble(uprior);
            writer2.writeDouble(Math.log(rand.nextDouble()));
        }
        writer1.close();
        writer2.close();

        String query = "the king queen royalty";
        InferenceNetWork inferenceNetWork = new InferenceNetWork();
        Scorer scorer = new DirichletSmoothing(index.getTotalTermsCount(),1500);
        ProximityNode.setIndex(index);
        ProximityNode.setScorer(scorer);


        QueryNode root = new BeliefAnd();
        root.setChildren(new ArrayList<>());
        Tokenizer parser = new BlankSpaceTokenizer();
        List<String> terms = parser.tokenize(query);
        for(String term : terms){
            Term t = new Term(term);
            root.addChild(t);
        }
        root.addChild(new Prior("uniform",index));
        List<Entry> rl= inferenceNetWork.runQuery(root,10);

        PrintWriter pw = new PrintWriter("uniform.trecrun","UTF-8");
        System.out.println(rl);
        List<String> ll = RetrievalModelComparison.format(rl, 1, index, scorer.generate_gib());
        for(String s: ll) pw.println(s);
        pw.close();

        root = new BeliefAnd();
        root.setChildren(new ArrayList<>());
        for(String term : terms){
            Term t = new Term(term);
            root.addChild(t);
        }
        root.addChild(new Prior("random",index));
        rl= inferenceNetWork.runQuery(root,10);

        pw = new PrintWriter("random.trecrun","UTF-8");
        System.out.println(rl);
        ll = RetrievalModelComparison.format(rl, 1, index, scorer.generate_gib());
        for(String s: ll) pw.println(s);
        pw.close();
    }
}
