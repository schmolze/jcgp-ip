package jcgp.backend.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import jcgp.backend.modules.problem.TestCaseProblem;
import jcgp.backend.resources.ModifiableResources;

/**
 * Contains a static method for parsing values from a
 * CGP problem data file. The actual file extension
 * varies from problem to problem, and is therefore
 * defined in the experiment's Problem instance.
 * 
 * 
 * @author Eduardo Pedroni
 *
 */
public abstract class TestCaseParser {
	
	/**
	 * Sets the number of inputs and outputs in the resources
	 * to match the given file, and parses each test case
	 * from the file into the specified problem.
	 * 
	 * @param file the problem file to parse.
	 * @param problem the problem into which to parse the problem data.
	 * @param resources a modifiable reference to the experiment's resources
	 */
	public static void parse(File file, TestCaseProblem<?> problem, ModifiableResources resources) {
		// create reader and scanner, print error message if file is missing
		FileReader fr;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			resources.println("[Parser] Error: could not find " + file.getAbsolutePath());
			return;
		}
		resources.println("[Parser] Parsing file: " + file.getAbsolutePath() + "...");
		Scanner in = new Scanner(fr);
		boolean readingTestCases = false;
		int inputs = 0, outputs = 0;
		int cases = 0;
		
		// this overwrites any previously added test cases
		problem.clearTestCases();
		
		while (in.hasNextLine()) {
			String nextLine = in.nextLine();
			// set resources input count to parsed value
			if (nextLine.startsWith(".i")) {
				String[] split = nextLine.split(" +");
				inputs = Integer.parseInt(split[1]);
				resources.setInputs(inputs);
				resources.println("[Parser] Number of inputs set to " + resources.inputs());
			}
			// set resources output count to parsed value
			else if (nextLine.startsWith(".o")) {
				String[] split = nextLine.split(" +");
				outputs = Integer.parseInt(split[1]);
				resources.setOutputs(outputs);
				resources.println("[Parser] Number of outputs set to " + resources.outputs());
				
			} else if (nextLine.startsWith(".p") || nextLine.startsWith(".t")) {
				readingTestCases = true;
				
			} else if (nextLine.startsWith(".e")) {
				readingTestCases = false;
			
			/*
			 * Split every line at one or more spaces or tabs, and 
			 * parse the two sides into inputs and outputs.
			 */
			} else if (readingTestCases) {
				String[] split = nextLine.split("( |\t)+");
				String[] inputCases = new String[inputs];
				String[] outputCases = new String[outputs];
				
				for (int i = 0; i < inputs; i++) {
					inputCases[i] = split[i];
				}
				
				for (int o = 0; o < outputs; o++) {
					outputCases[o] = split[o + inputs];
				}
				
				problem.addTestCase(inputCases, outputCases);
				cases++;
			}
		}
		resources.println("[Parser] Finished, added " + cases + " test cases");
		in.close();
	}
}
