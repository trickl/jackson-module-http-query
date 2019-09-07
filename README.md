# Jackson Module HTTP Query
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.trickl/jackson-module-http-query/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.trickl/jackson-module-http-query)
[![build_status](https://travis-ci.com/trickl/jackson-module-http-query.svg?branch=master)](https://travis-ci.com/trickl/jackson-module-http-query)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

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
mapper.registerModule(new HttpQueryModule());
```

after which functionality is available for all normal Jackson operations.

### Usage Example

TODO!
