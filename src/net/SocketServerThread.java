package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import net.server.SocketServerProtocol;


public class SocketServerThread extends Thread {

	private Socket clientSocket = null;
	private SocketServerProtocol protocol = null;

	private PrintWriter clientWriter = null;
	private BufferedReader clientReader = null;

	/**
	 * Constructor for SocketServerThread. This class is created by a
	 * SocketServer, and is meant to connect directly to a client.
	 * 
	 * @param clientSocket
	 *            The socket that represents a connection to the client.
	 * @param protocol
	 *            The message protocol to be followed.
	 */
	public SocketServerThread(Socket clientSocket, SocketServerProtocol protocol) {

		super("SocketServerThread");

		this.clientSocket = clientSocket;
		this.protocol = protocol;
		this.protocol.addSender(clientSocket);
	}

	/**
	 * Remove the client. This closes the associated readers & writers along
	 * with closing the socket itself.
	 */
	private void removeClient() {

		// Close the writer for the client.
		clientWriter.close();

		// Attempt to close the reader and socket for the client.
		try {
			clientReader.close();
			clientSocket.close();
		} catch (IOException io) {
			// Working on removing references of the object anyway; it should
			// be garbage collected eventually.
		}
	}

	private void createMessagers() {
		try {
			OutputStream outStream = clientSocket.getOutputStream();
			clientWriter = new PrintWriter(outStream, true);

			InputStream inStream = clientSocket.getInputStream();
			InputStreamReader inStreamReader = new InputStreamReader(inStream);
			clientReader = new BufferedReader(inStreamReader);

		} catch (IOException e) {
			// Getting either (or both) of the output and input streams has
			// failed. In this case we'll remove the offending socket.
			// TODO
		}
	}

	@Override
	public void run() {

		String inputLine;
		ArrayList<String> outputLines;
		String outLine;

		createMessagers();

		try {

			// Get the response, process it, and send back the next message.
			while ((inputLine = clientReader.readLine()) != null) {

				outputLines = protocol.processInput(clientSocket, inputLine);

				for (int i = 0; i < outputLines.size(); i++) {

					outLine = outputLines.get(i);

					clientWriter.println(outLine);

					if (outLine.equals(SocketServerProtocol.EXIT)) {

						removeClient();
						return;
					}
				}
			}

		} catch (IOException io) {
			// Readline has created an exception in the main loop. Close down
			// the system and remove all related sockets, readers, writers, etc.
			// TODO
		}
	}
}
