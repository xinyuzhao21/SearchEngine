package IO;

import compression.Compression;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class BinaryFileReader {
    RandomAccessFile file;
    long offset;
    public BinaryFileReader(String filename, long offset)throws IOException {
        this.file = new RandomAccessFile(filename,"r");
        this.offset = offset;
        file.seek(offset);
    }
    public byte[] readBytes(int bufflength)throws IOException {
        byte[] buffer = new byte[bufflength];
        file.read(buffer,0,bufflength);
        offset=file.getFilePointer();
        return buffer;
    }

    public List<Integer> readInt(int bufflength, Compression c)throws IOException {
        byte[] encoded = readBytes(bufflength);
        List<Integer> decoded = c.decode(encoded);
        return decoded;
    }

}
