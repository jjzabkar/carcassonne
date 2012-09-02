package net;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-011
 */
public class MultiSocketServer {

	private final int MAX_NUM_CLIENTS = 5;

	private ArrayList<MultiSocketServerThread> servers = new ArrayList<MultiSocketServerThread>();
	private ArrayList<Socket> clients = new ArrayList<Socket>();

	/**
	 * Constructor for the Server.
	 * 
	 * Allow MAX_NUM_CLIENTS clients to connect to the server. With each
	 * connection we create a new thread to server the client, as well as
	 * passing in the list of all clients so that messages can be multicast if
	 * need be. When a new client is connected, we call on each thread to update
	 * its client list.
	 * 
	 * @param port
	 *            The port number to run the server on.
	 * @param protocol
	 *            The protocol to run on the clients.
	 */
	public MultiSocketServer(int port, Class<? extends SocketProtocol> protocol) {

		// Bind the server to the port.
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			SocketProtocol sp = protocol.newInstance();
			int numClients = 0;

			while (numClients < MAX_NUM_CLIENTS) {

				clients.add(serverSocket.accept());

				MultiSocketServerThread serverThread;
				serverThread = new MultiSocketServerThread(clients, numClients,
						sp);
				serverThread.start();

				// When a new client is connected we get each server to update
				// its client list. We don't update the just added server's
				// client list as it's called explicitly in the thread itself.
				for (int i = 0; i < servers.size(); i++) {
					servers.get(i).updateClientList();
				}

				servers.add(serverThread);
				numClients++;
			}

			serverSocket.close();

		} catch (Exception e) {

		}
	}
}
