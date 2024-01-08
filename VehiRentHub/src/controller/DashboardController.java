package controller;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import Model.fetchData;
import Model.carData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utility.DatabaseConnection;

public class DashboardController implements Initializable {

	// UI elements from dashboard.fxml
    @FXML
    private AnchorPane mainForm;
    @FXML
    private Button close;
    @FXML
    private Button minimizeWindow;
    @FXML
    private Label mUsername;
    @FXML
    private Button homeButton;
    @FXML
    private Button logoutFunButton;
    @FXML
    private Button availableCarsButton;
    @FXML
    private Button rentCarButton;
    @FXML
    private Label availableCarsHome;
    @FXML
    private Label totalIncomeHome;
    @FXML
    private Label totalCustomersHome;
    @FXML
    private BarChart<?, ?> incomeChartHome;
    @FXML
    private LineChart<?, ?> customerChartHome;
    @FXML
    private AnchorPane formAvailableCars;
    @FXML
    private TextField availableCarId;
    @FXML
    private TextField availableCarsBrand;
    @FXML
    private TextField availableCarsModel;
    @FXML
    private ComboBox<?> availableCarsStatus;
    @FXML
    private ImageView availableCarsImageView;
    @FXML
    private Button availableCarsImportButton;
    @FXML
    private Button availableCarsInsertButton;
    @FXML
    private Button availableCarsUpdateButton;
    @FXML
    private Button availableCarsDeleteButton;
    @FXML
    private Button availableCarsClearButton;
    @FXML
    private TextField availableCarsPrice;
    @FXML
    private TableView<carData> availableCarsTableView;
    @FXML
    private TableColumn<carData, String> availableCarsIdColumn;
    @FXML
    private TableColumn<carData, String> availableCarsBrandColumn;
    @FXML
    private TableColumn<carData, String> availableCarsModelColumn;
    @FXML
    private TableColumn<carData, String> availableCarsPriceColumn;
    @FXML
    private TableColumn<carData, String> availableCarsStatusColumn;
    @FXML
    private TextField availableCarsSearch;
    @FXML
    private AnchorPane formRent;
    @FXML
    private ComboBox<?> rentCarId;
    @FXML
    private ComboBox<?> rentBrand;
    @FXML
    private ComboBox<?> rentModel;
    @FXML
    private TextField rentFirstName;
    @FXML
    private TextField rentLastName;
    @FXML
    private ComboBox<?> rentGender;
    @FXML
    private DatePicker rentDateFunRented;
    @FXML
    private DatePicker rentDateFunReturn;
    @FXML
    private Button rentButton;
    @FXML
    private Label rentTotal;
    @FXML
    private TextField rentAmountFun;
    @FXML
    private Label rentBalance;
    @FXML
    private AnchorPane formHome;
    @FXML
    private TableView<carData> rentTableView;
    @FXML
    private TableColumn<carData, String> rentCarIdColumn;
    @FXML
    private TableColumn<carData, String> rentBrandColumn;
    @FXML
    private TableColumn<carData, String> rentModelColumn;
    @FXML
    private TableColumn<carData, String> rentPriceColumn;
    @FXML
    private TableColumn<carData, String> rentStatusColumn;

    // For Database
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private Statement statement;

    // For uploading and downloading Image
    private Image image;

    
    public void availableCarsFun(){
        String sql = "SELECT COUNT(id) FROM car WHERE status = 'Available'";
        
        connection = DatabaseConnection.databaseConnection();
        int countAC = 0;
        try{
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            
            while(resultSet.next()){
                countAC = resultSet.getInt("COUNT(id)");
            }
            
            availableCarsHome.setText(String.valueOf(countAC));
            
        }catch(Exception e){e.printStackTrace();}
        
    }
    
    public void totalIncomeFun(){
        String sql = "SELECT SUM(total) FROM customer";
        
        double sumIncome = 0;
        
        connection = DatabaseConnection.databaseConnection();
        
        try{
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            
            while(resultSet.next()){
                sumIncome = resultSet.getDouble("SUM(total)");
            }
            totalIncomeHome.setText("$" + String.valueOf(sumIncome));
        }catch(Exception e){e.printStackTrace();}
    }
    
    
    public void totalCustomersFun(){
        
        String sql = "SELECT COUNT(id) FROM customer";
        
        int countTC = 0;
        
        connection = DatabaseConnection.databaseConnection();
        
        try{
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            
            while(resultSet.next()){
                countTC = resultSet.getInt("COUNT(id)");
            }
            totalCustomersHome.setText(String.valueOf(countTC));
        }catch(Exception e){e.printStackTrace();}
        
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void IncomeChartFun(){
        
        incomeChartHome.getData().clear();
        
        String sql = "SELECT date_rented, SUM(total) FROM customer GROUP BY date_rented ORDER BY TIMESTAMP(date_rented) ASC LIMIT 6";
        
        connection = DatabaseConnection.databaseConnection();
        
        try{
            @SuppressWarnings("rawtypes")
			XYChart.Series chart = new XYChart.Series();
            
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            
            while(resultSet.next()){
                chart.getData().add(new XYChart.Data(resultSet.getString(1), resultSet.getInt(2)));
            }
            
            incomeChartHome.getData().add(chart);
            
        }catch(Exception e){e.printStackTrace();}
    }
    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void CustomerChartFun(){
        customerChartHome.getData().clear();
        
        String sql = "SELECT date_rented, COUNT(id) FROM customer GROUP BY date_rented ORDER BY TIMESTAMP(date_rented) ASC LIMIT 4";
        
        connection = DatabaseConnection.databaseConnection();
        
        try{
            @SuppressWarnings("rawtypes")
			XYChart.Series chart = new XYChart.Series();
            
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            
            while(resultSet.next()){
                chart.getData().add(new XYChart.Data(resultSet.getString(1), resultSet.getInt(2)));
            }
            
            customerChartHome.getData().add(chart);
            
        }catch(Exception e){e.printStackTrace();}
        
    }
    
    public void carAddFun() {

        String sql = "INSERT INTO car (car_id, brand, model, price, status, image, date) "
                + "VALUES(?,?,?,?,?,?,?)";

        connection = DatabaseConnection.databaseConnection();

        try {
            Alert alert;

            if (availableCarId.getText().isEmpty()
                    || availableCarsBrand.getText().isEmpty()
                    || availableCarsModel.getText().isEmpty()
                    || availableCarsStatus.getSelectionModel().getSelectedItem() == null
                    || availableCarsPrice.getText().isEmpty()
                    || fetchData.path == null || fetchData.path == "") {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            } else {
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, availableCarId.getText());
                preparedStatement.setString(2, availableCarsBrand.getText());
                preparedStatement.setString(3, availableCarsModel.getText());
                preparedStatement.setString(4, availableCarsPrice.getText());
                preparedStatement.setString(5, (String) availableCarsStatus.getSelectionModel().getSelectedItem());

                String uri = fetchData.path;
                uri = uri.replace("\\", "\\\\");

                preparedStatement.setString(6, uri);

                Date date = new Date();
                java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                preparedStatement.setString(7, String.valueOf(sqlDate));

                preparedStatement.executeUpdate();

                alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Added!");
                alert.showAndWait();

                carShowListDataFun();
                carClearFun();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void CarUpdateFun() {

        String uri = fetchData.path;
        uri = uri.replace("\\", "\\\\");

        String sql = "UPDATE car SET brand = '" + availableCarsBrand.getText() + "', model = '"
                + availableCarsModel.getText() + "', status ='"
                + availableCarsStatus.getSelectionModel().getSelectedItem() + "', price = '"
                + availableCarsPrice.getText() + "', image = '" + uri
                + "' WHERE car_id = '" + availableCarId.getText() + "'";

        connection = DatabaseConnection.databaseConnection();

        try {
            Alert alert;

            if (availableCarId.getText().isEmpty()
                    || availableCarsBrand.getText().isEmpty()
                    || availableCarsModel.getText().isEmpty()
                    || availableCarsStatus.getSelectionModel().getSelectedItem() == null
                    || availableCarsPrice.getText().isEmpty()
                    || fetchData.path == null || fetchData.path == "") {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            } else {
                alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to UPDATE Car ID: " + availableCarId.getText() + "?");
                Optional<ButtonType> option = alert.showAndWait();

                if (option.get().equals(ButtonType.OK)) {
                    statement = connection.createStatement();
                    statement.executeUpdate(sql);

                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Updated!");
                    alert.showAndWait();

                    carShowListDataFun();
                    carClearFun();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void carDeleteFun() {

        String sql = "DELETE FROM car WHERE car_id = '" + availableCarId.getText() + "'";

        connection = DatabaseConnection.databaseConnection();

        try {
            Alert alert;
            if (availableCarId.getText().isEmpty()
                    || availableCarsBrand.getText().isEmpty()
                    || availableCarsModel.getText().isEmpty()
                    || availableCarsStatus.getSelectionModel().getSelectedItem() == null
                    || availableCarsPrice.getText().isEmpty()
                    || fetchData.path == null || fetchData.path == "") {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            } else {
                alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Message");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to DELETE Car ID: " + availableCarId.getText() + "?");
                Optional<ButtonType> option = alert.showAndWait();

                if (option.get().equals(ButtonType.OK)) {
                    statement = connection.createStatement();
                    statement.executeUpdate(sql);

                    alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Successfully Deleted!");
                    alert.showAndWait();

                    carShowListDataFun();
                    carClearFun();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void carClearFun() {
        availableCarId.setText("");
        availableCarsBrand.setText("");
        availableCarsModel.setText("");
        availableCarsStatus.getSelectionModel().clearSelection();
        availableCarsPrice.setText("");

        fetchData.path = "";

        availableCarsImageView.setImage(null);

    }

    private String[] listStatus = {"Available", "Not Available"};

    @SuppressWarnings("unchecked")
	public void carStatusListFun() {

        List<String> listS = new ArrayList<>();

        for (String data : listStatus) {
            listS.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(listS);
        availableCarsStatus.setItems(listData);
    }

    public void carImportImageFun() {

        FileChooser open = new FileChooser();
        open.setTitle("Open Image File");
        open.getExtensionFilters().add(new ExtensionFilter("Image File", "*jpg", "*png", "*.jpeg"));

        File file = open.showOpenDialog(mainForm.getScene().getWindow());

        if (file != null) {

            fetchData.path = file.getAbsolutePath();

            image = new Image(file.toURI().toString(), 116, 153, false, true);
            availableCarsImageView.setImage(image);

        }

    }

    public ObservableList<carData> carListDataFun() {

        ObservableList<carData> listData = FXCollections.observableArrayList();

        String sql = "SELECT * FROM car";

        connection = DatabaseConnection.databaseConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            carData carD;

            while (resultSet.next()) {
                carD = new carData(resultSet.getInt("car_id"),
                         resultSet.getString("brand"),
                         resultSet.getString("model"),
                         resultSet.getDouble("price"),
                         resultSet.getString("status"),
                         resultSet.getString("image"),
                         resultSet.getDate("date"));

                listData.add(carD);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }

    private ObservableList<carData> availableCarList;

    public void carShowListDataFun() {
        availableCarList = carListDataFun();

        availableCarsIdColumn.setCellValueFactory(new PropertyValueFactory<>("carId"));
        availableCarsBrandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        availableCarsModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        availableCarsPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        availableCarsStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        availableCarsTableView.setItems(availableCarList);
    }

    public void carSearchFun() {

        FilteredList<carData> filter = new FilteredList<>(availableCarList, e -> true);

        availableCarsSearch.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate(predicateCarData -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (predicateCarData.getCarId().toString().contains(searchKey)) {
                    return true;
                } else if (predicateCarData.getBrand().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateCarData.getModel().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateCarData.getPrice().toString().contains(searchKey)) {
                    return true;
                } else if (predicateCarData.getStatus().toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<carData> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(availableCarsTableView.comparatorProperty());
        availableCarsTableView.setItems(sortList);

    }

    public void carSelectFun() {
        carData carD = availableCarsTableView.getSelectionModel().getSelectedItem();
        int num = availableCarsTableView.getSelectionModel().getSelectedIndex();

        if ((num - 1) < - 1) {
            return;
        }

        availableCarId.setText(String.valueOf(carD.getCarId()));
        availableCarsBrand.setText(carD.getBrand());
        availableCarsModel.setText(carD.getModel());
        availableCarsPrice.setText(String.valueOf(carD.getPrice()));

        fetchData.path = carD.getImage();

        String uri = "file:" + carD.getImage();

        image = new Image(uri, 116, 153, false, true);
        availableCarsImageView.setImage(image);

    }
    
    public void payRentFun(){
        rentCustomerIdFun();
        
        String sql = "INSERT INTO customer "
                + "(customer_id, firstName, lastName, gender, car_id, brand"
                + ", model, total, date_rented, date_return) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?)";
        
        connection = DatabaseConnection.databaseConnection();
        
        try{
            Alert alert;
            
            if(rentFirstName.getText().isEmpty()
                    || rentLastName.getText().isEmpty()
                    || rentGender.getSelectionModel().getSelectedItem() == null
                    || rentCarId.getSelectionModel().getSelectedItem() == null
                    || rentBrand.getSelectionModel().getSelectedItem() == null
                    || rentModel.getSelectionModel().getSelectedItem() == null
                    || totalP == 0 || rentAmountFun.getText().isEmpty()){
                alert = new Alert(AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Something wrong :3");
                alert.showAndWait();
            }else{
                
                alert = new Alert(AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setContentText("Are you sure?");
                Optional<ButtonType> option = alert.showAndWait();
                
                if(option.get().equals(ButtonType.OK)){
                
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, String.valueOf(customerId));
                    preparedStatement.setString(2, rentFirstName.getText());
                    preparedStatement.setString(3, rentLastName.getText());
                    preparedStatement.setString(4, (String)rentGender.getSelectionModel().getSelectedItem());
                    preparedStatement.setString(5, (String)rentCarId.getSelectionModel().getSelectedItem());
                    preparedStatement.setString(6, (String)rentBrand.getSelectionModel().getSelectedItem());
                    preparedStatement.setString(7, (String)rentModel.getSelectionModel().getSelectedItem());
                    preparedStatement.setString(8, String.valueOf(totalP));
                    preparedStatement.setString(9, String.valueOf(rentDateFunRented.getValue()));
                    preparedStatement.setString(10, String.valueOf(rentDateFunReturn.getValue()));

                    preparedStatement.executeUpdate();
                    
                    // SET THE  STATUS OF CAR TO NOT AVAILABLE 
                    String updateCar = "UPDATE car SET status = 'Not Available' WHERE car_id = '"
                            +rentCarId.getSelectionModel().getSelectedItem()+"'";
                    
                    statement = connection.createStatement();
                    statement.executeUpdate(updateCar);
                    
                    alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Successful!");
                    alert.showAndWait();
                    
                    rentCarDataFun1();
                    
                    clearRentFun();
                } // NOW LETS PROCEED TO DASHBOARD FORM : ) 
            }
        }catch(Exception e){e.printStackTrace();}
        
    }
    
    public void clearRentFun(){
        totalP = 0;
        rentFirstName.setText("");
        rentLastName.setText("");
        rentGender.getSelectionModel().clearSelection();
        mAmount = 0;
        mBalance = 0;
        rentBalance.setText("$0.0");
        rentTotal.setText("$0.0");
        rentAmountFun.setText("");
        rentCarId.getSelectionModel().clearSelection();
        rentBrand.getSelectionModel().clearSelection();
        rentModel.getSelectionModel().clearSelection();
    }
    
    private int customerId;
    public void rentCustomerIdFun(){
        String sql = "SELECT id FROM customer";
        
        connection = DatabaseConnection.databaseConnection();
        
        try{
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            
            while(resultSet.next()){
                // GET THE LAST id and add + 1
                customerId = resultSet.getInt("id") + 1;
            }
            
        }catch(Exception e){e.printStackTrace();}
    }
    
    private double mAmount;
    private double mBalance;
    public void rentAmountFun(){
        Alert alert;
        if(totalP == 0 || rentAmountFun.getText().isEmpty()){
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Invalid : Amount should be more than Total Rent to offer change");
            alert.showAndWait();
            
            rentAmountFun.setText("");
        }else{
            mAmount = Double.parseDouble(rentAmountFun.getText());
            
            if(mAmount >= totalP){
                mBalance = (mAmount - totalP);
                rentBalance.setText("$" + String.valueOf(mBalance));
            }else{
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Invalid : Amount should be more than Total Rent to offer change");
                alert.showAndWait();
                
                rentAmountFun.setText("");
            }
            
        }
        
    }

    public ObservableList<carData> rentCarDataFun1() {

        ObservableList<carData> listData = FXCollections.observableArrayList();

        String sql = "SELECT * FROM car";

        connection = DatabaseConnection.databaseConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            carData carD;

            while (resultSet.next()) {
                carD = new carData(resultSet.getInt("car_id"), resultSet.getString("brand"),
                         resultSet.getString("model"), resultSet.getDouble("price"),
                         resultSet.getString("status"),
                         resultSet.getString("image"),
                         resultSet.getDate("date"));

                listData.add(carD);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }
    
    private int countDate;
    public void rentDateFun(){
        Alert alert;
        if(rentCarId.getSelectionModel().getSelectedItem() == null
                || rentBrand.getSelectionModel().getSelectedItem() == null
                || rentModel.getSelectionModel().getSelectedItem() == null){
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText(null);
            alert.setContentText("Something wrong :3");
            alert.showAndWait();
            
            rentDateFunRented.setValue(null);
            rentDateFunReturn.setValue(null);
            
        }else{
            
            if(rentDateFunReturn.getValue().isAfter(rentDateFunRented.getValue())){
                // COUNT THE DAY
                countDate = rentDateFunReturn.getValue().compareTo(rentDateFunRented.getValue());
            }else{
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Message");
                alert.setHeaderText(null);
                alert.setContentText("Something wrong :3");
                alert.showAndWait();
                // INCREASE OF 1 DAY ONCE THE USER CLICKED THE SAME DAY 
                rentDateFunReturn.setValue(rentDateFunRented.getValue().plusDays(1));
                
            }
        }
    }
    
    private double totalP;
    public void rentTotalFun(){
        rentDateFun();
        double price = 0;
        String sql = "SELECT price, model FROM car WHERE model = '"
                +rentModel.getSelectionModel().getSelectedItem()+"'";
        
        connection = DatabaseConnection.databaseConnection();
        
        try{
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            
            if(resultSet.next()){
                price = resultSet.getDouble("price");
            }
            // price * the count day you want to use the car
            totalP = (price * countDate);
            // DISPLAY TOTAL
            rentTotal.setText("$" + String.valueOf(totalP));
            
        }catch(Exception e){e.printStackTrace();}
        
    }

    private String[] genderList = {"Male", "Female", "Others"};

    public void rentGenderFun() {

        List<String> listG = new ArrayList<>();

        for (String data : genderList) {
            listG.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(listG);

        rentGender.setItems(listData);

    }

    public void rentCarIdFun() {

        String sql = "SELECT * FROM car WHERE status = 'Available'";

        connection = DatabaseConnection.databaseConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            ObservableList listData = FXCollections.observableArrayList();

            while (resultSet.next()) {
                listData.add(resultSet.getString("car_id"));
            }

            rentCarId.setItems(listData);

            rentBrandFun();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void rentBrandFun() {

        String sql = "SELECT * FROM car WHERE car_id = '"
                + rentCarId.getSelectionModel().getSelectedItem() + "'";

        connection = DatabaseConnection.databaseConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            ObservableList listData = FXCollections.observableArrayList();

            while (resultSet.next()) {
                listData.add(resultSet.getString("brand"));
            }

            rentBrand.setItems(listData);

            rentModelFun();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void rentModelFun() {

        String sql = "SELECT * FROM car WHERE brand = '"
                + rentBrand.getSelectionModel().getSelectedItem() + "'";

        connection = DatabaseConnection.databaseConnection();

        try {
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            ObservableList listData = FXCollections.observableArrayList();

            while (resultSet.next()) {
                listData.add(resultSet.getString("model"));
            }

            rentModel.setItems(listData);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ObservableList<carData> rentCarList;

    public void rentCarDataFun() {
        rentCarList = rentCarDataFun1();

        rentCarIdColumn.setCellValueFactory(new PropertyValueFactory<>("carId"));
        rentBrandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        rentModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        rentPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        rentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        rentTableView.setItems(rentCarList);
    }

    public void displayUsernameFun() {
        String user = fetchData.username;
        // TO SET THE FIRST LETTER TO BIG LETTER
        mUsername.setText(user.substring(0, 1).toUpperCase() + user.substring(1));

    }

    private double x = 0;
    private double y = 0;

    public void logoutFun() {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Message");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logoutFun?");
        Optional<ButtonType> option = alert.showAndWait();
        try {
            if (option.get().equals(ButtonType.OK)) {
                // HIDE YOUR DASHBOARD FORM
                logoutFunButton.getScene().getWindow().hide();

                // LINK YOUR LOGIN FORM
                Parent root = FXMLLoader.load(getClass().getResource("../login.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);

                root.setOnMousePressed((MouseEvent event) -> {
                    x = event.getSceneX();
                    y = event.getSceneY();
                });

                root.setOnMouseDragged((MouseEvent event) -> {
                    stage.setX(event.getScreenX() - x);
                    stage.setY(event.getScreenY() - y);

                    stage.setOpacity(.8);
                });

                root.setOnMouseReleased((MouseEvent event) -> {
                    stage.setOpacity(1);
                });

                stage.initStyle(StageStyle.TRANSPARENT);

                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchFormFun(ActionEvent event) {

        if (event.getSource() == homeButton) {
            formHome.setVisible(true);
            formAvailableCars.setVisible(false);
            formRent.setVisible(false);

            homeButton.setStyle("-fx-background-color:linear-gradient(to bottom right, #686f86, #8e9296);");
            availableCarsButton.setStyle("-fx-background-color:transparent");
            rentCarButton.setStyle("-fx-background-color:transparent");

            availableCarsFun();
            totalIncomeFun();
            totalCustomersFun();
            IncomeChartFun();
            CustomerChartFun();
            
        } else if (event.getSource() == availableCarsButton) {
            formHome.setVisible(false);
            formAvailableCars.setVisible(true);
            formRent.setVisible(false);

            availableCarsButton.setStyle("-fx-background-color:linear-gradient(to bottom right, #686f86, #8e9296);");
            homeButton.setStyle("-fx-background-color:transparent");
            rentCarButton.setStyle("-fx-background-color:transparent");

            // TO UPDATE YOUR TABLEVIEW ONCE YOU CLICK THE AVAILABLE CAR NAV BUTTON
            carShowListDataFun();
            carStatusListFun();
            carSearchFun();

        } else if (event.getSource() == rentCarButton) {
            formHome.setVisible(false);
            formAvailableCars.setVisible(false);
            formRent.setVisible(true);

            rentCarButton.setStyle("-fx-background-color:linear-gradient(to bottom right, #686f86, #8e9296);");
            homeButton.setStyle("-fx-background-color:transparent");
            availableCarsButton.setStyle("-fx-background-color:transparent");

            rentCarDataFun1();
            rentCarIdFun();
            rentBrandFun();
            rentModelFun();
            rentGenderFun();

        }

    }

    public void close() {
        System.exit(0);
    }

    public void minimizeWindow() {
        Stage stage = (Stage) mainForm.getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayUsernameFun();

        availableCarsFun();
        totalIncomeFun();
        totalCustomersFun();
        IncomeChartFun();
        CustomerChartFun();
        
        
        // TO DISPLAY THE DATA ON THE TABLEVIEW
        carShowListDataFun();
        carStatusListFun();
        carSearchFun();

        rentCarDataFun1();
        rentCarIdFun();
        rentBrandFun();
        rentModelFun();
        rentGenderFun();

    }

}
