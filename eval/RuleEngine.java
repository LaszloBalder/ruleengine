package eval;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import util.StopWatch;

public class RuleEngine {
	private VariableList variableList = new VariableList();

	private Map<String, DecisionTable> decisionTables = new TreeMap<String, DecisionTable>();

	private Map<String, RuleSet> ruleSets = new TreeMap<String, RuleSet>();

	public RuleSet getRuleSet(String ruleSetName) {
		return ruleSets.get(ruleSetName);
	}

	public void load(String fileName) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// factory.setValidating(true);
		// factory.setNamespaceAware(true);
		Document document;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(new File(fileName));
			Element curElem = document.getDocumentElement();
			// RuleEngine ruleEngine = new RuleEngine();
			for (int i = 0; i < curElem.getChildNodes().getLength(); i++) {
				Node node = curElem.getChildNodes().item(i);
				if (node.getNodeName().equals("VariableList")) {
					parseVarList(this, node);
				}
				if (node.getNodeName().equals("FunctionList")) {
					parseFunList(node);
				}
				if (node.getNodeName().equals("DecissionTables")) {
					parseDTables(this, node);
				}
				if (node.getNodeName().equals("RuleSets")) {
					parseRules(this, node);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		RuleEngine ruleEngine = new RuleEngine();
		ruleEngine.load("Repository.xml");
		System.out.println(ruleEngine.getRuleSet("ddddd"));
		RuleSet rs = ruleEngine.ruleSets.get("rs1");
		VariableList vl = new VariableList();
		vl.addVariable("Double", new Number("1.1"));
		StopWatch sw = new StopWatch();
		sw.start();
		for (int i = 0; i < 1000; i++) {
			vl = new VariableList();
			vl.addVariable("Double", new Number("1.1"));
			rs.execute(vl);
		}
		sw.stop();
		// System.out.println(vl.toString());
		System.out.println(sw.getTimeInMillis() + " in millis");
		rs = ruleEngine.ruleSets.get("rs2");
		MyClass mc = new MyClass();
		MyClass2 mc2 = new MyClass2();
		mc.setInt(112);
		mc.setDouble(12.12);
		mc.setLong(12L);
		mc.setMyDate(new java.util.Date());
		VariableList v2 = new VariableList();
		v2.addObject(mc);
		v2.addObject(mc2);

		// printClass(1);
		sw.start();
		// java.util.Date d1 = new java.util.Date();
		// Calendar cal = GregorianCalendar.getInstance();
		// Calendar cal2 = null;

		for (int i = 0; i < 10; i++) {
			v2 = new VariableList();
			v2.addObject(mc);
			mc2 = new MyClass2();
			v2.addObject(mc2);
			rs.execute(v2);
		}
		sw.stop();
		System.out.println(sw.getTimeInMillis() + " millis");
		System.out.println(mc2.getInt());
		System.out.println(mc2.getDouble());
		System.out.println(mc2.getMyDate());
		System.out.println(v2.toString());
		// Logger logger = Logger.getLogger("Laszlo");
		// logger.setLevel(Level.FINEST);
		// logger.addHandler(new ConsoleHandler());
		// logger.info("LSBMethod");
		// logger.info("LSBMethod2");
		DocumentBuilder db = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
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
		// Element trouwDatum = doc.createElement("TrouwDatum");
		Element leeftijd = doc.createElement("Leeftijd");
		// Element naam = doc.createElement("Naam");
		root.appendChild(naam);
		root.appendChild(inkomen);
		inkomen.setTextContent("10000.5");
		root.appendChild(geboorteDatum);
		root.appendChild(leeftijd);
		root.appendChild(getrouwd);
		// root.appendChild(trouwDatum);
		// trouwDatum.appendChild(trouwJaar);
		// trouwDatum.appendChild(trouwMaand);
		// trouwDatum.appendChild(trouwDag);
		trouwJaar.setTextContent("1996");
		trouwMaand.setTextContent("7");
		trouwDag.setTextContent("5");

		// VariableList v3 = new VariableList();
		// v3.addObject(root);
		// v3.addElementType("Root.GeboorteDatum", Date.class);
		// v3.addElementType("Root.TrouwDatum", Date.class, "DatumType");
		// XsdDateType xsdDateType = new XsdDateType("DatumType", "Jaar",
		// "Maand",
		// "Dag");
		//
		// v3.setXsdDateType(xsdDateType);
		// v3.addElementType("Root.Inkomen", Number.class);
		// v3.addElementType("Root.Leeftijd", Number.class);
		// v3.addElementType("Root.Getrouwd", Bool.class);
		// v3.addElementType("Root.Naam", Text.class);
		// v3.addVariable("Vandaag", new Date("2008-04-15"));
		// RuleSet rsx = ruleEngine.ruleSets.get("xml");
		// rsx.execute(v3);
		// System.out.println(v3.toString());
		// System.out.println(naam.getTextContent());
		// System.out.println(inkomen.getTextContent());
		// System.out.println(geboorteDatum.getTextContent());
		// System.out.println(getrouwd.getTextContent());
		// System.out.println(v3.getValue("Root.TrouwDatum"));
		// System.out.println(leeftijd.getTextContent());

		// DecisionTable dt = new DecisionTable("PostCodeFraude");
		// dt = ruleEngine.decisionTables.get("Postcode");
		// for (int i = 1000; i < 10000; i++) {
		// ArrayList<? extends DataElement> details = new
		// ArrayList<DataElement>();
		// details.add(new Number(Integer.toString(i));
		// dt.addRow(row)
		// }
	}

	private static void parseRules(RuleEngine ruleEngine, Node node) {
		for (int j = 0; j < node.getChildNodes().getLength(); j++) {
			Node set = node.getChildNodes().item(j);
			if (!set.getNodeName().equals("RuleSet"))
				continue;
			String name = set.getAttributes().getNamedItem("name")
					.getNodeValue();
			RuleSet rSet = new RuleSet(name);
			ruleEngine.ruleSets.put(name, rSet);
			for (int k = 0; k < set.getChildNodes().getLength(); k++) {
				Node ruleNode = set.getChildNodes().item(k);
				if (!ruleNode.getNodeName().equals("Rule"))
					continue;
				String ruleCondition = ruleNode.getAttributes().getNamedItem(
						"condition").getNodeValue();
				String ruleName = null;
				if (ruleNode.getAttributes().getNamedItem("name") != null) {
					ruleName = ruleNode.getAttributes().getNamedItem("name")
							.getNodeValue();
				}
				Rule rule = new Rule(ruleCondition);
				for (int l = 0; l < ruleNode.getChildNodes().getLength(); l++) {
					Node actionNode = ruleNode.getChildNodes().item(l);
					if (!(actionNode.getNodeName().equals("Action") || actionNode
							.getNodeName().equals("ElseAction")))
						continue;
					String actionType = actionNode.getAttributes()
							.getNamedItem("type").getNodeValue();
					Action action;
					if (actionType.equals("assign")) {
						String assignTo = actionNode.getAttributes()
								.getNamedItem("assignTo").getNodeValue();
						String expression = actionNode.getAttributes()
								.getNamedItem("expression").getNodeValue();
						action = new AssignAction(assignTo, expression);
					} else if (actionType.equals("execute")) {
						String expression = actionNode.getAttributes()
								.getNamedItem("expression").getNodeValue();
						action = new ExecuteAction(expression);
					} else if (actionType.equals("evaluate")) {
						String decissionTable = actionNode.getAttributes()
								.getNamedItem("decissionTable").getNodeValue();
						action = new EvaluateAction(ruleEngine.decisionTables
								.get(decissionTable));
					} else if (actionType.equals("rule")) {
						String ruleActioName = actionNode.getAttributes()
								.getNamedItem("ruleName").getNodeValue();
						action = new RuleAction(rSet.getRule(ruleActioName));
					} else { // addToBag
						String bagType = actionNode.getAttributes()
								.getNamedItem("bagType").getNodeValue();
						String expression = actionNode.getAttributes()
								.getNamedItem("expression").getNodeValue();
						action = new AddToBagAction(bagType, expression);
					}
					if (actionNode.getNodeName().equals("Action")) {
						rule.addAction(action);
					} else {
						rule.addElseAction(action);
					}
				}
				rSet.addRule(rule, ruleName);
			}
		}
	}

	@SuppressWarnings("all")
	private static void parseDTables(RuleEngine ruleEngine, Node node)
			throws InstantiationException, IllegalAccessException,
			InvocationTargetException {
		for (int j = 0; j < node.getChildNodes().getLength(); j++) {
			Node table = node.getChildNodes().item(j);
			if (!table.getNodeName().equals("DecissionTable"))
				continue;
			String name = table.getAttributes().getNamedItem("name")
					.getNodeValue();
			DecisionTable dt = new DecisionTable(name);
			ruleEngine.decisionTables.put(name, dt);
			ArrayList<String> conds = new ArrayList<String>(10);
			ArrayList<String> results = new ArrayList<String>(10);
			for (int k = 0; k < table.getChildNodes().getLength(); k++) {
				Node def = table.getChildNodes().item(k);
				if (def.getNodeName().equals("RowDefinition")) {
					for (int l = 0; l < def.getChildNodes().getLength(); l++) {
						Node row = def.getChildNodes().item(l);
						if (row.getNodeName().equals("Column")) {
							name = row.getAttributes().getNamedItem(
									"variableName").getNodeValue();
							int position = Integer.parseInt(row.getAttributes()
									.getNamedItem("position").getNodeValue());
							String type = row.getAttributes().getNamedItem(
									"type").getNodeValue();
							if (type.equals("condition")) {
								conds.add(name);
							} else {
								results.add(name);
							}
						}
					}
					dt.setCondition(conds);
					dt.setResult(results);
				} else if (def.getNodeName().equals("Rows")) {
					for (int l = 0; l < def.getChildNodes().getLength(); l++) {
						Node rows = def.getChildNodes().item(l);
						ArrayList rowList = new ArrayList();
						if (rows.getNodeName().equals("Row")) {
							for (int m = 0; m < rows.getChildNodes()
									.getLength(); m++) {
								Node cell = rows.getChildNodes().item(m);
								if (cell.getNodeName().equals("Cell")) {
									int column = 0;
									String upper = "";
									String lower = "";
									String condType = "";
									String value = "";
									for (int x = 0; x < cell.getAttributes()
											.getLength(); x++) {
										String attr = cell.getAttributes()
												.item(x).getNodeName();
										String val = cell.getAttributes().item(
												x).getNodeValue();
										if (attr.equals("upper")) {
											upper = val;
										} else if (attr.equals("value")) {
											value = val;
										} else if (attr.equals("lower")) {
											lower = val;
										} else if (attr.equals("conditionType")) {
											condType = val;
										} else if (attr.equals("column")) {
											column = Integer.parseInt(val);
										}
									}

									Constructor constr = ruleEngine.variableList
											.getVariableConstructor(dt
													.getColumnName(column - 1));
									if (constr != null) {
										if (!lower.isEmpty()) {
											if (!upper.isEmpty()) {
												rowList
														.add(new ConditionElement(
																(DataElement) constr
																		.newInstance(lower),
																(DataElement) constr
																		.newInstance(upper)));
											} else {
												rowList
														.add(new ConditionElement(
																condType,
																(DataElement) constr
																		.newInstance(lower)));
											}
										} else if (!upper.isEmpty()) {
											rowList
													.add(new ConditionElement(
															condType,
															(DataElement) constr
																	.newInstance(upper)));
										} else {
											rowList.add(constr
													.newInstance(value));
										}
									}
								}
							}
							dt.addRow(rowList);
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("all")
	private static void parseFunList(Node node) {
		for (int j = 0; j < node.getChildNodes().getLength(); j++) {
			Node fun = node.getChildNodes().item(j);
			if (!fun.getNodeName().equals("Function"))
				continue;
			String name = fun.getAttributes().getNamedItem("name")
					.getNodeValue();
			String qname = fun.getAttributes().getNamedItem("qname")
					.getNodeValue();
			String rtype = fun.getAttributes().getNamedItem("rtype")
					.getNodeValue();
			FunctionDefinition f = new FunctionDefinition(name, qname);
			FunctionList.getInstance().addFunction(f);
			for (int k = 0; k < fun.getChildNodes().getLength(); k++) {
				Node param = fun.getChildNodes().item(k);
				if (!param.getNodeName().equals("Parameter"))
					continue;
				String pname = param.getAttributes().getNamedItem("name")
						.getNodeValue();
				String ptype = param.getAttributes().getNamedItem("type")
						.getNodeValue();
				f.addParameter(ptype);
			}
		}
	}

	private static void parseVarList(RuleEngine ruleEngine, Node node) {
		for (int j = 0; j < node.getChildNodes().getLength(); j++) {
			Node var = node.getChildNodes().item(j);
			if (!var.getNodeName().equals("Variable"))
				continue;
			String name = var.getAttributes().getNamedItem("name")
					.getNodeValue();
			String type = var.getAttributes().getNamedItem("type")
					.getNodeValue();
			String defaultValue = var.getAttributes().getNamedItem(
					"defaultValue").getNodeValue();
			Class<? extends DataElement> varType = null;
			if (type.equals("Number"))
				varType = Number.class;
			if (type.equals("Date"))
				varType = Date.class;
			if (type.equals("Text"))
				varType = Text.class;
			if (type.equals("Bool"))
				varType = Bool.class;
			if (varType != null)
				ruleEngine.variableList
						.addVariable(name, varType, defaultValue);
		}
	}
}

class MyClass {
	private int _int;
	private java.util.Date myDate;
	private double _double;
	private long _long;
	private String _text;

	public int getInt() {
		return _int;
	}

	public void setInt(int _int) {
		this._int = _int;
	}

	public double getDouble() {
		return _double;
	}

	public void setDouble(double _double) {
		this._double = _double;
	}

	public long getLong() {
		return _long;
	}

	public void setLong(long _long) {
		this._long = _long;
	}

	public java.util.Date getMyDate() {
		return myDate;
	}

	public void setMyDate(java.util.Date myDate) {
		this.myDate = myDate;
	}

	public void setText(String _text) {
		this._text = _text;
	}

	public String getText() {
		return _text;
	}
}

class MyClass2 extends MyClass {
}
