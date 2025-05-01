
# 📦 Project: Bank Microservices

This is a Spring Boot API packaged and run inside Docker with Docker Compose for service orchestration.

---

## 🚀 Prerequisites

Make sure you have installed:

- **Docker** (for containerization)
- **Docker Compose** (for orchestrating multi-container environments)
- **OpenSSL** (for password encryption)

---

## 🛠 Clone the Project and Build

To get started with the project, follow these steps:

### 1. **Clone the Repository**

Clone the project from GitHub:

```bash
git clone https://github.com/Martin-Ochieng/dtb-interview.git
cd dtb-interview
```

### 2. **Run Docker Containers with Docker Compose**

In the root project directory, where the `docker-compose.yml` file is located, run the following command to build and start the services:

```bash
docker-compose up --build
```

This command will:

- Build the necessary Docker images for all services.
- Start all services (Gateway and microservices) inside containers.
- Expose the API on port 8080.

After running the command, the API will be available at: http://localhost:8080

---

### 🛑 Stopping the Containers

To stop the services, you can run:

```bash
docker-compose down
```

This will stop and remove all running containers defined in the `docker-compose.yml` file.

---

### 🔐 Password Encryption Process

To encrypt sensitive information like passwords, follow the steps below:

1. **Navigate to the `/keys` directory**:  
   Make sure you are inside the `/keys` folder before running the commands.

2. **Create a password file**:  
   Store your password in a file named `password.txt`:

   ```bash
    echo -n "MyP@ssw0rd123" > password.txt
   ```

3. **Encrypt the password using RSA**:  
   Use OpenSSL to encrypt the password with a public key:

   ```bash
   openssl rsautl -encrypt -pubin -inkey public_key.pem -in password.txt -out encrypted_password.bin
   ```

4. **Base64 encode the encrypted password**:  
   Convert the encrypted password to Base64 for easier handling in your applications:

   ```bash
   base64 encrypted_password.bin > encrypted_password.b64
   ```

The generated `encrypted_password.b64` file contains the encrypted password, which should be included in requests to the APIs for authentication.

---

### 🛠 Using Encrypted Password in API Requests

Once you have the encrypted password, you can use it in the API requests. Below is an example request body where the encrypted password is included as part of a user registration request:

```json
{
    "refId": "{{$guid}}",
    "username": "janedoe",
    "email": "janedoe@example.com",
    "password": "FvTcx8OTGgucqLCVKu8iZsjFfsM4IsjKkUdHA0qROQgnfHj6u9R8HCFu2Fkry0xgLwARCA8pZFeMzfd2oJ5S9o4Pn1c2Cd3KEViBJlFWW292vFC/0BlvpxkHcbQ0xQ3hc11RO7iPmwPvjWLwORQlWvmnP1xfaHFhjduG1DrCLOuWmiCbDDFj7n5yGOkJzxEUGuK66VeLvUyyc9tQvoA8eWdIJUQDyWVAB+N7LAIIyMFw+23LaYk8T51nkx6StHprIpRiuIVTvmy/8iw6/IUsCI5wS8oN3elaDnMrjmydRHbsJcFG7/D+3pkJ857LpbWHdzdV6n8uEjDk0tCSadozrg==",
    "firstName": "Jane",
    "lastName": "Doe",
    "role": "CUSTOMER"
}
```

In this example, the `password` field contains the encrypted password that was generated earlier.

---

### 📜 API Documentation via Swagger UI

Each microservice exposes its API specification via Swagger UI. Once the services are running, you can access the Swagger UIs for each service at the following URLs:

These will be unavailable in the production environment, but you can access them locally for testing purposes.

| Service             | Swagger UI URL                                        |
|---------------------|-------------------------------------------------------|
| **Gateway**         | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| **Profile Service** | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) |
| **Account Service** | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) |
| **Payment Service** | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) |
| **Events Service**  | [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html) |

These Swagger UI pages allow you to explore and interact with the APIs for each service.


---

### 📜 API Documentation via Postman (Gateway)

Each Microservice is also accessible via the Gateway service. You can use Postman to test the APIs through the Gateway.

These will be available in the production environment, but you can access them locally for testing purposes.

Please import the collection into Postman to test the APIs.

```bash
https://winter-resonance-137063.postman.co/workspace/DTB~88860ac6-6b26-4ab7-8439-2e30b715e694/collection/5790632-f990ba72-52e1-4397-99b7-c120dc0a73a7?action=share&creator=5790632
```
---



### 📎 Example Commands

Here’s a summary of useful commands for building and running the service:

Build and Start the Services with Docker Compose:

```bash
docker-compose up --build
```

Stop the Services:

```bash
docker-compose down
```

---

### ✅ Summary

| Step                  | Command                           |
| --------------------- | --------------------------------- |
| Clone the Repository   | `git clone https://github.com/Martin-Ochieng/dtb-interview.git` |
| Build and Run Services | `docker-compose up --build`      |
| Stop Services          | `docker-compose down`            |

---

### 📬 Notes

You can customize the Docker Compose file to automatically copy configuration files if needed.

Remember to expose only the necessary ports and environment variables for production.

---

### 📚 References
[Spring Boot Documentation](https://spring.io/projects/spring-boot)

[Docker Documentation](https://docs.docker.com/)

[Docker Compose Documentation](https://docs.docker.com/compose/)

