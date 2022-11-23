package fit.wenchao.propertyResolver;

import java.io.IOException;

public interface IPropertyResolver {
    <T> T create(Class<T> configClass) throws IOException;
}
