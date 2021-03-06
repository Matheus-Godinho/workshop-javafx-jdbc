package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
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
		private TableColumn<Department, Department> tableColumnEDIT;
		@FXML
		private TableColumn<Department, Department> tableColumnREMOVE;
	
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
			initEditButtons();
			initRemoveButtons();
		}
	}
	@Override
	public void onDataChanged() {
		updateTableView();
	}
	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(parameter -> new ReadOnlyObjectWrapper<>(parameter.getValue()));
		tableColumnEDIT.setCellFactory(parameter -> new TableCell<Department, Department>() {
			private final Button button = new Button("Edit");
			
			@Override
			protected void updateItem(Department department, boolean empty) {
				super.updateItem(department, empty);
				if (department == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm("/gui/DepartmentForm.fxml",
						department, Utils.currentStage(event)));
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(parameter -> new ReadOnlyObjectWrapper<>(parameter.getValue()));
		tableColumnREMOVE.setCellFactory(parameter -> new TableCell<Department, Department>() {
			private final Button button = new Button("Remove");
			
			@Override
			protected void updateItem(Department department, boolean empty) {
				super.updateItem(department, empty);
				if (department == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(department));
			}
		});
	}
	
	private void removeEntity(Department department) {
		if (service == null)
			Alerts.showAlert("Illegal State Exception", "Error in removing department",
					"Service was null", AlertType.ERROR);
		else {
			Optional<ButtonType> result;
			
			try {
				result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
				if (result.get() == ButtonType.OK) {
					service.remove(department);
					updateTableView();
				}
			}
			catch (DbException e) {
				Alerts.showAlert("Database Exception", "Error in removing department",
						e.getMessage(), AlertType.ERROR);
			}
		}
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
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error in loading view",
					e.getMessage(), AlertType.ERROR);
		}
		
	}

}
