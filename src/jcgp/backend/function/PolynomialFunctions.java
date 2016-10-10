package jcgp.backend.function;


/**
 * This class contains all symbolic regression functions
 * (defined as double functions in the classic CGP implementation)
 * in static nested classes.
 * <br>
 * This is the function set used by PolynomialProblem.
 * 
 * @see SymbolicRegressionProblem
 * @author Eduardo Pedroni
 *
 */
public class PolynomialFunctions extends FunctionSet {
	
	public final static double DIVISION_LIMIT = 0.0001;
	
	/**
	 * Creates a new instance of PolynomialFunctions.
	 */
	public PolynomialFunctions() {
		registerFunctions(
				new SquareRoot(),
				new Power(),
				new Addition(),
				new Subtraction(),
				new Multiplication(),
				new Division());
	}
	

	
	/**
	 * Protected square root function, returns the square root of the absolute
	 * value of input 0.
	 * 
	 * @see Math
	 */
	public static class SquareRoot extends Function {
		@Override
		public Integer run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Integer in0 = ((Integer) args[0]);
				return (int)Math.sqrt(Math.abs(in0));
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Square root";
		}
	}
	

	
	/**
	 * Power function. Returns input 0 to the power of input 1.
	 *
	 * @see Math
	 */
	public static class Power extends Function {
		@Override
		public Integer run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Integer in0 = ((Integer) args[0]);
				Integer in1 = ((Integer) args[1]);
				return (int)Math.pow(in0, in1);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Power";
		}
	}
	
	/**
	 * Addition returns the sum of inputs 0 and 1.
	 *
	 */
	public static class Addition extends Function {
		@Override
		public Integer run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Integer in0 = ((Integer) args[0]);
				Integer in1 = ((Integer) args[1]);
				return in0 + in1;
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Addition";
		}
	}

	/**
	 * Subtraction returns the difference between inputs 0 and 1.
	 *
	 */
	public static class Subtraction extends Function {
		@Override
		public Integer run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Integer in0 = ((Integer) args[0]);
				Integer in1 = ((Integer) args[1]);
				return in0 - in1;
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Subtraction";
		}
	}

	/**
	 * Multiplication returns the product of inputs 0 and 1.
	 *
	 */
	public static class Multiplication extends Function {
		@Override
		public Integer run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Integer in0 = ((Integer) args[0]);
				Integer in1 = ((Integer) args[1]);
				return in0 * in1;
			} 
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Multiplication";
		}
	}

	/**
	 * Protected division, returns the quotient of input 0 (the dividend) and input 1 (the divisor).
	 * If the divisor is less than {@code DoubleArithmetic.DIVISION_LIMIT}, this returns it unchanged.
	 *
	 */
	public static class Division extends Function {
		@Override
		public Integer run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Integer in0 = ((Integer) args[0]);
				Integer in1 = ((Integer) args[1]);

				return in1 < DIVISION_LIMIT ? in0 : (in0 / in1);
			} 
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Division";
		}
	}
}
