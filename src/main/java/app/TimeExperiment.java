package app;

import IO.JsonReader;
import indexer.SimpleIndex;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import scorer.Scorer;
import scorer.SumFreqScorer;
import scorer.TrivalScorer;
import tokenizer.BlankSpaceTokenizer;
import tokenizer.Tokenizer;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class TimeExperiment {
    public static int[] random7Int(int max){
        Random r =new Random();
        int[] randInts= new int[7];
        for(int i =0; i<7;i++ ){
            randInts[i]=r.nextInt(max);
        }
        return randInts;
    }
    public static String write100of14MaxDiceTerm(SimpleIndex index) throws IOException, ParseException {
        index.build();
        PrintWriter writer = new PrintWriter("100of14term.txt", "UTF-8");
        File f = new File("100of14term.txt");
        try {
            index.load();
            Scanner scanner = new Scanner(new File("100of7term.txt"));
            Tokenizer tokenizer = new BlankSpaceTokenizer();
            File file = new File("100of14term.txt");
            List<String> list = new ArrayList<>();
            while (scanner.hasNextLine()) {
                List<String> terms = tokenizer.tokenize(scanner.nextLine());
                list.addAll(terms);
            }
            scanner.close();
            String line ="";
            for(int i=0;i<list.size();i++){
                String term = list.get(i);
                line += term;
                line += " ";
                String maxdice = index.getMaxDiceCoeTermAndScore(term)[0];
                line += maxdice;
                line += " ";
                if(i%7==6){
                    writer.println(line);
                    line="";
                }
            }
            writer.close();
        }
        catch (Exception e){
            writer.close();
            e.printStackTrace();
        }
        return f.getAbsolutePath();
    }
    public static String write100of7RandomTerms(SimpleIndex index){
        try {
            index.load();
            List<String> volcabury = new ArrayList<String>(index.getVolcabury());
            PrintWriter writer = new PrintWriter("100of7term.txt", "UTF-8");
            File file = new File("100of7term.txt");
            for (int i = 0; i < 100; i++) {
                String line = "";
                int[] rand = random7Int(volcabury.size());
                for (int j = 0; j < 7; j++) {
                    line += volcabury.get(rand[j]);
                    line += " ";
                }
                writer.println(line);
            }
            writer.close();
            return file.getAbsolutePath();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] arg) throws IOException, ParseException {
        JsonReader jsonReader1 = new JsonReader("compressedIndexInfo.json");
        JsonReader jsonReader2 = new JsonReader("IndexInfo.json");
        Scanner scanner1 = new Scanner(new File("100of7term.txt"));
        List<String> seven = new ArrayList<>(),fourteen =new ArrayList<>();
        while (scanner1.hasNextLine()){
            seven.add(scanner1.nextLine());
        }
        scanner1.close();
        Scanner scanner2 = new Scanner(new File("100of14term.txt"));
        while (scanner2.hasNextLine()){
            fourteen.add(scanner2.nextLine());
        }
        scanner2.close();

        SimpleIndex compressed = new SimpleIndex(jsonReader1.read());
        compressed.load();
        SimpleIndex uncompressed = new SimpleIndex(jsonReader2.read());
        uncompressed.load();
        int k =100;
        long dur =0;
        write100of14MaxDiceTerm(compressed);

        experiment(compressed,seven,k);
        experiment(uncompressed,seven,k);
        dur=experiment(compressed,seven,k);
        System.out.println("100 set of 7 term compressed with Priority Q k="+ k+" query time:"+dur);
        dur=experiment(uncompressed,seven,k);
        System.out.println("100 set of 7 term uncompressed with Priority Q k="+ k+" query time:"+dur);
        experiment(compressed,fourteen,k);
        experiment(uncompressed,fourteen,k);
        dur=experiment(compressed,fourteen,k);
        System.out.println("100 set of 14 term compressed with Priority Q k="+ k+" query time:"+dur);
        dur=experiment(uncompressed,fourteen,k);
        System.out.println("100 set of 14 term uncompressed with Priority Q k="+ k+" query time:"+dur);



    }
    public static long experiment(SimpleIndex index, List<String> list,int k) throws IOException {
        Scorer G = new TrivalScorer();
        Scorer F = new SumFreqScorer();
        long start = Instant.now().toEpochMilli();
        for(String q:list){
            index.query(q,k,G,F,null);
        }
        long end = Instant.now().toEpochMilli();
        return end-start;
    }
}
