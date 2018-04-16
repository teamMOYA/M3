package m3.uf6.examens;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import m3.uf6.examens.model.Estudiant;
import m3.uf6.examens.model.Examen;
import m3.uf6.examens.model.Excepcio;
import m3.uf6.examens.model.Model;
import m3.uf6.examens.model.UnitatFormativa;

public class MenuController implements Initializable {
	@FXML private BorderPane paneArrel;
	@FXML private AnchorPane paneVista;
	@FXML private GridPane vistaInici;

	@FXML private Button btnInit;
	@FXML private TextField txtUFS;
	@FXML private TextField txtExamens;
	@FXML private TextField txtEstudiants;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {

	}

	/**
	 * Called when Sortir menuItem is fired.
	 *
	 * @param event the action event.
	 */
	public void sortirMenuClick(ActionEvent event) {
		// System.exit(0); <= Preferible Platform.exit()
		Platform.exit();
	}

	@FXML
	public void iniciMenuClick(ActionEvent event) {
		this.obrirInici();
	}

	public void initData() {
		// Dades inicials
		try {
			Model.getInstance().init();
		} catch (Excepcio e) {
			MenuController.mostrarError("No s'ha pogut iniciar les dades", e.getMessage());
		}
	}

	public void obrirInici() {
		try {
			//GridPane vista = (GridPane)FXMLLoader.load(getClass().getResource("VistaInici.fxml"));
			if (this.vistaInici == null) this.vistaInici = (GridPane)FXMLLoader.load(getClass().getResource("VistaInici.fxml"));
			this.carregarVista(this.vistaInici);

			AnchorPane.setTopAnchor(this.vistaInici,40.0);
			AnchorPane.setLeftAnchor(this.vistaInici, 150.0);
			AnchorPane.setRightAnchor(this.vistaInici, 350.0);

			Stage stage = Model.getInstance().getStage();
			// Totals
			this.txtUFS = (TextField) stage.getScene().lookup("#txtUFS");
			this.txtExamens = (TextField) stage.getScene().lookup("#txtExamens");
			this.txtEstudiants = (TextField) stage.getScene().lookup("#txtEstudiants");
			if (this.txtUFS != null)  this.txtUFS.setText(Model.getInstance().getUnitats().size()+"");
			if (this.txtExamens != null)  this.txtExamens.setText(Model.getInstance().getExamens().size()+"");
			if (this.txtEstudiants != null)  this.txtEstudiants.setText(Model.getInstance().getEstudiants().size()+"");


		} catch (IOException e) {
			MenuController.mostrarError("No s'ha pogut mostrar la finestra d'inici", e.getMessage());
		}
	}

	/**
	 * Called when Obrir menuItem is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void obrirMenuClick(ActionEvent event) {
		// Llegir fitxer examens, carregar-los i actualitzar la llista d'alumnes als inscrits a l'examen
		try {
			File file = openFileChooser("Obrir fitxer per carregar dades dels exàmens", true);
			if (file != null) {
				// Recuperació
				XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)));

				@SuppressWarnings("unchecked")
				LinkedList<Examen> examens = (LinkedList<Examen>) d.readObject();

				Model.getInstance().setExamens(FXCollections.observableArrayList(examens));

				TreeSet<Estudiant> estudiants= new TreeSet<Estudiant>();  // Evita duplicats i ordena per nom

				for (Examen examen : examens) {
					estudiants.addAll(examen.getAlumnes());
				}

				Model.getInstance().setEstudiants(FXCollections.observableArrayList(estudiants));

				d.close();

				MenuController.mostrarInfo("Tot correcte", "Dades carregades sense incidències");

				this.obrirInici();
			}
		} catch (Exception e) {

			MenuController.mostrarError("No s'ha pogut carregar les dades del fitxer", e.getMessage());
		}
	}

	/**
	 * Called when Desar menuItem is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void desarMenuClick(ActionEvent event) {
		// Desar
		try {
			File file = openFileChooser("Obrir fitxer per desar les dades dels exàmens i alumnes", false);
			if (file != null) {

				XMLEncoder e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
				e.writeObject(new LinkedList<Examen>(Model.getInstance().getExamens()));

				e.close();

				MenuController.mostrarInfo("Tot correcte", "Dades desades sense incidències");
			}
		} catch (Exception e) {

			MenuController.mostrarError("No s'ha pogut desar les dades al fitxer", e.getMessage());
		}
	}


	private File openFileChooser(String title, boolean open) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.setInitialDirectory(
				new File(System.getProperty("user.home"))
				);
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("XML", "*.xml"),
				new FileChooser.ExtensionFilter("Tots", "*.*")
				);
		File file;
		if (open) file = fileChooser.showOpenDialog(Model.getInstance().getStage());
		else file = fileChooser.showSaveDialog(Model.getInstance().getStage());
		return file;
	}

	@FXML
	public void gestionarUnitatsFormativesMenuClick(ActionEvent event)  {
		try {
			BorderPane vista = (BorderPane)FXMLLoader.load(getClass().getResource("VistaUnitatsFormatives.fxml"));

			this.carregarVista(vista);
		} catch (Exception e) {
			e.printStackTrace();
			MenuController.mostrarError("No s'ha pogut mostrar la gestió d'Unitats Formatives", e.getMessage());
		}
	}

	@FXML
	public void gestionarAlumnesMenuClick(ActionEvent event)  {
		try {
			BorderPane vista = (BorderPane)FXMLLoader.load(getClass().getResource("VistaEstudiants.fxml"));

			this.carregarVista(vista);
		} catch (Exception e) {
			e.printStackTrace();
			MenuController.mostrarError("No s'ha pogut mostrar la gestió dels estudiants", e.getMessage());
		}
	}

	@FXML
	public void gestionarExamensMenuClick(ActionEvent event)  {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("VistaExamens.fxml"));
			BorderPane vista = (BorderPane) loader.load();
			loader.<ExamensController>getController().setMenu(this);

			this.carregarVista(vista);
		} catch (Exception e) {
			e.printStackTrace();
			MenuController.mostrarError("No s'ha pogut mostrar la gestió dels exàmens", e.getMessage());
		}
	}

	@FXML
	public void crearExamenMenuClick(ActionEvent event)  {

		try {
			ChoiceDialog<UnitatFormativa> dialogSeleccioUF = new ChoiceDialog<UnitatFormativa>(null, Model.getInstance().getUnitats());
			dialogSeleccioUF.setTitle("Nou Examen");
			dialogSeleccioUF.setHeaderText("Selecció de la Unitat Formativa");
			dialogSeleccioUF.setContentText("Escolleix una:");
			dialogSeleccioUF.setWidth(300.0);
			dialogSeleccioUF.getDialogPane().getStylesheets().add( getClass().getResource("application.css").toExternalForm());
			dialogSeleccioUF.getDialogPane().getStyleClass().add("dialeg-ufs");

			Optional<UnitatFormativa> result = dialogSeleccioUF.showAndWait();
			if (result.isPresent()){
				FXMLLoader loader = new FXMLLoader(getClass().getResource("VistaEdicioExamen.fxml"));
				BorderPane vista = (BorderPane) loader.load();
				loader.<EdicioExamenController>getController().initData(result.get(), null);

				this.carregarVista(vista);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MenuController.mostrarError("No s'ha pogut crear un nou examen", e.getMessage());
		}
	}

	@FXML
	public void avaluarExamenMenuClick(ActionEvent event)  {
		try {
			BorderPane vista = (BorderPane)FXMLLoader.load(getClass().getResource("VistaAvaluacio.fxml"));

			this.carregarVista(vista);

		} catch (Exception e) {
			e.printStackTrace();
			MenuController.mostrarError("No s'ha pogut mostrar l'avaluació d'examens", e.getMessage());
		}
	}


	@FXML
	public void consultesMenuClick(ActionEvent event)  {
		try {
			BorderPane vista = (BorderPane)FXMLLoader.load(getClass().getResource("VistaConsultes.fxml"));

			this.carregarVista(vista);

		} catch (Exception e) {
			e.printStackTrace();
			MenuController.mostrarError("No s'ha pogut mostrar les consultes", e.getMessage());
		}
	}

	/**
	 * Called when Registre Errors menuItem is fired.
	 *
	 * @param event the action event.
	 */
	@FXML
	public void registreErrorsMenuClick(ActionEvent event) {
		try {
			AnchorPane vista = (AnchorPane)FXMLLoader.load(getClass().getResource("VistaRegistreErrors.fxml"));

			this.carregarVista(vista);
		} catch (Exception e) {
			MenuController.mostrarError("No s'ha pogut mostrar el registre d'errors", "\""+e.getMessage()+"\"");
		}
	}

	/**
	 * Called when About menuItem is fired.
	 *
	 * @param event the action event.
	 * @throws IOException
	 */
	@FXML
	public void aboutMenuClick(ActionEvent event) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About ...");
		alert.setHeaderText(null);
		alert.setContentText("Copyright@" + Calendar.getInstance(new Locale("ES_CA")).get(Calendar.YEAR) + "\nÀlex Macia");
		alert.showAndWait();
	}

	public void carregarVista(Pane vista) {
		if (vista == null) return;

		if (checkSiVistaEstaCarregada(vista.getId())) return;

		this.paneVista.getChildren().clear();

		//paneArrel.setPrefHeight(paneArrel.getHeight() - 80.0);

		this.paneVista.getChildren().add(vista);

		AnchorPane.setTopAnchor(vista,0.0);
		AnchorPane.setBottomAnchor(vista,0.0);
		AnchorPane.setLeftAnchor(vista, 0.0);
		AnchorPane.setRightAnchor(vista, 0.0);
		//this.paneVista.setVisible(true);
	}

	private boolean checkSiVistaEstaCarregada(String id) {
		if (id == null || this.paneVista != null || this.paneVista.getChildren() != null) return false;

		Iterator<Node> fills = this.paneVista.getChildren().iterator();

		while (fills.hasNext()) {
			Node aux = fills.next();
			if (id.equals(aux.getId())) return true;
		}

		return false;
	}

	public static void mostrarError(String title, String sms) {
		mostrarAlert("Error", title, sms, AlertType.ERROR, "error-panel");
	}

	public static void mostrarInfo(String title, String sms) {
		mostrarAlert("Informació", title, sms, AlertType.INFORMATION, "info-panel");
	}

	private static void mostrarAlert(String title, String header, String sms, AlertType tipus, String paneId) {

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.getDialogPane().setId(paneId);
		alert.setHeaderText(header);
		alert.setContentText(sms);
		alert.showAndWait();

	}
}
