package tokenizer;
import java.util.List;

public abstract class Tokenizer {
    public abstract List<String> tokenize(String document);
}
