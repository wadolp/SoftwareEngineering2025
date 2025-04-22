# Networked Chess Game

A Java-based networked chess game using client-server architecture.

## Quick Start Guide

### Database Setup

Run these SQL commands to set up the required database:

```sql
CREATE DATABASE chess_db;
USE chess_db;
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);
```

### Running the Server

Always start the server first:

```bash
java -cp "bin;lib/*" server.ChessServer
```

By default, the server listens on port 12345. You can specify a different port as an argument:

```bash
java -cp "bin;lib/*" server.ChessServer 9000
```

### Running the Client

Open a different terminal window for each client and run:

```bash
java -cp "bin;lib/*" client.ChessClient <ServerIP> <port#>
```

Where:
- `<ServerIP>` is the IP address of the server computer
- `<port#>` is the port number (default is 12345)

Examples:
- Same computer: `java -cp "bin;lib/*" client.ChessClient localhost 12345`
- Local network: `java -cp "bin;lib/*" client.ChessClient 192.168.1.5 12345`

## Network Configuration

### Local Network

If both computers are on the same local network:
1. Find the server's local IP address:
   - Windows: `ipconfig` in Command Prompt
   - macOS/Linux: `ifconfig` or `ip addr` in Terminal
2. Use this IP address when starting the client

### Different Networks

If the computers are on different networks:
1. The server computer needs a public IP address
2. Configure port forwarding on the server's router
3. Clients connect using the server's public IP address

## Troubleshooting

- Ensure MySQL is running on the server computer
- Check that firewalls allow connections on the game port
- Verify the correct IP address and port are being used by the client
- Restart both client and server if synchronization issues occur

## Game Instructions

1. Register or log in through the client interface
2. The server automatically matches players who are waiting for a game
3. White pieces always move first
4. Follow standard chess rules for piece movement and capture

## File Structure

- `src/client/` - Client-side code
- `src/server/` - Server-side code
- `src/shared/` - Shared message classes used by both client and server
