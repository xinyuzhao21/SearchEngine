package indexer;

import org.json.simple.JSONObject;

import java.util.HashMap;

public class LookUp {
    private long offset;
    private int bytesize;
    private int cdf;
    private int df;

    public LookUp(long offset,int bytesize,int cdf,int df){
        this.offset = offset;
        this.bytesize=bytesize;
        this.cdf=cdf;
        this.df=df;
    }
    public LookUp(JSONObject object){
        this.offset = (Long) object.get("offset");
        this.bytesize = (int)(long)(Long) object.get("bytesize");
        this.cdf = (int)(long)(Long) object.get("cdf");
        this.df = (int)(long)(Long) object.get("df");

    }

    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        obj.put("offset",offset);
        obj.put("bytesize",bytesize);
        obj.put("cdf",cdf);
        obj.put("df",df);
        return obj;
    }

    public int getBytesize() {
        return bytesize;
    }

    public int getDf() {
        return df;
    }

    public long getOffset() {
        return offset;
    }

    public int getCdf() {
        return cdf;
    }
}
