package fit.wenchao.propertyResolver.propertiesProcessor;

public class True implements PropertyValueProducer{
    @Override
    public String produce() {
        return "true";
    }
}
