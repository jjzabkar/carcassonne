package net.client;

public interface SocketProtocol {

	public static final String ACK = "ACK";
	public static final String NAK = "NAK";
	public static final String EXIT = "EXIT";

	public String processInput(String input);
}
