The objective of this project is to develop a highly concurrent API for bank account transfers. The application is built using Java 17 and Spring Boot 3.4.0



Swagger is used for document and testing rest API following would be the Swagger URLs :-

1. http://localhost:18080/api-docs

3. http://localhost:18080/swagger-ui.html


For observable application, Actuator is used.

1. http://localhost:18080/actuator/health

2. http://localhost:18080/actuator/info

3. http://localhost:18080/actuator/metrics

Further Enhancements:-

1. Externalize Validation and Exception Messages: Currently, field validation and exception messages are hardcoded. These should be moved to an external configuration file for easier maintenance and localization.

2. Configure Spring Security with OAuth2: Implement OAuth2 for Spring Security to prevent unauthorized access and identify client devices.

3. Leverage Spring Data REST for CRUD Operations: Spring Data REST can be used to expose basic CRUD operations on our Account repository-managed entities via HTTP REST semantics. HATEOAS can be utilized to provide dynamic navigation through the API by including hypermedia links in the responses.

4. Async Notification Service: The Notification Service is currently implemented with asynchronous execution, but it can be exposed as a microservice. Messaging systems can be used for communication between services.

5. Customize Swagger Documentation: The basic Swagger documentation should be customized to provide more detailed API information, ensuring proper interaction and clarity for consumers.

6. Enhance Spring Actuator:

   1. Custom Health Indicator: Implement a custom Spring Actuator HealthIndicator to provide detailed health checks for the system.
   2. Metrics Collection: Utilize services like CounterService, GuageService, or Dropwizard to track the number of transactions and their respective execution times.
   3. Export Metrics: Metrics should be exported to external systems like Redis, OpenTSDB, StatsD, JMX, or Dropwizard for better observability and analysis.
   
7. Dynamic Scaling with Service Discovery: To scale based on concurrency and resource utilization, the application should automatically add or remove instances. A static IP configuration won't work for this. Service discovery (e.g., using Consul) should be implemented so that services can dynamically locate one another, providing a reliable source of truth for available services.

8. API Gateway: Use an API Gateway to handle client-side load balancing, circuit breakers, and other requirements like security, API translation, and protocol translation. This keeps mid-tier services free from these concerns, preventing the need for frequent redeploys. The API Gateway can proxy requests to microservices, and in some cases, microproxying combined with HTTPS and authentication might suffice for specific clients.

9. Switch to Feign Client: Replace RestTemplate with Feign Client, a declarative web service client that simplifies writing web service clients. Feign supports annotations, custom encoders and decoders, and integrates with Spring Cloud for load balancing using Ribbon and Eureka.

10. CI/CD Pipeline: Implement a Continuous Integration and Continuous Deployment (CI/CD) pipeline to automate the build, integration testing, and deployment processes for faster and more reliable releases.

11. Stateless Application: The application should be stateless. If state management is necessary, external caches or databases should be used for storage instead of maintaining state within the application itself.

12. Scalability through Horizontal Scaling: The application should be designed for horizontal scalability, allowing more instances to be added as needed. By using containerized microservices, horizontal scaling becomes easier and more efficient than relying on vertical scaling (i.e., adding more resources to a single instance).