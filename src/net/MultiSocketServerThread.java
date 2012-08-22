package net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-011
 */
public class MultiSocketServerThread extends Thread {

	private ArrayList<Socket> clientSockets = null;
	private int client = 0;
	private SocketProtocol protocol = null;

	private ArrayList<PrintWriter> clientWriters = new ArrayList<PrintWriter>();
	private ArrayList<BufferedReader> clientReaders = new ArrayList<BufferedReader>();

	public MultiSocketServerThread(ArrayList<Socket> clientSockets, int client,
			SocketProtocol protocol) {

		super("MultiSocketServerThread");

		this.clientSockets = clientSockets;
		this.client = client;
		this.protocol = protocol;
	}

	public synchronized void updateClientList() {

		clientWriters.clear();
		clientReaders.clear();

		for (int i = 0; i < clientSockets.size(); i++) {

			try {
				Socket clientSocket = clientSockets.get(i);

				OutputStream outStream = clientSocket.getOutputStream();
				InputStream inStream = clientSocket.getInputStream();
				InputStreamReader inStreamReader = new InputStreamReader(
						inStream);

				clientWriters.add(i, new PrintWriter(outStream, true));
				clientReaders.add(i, new BufferedReader(inStreamReader));
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void run() {

		String inputLine;
		ArrayList<String> outputLines;

		updateClientList();

		try {

			// Get the response, process it, and send back the next message.
			while ((inputLine = clientReaders.get(client).readLine()) != null) {

				outputLines = protocol.processInput(inputLine);

				for (int i = 0; i < outputLines.size(); i++) {

					String outLine = outputLines.get(i);
					String messageRecipient = SocketProtocol.replyAll;
					String[] outLineArray = outLine.split(";");

					if (outLineArray[0].equals(SocketProtocol.replySender)) {

						messageRecipient = SocketProtocol.replySender;

					} else if (outLineArray[0].equals(SocketProtocol.replyAll)) {

						messageRecipient = SocketProtocol.replyAll;
					}

					// Remove the message recipient header from the message as
					// it is forwarded to the actual clients. If ';' isn't in
					// the string, then outline is set to itself.
					int beginIndex = outLine.indexOf(";") + 1;
					outLine = outLine.substring(beginIndex);

					// Depending on our message header either send the message
					// to the sender, or to all clients.
					if (messageRecipient.equals(SocketProtocol.replySender)) {

						clientWriters.get(client).println(outLine);

					} else if (messageRecipient.equals(SocketProtocol.replyAll)) {

						for (int j = 0; j < clientWriters.size(); j++) {
							clientWriters.get(j).println(outLine);
						}
					}

					if (outLine.equals(SocketProtocol.EXIT)) {
						break;
					}
				}
			}

			for (int i = 0; i < clientWriters.size(); i++) {
				clientWriters.get(i).close();
			}

			for (int i = 0; i < clientReaders.size(); i++) {
				clientReaders.get(i).close();
			}

			clientSockets.get(client).close();

		} catch (Exception e) {
		}
	}
}
