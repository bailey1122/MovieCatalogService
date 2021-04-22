Resilient and fault-tolerant microservice which communicates with other microservices. Microservices have been created using asyncronous programming, a load balancer, a circuit breaker pattern which includes the timeout, the number of requests, failure percentage, sleep window and the fallback mechanism, a proxy pattern which intercepts requests to perform circuit breaker logic and calls the fallback as necessary, and a bulkhead pattern (all 3 patterns are implemented and configured by Hystrix), Spring Boot, Spring Cloud, and Eureka. The microservices publish and consume using Eureka client.
