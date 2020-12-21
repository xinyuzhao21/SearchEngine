package app;

import IO.JsonReader;
import indexer.SimpleIndex;
import org.json.simple.parser.ParseException;
import tokenizer.BlankSpaceTokenizer;
import tokenizer.Tokenizer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MaxDice7Term100times {
    public static void main(String[] args) throws IOException, ParseException {
        JsonReader jsonReader1 = new JsonReader("compressedIndexInfo.json");
        SimpleIndex index = new SimpleIndex(jsonReader1.read());
        index.load();
        index.build();
        try {
            Scanner scanner = new Scanner(new File("100of7term.txt"));
            Tokenizer tokenizer = new BlankSpaceTokenizer();
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
                String[] maxdice = index.getMaxDiceCoeTermAndScore(term);
                System.out.println(term+" "+maxdice[0]+" "+maxdice[1]);
                line += maxdice;
                line += " ";
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}