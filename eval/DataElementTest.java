package eval;

import junit.framework.TestCase;

public class DataElementTest extends TestCase {

	public void testEvaluate() {
		DateTime t0 = new DateTime("2009-09-12");
		DateTime t1 = new DateTime("2009-09-12T12:12:12");
		DateTime t2 = new DateTime("2009-09-12");
		assertTrue(t0.compareTo(t1)==-1);
		assertTrue(t0.compareTo(t2)==0);
	}

}
