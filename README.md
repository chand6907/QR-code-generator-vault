## 🌐 Live Demo → https://qr-code-generator-vault.onrender.com/dashboard
# QRVault — Java Spring Boot QR Code Generator

A full-stack Java web application that lets registered users generate, customize, and download QR codes for URLs, text, Wi-Fi credentials, and contact cards. Built with Spring Boot, Spring Security, JPA, and ZXing.

---

## ✨ Features

| Feature | Details |
|---|---|
| **Authentication** | Register / Sign in / Sign out with BCrypt-hashed passwords |
| **4 QR types** | URL, Plain Text, Wi-Fi, Contact (vCard) |
| **Customization** | Error correction (L/M/Q/H), output size, custom colors |
| **Generation history** | Every code saved to your account — view anytime |
| **Download** | Export any QR code as a high-resolution PNG |
| **Responsive UI** | Works on desktop and mobile |

---

## 🛠 Tech Stack

- **Backend**: Java 17, Spring Boot 3.2
- **Security**: Spring Security 6 (form login + BCrypt)
- **Database**: H2 in-memory (zero setup — swappable with MySQL/PostgreSQL)
- **ORM**: Spring Data JPA / Hibernate
- **Templating**: Thymeleaf
- **QR Library**: ZXing (Google) 3.5.2
- **Build**: Apache Maven

---

## 🚀 How to run

### Prerequisites
- Java 17+ installed  (`java -version`)
- Maven installed      (`mvn -version`)

### Steps

```bash
# 1. Clone or unzip this project
cd QRVault

# 2. Build the project
mvn clean package -DskipTests

# 3. Run
mvn spring-boot:run
```

Then open your browser at: **http://localhost:8080**

---

## 📁 Project structure

```
QRVault/
├── pom.xml
└── src/main/
    ├── java/com/qrvault/
    │   ├── QRVaultApplication.java         ← Entry point
    │   ├── config/
    │   │   └── SecurityConfig.java         ← Spring Security setup
    │   ├── controller/
    │   │   ├── AuthController.java         ← Login / Register
    │   │   └── DashboardController.java    ← QR generation + history
    │   ├── model/
    │   │   ├── User.java                   ← User entity
    │   │   └── QRHistory.java              ← QR generation record
    │   ├── repository/
    │   │   ├── UserRepository.java
    │   │   └── QRHistoryRepository.java
    │   └── service/
    │       ├── QRCodeService.java          ← ZXing QR generation
    │       ├── UserService.java            ← Registration logic
    │       └── CustomUserDetailsService.java
    └── resources/
        ├── application.properties
        ├── static/
        │   ├── css/style.css
        │   └── js/app.js
        └── templates/
            ├── auth/
            │   ├── login.html
            │   └── register.html
            └── dashboard/
                ├── index.html              ← Main generator UI
                └── history.html
```

---

## 🔐 Security notes

- Passwords are hashed with **BCrypt** (never stored in plain text)
- All routes require authentication except `/auth/**` and static assets
- CSRF protection is enabled on all POST endpoints
- H2 console is enabled at `/h2-console` for development inspection

---

## 🔄 Switching to MySQL (production)

Replace the H2 dependency in `pom.xml` with:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
```

And update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/qrvault
spring.datasource.username=your_user
spring.datasource.password=your_pass
spring.jpa.hibernate.ddl-auto=update
```

---

## 👤 Author

Built as a portfolio project demonstrating:
- Full-stack Java development
- Spring Boot architecture (Controller → Service → Repository)
- Spring Security with form-based authentication
- QR code generation using the ZXing library
- Clean, responsive UI design with Thymeleaf
