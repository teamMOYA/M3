package m3.uf6.examens;

import java.text.DecimalFormat;
import java.text.ParseException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import m3.uf6.examens.model.Model;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			//VBox root = (VBox)FXMLLoader.load(getClass().getResource("VistaAppAvaluacioGUI.fxml"));

			FXMLLoader loader = new FXMLLoader(getClass().getResource("VistaAppAvaluacioGUI.fxml"));
			VBox root = (VBox)loader.load();

			MenuController controller = loader.getController();
			//controller.setStage(primaryStage);	// Permet carregar stage al Controller

			Scene scene = new Scene(root,900,600);

			//scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Catamaran:regular,bold,bolditalic");
			scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Catamaran");
			scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Dosis");
			scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Special+Elite");
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());	// No cal, es carrega a les vistes

			primaryStage.setScene(scene);

			Model.getInstance().setStage(primaryStage);
			Model.getInstance().setApp(this);

			//controller.initData();
			controller.obrirInici();
			//primaryStage.setResizable(false);
			primaryStage.setMinWidth(900);
			primaryStage.setMaxWidth(1200);
			primaryStage.setMinHeight(600);
			primaryStage.setMaxHeight(800);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() throws Exception {
		Model.getInstance().closeEM();

		super.stop();
	}

	public static void main(String[] args) {
		launch(args);
	}


	public static StringConverter<Double> getDecimalFormatter() {
		StringConverter<Double> formatter = new StringConverter<Double>()
		{
			@Override
			public Double fromString(String string)
			{
				DecimalFormat df = new DecimalFormat( "#0.0" );
				Double value;
				try {
					//value = nf.parse(string).doubleValue();
					value = df.parse(string).doubleValue();
				} catch (ParseException e) {
					value = 0.0;
				}
				return value;
			}

			@Override
			public String toString(Double object)
			{
				DecimalFormat df = new DecimalFormat( "#0.0" );

				return (object == null)?df.format(0.0):df.format(object);
			}
		};
		return formatter;
	}
}
