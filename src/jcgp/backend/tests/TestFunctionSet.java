package jcgp.backend.tests;

import jcgp.backend.function.Function;
import jcgp.backend.function.FunctionSet;

public class TestFunctionSet extends FunctionSet {
	
	public TestFunctionSet() {
		
		registerFunctions(
			new Function() {
				@Override
				public Integer run(Object... args) {
					return (Integer) args[0] + (Integer) args[1];
				}
				@Override
				public int getArity() {
					return 2;
				}
			},
			new Function() {
				@Override
				public Integer run(Object... args) {
					return (Integer) args[0] - (Integer) args[1];
				}
				@Override
				public int getArity() {
					return 2;
				}
			},
			new Function() {
				@Override
				public Integer run(Object... args) {
					return (Integer) args[0] * (Integer) args[1];
				}
				@Override
				public int getArity() {
					return 2;
				}
			},
			new Function() {
				@Override
				public Integer run(Object... args) {
					return (Integer) args[0] / (Integer) args[1];
				}
				@Override
				public int getArity() {
					return 2;
				}
			}
		);
	}	
}