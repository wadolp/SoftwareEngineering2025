package Chess;

import ocsf.server.*;

public class ChessServer extends AbstractServer {

    ChessServer() {
        super(1234);
        this.setTimeout(500);
    }

    ChessServer(int port, int timeout) {
        super(port);
        this.setTimeout(timeout);
    }

    @Override
    protected void handleMessageFromClient(Object o, ConnectionToClient connectionToClient) {

    }
}