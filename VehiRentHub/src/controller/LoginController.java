package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import utility.DatabaseConnection;
import Model.fetchData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController implements Initializable {

	@FXML
	private AnchorPane mainForm;

	@FXML
	private TextField username;

	@FXML
	private PasswordField password;

	@FXML
	private Button loginButton;

	@FXML
	private Button close;

//    DATABASE TOOLS
	private Connection connection;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;

	private double x = 0;
	private double y = 0;

	public void loginFun() {

		String sql = "SELECT * FROM admin WHERE username = ? and password = ?";

		connection = DatabaseConnection.databaseConnection();

		try {
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, username.getText());
			preparedStatement.setString(2, password.getText());

			resultSet = preparedStatement.executeQuery();
			Alert alert;

			if (username.getText().isEmpty() || password.getText().isEmpty()) {
				alert = new Alert(AlertType.ERROR);
				alert.setHeaderText(null);
				alert.setContentText("Please fill all blank fields");
				alert.showAndWait();
			} else {
				if (resultSet.next()) {

					fetchData.username = username.getText();

					alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText(null);
					alert.setContentText("Login Successful!");
					alert.showAndWait();
					// HIDE YOUR LOGIN FORM
					loginButton.getScene().getWindow().hide();

					// LINK YOUR DASHBOARD FORM
					Parent root = FXMLLoader.load(getClass().getResource("../dashboard.fxml"));
					Stage stage = new Stage();
					Scene scene = new Scene(root);

					root.setOnMousePressed((MouseEvent event) -> {
						x = event.getSceneX();
						y = event.getSceneY();
					});

					root.setOnMouseDragged((MouseEvent event) -> {
						stage.setX(event.getScreenX() - x);
						stage.setY(event.getScreenY() - y);
					});

					stage.initStyle(StageStyle.TRANSPARENT);

					stage.setScene(scene);
					stage.show();

				} else {
					alert = new Alert(AlertType.ERROR);
					alert.setHeaderText(null);
					alert.setContentText("Incorrect Username/Password. Please recheck your credentials!");
					alert.showAndWait();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void close() {
		System.exit(0);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO
	}

}
