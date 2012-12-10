package server.net;

import java.io.BufferedReader;
import java.io.IOException;
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
	private Hashtable<Integer, PrintWriter> clientWriters = null;
	private Hashtable<Integer, BufferedReader> clientReaders = null;

	/**
	 * Constructor for MultiSocketServerThread. This class is created by a
	 * MultiSocketServer, and is meant to connect directly to a client. It also
	 * maintains a list of the other servers in order to tell them to update
	 * themselves when the associated client exits, and a list of the other
	 * clients in order to send messages to all connected clients.
	 * 
	 * @param serverSockets
	 *            A Hashtable of all servers.
	 * @param clientSockets
	 *            A Hashtable of all clients.
	 * @param client
	 *            An integer key which represents the client associated with
	 *            this server.
	 * @param protocol
	 *            The message protocol to be followed.
	 */
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

	/**
	 * Update the client list for a server. This clears the client readers &
	 * writers and rebuilds them based on the clientSockets list.
	 */
	public synchronized void updateClientList() {

		clientWriters.clear();
		clientReaders.clear();

		Iterator<Integer> clientSocketIter = clientSockets.keySet().iterator();

		while (clientSocketIter.hasNext()) {

			Integer client = clientSocketIter.next();
			Socket clientSocket = clientSockets.get(client);

			try {
				OutputStream outStream = clientSocket.getOutputStream();
				InputStream inStream = clientSocket.getInputStream();
				InputStreamReader inStreamReader = new InputStreamReader(
						inStream);

				clientWriters.put(client, new PrintWriter(outStream, true));
				clientReaders.put(client, new BufferedReader(inStreamReader));

			} catch (IOException e) {
				// Getting either (or both) of the output and input streams has
				// failed. In this case we'll remove the offending socket.
				// TODO
			}
		}
	}

	/**
	 * Remove the specified client from the list. This closes any associated
	 * readers & writers along with closing the socket itself. The removeServer
	 * method should follow this to remove the corresponding server.
	 * 
	 * @param client
	 *            An integer key which represents the client to remove (they're
	 *            stored in a Hashtable).
	 */
	private void removeClient(int client) {

		// Close clientWriter & reader for the client.
		clientWriters.get(client).close();
		try {
			clientReaders.get(client).close();
		} catch (IOException io) {
			// Working on removing references of the object anyway; it should
			// be garbage collected eventually.
		}

		clientWriters.remove(client);
		clientReaders.remove(client);

		// Close the client socket.
		// Remove the client from the list.
		try {
			clientSockets.get(client).close();
		} catch (IOException io) {
			// Working on removing references of the object anyway; it should
			// be garbage collected eventually.
		}
		clientSockets.remove(client);
	}

	/**
	 * Remove the specified server from the list, and update all servers to have
	 * this change reflected.
	 * 
	 * @param server
	 *            An integer key which represents the server to remove (they're
	 *            stored in a Hashtable).
	 */
	private void removeServer(int server) {

		// Remove the server from the list.
		serverSockets.remove(server);

		// Update the client list for other servers in the game.
		Iterator<Integer> serverSocketIter = serverSockets.keySet().iterator();

		while (serverSocketIter.hasNext()) {
			Integer serverToUpdate = serverSocketIter.next();
			serverSockets.get(serverToUpdate).updateClientList();
		}
	}

	@Override
	public void run() {

		String inputLine;
		ArrayList<String> outputLines;

		String outLine;
		String[] outLineArray;
		String messageRecipient;

		updateClientList();

		try {

			// Get the response, process it, and send back the next message.
			while ((inputLine = clientReaders.get(client).readLine()) != null) {

				outputLines = protocol.processInput(inputLine);

				for (int i = 0; i < outputLines.size(); i++) {

					outLine = outputLines.get(i);
					outLineArray = outLine.split(";");

					if (outLineArray[0].equals(SocketProtocol.replySender)) {

						messageRecipient = SocketProtocol.replySender;

					} else if (outLineArray[0].equals(SocketProtocol.replyAll)) {

						messageRecipient = SocketProtocol.replyAll;
					} else {
						// If we don't get a well formed message then skip it.
						continue;
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

						removeClient(client);
						removeServer(client); // Server & Client have equal ID.
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
