package m3.uf6.examens;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import m3.uf6.examens.model.Model;
import m3.uf6.examens.model.ModelQueryResult;

public class ConsultesController implements Initializable {

	@FXML private BorderPane vistaFormConsultes;

	@FXML private Label lblTitolQuery;
	@FXML private Label lblTotalCount;
	@FXML private Pagination pagControl;
	@FXML private Button btnRunQuery1;

	@FXML private TableView<Map<String, String>> taulaResultats;


	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rsrcs) {

		this.pagControl.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex >= pagControl.getPageCount()) {
                    return null;
                } else {
                	ModelQueryResult resultQuery = Model.getInstance().query1(pageIndex+1, 2);
                	loadResultats(resultQuery.getTitol(), resultQuery.getCount(), resultQuery.getNames(), resultQuery.getWidths(), resultQuery.getAligns(), resultQuery.getDataMap());
                    return new Label();
                }
            }
        });

	}

	@FXML
	public void runQuery1(ActionEvent event) {
		try {
			this.pagControl.setCurrentPageIndex(0);
			ModelQueryResult resultQuery = Model.getInstance().query1(1, 2);

			this.loadResultats(resultQuery.getTitol(), resultQuery.getCount(), resultQuery.getNames(), resultQuery.getWidths(), resultQuery.getAligns(), resultQuery.getDataMap());
		} catch (Exception e) {
			MenuController.mostrarError("Error en la consulta", e.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void loadResultats(String titol, int count, Vector<String> colNames, Vector<Integer> colWidths, Vector<String> colAligns, List<Map<String, String>> resultData) {
		/* Crear mapa <Integer, String>
		 *
		 * Llista de mapes on cada mapa representa una fila de la taula. Cada fila  Map<String, String>
		 *
		 * "col1" => "asdasd"
		 * "col2" => "asdasd"
		 * "col3" => "adasdasd"
		 * ..
		 *
		 * On 1 representa la columna 1 i "asdasd" la dada de la fila per la columna 1
		 */
		try {
			this.pagControl.setPageCount(Math.min(5, (int) Math.ceil((double) count/2) ));

			this.lblTotalCount.setText(count+"");
			this.lblTitolQuery.setText(titol);
			this.taulaResultats.getColumns().clear();
			// Crear taula resultats dinàmicament
			ObservableList<Map<String, String>> data = FXCollections.observableArrayList(resultData);
			this.taulaResultats.setItems(data);

			for (int col = 0; col < colNames.size(); col++) {
				TableColumn<Map<String, String>, String> tableCol = new TableColumn<Map<String, String>, String>(colNames.get(col));
				tableCol.setMinWidth(colWidths.get(col));
				tableCol.setPrefWidth(colWidths.get(col));
				tableCol.getStyleClass().add(colAligns.get(col));
				tableCol.setCellValueFactory(new MapValueFactory(colNames.get(col)));
				this.taulaResultats.getColumns().add(tableCol);
			}

		} catch (Exception e) {
			this.lblTitolQuery.setText("---");
			this.lblTotalCount.setText("--");
			this.taulaResultats.getColumns().clear();
			MenuController.mostrarError("No es pot carregar la consulta", e.getMessage());
		}
	}
}



