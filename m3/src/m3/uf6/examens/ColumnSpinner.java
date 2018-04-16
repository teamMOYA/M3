package m3.uf6.examens;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/* 	Column Spinner
  	this.colEsborrar.setCellFactory(new ColumnSpinner<ClasseModel, T>( ) {
		@Override
		public Integer decrementSpinnerValue(T value, int steps) {
			return value<=Estudiant.MIN_EDAT?value:value-steps;
		}

		@Override
		public Integer incrementSpinnerValue(T value, int steps) {
			return value>=100?value:value+steps;
		}

		@Override
		public void spinnerAction(int index, T newValue) {
			// Actualitzacio
		}
	});
 */
public abstract class ColumnSpinner<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
	protected T maxValue;
	protected T minValue;

	public ColumnSpinner(T minValue, T maxValue) {
		this.maxValue = maxValue;
		this.minValue = minValue;
	}

	public abstract T decrementSpinnerValue(T value, int steps);

	public abstract T incrementSpinnerValue(T value, int steps);

	public abstract boolean checkSpinnerBounds(T value);	// True value dins límits

	public abstract void spinnerAction(int index, T newValue) throws Exception;

	@Override
	public TableCell<S, T> call( TableColumn<S, T> param )
	{
		TableCell<S, T> cell = new TableCell<S, T>()
		{
			private SpinnerValueFactory<T> factory;
			private Spinner<T> spinner;
			private ChangeListener<T> listener;

			{
				this.factory = new SpinnerValueFactory<T>() {
					@Override
					public void decrement(int steps) {
						this.setValue( decrementSpinnerValue(this.getValue(), steps) );
					}
					@Override
					public void increment(int steps) {
						this.setValue( incrementSpinnerValue(this.getValue(), steps) );
					}
				};

				this.spinner = new Spinner<T>(this.factory);
				this.spinner.setVisible(false);
				setGraphic(this.spinner);

				this.listener = new ChangeListener<T>() {
					@Override
					public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
						if (oldValue == null || newValue == null) return;
						if (oldValue.equals(newValue)) return;
						if (!checkSpinnerBounds(newValue)) return;

						try {
							spinnerAction(getIndex(), newValue);
						} catch (Exception e) {
							spinner.valueProperty().removeListener(listener);
							factory.setValue(oldValue);
						}
					}
				};


				this.spinner.valueProperty().addListener(listener);
			}


			@Override
			public void updateItem( T item, boolean empty )
			{
				// treure listener
				this.spinner.valueProperty().removeListener(listener);

				super.updateItem( item, empty );

				if (item == null || empty) {
					this.spinner.setVisible(false);
				} else {
					this.factory.setValue(item);
					this.spinner.valueProperty().addListener(listener);
					this.spinner.setVisible(true);
				}
			}
		};
		return cell;
	}
}
