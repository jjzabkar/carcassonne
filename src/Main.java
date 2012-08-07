import net.GameProtocol;
import net.SocketServer;

public class Main {

	public static void main(String[] args) {

		// Create our game protocol.
		GameProtocol gameProtocol = new GameProtocol();

		// Start up the server, and run it!
		SocketServer socketServer = new SocketServer(4444);
		socketServer.bind();
		socketServer.listen();
		socketServer.setUpStreams();
		socketServer.run(gameProtocol);
	}
}
