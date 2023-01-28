package fit.wenchao.propertyResolver.resolver;


import java.io.*;

public class StringInputStream extends InputStream {

    private String src;

    int pos;

    int markPoint = 0;

    public StringInputStream() {
        this.src = "";
        this.pos = 0;
    }

    public StringInputStream(String src) {
        this.src = src;
        this.pos = 0;
    }


    @Override
    public int read() {
        if(pos >= src.length()) {
            return -1;
        }
        return src.charAt(pos++);
    }

    @Override
    public synchronized void reset() {
        this.pos = markPoint;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void mark(int readlimit) {
        markPoint = pos;
    }

    public static void main(String[] args) throws IOException {
        StringInputStream stringInputStream = new StringInputStream("hello\nworld");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stringInputStream));
        int data = 0;
        String line;
        while((line=bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

        stringInputStream.reset();

        while((line=bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

    }

}