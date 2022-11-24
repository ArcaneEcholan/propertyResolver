package fit.wenchao;

import fit.wenchao.propertyResolver.*;
import fit.wenchao.utils.GenericTypeContextResolver;
import fit.wenchao.utils.IGenericTypeContextResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public class Factory {

    public static IPropertyResolver getPropertyResolver() {
        return new PropertyResolver(getGenericTypeContextResolver());
    }

    public static IGenericTypeContextResolver getGenericTypeContextResolver() {
        return new GenericTypeContextResolver();
    }

    public static ResettableInputStream getStringResettableInputStreamFromResource(String resource) throws IOException {
        InputStream inputStream = PropertyResolver.class.getClassLoader().getResourceAsStream(resource);
        Field in = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();

        return getStringResettableInputStreamFromString(baos.toString("UTF-8"));
    }

    public static ResettableInputStream getStringResettableInputStreamFromString(String src) throws IOException {
        return new StringResettableInputStream(new StringInputStream(src));
    }

}
