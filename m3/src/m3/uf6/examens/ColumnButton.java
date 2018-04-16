package m3.uf6.examens;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/* 	Column button
  	this.colEsborrar.setCellFactory(new ColumnButton<ClasseModel, Boolean>("Text"){
		@Override
		public void buttonAction(ClasseModel element) {
			// Acció ...
		}
	});
 */
public abstract class ColumnButton<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
	private String text;

    public ColumnButton(String text) {
        super();
        this.text = text;
    }

    public abstract void buttonAction(S element);

    @Override
	public TableCell<S, T> call( TableColumn<S, T> param )
	{
		TableCell<S, T> cell = new TableCell<S, T>()
		{
			@Override
			public void updateItem( T item, boolean empty )
			{
				super.updateItem( item, empty );

				if (item == null || empty) {

                    setGraphic( null );
                } else {
                	Button btn = new Button( text );

					btn.setOnAction( ( ActionEvent event ) ->
					{
						S element = getTableView().getItems().get( getIndex() );
						buttonAction(element);

					} );
					setGraphic( btn );
				}
			}
		};
		return cell;
	}


}
