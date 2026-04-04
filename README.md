# Jogo da Velha / Tic-Tac-Toe / Ta-Te-Ti

Tic-Tac-Toe em Java com Swing GUI, MVC, IA (alpha-beta), temas, save/load, undo/redo e placar persistente.

---

<details>
<summary>🌐 Português (BR)</summary>

## Requisitos

- **Java 21+** (compilado com OpenJDK 25)
- **JUnit 4** (para rodar os testes)

## Estrutura do Projeto

```
JogoDaVelha/
├── JogoDaVelha.sh              # Executavel (duplo click)
├── JogoDaVelha.desktop         # Atalho de desktop Linux
├── src/
│   ├── main/java/
│   │   ├── Main.java                    # Ponto de entrada
│   │   │
│   │   ├── model/                       # Camada de dados e regras
│   │   │   ├── GameConfig.java          # Constantes, modos, Builder
│   │   │   ├── Board.java               # Tabuleiro 3x3, undo/redo, historico
│   │   │   ├── Player.java              # Jogador (nome + simbolo)
│   │   │   ├── GameRules.java           # Verificacao de vitoria e empate
│   │   │   ├── AIPlayer.java            # IA: Facil, Medio, Dificil (alpha-beta)
│   │   │   ├── MoveRecord.java          # Registro de jogada (serializavel)
│   │   │   ├── GameState.java           # Estado completo para save/load
│   │   │   └── Scoreboard.java          # Placar, estatisticas, win rate
│   │   │
│   │   ├── controller/                  # Camada de controle
│   │   │   ├── GameEventListener.java   # Interface Observer (12 eventos)
│   │   │   └── GameController.java      # Orquestracao do jogo
│   │   │
│   │   ├── persistence/                 # Camada de persistencia
│   │   │   ├── ScoreRepository.java             # Interface
│   │   │   ├── GameStateRepository.java         # Interface
│   │   │   ├── FileScoreRepository.java         # Implementacao em arquivo
│   │   │   └── FileGameStateRepository.java     # Save/load de partida
│   │   │
│   │   └── view/                        # Camada de apresentacao
│   │       ├── Theme.java                       # 4 temas visuais
│   │       ├── swing/
│   │       │   ├── SwingMainMenuView.java       # Menu principal (GUI)
│   │       │   └── SwingGameView.java           # Tabuleiro + controles (GUI)
│   │       └── console/
│   │           └── ConsoleGameAdapter.java      # Adaptador para terminal
│   │
│   └── test/java/model/
│       ├── BoardTest.java                        # 12 testes
│       ├── GameRulesTest.java                    # 10 testes
│       └── AIPlayerTest.java                     # 8 testes
│
├── out/                           # Classes compiladas (.class)
└── dist/                          # Distribuicao
```

## Como Compilar

```bash
javac -sourcepath src/main/java -d out src/main/java/Main.java
```

Para gerar o JAR:

```bash
jar cfe out/JogoDaVelha.jar Main -C out .
```

## Como Executar

### Interface Grafica (padrao)

```bash
java -cp out Main
```

Ou com o executavel:

```bash
./JogoDaVelha.sh
```

### Modo Console

```bash
java -cp out Main --console
```

Comandos no modo console: `u` (undo), `r` (redo), `s` (save), ou numero 1-9 para jogar.

## Como Rodar os Testes

Com JUnit 4 no classpath:

```bash
java -cp out:junit-4.13.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore \
    model.BoardTest model.GameRulesTest model.AIPlayerTest
```

## Arquitetura

O projeto segue o padrao **MVC (Model-View-Controller)** com **Observer** para desacoplar a interface da logica.

```
┌─────────────┐     eventos      ┌──────────────────┐
│    View      │ ◄──────────────  │   Controller      │
│  (Swing/     │                  │ GameController    │
│   Console)   │ ── acoes ──────► │                   │
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

### Fluxo de um movimento

1. Usuario clica num botao (View)
2. `SwingGameView` chama `controller.makeMove(pos)` (Controller)
3. `GameController` valida, marca no `Board`, verifica `GameRules` (Model)
4. `GameController` dispara evento `onBoardUpdated()` via `GameEventListener` (Observer)
5. `SwingGameView` atualiza os botoes na tela (View)

### GameEventListener (Interface Observer)

| Evento | Descricao |
|---|---|
| `onBoardUpdated(board, highlight)` | Tabuleiro atualizado |
| `onPlayerSwitched(player)` | Turno trocou |
| `onGameOver(winner, winLine)` | Alguem venceu |
| `onDraw()` | Empate |
| `onInvalidMove(reason)` | Jogada invalida |
| `onTimerTick(secondsLeft)` | Timer atualizou |
| `onMoveMade(pos, player)` | Jogada registrada |
| `onMoveUndone(board, player)` | Jogada desfeita |
| `onMoveRedone(board, player)` | Jogada refeita |
| `onScoreUpdated(scoreboard)` | Placar mudou |
| `onGameSaved()` | Partida salva |
| `onGameLoaded(state)` | Partida carregada |

## Features

### Modos de Jogo

| Modo | Descricao |
|---|---|
| **PvP** | Jogador vs Jogador (local) |
| **IA Facil** | Jogadas aleatorias + bloqueio basico |
| **IA Media** | Heuristicas (centro, cantos) + 60% minimax |
| **IA Dificil** | Alpha-beta pruning completo (invencivel) |

### IA - Niveis de Dificuldade

- **Facil**: escolhe jogada aleatoria, mas bloqueia vitoria imediata do oponente
- **Medio**: prioriza vitoria > bloqueio > centro > cantos; 60% das vezes usa minimax
- **Dificil**: algoritmo **Minimax com Alpha-Beta Pruning** — avalia toda a arvore de decisoes e nunca perde

### Sistema de Undo/Redo

- `Board` mantem pilha de `MoveRecord` para undo e redo
- No modo IA, undo/redo desfaz/refaz tanto a jogada da IA quanto a do jogador
- Disponivel via botoes na GUI ou comandos `u`/`r` no console

### Save/Load de Partida

- Salva estado completo: tabuleiro, historico de jogadas, modo, jogadores, timer
- Formato texto simples com campos chave=valor
- Opcao "Continuar Partida" no menu principal quando ha jogo salvo
- Implementado via `FileGameStateRepository` (interface `GameStateRepository`)

### Placar e Ranking

- Conta vitorias de X, vitorias de O e empates
- Calcula taxa de vitoria (win rate %)
- Persistente entre sessoes via `FileScoreRepository`
- Opcao de resetar no dialog de ranking

### Temas Visuais

| Tema | Descricao |
|---|---|
| **Classico** | Fundo cinza claro, X azul, O vermelho |
| **Escuro** | Fundo escuro, X azul claro, O coral |
| **Neon** | Fundo preto, X ciano, O rosa |
| **Retro** | Fundo verde, X amarelo, O laranja |

### Timer por Jogada

- Opcional ao iniciar partida (15 segundos)
- Desabilitado durante turno da IA
- Se o tempo esgotar, o jogador perde (forfeit automatico)

### Historico de Jogadas

- Painel lateral na GUI lista todas as jogadas em ordem
- Atualiza em tempo real com cada movimento
- Cada entrada mostra: numero da jogada, simbolo, posicao

### Highlight de Vitoria

- Ao vencer, as 3 celulas da linha vencedora sao destacadas em verde
- Funciona para todas as 8 combinacoes (3 linhas + 3 colunas + 2 diagonais)

## Classes Principais

### model/Board

Gerencia a matriz 3x3 com pilhas de undo/redo. Metodos principais:

- `markPosition(pos, symbol)` — marca uma celula e registra no historico
- `undoLastMove()` — desfaz a ultima jogada (push para redo stack)
- `redoLastMove()` — refaz a ultima jogada desfeita
- `restoreFrom(cells, history, moveCount)` — restaura estado salvo

### model/GameRules

Logica pura de verificacao, sem estado. Retorna `int[]` com as posicoes vencedoras ou `null`.

- `checkVictory(board, symbol)` — verifica as 8 combinacoes possiveis
- `checkDraw(board)` — verifica se o tabuleiro esta cheio sem vencedor

### model/AIPlayer

Metodo `chooseMove(board)` com estrategia variavel por dificuldade:

- **Facil**: `findWinningMove()` + `randomMove()`
- **Medio**: vitoria > bloqueio > centro > cantos > 60% minimax
- **Dificil**: `alphabeta()` com cortes alfa-beta, profundidade ilimitada

### controller/GameController

Orquestra todo o fluxo do jogo. Recebe `GameEventListener` e `GameStateRepository` por injecao de dependencia.

- `makeMove(pos)` — valida, marca, verifica vitoria/empate, alterna jogador
- `undoMove()` / `redoMove()` — delega ao Board com troca de jogador
- `saveGame()` — cria `GameState` e salva via repositorio
- `resumeGame(state)` — restaura partida salva
- `forfeit()` — jogador atual desiste

### persistence/

Interfaces `ScoreRepository` e `GameStateRepository` desacoplam a logica de persistencia. Implementacoes em arquivo texto com formato `chave=valor`.

## Execucao Rapida

```bash
# Compilar
javac -sourcepath src/main/java -d out src/main/java/Main.java

# Rodar GUI
java -cp out Main

# Rodar Console
java -cp out Main --console

# Executavel (duplo click)
./JogoDaVelha.sh
```

</details>

---

<details>
<summary>🌐 English</summary>

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

</details>

---

<details>
<summary>🌐 Español</summary>

## Requisitos

- **Java 21+** (compilado con OpenJDK 25)
- **JUnit 4** (para ejecutar las pruebas)

## Estructura del Proyecto

```
JogoDaVelha/
├── JogoDaVelha.sh              # Ejecutable (doble click)
├── JogoDaVelha.desktop         # Acceso directo de escritorio Linux
├── src/
│   ├── main/java/
│   │   ├── Main.java                    # Punto de entrada
│   │   │
│   │   ├── model/                       # Capa de datos y reglas
│   │   │   ├── GameConfig.java          # Constantes, modos, Builder
│   │   │   ├── Board.java               # Tablero 3x3, deshacer/rehacer, historial
│   │   │   ├── Player.java              # Jugador (nombre + simbolo)
│   │   │   ├── GameRules.java           # Deteccion de victoria y empate
│   │   │   ├── AIPlayer.java            # IA: Facil, Medio, Dificil (alfa-beta)
│   │   │   ├── MoveRecord.java          # Registro de jugada (serializable)
│   │   │   ├── GameState.java           # Estado completo para guardar/cargar
│   │   │   └── Scoreboard.java          # Marcador, estadisticas, win rate
│   │   │
│   │   ├── controller/                  # Capa de control
│   │   │   ├── GameEventListener.java   # Interfaz Observer (12 eventos)
│   │   │   └── GameController.java      # Orquestacion del juego
│   │   │
│   │   ├── persistence/                 # Capa de persistencia
│   │   │   ├── ScoreRepository.java             # Interfaz
│   │   │   ├── GameStateRepository.java         # Interfaz
│   │   │   ├── FileScoreRepository.java         # Implementacion en archivo
│   │   │   └── FileGameStateRepository.java     # Guardar/cargar partida
│   │   │
│   │   └── view/                        # Capa de presentacion
│   │       ├── Theme.java                       # 4 temas visuales
│   │       ├── swing/
│   │       │   ├── SwingMainMenuView.java       # Menu principal (GUI)
│   │       │   └── SwingGameView.java           # Tablero + controles (GUI)
│   │       └── console/
│   │           └── ConsoleGameAdapter.java      # Adaptador para terminal
│   │
│   └── test/java/model/
│       ├── BoardTest.java                        # 12 pruebas
│       ├── GameRulesTest.java                    # 10 pruebas
│       └── AIPlayerTest.java                     # 8 pruebas
│
├── out/                           # Clases compiladas (.class)
└── dist/                          # Distribucion
```

## Como Compilar

```bash
javac -sourcepath src/main/java -d out src/main/java/Main.java
```

Para generar el JAR:

```bash
jar cfe out/JogoDaVelha.jar Main -C out .
```

## Como Ejecutar

### Interfaz Grafica (predeterminada)

```bash
java -cp out Main
```

O con el ejecutable:

```bash
./JogoDaVelha.sh
```

### Modo Consola

```bash
java -cp out Main --console
```

Comandos en modo consola: `u` (deshacer), `r` (rehacer), `s` (guardar), o numero 1-9 para jugar.

## Como Ejecutar las Pruebas

Con JUnit 4 en el classpath:

```bash
java -cp out:junit-4.13.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore \
    model.BoardTest model.GameRulesTest model.AIPlayerTest
```

## Arquitectura

El proyecto sigue el patron **MVC (Modelo-Vista-Controlador)** con **Observer** para desacoplar la interfaz de la logica.

```
┌─────────────┐     eventos      ┌──────────────────┐
│    Vista     │ ◄──────────────  │   Controlador     │
│  (Swing/     │                  │ GameController    │
│   Consola)   │ ── acciones ───► │                   │
└─────────────┘                   └───────┬───────────┘
                                          │
                                          ▼
                                  ┌───────────────┐
                                  │    Modelo      │
                                  │ Board, Rules,  │
                                  │ AI, Scoreboard │
                                  └───────┬───────┘
                                          │
                                          ▼
                                  ┌───────────────┐
                                  │  Persistencia  │
                                  │  Repositorios  │
                                  └───────────────┘
```

### Flujo de un movimiento

1. El usuario hace clic en un boton (Vista)
2. `SwingGameView` llama a `controller.makeMove(pos)` (Controlador)
3. `GameController` valida, marca en `Board`, verifica `GameRules` (Modelo)
4. `GameController` dispara el evento `onBoardUpdated()` via `GameEventListener` (Observer)
5. `SwingGameView` actualiza los botones en pantalla (Vista)

### GameEventListener (Interfaz Observer)

| Evento | Descripcion |
|---|---|
| `onBoardUpdated(board, highlight)` | Tablero actualizado |
| `onPlayerSwitched(player)` | Turno cambiado |
| `onGameOver(winner, winLine)` | Alguien gano |
| `onDraw()` | Empate |
| `onInvalidMove(reason)` | Jugada invalida |
| `onTimerTick(secondsLeft)` | Timer actualizado |
| `onMoveMade(pos, player)` | Jugada registrada |
| `onMoveUndone(board, player)` | Jugada deshecha |
| `onMoveRedone(board, player)` | Jugada rehecha |
| `onScoreUpdated(scoreboard)` | Marcador actualizado |
| `onGameSaved()` | Partida guardada |
| `onGameLoaded(state)` | Partida cargada |

## Caracteristicas

### Modos de Juego

| Modo | Descripcion |
|---|---|
| **PvP** | Jugador vs Jugador (local) |
| **IA Facil** | Jugadas aleatorias + bloqueo basico |
| **IA Media** | Heuristicas (centro, esquinas) + 60% minimax |
| **IA Dificil** | Poda alfa-beta completa (invencible) |

### IA - Niveles de Dificultad

- **Facil**: elige jugada aleatoria, pero bloquea la victoria inmediata del oponente
- **Medio**: prioriza victoria > bloqueo > centro > esquinas; usa minimax el 60% de las veces
- **Dificil**: algoritmo **Minimax con Poda Alfa-Beta** — evalua todo el arbol de decisiones y nunca pierde

### Sistema de Deshacer/Rehacer

- `Board` mantiene una pila de `MoveRecord` para deshacer y rehacer
- En modo IA, deshacer/rehacer revierte/repite tanto la jugada de la IA como la del jugador
- Disponible mediante botones en la GUI o comandos `u`/`r` en consola

### Guardar/Cargar Partida

- Guarda estado completo: tabuleiro, historial de jugadas, modo, jugadores, timer
- Formato de texto simple con campos clave=valor
- Opcion "Continuar Partida" en el menu principal cuando hay juego guardado
- Implementado via `FileGameStateRepository` (interfaz `GameStateRepository`)

### Marcador y Ranking

- Cuenta victorias de X, victorias de O y empates
- Calcula tasa de victoria (win rate %)
- Persistente entre sesiones via `FileScoreRepository`
- Opcion de reiniciar en el dialogo de ranking

### Temas Visuales

| Tema | Descripcion |
|---|---|
| **Clasico** | Fondo gris claro, X azul, O rojo |
| **Oscuro** | Fondo oscuro, X azul claro, O coral |
| **Neon** | Fondo negro, X cian, O rosa |
| **Retro** | Fondo verde, X amarillo, O naranja |

### Timer por Jugada

- Opcional al iniciar partida (15 segundos)
- Deshabilitado durante el turno de la IA
- Si se acaba el tiempo, el jugador pierde automaticamente

### Historial de Jugadas

- Panel lateral en la GUI lista todas las jugadas en orden
- Se actualiza en tiempo real con cada movimiento
- Cada entrada muestra: numero de jugada, simbolo, posicion

### Resaltado de Victoria

- Al ganar, las 3 celdas de la linea ganadora se resaltan en verde
- Funciona para las 8 combinaciones (3 filas + 3 columnas + 2 diagonales)

## Clases Principales

### model/Board

Gestiona la matriz 3x3 con pilas de deshacer/rehacer. Metodos principales:

- `markPosition(pos, symbol)` — marca una celda y registra en el historial
- `undoLastMove()` — deshace la ultima jugada (push a la pila de rehacer)
- `redoLastMove()` — rehace la ultima jugada deshecha
- `restoreFrom(cells, history, moveCount)` — restaura estado guardado

### model/GameRules

Logica pura de verificacion, sin estado. Retorna `int[]` con las posiciones ganadoras o `null`.

- `checkVictory(board, symbol)` — verifica las 8 combinaciones posibles
- `checkDraw(board)` — verifica si el tablero esta lleno sin ganador

### model/AIPlayer

Metodo `chooseMove(board)` con estrategia variable segun dificultad:

- **Facil**: `findWinningMove()` + `randomMove()`
- **Medio**: victoria > bloqueo > centro > esquinas > 60% minimax
- **Dificil**: `alphabeta()` con cortes alfa-beta, profundidad ilimitada

### controller/GameController

Orquesta todo el flujo del juego. Recibe `GameEventListener` y `GameStateRepository` por inyeccion de dependencia.

- `makeMove(pos)` — valida, marca, verifica victoria/empate, alterna jugador
- `undoMove()` / `redoMove()` — delega al Board con cambio de jugador
- `saveGame()` — crea `GameState` y guarda via repositorio
- `resumeGame(state)` — restaura partida guardada
- `forfeit()` — el jugador actual se rinde

### persistence/

Las interfaces `ScoreRepository` y `GameStateRepository` desacoplan la logica de persistencia. Las implementaciones en archivo usan formato de texto `clave=valor`.

## Inicio Rapido

```bash
# Compilar
javac -sourcepath src/main/java -d out src/main/java/Main.java

# Ejecutar GUI
java -cp out Main

# Ejecutar Consola
java -cp out Main --console

# Ejecutable (doble click)
./JogoDaVelha.sh
```

</details>
