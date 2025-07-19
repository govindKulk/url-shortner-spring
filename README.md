# 🚀 URL Shortener Microservices Project

A modern, full-stack URL shortener built with Spring Boot microservices, PostgreSQL, and a Next.js frontend.

## 🧩 Features
- 🔗 **URL Shortening**: Create, manage, and track short URLs.
- 👤 **User Authentication**: Register, login, and manage your own URLs securely (JWT-based).
- 🗂️ **Microservices Architecture**: Decoupled services for scalability and maintainability.
- 🌐 **API Gateway**: Central entry point for all frontend/backend communication.
- 🗃️ **Service Discovery**: Eureka-based dynamic service registration.
- 📊 **URL Analytics**: Track visits and stats for each short URL.

## 🏗️ Services Overview
- **api-gateway**: Handles routing, CORS, and authentication for all requests.
- **user-service**: Manages user registration, login, and JWT token issuance/validation.
- **url-service**: Stores original/short URLs, handles redirection, and tracks stats.
- **service-discovery**: Eureka server for dynamic service registration.
- **frontend**: Next.js app for user interaction (create, view, and manage URLs).

## 🔒 How Authentication Works
- Users register/login via the frontend.
- JWT tokens are issued by `user-service` and stored in the browser.
- All protected API requests include the JWT for authentication.

## ✂️ How URL Shortening Works
- Authenticated users submit a long URL via the frontend.
- The `url-service` generates a unique short code and stores the mapping.
- Visiting a short URL (e.g., `/abc123`) redirects to the original URL and increments visit stats.

## 🖥️ Local Setup (Docker Compose)

1. **Clone the repo:**
   ```sh
   git clone <your-repo-url>
   cd url-shortner-spring
   ```
2. **Build all JARs:**
   ```sh
   ./mvnw clean package -DskipTests
   # Do this in each service directory if needed
   ```
3. **Build Docker images:**
   ```sh
   docker-compose build
   ```
4. **Start all services:**
   ```sh
   docker-compose up
   ```
5. **Start the frontend:**
   ```sh
   cd url-shortener-frontend
   npm install
   npm run dev
   # Visit http://localhost:3000
   ```

## ⚡ Quick Links
- API Gateway: [http://localhost:8080](http://localhost:8080)
- User Service: [http://localhost:8081](http://localhost:8081)
- URL Service: [http://localhost:8082](http://localhost:8082)
- Eureka Dashboard: [http://localhost:8761](http://localhost:8761)
- Frontend: [http://localhost:3000](http://localhost:3000)

---

> 📝 **Tip:** All config is managed via environment variables and `application.yml` files. See each service for details. 