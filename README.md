# Go — Java Client–Server Game

A networked Go game built with Java 21. A JavaFX client communicates with a TCP matchmaking server; the server manages concurrent games, a heuristic opponent, persistence and replay.

**Scope:** a university/team project demonstrating Java networking, object-oriented design, game rules, graph traversal and automated tests. It is separate from the Go programming language.

## Features

- A 9 × 9 board, alternating turns, connected stone groups and captures.
- Liberty calculation, suicide prevention and ko checking.
- Pass, resignation and scoring after two consecutive passes.
- Dead-group agreement, territory, prisoners and 6.5-point komi.
- Multiplayer matchmaking and a server-side heuristic bot.
- MariaDB-backed game records and replay by game ID.

## Technology

| Area | Stack |
| --- | --- |
| Application | Java 21 |
| Client | JavaFX |
| Server | TCP sockets and per-client handling |
| Persistence | Spring Boot, JPA/Hibernate, MariaDB |
| Build and tests | Maven, JUnit 4, Mockito |
| Documentation | JavaDoc |

## Run locally

Requirements: JDK 21, Maven, MariaDB and a graphical environment. Maven downloads JavaFX; a manually installed JavaFX SDK is not required.

### 1. Configure MariaDB

Create the `go_game` database and a user with access to it. The checked-in local example is:

| Setting | Example |
| --- | --- |
| URL | `jdbc:mariadb://localhost:3306/go_game` |
| Username | `go_user` |
| Example password | `go_pass` |

Override these with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD` for your own environment. Hibernate currently creates or updates the tables on startup.

### 2. Compile and start the server

From the repository root:

```shell
mvn clean compile
mvn exec:java -Pserver
```

### 3. Start the graphical client

In another terminal:

```shell
mvn javafx:run
```

Start a second client for multiplayer, or select the bot from the interface. The current client connects to `localhost:5000`.

## Playing

Click a board intersection to place a stone. **Pass** skips a turn and **Resign** ends the player's participation. Two consecutive passes enter scoring: mark dead groups, then both players choose **Accept**. **Continue** returns to play if the position is disputed. The interface also supports loading a stored game by ID for replay.

## Tests and JavaDoc

```shell
mvn clean verify
mvn javadoc:javadoc
```

The current suite contains 55 tests. JavaFX tests need a display; on a headless Linux machine:

```shell
xvfb-run -a mvn clean verify
```

JavaDoc is generated under `target/site/apidocs/`. Generated documentation, build output and local IDE settings are excluded from version control.

## Read the code

Paths below are relative to `src/main/java/pl/edu/go/`:

| Package | Responsibility |
| --- | --- |
| [model](src/main/java/pl/edu/go/model/) | Board state, legal moves, groups and scoring |
| [client](src/main/java/pl/edu/go/client/) | JavaFX interface and server communication |
| [server](src/main/java/pl/edu/go/server/) | Client handling and game sessions |
| [server/bot](src/main/java/pl/edu/go/server/bot/) | Bot strategies |
| [server/commandInterfaces](src/main/java/pl/edu/go/server/commandInterfaces/) | Protocol commands |
| [server/persistence](src/main/java/pl/edu/go/server/persistence/) | Game and move storage |
| [server/replay](src/main/java/pl/edu/go/server/replay/) | Saved-game playback |

Interesting starting points include group/liberty traversal in the board model, territory scoring and the separation between transport commands and game state. Contributions from the team are recorded in Git history.
