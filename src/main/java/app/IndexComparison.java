package app;

import indexer.SimpleIndex;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class IndexComparison {
    public static void main(String[] arg) throws IOException, ParseException {
        SimpleIndex index0=SimpleIndex.build_from_binary(0,"shakespeare-scenes.json","index.uncompressed","lookUpTable.json");
        SimpleIndex index1=SimpleIndex.build_from_binary(1,"shakespeare-scenes.json","index.compressed","compressedLookUpTable.json");
        System.out.println("Vocab Size for uncompressed index:"+index0.getTotalUniqueTermsCount());
        System.out.println("Collection Size for uncompressed index:"+index0.getTotalTermsCount());
        System.out.println("Vocab Size for compressed index:"+index1.getTotalUniqueTermsCount());
        System.out.println("Collection Size for compressed index:"+index1.getTotalTermsCount());
        System.out.println("Example term 'rival' uncompressed:"+index0.getInvertedList().getPostings("rival").toIntegerList(0));
        System.out.println("Example term 'rival' compressed  :"+index1.getInvertedList().getPostings("rival").toIntegerList(0));


    }
}
