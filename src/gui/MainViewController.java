package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() {
		System.out.printf("onMenuItemSellerAction%n");
	}
	@FXML
	public void onMenuItemDepartmentAction() {
		loadDepartmentView("/gui/DepartmentList.fxml");
	}
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}
	
	private synchronized void loadView(String absoluteName) {
		FXMLLoader loader;
		VBox newVBox, mainVBox;
		Scene mainScene;
		Node mainMenu;
		
		try {
			loader = new FXMLLoader(getClass().getResource(absoluteName));
			newVBox = loader.load();
			mainScene = Main.getMainScene();
			mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	private synchronized void loadDepartmentView(String absoluteName) {
		FXMLLoader loader;
		VBox newVBox, mainVBox;
		Scene mainScene;
		Node mainMenu;
		DepartmentListController controller;
		
		try {
			loader = new FXMLLoader(getClass().getResource(absoluteName));
			newVBox = loader.load();
			mainScene = Main.getMainScene();
			mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			controller = loader.getController();
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}
