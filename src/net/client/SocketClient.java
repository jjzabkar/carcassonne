package net.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient {

	private final String server;
	private final int portNumber;
	private SocketClientProtocol protocol;

	private Socket socket = null;
	private PrintWriter writer = null;

	/**
	 * Constructor for the GameClient.
	 * 
	 * @param server
	 *            A string indicating the hostname of the server.
	 * @param portNumber
	 *            A port number for which to bind to the server on.
	 */
	public SocketClient(String server, int portNumber, SocketClientProtocol protocol) {
		this.server = server;
		this.portNumber = portNumber;
		this.protocol = protocol;
	}

	/**
	 * Bind the client to the server & port. Create a writer to send messages to
	 * the server, and a thread to constantly wait to receive messages.
	 * 
	 * @return a non-zero integer if there is an error, otherwise return zero.
	 */
	public int bind() {
		try {
			socket = new Socket(server, portNumber);
			writer = new PrintWriter(socket.getOutputStream(), true);

			// Start a separate thread listening for events from the server;
			// the events are created by different clients (or this one!).
			new SocketClientThread(socket, protocol).start();

		} catch (UnknownHostException e) {
			return 1;
		} catch (IOException e) {
			return 1;
		}

		return 0;
	}

	/**
	 * Send a message to the bound socket. The message sent back is caught by
	 * the thread created in bind function.
	 * 
	 * @param message
	 *            The message to send to the bound socket.
	 */
	public void sendMessage(String message) {

		if (message != null) {

			writer.println(message);

			if (message.equals(SocketClientProtocol.EXIT)) {
				writer.close();
			}
		}
	}

}
