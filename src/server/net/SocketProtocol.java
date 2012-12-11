package server.net;

import java.io.PrintWriter;
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
	 * @param sender A PrintWriter object which is tied to the sender object.
	 */
	public void addSender(PrintWriter sender);
	
	/**
	 * The sender provides a way to identify itself against the list of
	 * senders which are added. 
	 */
	public ArrayList<String> processInput(PrintWriter sender, String input);
}
