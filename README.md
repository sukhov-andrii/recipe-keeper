# Recipe Keeper


![Recipe Keeper logo](docs/images/2303_q893_013_s_m009_c10_cook_home_flat_text.jpg)
---

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Tests](https://img.shields.io/badge/tests-junit5-green)

## 🎯About
Recipe Keeper is a Spring Boot backend application that lets you import recipes from TheMealDB, store them locally, and enrich them with ratings, cooked status, ingredients, and downloaded images.

Each recipe is persisted in a local database and linked with normalized ingredients and related metadata.


---

## 📋 Table of Contents
- [About](#about)
- [Features](#-features)
- [Requirements](#-requirements)
- [Setup](#-setup)
- [Run Locally](#-run-locally)
- [API Reference](#-api-reference)
- [Usage Examples](#-usage-examples)
- [Testing](#-testing)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [Credits](#-credits)
- [License](#-license)

---

## 📦 Features
* Import recipes from TheMealDB
* Store recipes with ingredients, instructions, category, and area
* Rate recipes (1–5)
* Mark recipes as cooked
* Download and store:
  * main recipe image
  * ingredient thumbnails
* Paginated recipe listing
* Search by title and ingredients
* Full recipe retrieval
* RESTful CRUD API
---


## 🧩 Requirements
* Java 21 or higher
* Spring Boot 3.x
* Maven
* H2 Database (embedded)
* TheMealDB API (free test key supported)
---

## ⚙️ Setup

### Configure API (optional):
TheMealDB works with a free test key (development and educational purposes).
TheMealDB API can be requested from [MealDB website](https://www.themealdb.com/).
```
export THEMEALDB_API_KEY=1   # Linux/macOS
setx THEMEALDB_API_KEY 1     # Windows PowerShell
```

---

## 🚀 Run Locally

### 1. Clone repository:
```
git clone <repo>
cd <repo>
```

### 2. Build:

```
# Maven
./mvnw clean install

# Gradle
./gradlew build
```

### 3. Start the application:

```
# Maven
./mvnw clean install

# Gradle
./gradlew build
```

Application runs at:
```
http://localhost:8080.
```

---

## 📡 API Reference
Swagger UI is available at:
```
http://localhost:8080/swagger-ui/index.html
```
Use it to explore and test endpoints interactively.

### Recipes
| Method | Path | Description |
|--------|------|-------------|
| POST   | /recipes/import?name={name} | Import a recipe from TheMealDB |
| GET    | /recipes?page={page}&size={size} | Get paginated list of recipes |
| GET    | /recipes/{id} | Retrieve full recipe details |
| DELETE | /recipes/{id} | Delete a recipe and associated data |

---

### Actions
| Method | Path | Description |
|--------|------|-------------|
| PATCH  | /recipes/{id}/cooked | Mark recipe as cooked / uncooked |
| PATCH  | /recipes/{id}/rating | Update recipe rating (1–5) |

---

### Search
| Method | Path | Description |
|--------|------|-------------|
| GET    | /recipes/search?query={query} | Search recipes by title and ingredients |


## 📌 Usage examples

You can interact with the API using any HTTP client (e.g. cURL, Postman). Below are some examples (curl):

#### Import recipe
```
curl -X POST "http://localhost:8080/recipes/import?name=Arrabiata"
```

#### List Recipes

Get recipes (paginated)
```
curl "http://localhost:8080/recipes?page=0&size=10"
```

#### Update cooked status
```
curl -X PATCH "http://localhost:8080/recipes/{id}/cooked" \
-d '{"cooked": true}'
```

---
## 🧪 Testing

- Unit tests with JUnit
- Mockito for external dependencies (TheMealDB)
- Coverage for:
   - pagination
   - validation
   - service logic

Running tests locally:

```
# Maven
./mvnw test

# Gradle
./gradlew test
```

To generate test coverage (JaCoCo):
```
./mvnw clean test
./mvnw jacoco:report
```

Open:
```
target/site/jacoco/index.html
```
---


## 🧱 Project Structure

## Architecture
Layered Spring Boot architecture:
- Spring Boot layered architecture
- Controller → Service → Repository
- DTO-based API layer
- H2 database persistence
- local image storage
---

### Project Structure
```
src/main/java/com/inholland/oop3/recipekeeper
├── controller      # REST endpoints
├── service         # Business logic 
├── model           # JPA entities
├── repository      # JPA repositories
├── dto             # API layer DTOs (Request/response objects)
├── mapper          # DTO <-> Entity conversions
└── exception       # Custom exceptions & handlers
```
### Data Storage:
* Recipes are stored in an embedded H2 database
* Ingredients are normalized into separate entities
* Images are stored locally under:
```
src/main/resources/static/images/recipes/
```

## 🤝 Contributing
Contributions are welcome. Please fork the repository and submit a pull request. For major changes, open an issue first to discuss the design.

## 🧾 Credits

* Logo image by macrovector_official via [Magnific](https://www.magnific.com/free-vector/cook-home-flat-composition-text-icons-fried-eggs-salad-female-character-near-stove-vector-illustration_70561823.htm#fromView=keyword&page=1&position=10&uuid=75327c74-0e7d-49f2-9798-27ca6dc9719d&query=Recipe+book+logo)

## ⚖️ License
This project is developed as part of the OOP3 course at Hogeschool InHolland. No commercial use permitted.

