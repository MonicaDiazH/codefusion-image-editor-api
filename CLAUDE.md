# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CodeFusion Image Editor API — a Spring Boot 4.0.2 REST API for image editing. Currently a skeleton project with no business logic implemented yet.

## Tech Stack

- **Java 21** with **Spring Boot 4.0.2**
- **Spring MVC** (`spring-boot-starter-webmvc`) for REST endpoints
- **Lombok** for boilerplate reduction
- **Maven** build system (wrapper included: `./mvnw`)

## Common Commands

```bash
# Build
./mvnw clean install

# Run the application (default port 8080)
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=CodefusionImageEditorApplicationTests

# Run a single test method
./mvnw test -Dtest=CodefusionImageEditorApplicationTests#contextLoads

# Build without tests
./mvnw clean package -DskipTests
```

## Architecture

- **Base package**: `com.codefusion.image.editor`
- **Entry point**: `CodefusionImageEditorApplication.java`
- **Configuration**: `src/main/resources/application.yaml`

This is part of the **CODEFUSION workspace** alongside `codefusion-dte-transmission-api` and `lib-codefusion-dte-signer-api`. The workspace-level `CLAUDE.md` at `/Users/monicadiaz/Documents/PROJECTS/CODEFUSION/CLAUDE.md` covers the sibling projects.
