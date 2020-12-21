package fetchers;

import org.json.simple.JSONObject;

import java.util.*;

public class JsonDocument extends Document {
    private  JSONObject obj;
    private String docKey;
    public JsonDocument(JSONObject obj, int docID){
        super(docID);
        this.obj = obj;
        this.docKey= "text";
    }
    public String getDocument() {
        return (String)obj.get(docKey);
    }

    public Map<String, String> getMetaInfo() {
        Map<String,String> newMap = new HashMap<String, String>(obj);
        newMap.remove(docKey);
        return newMap;
    }
}
