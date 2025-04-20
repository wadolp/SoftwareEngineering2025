@echo off
REM Build script for compiling the chess game

REM Create the output directory for compiled classes
mkdir bin 2>nul

REM Compile shared message classes first
javac -d bin src/shared/messages/*.java

REM Compile server classes
javac -cp "bin;lib/*" -d bin src/server/*.java

REM Wait for 3 seconds to give the server time to initialize
timeout /t 3 /nobreak

REM Compile client classes
javac -cp "bin;lib/*" -d bin src/client/*.java

echo Compilation complete!

REM Run the server in a new command prompt
start cmd /k java -cp "bin;lib/*" server.ChessServer

REM Run the client in a new command prompt
start cmd /k java -cp "bin;lib/*" client.ChessClient