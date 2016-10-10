package jcgp.backend.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;

import jcgp.backend.population.Chromosome;
import jcgp.backend.population.Connection;
import jcgp.backend.population.Input;
import jcgp.backend.population.Node;
import jcgp.backend.resources.Resources;

/**
 * This class contains a method for parsing .chr files and another
 * for writing .chr files from given chromosomes.
 * 
 * @author Eduardo Pedroni
 *
 */
public abstract class ChromosomeParser {

	/**
	 * Use this method to parse .chr files into a given chromosome.
	 * <br><br>
	 * This is not fully defensive as it doesn't check for number of inputs, 
	 * doesn't compare rows and columns individually and doesn't account for levels back. It
	 * is not viable to implement these defensive measures with the chromosome format used 
	 * by CGP.
	 * 
	 * @param file the .chr file to parse from.
	 * @param chromosome the chromosome to configure.
	 * @param resources the experiment resources.
	 */
	public static void parse(File file, Chromosome chromosome, Resources resources) {
		/* 
		 * Count the nodes to make sure the size of the .chr file matches the experiment parameters.
		 * 
		 * We do this by using the scanner to get the node and output portions of the file as they
		 * are separated by 3 tab characters. Every number is replaced by a single known character, 
		 * and the length of the string with the new characters is compared with that of a string
		 * where the new known character has been removed, yielding the total number of values.
		 * 
		 * TODO this is NOT ideal and should be refactored
		 * 
		 */
		FileReader fr;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			resources.println("[Parser] Error: could not find " + file.getAbsolutePath());
			return;
		}
		Scanner in = new Scanner(fr);
		
		in.useDelimiter("\\t\\t\\t");
		String geneString = in.next().replaceAll("[0-9]+", "g");
		String outString = in.next().replaceAll("[0-9]+", "o");
		int geneCount = geneString.length() - geneString.replace("g", "").length();
		int outCount = outString.length() - outString.replace("o", "").length();
		in.close();
		
		
		// if the acquired values match the current parameters, apply them to the chromosome
		if ((geneCount == resources.nodes() * (resources.arity() + 1))
				&& outCount == resources.outputs()) {
			// prepare a new scanner
			try {
				fr = new FileReader(file);
			} catch (FileNotFoundException e) {
				resources.println("[Parser] Error: could not find " + file.getAbsolutePath());
				return;
			}
			in = new Scanner(fr);
			
			resources.println("[Parser] Parsing file: " + file.getAbsolutePath() + "...");
			
			int gene;
			Connection newConnection;
			Node changingNode;
			// for all nodes, columns first
			for (int c = 0; c < resources.columns(); c++) {
				for (int r = 0; r < resources.rows(); r++) {
					// store the changing node
					changingNode = chromosome.getNode(r, c);

					// for every connection
					for (int i = 0; i < resources.arity(); i++) {
						// get connection number from the .chr file
						gene = in.nextInt();
						if (gene < resources.inputs()) {
							// connection was an input
							newConnection = chromosome.getInput(gene);
						} else {
							// connection was another node, calculate which from its number
							newConnection = chromosome.getNode((gene - resources.inputs()) % resources.rows(), 
									(gene - resources.inputs()) / resources.rows());
						}
						changingNode.setConnection(i, newConnection);
					}

					// set the function, straight indexing should work - this is not entirely
					// safe, but it is not viable to check for functionset compatibility
					changingNode.setFunction(resources.getFunction(in.nextInt()));
				}
			}

			// outputs
			for (int o = 0; o < resources.outputs(); o ++) {
				gene = in.nextInt();
				if (gene < resources.inputs()) {
					// connection was an input
					newConnection = chromosome.getInput(gene);
				} else {
					// connection was another node, calculate which from its number
					newConnection = chromosome.getNode((gene - resources.inputs()) % resources.rows(), 
							(gene - resources.inputs()) / resources.rows());
				}
				chromosome.getOutput(o).setSource(newConnection);
			}
			in.close();
			
			resources.println("[Parser] File parsed successfully");
			
		} else {
			resources.println("[Parser] Error: the number of genes of the chromosome in " + file.getName() + " does not match that of the experiment");
		}
	}
	
	/**
	 * Writes a chromosome into the specified .chr file.
	 * <br><br>
	 * The file is written in the standard .chr format and can
	 * be read by the original CGP implementation.
	 * 
	 * @param file the file to write to.
	 * @param chromosome the chromosome to save.
	 * @param resources a reference to the experiment's resources.
	 */
	public static void save(File file, Chromosome chromosome, Resources resources) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			resources.println("[Parser] Error: could not find " + file.getAbsolutePath());
			return;
		}
		
		resources.println("[Parser] Saving to " + file.getAbsolutePath() + "...");
		
		// for all nodes, columns first
		for (int c = 0; c < resources.columns(); c++) {
			for (int r = 0; r < resources.rows(); r++) {
				for (int i = 0; i < resources.arity(); i++) {
					// print the connections, separated by spaces
					Connection conn = chromosome.getNode(r, c).getConnection(i);
					if (conn instanceof Input) {
						writer.print(" " + ((Input) conn).getIndex());
					} else if (conn instanceof Node) {
						writer.print(" " + (((((Node) conn).getColumn() + 1) * resources.inputs()) + ((Node) conn).getRow()));
					} else {
						resources.println("[Parser] Error: could not handle " + conn.getClass() + " as a subclass of Connection");
					}
				}
				// print the function numbers
				writer.print(" " + resources.getFunctionIndex(chromosome.getNode(r, c).getFunction()));
				// node is done, print tab
				writer.print("\t");
			}
		}
		// nodes are done, print two tabs to separate from output
		writer.print("\t\t");
		for (int o = 0; o < resources.outputs(); o ++) {
			Connection source = chromosome.getOutput(o).getSource();
			if (source instanceof Input) {
				writer.print(" " + ((Input) source).getIndex());
			} else if (source instanceof Node) {
				writer.print(" " + (((((Node) source).getColumn() + 1) * resources.inputs()) + ((Node) source).getRow()));
			} else {
				resources.println("[Parser] Error: could not handle " + source.getClass() + " as a subclass of Connection");
			}
		}
		
		writer.close();
		
		resources.println("[Parser] Chromosome saved successfully");
	}
	
	
	/**
	 * Writes a chromosome to the console in .chr format. Note
	 * that, if using a GUI console, that console must be flushed for the
	 * output to appear.
	 * 
	 * @param chromosome the chromosome to save.
	 * @param resources a reference to the experiment's resources.
	 */
	public static void print(Chromosome chromosome, Resources resources) {
		
		// for all nodes, columns first
		for (int c = 0; c < resources.columns(); c++) {
			for (int r = 0; r < resources.rows(); r++) {
				for (int i = 0; i < resources.arity(); i++) {
					// print the connections, separated by spaces
					Connection conn = chromosome.getNode(r, c).getConnection(i);
					if (conn instanceof Input) {
						resources.print(" " + ((Input) conn).getIndex());
					} else if (conn instanceof Node) {
						resources.print(" " + (((((Node) conn).getColumn() + 1) * resources.inputs()) + ((Node) conn).getRow()));
					} else {
						resources.println("[Parser] Error: could not handle " + conn.getClass() + " as a subclass of Connection");
					}
				}
				// print the function numbers
				resources.print(" " + resources.getFunctionIndex(chromosome.getNode(r, c).getFunction()));
				// node is done, print tab
				resources.print("\t");
			}
		}
		// nodes are done, print two tabs to separate from output
		resources.print("\t\t");
		for (int o = 0; o < resources.outputs(); o ++) {
			Connection source = chromosome.getOutput(o).getSource();
			if (source instanceof Input) {
				resources.print(" " + ((Input) source).getIndex());
			} else if (source instanceof Node) {
				resources.print(" " + (((((Node) source).getColumn() + 1) * resources.inputs()) + ((Node) source).getRow()));
			} else {
				resources.println("[Parser] Error: could not handle " + source.getClass() + " as a subclass of Connection");
			}
		}
		
		resources.println("");
	}
}
