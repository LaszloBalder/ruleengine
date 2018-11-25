package eval;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ExpressionDefintion {

	private ArrayList<ExpressionElement> objList = new ArrayList<ExpressionElement>();

	private SortedSet<Operator> operatorSet = new TreeSet<Operator>();

	private Set<String> identifierSet = new TreeSet<String>();
	private Dictionary<String, ArrayList<Integer>> identifierPositions = new Hashtable<String, ArrayList<Integer>>();

	private String expression;

	private int currentPosition = 0;

	public ExpressionDefintion(String expr) {
		expression = expr;
		objList.add(Block.START);
		while (currentPosition < expression.length()) {
			ExpressionElement token = getToken();
			if (token != null) {
				if (Block.START.equals(token)) {
					if (objList.get(objList.size() - 1).getClass() == Identifier.class
							|| objList.get(objList.size() - 1).getClass() == Attribute.class) {
						objList.set(objList.size() - 1, new Function(((Identifier) objList.get(objList.size() - 1))
								.getName()));
					}
				}
				objList.add(token);
				if (token.getClass().getSuperclass() == Operator.class) {
					operatorSet.add((Operator)token);
				}
			}
		}
		objList.add(Block.END);
		for (int i = 0; i<objList.size(); i++) {
			ExpressionElement token = objList.get(i);
			if (token.getClass() == Identifier.class || token.getClass().getSuperclass() == Identifier.class) {
				Identifier identifier = (Identifier) token;
				if (identifierPositions.get(identifier.getName()) == null) {
					identifierPositions.put(identifier.getName(), new ArrayList<Integer>());
				}
				identifierPositions.get(identifier.getName()).add(i);	
			}			
		}
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < objList.size() - 1; i++) {
			buff.append(objList.get(i));
			if (!(objList.get(i).equals(new Character('(')) || objList.get(i + 1).equals(new Character(')')))) {
				buff.append(" ");
			}
		}
		buff.append(objList.get(objList.size() - 1));
		return buff.toString();
	}

	private char getNextChar() {
		currentPosition++;
		if (currentPosition < expression.length())
			return expression.charAt(currentPosition);
		return 0;
	}

	Set<String> getParameterNames() {
		return identifierSet;
	}

	public Dictionary<String, ArrayList<Integer>> getIdentifierPositions() {
		return identifierPositions;
	}

	private char getCurrentChar() {
		if (currentPosition < expression.length())
			return expression.charAt(currentPosition);
		return 0;
	}

	private ExpressionElement getToken() {
		char ch = getCurrentChar();
		while (ch == ' ') {
			ch = getNextChar();
		}
		int startPos = currentPosition;
		if (ch == '(' || ch == ',' || ch == ')') {
			getNextChar();
			return new Block(ch);
		}
		if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=' || ch == '\\' || ch == '%') {
			getNextChar();
			return OperatorFactory.getInstance().getOperator(expression.substring(startPos, currentPosition));
		}
		if (ch == '>' || ch == '<' || ch == '!') {
			ch = getNextChar();
			if (ch == '=') {
				getNextChar();
			}
			return OperatorFactory.getInstance().getOperator(expression.substring(startPos, currentPosition));
		}
		if (Character.isJavaIdentifierStart(ch) || ch == '.') {
			ch = getNextChar();
			while (ch != 0 && (Character.isJavaIdentifierPart(ch)) || ch == '.') {
				ch = getNextChar();
			}
			String token = expression.substring(startPos, currentPosition).toUpperCase();
			if (token.equals("TRUE") || token.equals("FALSE")) {
				return new Bool(token);
			}
			if (token.equals("OR") || token.equals("AND") || token.equals("IN") || token.equals("NOTIN")
					|| token.equals("DIV") || token.equals("MOD")) {
				return OperatorFactory.getInstance().getOperator(token);
			}
			if (token.equals("NULL")) {
				return NullValue.NULL;
			}
			String identifierName = expression.substring(startPos, currentPosition);
			if (identifierName.charAt(0) == '.') {
				return new Function(identifierName);
			}
			if (identifierName.indexOf('.') > 0) {
				return new Attribute(identifierName);
			}
			return new Identifier(identifierName);
		}
		if (Character.isDigit(ch)) {
			ch = getNextChar();
			while (Character.isDigit(ch) || ch == '.') {
				ch = getNextChar();
			}
			return new Number(expression.substring(startPos, currentPosition));
		}
		if (ch == '"') {
			ch = getNextChar();
			while (ch != 0 && ch != '"') {
				ch = getNextChar();
			}
			getNextChar();
			return new Text(expression.substring(startPos + 1, currentPosition - 1));
		}
		if (ch == '\'') {
			ch = getNextChar();
			while (ch != 0 && ch != '\'') {
				ch = getNextChar();
			}
			getNextChar();
			return new Date(expression.substring(startPos + 1, currentPosition - 1));
		}
		return null;
	}
	
	public Object[] getOperators() {
		return  operatorSet.toArray();
	}
	
	public ArrayList<ExpressionElement> getTokenList() {
		return objList;
	}
}
