package fit.wenchao.propertyResolver;


import fit.wenchao.Factory;

import java.io.IOException;

public class PropertyResolvers {
    public static <T> T create(Class<T> configClass) throws IOException {
        IPropertyResolver propertyResolver = Factory.getPropertyResolver();
        return propertyResolver.create(configClass);
    }
}
