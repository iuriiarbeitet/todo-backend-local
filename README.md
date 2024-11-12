# To-Do List Projekt
https://todo-angular-frontend-176e6f717982.herokuapp.com/

Dies ist ein To-Do List Projekt, das mit **Spring Boot** für das Backend und **Angular** für das Frontend entwickelt wurde. Die Anwendung ermöglicht es Benutzern, Aufgaben zu erstellen, anzuzeigen, zu bearbeiten und zu löschen. Die Anwendung verwendet **JWT** für die Authentifizierung und **PostgreSQL** für die Datenspeicherung.

## Inhaltsverzeichnis

- [Beschreibung](#beschreibung)
- [Funktionen](#funktionen)
- [Verwendete Technologien](#verwendete-technologien)
- [Voraussetzungen](#voraussetzungen)
- [Installation](#installation)
  - [Backend](#backend)
  - [Frontend](#frontend)
- [Konfiguration](#konfiguration)
  - [Backend](#backend-konfiguration)
  - [Frontend](#frontend-konfiguration)
- [Nutzung](#nutzung)
- [Projektstruktur](#projektstruktur)


## Beschreibung

Diese Anwendung ist eine einfache Plattform zur Verwaltung von Aufgaben (To-Do List). Die Anwendung ermöglicht es Benutzern, Aufgaben zu erstellen, zu bearbeiten und zu löschen. Es bietet auch Benutzeranmeldung und -autorisierung mit **JSON Web Tokens (JWT)**. Das Backend ist mit **Spring Boot** erstellt, und das Frontend wurde mit **Angular** entwickelt.

## Funktionen

- **Benutzeranmeldung und -autorisierung** mit JWT.
- **CRUD (Erstellen, Lesen, Aktualisieren, Löschen)** für Aufgaben.
- **Aufgaben speichern** in einer PostgreSQL-Datenbank.
- **Benutzerfreundliche Oberfläche** erstellt mit Angular.

## Verwendete Technologien

- **Backend**:
  - Java 17+
  - Spring Boot 2.x
  - Spring Security für JWT-Authentifizierung
  - JPA für die Interaktion mit PostgreSQL
  - PostgreSQL für die Datenspeicherung
  - Spring Boot Test, jUnit, Mockito

- **Frontend**:
  - **Angular 11.x**
  - **Node.js 14.x** und **npm 6.x**
  - **Bootstrap** für Styling
  - **ng-sidebar** für Seitenmenüs
  - **ngx-translate** für die mehrsprachige Unterstützung
  - **express** für Backend-Dienste während der Entwicklung

## Voraussetzungen

- **Java** 17+
- **Node.js** 14.x
- **npm** 6.x
- **PostgreSQL** für die Datenbank
- **Gradle** für das Backend

## Installation

### Backend

1. Klone das Repository:

    ```bash
    git clone https://github.com/username/todo-backend.git
    cd todo-backend
    ```

2. Installiere die Backend-Abhängigkeiten mit Gradle:

    ```bash
    ./gradlew build
    ```

3. Konfiguriere die PostgreSQL-Datenbank:
    - Erstelle eine Datenbank (z. B. `todo_db`).
    - Füge in der Datei `src/main/resources/application.properties` folgende Konfiguration hinzu:

    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/todo_db
    spring.datasource.username=postgres
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    spring.datasource.driver-class-name=org.postgresql.Driver
    ```

4. Starte die Backend-Anwendung:

    ```bash
    ./gradlew bootRun
    ```

   Die Anwendung wird auf Port **8080** ausgeführt.

### Frontend

1. Klone das Repository für das Frontend:

    ```bash
    git clone https://github.com/username/todo-frontend.git
    cd todo-frontend
    ```

2. Installiere die Frontend-Abhängigkeiten mit npm:

    ```bash
    npm install
    ```

3. Starte die Angular-Anwendung:

    ```bash
    ng serve
    ```

   Die Frontend-Anwendung wird auf Port **4200** ausgeführt. Greife auf die Anwendung zu unter [http://localhost:4200](http://localhost:4200).

### Backend- und Frontend-Skripte

Die Anwendung bietet folgende Skripte zur Verwaltung und Ausführung von Aufgaben:

#### Frontend (Angular) - Skripte:

- `ng serve`: Startet die Angular-Anwendung im Entwicklungsmodus.
- `ng build`: Baut das Frontend für die Produktion.
- `ng test`: Führt Unit-Tests aus.
- `ng lint`: Führt Linting auf dem Code aus.
- `ng e2e`: Führt End-to-End-Tests aus.
- `heroku-postbuild`: Baut das Angular-Frontend im Produktionsmodus für die Bereitstellung auf Heroku.

#### Backend (Spring Boot) - Skripte:

- **Heroku-Post-Build**:
    - Wenn die Anwendung auf Heroku bereitgestellt wird, wird nach dem Deployment der Befehl `ng build --aot --prod` ausgeführt, um die Angular-App zu erstellen.

## Konfiguration

### Backend-Konfiguration

1. **JWT-Konfiguration**:
    - Füge in der Datei `src/main/resources/application.properties` einen geheimen Schlüssel für JWT hinzu:

    ```properties
    jwt.secret=your_secret_key
    jwt.expiration=86400000 # Ablaufzeit in Millisekunden (24 Stunden)
    ```

2. **PostgreSQL-Konfiguration**:
    - Erstelle eine PostgreSQL-Datenbank, z. B. `todo_db`, und konfiguriere die Verbindung in der Datei `application.properties`.

### Frontend-Konfiguration

1. **Angular-Konfiguration**:
    - In der Datei `src/app/app.module.ts`, konfiguriere die URL des Backend-APIs. Zum Beispiel:

    ```typescript
    export const API_URL = 'http://localhost:8080/api'; // URL des Backends
    ```

2. **Authentifizierungskonfiguration**:
    - Verwende JWT im Frontend zur Authentifizierung. Speichere das JWT im `localStorage` und nutze es, um authentifizierte Anfragen an das Backend zu stellen.

## Nutzung

1. **Erstelle einen Benutzer**: Greife auf den Endpunkt `/register` zu, um einen neuen Benutzer zu erstellen. Gib einen Benutzernamen und ein Passwort ein.
2. **Anmeldung**: Verwende den Endpunkt `/login`, um ein gültiges JWT zu erhalten.
3. **Verwalte Aufgaben**:
    - Nach der Anmeldung kannst du auf die Frontend-Anwendung zugreifen, um Aufgaben zu erstellen, anzuzeigen, zu bearbeiten oder zu löschen.
    - Das Backend wird auf Anfragen vom Frontend mit den Aufgaben-Daten antworten.

## Projektstruktur

```plaintext
.
├── backend/                   # Backend-Code
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/dev/   # Backend-Pakete
│   │   │   └── resources/      # Konfigurationsdateien (application.properties)
│   └── build.gradle            # Gradle-Konfigurationsdatei
└── frontend/                  # Frontend-Code (Angular)
    ├── src/
    ├── angular.json            # Angular-Konfigurationsdatei
    └── package.json            # NPM-Konfigurationsdatei für Frontend


