package tests;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import model.Meeple;

import org.junit.Test;

public class MeepleTest {

	@Test
	public void meepleInitTest() {

		// Test object.
		Meeple meeple = new Meeple(Color.BLACK);

		// Oracle.
		Color oracleColor = Color.BLACK.brighter().brighter().brighter();
		int oracleX = 0;
		int oracleY = 0;

		// Tests.
		assertEquals(oracleColor, meeple.getColor());
		assertEquals(oracleX, meeple.getx());
		assertEquals(oracleY, meeple.gety());
	}

}
