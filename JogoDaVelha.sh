#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAR="$SCRIPT_DIR/out/JogoDaVelha.jar"

if [ ! -f "$JAR" ]; then
    echo "Compilando..."
    cd "$SCRIPT_DIR"
    javac -sourcepath src/main/java -d out src/main/java/Main.java
    jar cfe out/JogoDaVelha.jar Main -C out .
fi

java -jar "$JAR" "$@"
