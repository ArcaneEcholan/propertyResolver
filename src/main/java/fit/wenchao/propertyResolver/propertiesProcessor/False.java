package fit.wenchao.propertyResolver.propertiesProcessor;

public class False implements PropertyValueProducer{
    @Override
    public String produce() {
        return "false";
    }
}
