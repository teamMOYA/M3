package m3.uf6.examens;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import m3.uf6.examens.model.Estudiant;
import m3.uf6.examens.model.Examen;
import m3.uf6.examens.model.Excepcio;
import m3.uf6.examens.model.Model;

public class EstudiantsController implements Initializable {
	@FXML private BorderPane vistaFormEstudiants;

	@FXML private Button btnAfegir;

	@FXML private TableView<Estudiant> estudiantsTable;
	@FXML private TableColumn<Estudiant, Number> colNum;
	@FXML private TableColumn<Estudiant, String> colNom;
	@FXML private TableColumn<Estudiant, String> colCognoms;
	@FXML private TableColumn<Estudiant, Integer> colEdat;
	private HashMap<TableColumn<Estudiant, Boolean>, Examen> colsExamens;
	@FXML private TableColumn<Estudiant, Boolean> colEsborrar;

	@FXML private TextField txtNom;
	@FXML private TextField txtCognoms;
	@FXML private Spinner<Integer> spinEdat;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {

		if (this.colNum != null) {
			this.colNum.setMinWidth(30);
			this.colNum.setMaxWidth(40);
			this.colNum.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Estudiant, Number>, ObservableValue<Number>>() {
				@Override
				public ObservableValue<Number> call(TableColumn.CellDataFeatures<Estudiant, Number> estudiant) {

					return new SimpleIntegerProperty( Model.getInstance().getIndexEstudiant(estudiant.getValue()) + 1 );
				}
			});
		}

		if (this.colNom != null) {
			this.colNom.setMinWidth(100);
			this.colNom.setMaxWidth(120);
			this.colNom.setCellValueFactory(new PropertyValueFactory<Estudiant,String>("getNom"));

			this.colNom.setCellFactory(TextFieldTableCell.forTableColumn());	// Edició
			this.colNom.setOnEditCommit(
					new EventHandler<CellEditEvent<Estudiant, String>>() {
						@Override
						public void handle(CellEditEvent<Estudiant, String> t) {
							try {
								Estudiant estudiant = Model.getInstance().getEstudiantByIndex(t.getTablePosition().getRow());
								estudiant.setNom(t.getNewValue());
								Model.getInstance().updateEstudiant(estudiant);
							} catch (Excepcio e) {
								MenuController.mostrarError("No s'ha pogut modificar el nom de l'estudiant", e.getMessage());
							}
						}
					}
					);
		}

		if (this.colCognoms != null) {
			this.colCognoms.setMinWidth(150);
			this.colCognoms.setMaxWidth(250);
			this.colCognoms.setCellValueFactory(new PropertyValueFactory<Estudiant,String>("getCognoms"));
			this.colCognoms.setCellFactory(TextFieldTableCell.forTableColumn());	// Edició

			this.colCognoms.setOnEditCommit(
					new EventHandler<CellEditEvent<Estudiant, String>>() {
						@Override
						public void handle(CellEditEvent<Estudiant, String> t) {
							try {
								Estudiant estudiant = Model.getInstance().getEstudiantByIndex(t.getTablePosition().getRow());
								estudiant.setCognoms(t.getNewValue());
								Model.getInstance().updateEstudiant(estudiant);
							} catch (Excepcio e) {
								MenuController.mostrarError("No s'ha pogut modificar els cognoms de l'estudiant", e.getMessage());
							}
						}
					}
					);
		}

		if (this.colEdat != null) {
			this.colEdat.setMinWidth(80);
			this.colEdat.setMaxWidth(100);
			this.colEdat.setCellValueFactory(new PropertyValueFactory<Estudiant,Integer>("getEdat"));
			this.colEdat.setCellFactory(new ColumnSpinner<Estudiant, Integer>(Estudiant.MIN_EDAT, 100) {
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

				@Override
				public void spinnerAction(int index, Integer newValue) {
					try {
						Estudiant estudiant = Model.getInstance().getEstudiantByIndex(index);
						estudiant.setEdat(newValue.intValue());
						Model.getInstance().updateEstudiant(estudiant);
					} catch (Excepcio e) {
						MenuController.mostrarError("No s'ha pogut modificar l'edat de l'estudiant", e.getMessage());
					}
				}
			});
		}

		if (this.colEsborrar != null) {
			this.colEsborrar.setMinWidth(100);
			this.colEsborrar.setMaxWidth(100);
			// define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
			this.colEsborrar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Estudiant, Boolean>, ObservableValue<Boolean>>() {
				@Override
				public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Estudiant, Boolean> button) {
					return new SimpleBooleanProperty(true);
				}
			});

			this.colEsborrar.setCellFactory(new ColumnButton<Estudiant, Boolean>( "Esborrar" ) {
				@Override
				public void buttonAction(Estudiant element) {
					try {
						Model.getInstance().removeEstudiant(element);
					} catch (Excepcio e) {
						MenuController.mostrarError("No s'ha pogut esborrar l'estudiant", e.getMessage());
					}

				}
			});
		}


		if (this.estudiantsTable != null) {
			this.estudiantsTable.setEditable(true);
			this.estudiantsTable.getSelectionModel().setCellSelectionEnabled(true);
			this.estudiantsTable.setItems( Model.getInstance().getEstudiants() );
			this.estudiantsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


			// Afegir columnes checkbox per cada examen
			ObservableList<Examen> examens = Model.getInstance().getExamens();
			this.colsExamens = new HashMap<TableColumn<Estudiant, Boolean>, Examen> ();

			for (Examen examen : examens) {

				TableColumn<Estudiant, Boolean> column = new TableColumn<Estudiant, Boolean>(examen.getModulUf());

				column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Estudiant, Boolean>, ObservableValue<Boolean>>() {
					@Override
					public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Estudiant, Boolean> cell) {
						Estudiant estudiant = cell.getValue();
						Model.getInstance().setSelectedExamen(examen);
						return new SimpleBooleanProperty(Model.getInstance().estudiantInscritSelectedExamen(estudiant));
					}
				});
				column.setCellFactory(new ColumnCheckBox<Estudiant, Boolean>(){
					@Override
					public void checkAction(Estudiant estudiant, boolean inscriure) {
						try {
							Model.getInstance().setSelectedExamen(examen);
							if (inscriure) Model.getInstance().inscriureEstudiantSelectedExamen(estudiant);
							else Model.getInstance().anularMatriculaEstudiantSelectedExamen(estudiant);
						} catch (Excepcio e) {
							MenuController.mostrarError("No s'ha pogut inscriure l'estudiant", e.getMessage());
						}

					}
					@Override
					public boolean checkValue(Estudiant estudiant) {
						Model.getInstance().setSelectedExamen(examen);
						return Model.getInstance().estudiantInscritSelectedExamen(estudiant);
					}
					@Override
					public boolean checkEditable(Estudiant element) {
						return true;
					}
				});
				column.setMinWidth(50);
				column.getStyleClass().add("col-center");

				this.colsExamens.put(column, examen);
				int cols = this.estudiantsTable.getColumns().size();
				this.estudiantsTable.getColumns().add(cols - 1, column);
			}
		}
	}

	@FXML
	public void nouEstudiantClick(ActionEvent event) {
		String nom = "";
		String cognoms = "";
		int edat = 0;

		try {

			if (this.txtNom != null) nom = this.txtNom.getText();
			if (this.txtCognoms != null) cognoms = this.txtCognoms.getText();
			if (this.spinEdat != null) edat = this.spinEdat.getValue();

			Model.getInstance().addEstudiant(nom, cognoms, edat);

			// Clear
			this.txtNom.clear();
			this.txtCognoms.clear();
			this.spinEdat.getValueFactory().setValue(Estudiant.MIN_EDAT);

		} catch (Exception e) {
			MenuController.mostrarError("No s'ha pogut afegir l'estudiant", e.getLocalizedMessage());
		}
	}
}



