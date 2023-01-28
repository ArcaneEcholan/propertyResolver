package fit.wenchao.propertyResolver.example;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class TestProperty {

    String classpath;

    String encryptAll;

    List<String> keep;

    TestSecProperty sec;

    List<TestSecProperty> secs;

    TestSecProperty secNull;

}
