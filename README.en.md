# Tic-Tac-Toe

Tic-Tac-Toe implemented in Java with Swing GUI, MVC architecture, AI with alpha-beta pruning, theme system, save/load, undo/redo, and persistent scoreboard.

> 🌐 [Português](README.md) | **English** | [Español](README.es.md)

## Requirements

- **Java 21+** (compiled with OpenJDK 25)
- **JUnit 4** (to run tests)

## Project Structure

```
JogoDaVelha/
├── JogoDaVelha.sh              # Executable (double-click)
├── JogoDaVelha.desktop         # Linux desktop shortcut
├── src/
│   ├── main/java/
│   │   ├── Main.java                    # Entry point
│   │   │
│   │   ├── model/                       # Data and rules layer
│   │   │   ├── GameConfig.java          # Constants, modes, Builder
│   │   │   ├── Board.java               # 3x3 board, undo/redo, history
│   │   │   ├── Player.java              # Player (name + symbol)
│   │   │   ├── GameRules.java           # Win and draw detection
│   │   │   ├── AIPlayer.java            # AI: Easy, Medium, Hard (alpha-beta)
│   │   │   ├── MoveRecord.java          # Move record (serializable)
│   │   │   ├── GameState.java           # Full state for save/load
│   │   │   └── Scoreboard.java          # Scoreboard, stats, win rate
│   │   │
│   │   ├── controller/                  # Control layer
│   │   │   ├── GameEventListener.java   # Observer interface (12 events)
│   │   │   └── GameController.java      # Game orchestration
│   │   │
│   │   ├── persistence/                 # Persistence layer
│   │   │   ├── ScoreRepository.java             # Interface
│   │   │   ├── GameStateRepository.java         # Interface
│   │   │   ├── FileScoreRepository.java         # File implementation
│   │   │   └── FileGameStateRepository.java     # Save/load game
│   │   │
│   │   └── view/                        # Presentation layer
│   │       ├── Theme.java                       # 4 visual themes
│   │       ├── swing/
│   │       │   ├── SwingMainMenuView.java       # Main menu (GUI)
│   │       │   └── SwingGameView.java           # Board + controls (GUI)
│   │       └── console/
│   │           └── ConsoleGameAdapter.java      # Terminal adapter
│   │
│   └── test/java/model/
│       ├── BoardTest.java                        # 12 tests
│       ├── GameRulesTest.java                    # 10 tests
│       └── AIPlayerTest.java                     # 8 tests
│
├── out/                           # Compiled classes (.class)
└── dist/                          # Distribution
```

## How to Compile

```bash
javac -sourcepath src/main/java -d out src/main/java/Main.java
```

To generate the JAR:

```bash
jar cfe out/JogoDaVelha.jar Main -C out .
```

## How to Run

### Graphical Interface (default)

```bash
java -cp out Main
```

Or with the executable:

```bash
./JogoDaVelha.sh
```

### Console Mode

```bash
java -cp out Main --console
```

Console commands: `u` (undo), `r` (redo), `s` (save), or number 1-9 to play.

## How to Run Tests

With JUnit 4 on the classpath:

```bash
java -cp out:junit-4.13.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore \
    model.BoardTest model.GameRulesTest model.AIPlayerTest
```

## Architecture

The project follows the **MVC (Model-View-Controller)** pattern with **Observer** to decouple the interface from the logic.

```
┌─────────────┐     events       ┌──────────────────┐
│    View      │ ◄──────────────  │   Controller      │
│  (Swing/     │                  │ GameController    │
│   Console)   │ ── actions ────► │                   │
└─────────────┘                   └───────┬───────────┘
                                          │
                                          ▼
                                  ┌───────────────┐
                                  │    Model       │
                                  │ Board, Rules,  │
                                  │ AI, Scoreboard │
                                  └───────┬───────┘
                                          │
                                          ▼
                                  ┌───────────────┐
                                  │  Persistence   │
                                  │ Repositories   │
                                  └───────────────┘
```

### Move Flow

1. User clicks a button (View)
2. `SwingGameView` calls `controller.makeMove(pos)` (Controller)
3. `GameController` validates, marks on `Board`, checks `GameRules` (Model)
4. `GameController` fires `onBoardUpdated()` event via `GameEventListener` (Observer)
5. `SwingGameView` updates the buttons on screen (View)

### GameEventListener (Observer Interface)

| Event | Description |
|---|---|
| `onBoardUpdated(board, highlight)` | Board updated |
| `onPlayerSwitched(player)` | Turn switched |
| `onGameOver(winner, winLine)` | Someone won |
| `onDraw()` | Draw |
| `onInvalidMove(reason)` | Invalid move |
| `onTimerTick(secondsLeft)` | Timer updated |
| `onMoveMade(pos, player)` | Move recorded |
| `onMoveUndone(board, player)` | Move undone |
| `onMoveRedone(board, player)` | Move redone |
| `onScoreUpdated(scoreboard)` | Score changed |
| `onGameSaved()` | Game saved |
| `onGameLoaded(state)` | Game loaded |

## Features

### Game Modes

| Mode | Description |
|---|---|
| **PvP** | Player vs Player (local) |
| **AI Easy** | Random moves + basic blocking |
| **AI Medium** | Heuristics (center, corners) + 60% minimax |
| **AI Hard** | Full alpha-beta pruning (unbeatable) |

### AI - Difficulty Levels

- **Easy**: picks random move, but blocks opponent's immediate win
- **Medium**: prioritizes win > block > center > corners; uses minimax 60% of the time
- **Hard**: **Minimax with Alpha-Beta Pruning** — evaluates the entire decision tree and never loses

### Undo/Redo System

- `Board` maintains a stack of `MoveRecord` for undo and redo
- In AI mode, undo/redo reverses/replays both the AI's and the player's moves
- Available via buttons in the GUI or `u`/`r` commands in console

### Save/Load Game

- Saves full state: board, move history, mode, players, timer
- Simple text format with key=value fields
- "Continue Game" option in the main menu when a saved game exists
- Implemented via `FileGameStateRepository` (implements `GameStateRepository`)

### Scoreboard and Ranking

- Counts X wins, O wins, and draws
- Calculates win rate (%)
- Persists between sessions via `FileScoreRepository`
- Option to reset in the ranking dialog

### Visual Themes

| Theme | Description |
|---|---|
| **Classic** | Light gray background, blue X, red O |
| **Dark** | Dark background, light blue X, coral O |
| **Neon** | Black background, cyan X, pink O |
| **Retro** | Green background, yellow X, orange O |

### Turn Timer

- Optional when starting a game (15 seconds)
- Disabled during AI's turn
- If time runs out, the player forfeits automatically

### Move History

- Side panel in the GUI lists all moves in order
- Updates in real time with each move
- Each entry shows: move number, symbol, position

### Win Highlight

- On victory, the 3 winning cells are highlighted in green
- Works for all 8 combinations (3 rows + 3 columns + 2 diagonals)

## Main Classes

### model/Board

Manages the 3x3 matrix with undo/redo stacks. Key methods:

- `markPosition(pos, symbol)` — marks a cell and records in history
- `undoLastMove()` — undoes the last move (pushes to redo stack)
- `redoLastMove()` — redoes the last undone move
- `restoreFrom(cells, history, moveCount)` — restores saved state

### model/GameRules

Pure verification logic, stateless. Returns `int[]` with winning positions or `null`.

- `checkVictory(board, symbol)` — checks all 8 possible combinations
- `checkDraw(board)` — checks if the board is full with no winner

### model/AIPlayer

`chooseMove(board)` method with variable strategy by difficulty:

- **Easy**: `findWinningMove()` + `randomMove()`
- **Medium**: win > block > center > corners > 60% minimax
- **Hard**: `alphabeta()` with alpha-beta cuts, unlimited depth

### controller/GameController

Orchestrates the entire game flow. Receives `GameEventListener` and `GameStateRepository` via dependency injection.

- `makeMove(pos)` — validates, marks, checks win/draw, switches player
- `undoMove()` / `redoMove()` — delegates to Board with player switch
- `saveGame()` — creates `GameState` and saves via repository
- `resumeGame(state)` — restores saved game
- `forfeit()` — current player gives up

### persistence/

`ScoreRepository` and `GameStateRepository` interfaces decouple persistence logic. File implementations use `key=value` text format.

## Quick Start

```bash
# Compile
javac -sourcepath src/main/java -d out src/main/java/Main.java

# Run GUI
java -cp out Main

# Run Console
java -cp out Main --console

# Executable (double-click)
./JogoDaVelha.sh
```
