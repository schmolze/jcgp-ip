package jcgp.backend.function;

/**
 * Integer wrapper type for unsigned integer values. 
 * <br><br>
 * Java offers no support for unsigned types save from 
 * unsigned conversion methods. This class uses those methods
 * to simulate the unsigned int data type, useful for circuit 
 * truth table encodings.
 * <br><br>
 * When a string representation of an unsigned integer is parsed
 * using Integer.parseUnsignedInt(), an Integer is created using
 * all 32 bits for unsigned magnitude. The integer however is still
 * signed and will behave as such for all arithmetic operations.
 * Bitwise operations can still be performed as they work at the bit
 * level, making this data type particularly suitable for circuit design. 
 * <br><br>
 * TODO in the unlikely event that unsigned integers are natively 
 * implemented in Java, they should be used instead of this class.
 * <br><br>
 * Why are unsigned integers not supported?<br>
 * http://stackoverflow.com/questions/430346/why-doesnt-java-support-unsigned-ints
 * 
 * @author Eduardo Pedroni
 * @see Integer
 *
 */
public class UnsignedInteger {
	
	private Integer value;
	
	/**
	 * Makes a new instance of UnsignedInteger with a specified value.
	 * 
	 * @param i the value with which to initialise.
	 */
	public UnsignedInteger(int i) {
		value = new Integer(i);
	}
	
	/**
	 * Makes a new instance of UnsignedInteger from the string representation
	 * of an unsigned integer.
	 * 
	 * @param i the string with which to initialise.
	 */
	public UnsignedInteger(String i) {
		value = Integer.parseUnsignedInt(i);
	}
	
	/**
	 * @return the wrapped Integer object.
	 */
	public Integer get() {
		return value;
	}
	
	@Override
	public String toString() {
		/*
		 * It is important to override this so that
		 * the visual representation of the integer
		 * is unsigned as well.
		 */
		return Integer.toUnsignedString(value);
	}
}
