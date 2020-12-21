package app;

import IO.JsonReader;
import indexer.LookUp;
import indexer.SimpleIndex;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class   Select7Random100times {
    public static int[] random7Int(int max) {
        Random r = new Random();
        int[] randInts = new int[7];
        for (int i = 0; i < 7; i++) {
            randInts[i] = r.nextInt(max);
        }
        return randInts;
    }

    public static void main(String[] args) throws IOException, ParseException {
        JsonReader jsonReader1 = new JsonReader("compressedIndexInfo.json");
        SimpleIndex index = new SimpleIndex(jsonReader1.read());
        try {
            index.load();
            List<String> volcabury = new ArrayList<String>(index.getVolcabury());
            for (int i = 0; i < 100; i++) {
                String line = "";
                int[] rand = random7Int(volcabury.size());
                for (int j = 0; j < 7; j++) {
                    String term = volcabury.get(rand[j]);
                    System.out.printf("Term: %s, Term Freq: %d, Doc Freq: %d\n",term,index.getTermFreq(term),index.getDocFreq(term));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
