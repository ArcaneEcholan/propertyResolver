package fit.wenchao.propertyResolver.example;


import fit.wenchao.propertyResolver.propertiesProcessor.CustomPropertyValueProducer;
import fit.wenchao.propertyResolver.propertiesProcessor.Default;
import fit.wenchao.propertyResolver.propertiesProcessor.Mandatory;
import fit.wenchao.propertyResolver.propertiesProcessor.True;

import java.util.List;

//@ConfProfile("dev")
//@ConfPrefix("config")
public interface Config {

    @Mandatory
    public String classpath();

    @Default(CustomPropertyValueProducer.class)
    public String output();

    @Default(True.class)
    public String encryptAll();

    public List<String> keep();

    public List<String> encrypt();

    public ConfigSec sec1();

    List<ConfigSec> secs();

}
