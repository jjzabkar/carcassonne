package net.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

// We have 2 avenues to send/receive messages from the server. We can either
// send a message and get a message back directly (sendMessage). Or we can
// receive messages from the server without sending a message.
class SocketClientThread extends Thread {

	private Socket server = null;
	private SocketClientProtocol protocol = null;

	public SocketClientThread(Socket server, SocketClientProtocol protocol) {
		super("SocketClientThread");
		this.server = server;
		this.protocol = protocol;
	}

	@Override
	public void run() {

		try {
			OutputStream outputStream = server.getOutputStream();
			InputStream inputStream = server.getInputStream();

			InputStreamReader isr = new InputStreamReader(inputStream);

			PrintWriter writer = new PrintWriter(outputStream, true);
			BufferedReader reader = new BufferedReader(isr);

			String inputLine;

			while ((inputLine = reader.readLine()) != null) {

				// Don't do anything with the response for now.
                // Nor does the client protocol use the socket.
				protocol.processInput(null, inputLine);

				if (inputLine.equals(SocketClientProtocol.EXIT)) {

					writer.close();
					reader.close();

					// Close the socket here instead of in the parent
					// 'socketClient' so that we can pass back the 'exit'
					// message & allow the client application to act on it.

                    // Socket client is attached to the app and sends messages
                    // from the app to the server, while the Socket client
                    // thread listens for returned messages from the server
                    // which it forwards to the app. They share the socket.
					server.close();

					return;
				}
			}

		} catch (Exception e) {
		}
	}
}
