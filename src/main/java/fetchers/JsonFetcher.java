package fetchers;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
public class JsonFetcher extends Fetcher {
    private Iterator<JSONObject> itr;

    public JsonFetcher(JSONObject jsonObject){
        // iterating documents
        JSONArray jsonArray = (JSONArray)jsonObject.get("corpus");
        this.itr = jsonArray.iterator();

    }

    public Document next() {
        if (itr.hasNext()){
            JSONObject obj = itr.next();
            this.numofDocument++;
            JsonDocument newDoc = new JsonDocument(obj,numofDocument);
            return  newDoc;
        }
        return null;
    }

    public boolean hasNext(){
        return itr.hasNext();
    }

}
