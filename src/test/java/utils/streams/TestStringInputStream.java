package utils.streams;

import fit.wenchao.propertyResolver.resolver.StringInputStream;
import org.junit.Test;

import java.io.*;

public class TestStringInputStream {

    @Test
    public void test_reset() throws IOException {
        StringInputStream stringInputStream = new StringInputStream("hello\nworld\n");
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
