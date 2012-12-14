package server.net;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;

public class MultiSocketServer extends Thread {

	private int port;
	private Class<? extends SocketProtocol> protocol;

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
	public MultiSocketServer(int port, Class<? extends SocketProtocol> protocol) {

		this.port = port;
		this.protocol = protocol;
	}

	// TODO? max number of clients

	public void run() {

		try {
			ServerSocket serverSocket = new ServerSocket(port);
			SocketProtocol socketProtocol = protocol.newInstance();

			while (true) {

				Socket client = serverSocket.accept();
				
				int maxConnections = socketProtocol.getMaxConnections();
				int numConnections = socketProtocol.getNumConnections();
				
				if (numConnections < maxConnections) {
					new MultiSocketServerThread(client, socketProtocol).start();
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
