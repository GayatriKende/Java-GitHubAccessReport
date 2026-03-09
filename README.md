# Java GitHub Access Report

## Overview

The **Java GitHub Access Report** is a Spring Boot application that generates a report showing **which users have access to which repositories** for a given GitHub user or organization.

The application retrieves repository and collaborator information using the **GitHub REST API**, processes the data efficiently using **parallel API calls**, and provides the results through API endpoints or as a downloadable CSV report.



# Technologies Used

* Java 17
* Spring Boot 3
* Maven
* GitHub REST API



# 1. How to Run the Project

## Prerequisites

Make sure the following are installed:

* Java 17 or later
* Maven
* Git



## Step 1: Clone the Repository

```
git clone https://github.com/GayatriKende/Java-GitHubAccessReport.git
cd Java-GitHubAccessReport
```

---

## Step 2: Configure GitHub Authentication

Create the file:

```
src/main/resources/application.properties
```

Add the following configuration:

```
github.token=YOUR_GITHUB_TOKEN
github.org=YOUR_GITHUB_USERNAME_OR_ORG
cache.expiry=300000
```

Example:

```
github.token=ghp_xxxxxxxxxxxxxxxxx
github.org=GayatriKende
cache.expiry=300000
```

---

## Step 3: Build the Project

```
mvn clean install
```

---

## Step 4: Run the Application

```
mvn spring-boot:run
```

The application will start at:

```
http://localhost:8080
```

---

# 2. How Authentication is Configured

The application uses a **GitHub Personal Access Token (PAT)** for authentication.

The token is stored in the `application.properties` file and used in every GitHub API request.

Example configuration:

```
github.token=YOUR_GITHUB_TOKEN
```

The token is attached to API requests using the HTTP Authorization header:

```
Authorization: Bearer <token>
```

Example in code:

```
conn.setRequestProperty("Authorization", "Bearer " + token);
```

This allows the application to securely access GitHub repositories and collaborator information.

---

# 3. How to Call API Endpoints

## View Access Report (JSON)

This endpoint returns the access report in JSON format.

Endpoint:

```
GET /access-report
```

Example:

```
http://localhost:8080/access-report
```

Example Response:

```
[
 {
  "username": "user1",
  "repositories": ["repo1", "repo2"]
 }
]
```

---

## Download Access Report (CSV)

This endpoint downloads the report as a CSV file.

Endpoint:

```
GET /access-report/download
```

Example:

```
http://localhost:8080/access-report/download
```

Downloaded file:

```
github_access_report.csv
```

Example CSV output:

```
User,Repositories
user1,repo1|repo2
user2,repo3|repo4
```

---

# 4. Assumptions and Design Decisions

### Parallel API Calls

Fetching collaborators for multiple repositories can take time. To improve performance, the application uses **CompletableFuture with a fixed thread pool** to fetch repository collaborator data in parallel.

### In-Memory Caching

A simple in-memory caching mechanism is used to store the generated report temporarily. This reduces repeated GitHub API calls and improves performance.

### External Configuration

GitHub credentials and organization details are stored in the `application.properties` file to avoid hardcoding sensitive information in the source code.

### Lightweight JSON Parsing

The project uses lightweight string-based parsing to extract required fields from the GitHub API response without introducing additional dependencies.



