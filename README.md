# URL Shortener Spring Microservices

## Running with Docker Compose

1. **Start PostgreSQL database:**
   ```sh
   docker-compose up -d
   ```

2. **Build Spring Boot service jars:**
   For each service (url-service, user-service):
   ```sh
   cd url-service
   ./mvnw clean package -DskipTests
   cd ../user-service
   ./mvnw clean package -DskipTests
   ```

3. **Build Docker images:**
   ```sh
   docker build -t url-service:latest ./url-service
   docker build -t user-service:latest ./user-service
   ```

4. **Run the services (example):**
   ```sh
   docker run --network host url-service:latest
   docker run --network host user-service:latest
   ```
   Or add them to your docker-compose.yml for orchestration.

## Database Configuration
- The services are configured to use PostgreSQL at `localhost:5432` with database `urlshortener`, user `user`, password `password`.
- H2 configs are commented out for easy switching.

## Cloud Deployment Notes
- For cloud, update the database host/user/password in `application.yml` to match your managed database.
- Build and push Docker images to your registry.
- Use a managed PostgreSQL (e.g., AWS RDS, GCP Cloud SQL) and update connection strings.
- Use orchestration (Kubernetes, ECS, etc.) for production deployments. 