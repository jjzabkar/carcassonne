package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-05
 */
public class SocketServer {

	private final int portNumber;

	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;

	private PrintWriter clientWriter = null;
	private BufferedReader clientReader = null;

	/**
	 * Constructor for the GameServer.
	 * 
	 * @param portNumber
	 *            The port number to run the server on.
	 */
	public SocketServer(int portNumber) {
		this.portNumber = portNumber;
	}

	/**
	 * Bind the server to the port.
	 * 
	 * @return a non-zero integer if there is an error, otherwise return zero.
	 */
	public int bind() {
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			return 1;
		}
		return 0;
	}

	/**
	 * Begin listening for connections. (must already be bound)
	 * 
	 * @return a non-zero integer if there is an error, otherwise return zero.
	 */
	public int listen() {
		try {
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			return 1;
		}
		return 0;
	}

	/**
	 * Set up the streams to send & receive data with the client.
	 * 
	 * @return a non-zero integer if there is an error, otherwise return zero.
	 */
	public int setUpStreams() {
		try {
			OutputStream clientOutputStream = clientSocket.getOutputStream();
			InputStream clientInputStream = clientSocket.getInputStream();

			clientWriter = new PrintWriter(clientOutputStream, true);

			InputStreamReader isr = new InputStreamReader(clientInputStream);
			clientReader = new BufferedReader(isr);

		} catch (IOException e) {
			return 1;
		}
		return 0;
	}

	/**
	 * Begin message transactions with the client following the passed in
	 * protocol.
	 * 
	 * @param protocol
	 *            The protocol to follow while interacting with the client.
	 * 
	 * @return a non-zero integer if there is an error, otherwise return zero.
	 */
	public int run(SocketProtocol protocol) {

		String inputLine;
		String outputLine;

		try {
			// Send the initial message to the client.
			outputLine = protocol.processInput(null);
			clientWriter.println(outputLine);

			// Get the response, process it, and send back the next message.
			while ((inputLine = clientReader.readLine()) != null) {

				outputLine = protocol.processInput(inputLine);
				clientWriter.println(outputLine);

				if (outputLine.equals(SocketProtocol.EXIT)) {
					break;
				}
			}

			clientWriter.close();
			clientReader.close();

			clientSocket.close();
			serverSocket.close();

		} catch (IOException e) {
			return 1;
		}
		return 0;
	}
}
