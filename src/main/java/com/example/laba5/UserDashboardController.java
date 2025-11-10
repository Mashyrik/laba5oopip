package com.example.laba5;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;

public class UserDashboardController {

    @FXML private TableView<AbstractExcursion> excursionsTable;
    @FXML private TableColumn<AbstractExcursion, String> placeColumn;
    @FXML private TableColumn<AbstractExcursion, String> dayColumn;
    @FXML private TableColumn<AbstractExcursion, String> timeColumn;
    @FXML private TableColumn<AbstractExcursion, String> guideColumn;
    @FXML private TableColumn<AbstractExcursion, String> costColumn;

    @FXML private ComboBox<String> dayFilterComboBox;
    @FXML private ComboBox<String> guideFilterComboBox;
    @FXML private Button sortAscButton;
    @FXML private Button sortDescButton;
    @FXML private Button calculateCostButton;
    @FXML private Label costLabel;

    private ExcursionStudio studio = new ExcursionStudio();
    private UserManager userManager = UserManager.getInstance();
    private ObservableList<AbstractExcursion> excursionsData;
    private FilteredList<AbstractExcursion> filteredData;
    private SortedList<AbstractExcursion> sortedData;

    @FXML
    private void initialize() {
        studio.loadFromFile();
        initializeTable();
        initializeFilters();
        loadData();
    }

    private void initializeTable() {
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("dayType"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeOfDay"));
        guideColumn.setCellValueFactory(new PropertyValueFactory<>("guideLevel"));
        costColumn.setCellValueFactory(cellData -> {
            AbstractExcursion excursion = cellData.getValue();
            double cost = studio.calculateCost(excursion);
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f BYN", cost));
        });

        excursionsData = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(excursionsData);
        sortedData = new SortedList<>(filteredData);

        excursionsTable.setItems(sortedData);
        sortedData.comparatorProperty().bind(excursionsTable.comparatorProperty());
    }

    private void initializeFilters() {
        dayFilterComboBox.getItems().addAll("Все", "будни", "выходные");
        guideFilterComboBox.getItems().addAll("Все", "слабо", "средне", "высоко");

        dayFilterComboBox.setValue("Все");
        guideFilterComboBox.setValue("Все");

        // Применение фильтров
        dayFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        guideFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void loadData() {
        excursionsData.clear();
        excursionsData.addAll(studio.getExcursions());
    }

    private void applyFilters() {
        filteredData.setPredicate(excursion -> {
            String dayFilter = dayFilterComboBox.getValue();
            String guideFilter = guideFilterComboBox.getValue();

            boolean dayMatch = dayFilter.equals("Все") || excursion.getDayType().equals(dayFilter);
            boolean guideMatch = guideFilter.equals("Все") || excursion.getGuideLevel().equals(guideFilter);

            return dayMatch && guideMatch;
        });
    }

    @FXML
    private void handleSortAscending() {
        studio.reapplySort();
        loadData();
        showAlert(AlertType.INFORMATION, "Сортировка", "Сортировка по возрастанию применена");
    }

    @FXML
    private void handleSortDescending() {
        List<AbstractExcursion> filtered = studio.getFilteredExcursions();
        SortThread descendingThread = new SortThread(filtered, false, studio);
        descendingThread.start();

        // Обновляем данные после сортировки
        new Thread(() -> {
            try {
                descendingThread.join();
                javafx.application.Platform.runLater(() -> {
                    loadData();
                    showAlert(AlertType.INFORMATION, "Сортировка", "Сортировка по убыванию применена");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleCalculateCost() {
        AbstractExcursion selected = excursionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.ERROR, "Ошибка", "Выберите экскурсию для расчета стоимости");
            return;
        }

        double cost = studio.calculateCost(selected);
        costLabel.setText(String.format("Стоимость: %.2f BYN", cost));
    }

    @FXML
    private void handleResetFilters() {
        dayFilterComboBox.setValue("Все");
        guideFilterComboBox.setValue("Все");
        costLabel.setText("Стоимость: --");
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