run:
	mvn spring-boot:run

install:
	mvn clean install

test:
	./mvnw test -Dtest=com.bicosteve.api_gateway.controllers.HealthCheckTest

tests:
	./mvnw test


