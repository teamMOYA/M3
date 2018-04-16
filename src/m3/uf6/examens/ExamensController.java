package m3.uf6.examens;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import m3.uf6.examens.model.Examen;
import m3.uf6.examens.model.Excepcio;
import m3.uf6.examens.model.Model;

public class ExamensController implements Initializable {
	private MenuController menu;

	@FXML private BorderPane vistaTaulaExamens;

	@FXML private TableView<Examen> examensTable;
	@FXML private TableColumn<Examen, String> colModul;
	@FXML private TableColumn<Examen, String> colUnitat;
	@FXML private TableColumn<Examen, Integer> colPreguntes;
	@FXML private TableColumn<Examen, Double> colPuntuacio;
	@FXML private TableColumn<Examen, Boolean> colEditar;
	@FXML private TableColumn<Examen, Boolean> colAvaluar;
	@FXML private TableColumn<Examen, Boolean> colEsborrar;

	public void setMenu(MenuController menu) {
		this.menu = menu;
	}

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {

		if (this.colModul != null) {
			this.colModul.setMinWidth(170);
			this.colModul.setCellValueFactory(new PropertyValueFactory<Examen,String>("getModulUf"));
		}

		if (this.colUnitat != null) {
			this.colUnitat.setMinWidth(300);
			this.colUnitat.setCellValueFactory(new PropertyValueFactory<Examen,String>("getTitolUf"));
		}

		if (this.colPreguntes != null) {
			this.colPreguntes.setMinWidth(70);
			this.colPreguntes.setMaxWidth(80);
			this.colPreguntes.setCellValueFactory(new PropertyValueFactory<Examen,Integer>("getTotalPreguntes"));
		}

		if (this.colPuntuacio != null) {
			this.colPuntuacio.setMinWidth(80);
			this.colPuntuacio.setMaxWidth(90);
			this.colPuntuacio.setCellValueFactory(new PropertyValueFactory<Examen,Double>("getPuntuacio"));
			this.colPuntuacio.setCellFactory(new ColumnFormatter<Examen, Double>(new DecimalFormat( "#0.0" )));
		}

		if (this.colEditar != null) {
			this.colEditar.setMinWidth(80);
			this.colEditar.setMaxWidth(80);
			// define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
			this.colEditar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Examen, Boolean>, ObservableValue<Boolean>>() {
				@Override
				public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Examen, Boolean> button) {
					return new SimpleBooleanProperty(true);
				}
			});

			this.colEditar.setCellFactory(new ColumnButton<Examen, Boolean>( "Editar" ) {
				@Override
				public void buttonAction(Examen element) {
					try {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("VistaEdicioExamen.fxml"));
						BorderPane vista = (BorderPane) loader.load();
						loader.<EdicioExamenController>getController().initData(null, element);

						menu.carregarVista(vista);
					} catch (Excepcio | IOException e) {
						e.printStackTrace();
						MenuController.mostrarError("No es pot editar l'Examen", e.getMessage());
					}
				}
			});
		}

		if (this.colAvaluar != null) {
			this.colAvaluar.setMinWidth(80);
			this.colAvaluar.setMaxWidth(80);
			// define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
			this.colAvaluar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Examen, Boolean>, ObservableValue<Boolean>>() {
				@Override
				public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Examen, Boolean> button) {
					return new SimpleBooleanProperty(true);
				}
			});

			this.colAvaluar.setCellFactory(new ColumnButton<Examen, Boolean>( "Avaluar" ) {
				@Override
				public void buttonAction(Examen element) {
					try {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("VistaAvaluacio.fxml"));
						BorderPane vista = (BorderPane) loader.load();
						loader.<AvaluacioController>getController().initData(element);

						menu.carregarVista(vista);
					} catch (IOException e) {
						e.printStackTrace();
						MenuController.mostrarError("No es pot avalaur l'Examen", e.getMessage());
					}
				}
			});
		}

		if (this.colEsborrar != null) {
			this.colEsborrar.setMinWidth(80);
			this.colEsborrar.setMaxWidth(80);
			// define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
			this.colEsborrar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Examen, Boolean>, ObservableValue<Boolean>>() {
				@Override
				public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Examen, Boolean> button) {
					return new SimpleBooleanProperty(true);
				}
			});

			this.colEsborrar.setCellFactory(new ColumnButton<Examen, Boolean>( "Esborrar" ) {
				@Override
				public void buttonAction(Examen element) {
					try {
						Model.getInstance().removeExamen(element);
					} catch (Excepcio e) {
						MenuController.mostrarError("No s'ha pogut esborrar l'Examen", e.getMessage());
					}


				}
			});
		}

		if (this.examensTable != null) {
			this.examensTable.setEditable(true);
			this.examensTable.getSelectionModel().setCellSelectionEnabled(true);
			this.examensTable.setItems( Model.getInstance().getExamens() );
			this.examensTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		}

	}

}



