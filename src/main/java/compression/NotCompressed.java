package compression;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class NotCompressed extends Compression {
    public NotCompressed(){

    }
    public List<Integer> decode(byte[] input) {
        List<Integer> output = new ArrayList<Integer>();
        ByteBuffer bytes = ByteBuffer.wrap(input);
        bytes.rewind();
        IntBuffer temp = bytes.asIntBuffer();
        int[] raw = new int[temp.limit()];
        temp.get(raw);
        for(int r : raw){
            output.add(r);
        }
        return output;
    }

    public byte[] encode( List<Integer>  input) {
        ByteBuffer bb =ByteBuffer.allocate(input.size()*4);
        IntBuffer ib = bb.asIntBuffer();
        for(int i: input) {
            ib.put(i);
        }
        return bb.array();
    }

}
