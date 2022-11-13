package fit.wenchao.propertyResolver;


import java.io.IOException;
import java.io.InputStream;

public class PropertyResolvers {
    public static <T> T create(Class<T> configClass) throws IOException {
        InputStream inputStream;
        inputStream = PropertyResolver.getConfInput();
        return PropertyResolver.createConf(inputStream,
                configClass, false, null, null, false, "");
    }
}
