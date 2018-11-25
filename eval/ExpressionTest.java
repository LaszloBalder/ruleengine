package eval;

import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.SmallDate;
import util.StopWatch;

public class ExpressionTest extends TestCase {

	public void testEvaluate() {
		StopWatch sw = new StopWatch();
		sw.start();
		ExpressionDefintion ed = new ExpressionDefintion("Var1 + Var1 = 2");
		Expression exp = new Expression(ed);
		exp.setParameter("Var1", new Number("1"));
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("true = !false");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("true != false");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("true != !true");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("true and true= true");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("(true AND  true) = true");
		exp = new Expression(ed);
		//System.out.println(exp.evaluate().toString());
		ed = new ExpressionDefintion("(true AND  true) = true");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("'2006-12-01' = '2006-12-01'");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("'2006-12-02' > '2006-12-01'");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("'2006-12-01' < '2006-12-02'");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("'14-01-2006' = '14-01-2006'");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("'14-01-2006' = '15-01-2006'");
		exp = new Expression(ed);
		assertFalse(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("'15-01-2006' > '14-01-2006'");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("'14-01-2006' < '15-01-2006'");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("'15-01-2006' < '14-01-2006'");
		exp = new Expression(ed);
		assertFalse(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("'14-01-2006' > '15-01-2006'");
		exp = new Expression(ed);
		assertFalse(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("9\\2=4");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("17 mod 10=7");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("9 div 2=4");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("17 % 10=7");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("2*2=4");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("5-4=1");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("5>4");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("4<5");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("4<=4");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("4>=4");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("3-4 = -1");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("1=1 or 1=2");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("0 in (1,2,3)");
		exp = new Expression(ed);
		assertFalse(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("2 in (1,2,3)");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("0 notin (1,2,3)");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("2 notin (1,2,3)");
		exp = new Expression(ed);
		assertFalse(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("true in (0 notin (1,2,3))");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("3 - 1 + 6 / 2 * 3 = 11");
		// Hoe moeten we van de onvoldoendes afkomen
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("-3 * -3 = 9");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion(
				"3 - 1 + 6 / 2 * 3 = 9 + (1 + (6 / (2 * 3)))");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("\"Laszlo\".getLength() = 6");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		VariableList vlist = new VariableList();
		assertTrue(Boolean.parseBoolean(exp.evaluate(vlist).toString()));
		ed = new ExpressionDefintion("'2008-03-04'.getYear() = 2008");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("'2008-03-04'.getMonth() = 3");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("'2008-03-04'.getDay() = 4");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("Math.round(1.5) = 2");
		exp = new Expression(ed);
		FunctionDefinition fd;
		fd = new FunctionDefinition("round", "java.lang.Math");
		fd.addParameter("double");
		FunctionList.getInstance().addFunction(fd);
		fd = new FunctionDefinition("random", "java.lang.Math");
		FunctionList.getInstance().addFunction(fd);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion(
				"SmallDate.dateDiff(\"y\", '14-01-1965', '19-06-2008') = 43");
		exp = new Expression(ed);
		fd = new FunctionDefinition("util.SmallDate.dateDiff");
		fd.addParameter("java.lang.String");
		fd.addParameter("util.SmallDate");
		fd.addParameter("util.SmallDate");
		FunctionList.getInstance().addFunction(fd);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("Math.round(Math.random()) in (0, 1)");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		fd = new FunctionDefinition("SmallDate.getDayNumber", "util");
		FunctionList.getInstance().addFunction(fd);
		VariableList vl = new VariableList();
		vl.addObject(new SmallDate(2008, 6, 19));
		ed = new ExpressionDefintion("'2008-03-04'.addYears(5) = '2013-03-04'");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("'2008-03-04'.addMonths(5) = '2008-08-04'");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("'2008-03-04'.addDays(50) = '2008-04-23'");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion(
				"'2008-03-04'.addDays(100-150).addDays(25 + 25) = '2008-03-04'");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion(
				"Var1.addDays(100-150).addDays(25 + 25) = Var1");
		exp = new Expression(ed);
		vl = new VariableList();
		vl.addVariable("Var1", new Date("2008-04-10"));
		assertTrue(Boolean.parseBoolean(exp.evaluate(vl).toString()));
		ed = new ExpressionDefintion("Naam.getLength() = 6");
		exp = new Expression(ed);
		vl.addVariable("Naam", new Text("Laszlo"));
		assertTrue(Boolean.parseBoolean(exp.evaluate(vl).toString()));
		ed = new ExpressionDefintion("MyClass.setLong(4)");
		exp = new Expression(ed);
		fd = new FunctionDefinition("eval.MyClass.setLong");
		fd.addParameter("long");
		FunctionList.getInstance().addFunction(fd);
		MyClass mc = new MyClass();
		mc.setLong(2);
		vl.addObject(mc);
		assertNull(exp.evaluate(vl));
		assertTrue(mc.getLong() == 4);
		mc.setText("Laszlo");
		ed = new ExpressionDefintion("MyClass.Text.getLength() = 6");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate(vl).toString()));

		sw.stop();
		// System.out.println(sw.getTimeInMillis());
	}
	
	public void testEarlyBailOut() {
		ExpressionDefintion ed;
		Expression exp;
		ed = new ExpressionDefintion("(false and 1/0) = false");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));
		ed = new ExpressionDefintion("(true or 1/0) = true");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));		
		ed = new ExpressionDefintion("(1/0 = 1/0) = true");
		exp = new Expression(ed);
		assertTrue(Boolean.parseBoolean(exp.evaluate().toString()));		
	}

	public void testXmlNodes() {
		DocumentBuilder db = null;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = db.newDocument();
		Element root = doc.createElement("Root");
		doc.appendChild(root);
		Element naam = doc.createElement("Naam");
		Element inkomen = doc.createElement("Inkomen");
		Element geboorteDatum = doc.createElement("GeboorteDatum");
		Element getrouwd = doc.createElement("Getrouwd");
		Element trouwJaar = doc.createElement("Jaar");
		Element trouwMaand = doc.createElement("Maand");
		Element trouwDag = doc.createElement("Dag");
		Element trouwDatum = doc.createElement("TrouwDatum");
		Element leeftijd = doc.createElement("Leeftijd");
		root.appendChild(naam);
		root.appendChild(inkomen);
		inkomen.setTextContent("10000.5");
		root.appendChild(geboorteDatum);
		root.appendChild(leeftijd);
		root.appendChild(getrouwd);
		root.appendChild(trouwDatum);
		trouwDatum.appendChild(trouwJaar);
		trouwDatum.appendChild(trouwMaand);
		trouwDatum.appendChild(trouwDag);
		trouwJaar.setTextContent("1996");
		trouwMaand.setTextContent("7");
		trouwDag.setTextContent("5");

		VariableList v3 = new VariableList();
		v3.addObject(root);
		ComplexType ct = new ComplexType("Root");
		Xsd xsd = new Xsd();
		ct.addAttribute("GeboorteDatum", Date.class, "date", null, 1);
		ct.addAttribute("TrouwDatum", Date.class, "DatumType", "DatumType", 1);
		ct.addAttribute("Inkomen", Number.class, "decimal", null, 1);
		ct.addAttribute("Leeftijd", Number.class, "int", null, 1);
		ct.addAttribute("Getrouwd", Bool.class, "boolean", null, 1);
		ct.addAttribute("Naam", Text.class, "string", null, 1);
		ct.addAttribute("AchterNaam", Text.class, "string", null, 1);
		v3.addVariable("Vandaag", new Date("2008-04-15"));
		XsdDateType dt = new XsdDateType("DatumType", "Jaar", "Maand", "Dag");
		xsd.setXsdDateType(dt);
		xsd.addComplexType(ct);
		v3.setXsd(xsd);

		RuleSet rsx = new RuleSet("test");

		Rule r = new Rule();
		rsx.addRule(r);
		r.addAction(new AssignAction("Root.Naam", "\"Laszlo\""));
		r.addAction(new AssignAction("Root.Naam", "Root.Naam + \" Balder\""));
		r.addAction(new AssignAction("Root.AchterNaam", "\"Balder\""));
		r.addAction(new AssignAction("Root.Getrouwd", "true"));
		r.addAction(new AssignAction("Root.GeboorteDatum", "'1965-01-14'"));
		r.addAction(new AssignAction("Root.TrouwDatum", "Root.GeboorteDatum"));
		r.addAction(new AssignAction("Vandaag", "Root.GeboorteDatum"));
		r.addAction(new AssignAction("Root.Inkomen", "Root.Inkomen+53000"));
		r.addAction(new AssignAction("Root.Leeftijd", "12"));
		r = new Rule("Root.GeboorteDatum != null");
		r.addAction(new AssignAction("Root.GeboorteDatum", "'1968-04-26'"));
		rsx.addRule(r);
		rsx.execute(v3);

		serialize(doc, System.out);
		//System.out.println(geboorteDatum.getTextContent().toString());
		//assertTrue(geboorteDatum.getTextContent().toString().equals("1968-04-26"));
		assertTrue(inkomen.getTextContent().toString().equals("63000.5"));
		
		TestObject test = new TestObject();
		test.setTest(null);
		VariableList v4 = new VariableList();
		v4.addObject(test);
		RuleSet rsx2 = new RuleSet("test");

		r = new Rule("TestObject.Test.getLength() = 6");
		r.addAction(new AssignAction("TestObject.Test", "\"Joepie de poepie\""));
		r.addElseAction(new AssignAction("TestObject.Test", "\"Joepie de poepie\""));
		rsx2.addRule(r);
		rsx2.execute(v4);
		assertTrue(test.getTest().equals("Joepie de poepie"));
	}

	public static void serialize(Document doc, OutputStream out) {

		TransformerFactory tfactory = TransformerFactory.newInstance();
		Transformer serializer;
		try {
			serializer = tfactory.newTransformer();
			// Setup indenting to "pretty print"
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			serializer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "3");

			serializer.transform(new DOMSource(doc), new StreamResult(out));
		} catch (TransformerException e) {
			// this is fatal, just dump the stack
			e.printStackTrace();

		}
	}
}

class TestObject {
	private String test = null;
	public void setTest(String test) {
		this.test = test;
	}
	public String getTest() {
		return test;
	}
}