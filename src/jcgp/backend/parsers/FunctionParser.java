package jcgp.backend.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import jcgp.backend.function.FunctionSet;
import jcgp.backend.resources.Resources;

/**
 * Contains a static method for parsing functions from a
 * .par file. 
 * 
 * @author Eduardo Pedroni
 *
 */
public abstract class FunctionParser {

	/**
	 * Reads the specified file and attempts to enable 
	 * and disable the functions in the FunctionSet 
	 * accordingly.
	 * <br><br>
	 * Standard CGP .par files do not contain enough information
	 * to determine if they match the currently selected function set.
	 * For this reason, the parser assumes the function set is correct
	 * and treats functions by their index rather than their name. Any
	 * index outside the bounds of the function set is ignored and a
	 * warning message is printed once parsing is complete.
	 * 
	 * @param file the .par file to parse.
	 * @param functionSet the function set whose functions should be modified. 
	 * @param resources used for printing console messages.
	 */
	public static void parse(File file, FunctionSet functionSet, Resources resources) {
		// create file reader and scanner to parse, return if file does not exist
		FileReader fr;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			resources.println("[Parser] Error: could not find " + file.getAbsolutePath());
			return;
		}

		Scanner in = new Scanner(fr);
		boolean excessFunctions = false;
		resources.println("[Parser] Parsing file: " + file.getAbsolutePath() + "...");

		/*
		 * The encoding used in .par files is quite simple, so regex matches are used to extract
		 * the values.
		 * 
		 * A standard .par file contains functions in the following format:
		 * 
		 * 0  1      modulus-0
		 * 0  1      sqrt-1
		 * 0  1      reciprocal-2
		 * 
		 * The first integer signals whether to enable or disable the function. Any non-zero value
		 * is treated as "enable". The second integer is the function arity. The integer following
		 * the function name is the function index. 
		 * 
		 * The scanner is used to return each line separately. Every line that ends in a number
		 * is treated as a function line and split into an array, which holds its composing integers.
		 * This array is then used to enable or disabled the indexed function.
		 * 
		 * A flag is raised if the index exceeds the total number of functions, and a warning is
		 * printed once parsing is complete regarding the index mismatch.
		 * 
		 */
		while (in.hasNextLine()) {
			String line = in.nextLine();
			if (!line.isEmpty()) {
				if (line.substring(line.length() - 1).matches("[0-9]")) {
					String[] splitString = line.split("[^0-9]+");
					int functionIndex = Integer.parseInt(splitString[splitString.length - 1]);

					if (functionIndex < functionSet.getTotalFunctionCount()) {
						if (Integer.parseInt(splitString[0]) != 0) {
							functionSet.enableFunction(functionIndex);
							resources.println("[Parser] Enabled function: " + functionSet.getFunction(functionIndex));
						} else if (Integer.parseInt(splitString[0]) == 0) {
							functionSet.disableFunction(functionIndex);
							resources.println("[Parser] Disabled function: " + functionSet.getFunction(functionIndex));
						}
					} else {
						excessFunctions = true;
					}
				}
			}

		}

		// warn the user function index went out of bounds
		if (excessFunctions) {
			resources.println("[Parser] Warning: the parameter file contained more functions than the current function set");
		}

		in.close();
		resources.println("[Parser] Finished parsing functions");
	}
}
