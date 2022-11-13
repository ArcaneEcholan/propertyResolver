# Property Resolver

该项目为解析properties文件提供了便利。通过创建与properties对应的 Javabean 引用properties中的值。目前支持的properties类型为：

* String
* Object(自定义的properties Javabean 类)
* List(支持嵌套List，List中只能是List或String或properties Javabean)

## Install

mvn -DskipTests=true install

## Usage

写properties文件（注意，名称只能为 appconfig.properties）

```properties
# appconfig.properties

server.domain=www.wenchao.fit
server.port=8080
server.ip=192.168.12.33

server.admins[]=wc
server.admins[]=cc
server.admins[]=xc

```

创建 properties Javabean 并解析properties文件

注意，您的Javabean必须有一个public的默认构造
```java
// lombok
@Getter
@ToString
public class Server {
    private String domain;
    private String port;
    private String ip;
    private List<String> admins;

    public static void main(String[] args) throws IOException {
        Server server = PropertyResolver.create(Server.class);
        System.out.println(server);
    }
}
```

## Features

### Config Prefix

使用@ConfPrefix 注解指定property前缀；默认前缀是类名的点连接符形式（类 ServerOwner 的前缀为 server.owner）

注意，如果Javabean作为嵌套Object，则前缀不生效

```properties
# appconfig.properties

server.config.domain=www.wenchao.fit
server.config.port=8080
server.config.ip=192.168.12.33

server.config.admins[]=wc
server.config.admins[]=cc
server.config.admins[]=xc
```

```java
// lombok
@Getter
@ToString
@ConfPrefix("server.config") // or-> public class ServerConfig {
public class Server {
    private String domain;
    private String port;
    private String ip;
    private List<String> admins;

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
```


### Nested Javabean

```properties
# appconfig.properties

server.domain=www.wenchao.fit
server.port=8080
server.ip=192.168.12.33

server.admins[]=wc
server.admins[]=cc
server.admins[]=xc

server.owner.name=wc
server.owner.age=23

```


```java
// lombok
@Getter
@ToString
public class Server {
    private String domain;
    private String port;
    private String ip;
    private List<String> admins;
    private Owner owner;

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
```


### List Javabean


```properties
# appconfig.properties

server.domain=www.wenchao.fit
server.port=8080
server.ip=192.168.12.33

server.admins[]=wc
server.admins[]=cc
server.admins[]=xc

server.owners[1].name=wc
server.owners[1].age=25
server.owners[2].name=cc
server.owners[2].age=23
server.owners[3].name=ee
server.owners[3].age=21


```


```java
// lombok
@Getter
@ToString
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
```




