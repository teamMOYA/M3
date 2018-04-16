package m3.uf6.examens;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import m3.uf6.examens.model.Excepcio;
import m3.uf6.examens.model.Model;
import m3.uf6.examens.model.UnitatFormativa;

public class UnitatsFormativesController implements Initializable {
	@FXML private BorderPane vistaFormUnitatsFormatives;

	@FXML private Button btnAfegir;

	@FXML private TableView<UnitatFormativa> ufsTable;
	@FXML private TableColumn<UnitatFormativa, String> colCicle;
	@FXML private TableColumn<UnitatFormativa, String> colModul;
	@FXML private TableColumn<UnitatFormativa, Integer> colNum;
	@FXML private TableColumn<UnitatFormativa, String> colTitol;
	@FXML private TableColumn<UnitatFormativa, Integer> colHores;
	@FXML private TableColumn<UnitatFormativa, Boolean> colEsborrar;

	@FXML private TextField txtModul;
	@FXML private Spinner<Integer> spinNum;
	@FXML private TextField txtTitol;
	@FXML private Spinner<Integer> spinHores;
	@FXML private ChoiceBox<String> lstCicles;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {

		if (lstCicles != null)  this.lstCicles.setItems(FXCollections.observableArrayList(Model.CICLES));

		if (spinNum != null)  {	}

		if (spinHores != null)  { }

		if (this.colCicle != null) {

			this.colCicle.setMinWidth(50);
			this.colCicle.setMaxWidth(70);
			// setCellValueFactory => asocia columnes amb un valor observable de classe del model associada a la columna
			this.colCicle.setCellValueFactory(cellData -> cellData.getValue().getCicleProperty());  // Visualització dels valors Opció 1 notació java8
			//this.colCicle.setCellValueFactory(new PropertyValueFactory<UnitatFormativa,String>("getCicle"));	// Visualització dels valors Opció 2  factoria
			/*this.colCicle.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UnitatFormativa, String>, ObservableValue<String>>() {		// Implementació callback Opció 3
				@Override
				public ObservableValue<String> call(TableColumn.CellDataFeatures<UnitatFormativa, String> unitat) {
					// unitat.getValue() returns the UnitatForamtiva instance for a particular TableView row
			        // return button.getValue().lastNameProperty();
					return new SimpleStringProperty(unitat.getValue().getCicle());
				}
			});*/
			this.colCicle.setCellFactory(TextFieldTableCell.forTableColumn());	// Edició
			this.colCicle.setOnEditCommit(
				new EventHandler<CellEditEvent<UnitatFormativa, String>>() {
					@Override
					public void handle(CellEditEvent<UnitatFormativa, String> t) {
						try {
							UnitatFormativa unitat = Model.getInstance().getUnitatFormativaByIndex(t.getTablePosition().getRow());
							unitat.setCicle(t.getNewValue());
							Model.getInstance().updateUnitatFormativa(unitat);
						} catch (Excepcio e) {
							MenuController.mostrarError("No s'ha pogut modificar el Cicle Formatiu", e.getMessage());
						}
					}
				}
			);
		}

		if (this.colModul != null) {
			this.colModul.setMinWidth(230);
			this.colModul.setMaxWidth(300);
			this.colModul.setCellValueFactory(new PropertyValueFactory<UnitatFormativa,String>("getModul"));
			this.colModul.setCellFactory(TextFieldTableCell.forTableColumn());	// Edició

			this.colModul.setOnEditCommit(
					new EventHandler<CellEditEvent<UnitatFormativa, String>>() {
						@Override
						public void handle(CellEditEvent<UnitatFormativa, String> t) {
							try {
								UnitatFormativa unitat = Model.getInstance().getUnitatFormativaByIndex(t.getTablePosition().getRow());
								unitat.setModul(t.getNewValue());
								Model.getInstance().updateUnitatFormativa(unitat);
							} catch (Excepcio e) {
								MenuController.mostrarError("No s'ha pogut modificar el nom del Mòdul", e.getMessage());
							}
						}
					}
					);

		}

		if (this.colNum != null) {
			this.colNum.setMinWidth(80);
			this.colNum.setMaxWidth(100);
			//this.colNum.setCellValueFactory(cellData -> cellData.getValue().getNumProperty());
			this.colNum.setCellValueFactory(new PropertyValueFactory<UnitatFormativa,Integer>("getNum"));
			//this.colNum.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

			this.colNum.setCellFactory(new ColumnSpinner<UnitatFormativa, Integer>(1, 10) {
				@Override
				public void spinnerAction(int index, Integer newValue) {
					try {
						UnitatFormativa unitat = Model.getInstance().getUnitatFormativaByIndex(index);
						unitat.setNum(newValue.intValue());
						Model.getInstance().updateUnitatFormativa(unitat);
					} catch (Excepcio e) {
						MenuController.mostrarError("No s'ha pogut modificarles hores del Mòdul", e.getMessage());
					}
				}

				@Override
				public Integer decrementSpinnerValue(Integer value, int steps) {
					return value<=super.minValue?value:value-steps;
				}

				@Override
				public Integer incrementSpinnerValue(Integer value, int steps) {
					return value>=super.maxValue?value:value+steps;
				}

				@Override
				public boolean checkSpinnerBounds(Integer value) {
					return value >= super.minValue;
				}
			});
		}

		if (this.colTitol != null) {
			this.colTitol.setMinWidth(300);
			//this.colTitol.setCellValueFactory(cellData -> cellData.getValue().getTitolProperty());
			this.colTitol.setCellValueFactory(new PropertyValueFactory<UnitatFormativa,String>("getTitol"));
		}

		if (this.colHores != null) {
			this.colHores.setMinWidth(100);
			this.colHores.setMaxWidth(120);
			this.colHores.setCellValueFactory(new PropertyValueFactory<UnitatFormativa,Integer>("getHores"));


			this.colHores.setCellFactory(new ColumnSpinner<UnitatFormativa, Integer>(1, 150) {
				@Override
				public void spinnerAction(int index, Integer newValue) {
					try {
						UnitatFormativa unitat = Model.getInstance().getUnitatFormativaByIndex(index);
						unitat.setHores(newValue.intValue());
						Model.getInstance().updateUnitatFormativa(unitat);
					} catch (Excepcio e) {
						MenuController.mostrarError("No s'ha pogut modificarles hores del Mòdul", e.getMessage());
					}
				}

				@Override
				public Integer decrementSpinnerValue(Integer value, int steps) {
					return value<=super.minValue?value:value-steps;
				}

				@Override
				public Integer incrementSpinnerValue(Integer value, int steps) {
					return value>=super.maxValue?value:value+steps;
				}

				@Override
				public boolean checkSpinnerBounds(Integer value) {
					return value >= super.minValue && value <= super.maxValue;
				}
			});
		}

		if (this.colEsborrar != null) {
			this.colEsborrar.setMinWidth(100);
			this.colEsborrar.setMaxWidth(100);
			// define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
			this.colEsborrar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<UnitatFormativa, Boolean>, ObservableValue<Boolean>>() {
				@Override
				public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<UnitatFormativa, Boolean> button) {
					return new SimpleBooleanProperty(true);
				}
			});

			this.colEsborrar.setCellFactory(new ColumnButton<UnitatFormativa, Boolean>( "Esborrar" ) {
				@Override
				public void buttonAction(UnitatFormativa element) {
					try {
						Model.getInstance().removeUnitatFormativa(element);
					} catch (Excepcio e) {
						MenuController.mostrarError("No s'ha pogut esborrar la Unitat Formativa", e.getMessage());
					}

				}
			});
		}

		if (this.ufsTable != null) {
			this.ufsTable.setEditable(true);
			this.ufsTable.getSelectionModel().setCellSelectionEnabled(true);
			this.ufsTable.setItems( Model.getInstance().getUnitats() );
			this.ufsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		}
	}

	@FXML
	public void novaUnitatFormativaClick(ActionEvent event) {
		String cicle = "";
		String modul = "";
		String titol = "";
		int num = 0;
		int hores = 0;

		try {

			if (this.txtModul != null) modul = this.txtModul.getText();
			if (this.txtTitol != null) titol = this.txtTitol.getText();
			if (this.lstCicles != null) cicle = this.lstCicles.getValue();
			if (this.spinNum != null) num = this.spinNum.getValue();
			if (this.spinHores != null) hores = this.spinHores.getValue();

			Model.getInstance().addUnitatFormativa(cicle, modul, num, titol, hores);

			// Clear
			this.txtModul.clear();
			this.txtTitol.clear();
			this.lstCicles.setValue(null);
			this.spinNum.getValueFactory().setValue(1);
			this.spinHores.getValueFactory().setValue(33);

		} catch (Exception e) {
			MenuController.mostrarError("No s'ha pogut afegir la Unitat Formativa", e.getLocalizedMessage());
		}
	}
}



