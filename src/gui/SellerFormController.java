package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {
	
	private Seller entity;
	private SellerService service;
	private DepartmentService departmentService;
	private List<DataChangeListener> dataChangeListeners;
	
	public SellerFormController() {
		dataChangeListeners = new ArrayList<>();
	}
	
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpBirthDate;
	@FXML
	private TextField txtBaseSalary;
	@FXML
	private ComboBox<Department> cbDepartment;
		private ObservableList<Department> obsList;
	@FXML
	private Label labelErrorName;
	@FXML
	private Label labelErrorEmail;
	@FXML
	private Label labelErrorBirthDate;
	@FXML
	private Label labelErrorBaseSalary;
	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	
	@FXML
	public void onButtonSaveAction(ActionEvent event) {
		if (entity == null || service == null) {
			if (entity == null)
				Alerts.showAlert("Illegal State Exception", "Error in saving seller",
						"Entity was null", AlertType.ERROR);
			if (service == null)
				Alerts.showAlert("Illegal State Exception", "Error in saving seller",
						"Service was null", AlertType.ERROR);
		}
		else {
			try {
				entity = getFormData();
				service.saveOrUpdate(entity);
				notifyDataChangeListeners();
				Utils.currentStage(event).close();
			}
			catch (ValidationException e) {
				setErrorMessages(e.getErrors());
			}
			catch (DbException e) {
				Alerts.showAlert("Database Exception", "Error in saving seller",
						e.getMessage(), AlertType.ERROR);
			}
		}
	}
	@FXML
	public void onButtonCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		Constraints.setTextFieldDouble(txtBaseSalary);
		initializeCbDepartment();
	}
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeCbDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory;
		
		factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department department, boolean empty) {
				super.updateItem(department, empty);
				setText(empty ? "" : department.getName());
			}
		};
		cbDepartment.setCellFactory(factory);
		cbDepartment.setButtonCell(factory.call(null));
	}
	
	public void loadAssociatedObjects() {
		if (departmentService == null)
			Alerts.showAlert("Illegal State Exception", "Error in loading associated objects",
					"DepartmentService was null", AlertType.ERROR);
		else {
			List<Department> list;
			
			list = departmentService.findAll();
			obsList = FXCollections.observableArrayList(list);
			cbDepartment.setItems(obsList);
		}
	}
	
	private Seller getFormData() {
		ValidationException exception;
		Seller seller;
		
		exception = new ValidationException("Validation error");
		seller = new Seller();
		seller.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtName.getText() == null || txtName.getText().trim().equals(""))
			exception.addError("Name", "Field can't be empty");
		seller.setName(txtName.getText());
		if (exception.getErrors().size() > 0)
			throw exception;
		return seller;
	}
	public void updateFormData() {
		if (entity == null)
			Alerts.showAlert("Illegal State Exception", "Error in updating form data",
					"Entity was null", AlertType.ERROR);
		else {
			txtId.setText(String.valueOf(entity.getId()));
			txtName.setText(entity.getName());
			txtEmail.setText(entity.getEmail());
			if (entity.getBirthDate() != null)
				dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
			Locale.setDefault(Locale.US);
			txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
			if (entity.getDepartment() == null)
				cbDepartment.getSelectionModel().selectFirst();
			else
				cbDepartment.setValue(entity.getDepartment());
		}
	}
	
	private void notifyDataChangeListeners() {
		dataChangeListeners.forEach(listener -> listener.onDataChanged());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields;
		
		fields = errors.keySet();
		if (fields.contains("Name")) {
			labelErrorName.setText(errors.get("Name"));
		}
	}

}
