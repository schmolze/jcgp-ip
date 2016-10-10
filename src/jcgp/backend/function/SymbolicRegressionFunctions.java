package jcgp.backend.function;

import jcgp.backend.modules.problem.SymbolicRegressionProblem;

/**
 * This class contains all symbolic regression functions
 * (defined as double functions in the classic CGP implementation)
 * in static nested classes.
 * <br>
 * This is the function set used by SymbolicRegressionProblem.
 * 
 * @see SymbolicRegressionProblem
 * @author Eduardo Pedroni
 *
 */
public class SymbolicRegressionFunctions extends FunctionSet {
	
	public final static double DIVISION_LIMIT = 0.0001;
	
	/**
	 * Creates a new instance of SymbolicRegressionFunctions.
	 */
	public SymbolicRegressionFunctions() {
		registerFunctions(
				new Absolute(),
				new SquareRoot(),
				new Reciprocal(),
				new Sine(),
				new Cosine(),
				new Tangent(),
				new Exponential(),
				new HyperbolicSine(),
				new HyperbolicCosine(),
				new HyperbolicTangent(),
				new NaturalLog(),
				new LogBaseTen(),
				new SineAB(),
				new CosineAB(),
				new Hypotenuse(),
				new Power(),
				new Addition(),
				new Subtraction(),
				new Multiplication(),
				new Division());
	}
	
	/**
	 * Absolute returns the positive value of input 0.
	 * 
	 * @see Math
	 */
	public static class Absolute extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return Math.abs(in0);
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Absolute";
		}
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
	 * Protected reciprocal function, returns (1 / input 0). If input 0 is less than
	 * {@code DoubleArithmetic.DIVISION_LIMIT}, this returns it unchanged.
	 *
	 */
	public static class Reciprocal extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return in0 < DIVISION_LIMIT ? in0 : (1 / in0);
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Reciprocal";
		}
	}
	
	/**
	 * Sine function, in radians.
	 *
	 * @see Math
	 */
	public static class Sine extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return Math.sin(in0);
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Sin";
		}
	}
	
	/**
	 * Cosine function, in radians.
	 *
	 * @see Math
	 */
	public static class Cosine extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return Math.cos(in0);
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Cos";
		}
	}
	
	/**
	 * Protected tangent function, in radians. Returns the tangent of input 0.
	 * If input 0 is less than {@code DoubleArithmetic.DIVISION_LIMIT}, 
	 * this returns it unchanged.
	 *
	 * @see Math
	 */
	public static class Tangent extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return in0 < DIVISION_LIMIT ? in0 : Math.tan(in0);
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Tan";
		}
	}
	
	/**
	 * Exponential function. Returns e raised to input 0.
	 *
	 * @see Math
	 */
	public static class Exponential extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return Math.exp(in0);
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Exp";
		}
	}
	
	/**
	 * Returns the hyperbolic sine of input 0.
	 *
	 * @see Math
	 */
	public static class HyperbolicSine extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return Math.sinh(in0);
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Sinh";
		}
	}
	
	/**
	 * Returns the hyperbolic cosine of input 0.
	 *
	 * @see Math
	 */
	public static class HyperbolicCosine extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return Math.cosh(in0);
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Cosh";
		}
	}
	
	/**
	 * Returns the hyperbolic tangent of input 0.
	 *
	 * @see Math
	 */
	public static class HyperbolicTangent extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return Math.tanh(in0);
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Tanh";
		}
	}
	
	/**
	 * Protected natural log function. Returns the natural log of the absolute
	 * value of input 0. If input 0 is less than {@code DoubleArithmetic.DIVISION_LIMIT}, 
	 * this returns it unchanged.
	 *
	 * @see Math
	 */
	public static class NaturalLog extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return in0 < DIVISION_LIMIT ? in0 : Math.log(Math.abs(in0));
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Ln";
		}
	}
	
	/**
	 * Protected log base 10 function. Returns the log to base 10 the absolute
	 * value of input 0. If input 0 is less than {@code DoubleArithmetic.DIVISION_LIMIT}, 
	 * this returns it unchanged.
	 *
	 * @see Math
	 */
	public static class LogBaseTen extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				return in0 < DIVISION_LIMIT ? in0 : Math.log10(Math.abs(in0));
			}
		}

		@Override
		public int getArity() {
			return 1;
		}

		@Override
		public String toString() {
			return "Log";
		}
	}
	
	/**
	 * Sine of sum. Returns the sine of the sum of inputs 0 and 1.
	 *
	 * @see Math
	 */
	public static class SineAB extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
				return Math.sin(in0 + in1);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Sin(a+b)";
		}
	}
	
	/**
	 * Cosine of sum. Returns the cosine of the sum of inputs 0 and 1.
	 *
	 * @see Math
	 */
	public static class CosineAB extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
				return Math.cos(in0 + in1);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Cos(a+b)";
		}
	}
	
	/**
	 * Hypotenuse function. Returns the square root of input 0 squared
	 * plus input 1 squared.
	 *
	 * @see Math
	 */
	public static class Hypotenuse extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
				return Math.hypot(in0, in1);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Hypotenuse";
		}
	}
	
	/**
	 * Power function. Returns the absolute value of input 0 to the power of input 1.
	 *
	 * @see Math
	 */
	public static class Power extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
				return Math.pow(Math.abs(in0), in1);
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
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
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
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);
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
	 * Protected division, returns the quotient of input 0 (the dividend) and input 1 (the divisor).
	 * If the divisor is less than {@code DoubleArithmetic.DIVISION_LIMIT}, this returns it unchanged.
	 *
	 */
	public static class Division extends Function {
		@Override
		public Double run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " arguments but arity is " + getArity() + ".");
			} else {
				Double in0 = ((Double) args[0]);
				Double in1 = ((Double) args[1]);

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
