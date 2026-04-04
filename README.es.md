# Juego da Velha (Ta-Te-Ti)

Ta-Te-Ti (Tic-Tac-Toe) implementado en Java con interfaz grafica Swing, arquitectura MVC, IA con poda alfa-beta, sistema de temas, guardado/carga, deshacer/rehacer y marcador persistente.

> 🌐 [Português](README.md) | [English](README.en.md) | **Español**

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

- Guarda estado completo: tablero, historial de jugadas, modo, jugadores, timer
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
