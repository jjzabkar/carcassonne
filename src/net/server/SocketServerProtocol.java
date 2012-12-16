package net.server;

import java.net.Socket;

import net.client.SocketClientProtocol;

public interface SocketServerProtocol extends SocketClientProtocol {

	/**
	 * Allow a sender to be added to the socket protocol's list of senders.
	 * 
	 * This can be useful to send messages to clients which are not the current
	 * senders, though which are affected by the actions of the current sender.
	 * 
	 * @param sender
	 *            A Socket object which is tied to the sender object.
	 */
	public void addSender(Socket sender);

	/**
	 * Allow a sender to be removed from the socket protocol's list of senders.
	 * 
	 * @param sender
	 *            A Socket object which is tied to the sender object.
	 */
	public void removeSender(Socket sender);

	/**
	 * Get the maximum number of allowed client connections to this protocol.
	 * 
	 * @return an integer representing the maximum number of allowed
	 *         connections.
	 */
	public int getMaxConnections();

	/**
	 * Get the current number of clients connected to this protocol.
	 * 
	 * @return an integer representing the current number of connections.
	 */
	public int getNumConnections();

}
