# Warehouse Software API

This is a REST API to load the inventory, check the availability and sell the products.

## What has been implemented?
Created an API using Spring Boot with the following 4 simple endpoints with URI Versioning.
- /v1/uploadFile
- /v1/uploadMultipleFiles
- /v1/getProductAvailability
- /v1/sellProduct

The first 2 endpoints can be used to read inventory.json and products.json files and later can be used for the following requirements :
* Get all products and quantity of each that is an available with the current inventory
* Remove(Sell) a product and update the inventory accordingly

##### Built With
- JDK 11
- Spring Boot 2.5
- Maven 3.6.0 
- JUnit 5, Mockito, MockMVC - UnitTesting
- Jackson - Data Binding
- Lombok - Convenience library for reducing boilerplate code.
- IntelliJ - IDE
- Postman - API Client
- Slf4j - Logging

## Next Iteration

- ###### Add Database to persist data and avoid storing and reading files from local directory
     Right now everytime there is a request, we are reading data directly from the files placed in local directory. After reading data from the json files,
  we can persist it into Database and get the data from DB for availability check and selling. We can go for mySQL or Mongo Database with Spring JPA. 
  The main thing to consider while integrating with DB will be to handle multiple threads while read and update inventory.
  We can use Spring provided transaction and lock mechanism to update the inventory.
- ###### Add an Authentication for API security
  Currently we don't have any Authentication/Authorization in place. We can go for Basic Authentication 
  initially. But to more secure application,we can implement Identity and Access Management (IAM) tasks using OAuth 2.0, an authorization framework, and OpenID Connect (OIDC),
  a simple identity layer on top of it.
- ###### Increase the unit test coverage
  We have only 25% unit test coverage which is not enough. There are lot more test scenarios to consider as well as need to test the business logic 
  implemented in the service layer as well.
- ###### Provide more endpoints for a user friendly experience
    We can add an endpoint to sell multiple products together. 
- ###### Better Exception/Error handling
    Need to add validations to verify the file uploaded is in required format. If there is any 
  such validation issues, return meaningful error message to the user.
  Used ResponseStatusException to modify the HTTP Response status code because it was easier to implement in a prototype. 
  But going forward, this will lead to code duplication. However, we can consider using @ExceptionHandler with the 
  @ControllerAdvice annotation, which provides a global approach.
 - ###### Build and Deployment
   Currently, we are using local build and deployment using Spring boot default embedded Tomcat.We can move the solution to cloud
by creating a docker image using Docker desktop and push it to AWS ECR repository and deploy the docker image with ECS.

## Future
- Go with an API Gateway(eg: AWS API Gateway, KONG etc).It will provide authentication plugins.It can also act as a reverse proxy, load balancer etc.
- For better scalability and maintenance purpose, it will be better if we separate file handling logic from API. There can 
be another layer which consumes this file and will be put it in some cloud storage like AWS S3. Then write a consumer (like AWS Lambda)
  to process this file and call API endpoints to process the data and save the inventory in DB.
- Implementing pagination will be critical once we scale up and will be helping with the performance as well.
- We developed a web-based service that handles the core operations involving inventory and products.This so far is better
  described as RPC. It will be better if we can include some hypermedia (eg: Spring HATEOAS) in our representations so that
  clients  don't need to hard code URI's to navigate the API. Hateoas will give us the constructs to define a RESTful
  service and then render it in an acceptable format for client consumption.
- API Documentation (Swagger UI, OpenAPI etc)
- We can incorporate H2 for Integration Testing
- Incorporate Logging and Monitoring mechanisms- (Splunk/ELK, Prometheus/AWS CloudWatch)
- Web hooks for Lint Check and Automated unit testing before promoting the code to master and Mandatory integration testing before
  promoting the code to production
- CI/CD Pipeline - Jenkins
- Once we move our solution to Cloud, we can setup load balancers and auto scaling in ECS, do Route53 configurations etc.
  

NOTE: For better resilience whenever we do any kind of modifications in the future,we have to think about backward compatibility, 
how we can upgrade with minimum or no downtime.






