# Jogo da Velha

Jogo da Velha (Tic-Tac-Toe) implementado em Java com interface grafica Swing, arquitetura MVC, IA com alpha-beta pruning, sistema de temas, save/load, undo/redo e placar persistente.

> 🌐 **PT-BR** | [English](README.en.md) | [Español](README.es.md)

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
