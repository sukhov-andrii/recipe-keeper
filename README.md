# Recipe Keeper


![Recipe Keeper logo](docs/images/2303_q893_013_s_m009_c10_cook_home_flat_text.jpg)
---

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Tests](https://img.shields.io/badge/tests-junit5-green)

## 🎯About
Recipe Keeper is a backend application for managing a **personal recipe collection**.

It integrates with **TheMealDB API** as its external data source, importing recipes and enriching them with local metadata such as ratings, cooked status, related recipes, and persisted images.

---

## 📋 Table of Contents
- [About](#about)
- [Features](#-features)
- [Requirements](#-requirements)
- [Setup](#-setup)
- [Run](#-run-the-application)
- [API Overview](#-api-overview)
- [Usage Examples](#-usage-examples)
- [Testing](#-testing)
- [External Services](#external-services)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [Credits](#-credits)
- [License](#-license)

---

## 📦 Features
- Import recipes from TheMealDB
- Normalize and store ingredients and measures
- Retrieve full recipe details by ID
- Paginated recipe listing
- Search recipes by title or ingredients
- Rate recipes (1–5 scale)
- Mark recipes as cooked
- Store related recipes based on category/area filters
- Download and persist images locally:
  - main recipe image
  - two ingredient images
- Concurrent processing for imports and image downloads
---


## 🧩 Requirements
* Java 21 or higher
* Spring Boot 3.x
* Maven
* H2 Database (embedded)
* TheMealDB API key (free test key supported)
---

## ⚙️ Setup

### TheMealDB API configuration

This project uses the public test key for development purposes:
```
export THEMEALDB_API_KEY=1   # Linux/macOS
setx THEMEALDB_API_KEY 1     # Windows PowerShell
```
Documentation: https://www.themealdb.com/api.php

---

## 🚀 Run the Application

### 1. Clone repository:
```
git clone <repo-url>
cd <repo>
```

### 2. Build:

```
# Maven
./mvnw clean install
```

### 3. Start:

```
# Maven
./mvnw clean install
```

Application runs at:
```
http://localhost:8080
```

Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```

---

## 📡 API Overview


### Core endpoints
| Method | Path | Description |
|--------|------|-------------|
| POST   | /recipes/import?name={name} | Import a recipe from TheMealDB |
| GET    | /recipes?page={page}&size={size} | Get paginated list of recipes |
| GET    | /recipes/{id} | Retrieve full recipe details |
| GET    | /recipes/search?query={query} | Search recipes by title or ingredients |


---

### Updates
| Method | Path | Description                |
|--------|------|----------------------------|
| PATCH  | /recipes/{id}/cooked | Update cooked status       |
| PATCH  | /recipes/{id}/rating | Update recipe rating (1–5) |
| DELETE | /recipes/{id} | Delete a recipe |
---



## 📌 Usage examples

You can interact with the API using any HTTP client (e.g. cURL, Postman). Below are some examples (curl):

### Import recipe
```
curl -X POST "http://localhost:8080/recipes/import?name=Arrabiata"
```

### List Recipes

Get recipes (paginated)
```
curl "http://localhost:8080/recipes
```

### Get recipe details
```
curl "http://localhost:8080/recipes/{id}"
```

### Search recipes

Searches locally stored recipes by title or ingredient match.
```
curl "http://localhost:8080/recipes/search?query=chicken"
```
### Update cooked status
```
curl -X PATCH "http://localhost:8080/recipes/{id}/cooked" \
-d '{"cooked": true}'
```

---
## 🧪 Testing

### Run tests

```
# Maven
./mvnw test
```

### Generate coverage report (JaCoCo)
```
./mvnw clean test
./mvnw jacoco:report
```

Report:
```
target/site/jacoco/index.html
```
---

## External Services
### TheMealDB API

Recipe Keeper relies on TheMealDB as its external recipe provider.

It is used for:

Recipe search and lookup
Metadata retrieval (category, area, ingredients)
Related recipe discovery via filters
Image sourcing

Documentation: https://www.themealdb.com/api.php

## 🧱 Project Structure


### Project Structure
```
src/main/java/nl/inholland/recipekeeper
├── controller
├── service
├── model
│   ├── entity
│   └── dto
├── repository
├── mapper
├── exception
└── client
```
### Data Storage:
* Recipes are stored in an embedded H2 database
* Ingredients are normalized into separate entities
* Images are stored locally under:
```
data/images/{recipe-id}/
```

## 🤝 Contributing
Contributions are welcome. Please fork the repository and submit a pull request. For major changes, open an issue first to discuss the design.

## 🧾 Credits
Logo asset:
* Logo image by macrovector_official via [Magnific](https://www.magnific.com/free-vector/cook-home-flat-composition-text-icons-fried-eggs-salad-female-character-near-stove-vector-illustration_70561823.htm#fromView=keyword&page=1&position=10&uuid=75327c74-0e7d-49f2-9798-27ca6dc9719d&query=Recipe+book+logo)

## ⚖️ License
This project is developed as part of the OOP3 course at Hogeschool InHolland. No commercial use permitted.
