package fit.wenchao.propertyResolver.propertiesProcessor;


/**
 * Use DEFAULT_OUTPUT_DIR as output default value.
 */
public class CustomPropertyValueProducer implements PropertyValueProducer {
    @Override
    public String produce() {
        return "hello";
    }
}
