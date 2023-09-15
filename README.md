# Bank

A bank application.

## Formatting
```
mvn formatter:format
```

### Run Tests

#### Unit Tests

```
mvn clean test
```

#### Integration Tests

Integration tests require a PostgreSQL database to be available, thus you need to spin up a Docker container with a running database.
To run the integration tests execute the following commands:

```
docker-compose up
mvn clean test-compile failsafe:integration-test
```

You can run both unit and integration tests as follows:

```
mvn clean verify
```
