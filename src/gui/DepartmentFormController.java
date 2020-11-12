package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	private Department entity;
	private DepartmentService service;
	private List<DataChangeListener> dataChangeListeners;
	
	public DepartmentFormController() {
		dataChangeListeners = new ArrayList<>();
	}
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private Label labelErrorName;
	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	
	@FXML
	public void onButtonSaveAction(ActionEvent event) {
		if (entity == null)
			Alerts.showAlert("Illegal State Exception", "Error in saving department",
					"Entity was null", AlertType.ERROR);
		if (service == null)
			Alerts.showAlert("Illegal State Exception", "Error in saving department",
					"Service was null", AlertType.ERROR);
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		}
		catch (DbException e) {
			Alerts.showAlert("Database Exception", "Error in saving department",
					e.getMessage(), AlertType.ERROR);
		}
	}
	@FXML
	public void onButtonCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private Department getFormData() {
		Department department;
		
		department = new Department();
		department.setId(Utils.tryParseToInt(txtId.getText()));
		department.setName(txtName.getText());
		return department;
	}
	public void updateFormData() {
		if (entity == null)
			Alerts.showAlert("Illegal State Exception", "Error in updating form data",
					"Entity was null", AlertType.ERROR);
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
	private void notifyDataChangeListeners() {
		dataChangeListeners.forEach(listener -> listener.onDataChanged());
	}

}
