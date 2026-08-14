# Contributing

## Prerequisites

- JDK 17
- Docker, only for `-PwithSmoke` and Docker image verification

Use the included Gradle Wrapper; do not add bundled Keycloak/Jackson/HTTP/logging dependencies,
credentials, real tokens, realm imports, databases, or deployment-specific configuration.

```bash
./gradlew clean build
./gradlew test -PwithSmoke
./gradlew prepareDockerProvider
docker build -t keycloak-discord:local .
```
