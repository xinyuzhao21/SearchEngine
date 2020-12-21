package indexer;


import IO.BinaryFileReader;
import IO.BinaryFileWriter;
import compression.Compression;
import compression.NotCompressed;
import compression.V_Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InvertedList {
    private int total_terms;
    private Map<String,PostingsList> invList;


    public InvertedList(){
        this.total_terms=0;
        this.invList= new HashMap<String, PostingsList>();

    }

    public Map<String, PostingsList> getInvList() {
        return invList;
    }

    public int getTotal_terms() {
        return total_terms;
    }


    public void add_term(String term,PostingsList plist){
        this.total_terms+= plist.getCf();
        invList.put(term,plist);
    }



    public void add(String term, int docID, int pos){
        this.total_terms++;
        if(!invList.containsKey(term)) {
            PostingsList newPlist = new PostingsList();
            newPlist.add(docID,pos);
            invList.put(term, newPlist);
        }
        else{
            PostingsList existedPlist = invList.get(term);
            existedPlist.add(docID,pos);
        }

    }
    public PostingsList getPostings(String term){
        return invList.get(term);
    }

    public Map<String,LookUp> lists2file(String fileName,int compress_flag) throws IOException {
        String filename = fileName;
        BinaryFileWriter writer = new BinaryFileWriter(filename,0);
        Map<String,LookUp> look_up_table = new HashMap<String, LookUp>();
        Compression c;
        if(compress_flag==1){
            c = new V_Bytes();
        }
        else
        {
            c = new NotCompressed();
        }
        for(String key: invList.keySet()){
            PostingsList plist = invList.get(key);
            long oldoffset = writer.getOffset();
            ArrayList<Integer> raw = plist.toIntegerList(compress_flag);
            long newoffset = writer.writeBytesNonStop(c.encode(raw));
            int bytesize = (int) (newoffset -oldoffset);
            //System.out.println(oldoffset+" "+key+" "+raw);
            look_up_table.put(key,new LookUp(oldoffset,bytesize,plist.getCf(),plist.getDf()));
        }
        writer.close();
        return look_up_table;
    }



}
