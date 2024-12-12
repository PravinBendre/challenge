The objective of this project is to develop a highly concurrent API for bank account transfers. The application is built using Java 17 and Spring Boot 3.4.0



Swagger is used for document and testing rest API following would be the Swagger URLs :-

http://localhost:18080/swagger-resources

http://localhost:18080/v2/api-docs

http://localhost:18080/swagger-ui.html



For observable application, Actuator is used.

http://localhost:18080/health

http://localhost:18080/info

http://localhost:18080/metrics

http://localhost:18080/trace

Further Enhancements:-

1. Field validation message and exception messages are hard coded that can be moved to external config file.

2. Configure spring security with OAuth2 to prohibit unauthorized access and indentify the client device.

3. Spring data rest can be used to provide a solid foundation on which to expose CRUD operation to our Account repository managed entities using plain HTTP Rest Semantics. HATEOAS provides info to navigate the REST interface dynamically by including hypermedia links with response.

4. For the moment Notification Service is implemented as Async execution which can be expose as microservice and Messaging can be use for communication.

5. Basic Swagger document is used that can be customize.

6. Actuator enhancement :-

    1. Spring actuator custom HealthIndicator can be implemented to get detail custom info.
    2. CounterService , GuageService or Dropwizard can be used to count no of transactions done and time taken by each transaction.
    3. Metrics can be exported to external db like Redis,Open TSDB, Statsd, JMX ,Dropwizard.

7. Based on concurrency and resource utilization, our transaction app should be able to scale out by adding more instances and once load reduces should be shut down the additional instance. To implement the same hard coding of IP isn't going to work. We will need a discovery(Consul) mechanism that services can use to find each other. This means having a source of truth for what services are available.

8. We can use API Gateway :API gateways(circuit breakers, client-side load balancing) for various reasons like to have logical place to insert any client-specific requirements (security, API translation, protocol translation) and keep the mid-tier services free of this burdensome logic (as well as free from associated redeploys!).

    Proxy requests from an edge-service to mid-tier services with a microproxy. For some classes of clients, a microproxy and security (HTTPS, authentication) might be enough.

9. We can replace RestTemplate with Feign Client, it is a declarative web service client. It makes writing web service clients easier. It has pluggable annotation support including Feign annotations and JAX-RS annotations. Feign also supports pluggable encoders and decoders. Spring Cloud adds support for Spring MVC annotations and for using the same HttpMessageConverters used by default in Spring Web. Spring Cloud integrates Ribbon and Eureka to provide a load balanced http client when using Feign.

10. We can create CI/CD pipeline to continuous build, integration test and deployment.

11. Stateless application :  should not manage any state if state management is required then it should be managed it in external cache or DB.

12. Scalability : we should be able to horizontally scale out the application by adding more resources not the vertically scale up application by adding more resource. By deploying microservices using container it is easy to scale out a app.

