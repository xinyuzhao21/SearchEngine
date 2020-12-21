package IO;

import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class JsonWriter {
    private FileWriter file;
    public JsonWriter(String filename) throws IOException {
        this.file = new FileWriter(filename);
    }
    public static JSONObject mapTojson(Map<String,Double> map){
        JSONObject jo = new JSONObject();
        for(String key: map.keySet()){
            jo.put(key,map.get(key));
        }
        return jo;
    }
    public void write(JSONObject object) throws IOException {
        file.write(object.toJSONString());
        file.flush();
        file.close();
    }

}
