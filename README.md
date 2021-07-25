# Warehouse Software API

This is a REST API to load the inventory, check the availability and sell the products.

### Getting Started
TODO: How to get started?
###### Prerequisites
TODO: What do they need to get started?
###### Installing
TODO: Installing

### Running the tests
Explain how to run the automated tests for this API including coding style tests

### Deployment

### Built With
Spring 5.3.8
Spring Boot 2.5.3
Java 11

### Authors

### License

### Acknowledgements


##### AUTHENTICATION & AUTHORIZATION
Currently we don't have any Authentication/Authorization in place. 
In the next Iteration we can implement Identity and Access Management (IAM) tasks using OAuth 2.0, an authorization framework, and OpenID Connect (OIDC),
a simple identity layer on top of it.

In future, it will be better to go with an API Gateway(eg: AWS API Gateway, KONG etc).It also acts as a reverse proxy, load balancer etc.

##### VERSIONING
URI Versioning

##### DATABASE

##### UNIT TESTING

##### INTEGRATION TESTING

##### BUILD 

##### DEPLOY

##### LINT CHECK

##### Pagination

##### LOGGING AND MONITORING

##### EXCEPTION AND ERROR HANDLING
Used ResponseStatusException class introduced in Spring 5 to modify the HTTP Response status code and custom message 
because it was easier to implement in a prototype. But going forward, this will lead to code duplication and difficult 
to enforce some application-wide conventions. However, we can consider using @ExceptionHandler with the @ControllerAdvice 
annotation, which provides a global approach. They deal with the separation of concerns very well. The app can throw 
exceptions normally to indicate a failure of some kind, which will then be handled separately.




##### API USECASES


##### DOCUMENTATION
Swagger UI


##### RPC to REST API
We developed a web-based service that handles the core operations involving inventory and products.This so far is better 
described as RPC. It will be better if we can include some hypermedia (eg: Spring HATEOAS) in our representations so that 
clients  don't need to hard code URI's to navigate the API. Hateoas will give us the constructs to define a RESTful 
service and then render it in an acceptable format for client consumption. A critical ingredient to any RESTful service 
is adding links to relevant operations

An important facet of REST is the fact that it’s neither a technology stack nor a single standard. REST is a collection of 
architectural constraints that when adopted make your application much more resilient. A key factor of resilience is that 
when you make upgrades to your services, your clients don’t suffer from downtime. So whenever we do any kind of modifications 
in the future,we have to think about backward compatibility, how we can upgrade with minimum or no downtime.





