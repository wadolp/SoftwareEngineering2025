package Chess;

import ocsf.client.AbstractClient;

public class ChessClient extends AbstractClient {
    public ChessClient(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object o) {

    }
}
