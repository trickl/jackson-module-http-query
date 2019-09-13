# Jackson Module HTTP Query
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.trickl/jackson-module-http-query/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.trickl/jackson-module-http-query)
[![build_status](https://travis-ci.com/trickl/jackson-module-http-query.svg?branch=master)](https://travis-ci.com/trickl/jackson-module-http-query)
[![Maintainability](https://api.codeclimate.com/v1/badges/b9266f5831a34c760b3a/maintainability)](https://codeclimate.com/github/trickl/jackson-module-http-query/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/b9266f5831a34c760b3a/test_coverage)](https://codeclimate.com/github/trickl/jackson-module-http-query/test_coverage)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A jackson module for allowing HTTP queries to be represented as typed objects

Installation
============

To install from Maven Central:

```xml
<dependency>
  <groupId>com.github.trickl</groupId>
  <artifactId>jackson-module-http-query</artifactId>
  <version>1.0.0</version>
</dependency>
```
### Registering module

Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new HttpQueryModule());
```

after which functionality is available for all normal Jackson operations.

### Purpose

Converts POJOs to query strings and vice versa. 

* Use type objects for convenient conversion to query strings (and back).
* Less boilerplate code required for supporting a variety of query combinations.
* Supports existing Jackson JSON annotations for formatting.
* Extra annotations allow for different strategies on handling multi-valued parameters.

### Usage Example

```java
@HttpQuery
private static class TypedExample {
    private String valueA;

    private String valueB;

    private int valueC;
}

// To produce "?valueA=test&valueB=testB&valueC=123"...
TypedQuery typed = new TypedQuery(... // omitted for brevity
String queryString = objectMapper.valueToTree(typedQuery).textValue();
```

### Features

* Supports many Jackson annotations
  - @JsonProperty (have a different query parameter name from the variable name).
  - @JsonIgnore (don't serialize a property)
  - @JsonFormat (useful for date strings).
* Supports new parameters
  - @HttpQuery (required to serialize to a query string, not a JSON object).
  - @HttpQueryDelimited (serialize a multi valued property using a delimited list e.g "?values=1,2,3").
  - @HttpQueryNoValue (allow boolean valueless params, e.g. "?debug")
