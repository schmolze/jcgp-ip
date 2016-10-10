package jcgp.backend.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import jcgp.backend.resources.ModifiableResources;

/**
 * Contains a static method for parsing parameters from a
 * .par file. 
 * 
 * @author Eduardo Pedroni
 *
 */
public abstract class ParameterParser {

	/**
	 * Parses the parameters from a specified CGP parameter file and
	 * modifies the experiment resources appropriately. 
	 * <br><br>
	 * CGP .par files do not follow a very strict convention, so this
	 * parser does its best to cope with format irregularities. Parsing
	 * works even if the parameters are in the wrong order, and unknown
	 * parameters are simply ignored.
	 * 
	 * @param file the .par file to parse.
	 * @param resources a reference to the resources object that must be modified.
	 */
	public static void parse(File file, ModifiableResources resources) {
		// create reader and scanner, print message if file is missing
		FileReader fr;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			resources.println("[Parser] Error: could not find " + file.getAbsolutePath());
			return;
		}
		
		Scanner in = new Scanner(fr);
		resources.println("[Parser] Parsing file: " + file.getAbsolutePath() + "...");
		
		// parse line by line
		while (in.hasNextLine()) {
			// split if one or more tabs or one or more spaces are found
			String[] splitString = in.nextLine().split("( |\t)+");
			// expected length is 2, anything beyond that should also work
			if (splitString.length >= 2) {
				switch (splitString[1]) {
				case "population_size":
					resources.setPopulationSize(Integer.parseInt(splitString[0]));
					resources.println("[Parser] Population size is now " + resources.populationSize());
					break;

				case "num_generations":
					resources.setGenerations(Integer.parseInt(splitString[0]));
					resources.println("[Parser] Total generations is now " + resources.generations());
					break;

				case "num_runs_total":
					resources.setRuns(Integer.parseInt(splitString[0]));
					resources.println("[Parser] Total runs is now " + resources.runs());
					break;

				case "num_rows":
					resources.setRows(Integer.parseInt(splitString[0]));
					resources.println("[Parser] Row number is now " + resources.rows());
					break;

				case "num_cols":
					resources.setColumns(Integer.parseInt(splitString[0]));
					resources.println("[Parser] Column number is now " + resources.columns());
					break;

				case "levels_back":
					resources.setLevelsBack(Integer.parseInt(splitString[0]));
					resources.println("[Parser] Levels back is now " + resources.levelsBack());
					break;

				case "report_interval":
					resources.setReportInterval(Integer.parseInt(splitString[0]));
					resources.println("[Parser] Report interval is now " + resources.levelsBack());
					break;

				case "global_seed":
					resources.setSeed(Integer.parseInt(splitString[0]));
					resources.println("[Parser] Seed is now " + resources.seed());
					break;

				default:
					break;
				}
			}
		}
		
		in.close();
		resources.println("[Parser] Finished parsing parameters");
	}
}
