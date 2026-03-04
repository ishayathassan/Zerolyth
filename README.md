# Zerolyth

Zerolyth is a 2-player multiplayer puzzle game built using Java, JavaFX, and a custom Client-Server architecture. Players compete against each other by solving a series of puzzles and exploring levels. 

In Zerolyth, two players connect to a central server and choose their roles: **Protagonist** or **Antagonist**. They race against each other to complete challenges and reach the end of the level first!

## Features

- **Multiplayer Architecture**: Custom TCP socket-based client-server architecture (`GameServer` and `GameClient`) allowing two players to connect and compete in real-time.
- **Role Selection**: Play as the *Protagonist* or the *Antagonist*.
- **Interactive Puzzles**: Includes several engaging mini-games:
  - **Simon Says**: Memory and sequence puzzle.
  - **Tower of Hanoi**: Classic mathematical puzzle.
  - **Cipher Disc**: Cryptography and alignment puzzle.
  - **Sliding Tile Puzzle**: Image reconstruction puzzle.
- **Level Exploration**: Navigate through levels, interact with the environment, and find collectibles.
- **JavaFX UI**: A rich graphical interface built using JavaFX and FXML.

## Prerequisites

To build and run this project, you will need:
- **Java JDK 24** (or compatible JDK)
- **Apache Maven** (3.8+ recommended)
- **JavaFX** (configured via Maven dependencies)

## Getting Started

### 1. Build the Project
Open a terminal in the project root directory and run:
```sh
mvn clean install
```

### 2. Start the Server
Before any clients can play, you must start the game server.
Run the `GameServer` main class:
```sh
mvn exec:java -Dexec.mainClass="com.example.zerolyth.GameServer"
```
The server runs on port `55555` by default.

### 3. Start the Clients
Open two separate terminals and start two instances of the `GameApp` client:
```sh
mvn javafx:run
```
Alternatively, you can run the `com.example.zerolyth.GameApp` main class directly from your IDE.

### 4. How to Play
1. In the first client, enter your name, select **Protagonist**, and click Start.
2. In the second client, enter your name, select **Antagonist**, and click Start.
3. The server will pair the two players and the game will begin!
4. Navigate through the level, interact with objects, and solve puzzles to win.

## Project Structure

- `src/main/java/com/example/zerolyth/` - Main Java source code.
  - `GameApp.java` - The main entry point for the client interface.
  - `GameServer.java` - The standalone server that manages player connections and game state.
  - `GameClient.java`, `ClientHandler.java` - Networking logic.
  - `puzzle/` - Contains logic and controllers for the various mini-games (Simon Says, Hanoi, Cipher Disc, Sliding Tile).
  - `LevelViewController.java` - Manages the main game view and interactions.
- `src/main/resources/com/example/zerolyth/` - FXML layouts, CSS styles, and graphical assets.

## License
MIT License.
