package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Alerts;
import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable {
	
	private Department entity;
	
	public void setDepartment(Department entity) {
		this.entity = entity;
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
	public void onButtonSaveAction() {
		System.out.printf("onButtonSaveAction%n");
	}
	@FXML
	public void onButtonCancelAction() {
		System.out.printf("onButtonCancelAction%n");
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	public void updateFormData() {
		if (entity == null)
			Alerts.showAlert("Illegal State Exception", "Error in updating form data",
					"Entity was null", AlertType.ERROR);
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}

}
