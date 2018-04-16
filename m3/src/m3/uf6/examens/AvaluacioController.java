package m3.uf6.examens;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import m3.uf6.examens.model.Estudiant;
import m3.uf6.examens.model.Examen;
import m3.uf6.examens.model.Excepcio;
import m3.uf6.examens.model.Lliurament;
import m3.uf6.examens.model.Model;

public class AvaluacioController implements Initializable {
	@FXML private BorderPane vistaFormEstudiants;

	@FXML private ChoiceBox<Examen> examens;

	@FXML private TableView<Estudiant> assistenciaTable;
	@FXML private TableColumn<Estudiant, String> colNomCognoms;
	@FXML private TableColumn<Estudiant, Boolean> colLliurat;

	@FXML private TableView<Lliurament> lliuramentsTable;
	@FXML private TableColumn<Lliurament, String> colLliuramentEstudiant;

	@FXML private TableView<Lliurament> correccionsTable;
	@FXML private TableColumn<Lliurament, String> colCorreccioEstudiant;
	@FXML private TableColumn<Lliurament, Double> colCorreccioNota;
	@FXML private TableColumn<Lliurament, Boolean> colRevisar;

	@FXML private TableView<Lliurament> revisionsTable;
	@FXML private TableColumn<Lliurament, String> colRevisioEstudiant;

	@FXML private Button btnCorregir;
	@FXML private Button btnRevisar;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {

		if (this.examens != null) {
			this.examens.setItems(Model.getInstance().getExamens());
			this.examens.setConverter(new StringConverter<Examen>()
			{
				@Override
				public Examen fromString(String string) {
					return null;
				}

				@Override
				public String toString(Examen examen)
				{
					return (examen == null)?"":examen.getModulUf()+". "+examen.getTitolUf();
				}
			});
		}

		// Columnes Taula Assistència
		if (this.colNomCognoms != null) {
			this.colNomCognoms.setMinWidth(150);
			this.colNomCognoms.setMaxWidth(170);
			this.colNomCognoms.setCellValueFactory(new PropertyValueFactory<Estudiant,String>("getCognomsNom"));
		}

		if (this.colLliurat != null) {
			this.colLliurat.setMinWidth(70);
			this.colLliurat.setMaxWidth(70);
			this.colLliurat.getStyleClass().add("col-center");
			this.colLliurat.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Estudiant, Boolean>, ObservableValue<Boolean>>() {
				@Override
				public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Estudiant, Boolean> cell) {
					Estudiant estudiant = cell.getValue();
					return new SimpleBooleanProperty(Model.getInstance().estudiantLliuramentSelectedExamen(estudiant));
				}
			});
			this.colLliurat.setCellFactory(new ColumnCheckBox<Estudiant, Boolean>(){
				@Override
				public void checkAction(Estudiant estudiant, boolean inscriure) {
					try {
						if (inscriure) Model.getInstance().apilarLliuramentSelectedExamen(estudiant);
						else Model.getInstance().desapilarLliuramentSelectedExamen();
					} catch (Excepcio e) {
						MenuController.mostrarError("No s'ha pogut "+(inscriure?"apilar":"desapilar")+" el lliurament d'aquest estudiant", e.getMessage());
					}
				}
				public boolean checkValue(Estudiant estudiant) {
					return Model.getInstance().estudiantLliuramentSelectedExamen(estudiant) ||
							Model.getInstance().estudiantCorreccioSelectedExamen(estudiant);
				}
				@Override
				public boolean checkEditable(Estudiant estudiant) {
					// Editables els alumnes que encara no han lliurat, i el darrer alumne que ha lliurat (cim de la pila)
					// No editables lliuraments corregits
					if (Model.getInstance().estudiantCorreccioSelectedExamen(estudiant)) return false;

					if (!Model.getInstance().estudiantLliuramentSelectedExamen(estudiant)) return true;

					Estudiant darrer = Model.getInstance().estudiantDarrerLliuramentSelectedExamen();
					if (darrer == null) return true;

					return estudiant.compareTo(darrer) == 0;
				}
			});
		}

		// Columnes Taula Lliuraments pendents de corregir
		if (this.colLliuramentEstudiant != null) {
			this.colLliuramentEstudiant.setMinWidth(150);
			this.colLliuramentEstudiant.setMaxWidth(170);
			this.colLliuramentEstudiant.setCellValueFactory(new PropertyValueFactory<Lliurament,String>("getNomEstudiant"));
		}

		// Columnes Taula Lliuraments corregits
		if (this.colCorreccioEstudiant != null) {
			this.colCorreccioEstudiant.setMinWidth(150);
			this.colCorreccioEstudiant.setMaxWidth(170);
			this.colCorreccioEstudiant.setCellValueFactory(new PropertyValueFactory<Lliurament,String>("getNomEstudiant"));
		}

		if (this.colCorreccioNota != null) {
			this.colCorreccioNota.setMinWidth(80);
			this.colCorreccioNota.setMaxWidth(90);
			this.colCorreccioNota.setCellValueFactory(new PropertyValueFactory<Lliurament,Double>("getNota"));
			this.colCorreccioNota.setCellFactory(new ColumnFormatter<Lliurament, Double>(new DecimalFormat( "#0.0" )));
		}

		if (this.colRevisar != null) {
			this.colRevisar.setMinWidth(100);
			this.colRevisar.setMaxWidth(100);
			// define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
			this.colRevisar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Lliurament, Boolean>, ObservableValue<Boolean>>() {
				@Override
				public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Lliurament, Boolean> button) {
					return new SimpleBooleanProperty(true);
				}
			});

			this.colRevisar.setCellFactory(new ColumnButton<Lliurament, Boolean>( "Revisar" ) {
				@Override
				public void buttonAction(Lliurament lliurament) {
					try {
						Model.getInstance().solicitarRevisioSelectedExamen(lliurament);
					} catch (Excepcio e) {
						MenuController.mostrarError("No s'ha pogut demanar la revisió de la nota d'aquest estudiant", e.getMessage());
					}
				}
			});
		}

		// Columnes Taula Revisions pendents de revisar
		if (this.colRevisioEstudiant != null) {
			this.colRevisioEstudiant.setMinWidth(150);
			this.colRevisioEstudiant.setMaxWidth(170);
			this.colRevisioEstudiant.setCellValueFactory(new PropertyValueFactory<Lliurament,String>("getNomEstudiant"));
		}

		this.initData(null);
	}

	public void initData(Examen examen) {

		if (this.examens != null) {
			if (examen == null) {
				this.examens.getSelectionModel().selectFirst();
			} else {
				this.examens.getSelectionModel().select(examen);
			}
			Model.getInstance().setSelectedExamen(this.examens.getSelectionModel().getSelectedItem());

			this.examens.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Examen>() {
				@Override
				public void changed(ObservableValue<? extends Examen> observable, Examen oldValue, Examen newValue) {
					if (newValue != null && !newValue.equals(oldValue)) initData(newValue);
				}
			});
		}


		// Taula Assistència
		if (this.assistenciaTable != null) {
			this.assistenciaTable.setEditable(true);
			this.assistenciaTable.getSelectionModel().setCellSelectionEnabled(true);
			this.assistenciaTable.setItems( Model.getInstance().getAlumnesSelectedExamen() );
			this.assistenciaTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		}

		// Taula Lliuraments pendents de corregir
		if (this.lliuramentsTable != null) {
			this.lliuramentsTable.setEditable(false);
			this.lliuramentsTable.getSelectionModel().setCellSelectionEnabled(false);
			this.lliuramentsTable.setItems( Model.getInstance().getLliuramentsSelectedExamen() );
			this.lliuramentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		}

		// Taula Lliuraments corregits
		if (this.correccionsTable != null) {
			this.correccionsTable.setEditable(true);
			this.correccionsTable.getSelectionModel().setCellSelectionEnabled(true);
			this.correccionsTable.setItems( Model.getInstance().getCorreccionsSelectedExamen() );
			this.correccionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		}

		// Taula Revisions pendents de revisar
		if (this.revisionsTable != null) {
			this.revisionsTable.setEditable(false);
			this.revisionsTable.getSelectionModel().setCellSelectionEnabled(false);
			this.revisionsTable.setItems( Model.getInstance().getRevisionsSelectedExamen()  );
			this.revisionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		}
	}

	@FXML
	public void corregirLliuramentClick(ActionEvent event) {
		Estudiant darrer = Model.getInstance().estudiantDarrerLliuramentSelectedExamen();

		try {
			if (darrer == null) throw new Excepcio("AvaluacioController", "No hi ha cap lliurament per corregir");

			TextInputDialog dialogCorreccioLliurament = this.crearDialegNota("Corregir lliurament", darrer.getCognomsNom(), 0);

			Optional<String> result = dialogCorreccioLliurament.showAndWait();

			if (result.isPresent()) {
				double nota = Main.getDecimalFormatter().fromString(result.get());
				if (nota == 0) throw new Excepcio("AvaluacioController", "Valor de la nota incorrecte ");
				Model.getInstance().corregirLliuramentSelectedExamen(nota);
			}
		} catch (Excepcio e) {
			MenuController.mostrarError("No s'ha pogut corregir el lliurament", e.getMessage());
		}
	}

	@FXML
	public void revisarLliuramentClick(ActionEvent event) {
		Lliurament primer = Model.getInstance().primerLliuramentRevisarSelectedExamen();

		try {
			if (primer == null) throw new Excepcio("AvaluacioController", "No hi ha cap lliurament per revisar");

			TextInputDialog dialogCorreccioLliurament = this.crearDialegNota("Revisar lliurament", primer.getEstudiant().getCognomsNom(), primer.getNota());

			Optional<String> result = dialogCorreccioLliurament.showAndWait();

			if (result.isPresent()) {
				double nota = Main.getDecimalFormatter().fromString(result.get());
				if (nota == 0) throw new Excepcio("AvaluacioController", "Valor de la nota incorrecte ");
				Model.getInstance().atendreRevisioSelectedExamen(nota);
			}
		} catch (Excepcio e) {
			MenuController.mostrarError("No s'ha pogut revisar el lliurament", e.getMessage());
		}
	}

	private TextInputDialog crearDialegNota(String title, String alumne, double nota) {
		TextInputDialog dialogNotaLliurament = new TextInputDialog(Main.getDecimalFormatter().toString(nota));

		dialogNotaLliurament.setTitle(title);
		dialogNotaLliurament.setHeaderText(alumne);
		dialogNotaLliurament.setContentText("Nota: ");

		dialogNotaLliurament.setWidth(300.0);
		dialogNotaLliurament.getDialogPane().getStylesheets().add( getClass().getResource("application.css").toExternalForm());
		dialogNotaLliurament.getDialogPane().getStyleClass().add("dialeg-nota");

		TextField txtNota = dialogNotaLliurament.getEditor();
		txtNota.getStyleClass().add("control-center");
		txtNota.setTextFormatter(new TextFormatter<Double>(Main.getDecimalFormatter()));
		txtNota.setText(Main.getDecimalFormatter().toString(nota));

		return dialogNotaLliurament;
	}

}



