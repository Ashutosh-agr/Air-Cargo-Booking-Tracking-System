Performance Considerations:
•	Why findSecondLeg uses between arrivalDate and arrivalDate+1: 
This query pattern allows the system to find connecting flights that arrive either on the same day or the next day, accommodating overnight layovers. It increases the chance of finding valid connections for multi-leg journeys, improving user experience and booking flexibility.
•	Why indexes are added: 
Indexes are created on frequently queried columns (like refId, flightNumber, or date fields) to speed up search and retrieval. Without indexes, the database must scan the entire table for each query, which is slow for large datasets. Indexes allow the database to quickly locate relevant rows, reducing query latency and improving throughput.
•	Why caching is used: 
Caching is used to store frequently accessed data (like flight schedules or booking details) in memory, reducing repeated database queries. This improves response times and reduces database load, especially for read-heavy or rarely changing data.  In this project, a distributed cache (such as Redis) is typically used, often integrated via Spring Cache abstraction. Distributed caching is chosen because:  
o	It allows multiple application instances to share cached data, ensuring consistency and scalability in clustered or microservices environments.
o	Redis provides fast, in-memory access and supports features like TTL (time-to-live) and eviction policies.
o	It helps maintain high availability and performance even as the system scales horizontally.

This approach ensures that all nodes in the system benefit from the cache, not just a single instance, making it suitable for cloud-native and scalable architectures. Why version optimistic locking is used: Optimistic locking (using a @Version field) ensures data consistency during concurrent updates. Each update checks the version; if another transaction has modified the record, the update fails. This prevents lost updates without locking the row, making it efficient for low-contention scenarios and improving overall throughput.
•	Why distributed locking with Redisson is used: 
Distributed locking is necessary when multiple application instances (e.g., in a microservices or clustered environment) might update the same resource concurrently. Redisson uses Redis to coordinate locks across instances, ensuring only one process can modify a resource at a time, preventing race conditions and data corruption.
•	Why in booking optimistic is used (@Version) instead of distributed: 
Optimistic locking is lightweight and sufficient when updates are infrequent or contention is low, and when all updates happen within a single database instance. It avoids the overhead of distributed coordination, making it more efficient for simple, local concurrency control.
•	Why in BookingService distributed lock is used instead of optimistic: 
Distributed locks are used in BookingService for operations that must be synchronized across multiple nodes (e.g., in a cloud or clustered deployment). Optimistic locking only works within a single database transaction, but distributed locks ensure that only one instance across the whole system can process a critical section at a time, providing stronger consistency guarantees in distributed environments.
•	Why HikariCP is used:
HikariCP is used as the connection pool for your data source because it is the default and recommended JDBC connection pool in Spring Boot. It is chosen for its high performance, low latency, and efficient resource management. HikariCP helps manage database connections efficiently, reducing overhead and improving the scalability and responsiveness of your application, especially under high load.
