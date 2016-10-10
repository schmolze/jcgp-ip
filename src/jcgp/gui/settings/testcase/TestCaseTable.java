package jcgp.gui.settings.testcase;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jcgp.backend.modules.problem.TestCaseProblem;
import jcgp.backend.modules.problem.TestCaseProblem.TestCase;
import jcgp.backend.resources.Resources;
import jcgp.gui.GUI;

/**
 * This is a test case table. For problems that have test cases, 
 * this table shows the test case inputs and outputs. Clicking on 
 * a test case (one is shown per row) applies the values to all 
 * chromosome inputs shows the calculated values throughout the chromosome.
 * 
 * @author Eduardo Pedroni
 *
 */
public class TestCaseTable extends Stage {

	private TableView<TestCase<Object>> table;

	/**
	 * Make a new instance of {@code TestCaseTable}.
	 * 
	 * @param testCaseProblem the {@code TestCaseProblem} whose data must be displayed.
	 * @param gui a reference to the GUI.
	 */
	public TestCaseTable(final TestCaseProblem<Object> testCaseProblem, final GUI gui) {
		super();
		
		Resources resources = gui.getExperiment().getResources();
		
		// create the actual table view
		table = new TableView<TestCase<Object>>();
		// get test cases from problem
		ObservableList<TestCase<Object>> testCaseList = testCaseProblem.getTestCases();
		
		// prepare input and output columns
		ArrayList<TableColumn<TestCase<Object>, String>> inputs = new ArrayList<TableColumn<TestCase<Object>, String>>(resources.inputs());
		ArrayList<TableColumn<TestCase<Object>, String>> outputs = new ArrayList<TableColumn<TestCase<Object>, String>>(resources.outputs());

		// create input columns
		TableColumn<TestCase<Object>, String> tc;
		for (int i = 0; i < resources.inputs(); i++) {
			tc = new TableColumn<TestCase<Object>, String>("I: " + i);
			inputs.add(tc);
			final int index = i;
			tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TestCase<Object>,String>, ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(CellDataFeatures<TestCase<Object>, String> param) {
					// create a new string property and give it the test case value, no need for dynamic binding - this wont change often
					return new SimpleStringProperty(param.getValue().getInputs()[index].toString());
				}
			});
			tc.setSortable(false);
			// set column width so all columns are distributed across the width of the stage
			tc.prefWidthProperty().bind(table.widthProperty().divide(resources.inputs() + resources.outputs()));
		}
		
		// create output columns
		for (int o = 0; o < resources.outputs(); o++) {
			tc = new TableColumn<TestCase<Object>, String>("O: " + o);
			outputs.add(tc);
			final int index = o;
			tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TestCase<Object>,String>, ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(CellDataFeatures<TestCase<Object>, String> param) {
					// create a new string property and give it the test case value, no need for dynamic binding - this wont change often
					return new SimpleStringProperty(param.getValue().getOutputs()[index].toString());
				}
			});
			tc.setSortable(false);
			// set column width so all columns are distributed across the width of the stage
			tc.prefWidthProperty().bind(table.widthProperty().divide(resources.inputs() + resources.outputs()));
		}
		
		// add created columns
		table.getColumns().addAll(inputs);
		table.getColumns().addAll(outputs);
		
		// populate table with actual data
		table.setItems(testCaseList);
		
		// apply test case values when a new test case is selected
		table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TestCase<Object>>() {
			@Override
			public void changed(ObservableValue<? extends TestCase<Object>> observable,	TestCase<Object> oldValue, TestCase<Object> newValue) {
				gui.evaluateTestCase(newValue);
			}
		});
		
		// when the stage is closed, clear the selection
		// this doesn't work if the stage is closed by the program for some reason...
		setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				gui.hideGeneValues();
				table.getSelectionModel().clearSelection();
			}
		});
		
		setScene(new Scene(table));
	}

	/**
	 * @return a reference to the actual table of test cases.
	 */
	public TableView<TestCase<Object>> getTable() {
		return table;
	}
}
