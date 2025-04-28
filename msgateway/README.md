
# 📦 Project: Msgateway Service

This is a Spring Boot API packaged and run inside Docker.

---

## 🚀 Prerequisites

Make sure you have installed:

- **Docker** (for containerization)
- **(Optional) Maven** (if you’re building manually)

---

## 🛠 Building the Project

If you already have a multi-stage Dockerfile that builds Maven inside Docker (like the one we discussed earlier), you can skip Maven locally.

Otherwise, build manually:

### Build the Spring Boot App Locally

Run the following Maven command to build the Spring Boot application:

```bash
    mvn clean package -DskipTests
```


### 🐳 Build Docker Image
From the root project directory (where the Dockerfile is located):

Build the Docker Image
Run the following command to build the Docker image:

```bash
   docker build -t msgateway-service .
```
This will build an image named msgateway-service that can be used to run the application.

### 🚀 Run Docker Container
After building the image, run the container:

```bash
  docker run -p 8080:8080 msgateway-service
```
This maps container port 8080 to your local machine's port 8080.

The API will be available at: http://localhost:8080

### 🛑 Stopping the Container
To stop the container:

Find the Running Container
```bash
docker ps
```
This will show the list of running containers. Find the container ID or name.

Stop the Container
```bash
  docker stop <container_id>
```
Or if you run it with a custom name like --name msgateway, you can use:

```bash
  docker stop msgateway
```

### ✨ Bonus: Using Docker Compose (Optional)
If you have a docker-compose.yml file, you can use Docker Compose to manage multiple containers:

Build and Start the Service
```bash
  docker-compose up --build
```
Stop the Service
```bash
  docker-compose down
```
### 📎 Example Commands
Here’s a summary of useful commands for building and running the service:

Build JAR Locally
```bash
  mvn clean package -DskipTests
```
Build Docker Image
```bash
  docker build -t msgateway-service .
```
Run the Service
```bash
  docker run -p 8080:8080 msgateway-service
```
### ✅ Summary

| Step        | Command                           |
| ----------- | --------------------------------- |
| Build JAR   | `mvn clean package -DskipTests`   |
| Build Image | `docker build -t msgateway-service .` |
| Run Service | `docker run -p 8080:8080 msgateway-service` |


### 📬 Notes
You can customize the Dockerfile to automatically copy configuration files if needed.

Remember to expose only the necessary ports and environment variables for production.

### 📚 References
[Spring Boot Documentation](https://spring.io/projects/spring-boot)

[Docker Documentation](https://docs.docker.com/)