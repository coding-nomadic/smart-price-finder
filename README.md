# Recipe-Sharing-Service

This is Backend for recipe server built in springboot, JWT Token and email service. UI is implemented in react JS

## Features in this application- 

- JWT token support for quick login.
- Regular Username/Password authentication.
- Stores user information in the PostgreSQL database.
- Email verification to confirm during user registration
- Create, view, update recipes, 
- comments, ratings and view ingredients, cuisines
- Stores API data in Cache to minimize network calls.
- Scheduled tasks send email every 14 days to the most liked recipe makers
- Analytics API covered to show case in UI.

## Microservices - 

- React-UI Service: Front-end client UI which displays data and makes API calls using Axios API.
- Authentication Service: Creates user account and handles username/password authentication, login/logout.
- Recipe Service: It allows to create recipes, view, update, comment, ratings etc.

## Tools and Technologies
- Java 17
- Spring Boot
- Spring Web MVC
- ThyLeaf
- Gemini LLM Model
- Prompt Engieering
- Docker
- Render
