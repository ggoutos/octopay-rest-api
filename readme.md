#  Rest API Example

## Requirements

For building and running the application you need:

- [JDK 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
- [Maven 3](https://maven.apache.org)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `eu.octopay.Application` class from your IDE. Local datasource is H2.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

For production build make sure you set active profile for MySQL 8 datasource to be activated.

```shell
mvn spring-boot:run -Dspring.profiles.active=prod
```

## Rest API

There is implemented a demo core banking system supporting concurrent debit and credit operations on a bank account that always yield correct balance. The API is also documented in [OpenAPI 3.0](http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/).

### Endpoints

- GET: [/accounts](http://localhost:8080/accounts): Get all accounts. (For testing purposes)
- GET: [/accounts/{id}](http://localhost:8080/accounts/id): Find account by id
- GET: [/accounts/{id}/operations?dateFrom={optional_date}&dateTo={optional_date}](http://localhost:8080/accounts/id/operations?dateFrom=[date]]&dateTo=[date]): Find account credit/debit operations optionally filtered by a date range in "dd.MM.yyyy" format, ordered by their timestamp created descending.
- GET: [/accounts/{id}/balance](http://localhost:8080/accounts/id/balance): Find account balance
- POST: [/accounts/{id}/debit](http://localhost:8080/accounts/id/debit): Execute a debit account operation on the account passing in a json request body with the amount in big decimal form and a comment, like the following:

``
{
"amount": 0,
"comment": "string"
}
``

- POST: [/accounts/{id}/credit](http://localhost:8080/accounts/id/credit): Execute a credit account operation on the account passing in a json request body with the amount in big decimal form and a comment, like the following:

``
{
"amount": 0,
"comment": "string"
}
``

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.5/maven-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#using-boot-devtools)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.5.5/reference/htmlsingle/#boot-features-jpa-and-spring-data)

### Guides/Tutorials

The following guides illustrate how to use some features concretely:

* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Building a Hypermedia-Driven RESTful Web Service](https://spring.io/guides/gs/rest-hateoas/)
* [Documenting a Spring REST API Using OpenAPI 3.0](https://www.baeldung.com/spring-rest-openapi-documentation)
* [Managing Transactions](https://spring.io/guides/gs/managing-transactions/)
* [Enabling Transaction Locks in Spring Data JPA](https://www.baeldung.com/java-jpa-transaction-locks)
* [Optimistic Locking in JPA](https://www.baeldung.com/jpa-optimistic-locking)
* [Testing Optimistic Locking Handling with Spring Boot and JPA ](https://blog.mimacom.com/testing-optimistic-locking-handling-spring-boot-jpa/)
* [A beginner’s guide to transaction isolation levels in enterprise Java](https://vladmihalcea.com/a-beginners-guide-to-transaction-isolation-levels-in-enterprise-java/)
* [Transaction Propagation and Isolation in Spring @Transactional](https://www.baeldung.com/spring-transactional-propagation-isolation)

