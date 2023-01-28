package fit.wenchao.propertyResolver.resolver;

import java.io.BufferedReader;
import java.io.InputStream;

public interface ResettableInputStream {

    public void reset();

    public void mark();

    InputStream getInputStream();

    BufferedReader getBufferedReader();

}
