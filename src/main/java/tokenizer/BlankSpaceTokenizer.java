package tokenizer;

import java.util.Arrays;
import java.util.List;

public class BlankSpaceTokenizer extends Tokenizer {
    public List<String> tokenize(String document) {

        String[] split = document.split("\\s+");
        List<String> list = Arrays.asList(split);
        return list;
    }
}
