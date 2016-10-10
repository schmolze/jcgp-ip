package jcgp.backend.function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * FunctionSet encapsulates a group of functions. This is done to
 * simplify the implementation of problem types.
 * <br><br>
 * FunctionSet contains a variety of useful methods for acquiring general
 * information, such as the maximum arity across all functions and the total
 * number of functions.
 * <br><br>
 * In addition, FunctionSet offers the ability to enable and disable functions.
 * Accessing the functions through {@code getAllowedFunction()} will return
 * allowed functions only, providing an easy way to control which functions
 * can be used in mutations.
 * <br><br>
 * An implementation of FunctionSet must simply use its constructor to set
 * the name field and use {@code registerFunctions()} to add the required
 * functions. 
 * 
 * @author Eduardo Pedroni
 *
 */
public abstract class FunctionSet {
		private ArrayList<Function> functionList = new ArrayList<Function>();
		private ArrayList<Integer> allowedFunctions = new ArrayList<Integer>();
		
		/**
		 * @return the number of currently allowed functions.
		 */
		public int getAllowedFunctionCount() {
			return allowedFunctions.size();
		}
		
		/**
		 * @return the total number of functions, including disabled ones.
		 */
		public int getTotalFunctionCount() {
			return functionList.size();
		}
		
		/**
		 * Returns an allowed function. This throws an
		 * ArrayIndexOutOfBoundsException if the supplied 
		 * index is beyond the count of allowed functions.
		 * 
		 * @param index the allowed function index.
		 * @return the allowed function object.
		 */
		public Function getAllowedFunction(int index) {
			return functionList.get(allowedFunctions.get(index));
		}
		
		/**
		 * Returns a function from the complete collection,
		 * enabled or disabled alike. This throws an
		 * ArrayIndexOutOfBoundsException if the supplied 
		 * index is beyond the count of allowed functions.
		 * 
		 * @param index the function index.
		 * @return the function object.
		 */
		public Function getFunction(int index) {
			return functionList.get(index);
		}
		
		/**
		 * Computes and returns the maximum arity out of
		 * all enabled functions.
		 * 
		 * @return the problem's current maximum arity.
		 */
		public int getMaxArity(){
			int arity = 0;
			for (Integer function : allowedFunctions) {
				// if a higher arity is found, store it
				if (functionList.get(function).getArity() > arity) {
					arity = functionList.get(function).getArity();
				}
			}
			return arity;
		}
		
		/**
		 * Disables the indexed function. If the function
		 * is already disabled, this does nothing.
		 * 
		 * @param index the function to disable.
		 */
		public void disableFunction(int index) {
			/*
			 * allowedFunctions is a list of the indices of all allowed functions, 
			 * as addressed in functionList. This method iterates through the whole
			 * list of allowed functions and removes any elements which are equal
			 * to the specified index.
			 */
			if (index < functionList.size()) {
				for (Iterator<Integer> iterator = allowedFunctions.iterator(); iterator.hasNext();) {
					int function = iterator.next();
					if (function == index) {
						iterator.remove();
					}
				}
			} else {
				throw new ArrayIndexOutOfBoundsException("Function " + index + " does not exist, the set only has " + functionList.size() + " functions.");
			}
		}
		
		/**
		 * Disables the indexed function. If the function is
		 * already enabled, this does nothing. 
		 * 
		 * @param index the function to disable.
		 */
		public void enableFunction(int index) {
			// add the specified index to the list of allowed indices
			if (!allowedFunctions.contains(index)) {
				allowedFunctions.add(index);
				Collections.sort(allowedFunctions);
			}
		}
		
		/**
		 * Checks if a specified function is enabled. If the function
		 * does not belong in the FunctionSet, this returns false.
		 * 
		 * @param function the function to check.
		 * @return true if the function is enabled.
		 */
		public boolean isEnabled(Function function) {
			for (int i = 0; i < allowedFunctions.size(); i++) {
				if (functionList.get(allowedFunctions.get(i)) == function) {
					return true;
				}
			}
			return false;
		}
		
		/**
		 * For internal use in subclass constructors. This method
		 * adds the specified functions and enables them. The same
		 * function cannot be added more than once.
		 * 
		 * @param functions the functions to register in the function set.
		 */
		protected void registerFunctions(Function... functions) {
			for (int i = 0; i < functions.length; i++) {
				if (!alreadyHave(functions[i])) {
					functionList.add(functions[i]);
					enableFunction(functionList.size() - 1);
				}
			}
		}
		
		/**
		 * For internal use only, this checks whether a function
		 * is already present in the function set.
		 * 
		 * @param function the function to look for.
		 * @return true if the function is already in the function set.
		 */
		private boolean alreadyHave(Function function) {
			for (int i = 0; i < functionList.size(); i++) {
				if (functionList.get(i).getClass() == function.getClass()) {
					return true;
				}
			}
			return false;
		}
	}