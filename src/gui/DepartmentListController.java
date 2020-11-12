package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener {
	
	private DepartmentService service;
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	@FXML
	private Button buttonNew;
	@FXML
	private TableView<Department> tableViewDepartment;
		private ObservableList<Department> obsList;
		@FXML
		private TableColumn<Department, Integer> tableColumnId;
		@FXML
		private TableColumn<Department, String> tableColumnName;
	
	@FXML
	public void onButtonNewAction(ActionEvent event) {
		Department department;
		Stage parentStage;
		
		department = new Department();
		parentStage = Utils.currentStage(event);
		createDialogForm("/gui/DepartmentForm.fxml", department, parentStage);
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
	
	public void updateTableView() {
		if (service == null)
			Alerts.showAlert("Illegal State Exception", "Error updating table view",
					"Service was null", AlertType.ERROR);
		else {
			List<Department> list;
			
			list = service.findAll();
			obsList = FXCollections.observableArrayList(list);
			tableViewDepartment.setItems(obsList);
		}
	}
	@Override
	public void onDataChanged() {
		updateTableView();
	}
	
	private void createDialogForm(String absoluteName, Department department, Stage parentStage) {
		FXMLLoader loader;
		Pane pane;
		DepartmentFormController controller;
		Stage dialogStage;
		
		try {
			loader = new FXMLLoader(getClass().getResource(absoluteName));
			pane = loader.load();
			controller = loader.getController();
			controller.setDepartment(department);
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error in loading view",
					e.getMessage(), AlertType.ERROR);
		}
		
	}

}
