package gui;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;

public class DepartmentListController implements Initializable {

	@FXML
	private Button buttonNew;
	@FXML
	private TableView<Department> tableViewDepartment;
		@FXML
		private TableColumn<Department, Integer> tableColumnId;
		@FXML
		private TableColumn<Department, String> tableColumnName;
	
	@FXML
	public void onButtonNewAction() {
		System.out.printf("onButtonNewAction%n");
	}
	
	private void initializeNodes() {
		Stage stage;
		
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

}
