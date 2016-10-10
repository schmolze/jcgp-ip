package jcgp.backend.function;

/**
 * This class contains all travelling salesman functions in static nested classes.
 * <br>
 * This is the function set to be used by TravellingSalesmanProblem.
 * 
 * @author Eduardo Pedroni
 *
 */
public class TravellingSalesmanFunctions extends FunctionSet {
		
	/**
	 * Creates a new instance of TravellingSalesmanFunctions.
	 */
	public TravellingSalesmanFunctions() {
		registerFunctions(
				new SquareRoot(),
				new Square(),
				new Cube(),
				new ScaledExponential(),
				new AbsoluteSineAB(),
				new AbsoluteCosineAB(),
				new AbsoluteHyperbolicTangentAB(),
				new ScaledHypotenuse(),
				new ScaledAddition(),
				new SymmetricSubtraction(),
				new Multiplication(),
				new BoundedDivision());
	}
	
	/**
	 * Protected square root function, returns the square root of the absolute
	 * value of input 0.
	 * 
	 * @see Math
	 */
	public static class SquareRoot extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return Math.sqrt(Math.abs(in0));
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
	 * Square function, returns the square of the
	 * value of input 0.
	 * 
	 */
	public static class Square extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return in0 * in0;
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Square";
		}
	}
	
	/**
	 * Cube function, returns the value of input 0 cubed.
	 * 
	 */
	public static class Cube extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return in0 * in0 * in0;
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Cube";
		}
	}
	
	/**
	 * Scaled exponential function. Returns the exponential of input 0 
	 * scaled to the range 0 &lt; x &gt; 1.
	 *
	 * @see Math
	 */
	public static class ScaledExponential extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return (Math.exp(in0) - 1) / (Math.exp(-1) - 1);
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Scaled Exp";
		}
	}
	
	/**
	 * Sine of sum. Returns the absolute value of the sine 
	 * of the sum of inputs 0 and 1, in radians.
	 *
	 * @see Math
	 */
	public static class AbsoluteSineAB extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
				return Math.abs(Math.sin(in0 + in1));
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Abs Sin(a+b)";
		}
	}
	
	/**
	 * Cosine of sum. Returns the absolute value of the cosine 
	 * of the sum of inputs 0 and 1, in radians.
	 *
	 * @see Math
	 */
	public static class AbsoluteCosineAB extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
				return Math.abs(Math.cos(in0 + in1));
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Abs Cos(a+b)";
		}
	}
	
	/**
	 * Hyperbolic tangent of sum. Returns the absolute value of the sine 
	 * of the sum of inputs 0 and 1, in radians.
	 *
	 * @see Math
	 */
	public static class AbsoluteHyperbolicTangentAB extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
				return Math.abs(Math.tanh(in0 + in1));
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Abs Tanh(a+b)";
		}
	}
	
	/**
	 * Scaled hypotenuse function. Returns the square root of input 0 squared
	 * plus input 1 squared, scaled to the range 0 &lt; x &gt; 1.
	 *
	 * @see Math
	 */
	public static class ScaledHypotenuse extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
				return Math.hypot(in0, in1) / Math.sqrt(2);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Scaled Hypotenuse";
		}
	}
	
	/**
	 * Scaled addition returns the sum of inputs 0 and 1 scaled
	 * to the range 0 &lt; x &gt; 1.
	 *
	 */
	public static class ScaledAddition extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
				return (in0 + in1) / 2.0;
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Scaled Addition";
		}
	}

	/**
	 * Symmetric subtraction returns the absolute difference between inputs 0 and 1,
	 * scaled to the range 0 &lt;; x &gt; 1.
	 *
	 */
	public static class SymmetricSubtraction extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
				return Math.abs(in0 - in1) / 2.0;
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Symmetric Subtraction";
		}
	}

	/**
	 * Multiplication returns the product of inputs 0 and 1.
	 *
	 */
	public static class Multiplication extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
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
	 * Multiplication returns the product of inputs 0 and 1, squared.
	 *
	 */
	public static class SquaredMultiplication extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
				return in0 * in1 * in0 * in1;
			} 
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Squared Multiplication";
		}
	}

	/**
	 * Bounded division, returns the quotient of the two inputs where the larger
	 * is the denominator. 
	 *
	 */
	public static class BoundedDivision extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = Math.abs((Double) args[0]);
				Double in1 = Math.abs((Double) args[1]);
				Double result;
				
				if (in1 < 1e-10) {
					result = 1.0;
				} else if (in1 > in0) {
					result = in0 / in1;
				} else {
					result = in1 / in0;
				}

				return result;
			} 
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Bounded Division";
		}
	}
}
