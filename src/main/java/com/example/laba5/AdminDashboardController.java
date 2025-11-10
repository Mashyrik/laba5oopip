package com.example.laba5;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;

public class AdminDashboardController {

    @FXML private TableView<AbstractExcursion> excursionsTable;
    @FXML private TableColumn<AbstractExcursion, String> placeColumn;
    @FXML private TableColumn<AbstractExcursion, String> dayColumn;
    @FXML private TableColumn<AbstractExcursion, String> timeColumn;
    @FXML private TableColumn<AbstractExcursion, String> guideColumn;

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;

    @FXML private TextField placeField;
    @FXML private ComboBox<String> dayComboBox;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private ComboBox<String> guideComboBox;

    private ExcursionStudio studio = new ExcursionStudio();
    private UserManager userManager = UserManager.getInstance();
    private CommandManager commandManager = CommandManager.getInstance();
    private ObservableList<AbstractExcursion> excursionsData;
    private ObservableList<User> usersData;

    @FXML
    private void initialize() {
        studio.loadFromFile();
        initializeExcursionsTable();
        initializeUsersTable();
        initializeForm();
        loadExcursionsData();
        loadUsersData();
    }

    private void initializeExcursionsTable() {
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("dayType"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeOfDay"));
        guideColumn.setCellValueFactory(new PropertyValueFactory<>("guideLevel"));

        excursionsData = FXCollections.observableArrayList();
        excursionsTable.setItems(excursionsData);
    }

    private void initializeUsersTable() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String status = user.isBlocked() ? "Заблокирован" : "Активен";
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        usersData = FXCollections.observableArrayList();
        usersTable.setItems(usersData);
    }

    private void initializeForm() {
        setupField(placeField, "Введите место экскурсии");

        dayComboBox.getItems().addAll("будни", "выходные");
        timeComboBox.getItems().addAll("утро", "день", "вечер");
        guideComboBox.getItems().addAll("слабо", "средне", "высоко");

        dayComboBox.setPromptText("Выберите день");
        timeComboBox.setPromptText("Выберите время");
        guideComboBox.setPromptText("Выберите квалификацию");
    }

    private void setupField(TextField field, String prompt) {
        field.setPromptText(prompt);
    }

    private void loadExcursionsData() {
        excursionsData.clear();
        excursionsData.addAll(studio.getExcursions());
    }

    private void loadUsersData() {
        usersData.clear();
        usersData.addAll(userManager.getUsers());
    }

    @FXML
    private void handleAddExcursion() {
        String place = placeField.getText().trim();
        String day = dayComboBox.getValue();
        String time = timeComboBox.getValue();
        String guide = guideComboBox.getValue();

        if (place.isEmpty() || day == null || time == null || guide == null) {
            showAlert(AlertType.ERROR, "Ошибка", "Заполните все поля");
            return;
        }

        AbstractExcursion excursion = new Excursion(place, day, time, guide);
        AdminCommand command = new AddExcursionCommand(studio, excursion);
        commandManager.executeCommand(command);

        loadExcursionsData();
        clearForm();
        showAlert(AlertType.INFORMATION, "Успех", "Экскурсия добавлена");
    }

    @FXML
    private void handleDeleteExcursion() {
        AbstractExcursion selected = excursionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.ERROR, "Ошибка", "Выберите экскурсию для удаления");
            return;
        }

        int index = studio.getExcursions().indexOf(selected);
        AdminCommand command = new DeleteExcursionCommand(studio, index);
        commandManager.executeCommand(command);

        loadExcursionsData();
        showAlert(AlertType.INFORMATION, "Успех", "Экскурсия удалена");
    }

    @FXML
    private void handleBlockUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.ERROR, "Ошибка", "Выберите пользователя");
            return;
        }

        if (selected.equals(userManager.getCurrentUser())) {
            showAlert(AlertType.ERROR, "Ошибка", "Нельзя заблокировать себя");
            return;
        }

        if (userManager.blockUser(selected.getUsername())) {
            loadUsersData();
            showAlert(AlertType.INFORMATION, "Успех", "Пользователь заблокирован");
        } else {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось заблокировать пользователя");
        }
    }

    @FXML
    private void handleUnblockUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.ERROR, "Ошибка", "Выберите пользователя");
            return;
        }

        if (userManager.unblockUser(selected.getUsername())) {
            loadUsersData();
            showAlert(AlertType.INFORMATION, "Успех", "Пользователь разблокирован");
        } else {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось разблокировать пользователя");
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.ERROR, "Ошибка", "Выберите пользователя");
            return;
        }

        if (selected.equals(userManager.getCurrentUser())) {
            showAlert(AlertType.ERROR, "Ошибка", "Нельзя удалить себя");
            return;
        }

        if (userManager.deleteUser(selected.getUsername())) {
            loadUsersData();
            showAlert(AlertType.INFORMATION, "Успех", "Пользователь удален");
        } else {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось удалить пользователя");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            userManager.logout();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/login.fxml"));
            javafx.scene.Parent root = loader.load();

            Stage stage = (Stage) excursionsTable.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось выйти из системы");
        }
    }

    private void clearForm() {
        placeField.clear();
        dayComboBox.setValue(null);
        timeComboBox.setValue(null);
        guideComboBox.setValue(null);
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

        alert.showAndWait();
    }
}