package net.client;

import java.net.Socket;
import java.util.ArrayList;

public interface SocketClientProtocol {

	public static final String ACK = "ACK";
	public static final String NAK = "NAK";
	public static final String EXIT = "EXIT";

	public static final String replyAll = "replyAll";
	public static final String replySender = "replySender";

	/**
	 * The sender provides a way to identify itself against the list of senders
	 * which are added.
	 */
	public ArrayList<String> processInput(Socket sender, String input);
}
