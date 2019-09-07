# Jackson Module HTTP Query

A jackson module for allowing HTTP queries to be represented as typed objects

Installation
============

To install from Maven Central:

```xml
<dependency>
  <groupId>com.github.trickl</groupId>
  <artifactId>jackson-module-http-query</artifactId>
  <version>0.0.1</version>
</dependency>
```
### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new ParameterNamesModule());
```

after which functionality is available for all normal Jackson operations.

### Usage Example

TODO!
