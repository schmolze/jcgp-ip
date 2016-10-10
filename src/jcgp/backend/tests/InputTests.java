package jcgp.backend.tests;

import static org.junit.Assert.assertTrue;
import jcgp.backend.population.Input;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Tests which cover the behaviour specified for an input.
 * 
 *  - An input contains a single set value. This value can be freely set for 
 *    fitness evaluation purposes. 
 *  - It must be addressable by an index set upon construction only.
 *  
 * 
 * @author Eduardo Pedroni
 *
 */
public class InputTests {

	private Input input;
	// these are the test values
	private final int inputValue = 19;
	private final int inputIndex = 12;

	@Before
	public void setUp() throws Exception {
		input = new Input(inputIndex);
	}

	@Test
	public void valueTest() {
		// assign a value to input, check that the returned value is correct
		input.setValue(inputValue);

		assertTrue("Incorrect value returned.", ((Integer) input.getValue()) == inputValue);
	}

	@Test
	public void indexTest() {
		// check that the index returned is the one passed to the constructor
		assertTrue("Incorrect index returned.", input.getIndex() == inputIndex);
	}

}
