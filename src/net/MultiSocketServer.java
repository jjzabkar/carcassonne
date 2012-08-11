package net;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-011
 */
public class MultiSocketServer {

	private final int MAX_NUM_CLIENTS = 5;
	private int numClients = 0;

	/**
	 * Constructor for the GameServer.
	 * 
	 * @param portNumber
	 *            The port number to run the server on.
	 * @param The
	 *            protocol to run on the clients.
	 */
	public MultiSocketServer(int portNumber, SocketProtocol protocol) {

		// Bind the server to the port.
		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);

			while (numClients <= MAX_NUM_CLIENTS) {

				Socket clientSocket = serverSocket.accept();
				numClients++;

				new MultiSocketServerThread(clientSocket, protocol).start();
			}

			serverSocket.close();

		} catch (Exception e) {

		}
	}
}
