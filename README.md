# Jogo da Velha / Tic-Tac-Toe / Ta-Te-Ti

**[Portugues](#portugues-br)** &nbsp;|&nbsp; **[English](#english)** &nbsp;|&nbsp; **[Espanol](#espanol)**

---

<a id="portugues-br"></a>
## 🇧🇷 Portugues (BR)

Jogo da Velha (Tic-Tac-Toe) implementado em Java com interface grafica Swing, arquitetura MVC, IA com alpha-beta pruning, sistema de temas, save/load, undo/redo e placar persistente.

### Requisitos

- **Java 21+** (compilado com OpenJDK 25)
- **JUnit 4** (para rodar os testes)

### Estrutura do Projeto

```
JogoDaVelha/
├── JogoDaVelha.sh              # Executavel (duplo click)
├── JogoDaVelha.desktop         # Atalho de desktop Linux
├── src/
│   ├── main/java/
│   │   ├── Main.java                    # Ponto de entrada
│   │   ├── model/                       # Camada de dados e regras
│   │   │   ├── GameConfig.java          # Constantes, modos, Builder
│   │   │   ├── Board.java               # Tabuleiro 3x3, undo/redo, historico
│   │   │   ├── Player.java              # Jogador (nome + simbolo)
│   │   │   ├── GameRules.java           # Verificacao de vitoria e empate
│   │   │   ├── AIPlayer.java            # IA: Facil, Medio, Dificil (alpha-beta)
│   │   │   ├── MoveRecord.java          # Registro de jogada (serializavel)
│   │   │   ├── GameState.java           # Estado completo para save/load
│   │   │   └── Scoreboard.java          # Placar, estatisticas, win rate
│   │   ├── controller/
│   │   │   ├── GameEventListener.java   # Interface Observer (12 eventos)
│   │   │   └── GameController.java      # Orquestracao do jogo
│   │   ├── persistence/
│   │   │   ├── ScoreRepository.java             # Interface
│   │   │   ├── GameStateRepository.java         # Interface
│   │   │   ├── FileScoreRepository.java         # Implementacao em arquivo
│   │   │   └── FileGameStateRepository.java     # Save/load de partida
│   │   └── view/
│   │       ├── Theme.java                       # 4 temas visuais
│   │       ├── swing/
│   │       │   ├── SwingMainMenuView.java       # Menu principal (GUI)
│   │       │   └── SwingGameView.java           # Tabuleiro + controles (GUI)
│   │       └── console/
│   │           └── ConsoleGameAdapter.java      # Adaptador para terminal
│   └── test/java/model/
│       ├── BoardTest.java                        # 12 testes
│       ├── GameRulesTest.java                    # 10 testes
│       └── AIPlayerTest.java                     # 8 testes
├── out/                           # Classes compiladas (.class)
└── dist/                          # Distribuicao
```

### Como Compilar

```bash
javac -sourcepath src/main/java -d out src/main/java/Main.java
jar cfe out/JogoDaVelha.jar Main -C out .
```

### Como Executar

| Modo | Comando |
|---|---|
| GUI | `java -cp out Main` ou `./JogoDaVelha.sh` |
| Console | `java -cp out Main --console` |

Console: `u` (undo), `r` (redo), `s` (save), ou 1-9 para jogar.

### Testes

```bash
java -cp out:junit-4.13.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore \
    model.BoardTest model.GameRulesTest model.AIPlayerTest
```

### Arquitetura

Padrao **MVC + Observer**:

```
View (Swing/Console) ── acoes ──► Controller ──► Model (Board, Rules, AI)
       ▲                            │                   │
       └──────── eventos ──────────┘                   ▼
                                              Persistence (Repositories)
```

**Fluxo:** Usuario clica → `controller.makeMove(pos)` → marca no `Board` → verifica `GameRules` → dispara `onBoardUpdated()` → View atualiza.

### GameEventListener (12 eventos)

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

### Modos de Jogo

| Modo | Descricao |
|---|---|
| **PvP** | Jogador vs Jogador (local) |
| **IA Facil** | Aleatorio + bloqueio basico |
| **IA Media** | Heuristicas (centro, cantos) + 60% minimax |
| **IA Dificil** | Alpha-beta pruning (invencivel) |

### Temas Visuais

| Tema | Descricao |
|---|---|
| **Classico** | Fundo cinza claro, X azul, O vermelho |
| **Escuro** | Fundo escuro, X azul claro, O coral |
| **Neon** | Fundo preto, X ciano, O rosa |
| **Retro** | Fundo verde, X amarelo, O laranja |

### Features

- **Undo/Redo** — pilha de jogadas, disponivel na GUI e console
- **Save/Load** — salva estado completo do tabuleiro e historico
- **Placar persistente** — vitorias, empates, win rate %, entre sessoes
- **Timer por jogada** — 15s opcional, forfeit automatico ao expirar
- **Historico visual** — painel lateral lista todas as jogadas em tempo real
- **Highlight de vitoria** — 3 celulas vencedoras destacadas em verde

### Classes Principais

| Classe | Responsabilidade |
|---|---|
| `model/Board` | Matriz 3x3, undo/redo, historico de jogadas |
| `model/GameRules` | Verificacao de vitoria (8 combos) e empate |
| `model/AIPlayer` | IA com 3 niveis (aleatorio / heuristico / alpha-beta) |
| `controller/GameController` | Orquestra fluxo do jogo, timer, IA |
| `persistence/*` | Interfaces + implementacoes File para save e placar |

**[Voltar ao topo](#jogo-da-velha--tic-tac-toe--ta-te-ti)**

---
---

<a id="english"></a>
## 🇺🇸 English

Tic-Tac-Toe implemented in Java with Swing GUI, MVC architecture, AI with alpha-beta pruning, theme system, save/load, undo/redo and persistent scoreboard.

### Requirements

- **Java 21+** (compiled with OpenJDK 25)
- **JUnit 4** (to run tests)

### Project Structure

```
JogoDaVelha/
├── JogoDaVelha.sh              # Executable (double-click)
├── JogoDaVelha.desktop         # Linux desktop shortcut
├── src/
│   ├── main/java/
│   │   ├── Main.java                    # Entry point
│   │   ├── model/                       # Data and rules layer
│   │   │   ├── GameConfig.java          # Constants, modes, Builder
│   │   │   ├── Board.java               # 3x3 board, undo/redo, history
│   │   │   ├── Player.java              # Player (name + symbol)
│   │   │   ├── GameRules.java           # Win and draw detection
│   │   │   ├── AIPlayer.java            # AI: Easy, Medium, Hard (alpha-beta)
│   │   │   ├── MoveRecord.java          # Move record (serializable)
│   │   │   ├── GameState.java           # Full state for save/load
│   │   │   └── Scoreboard.java          # Scoreboard, stats, win rate
│   │   ├── controller/
│   │   │   ├── GameEventListener.java   # Observer interface (12 events)
│   │   │   └── GameController.java      # Game orchestration
│   │   ├── persistence/
│   │   │   ├── ScoreRepository.java             # Interface
│   │   │   ├── GameStateRepository.java         # Interface
│   │   │   ├── FileScoreRepository.java         # File implementation
│   │   │   └── FileGameStateRepository.java     # Save/load game
│   │   └── view/
│   │       ├── Theme.java                       # 4 visual themes
│   │       ├── swing/
│   │       │   ├── SwingMainMenuView.java       # Main menu (GUI)
│   │       │   └── SwingGameView.java           # Board + controls (GUI)
│   │       └── console/
│   │           └── ConsoleGameAdapter.java      # Terminal adapter
│   └── test/java/model/
│       ├── BoardTest.java                        # 12 tests
│       ├── GameRulesTest.java                    # 10 tests
│       └── AIPlayerTest.java                     # 8 tests
├── out/                           # Compiled classes (.class)
└── dist/                          # Distribution
```

### How to Compile

```bash
javac -sourcepath src/main/java -d out src/main/java/Main.java
jar cfe out/JogoDaVelha.jar Main -C out .
```

### How to Run

| Mode | Command |
|---|---|
| GUI | `java -cp out Main` or `./JogoDaVelha.sh` |
| Console | `java -cp out Main --console` |

Console: `u` (undo), `r` (redo), `s` (save), or 1-9 to play.

### Tests

```bash
java -cp out:junit-4.13.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore \
    model.BoardTest model.GameRulesTest model.AIPlayerTest
```

### Architecture

**MVC + Observer** pattern:

```
View (Swing/Console) ── actions ──► Controller ──► Model (Board, Rules, AI)
       ▲                            │                   │
       └──────── events ───────────┘                   ▼
                                              Persistence (Repositories)
```

**Flow:** User clicks → `controller.makeMove(pos)` → marks on `Board` → checks `GameRules` → fires `onBoardUpdated()` → View updates.

### GameEventListener (12 events)

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

### Game Modes

| Mode | Description |
|---|---|
| **PvP** | Player vs Player (local) |
| **AI Easy** | Random moves + basic blocking |
| **AI Medium** | Heuristics (center, corners) + 60% minimax |
| **AI Hard** | Alpha-beta pruning (unbeatable) |

### Visual Themes

| Theme | Description |
|---|---|
| **Classic** | Light gray background, blue X, red O |
| **Dark** | Dark background, light blue X, coral O |
| **Neon** | Black background, cyan X, pink O |
| **Retro** | Green background, yellow X, orange O |

### Features

- **Undo/Redo** — move stack, available in GUI and console
- **Save/Load** — saves full board state and move history
- **Persistent scoreboard** — wins, draws, win rate %, across sessions
- **Turn timer** — optional 15s, auto-forfeit on timeout
- **Move history** — side panel lists all moves in real time
- **Win highlight** — 3 winning cells highlighted in green

### Main Classes

| Class | Responsibility |
|---|---|
| `model/Board` | 3x3 matrix, undo/redo, move history |
| `model/GameRules` | Win detection (8 combos) and draw check |
| `model/AIPlayer` | AI with 3 levels (random / heuristic / alpha-beta) |
| `controller/GameController` | Orchestrates game flow, timer, AI |
| `persistence/*` | Interfaces + File implementations for save and scoreboard |

**[Back to top](#jogo-da-velha--tic-tac-toe--ta-te-ti)**

---
---

<a id="espanol"></a>
## 🇪🇸 Espanol

Ta-Te-Ti (Tic-Tac-Toe) implementado en Java con interfaz grafica Swing, arquitectura MVC, IA con poda alfa-beta, sistema de temas, guardado/carga, deshacer/rehacer y marcador persistente.

### Requisitos

- **Java 21+** (compilado con OpenJDK 25)
- **JUnit 4** (para ejecutar las pruebas)

### Estructura del Proyecto

```
JogoDaVelha/
├── JogoDaVelha.sh              # Ejecutable (doble click)
├── JogoDaVelha.desktop         # Acceso directo de escritorio Linux
├── src/
│   ├── main/java/
│   │   ├── Main.java                    # Punto de entrada
│   │   ├── model/                       # Capa de datos y reglas
│   │   │   ├── GameConfig.java          # Constantes, modos, Builder
│   │   │   ├── Board.java               # Tablero 3x3, deshacer/rehacer, historial
│   │   │   ├── Player.java              # Jugador (nombre + simbolo)
│   │   │   ├── GameRules.java           # Deteccion de victoria y empate
│   │   │   ├── AIPlayer.java            # IA: Facil, Medio, Dificil (alfa-beta)
│   │   │   ├── MoveRecord.java          # Registro de jugada (serializable)
│   │   │   ├── GameState.java           # Estado completo para guardar/cargar
│   │   │   └── Scoreboard.java          # Marcador, estadisticas, win rate
│   │   ├── controller/
│   │   │   ├── GameEventListener.java   # Interfaz Observer (12 eventos)
│   │   │   └── GameController.java      # Orquestacion del juego
│   │   ├── persistence/
│   │   │   ├── ScoreRepository.java             # Interfaz
│   │   │   ├── GameStateRepository.java         # Interfaz
│   │   │   ├── FileScoreRepository.java         # Implementacion en archivo
│   │   │   └── FileGameStateRepository.java     # Guardar/cargar partida
│   │   └── view/
│   │       ├── Theme.java                       # 4 temas visuales
│   │       ├── swing/
│   │       │   ├── SwingMainMenuView.java       # Menu principal (GUI)
│   │       │   └── SwingGameView.java           # Tablero + controles (GUI)
│   │       └── console/
│   │           └── ConsoleGameAdapter.java      # Adaptador para terminal
│   └── test/java/model/
│       ├── BoardTest.java                        # 12 pruebas
│       ├── GameRulesTest.java                    # 10 pruebas
│       └── AIPlayerTest.java                     # 8 pruebas
├── out/                           # Clases compiladas (.class)
└── dist/                          # Distribucion
```

### Como Compilar

```bash
javac -sourcepath src/main/java -d out src/main/java/Main.java
jar cfe out/JogoDaVelha.jar Main -C out .
```

### Como Ejecutar

| Modo | Comando |
|---|---|
| GUI | `java -cp out Main` o `./JogoDaVelha.sh` |
| Consola | `java -cp out Main --console` |

Consola: `u` (deshacer), `r` (rehacer), `s` (guardar), o 1-9 para jugar.

### Pruebas

```bash
java -cp out:junit-4.13.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore \
    model.BoardTest model.GameRulesTest model.AIPlayerTest
```

### Arquitectura

Patron **MVC + Observer**:

```
Vista (Swing/Consola) ── acciones ──► Controlador ──► Modelo (Board, Rules, AI)
       ▲                                │                   │
       └────────── eventos ────────────┘                   ▼
                                                Persistencia (Repositorios)
```

**Flujo:** Usuario hace clic → `controller.makeMove(pos)` → marca en `Board` → verifica `GameRules` → dispara `onBoardUpdated()` → Vista se actualiza.

### GameEventListener (12 eventos)

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

### Modos de Juego

| Modo | Descripcion |
|---|---|
| **PvP** | Jugador vs Jugador (local) |
| **IA Facil** | Aleatorio + bloqueo basico |
| **IA Media** | Heuristicas (centro, esquinas) + 60% minimax |
| **IA Dificil** | Poda alfa-beta (invencible) |

### Temas Visuales

| Tema | Descripcion |
|---|---|
| **Clasico** | Fondo gris claro, X azul, O rojo |
| **Oscuro** | Fondo oscuro, X azul claro, O coral |
| **Neon** | Fondo negro, X cian, O rosa |
| **Retro** | Fondo verde, X amarillo, O naranja |

### Caracteristicas

- **Deshacer/Rehacer** — pila de jugadas, disponible en GUI y consola
- **Guardar/Cargar** — guarda estado completo del tablero e historial
- **Marcador persistente** — victorias, empates, win rate %, entre sesiones
- **Timer por jugada** — 15s opcional, derrota automatica al expirar
- **Historial visual** — panel lateral lista todas las jugadas en tiempo real
- **Resaltado de victoria** — 3 celdas ganadoras resaltadas en verde

### Clases Principales

| Clase | Responsabilidad |
|---|---|
| `model/Board` | Matriz 3x3, deshacer/rehacer, historial de jugadas |
| `model/GameRules` | Deteccion de victoria (8 combos) y empate |
| `model/AIPlayer` | IA con 3 niveles (aleatorio / heuristico / alfa-beta) |
| `controller/GameController` | Orquesta flujo del juego, timer, IA |
| `persistence/*` | Interfaces + implementaciones File para guardado y marcador |

**[Volver arriba](#jogo-da-velha--tic-tac-toe--ta-te-ti)**
