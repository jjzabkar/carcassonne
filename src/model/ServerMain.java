package model;

import net.GameProtocol;
import net.MultiSocketServer;

public class ServerMain {

	public static void main(String[] args) {

		// Check & parse arguments.
		if (args.length != 1) {
			System.out.println("Parameters are: <port>");
			return;
		}

		final int port = Integer.parseInt(args[0]);

		// Create our game protocol, Start up the server, and run it!
		new MultiSocketServer(port, GameProtocol.class);
	}
}
