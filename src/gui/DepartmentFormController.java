package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepartmentFormController implements Initializable {
	
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

}