package utils.streams;

import fit.wenchao.propertyResolver.resolver.ResettableFileInputStream;
import org.junit.Test;

import java.io.*;
import java.net.URL;

public class TestResettableStream {

    @Test
    public void test_reset() throws IOException {
        URL testFile = TestResettableStream.class.getClassLoader().getResource("testFile");
        if (testFile == null) {
            throw new FileNotFoundException("testFile");
        }
        String fileString = testFile.getFile();
        ResettableFileInputStream input = new ResettableFileInputStream(new FileInputStream(fileString));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
        int data = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

        input.reset();

        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

    }
}
