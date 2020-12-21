package app;

import IO.JsonReader;
import indexer.SimpleIndex;
import org.json.simple.parser.ParseException;
import retriever.Entry;
import scorer.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RetrievalModelComparison {
    public static void main(String[] args) throws IOException, ParseException {
        String[] queries = {
                "the king queen royalty",  //Q1
                "servant guard soldier",   //Q2
                "hope dream sleep",//Q3
                "ghost spirit", //Q4
                "fool jester player", //Q5
                "to be or not to be", //Q6
                "alas poor", //Q8
                "alas poor yorick", //Q9
                "antony strumpet"  //Q10
        };
        JsonReader jsonReader1 = new JsonReader("compressedIndexInfo.json");
        SimpleIndex index = new SimpleIndex(jsonReader1.read());
        index.load();
        HashSet<Integer> top10s = new HashSet<>();
        List<String> top10ss = new ArrayList<>();
        Scorer model = new BM25(1.1,10,index.getAveDocLen(),index.getDocNum(),0.6);
        output(queries,model,index,"bm25.trecrun","BM25");
        List<Entry> r1 = score(model,index,queries[2],"BM25");
        for(Entry e:r1) top10s.add(e.getKey());
        model = new JelinikMercerSmoothing(0.5,index.getTotalTermsCount());
        output(queries,model,index,"ql-jm.trecrun","LanguageModel");
        List<Entry> r2 = score(model,index,queries[2],"LanguageModel");
        for(Entry e:r2) top10s.add(e.getKey());
        model = new DirichletSmoothing(index.getTotalTermsCount(),1000);
        output(queries,model,index,"ql-dir.trecrun","LanguageModel");
        List<Entry> r3 = score(model,index,queries[2],"LanguageModel");
        for(Entry e:r3) top10s.add(e.getKey());
        for(int i:top10s){
            top10ss.add(index.docToScene(i));
        }
        System.out.println(top10ss);
        System.out.println(queries[2]);
        judge();


    }

    public static void output(String[] queries,Scorer model,SimpleIndex index,String filename,String option) throws IOException {
        int count =1;
        PrintWriter pw = new PrintWriter(filename,"UTF-8");
        for(String q : queries){
            List<Entry> pq = score(model,index,q,option);
            List<String> f = format(pq,count,index,model.generate_gib());
            for(String r : f){
                pw.println(r);
            }
            count++;
        }
        pw.close();
    }

    public static List<Entry> score(Scorer model, SimpleIndex index,String q,String option) throws IOException {
        Scorer G = new TrivalScorer();
        return index.query(q,10,G,model,option);
    }

    public static List<String> format(List<Entry> entries,int qnum,SimpleIndex index,String gib){
        List<String> formated = new ArrayList<>();
        int r = 1;
        for(Entry e: entries){
            String row = String.format("Q%d skip %s %d %f %s",qnum,index.docToScene(e.getKey()),r,e.getValue(),gib);
            r++;
            formated.add(row);
        }
        return  formated;
    }

    public static void judge() throws FileNotFoundException, UnsupportedEncodingException {
        String[] slist= {
                "merchant_of_venice:1.4",
        "midsummer_nights_dream:1.1",
        "midsummer_nights_dream:3.0",
        "titus_andronicus:1.3",
        "macbeth:2.1",
        "richard_iii:4.2",
        "romeo_and_juliet:4.0",
        "twelfth_night:3.0",
        "hamlet:2.0",
        "richard_iii:0.3",
        "romeo_and_juliet:0.3",
        "cymbeline:3.1",
        "cymbeline:4.3",
                "antony_and_cleopatra: 1.0"
                };
        int[] ilist ={
                1,
                2,
                2,
                0,
                0,
                1,
                3,
                0,
                2,
                3,
                2,
                2,
                2,
                1
        };
        PrintWriter pw = new PrintWriter("judgments.txt","UTF-8");
        for(int i =0; i<ilist.length;i++){
            pw.println(String.format("%s %d",slist[i],ilist[i]));
        }
        pw.close();
    }
}
