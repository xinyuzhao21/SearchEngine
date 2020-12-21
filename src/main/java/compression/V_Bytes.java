package compression;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class V_Bytes extends Compression {
    public List<Integer> decode(byte[] input) {
        List<Integer> output = new ArrayList<Integer>();
        for(int i =0; i<input.length;i++){
            int position =0;
            int result =((int) input[i]&0X7F);
            while((input[i]&0X80)==0){
                i+=1;
                position+=1;
                int unsignedByte =((int)input[i]&0X7F);
                result |=(unsignedByte<<(7*position));
            }
            output.add(result);
        }
        return output;
    }

    public byte[] encode(List<Integer> input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for(int i: input){
            while(i>=128){
                output.write(i&0X7F);
                i>>>=7;
            }
            output.write(i|0X80);
        }
        return output.toByteArray();
    }


}
