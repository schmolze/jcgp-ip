package jcgp.backend.function;

/**
 * This class contains all digital circuit functions
 * (defined as unsigned integer functions in the classic
 * CGP implementation) defined in static nested classes.
 * <br>
 * This is the function set used by {@code DigitalCircuitProblem}.
 * 
 * @see jcgp.backend.modules.problem.DigitalCircuitProblem
 * @author Eduardo Pedroni
 *
 */
public class DigitalCircuitFunctions extends FunctionSet {
	
	/**
	 * Creates a new instance of {@code DigitalCircuitFunctions}.
	 */
	public DigitalCircuitFunctions() {
		registerFunctions(
				new ConstantZero(),
				new ConstantOne(),
				new WireA(),
				new WireB(),
				new NotA(),
				new NotB(),
				new And(),
				new AndNotA(),
				new AndNotB(),
				new Nor(),
				new Xor(),
				new Xnor(),
				new Or(),
				new OrNotA(),
				new OrNotB(),
				new Nand(),
				new Mux1(),
				new Mux2(),
				new Mux3(),
				new Mux4()
				);
	}
	
	/**
	 * Outputs a constant 0, has no inputs.
	 */
	public static class ConstantZero extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			return new UnsignedInteger(0);
		}

		@Override
		public int getArity() {
			return 0;
		}

		@Override
		public String toString() {
			return "0";
		}
	}
	
	/**
	 * Outputs a constant 1, has no inputs.
	 */
	public static class ConstantOne extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			return new UnsignedInteger(0xFFFF);
		}

		@Override
		public int getArity() {
			return 0;
		}

		@Override
		public String toString() {
			return "1";
		}
	}
	
	/**
	 * Connects one node to another with no function.
	 */
	public static class WireA extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				return ((UnsignedInteger) args[0]);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Wire A";
		}
	}
	
	/**
	 * Connects one node to another with no function.
	 */
	public static class WireB extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				return ((UnsignedInteger) args[1]);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Wire B";
		}
	}
	
	/**
	 * Inverts input, equivalent to inverter logic gate.
	 */
	public static class NotA extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				return new UnsignedInteger(~((UnsignedInteger) args[0]).get());
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Not A";
		}
	}
	
	/**
	 * Inverts input, equivalent to inverter logic gate.
	 */
	public static class NotB extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				return new UnsignedInteger(~((UnsignedInteger) args[1]).get());
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Not B";
		}
	}
	
	/**
	 * ANDs inputs together.
	 */
	public static class And extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				Integer result = in0.get() & in1.get();
				
				return new UnsignedInteger(result);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "And";
		}
	}
	
	/**
	 * ANDs inputs together with one input inverted.
	 */
	public static class AndNotA extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				Integer result = ~(in0.get()) & in1.get();
				
				return new UnsignedInteger(result);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "And !A";
		}
	}
	
	/**
	 * ANDs inputs together with one input inverted.
	 */
	public static class AndNotB extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				Integer result = in0.get() & ~(in1.get());
				
				return new UnsignedInteger(result);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "And !B";
		}
	}
	
	/**
	 * NORs inputs together.
	 */
	public static class Nor extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				Integer result = in0.get() | in1.get();

				return new UnsignedInteger(~result);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Nor";
		}
	}
	
	/**
	 * XORs inputs together.
	 */
	public static class Xor extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				Integer result = in0.get() ^ in1.get();

				return new UnsignedInteger(result);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Xor";
		}
	}
	
	/**
	 * XNORs inputs together.
	 */
	public static class Xnor extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				Integer result = in0.get() ^ in1.get();

				return new UnsignedInteger(~result);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Xnor";
		}
	}
	
	/**
	 * ORs inputs together.
	 */
	public static class Or extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				Integer result = in0.get() | in1.get();

				return new UnsignedInteger(result);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Or";
		}
	}
	
	/**
	 * ORs inputs together with one inverted input.
	 */
	public static class OrNotA extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				Integer result = ~in0.get() | in1.get();

				return new UnsignedInteger(result);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Or !A";
		}
	}
	
	/**
	 * ORs inputs together with one inverted input.
	 */
	public static class OrNotB extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				Integer result = in0.get() | ~in1.get();

				return new UnsignedInteger(result);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Or !B";
		}
	}

	/**
	 * NANDs inputs together.
	 */
	public static class Nand extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				Integer result = in0.get() & in1.get();

				return new UnsignedInteger(~result);
			}
		}

		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public String toString() {
			return "Nand";
		}
	}
	
	/**
	 * Works as a multiplexer. Outputs either one of its two inputs
	 * depending on a third input (select).
	 */
	public static class Mux1 extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				UnsignedInteger in2 = ((UnsignedInteger) args[2]);
				Integer result = ((in0.get() & ~in2.get()) | (in1.get() & in2.get()));

				return new UnsignedInteger(result);
			}
		}

		@Override
		public int getArity() {
			return 3;
		}

		@Override
		public String toString() {
			return "Mux1";
		}
	}
	
	/**
	 * Works as a multiplexer. Outputs either one of its two inputs
	 * depending on a third input (select). Input 0 is inverted.
	 */
	public static class Mux2 extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				UnsignedInteger in2 = ((UnsignedInteger) args[2]);
				Integer result = ((in0.get() & ~in2.get()) | (~in1.get() & in2.get()));

				return new UnsignedInteger(result);
			}
		}

		@Override
		public int getArity() {
			return 3;
		}

		@Override
		public String toString() {
			return "Mux2";
		}
	}
	
	/**
	 * Works as a multiplexer. Outputs either one of its two inputs
	 * depending on a third input (select). Input 1 is inverted.
	 */
	public static class Mux3 extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				UnsignedInteger in2 = ((UnsignedInteger) args[2]);
				Integer result = ((~in0.get() & ~in2.get()) | (in1.get() & in2.get()));

				return new UnsignedInteger(result);
			}
		}

		@Override
		public int getArity() {
			return 3;
		}

		@Override
		public String toString() {
			return "Mux3";
		}
	}
	
	/**
	 * Works as a multiplexer. Outputs either one of its two inputs
	 * depending on a third input (select). Both inputs are inverted.
	 */
	public static class Mux4 extends Function {
		@Override
		public UnsignedInteger run(Object... args) {
			if (args.length < getArity()) {
				throw new IllegalArgumentException(toString() + " received " + args.length + " in but arity is " + getArity() + ".");
			} else {
				UnsignedInteger in0 = ((UnsignedInteger) args[0]);
				UnsignedInteger in1 = ((UnsignedInteger) args[1]);
				UnsignedInteger in2 = ((UnsignedInteger) args[2]);
				Integer result = ((~in0.get() & ~in2.get()) | (~in1.get() & in2.get()));

				return new UnsignedInteger(result);
			}
		}

		@Override
		public int getArity() {
			return 3;
		}

		@Override
		public String toString() {
			return "Mux4";
		}
	}
}
