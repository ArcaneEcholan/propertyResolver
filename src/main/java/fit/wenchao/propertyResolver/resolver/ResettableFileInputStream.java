package fit.wenchao.propertyResolver.resolver;

import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ResettableFileInputStream extends FilterInputStream {
    private FileChannel fileChannel;

    public ResettableFileInputStream(FileInputStream fis) {
        super(fis);
        fileChannel = fis.getChannel();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public synchronized void reset() throws IOException {
        fileChannel.position(0);
    }

}