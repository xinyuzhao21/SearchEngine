package retriever;

public class RetrieverFactory {
    public static Retriever produce(String Option){
        if(Option.equals("BM25")){
            return new BM25Retrieval();
        }
        if(Option.equals("LanguageModel")){
            return new LanguageModelRetrieval();
        }
        return new DocumentAtATimeRetrieval();
    }
}
