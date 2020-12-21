package compression;

import java.util.ArrayList;
import java.util.List;

public abstract class Compression {
    public abstract List<Integer> decode(byte[] input);
    public abstract byte[] encode(List<Integer> input);
}
