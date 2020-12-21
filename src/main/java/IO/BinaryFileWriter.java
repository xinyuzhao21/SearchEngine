package IO;

import compression.Compression;

import java.io.*;
import java.util.List;

public class BinaryFileWriter {
    RandomAccessFile file;
    long offset;
    String filename;

    public BinaryFileWriter(String filename, int offset) throws IOException {
        this.filename = filename;
        File out = new File(filename);
        this.offset =offset;
        file = new RandomAccessFile(filename,"rw");
    }

    public long getOffset()  throws IOException{
        return offset;
    }

    public long writeBytesNonStop(byte[] list) throws IOException{
        file.write(list);
        offset = file.getFilePointer();
        return this.getOffset();
    }

    public long writeBytes(byte[] list) throws IOException{
        file = new RandomAccessFile(filename,"rw");
        file.seek(offset);
        file.write(list);
        offset = file.getFilePointer();
        file.close();
        return this.getOffset();
    }

    public void close() throws IOException {
        file.close();
    }

    public long writeInts(List<Integer> list, Compression e)throws IOException{
        byte[] encoded = e.encode(list);
        writeBytes(encoded);
        return this.getOffset();
    }
}
