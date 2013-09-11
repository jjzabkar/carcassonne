package model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BoardTest.class, DrawPileTest.class, MeepleTest.class,
		PlayerTest.class, TileTest.class })
public class AllTests {
}
