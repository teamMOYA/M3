package m3.uf6.examens;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import m3.uf6.examens.model.Examen;
import m3.uf6.examens.model.Excepcio;
import m3.uf6.examens.model.Model;
import m3.uf6.examens.model.Pregunta;
import m3.uf6.examens.model.UnitatFormativa;

public class EdicioExamenController implements Initializable {
	private UnitatFormativa unitat;
	private Examen examen;

	@FXML private BorderPane vistaFormExamen;

	@FXML private Label lblPreview;
	@FXML private TextField txtUF;
	@FXML private TextField txtNum;
	@FXML private TextField txtPuntuacio;
	@FXML private TextArea txtPregunta;
	@FXML private ToggleGroup tipusPregunta;
	@FXML private RadioButton radioOberta;
	@FXML private RadioButton radioVoF;
	@FXML private RadioButton radioOpcions;
	@FXML private Button btnOpcions;


	@FXML private Button btnAfegir;
	@FXML private Button btnPDF;

	private ObservableList<Pregunta> preguntes;

	@FXML private TableView<Pregunta> preguntesTable;
	@FXML private TableColumn<Pregunta, Number> colNum;
	@FXML private TableColumn<Pregunta, Double> colPuntuacio;
	@FXML private TableColumn<Pregunta, String> colPregunta;
	@FXML private TableColumn<Pregunta, Boolean> colVerdaderFals;	// Check box => cell
	@FXML private TableColumn<Pregunta, Integer> colOpcions;		// Nombre total opcions
	@FXML private TableColumn<Pregunta, Boolean> colEsborrar;


	private ObservableList<String> opcions;

	/* Controls diàleg Opcions */
	@FXML private TextField txtOpcio;
	@FXML private ListView<String> lstOpcions;
	@FXML private Button btnAfegirOpcio;
	@FXML private Button btnEsborrarOpcio;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {
		this.preguntes = FXCollections.observableArrayList();
		this.opcions = FXCollections.observableArrayList();
	}

	/**
	 * Informar les dades
	 * 		Nou examen => unitat
	 * 		Editar examen => examen
	 *
	 * @param unitat
	 * @param examen
	 * @throws Excepcio
	 */
	public void initData(UnitatFormativa unitat, Examen exam) throws Excepcio {

		if (unitat == null && exam == null) throw new Excepcio("EdicioExamenController", "Cal indicar una Unitat Formativa o bé un Examen per poder editar-los");

		if (exam == null) this.examen = Model.getInstance().addExamen(unitat);
		else this.examen = exam;

		this.unitat = this.examen.getUnitat();


		if (this.btnPDF != null) {
			Image imagePDF = new Image(getClass().getResourceAsStream("resources/pdf-logo.png"));
			ImageView logo = new ImageView(imagePDF);
			logo.setFitWidth(20);
			logo.setFitHeight(20);
			this.btnPDF.setGraphic(logo);
		}

		if (this.tipusPregunta != null) {
			this.tipusPregunta.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
				@Override
				public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {

					RadioButton chk = (RadioButton)t1.getToggleGroup().getSelectedToggle(); // Cast object to radio button

					String radio = "";
					if (chk != null && chk.getText() != null) radio = chk.getText();

					btnOpcions.setDisable(!radio.equals(radioOpcions.getText()));
				}
			});
		}

		if (this.txtUF != null) {
			this.txtUF.setText( this.unitat.toString() );
		}

		if (this.examen != null) {
			this.initFormExamen(this.examen);

			this.preguntes.addAll(this.examen.getPreguntes());

			if (this.colNum != null) {
				this.colNum.setMinWidth(30);
				this.colNum.setMaxWidth(40);
				this.colNum.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pregunta, Number>, ObservableValue<Number>>() {
					@Override
					public ObservableValue<Number> call(TableColumn.CellDataFeatures<Pregunta, Number> cell) {

						return new SimpleIntegerProperty(preguntes.indexOf(cell.getValue()) + 1);
					}
				});
			}
			if (this.colPuntuacio != null) {
				this.colPuntuacio.setMinWidth(60);
				this.colPuntuacio.setMaxWidth(80);
				this.colPuntuacio.setCellValueFactory(new PropertyValueFactory<Pregunta,Double>("getPuntuacio"));
				this.colPuntuacio.setCellFactory(new ColumnFormatter<Pregunta, Double>(new DecimalFormat( "#0.0" )));


				this.colPuntuacio.setOnEditCommit(
						new EventHandler<CellEditEvent<Pregunta, Double>>() {
							@Override
							public void handle(CellEditEvent<Pregunta, Double> t) {
								Pregunta modificat = t.getTableView().getItems().get(t.getTablePosition().getRow() );
								try {
									modificat.setPuntuacio(t.getNewValue());
								} catch (Excepcio e) {
									MenuController.mostrarError("No s'ha pogut modificar la puntuació de la pregunta", e.getMessage());
								}
							}
						}
						);
			}

			if (this.colPregunta != null) {
				this.colPregunta.setMinWidth(200);
				this.colPregunta.setMaxWidth(250);
				this.colPregunta.setCellValueFactory(new PropertyValueFactory<Pregunta,String>("getText"));
				this.colPregunta.setCellFactory(TextFieldTableCell.forTableColumn());	// Edició

				this.colPregunta.setOnEditCommit(
						new EventHandler<CellEditEvent<Pregunta, String>>() {
							@Override
							public void handle(CellEditEvent<Pregunta, String> t) {
								try {
									Pregunta modificat = t.getTableView().getItems().get(t.getTablePosition().getRow() );
									modificat.setText(t.getNewValue());
								} catch (Excepcio e) {
									MenuController.mostrarError("No s'ha pogut modificar el text de la pregunta", e.getMessage());
								}
							}
						}
						);

			}

			if (this.colVerdaderFals != null) {
				this.colVerdaderFals.setMinWidth(50);
				this.colVerdaderFals.setMaxWidth(70);
				// define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
				this.colVerdaderFals.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pregunta, Boolean>, ObservableValue<Boolean>>() {
					@Override
					public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Pregunta, Boolean> cell) {
						return new SimpleBooleanProperty(cell.getValue().esVerdaderFals());
					}
				});
				this.colVerdaderFals.setCellFactory(new ColumnCheckBox<Pregunta, Boolean>(){
					@Override
					public void checkAction(Pregunta element, boolean value) {
						// No cal fer res, no és editable
					}
					@Override
					public boolean checkValue(Pregunta element) {
						return element.esVerdaderFals();
					}
					@Override
					public boolean checkEditable(Pregunta element) {
						return false;
					}
				});
			}

			if (this.colOpcions != null) {
				this.colOpcions.setMinWidth(50);
				this.colOpcions.setMaxWidth(70);
				this.colOpcions.setCellValueFactory(new PropertyValueFactory<Pregunta,Integer>("getTotalOpcions"));
			}


			if (this.colEsborrar != null) {
				this.colEsborrar.setMinWidth(100);
				this.colEsborrar.setMaxWidth(100);
				// define a simple boolean cell value for the action column so that the column will only be shown for non-empty rows.
				this.colEsborrar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pregunta, Boolean>, ObservableValue<Boolean>>() {
					@Override
					public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Pregunta, Boolean> button) {
						return new SimpleBooleanProperty(true);
					}
				});

				this.colEsborrar.setCellFactory(new ColumnButton<Pregunta, Boolean>( "Esborrar" ) {
					@Override
					public void buttonAction(Pregunta pregunta) {
						try {
							examen.esborrarPregunta(preguntes.indexOf(pregunta) + 1);
							preguntes.remove(pregunta);

							initFormExamen(examen);
						} catch (Excepcio e) {
							MenuController.mostrarError("No s'ha esborrat la pregunta", e.getMessage());
						}

					}
				});
			}

			if (this.preguntesTable != null) {
				this.preguntesTable.setEditable(true);
				this.preguntesTable.getSelectionModel().setCellSelectionEnabled(true);
				this.preguntesTable.setItems( this.preguntes );
				this.preguntesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			}
		}
	}


	@SuppressWarnings("unchecked")
	@FXML
	public void afegirOpcionsClick(ActionEvent event) throws IOException {

		/*
		ObservableList<Pregunta> preguntes;

		 *
		 */

		try {
			Dialog<List<String>> dialog = new Dialog<>();
			dialog.setTitle("Llista d'opcions");
			dialog.setHeaderText("Gestiona les opcions de la pregunta");

			// Només botó per tancar
			ButtonType acceptarButtonType = new ButtonType("Tancar", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(acceptarButtonType);

			GridPane vista = (GridPane)FXMLLoader.load(getClass().getResource("VistaDialegOpcions.fxml"));;
			dialog.getDialogPane().setContent(vista);

			this.btnAfegirOpcio = (Button) dialog.getDialogPane().lookup("#btnAfegirOpcio");
			this.btnEsborrarOpcio = (Button) dialog.getDialogPane().lookup("#btnEsborrarOpcio");
			this.txtOpcio = (TextField) dialog.getDialogPane().lookup("#txtOpcio");
			this.lstOpcions = (ListView<String>) dialog.getDialogPane().lookup("#lstOpcions");

			if (this.btnEsborrarOpcio == null || this.btnAfegirOpcio == null || this.txtOpcio == null || this.lstOpcions == null)
				throw new Excepcio("EdicioExamenController", "No s'ha pogut carregar el formulari correctament");

			this.btnEsborrarOpcio.setDisable(true);
			this.lstOpcions.setItems(opcions);

			this.btnAfegirOpcio.setOnAction(new EventHandler<ActionEvent>() {
			    @Override
			    public void handle(ActionEvent e) {
			    	afegirOpcioClick(e);
			    }
			});

			this.btnEsborrarOpcio.setOnAction(new EventHandler<ActionEvent>() {
			    @Override
			    public void handle(ActionEvent e) {
			    	esborrarOpcioClick(e);
			    	if (opcions.size() == 0) btnEsborrarOpcio.setDisable(true);
			    }
			});

			this.lstOpcions.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
						btnEsborrarOpcio.setDisable(false);
			        }
			});

			// Request focus on the txtOpcio field by default.
			Platform.runLater(() -> this.txtOpcio.requestFocus());

			dialog.showAndWait();

		} catch (Excepcio e) {
			MenuController.mostrarError("Error formulari d'opcions", e.getMessage());
		}
	}

	@FXML
	public void afegirOpcioClick(ActionEvent event) {
		try {
			if (this.txtOpcio == null) throw new Excepcio("EdicioExamenController", "No s'ha pogut afegir la nova opció correctament");

			if ("".equals(this.txtOpcio.getText())) throw new Excepcio("EdicioExamenController", "Cal indicar un text");

			if (this.opcions.contains(this.txtOpcio.getText())) throw new Excepcio("EdicioExamenController", "Aquesta opció ja existeix");

			this.opcions.add(this.txtOpcio.getText());
		} catch (Excepcio e) {
			MenuController.mostrarError("Error formulari d'opcions", e.getMessage());
		}
	}

	@FXML
	public void esborrarOpcioClick(ActionEvent event) {
		int selected = this.lstOpcions.getSelectionModel().getSelectedIndex();

		this.opcions.remove(selected);
	}

	@FXML
	public void novaPreguntaClick(ActionEvent event) {
		String text = "";
		double puntuacio = 0;

		try {
			if (this.txtPuntuacio != null) {
				StringConverter<Double> formatter = Main.getDecimalFormatter();
				puntuacio = formatter.fromString(this.txtPuntuacio.getText());
			}

			if (this.txtPregunta != null) text = this.txtPregunta.getText();


			RadioButton chk = (RadioButton)tipusPregunta.getSelectedToggle(); // Cast object to radio button

			String radio = "";
			if (chk != null && chk.getText() != null) radio = chk.getText();


			if (radio.equals(radioOberta.getText())) this.examen.afegirPreguntaOberta(text, puntuacio);

			if (radio.equals(radioVoF.getText())) this.examen.afegirPreguntaVerdaderFals(text, puntuacio);

			if (radio.equals(radioOpcions.getText())) {
				this.examen.afegirPreguntaOpcions(text, puntuacio, opcions.toArray(new String[opcions.size()]));
			}

			this.preguntes.clear();
			this.preguntes.addAll(this.examen.getPreguntes());

			this.initFormExamen(this.examen);

		} catch (Exception e) {
			MenuController.mostrarError("No s'ha pogut afegir la pregunta", e.getLocalizedMessage());
		}
	}

	private void initFormExamen(Examen examen) {
		// Edició d'un examen
		if (this.txtNum != null) {
			this.txtNum.setText( (this.examen.getPreguntes().size()+1)+"" );
		}

		if (this.txtPuntuacio != null) {
			this.txtPuntuacio.setTextFormatter(new TextFormatter<Double>(Main.getDecimalFormatter()));
			this.txtPuntuacio.setText("1");
		}

		if (this.txtPregunta != null) {
			this.txtPregunta.clear();
		}

		if (this.lblPreview != null) {
			this.lblPreview.setText(this.examen.getTitol()+this.examen.getEnunciat()+System.lineSeparator());
		}

		this.radioOberta.setSelected(true);
		this.btnOpcions.setDisable(true);

		this.opcions.clear();
	}


	@FXML
	public void generarPDFClick(ActionEvent event) {
		/* iText

		 https://github.com/itext/itext7/releases	=> baixar 7.0.5 	itext7-7.0.5.zip

		Libraries
			itext7-kernel-7.0.5.jar
			itext7-io-7.0.5.jar
			itext7-layout-7.0.5.jar

		Javadoc
			itext7-kernel-7.0.5-javadoc.jar
			itext7-io-7.0.5-javadoc.jar
			itext7-layout-7.0.5-javadoc.jar

Afegir *.jar i configurar Build Path

		https://www.slf4j.org/download.html	  (Simple Logging Facade for Java)	==> baixar slf4j-1.7.25.zip

		Libraries
			slf4j-api-1.7.25.jar

		Javadoc
			https://www.slf4j.org/apidocs/index.html

		https://developers.itextpdf.com/

		 */
		String htmlContent = this.examen.getTitolHtml()+this.examen.getEnunciatHtml()+System.lineSeparator();

		PdfManager manager;
		try {
			manager = new PdfManager("Examen.pdf");

			manager.generarPDF( htmlContent );

			File pdfGenerat = new File("Examen.pdf");

			HostServices hostServices = Model.getInstance().getApp().getHostServices();
	        hostServices.showDocument(pdfGenerat.getAbsolutePath());

		} catch (Excepcio e) {
			//e.printStackTrace();
		    MenuController.mostrarError("Examen a PDF", "Error generant el document. "+e.getMessage());
		}
	}
}



