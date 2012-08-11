package net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-011
 */
public class MultiSocketServerThread extends Thread {

	private Socket clientSocket = null;
	private SocketProtocol protocol = null;

	public MultiSocketServerThread(Socket clientSocket, SocketProtocol protocol) {
		super("MultiSocketServerThread");

		this.clientSocket = clientSocket;
		this.protocol = protocol;
	}

	@Override
	public void run() {

		PrintWriter clientWriter = null;
		BufferedReader clientReader = null;

		String inputLine;
		String outputLine;

		try {

			OutputStream clientOutputStream = clientSocket.getOutputStream();
			InputStream clientInputStream = clientSocket.getInputStream();

			clientWriter = new PrintWriter(clientOutputStream, true);

			InputStreamReader isr = new InputStreamReader(clientInputStream);
			clientReader = new BufferedReader(isr);

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

		} catch (Exception e) {

		}

	}
}
