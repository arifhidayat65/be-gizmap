# BE-GIZMAP

Backend service for GIZMAP application built with Spring Boot.

## Features

- User Authentication & Authorization with JWT
- Product Management
- Blog Management
- Discount Management
- Promo Management
- Content Management
- Info Management

## Tech Stack

- Java 11
- Spring Boot
- Spring Security
- PostgreSQL
- Docker
- Maven

## Project Structure

```
src/main/java/com/apps/
├── controller/         # REST controllers
├── model/             # Entity classes
├── repository/        # Data access layer
├── service/          # Business logic
├── security/         # Security configuration
├── payload/          # Request/Response DTOs
└── BeGizmapApplication.java
```

## Getting Started

### Prerequisites

- Java 11
- Maven
- Docker & Docker Compose
- PostgreSQL (if running locally)

### Running with Docker

1. Build the application:
```bash
mvn clean package
```

2. Start the application and database:
```bash
docker-compose up -d
```

The application will be available at `http://localhost:8080`

### Running Locally

1. Configure database connection in `application.properties`

2. Build and run:
```bash
mvn spring-boot:run
```

## API Documentation

### Authentication Endpoints
- POST `/api/auth/signup` - Register new user
- POST `/api/auth/signin` - Login user

### Product Endpoints
- GET `/api/products` - Get all products
- POST `/api/products` - Create new product
- GET `/api/products/{id}` - Get product by ID
- PUT `/api/products/{id}` - Update product
- DELETE `/api/products/{id}` - Delete product

### Blog Endpoints
- GET `/api/blogs` - Get all blogs
- POST `/api/blogs` - Create new blog
- GET `/api/blogs/{id}` - Get blog by ID
- PUT `/api/blogs/{id}` - Update blog
- DELETE `/api/blogs/{id}` - Delete blog

### Content Endpoints
- GET `/api/kontens` - Get all content
- POST `/api/kontens` - Create new content
- GET `/api/kontens/{id}` - Get content by ID
- PUT `/api/kontens/{id}` - Update content
- DELETE `/api/kontens/{id}` - Delete content

### Info Endpoints
- GET `/api/infos` - Get all info
- POST `/api/infos` - Create new info
- GET `/api/infos/{id}` - Get info by ID
- PUT `/api/infos/{id}` - Update info
- DELETE `/api/infos/{id}` - Delete info

## Security

The application uses JWT (JSON Web Tokens) for authentication. Protected endpoints require a valid JWT token in the Authorization header:

```
Authorization: Bearer <token>
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
