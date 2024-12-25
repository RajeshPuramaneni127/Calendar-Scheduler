# Calendar Scheduler Backend Application

## Overview
This is a backend application for a **Calendar Scheduler** that enables efficient user and event management. Developed using **Java**, **Spring Boot**, and **PostgreSQL**, this application provides functionalities such as adding users, managing busy time slots, detecting scheduling conflicts, and suggesting available time slots for events. It also supports recurring events, advanced search functionality and events with multiple users. 

---

## Features

### User Management
- **Add Users**: Create new users with unique identifiers and basic details (e.g., name, email).
- **Fetch User Details**: Retrieve user information using their unique identifier.
- **Update User Details**: Edit user information.
- **Delete Users**: Remove users from the system.
- **Search Users**: Search for users by their name.

### Event Management
- **Create Events**: Add busy time slots for one or multiple users, with start and end times.
- **Retrieve Events**: Get all events for a specific user on a particular date.
- **Conflict Detection**: Identify overlapping or conflicting events for a user.
- **Suggest Time Slots**: Suggest the next available time slot for a group of users, given a specified duration.
- **Recurring Events**: Create events that repeat on a daily, weekly, or monthly basis. Efficiently handled recurring events without saving them as additional event in database.
- **Advanced Search**: Search events by keyword or participant name.

### Additional Features
- **Validation**: Validation for time ranges, user IDs, emails, dates.
- **Error Handling**: Meaningful error messages for invalid inputs or other issues.
- **Optimized Queries**: Efficient database queries for large data sets.

---

## Technologies Used
- **Language**: Java
- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **Dependencies**:
  - Spring Data JPA
  - Spring Web
  - Lombok
  - Validation libraries (for input validation)

---

## Endpoints

### User Controller
| Method   | Endpoint                  | Description                     |
|----------|---------------------------|---------------------------------|
| `POST`   | `/api/users`              | Create a new user               |
| `GET`    | `/api/users`              | Retrieve all users              |
| `GET`    | `/api/users/{id}`         | Get user details by ID          |
| `PUT`    | `/api/users/{id}`         | Update user details             |
| `DELETE` | `/api/users/{id}`         | Delete a user                   |
| `GET`    | `/api/users/search`       | Search users by name            |

<img width="1341" alt="Screenshot 2024-12-25 at 8 40 57 AM" src="https://github.com/user-attachments/assets/9d7c2170-8263-4c06-9b81-4b7854c1171b" />

<img width="1338" alt="Screenshot 2024-12-25 at 8 41 29 AM" src="https://github.com/user-attachments/assets/e0c37aec-c2ed-4682-ad27-6f438bd6fafc" />

<img width="1337" alt="Screenshot 2024-12-25 at 8 42 14 AM" src="https://github.com/user-attachments/assets/4c9cc7c5-04d6-4bc2-a676-046312cd9f41" />

<img width="1350" alt="Screenshot 2024-12-25 at 8 42 55 AM" src="https://github.com/user-attachments/assets/460a662f-58d0-43fb-9a66-3c71d1ef8d7f" />

<img width="1339" alt="Screenshot 2024-12-25 at 8 44 03 AM" src="https://github.com/user-attachments/assets/bdab608b-4373-4cd9-946b-9dc88ea1e6e7" />

<img width="1332" alt="Screenshot 2024-12-25 at 8 44 40 AM" src="https://github.com/user-attachments/assets/20027a88-dc9c-4883-9d2f-7e122ae22618" />



### Event Controller
| Method   | Endpoint                         | Description                                         |
|----------|-----------------------------------|-----------------------------------------------------|
| `POST`   | `/api/events`                    | Create a new event                                  |
| `GET`    | `/api/events/user/{userId}/date/{date}` | Retrieve all events for a user on a specific date including recurring events|
| `GET`    | `/api/events/conflicts/{userId}/date/{date}` | Detect conflicts for a user on a specific date including recurring events|
| `GET`    | `/api/events/suggest-time-slots`  | Suggest available time slots for a group of users on a specific date |
| `DELETE` | `/api/events/{eventId}`           | Delete an event                                    |
| `GET`    | `/api/events/search`             | Search events by keyword or participant            |

<img width="1331" alt="Screenshot 2024-12-25 at 8 50 28 AM" src="https://github.com/user-attachments/assets/c5a4f0b4-68a3-4f01-b63f-da802ac64a2e" />

<img width="1344" alt="Screenshot 2024-12-25 at 8 50 58 AM" src="https://github.com/user-attachments/assets/d5980985-a035-4543-9a45-5997d2f8047d" />

<img width="1343" alt="Screenshot 2024-12-25 at 8 51 41 AM" src="https://github.com/user-attachments/assets/812b4963-2cc7-4de5-a9e9-10baf582561f" />

<img width="1342" alt="Screenshot 2024-12-25 at 8 52 25 AM" src="https://github.com/user-attachments/assets/5f65f076-3d09-4912-be1c-73d2b7069faf" />

<img width="1345" alt="Screenshot 2024-12-25 at 8 53 00 AM" src="https://github.com/user-attachments/assets/96bd8ff5-5f02-4642-a78e-9c1b5d74218b" />


---


### Database Tables 

<img width="1470" alt="Screenshot 2024-12-25 at 8 53 35 AM" src="https://github.com/user-attachments/assets/aff281df-c33d-4cee-ae17-63ba5c4bae21" />

<img width="1470" alt="Screenshot 2024-12-25 at 8 53 43 AM" src="https://github.com/user-attachments/assets/42581b99-6c54-402e-abbe-7965dd0cabc9" />

<img width="1470" alt="Screenshot 2024-12-25 at 8 54 09 AM" src="https://github.com/user-attachments/assets/f3cfb9ed-36a1-47fa-8906-88797d8b41a9" />

## Setting Up the Project

### Prerequisites
- **Java** (Version 17)
- **Maven** (for dependency management)
- **PostgreSQL** (Version 12 or higher)
- **Lombok** (Version 1.18.30)

### Installation Steps
1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```bash
   cd calendar_scheduler
   ```

3. Configure the `application.properties` file for PostgreSQL:
   ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/calendar_scheduler
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
    server.port=8080
    server.error.include-message=ALWAYS
   ````

4. Build the project:
   ```bash
   mvn clean install
   ```

5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

6. Access the application at: `http://localhost:8080`

