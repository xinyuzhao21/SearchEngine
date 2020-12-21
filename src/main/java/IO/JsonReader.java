package IO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class JsonReader {
    private FileReader file;
    public JsonReader(String filename) throws FileNotFoundException {
        this.file = new FileReader(filename);
    }
    public JSONObject read() throws IOException, ParseException{
        JSONObject obj = (JSONObject) new JSONParser().parse(file);
        file.close();
        return obj;
    }


}
