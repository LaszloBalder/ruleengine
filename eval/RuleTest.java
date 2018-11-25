package eval;

import junit.framework.TestCase;

public class RuleTest extends TestCase {

	public void testExecute() {
		Rule r = new Rule("Naam = \"Laszlo\"");
		r.addAction(new AssignAction("Executed", "true"));
		VariableList vl = new VariableList();
		vl.addVariable("Naam", new Text("Laszlo"));
		r.execute(vl);
		assertTrue(vl.getValue("Executed").equals(new Bool("true")));
		r = new Rule("Naam + \" Balder\" = \"Laszlo Balder\"");
		vl = new VariableList();
		vl.addVariable("Naam", new Text("Laszlo"));
		r.addAction(new AssignAction("Executed", "true"));
		r.execute(vl);
		assertTrue(vl.getValue("Executed").equals(new Bool("true")));
	}
}
