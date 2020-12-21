package indexer;

import IO.BinaryFileReader;
import IO.JsonReader;
import IO.JsonWriter;
import cluster.DocumentVector;
import compression.Compression;
import compression.NotCompressed;
import compression.V_Bytes;
import fetchers.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import retriever.*;
import scorer.ConjunctiveSumFreqScorer;
import scorer.Scorer;
import scorer.SumFreqScorer;
import scorer.TrivalScorer;
import tokenizer.BlankSpaceTokenizer;
import tokenizer.Tokenizer;

import java.io.*;
import java.util.*;

public class SimpleIndex {
    private String sourcefile="";
    private String lookUpFile="";
    private String binaryfile="";
    private String scenIDandLen="";
    private int docNum=0;
    private int compress_flag=0;
    private InvertedList invertedList;
    private JSONObject config;
    private JSONObject lookUpTable;
    private int totalTerms=0;
    private int totalUniqueTerms=0;
    private List<String> sceneIDList = new ArrayList<String>();
    private List<Integer> docLen = new ArrayList<>();
    private Map<String,Integer> playIDandLength = new HashMap<>();
    private int maxPlaylen=0;
    private int minPlaylen=0;
    private Map<Integer,DocumentVector> docVectors = new HashMap<>();
    private  String dvf = "docVectors.json";
    /**
     * Constructor: Construct an Index, given a collection file.
     * @param filename
     */
    public SimpleIndex(String filename){
        this.sourcefile=filename;
        this.invertedList=new InvertedList();
    }

    /**
     * Constructor: Construct an Index, given a collection file, a binary index,an  d a look up table.
     * @param sourcefile The collection file path.
     * @param indexfile The binary index file path.
     * @param lookupfile The LookUpTable file path.
     * @throws IOException
     * @throws ParseException
     */
    public SimpleIndex(String sourcefile,String indexfile,String lookupfile) throws IOException, ParseException {
        this.binaryfile=indexfile;
        this.sourcefile=sourcefile;
        this.lookUpFile=lookupfile;
        this.invertedList=new InvertedList();
    }

    /**
     * Constructor: Construct an Index, given a Json config.
     * @param config The Json config that define the path of collection file, Auxiliary stucture,and general stats.
     */
    public SimpleIndex(JSONObject config){
        this.config = config;
        this.sourcefile = (String) config.get("sourcefile");
        this.lookUpFile=(String) config.get("lookUpFile");
        this.binaryfile = (String) config.get("binaryfile");
        this.invertedList=new InvertedList();
        this.compress_flag= (int)(long)(Long) config.get("compress_flag");
        this.docNum = (int)(long)(Long) config.get("docNum");
        this.totalTerms = (int)(long)(Long) config.get("totalTerms");
        this.totalUniqueTerms=(int)(long)(Long) config.get("totalUniqueTerms");
        scenIDandLen = (String) config.get("scenIDandLen");
    }


    /**
     *  Get the Average Document length
     * @return Double Average Document length
     */
    public double getAveDocLen(){
        return (double) (getTotalTermsCount())/docNum;
    }

    /**
     * Get Doc with Max Length and its length
     * @return [docID,length]
     */
    public int[] getMaxDocLen(){
        int result = -1;
        int docId = 0;
        for(int i=0 ; i<docLen.size();i++){
            if(result<0||docLen.get(i)>result){
                result =docLen.get(i);
                docId= i;
            }
        }
        int[] output= {docId+1,result};
        return output;
    }

    /**
     * Get Doc with Min Length and its length
     * @return [docID,length]
     */
    public int[] getMinDocLen(){
        int result = -1;
        int docId = 0;
        for(int i=0 ; i<docLen.size();i++){
            if(result<0||docLen.get(i)<result){
                result =docLen.get(i);
                docId= i;
            }
        }
        int[] output= {docId+1,result};
        return output;
}

    /**
     * Get Play with Max Length and its length
     * @return [PlayId,length]
     */
    public Map.Entry<String,Integer> getMaxPlaylen(){
        Map.Entry<String,Integer> result =null;
        int max = -1;
        for(Map.Entry<String,Integer> e:playIDandLength.entrySet()){
            if(result==null||e.getValue()>max){
                max = e.getValue();
                result=e;
            }
        }
        return result;
    }

    /**
     * Get Play with Min Length and its length
     * @return [PlayId,length]
     *
     */
    public Map.Entry<String,Integer> getMinPlaylen(){
        Map.Entry<String,Integer> result =null;
        int min = -1;
        for(Map.Entry<String,Integer> e:playIDandLength.entrySet()){
            if(result==null||e.getValue()<min){
                min = e.getValue();
                result=e;
            }
        }
        return result;
    }

    /**
     * Get the length of Doc with given docID
     * @param docID The given docID
     * @return length
     */
    public int getDocLen(int docID){
        return docLen.get(docID-1);
    }

    /**
     * Get term frequnency of a given term
     * @param term
     * @return frequency
     */
    public int getTermFreq(String term){
        LookUp info = new LookUp((JSONObject) lookUpTable.get(term));
        return info.getCdf();
    }

    /**
     * Get document frequency of a given term
     * @param term
     * @return frequency
     */
    public int getDocFreq(String term){
        try {
            LookUp info = new LookUp((JSONObject) lookUpTable.get(term));
            return info.getDf();
        }
        catch (Exception e){
            System.out.println(term);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get SceneID of a given docID
     * @param docID The given docID
     * @return SceneID corresponded
     */
    public String docToScene(int docID){
        return sceneIDList.get(docID-1);
    }

    /**
     * Setter: set the compress_flag. The compress_flag indicate if the index is compressed or not
     * @param compress_flag 0 or 1; uncompressed or compressed
     */
    public void setCompress_flag(int compress_flag) {
        this.compress_flag = compress_flag;
    }

    /**
     * Setter: Store the invertedList in Memory
     * @param invertedList invertedList in memory
     */
    public void setInvertedList(InvertedList invertedList) {
        this.invertedList = invertedList;
    }

    /**
     * Getter: Get the invertedList from Memory
     * @return InvertedList of the index
     */
    public InvertedList getInvertedList() {
        return invertedList;
    }

    /**
     * Return the total number of document
     * @return total number of document
     */
    public int getDocNum() {
        return docNum;
    }

    /**
     * Load the Auxiliary Structure into Memory
     * @throws IOException
     * @throws ParseException
     */
    public void load() throws IOException, ParseException {

        JsonReader lookupreader = new JsonReader(lookUpFile);
        this.lookUpTable=lookupreader.read();
        System.out.println(lookUpTable.keySet().size());
        if(scenIDandLen=="") return;
        BufferedReader scanner = new BufferedReader(new FileReader(scenIDandLen));
        Tokenizer tokenizer = new BlankSpaceTokenizer();
        sceneIDList= tokenizer.tokenize(scanner.readLine());
        List<String> list= tokenizer.tokenize(scanner.readLine());
        for(String s:list){
                docLen.add(Integer.parseInt(s));
        }

        scanner.close();
        JsonReader docVectreader = new JsonReader(dvf);
        JSONObject dvtmp = docVectreader.read();
        for(String i: (Set<String>) dvtmp.keySet() ){
            int docid = Integer.valueOf(i);
            docVectors.put(docid, DocumentVector.map2dv(docid,(Map<String, Double>) dvtmp.get(i),this));
        }
    }

    /**
     * Return the total number 0f Unique Term
     * @return the total number 0f Unique Term
     */
    public int getTotalUniqueTermsCount() {
        if(invertedList.getInvList()!=null)
            return  invertedList.getInvList().keySet().size();
        else if(totalUniqueTerms>0)
            return totalUniqueTerms;
        else
            return lookUpTable.keySet().size();
    }

    /**
     * Return the total number of terms.
     * @return the total number of terms.
     */
    public int getTotalTermsCount(){
        if(invertedList.getInvList()!=null&&invertedList.getTotal_terms()!=0)
            return invertedList.getTotal_terms();
        else if(totalTerms>0)
            return totalTerms;
        else{
            int total =0;
            for(String key:(Set<String>)lookUpTable.keySet()){
                total+=new LookUp((JSONObject) lookUpTable.get(key)).getCdf();
            }
            return total;
        }


    }

    /**
     * Return A set of all unique terms in this collection
     * @return the set of volcabury
     */
    public Set<String> getVolcabury(){
        if(invertedList.getInvList().keySet().size()<=0)
            return lookUpTable.keySet();
        return invertedList.getInvList().keySet();
    }

    /**
     * Create an index in memory by doing the indexing process based on the collection file.
     * @throws IOException
     * @throws ParseException
     */
    public void build() throws IOException, ParseException {
        JsonReader jsonFile = new JsonReader(sourcefile);
        Fetcher fetcher = new JsonFetcher(jsonFile.read());
        Tokenizer tokenizer = new BlankSpaceTokenizer();
        docLen= new ArrayList<Integer>();
        sceneIDList= new ArrayList<String>();
        playIDandLength= new HashMap<>();
        invertedList=new InvertedList();
        while(fetcher.hasNext()){
            Document newDoc = fetcher.next();
            Map<String,String> metaInfo = newDoc.getMetaInfo();
            String sceneId = metaInfo.get("sceneId");
            String playId =metaInfo.get("playId");
            List<String> tokens = tokenizer.tokenize(newDoc.getDocument());
            docLen.add(tokens.size());
            sceneIDList.add(sceneId);
            playIDandLength.put(playId,playIDandLength.getOrDefault(playId,0)+tokens.size());
            for(int pos =0; pos<tokens.size(); ++pos){
                newDoc.addToVector(tokens.get(pos));
                invertedList.add(tokens.get(pos),newDoc.getDocID(),pos+1);
            }
            docVectors.put(newDoc.getDocID(),newDoc.getDocumentVector());
        }
        this.docNum=fetcher.numofDocument;

    }

    public DocumentVector getDocVector(int docID){
        return docVectors.get(docID);
    }

    private void docVectsTodisk(String docvectors) throws IOException {
        JsonWriter writer = new JsonWriter(docvectors);
        JSONObject docVects = new JSONObject();

        for(Integer key: this.docVectors.keySet()){
            docVects.put(key,JsonWriter.mapTojson(docVectors.get(key).getVector()));
        }
        writer.write(docVects);
    }
    /**
     * Write the index into compressed/uncompressed binary file and Write Auxiliary file to disk.
     * @param compress_flag 0 or 1; is uncompressed or compressed
     * @throws IOException
     */
    public void to_disk(int compress_flag) throws IOException {
        JSONObject config  = new JSONObject();
        String binaryfile="";
        String lookUpFile="";
        String configFile = "";
        String docVectors = "docVectors.json";
        if(compress_flag==1){
            binaryfile = "index.compressed";
             lookUpFile = "compressedLookUpTable.json";
            configFile = "compressedIndexInfo.json";
        }
        else {
            binaryfile = "index.uncompressed";
             lookUpFile = "lookUpTable.json";
            configFile = "IndexInfo.json";
       }
        docVectsTodisk(docVectors);
        config.put("docVectors",docVectors);
        config.put("lookUpFile",lookUpFile);
        config.put("sourcefile",sourcefile);
        config.put("compress_flag",compress_flag);
        config.put("docNum",docNum);
        config.put("totalTerms",getTotalTermsCount());
        config.put("totalUniqueTerms",getTotalUniqueTermsCount());
        config.put("binaryfile",binaryfile);
        config.put("scenIDandLen","scenIDandLen.txt");

        Map<String,LookUp> lookUpTable =invertedList.lists2file(binaryfile,compress_flag);
        JsonWriter loopupwriter = new JsonWriter(lookUpFile);
        JSONObject lookUpTableJson = new JSONObject();
        for(String key: lookUpTable.keySet()){
            lookUpTableJson.put(key,lookUpTable.get(key).toJson());
        }
        System.out.println(lookUpTableJson.keySet().size());
        loopupwriter.write(lookUpTableJson);
        JsonWriter configwriter = new JsonWriter(configFile);
        configwriter.write(config);
        PrintWriter writer = new PrintWriter(new File("scenIDandLen.txt"));
        String line="";
        for(String s: sceneIDList){
            line +=s;
            line += " ";
        }
        writer.println(line);
        line ="";
        for(Integer i: docLen){
            line +=i;
            line += " ";
        }
        writer.println(line);
        writer.close();

    }

    /**
     * Get the PostingsList of a given term from the binary file.
     * @param key The given term
     * @return The posting list of the given term.
     * @throws IOException
     */
    public PostingsList get(String key) throws IOException {
        Compression c;
        if(compress_flag==1){
            c = new V_Bytes();
        }
        else {
            c=new NotCompressed();
        }

        LookUp term_info = new LookUp((JSONObject) lookUpTable.get(key));
        BinaryFileReader reader = new BinaryFileReader(binaryfile,term_info.getOffset());
        List<Integer> decoded = reader.readInt(term_info.getBytesize(),c);
        PostingsList plist = PostingsList.fromIntegerList(decoded,compress_flag);
        return plist;
    }

    /**
     * Return a newly constructed index from a given binary file and look up table.
     * @param compress_flag 0 or 1 indicate uncompressed Binary and compressed binary
     * @param sourcefile the collection file path
     * @param indexfile the binary file path
     * @param lookupfile the look up table file path
     * @return A new Index Object
     * @throws IOException
     * @throws ParseException
     */
    public static SimpleIndex build_from_binary(int compress_flag,String sourcefile,String indexfile,String lookupfile) throws IOException, ParseException {
        SimpleIndex simpleIndex = new SimpleIndex(sourcefile,indexfile,lookupfile);
        simpleIndex.setCompress_flag(compress_flag);
        simpleIndex.load();
        InvertedList newList = simpleIndex.getInvertedList();
        for(String key: (Set<String>)simpleIndex.lookUpTable.keySet()) {
            PostingsList plist = simpleIndex.get(key);
            newList.add_term(key,plist);
        }
        simpleIndex.setInvertedList(newList);
        return  simpleIndex;
    }

    /**
     * The retrieval API Query  a String q and return the best k results based on the index
     * @param q the query sentence
     * @param k the number of result needed
     * @return [[docID,scores],[]...]
     * @throws IOException
     */
    public List<Entry> query(String q,int k,Scorer G,Scorer F,String option) throws IOException {
        Tokenizer tokenizer = new BlankSpaceTokenizer();
        Retriever retriever = RetrieverFactory.produce(option);
        List<String> Q = tokenizer.tokenize(q);
        return retriever.retrieval(this,Q,docNum,G,F,k);
    }

    /**
     * Get the Dice Coefficient between term1 and term2, processed in memory.
     * @param term1
     * @param term2
     * @return Dice Coefficient
     * @throws IOException
     */
    public double getDiceCoeFast(String term1,String term2) throws IOException{
        if(term1.equals(term2)) return 0;
        PostingsList term1_list = invertedList.getPostings(term1);
        term1_list.resetIterator();
        PostingsList term2_list = invertedList.getPostings(term2);
        term2_list.resetIterator();
        int na = new LookUp((JSONObject)this.lookUpTable.get(term1)).getCdf();
        int nb = new LookUp((JSONObject)this.lookUpTable.get(term2)).getCdf();
        int nab = 0;
        while (term1_list.getCurr_pos()!=null &&term2_list.getCurr_pos()!=null){
            int docID = Math.max(term1_list.getCurr_pos().getDocID(),term2_list.getCurr_pos().getDocID());
            term1_list.skipTo(docID);
            term2_list.skipTo(docID);
            if(term1_list.getCurr_pos()==null || term2_list.getCurr_pos()==null) break;
            if(term1_list.getCurr_pos().getDocID()==term2_list.getCurr_pos().getDocID()){
                Postings t1 = term1_list.getCurr_pos();
                t1.resetPos();
                Postings t2 = term2_list.getCurr_pos();
                t2.resetPos();
                while(t1.getCurr_pos()>0&&t2.getCurr_pos()>0){
                    int pos = Math.max(t1.getCurr_pos(),t2.getCurr_pos());
                    t1.skipTo(pos-1);
                    t2.skipTo(pos);
                    if(t1.getCurr_pos()<0||t2.getCurr_pos()<0) break;
                    if(t1.getCurr_pos()==t2.getCurr_pos()-1){
                        nab++;
                        t1.movePastPos(pos);
                        t2.movePastPos(pos+1);
                    }
                }
                term1_list.nextPosting();
                term2_list.nextPosting();
            }
        }

        double nanb = na+nb;
        return nab/nanb;
    }

    /**
     * Get the Dice Coefficient between term1 and term2, processed from disk.
     * @param term1
     * @param term2
     * @return Dice Coefficient
     * @throws IOException
     */
    public double getDiceCoeSlow(String term1,String term2) throws IOException {
        Retriever retriever =new ConjucntiveDocumentAtATimeRetrieval();
        Scorer G = new TrivalScorer();
        Scorer F = new ConjunctiveSumFreqScorer();
        int na = new LookUp((JSONObject)this.lookUpTable.get(term1)).getCdf();
        List<String> Q = new ArrayList<String>();
        Q.add(term1);
        Q.add(term2);
        double nab =retriever.retrieval(this,Q,docNum,G,F,1).get(0).getValue();
        int nb = new LookUp((JSONObject)this.lookUpTable.get(term2)).getCdf();
        double nanb = na+nb;
        return nab/nanb;
    }

    /**
     * Return the term with the max Dice Coefficient with the given term
     * @param term the given term
     * @return [term,score]
     * @throws IOException
     */
    public String[] getMaxDiceCoeTermAndScore(String term) throws IOException {
        Retriever retriever =new ConjucntiveDocumentAtATimeRetrieval();
        Scorer G = new TrivalScorer();
        Scorer F = new ConjunctiveSumFreqScorer();
        int na = new LookUp((JSONObject)this.lookUpTable.get(term)).getCdf();
        double maxScore = -1;
        String output=null;
        for(String term2: this.getVolcabury()){
            double newScore = getDiceCoeFast(term,term2);
            if(maxScore<newScore){
                maxScore = newScore;
                output=term2;
            }
        }
        String[] result = new String[2];
        result[0]=output;
        result[1]=maxScore+"";
       return result;
    }

    public Double getPrior(String name, int docID) throws IOException {
        Double score = null;
        if(docID<1||docID>getDocNum())
            return score;
        RandomAccessFile reader =  new RandomAccessFile(name+".prior","r");
        reader.seek((docID-1)*8);
        score = reader.readDouble();
        reader.close();
        return score;
    }

}
