@echo off
REM Chess Game Compilation Fix Script

echo ===================================
echo Chess Game Compilation Fix Script
echo ===================================

REM Create necessary directories
echo Creating resource directories...
mkdir resources 2>nul
mkdir resources\pieces 2>nul

REM Clean up previous compilation
echo Cleaning up previous compilation...
del /Q /S *.class 2>nul

REM Compile files in the correct order
echo Compiling files in the correct order...

REM First, compile the enums and basic classes in the correct order
echo Step 1: Compiling base classes and enums...
javac src\Chess\ChessPieceColor.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ChessPieceColor.java
    goto :error
)

javac src\Chess\ChessPieceRank.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ChessPieceRank.java
    goto :error
)

javac src\Chess\ChessPieceLocation.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ChessPieceLocation.java
    goto :error
)

javac -cp src src\Chess\ChessPiece.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ChessPiece.java
    goto :error
)

REM Next, compile the ResourceManager
echo Step 2: Compiling ResourceManager...
javac -cp src src\Chess\ResourceManager.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ResourceManager.java
    goto :error
)

REM Now compile all chess piece implementations
echo Step 3: Compiling chess pieces...
javac -cp src src\Chess\ChessPieceKing.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ChessPieceKing.java
    goto :error
)

javac -cp src src\Chess\ChessPieceBishop.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ChessPieceBishop.java
    goto :error
)

javac -cp src src\Chess\ChessPieceKnight.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ChessPieceKnight.java
    goto :error
)

javac -cp src src\Chess\ChessPiecePawn.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ChessPiecePawn.java
    goto :error
)

javac -cp src src\Chess\ChessPieceQueen.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ChessPieceQueen.java
    goto :error
)

javac -cp src src\Chess\ChessPieceRook.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ChessPieceRook.java
    goto :error
)

REM Next, compile the board and related classes
echo Step 4: Compiling board classes...
javac -cp src src\Chess\ChessBoard.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ChessBoard.java
    goto :error
)

javac -cp src src\Chess\ChessBoardController.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ChessBoardController.java
    goto :error
)

REM Finally, compile the main game class
echo Step 5: Compiling main game class...
javac -cp src src\Chess\ChessGame.java
IF %ERRORLEVEL% NEQ 0 (
    echo Error compiling ChessGame.java
    goto :error
)

REM Try to compile the client/server classes (may fail if OCSF not available)
echo Step 6: Trying to compile network classes...
javac -cp src src\Chess\ChessClient.java 2>nul
javac -cp src src\Chess\ChessServer.java 2>nul

echo.
echo Compilation complete! You can run the game with:
echo java -cp . Chess.ChessGame
echo.
echo ===================================
goto :end

:error
echo.
echo Compilation failed. Please fix the errors and try again.
echo.
echo ===================================

:end