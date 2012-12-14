package server.net;

import java.net.Socket;
import java.util.ArrayList;

public interface SocketProtocol {

	public static final String ACK = "ACK";
	public static final String NAK = "NAK";
	public static final String EXIT = "EXIT";

	public static final String replyAll = "replyAll";
	public static final String replySender = "replySender";

	/**
	 * Allow a sender to be added to the socket protocol's list of senders.
	 * 
	 * This can be useful to send messages to clients which are not the current
	 * senders, though which are affected by the actions of the current sender. 
	 * 
	 * @param sender
	 *     A PrintWriter object which is tied to the sender object.
	 */
	public void addSender(Socket sender);
	
	/**
	 * The sender provides a way to identify itself against the list of
	 * senders which are added. 
	 */
	public ArrayList<String> processInput(Socket sender, String input);
	
	/**
	 * Get the maximum number of allowed client connections to this protocol.
	 * 
	 * @return
	 *     an integer representing the maximum number of allowed connections.
	 */
	public int getMaxConnections();
	
	/**
	 * Get the current number of clients connected to this protocol.
	 *  
	 * @return
	 *     an integer representing the current number of connections.
	 */
	public int getNumConnections();
	
}
