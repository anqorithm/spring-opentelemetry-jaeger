.PHONY: help build run test clean docker-build docker-up docker-down docker-restart docker-logs jaeger setup install

help:
	@echo "Available commands:"
	@echo "  make install        - Install dependencies"
	@echo "  make build          - Build the application"
	@echo "  make run            - Run the application locally"
	@echo "  make test           - Run tests"
	@echo "  make clean          - Clean build artifacts"
	@echo "  make docker-build   - Build Docker images"
	@echo "  make docker-up      - Start all services in Docker"
	@echo "  make docker-down    - Stop all Docker services"
	@echo "  make docker-restart - Restart all Docker services"
	@echo "  make docker-logs    - Show Docker logs"
	@echo "  make jaeger         - Open Jaeger UI in browser"
	@echo "  make setup          - Complete setup (install, build, docker-up)"

install:
	mvn clean install -DskipTests

build:
	mvn clean package -DskipTests

run:
	mvn spring-boot:run

test:
	mvn test

clean:
	mvn clean
	docker compose down -v
	docker system prune -f

docker-build:
	docker compose build --no-cache

docker-up:
	docker compose up -d

docker-down:
	docker compose down -v

docker-restart: docker-down docker-build docker-up docker-logs

docker-logs:
	docker compose logs -f spring-app

jaeger:
	@echo "Opening Jaeger UI..."
	@open http://localhost:16686 || xdg-open http://localhost:16686 || echo "Visit http://localhost:16686"

setup: install docker-build docker-up
	@echo "Setup complete! Services are running."
	@echo "Spring Boot API: http://localhost:8080"
	@echo "Jaeger UI: http://localhost:16686"