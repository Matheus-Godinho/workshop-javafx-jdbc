package gui.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

public class Utils {
	
	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}
	
	public static Integer tryParseToInt(String string) {
		try {
			return Integer.parseInt(string);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
	
	public static <T> void formatTableColumnDate(TableColumn<T, Date> tableColumn, String format) {
		tableColumn.setCellFactory(column -> new TableCell<T, Date>() {
			private final SimpleDateFormat sdf = new SimpleDateFormat(format);
			
			@Override
			protected void updateItem(Date item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? null : sdf.format(item));
			}
		});
	}
	
	public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, Integer decimalPlaces) {
		tableColumn.setCellFactory(column -> new TableCell<T, Double>() {
			@Override
			protected void updateItem(Double item, boolean empty) {
				super.updateItem(item, empty);
				Locale.setDefault(Locale.US);
				setText(empty ? null : String.format("%." + decimalPlaces + "f", item));
			}
		});
	}
	


}
