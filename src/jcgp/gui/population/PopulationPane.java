package jcgp.gui.population;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import jcgp.JCGP;
import jcgp.backend.modules.problem.TestCaseProblem;
import jcgp.backend.modules.problem.TestCaseProblem.TestCase;
import jcgp.gui.GUI;

public class PopulationPane extends TabPane {
	
	private GUI gui;
	private TestCase<Object> currentTestCase;
	private boolean evaluating = false;
	
	public PopulationPane(GUI gui) {
		super();
		this.gui = gui;
		setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		remakeTabs();
	}
	
	public void remakeTabs() {
		getTabs().clear();
		JCGP jcgp = gui.getExperiment();
		
		Tab tab;
		ChromosomePane cp;
		for (int i = 0; i < jcgp.getResources().populationSize(); i++) {
			cp = new ChromosomePane(jcgp.getPopulation().get(i), gui, this);
			tab = new Tab("Chr " + i);
			tab.setContent(cp);
			getTabs().add(tab);
		}
	}
		
	public void updateGenes() {
		if (evaluating) {
			evaluateTestCase(currentTestCase);
		}
		for (int i = 0; i < getTabs().size(); i++) {
			((ChromosomePane) getTabs().get(i).getContent()).updateGenes(gui.getExperiment().getPopulation().get(i));
		}
	}
	
	public void unlockOutputs() {
		for (int i = 0; i < getTabs().size(); i++) {
			((ChromosomePane) getTabs().get(i).getContent()).unlockOutputs();
		}
	}
	
	public void relockOutputs() {
		for (int i = 0; i < getTabs().size(); i++) {
			((ChromosomePane) getTabs().get(i).getContent()).relockOutputs();
		}
	}
	
	public void evaluateTestCase(TestCase<Object> testCase) {
		if (gui.getExperiment().getProblem() instanceof TestCaseProblem && testCase != null) {
			currentTestCase = testCase;
			if (testCase.getInputs().length == gui.getExperiment().getResources().inputs()) {
				evaluating = true;
				for (int i = 0; i < getTabs().size(); i++) {
					((ChromosomePane) getTabs().get(i).getContent()).setInputs(testCase.getInputs());
				}
			} else {
				throw new IllegalArgumentException("Test case has " + testCase.getInputs().length
						+ " inputs and chromosome has " + gui.getExperiment().getResources().inputs());
			}
		}
	}
	
	public void hideValues() {
		evaluating = false;
		for (int i = 0; i < getTabs().size(); i++) {
			((ChromosomePane) getTabs().get(i).getContent()).updateValues();
		}
	}

	public boolean isEvaluating() {
		return evaluating;
	}

	public void setEvaluating(boolean value) {
		evaluating = value;
	}
}
