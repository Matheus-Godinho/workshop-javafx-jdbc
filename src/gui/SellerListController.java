package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {
	
	private SellerService service;
	
	public void setSellerService(SellerService service) {
		this.service = service;
	}

	@FXML
	private Button buttonNew;
	@FXML
	private TableView<Seller> tableViewSeller;
		private ObservableList<Seller> obsList;
		@FXML
		private TableColumn<Seller, Integer> tableColumnId;
		@FXML
		private TableColumn<Seller, String> tableColumnName;
		@FXML
		private TableColumn<Seller, String> tableColumnEmail;
		@FXML
		private TableColumn<Seller, Date> tableColumnBirthDate;
		@FXML
		private TableColumn<Seller, Double> tableColumnBaseSalary;
		@FXML
		private TableColumn<Seller, Seller> tableColumnEDIT;
		@FXML
		private TableColumn<Seller, Seller> tableColumnREMOVE;
	
	@FXML
	public void onButtonNewAction(ActionEvent event) {
		Seller seller;
		Stage parentStage;
		
		seller = new Seller();
		parentStage = Utils.currentStage(event);
		createDialogForm("/gui/SellerForm.fxml", seller, parentStage);
	}
	
	private void initializeNodes() {
		Stage stage;
		
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("BirthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("BaseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
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
			List<Seller> list;
			
			list = service.findAll();
			obsList = FXCollections.observableArrayList(list);
			tableViewSeller.setItems(obsList);
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
		tableColumnEDIT.setCellFactory(parameter -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Edit");
			
			@Override
			protected void updateItem(Seller seller, boolean empty) {
				super.updateItem(seller, empty);
				if (seller == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm("/gui/SellerForm.fxml",
						seller, Utils.currentStage(event)));
			}
		});
	}
	
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(parameter -> new ReadOnlyObjectWrapper<>(parameter.getValue()));
		tableColumnREMOVE.setCellFactory(parameter -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Remove");
			
			@Override
			protected void updateItem(Seller seller, boolean empty) {
				super.updateItem(seller, empty);
				if (seller == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(seller));
			}
		});
	}
	
	private void removeEntity(Seller seller) {
		if (service == null)
			Alerts.showAlert("Illegal State Exception", "Error in removing seller",
					"Service was null", AlertType.ERROR);
		else {
			Optional<ButtonType> result;
			
			try {
				result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
				if (result.get() == ButtonType.OK) {
					service.remove(seller);
					updateTableView();
				}
			}
			catch (DbException e) {
				Alerts.showAlert("Database Exception", "Error in removing seller",
						e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
	private void createDialogForm(String absoluteName, Seller seller, Stage parentStage) {
		FXMLLoader loader;
		Pane pane;
		SellerFormController controller;
		Stage dialogStage;
		
		try {
			loader = new FXMLLoader(getClass().getResource(absoluteName));
			pane = loader.load();
			controller = loader.getController();
			controller.setSeller(seller);
			controller.setSellerService(new SellerService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data");
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
