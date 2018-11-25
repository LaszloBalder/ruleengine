package eval;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Dictionary;
import java.util.Hashtable;

abstract class Operator extends ExpressionElement implements
		Comparable<Operator> {
	protected String operator = "";
	protected boolean binary = true;
	protected boolean unary = false;
	private int catagory = 0;
	public static final int MultiplyDivide = 1;
	public static final int AddSubstract = 2;
	public static final int Compare = 3;
	public static final int LogicalAnd = 4;
	public static final int LogicalOr = 5;

	protected int priority = 0;

	public Operator(int catagory) {
		this.catagory = catagory;
	}

	public DataElement evaluate(DataElement rhs) {
		return new Text(rhs.toString());
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() == NullValue.class || rhs.getClass() != List.class) {
			return NullValue.NULL;
		}
		return new Text(lhs.toString() + operator + rhs.toString());
	}

	@Override
	public String toString() {
		return operator;
	}

	protected void error(DataElement lhs, DataElement rhs) throws Exception {
		throw new Exception("Not implemented for " + lhs != null ? lhs
				.toString()
				+ " " : " " + operator + " " + rhs.toString());
	}

	public int compareTo(Operator oper) {
		return priority - oper.priority;
	}

	public boolean isUnary() {
		return unary;
	}

	public boolean isBinary() {
		return binary;
	}

	public int getCatagory() {
		return catagory;
	}
}

class OperatorFactory {
	private static OperatorFactory instance = null;
	private Dictionary<String, Operator> operators = new Hashtable<String, Operator>();

	private OperatorFactory() {
		operators.put("=", new EqualOperator());
		operators.put("!", new NotOperator());
		operators.put("!=", new NotEqualOperator());
		operators.put(">", new GreaterOperator());
		operators.put(">=", new GreaterEqualOperator());
		operators.put("<", new LessOperator());
		operators.put("<=", new LessEqualOperator());
		operators.put("AND", new AndOperator());
		operators.put("OR", new OrOperator());
		operators.put("NOTIN", new NotInOperator());
		operators.put("IN", new InOperator());
		operators.put("+", new PlusOperator());
		operators.put("-", new MinusOperator());
		operators.put("/", new DivideOperator());
		operators.put("*", new MultiplyOperator());
		operators.put("%", new ModulusOperator());
		operators.put("MOD", new ModulusOperator());
		operators.put("\\", new DivIntOperator());
		operators.put("DIV", new DivIntOperator());
	}

	public Operator getOperator(String operator) {
		return operators.get(operator);
	}

	public static OperatorFactory getInstance() {
		if (instance == null) {
			instance = new OperatorFactory();
		}
		return instance;
	}
}

class EqualOperator extends Operator {
	public EqualOperator() {
		super(Operator.Compare);
		this.operator = "=";
		this.priority = 1000;
	}

	public DataElement evaluate(DataElement rhs) {
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
//		if (lhs.getClass() == NullValue.class
//				|| rhs.getClass() == NullValue.class) {
//			return Bool.FALSE;
//		} else {
			return new Bool(lhs.equals(rhs));
//		}
	}
}

class NotOperator extends Operator {
	public NotOperator() {
		super(Operator.Compare);
		this.operator = "!";
		this.priority = 101;
		this.unary = true;
		this.binary = false;
	}

	public DataElement evaluate(DataElement rhs) {
		Bool b = (Bool) rhs;
		return new Bool(!b.value);
	}
}

class NotEqualOperator extends Operator {
	public NotEqualOperator() {
		super(Operator.Compare);
		this.operator = "!=";
		this.priority = 900;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() == NullValue.class
				|| rhs.getClass() == NullValue.class) {
			return Bool.FALSE;
		} else {
			return new Bool(!lhs.equals(rhs));
		}
	}
}

class GreaterOperator extends Operator {
	public GreaterOperator() {
		super(Operator.Compare);
		this.operator = ">";
		this.priority = 1001;
	}

	public DataElement evaluate(DataElement rhs) {
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() == NullValue.class
				|| rhs.getClass() == NullValue.class) {
			return Bool.FALSE;
		} else {
			return new Bool(lhs.compareTo(rhs) > 0);
		}
	}
}

class GreaterEqualOperator extends Operator {
	public GreaterEqualOperator() {
		super(Operator.Compare);
		this.operator = ">=";
		this.priority = 1002;
	}

	public DataElement evaluate(DataElement rhs) {
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() == NullValue.class
				|| rhs.getClass() == NullValue.class) {
			return Bool.FALSE;
		} else {
			return new Bool(lhs.compareTo(rhs) >= 0);
		}
	}
}

class LessOperator extends Operator {
	public LessOperator() {
		super(Operator.Compare);
		this.operator = "<";
		this.priority = 1003;
	}

	public DataElement evaluate(DataElement rhs) {
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() == NullValue.class
				|| rhs.getClass() == NullValue.class) {
			return Bool.FALSE;
		} else {
			return new Bool(lhs.compareTo(rhs) < 0);
		}
	}
}

class LessEqualOperator extends Operator {
	public LessEqualOperator() {
		super(Operator.Compare);
		this.operator = "<=";
		this.priority = 1004;
	}

	public DataElement evaluate(DataElement rhs) {
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() == NullValue.class
				|| rhs.getClass() == NullValue.class) {
			return Bool.FALSE;
		} else {
			return new Bool(lhs.compareTo(rhs) <= 0);
		}
	}
}

class OrOperator extends Operator {
	public OrOperator() {
		super(Operator.LogicalOr);
		this.operator = "OR";
		this.priority = 1100;
	}

	public DataElement evaluate(DataElement rhs) {
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() == Bool.class) {
			if (((Bool) lhs).value == true) {
				return lhs;
			}
			if (rhs.getClass() == Bool.class) {
				return new Bool(((Bool) lhs).value || ((Bool) rhs).value);
			}
		}
		return Bool.FALSE;
	}
}

class AndOperator extends Operator {
	public AndOperator() {
		super(Operator.LogicalAnd);
		this.operator = "AND";
		this.priority = 1200;
	}

	public DataElement evaluate(DataElement rhs) {
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.equals(Bool.FALSE)) {
			return lhs;
		} else if (lhs.getClass() == Bool.class && rhs.getClass() == Bool.class) {
			return new Bool(((Bool) lhs).value && ((Bool) rhs).value);
		}
		return Bool.FALSE;
	}
}

class PlusOperator extends Operator {
	public PlusOperator() {
		super(Operator.AddSubstract);
		this.operator = "+";
		this.priority = 300;
		this.unary = true;
	}

	public DataElement evaluate(DataElement rhs) {
		if (rhs.getClass() == Number.class) {
			return rhs;
		}
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() == Number.class && rhs.getClass() == Number.class)
			return new Number(((Number) lhs).value.add(((Number) rhs).value));
		else if (lhs.getClass() == Text.class || rhs.getClass() == Text.class)
			return new Text(lhs.toString() + rhs.toString());
		return super.evaluate(lhs, rhs);
	}
}

class MinusOperator extends Operator {
	public MinusOperator() {
		super(Operator.AddSubstract);
		this.operator = "-";
		this.priority = 400;
		this.unary = true;
	}

	public DataElement evaluate(DataElement rhs) {
		if (rhs.getClass() == Number.class) {
			Number rhsD = (Number) rhs;
			return new Number(BigDecimal.ZERO.subtract(rhsD.value));
		}
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() != Number.class || rhs.getClass() != Number.class)
			return super.evaluate(lhs, rhs);
		return new Number(((Number) lhs).value.subtract(((Number) rhs).value));
	}
}

class DivideOperator extends Operator {
	public DivideOperator() {
		super(Operator.MultiplyDivide);
		this.operator = "/";
		this.priority = 200;
	}

	public DataElement evaluate(DataElement rhs) {
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() != Number.class || rhs.getClass() != Number.class) {
			return super.evaluate(lhs, rhs);
		}
		if (((Number) rhs).value.equals(BigDecimal.ZERO)) {
			return NullValue.NULL;
		}
		return new Number(((Number) lhs).value.divide(((Number) rhs).value,
				MathContext.UNLIMITED));
	}
}

class ModulusOperator extends Operator {
	public ModulusOperator() {
		super(Operator.MultiplyDivide);
		this.operator = "%";
		this.priority = 150;
	}

	public DataElement evaluate(DataElement rhs) {
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() != Number.class || rhs.getClass() != Number.class)
			return super.evaluate(lhs, rhs);
		BigDecimal right = ((Number) rhs).value;
		if (right.equals(BigDecimal.ZERO)) {
			return NullValue.NULL;
		}
		BigDecimal left = ((Number) lhs).value;
		BigDecimal remainder = left.remainder(right, MathContext.UNLIMITED);
		return new Number(remainder);
	}
}

class DivIntOperator extends Operator {
	public DivIntOperator() {
		super(Operator.MultiplyDivide);
		this.operator = "DIV";
		this.priority = 175;
	}

	public DataElement evaluate(DataElement rhs) {
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() != Number.class || rhs.getClass() != Number.class)
			return super.evaluate(lhs, rhs);
		BigDecimal right = ((Number) rhs).value;
		if (right.equals(BigDecimal.ZERO)) {
			return NullValue.NULL;
		}
		BigDecimal left = ((Number) lhs).value;
		BigDecimal divInt = left.divideToIntegralValue(right,
				MathContext.UNLIMITED);
		return new Number(divInt);
	}
}

class MultiplyOperator extends Operator {
	public MultiplyOperator() {
		super(Operator.MultiplyDivide);
		this.operator = "*";
		this.priority = 100;
	}

	public DataElement evaluate(DataElement rhs) {
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() != Number.class || rhs.getClass() != Number.class)
			return super.evaluate(lhs, rhs);
		return new Number(((Number) lhs).value.multiply(((Number) rhs).value));
	}
}

class InOperator extends Operator {
	public InOperator() {
		super(Operator.Compare);
		this.operator = "IN";
		this.priority = 1050;
	}

	public DataElement evaluate(DataElement rhs) {
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() == NullValue.class || rhs.getClass() != List.class) {
			return Bool.FALSE;
		}
		List list = (List) rhs;
		return new Bool(list.value.contains(lhs));
	}
}

class NotInOperator extends Operator {
	public NotInOperator() {
		super(Operator.Compare);
		this.operator = "NOTIN";
		this.priority = 1051;
	}

	public DataElement evaluate(DataElement rhs) {
		return null;
	}

	public DataElement evaluate(DataElement lhs, DataElement rhs) {
		if (lhs.getClass() == NullValue.class || rhs.getClass() != List.class) {
			return Bool.FALSE;
		}
		List list = (List) rhs;
		return new Bool(!list.value.contains(lhs));
	}
}
