package fit.wenchao.propertyResolver.resolver;


import fit.wenchao.propertyResolver.Factory;

import java.io.IOException;

public class PropertyResolvers {
    //static {
    //    ClassInitiator.initPackage("fit.wenchao.propertyResolver");
    //}
    public static <T> T create(Class<T> configClass) throws IOException {
        IPropertyResolver propertyResolver = Factory.getPropertyResolver();
        return propertyResolver.create(configClass);
    }
}
