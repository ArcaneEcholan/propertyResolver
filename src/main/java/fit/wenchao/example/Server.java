package fit.wenchao.example;

import fit.wenchao.propertiesProcessor.ConfPrefix;
import fit.wenchao.propertyResolver.PropertyResolver;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.security.acl.Owner;
import java.util.List;

@Getter
@ToString
@ConfPrefix("server.config")
public class Server {
    private String domain;
    private String port;
    private String ip;
    private List<String> admins;
    private List<Owner> owners;

    @Getter
    @ToString
    public static class Owner{
        private String name;
        private String age;
    }

    public static void main(String[] args) throws IOException {
        Server server = PropertyResolver.create(Server.class);
        System.out.println(server);
    }
}

