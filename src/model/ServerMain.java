package model;
import net.GameProtocol;
import net.SocketServer;

public class ServerMain {

	public static void main(String[] args) {

		// Create our game protocol.
		GameProtocol gameProtocol = new GameProtocol();

		// Start up the server, and run it!
		SocketServer socketServer = new SocketServer(4444);

		int err = socketServer.bind();

		err = socketServer.listen();

		err = socketServer.setUpStreams();

		err = socketServer.run(gameProtocol);
	}
}
