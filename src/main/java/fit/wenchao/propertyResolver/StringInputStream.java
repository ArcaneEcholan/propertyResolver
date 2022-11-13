package fit.wenchao.propertyResolver;


import java.io.*;

public class StringInputStream extends InputStream {

    private String src;
    int pos;

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
    public synchronized void reset() throws IOException {
        this.pos = 0;
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