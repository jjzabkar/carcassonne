package net.server;

import java.net.ServerSocket;
import java.net.Socket;



public class SocketServer extends Thread {

	private int port;
	private Class<? extends SocketServerProtocol> protocol;

	/**
	 * Constructor for the Server.
	 * 
	 * Allow clients to connect to the server. With each connection we create a
	 * new thread to serve the client.
	 * 
	 * @param port
	 *            The port number to run the server on.
	 * @param protocol
	 *            The protocol to run on the clients.
	 */
	public SocketServer(int port, Class<? extends SocketServerProtocol> protocol) {

		this.port = port;
		this.protocol = protocol;
	}

	public void run() {

		try {
			ServerSocket serverSocket = new ServerSocket(port);
			SocketServerProtocol socketProtocol = protocol.newInstance();

			while (true) {

				Socket client = serverSocket.accept();

				int maxConnections = socketProtocol.getMaxConnections();
				int numConnections = socketProtocol.getNumConnections();

				if (numConnections < maxConnections) {
					new SocketServerThread(client, socketProtocol).start();
				} else {
					// TODO: something.. send a message to client saying game
					// is full.?
				}
			}

		} catch (Exception e) {
			// TODO
		}
	}
}
