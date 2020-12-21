package fetchers;

public abstract class Fetcher {
    public int numofDocument;
    public Fetcher(){
        numofDocument =0;
    }
    public abstract Document next();
    public abstract boolean hasNext();
}
