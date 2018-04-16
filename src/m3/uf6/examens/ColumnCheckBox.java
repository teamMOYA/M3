package m3.uf6.examens;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/* 	Column button
  	this.colCheck.setCellFactory(new ColumnCheckBox<ClasseModel, Boolean>(enabled){
		@Override
		public void checkAction(ClasseModel element, boolean value) {
			// Acció ...
		}
		public boolean checkValue(ClasseModel element) {
			return ??;
		}
	});
 */
abstract class ColumnCheckBox<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    public ColumnCheckBox() {
        super();
    }

    public abstract boolean checkValue(S element);

    public abstract boolean checkEditable(S element);

    public abstract void checkAction(S element, boolean value);

    @Override
	public TableCell<S, T> call( TableColumn<S, T> param )
	{
		TableCell<S, T> cell = new TableCell<S, T>()
		{
			public void updateItem( T item, boolean empty )
			{
				super.updateItem( item, empty );

				if (item == null || empty) {

                    setGraphic( null );
                } else {
                	S element = getTableView().getItems().get( getIndex() );

                	CheckBox checkBox = new CheckBox();
                	checkBox.setDisable(!checkEditable(element));
                	checkBox.setSelected(checkValue(element));

                	checkBox.selectedProperty().addListener(new ChangeListener<Boolean> () {
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if(!isEditing()) checkAction(element, newValue == null ? false : newValue);
                        }
                    });
                	this.setEditable(checkEditable(element));
                	this.setGraphic(checkBox);
				}
			}
		};
		return cell;
	}


}
