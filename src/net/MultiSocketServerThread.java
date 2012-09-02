package net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-08-011
 */
public class MultiSocketServerThread extends Thread {

	private int client = 0;
	private SocketProtocol protocol = null;

	private Hashtable<Integer, MultiSocketServerThread> serverSockets = null;
	private Hashtable<Integer, Socket> clientSockets = null;
	private Hashtable<Integer, PrintWriter> clientWriters;
	private Hashtable<Integer, BufferedReader> clientReaders;

	public MultiSocketServerThread(
			Hashtable<Integer, MultiSocketServerThread> serverSockets,
			Hashtable<Integer, Socket> clientSockets, int client,
			SocketProtocol protocol) {

		super("MultiSocketServerThread");

		this.client = client;
		this.protocol = protocol;

		this.serverSockets = serverSockets;
		this.clientSockets = clientSockets;
		clientWriters = new Hashtable<Integer, PrintWriter>();
		clientReaders = new Hashtable<Integer, BufferedReader>();
	}

	public synchronized void updateClientList() {

		clientWriters.clear();
		clientReaders.clear();

		Iterator<Integer> clientSocketIter = clientSockets.keySet().iterator();

		while (clientSocketIter.hasNext()) {

			Integer client = clientSocketIter.next();

			try {
				Socket clientSocket = clientSockets.get(client);

				OutputStream outStream = clientSocket.getOutputStream();
				InputStream inStream = clientSocket.getInputStream();
				InputStreamReader inStreamReader = new InputStreamReader(
						inStream);

				clientWriters.put(client, new PrintWriter(outStream, true));
				clientReaders.put(client, new BufferedReader(inStreamReader));
			} catch (Exception e) {
				// TODO
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
					} else {
						// TODO
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

						Iterator<Integer> clientWriterIter;
						clientWriterIter = clientWriters.keySet().iterator();

						while (clientWriterIter.hasNext()) {
							Integer clientWriter = clientWriterIter.next();
							clientWriters.get(clientWriter).println(outLine);
						}
					}

					if (outLine.equals(SocketProtocol.EXIT)) {
						// Close clientWriter & reader for the client.
						clientWriters.get(client).close();
						clientReaders.get(client).close();

						clientWriters.remove(client);
						clientReaders.remove(client);

						// Close the client socket.
						// Remove the client from the list.
						clientSockets.get(client).close();
						clientSockets.remove(client);

						// Remove the server from the list.
						// Server & Client are given the same ID.
						serverSockets.remove(client);

						// Update the client list for other servers in the game.
						Iterator<Integer> servSocketIter;
						servSocketIter = serverSockets.keySet().iterator();

						while (servSocketIter.hasNext()) {
							Integer server = servSocketIter.next();
							serverSockets.get(server).updateClientList();
						}

						return;
					}
				}
			}

		} catch (Exception e) {
		}
	}
}
