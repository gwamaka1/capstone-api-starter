# EasyShop — E-Commerce API

## Description of the Project

EasyShop is the Spring Boot REST API backend for an online clothing store. It powers a JavaScript front-end that lets shoppers browse products by category, search and filter the catalog, manage a shopping cart, edit their profile, and check out. The API was delivered as a partially built, already-published "Version 1" — the work here was to find and fix existing bugs, complete unimplemented endpoints, and add several new features, all on the backend.

The application connects to a MySQL database using Spring Data JPA. Authentication is handled with JSON Web Tokens (JWT): users log in, receive a token, and send it on each request to access protected endpoints. Authorization is enforced by role, so only administrators can modify the catalog while reads stay public and the cart, profile, and checkout require a logged-in user.

This project demonstrates a layered Spring Boot architecture (controller, service, repository), JPA entity mapping, derived query methods, JWT-based security with method-level authorization (`@PreAuthorize`), bean validation, consistent REST error handling, and automated tests across each layer.
 
---

## User Stories

- As a developer, I want to run my store's SQL script and confirm the existing endpoints work so that I have a known-good starting point before changing code.
- As a shopper, I want the website to load data from the API so that I can browse the store in my browser.
- As a shopper, I want to view all categories and a single category by id so that I can navigate the store.
- As a shopper, I want to list the products in a category so that I can find items I'm interested in.
- As an admin, I want to create, update, and delete categories so that I can keep the catalog organized.
- As a shopper, I want search and filters to return every matching product so that I can reliably find what I'm looking for.
- As an admin, I want all fields to save when I edit a product, including stock, so that my changes actually persist.
- As an API client, I want a 404 for missing resources and a 400 for invalid input so that I know what went wrong.
- As a developer, I want unit tests covering the search filter and the product update so that the two bugs stay fixed.
- As a developer, I want the provided Insomnia collections passing so that I can verify my endpoints quickly.
- As an admin, I want only admins to add, edit, and delete categories and products while reads stay public and the cart, profile, and orders require a logged-in user, so that the store is secure.
- As a developer, I want interactive Swagger docs so that I can test secured endpoints with my token.
- As a logged-in shopper, I want to add, view, update, and clear items in my cart so that my cart persists across logins.
- As a logged-in user, I want to view and update my profile so that my account info stays current.
- As a logged-in shopper, I want to turn my cart into an order so that I can complete a purchase.
---

## Setup

### Prerequisites

- IntelliJ IDEA installed
- Java 17 SDK installed and configured
- MySQL server running locally
- MySQL Workbench (to run the database script)
### Configuring the Database

Open the clothing-store `.sql` script from the project's `database` folder in MySQL Workbench and execute it. This creates the database, tables, sample products, and three demo users (`user`, `admin`, `george` — password `password` for all three).

Then open `src/main/resources/application.properties` and fill in your MySQL connection details:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Running the Application in IntelliJ IDEA

1. Open **IntelliJ IDEA**.
2. Select **Open** and navigate to the project root (the `capstone-api-starter` folder, where `pom.xml` lives).
3. Wait for IntelliJ to index the files and import the Maven dependencies.
4. Click **Reload Maven Project** if prompted after the `pom.xml` is opened.
5. Locate the main application class in the `org.yearup` package.
6. Right-click it and select **Run**.
7. The API starts on `http://localhost:8080`. Use Insomnia (import the provided collections) or Swagger UI (`http://localhost:8080/swagger-ui/index.html`) to log in and test the endpoints.
---

## Technologies Used

- **Java 17**
- **Spring Boot 4** — application framework and auto-configuration
- **Spring Security 7** — authentication and method-level authorization (`@PreAuthorize`)
- **JSON Web Tokens (JWT)** — stateless authentication
- **Spring Data JPA** — repository layer and derived queries
- **Hibernate** — ORM / database interaction
- **MySQL** — relational database
- **JUnit 5 + Mockito** — automated tests (service, controller, repository)
- **springdoc-openapi / Swagger UI** — interactive API documentation
- **Maven** — build and dependency management
---

## An Interesting Piece of Code

The most instructive fix was **Bug 1**, the product search. Some products never appeared in results. The cause was a single unconditional line in the service's `search` method:

```java
.filter(Product::isFeatured)   // silently dropped every non-featured product
```

With no guard, that filter ran on every search and removed any product whose `featured` flag was false. Removing it left the catalog filtering to a set of guarded, combinable filters:

```java
return products.stream()
        .filter(p -> categoryId  == null || categoryId.equals(p.getCategoryId()))
        .filter(p -> minPrice    == null || p.getPrice() >= minPrice)
        .filter(p -> maxPrice    == null || p.getPrice() <= maxPrice)
        .filter(p -> subCategory == null || subCategory.equalsIgnoreCase(p.getSubCategory()))
        .toList();
```

The `param == null || matches` pattern is what makes it work: when a query parameter is absent, its condition short-circuits to `true`, so that filter doesn't apply. Every combination of `cat`, `minPrice`, `maxPrice`, and `subCategory` stacks correctly, including the "no filters" case that now returns the full catalog. A Mockito unit test feeds in one featured and one non-featured product, calls `search` with no filters, and asserts both come back — turning red the moment the `isFeatured` line is reintroduced.
 
---

## Demo
<img alt="compressed-ezgif.com-video-to-gif-converter.gif" src="C:\Users\mwamt\Downloads\compressed-ezgif.com-video-to-gif-converter.gif"/>[compressed-ezgif.com-video-to-gif-converter.gif](../../../compressed-ezgif.com-video-to-gif-converter.gif)

## Future Work

- Implement and harden the checkout flow (`POST /orders`) so a cart converts to an order in a single transaction.
- Add fully custom JSON error responses via a global `@RestControllerAdvice`, including a clean 403 for authorization failures.
- Expand validation rules across all models with custom messages.
- Add controller and integration tests for the cart, profile, and order endpoints.
- Add pagination and sorting to the product list for larger catalogs.
- Customize the front-end styling and add new products to the store.
---

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- IntelliJ IDEA
- Class notes and project instructions
---

## Team Members

- Gwamaka Mwamtobe
---

## Thanks

Thank you to **Raymond** for continuous support and guidance throughout this project.