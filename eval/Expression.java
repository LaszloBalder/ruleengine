package eval;

import java.util.ArrayList;

public class Expression {

	ArrayList<ExpressionElement> objList;

	private ExpressionDefintion definition;
	private ArrayList<Identifier> openParameters = null;

	// private Object[] operSet;

	@SuppressWarnings("unchecked")
	public Expression(ExpressionDefintion def) {
		this.definition = def;
		objList = (ArrayList<ExpressionElement>) definition.getTokenList()
				.clone();
		// operSet = definition.operatorSet.toArray(? extends Operator);
		// operSet = definition.getOperators();
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < objList.size() - 1; i++) {
			buff.append(objList.get(i));
			if (!(objList.get(i).equals(new Character('(')) || objList.get(
					i + 1).equals(new Character(')')))) {
				buff.append(" ");
			}
		}
		buff.append(objList.get(objList.size() - 1));
		return buff.toString();
	}

	ArrayList<Identifier> getParameters() {
		if (openParameters == null) {
			openParameters = new ArrayList<Identifier>();
			for (int i = 0; i < objList.size(); i++) {
				if (objList.get(i).getClass() == Identifier.class
						|| objList.get(i).getClass() == Attribute.class) {
					openParameters.add((Identifier) objList.get(i));
				}
			}
		}
		return openParameters;
	}

	void setParameter(String name, DataElement value) {
		ArrayList<Integer> positions = definition.getIdentifierPositions().get(
				name);
		for (int i = 0; i < positions.size(); i++) {
			objList.set(positions.get(i).intValue(), value);
		}
	}

	void resolveList(int firstBlockStart) {
		ArrayList<DataElement> arr = new ArrayList<DataElement>();
		int cnt = 0;
		ExpressionElement curEl = (ExpressionElement) objList
				.get(firstBlockStart);
		while (!curEl.equals(Block.END)) {
			cnt++;
			if (curEl.getClass() != Block.class) {
				arr.add((DataElement) curEl);
			}
			curEl = (ExpressionElement) objList.get(firstBlockStart + cnt);
		}
		objList.set(firstBlockStart, new List(arr));
		while (cnt > 0) {
			objList.remove(firstBlockStart + 1);
			cnt--;
		}
		return;
	}

	DataElement evaluate() {
		return evaluate(null);
	}

	DataElement evaluate(VariableList varList) {
		if (varList != null && !getParameters().isEmpty()) {
			for (int i = 0; i < getParameters().size(); i++) {
				setParameter(getParameters().get(i).getName(), varList
						.getValue(getParameters().get(i).getName()));
			}
		}
		int firstBlockStart = getInnerBlock();
		int cnt = 0;
		while (firstBlockStart > 0 && cnt++ < 50) {
			int end = getInnerBlockEnd(firstBlockStart);
			evaluateBlock(firstBlockStart + 1, end - 1);
			if ((objList.get(firstBlockStart - 1).getClass() != Function.class
					&& objList.get(firstBlockStart - 1).getClass() != InOperator.class && objList
					.get(firstBlockStart - 1).getClass() != NotInOperator.class)
					&& objList.get(firstBlockStart + 2).equals(Block.END)
					&& !objList.get(firstBlockStart + 1).equals(Block.END)) {
				// value surrounded by ( )
				objList.remove(firstBlockStart + 2);
				objList.remove(firstBlockStart);
			} else { // we got a list of elements (,,,)
				resolveList(firstBlockStart);
				if (objList.get(firstBlockStart - 1).getClass() == Function.class) {
					evaluateFunction(firstBlockStart - 1, varList);
				}
			}
			firstBlockStart = getInnerBlock();
		}
		if (objList.size() > 3) {
			evaluateBlock(0, objList.size() - 1);
		}
		if (objList.size() > 2) {
			return (DataElement) objList.get(1);
		}
		return null;
	}

	private void evaluateFunction(int position, VariableList varList) {
		String funName = ((Function) objList.get(position)).getName();
		ExpressionElement target = null;// objList.get(position -1);
		FunctionDefinition ef = FunctionList.getInstance().getFunction(funName);
		boolean onLiteral = false;
		if (ef == null && funName.charAt(0) != '.' && funName.indexOf('.') > 0) {
			onLiteral = true;
			String targetName = funName.substring(0, funName.lastIndexOf('.'));
			funName = funName.substring(funName.lastIndexOf('.'));
			target = varList.getValue(targetName);
		}
		if (ef == null && funName.charAt(0) == '.') {
			onLiteral = !onLiteral;
			if (target == null) {
				target = objList.get(position - 1);
			}
			String className = target.getClass().getName();
			ef = new FunctionDefinition(className + funName);
			List list = (List) objList.get(position + 1);
			if (list.value.size() > 0) {
				ef.addParameter("int");
			}
		}
		List list = (List) objList.get(position + 1);

		objList.set(position, ef.execute(target, list.value, varList));

		if (onLiteral) {
			if (objList.get(position) == null) {
				objList.remove(position - 1);
				objList.remove(position - 1);
				objList.remove(position - 1);
			} else {
				objList.remove(position + 1);
				objList.remove(position - 1);
			}
		} else {
			if (objList.get(position) == null) {
				objList.remove(position);
				objList.remove(position);
			} else {
				objList.remove(position + 1);
			}
		}
	}

	private int getInnerBlock() {
		int lastBlockStart = 0;
		for (int i = 0; i < objList.size(); i++) {
			if (objList.get(i).equals(Block.START))
				lastBlockStart = i;
			if (objList.get(i).equals(Block.END) && i > 1
					&& !objList.get(i - 2).equals(Block.START))
				return lastBlockStart;
		}
		return -1;
	}

	private int getInnerBlockEnd(int blockStart) {
		for (int i = blockStart; i < objList.size(); i++) {
			if (objList.get(i).equals(Block.END))
				return i;
		}
		return 0;
	}


	private void evaluateBlock(int start, int end) {
		// implements Hoe moeten wij van de onvoldoendes afkomen
		// go through the block looking for unary operators
		boolean[] catagories = { false, false, false, false, false };
		for (int x = start; x < end; x++) {
			if (objList.get(x).getClass().getSuperclass() == Operator.class) {
				Operator operator = (Operator) objList.get(x);
				ExpressionElement prevElem = objList.get(x - 1);
				if (prevElem.getClass().getSuperclass() == Operator.class
						|| prevElem.getClass() == Block.class) {
					ExpressionElement result = operator
							.evaluate((DataElement) objList.get(x + 1));
					if (result != null) {
						objList.set(x, result);
						objList.remove(x + 1);
						end--;
					}
				} else {
					catagories[operator.getCatagory() - 1] = true;
				}
			}
		}
		int catagory = 1;
		while (end - start > 1 && catagory <= 5) {
			if (!catagories[catagory - 1]) {
				catagory++;
				continue;
			}
			// go through the block looking for binary operators
			for (int x = start; x < end; x++) {
				if (objList.get(x).getClass().getSuperclass() == Operator.class) {
					Operator operator = (Operator) objList.get(x);
					if (operator.getCatagory() == catagory) {
						DataElement result = operator.evaluate(
								(DataElement) objList.get(x - 1),
								(DataElement) objList.get(x + 1));
						if (result != null) {
							objList.set(x - 1, result);
							objList.remove(x);
							objList.remove(x);
							end -= 2;
							x--;
						} else {
							System.out.println("no result");
						}
					}
				}
			}
			catagory++;
		}

	}
}
