package fit.wenchao.example;

import fit.wenchao.propertiesProcessor.CustomPropertyValueProducer;
import fit.wenchao.propertiesProcessor.Default;
import fit.wenchao.propertiesProcessor.Mandatory;
import fit.wenchao.propertiesProcessor.True;

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
