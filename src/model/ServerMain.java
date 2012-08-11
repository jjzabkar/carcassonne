package model;

import net.GameProtocol;
import net.MultiSocketServer;

public class ServerMain {

	public static void main(String[] args) {

		// Create our game protocol, Start up the server, and run it!
		new MultiSocketServer(4444, new GameProtocol());
	}
}
