package fit.wenchao.propertyResolver.resolver;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static fit.wenchao.propertyResolver.utils.StrUtils.ft;

@Slf4j
public class StringResettableInputStream implements ResettableInputStream {

    private final InputStream inputStream;

    private String markNotSupportMsg(InputStream inputStream) {
        return ft("Inner stream: {} does not support mark operation.", inputStream.getClass());
    }

    public StringResettableInputStream(InputStream inputStream) {
        if (!inputStream.markSupported()) {
            throw new UnsupportedOperationException(markNotSupportMsg(inputStream));
        }
        this.inputStream = inputStream;
    }

    @Override
    public void reset() {
        try {
            inputStream.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void mark() {
        if (!inputStream.markSupported()) {
            throw new UnsupportedOperationException(markNotSupportMsg(inputStream));
        }
        inputStream.mark(0);
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public BufferedReader getBufferedReader() {
        return new BufferedReader(new InputStreamReader(inputStream));
    }
}
