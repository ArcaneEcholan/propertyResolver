package fit.wenchao.propertiesProcessor;

public class False implements PropertyValueProducer{
    @Override
    public String produce() {
        return "false";
    }
}
