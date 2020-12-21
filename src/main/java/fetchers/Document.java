package fetchers;

import cluster.DocumentVector;

import java.util.HashMap;
import java.util.Map;

public abstract class Document {
    private int docID;
    protected DocumentVector documentVector = new DocumentVector(docID,new HashMap<>());
    public void addToVector(String term){
        Map<String,Double> dv = documentVector.getVector();
        if (dv.keySet().contains(term)){
            dv.put(term,dv.get(term)+1);
        }
        else{
            dv.put(term,1.0);
        }
    }

    public DocumentVector getDocumentVector() {
        return documentVector;
    }

    public Document(int docID){
        this.docID = docID;
    }
    public abstract String getDocument();
    public abstract Map<String,String> getMetaInfo();
    public int getDocID(){
        return this.docID;
    }
}
