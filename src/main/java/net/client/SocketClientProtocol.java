package net.client;

import java.awt.Color;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;

public abstract class SocketClientProtocol {

	public static final String ACK = "ACK";
	public static final String NAK = "NAK";
	public static final String EXIT = "EXIT";

	public static final String replyAll = "replyAll";
	public static final String replySender = "replySender";

	/**
	 * The sender provides a way to identify itself against the list of senders
	 * which are added.
	 */
	public abstract ArrayList<String> processInput(Socket sender, String input);

	/**
	 * Convert a Color to a String of length nine consisting of an RGB value.
	 * Each individual color value (R, G, B) is a string of length three,
	 * containing a value from "000" to "255".
	 * 
	 * @param color
	 *            A Color to be converted to a String representation.
	 * 
	 * @return A String representing the input Color.
	 */
	public static String colorToString(Color color) {

		DecimalFormat df = new DecimalFormat("000");

		String r = df.format(color.getRed());
		String g = df.format(color.getGreen());
		String b = df.format(color.getBlue());
		String rgb = r + g + b;

		return rgb;
	}

	/**
	 * Convert a String of length nine to a Color. The string consists of an RGB
	 * value; with each being 3 characters each containing a value from "000" to
	 * "255".
	 * 
	 * @param string
	 *            The String to be converted to a Color.
	 * 
	 * @return A Color representing the input String.
	 */
	public static Color stringToColor(String string) {

		int r = Integer.parseInt(string.substring(0, 3));
		int g = Integer.parseInt(string.substring(3, 6));
		int b = Integer.parseInt(string.substring(6, 9));

		return new Color(r, g, b);
	}
}
