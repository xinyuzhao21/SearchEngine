import indexer.SimpleIndex;
import org.json.simple.JSONObject;
import retriever.Entry;

import java.util.List;

public class Query {
    public static void main(String[] args){
        String filename="C:\\Users\\frfcd\\IdeaProjects\\indexer\\src\\app.IndexBuilder\\resources\\shakespeare-scenes.json";
        String binaryfile = "C:\\Users\\frfcd\\IdeaProjects\\indexer\\index.uncompressed";
        String lookUpFile = "C:\\Users\\frfcd\\IdeaProjects\\indexer\\lookUpTable.json";
        int compress_flag = 1;
        JSONObject config = new JSONObject();
        config.put("sourcefile",filename);
        config.put("binaryfile",binaryfile);
        config.put("lookUpFile",lookUpFile);
        config.put("compress_flag",compress_flag);
        config.put("docNum",748);

        SimpleIndex index = new SimpleIndex(config);
        try {
            index.load();
            List<Entry> result =index.query("before the wall",20);
            System.out.println(result);
            result =index.query("before",10);
            System.out.println(result);
            result =index.query("the",10);
            System.out.println(result);
            result =index.query("wall",10);
            System.out.println(result);
            double dice = index.getDiceCoeFast("pullet","pullet");
            System.out.println(index.get("before").toIntegerList(0));

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
