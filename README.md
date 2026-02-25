# ThePointsPup Hotel Pet Policy API

REST API serving pet policy data for 16 major hotel chains. Built with Spring Boot and designed to power the pet-friendly hotel finder on [ThePointsPup.com](https://thepointspup.com).

## Tech Stack

- Java 17 + Spring Boot 3.2
- SpringDoc OpenAPI (Swagger UI)
- Maven
- No database — in-memory data store

## Run Locally

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

## API Endpoints

### Hotels

```bash
# List all hotel chains
curl http://localhost:8080/api/hotels

# Filter by minimum dog weight (lbs)
curl http://localhost:8080/api/hotels?minWeight=70

# Filter by maximum pet fee ($)
curl http://localhost:8080/api/hotels?maxFee=0

# Filter by rating (excellent, good, moderate)
curl http://localhost:8080/api/hotels?rating=excellent

# Search by name or notes
curl http://localhost:8080/api/hotels?search=kimpton

# Combine filters
curl "http://localhost:8080/api/hotels?minWeight=50&rating=good"

# Get a single chain's policy
curl http://localhost:8080/api/hotels/kimpton
```

### Stats

```bash
curl http://localhost:8080/api/stats
```

## Swagger Docs

Interactive API docs available at:

```
http://localhost:8080/swagger-ui.html
```

## Hotel Chains Included

Kimpton, Hilton, Marriott, IHG, Hyatt, Wyndham/La Quinta, Best Western, Choice Hotels, Motel 6/Studio 6, Red Roof Inn, Drury Hotels, Loews Hotels, Extended Stay America, Sonesta, Omni Hotels, Four Seasons

## Build

```bash
mvn package
```

Creates a deployable JAR at `target/pet-policy-api-1.0.0.jar`.

## Links

- [ThePointsPup](https://thepointspup.com) — Pet travel tips, hotel reviews, and points strategies
- [API Docs (Swagger)](http://localhost:8080/swagger-ui.html)
