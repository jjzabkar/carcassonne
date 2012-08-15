package net;

import java.util.ArrayList;

public interface SocketProtocol {

	public static final String ACK = "ACK";
	public static final String NAK = "NAK";
	public static final String EXIT = "EXIT";

	public ArrayList<String> processInput(String input);
}
