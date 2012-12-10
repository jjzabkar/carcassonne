package server.net;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;

public class MultiSocketServer {

	private int currentClientID = 0;

	private int port;
	private Class<? extends SocketProtocol> protocol;

	private Hashtable<Integer, MultiSocketServerThread> servers;
	private Hashtable<Integer, Socket> clients;

	/**
	 * Constructor for the Server.
	 * 
	 * Allow clients to connect to the server. With each connection we create a
	 * new thread to serve the client, as well as passing in the list of all
	 * clients so that messages can be sent to all clients if need be. When a
	 * new client is connected, we call on each thread to update its client
	 * list.
	 * 
	 * @param port
	 *            The port number to run the server on.
	 * @param protocol
	 *            The protocol to run on the clients.
	 */
	public MultiSocketServer(int port, Class<? extends SocketProtocol> protocol) {

		this.port = port;
		this.protocol = protocol;

		servers = new Hashtable<Integer, MultiSocketServerThread>();
		clients = new Hashtable<Integer, Socket>();
		run();
	}

	// TODO? max number of clients
	// TODO? max number of clients per server (multiple games)

	public void run() {

		try {
			ServerSocket serverSocket = new ServerSocket(port);
			SocketProtocol socketProtocol = protocol.newInstance();

			while (true) {

				Socket client = serverSocket.accept();
				clients.put(currentClientID, client);

				MultiSocketServerThread serverThread;
				serverThread = new MultiSocketServerThread(servers, clients,
						currentClientID, socketProtocol);
				serverThread.start();

				// When a new client is connected we get each server to update
				// its client list. We don't update the just added server's
				// client list as it's called explicitly in the thread itself.
				Iterator<Integer> servSocketIter = servers.keySet().iterator();

				while (servSocketIter.hasNext()) {
					Integer server = servSocketIter.next();
					servers.get(server).updateClientList();
				}

				servers.put(currentClientID, serverThread);
				currentClientID++;
			}

		} catch (Exception e) {
			// TODO
		}
	}
}
