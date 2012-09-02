package net;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-011
 */
public class MultiSocketServer {

	private int currentClientID = 0;

	private int port;
	private Class<? extends SocketProtocol> protocol;

	private Hashtable<Integer, MultiSocketServerThread> servers;
	private Hashtable<Integer, Socket> clients;

	/**
	 * Constructor for the Server.
	 * 
	 * Allow clients to connect to the server. With each connection we create a
	 * new thread to serve the client, as well as passing in the list of all
	 * clients so that messages can be sent to all clients if need be. When a
	 * new client is connected, we call on each thread to update its client
	 * list.
	 * 
	 * @param port
	 *            The port number to run the server on.
	 * @param protocol
	 *            The protocol to run on the clients.
	 */
	public MultiSocketServer(int port, Class<? extends SocketProtocol> protocol) {

		this.port = port;
		this.protocol = protocol;

		servers = new Hashtable<Integer, MultiSocketServerThread>();
		clients = new Hashtable<Integer, Socket>();
		run();
	}

	// TODO? max number of clients
	// TODO? max number of clients per server (multiple games)

	public void run() {

		try {
			ServerSocket serverSocket = new ServerSocket(port);
			SocketProtocol socketProtocol = protocol.newInstance();

			while (true) {

				clients.put(currentClientID, serverSocket.accept());

				MultiSocketServerThread serverThread;
				serverThread = new MultiSocketServerThread(clients,
						currentClientID, socketProtocol);
				serverThread.start();

				// When a new client is connected we get each server to update
				// its client list. We don't update the just added server's
				// client list as it's called explicitly in the thread itself.
				for (int i = 0; i < servers.size(); i++) {
					servers.get(i).updateClientList();
				}

				servers.put(currentClientID, serverThread);
				currentClientID++;
			}

		} catch (Exception e) {
			// TODO
		}
	}
}
