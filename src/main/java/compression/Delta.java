package compression;

import java.util.ArrayList;
import java.util.List;

public class Delta extends Compression {
    private int prev;

    public  Delta(){
        prev=0;
    }
    public Delta(int start){
        this.prev=start;
    }
    public List<Integer> decode(byte[] input) {
        NotCompressed notCompressed = new NotCompressed();
        List<Integer> output = notCompressed.decode(input);
        return decode(output);
    }

    public List<Integer> decode(List<Integer> input){
        List<Integer> output = new ArrayList<Integer>();
        for(int i =0; i<output.size();i++){
            if(output.size()>1){
                System.out.println("what");
            }
            output.add(decode(input.get(i)));
        }
        return output;
    }

    public int decode(int input){
        input +=prev;
        prev=input;
        return input;
    }

    public byte[] encode(List<Integer> input) {
        NotCompressed notCompressed = new NotCompressed();
        byte[] output = notCompressed.encode(encodeInts(input));
        return output ;
    }

    public List<Integer> encodeInts(List<Integer> input){
        List<Integer> output = new ArrayList<Integer>();
        for(int i =0; i<input.size();i++){
            output.add(encode(input.get(i)));
        }
        return output;
    }

    public int encode(int input){
        int temp = input;
        input -=prev;
        prev=temp;
        return input;
    }
}
