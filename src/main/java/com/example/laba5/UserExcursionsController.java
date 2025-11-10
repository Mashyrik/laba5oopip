package com.example.laba5;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserExcursionsController {

    @FXML private TableView<Excursion> excursionsTable;
    @FXML private ComboBox<String> dayFilterComboBox;
    @FXML private ComboBox<String> guideFilterComboBox;
    @FXML private Label costLabel;

    private ExcursionStudio studio = new ExcursionStudio();
    private ObservableList<Excursion> excursionsData;

    @FXML
    private void initialize() {
        studio.loadFromFile();
        initializeFilters();
        initializeTable();
        loadExcursionsData();
    }

    private void initializeFilters() {
        // Заполняем комбобоксы фильтров
        dayFilterComboBox.setItems(FXCollections.observableArrayList("Все", "будни", "выходные"));
        guideFilterComboBox.setItems(FXCollections.observableArrayList("Все", "слабо", "средне", "высоко"));

        dayFilterComboBox.setValue("Все");
        guideFilterComboBox.setValue("Все");
    }

    private void initializeTable() {
        // Настраиваем колонки таблицы
        TableColumn<Excursion, String> placeColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(0);
        TableColumn<Excursion, String> dayColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(1);
        TableColumn<Excursion, String> timeColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(2);
        TableColumn<Excursion, String> guideColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(3);
        TableColumn<Excursion, String> costColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(4);

        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("dayType"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeOfDay"));
        guideColumn.setCellValueFactory(new PropertyValueFactory<>("guideLevel"));

        // Колонка стоимости
        costColumn.setCellValueFactory(cellData -> {
            Excursion excursion = cellData.getValue();
            double cost = studio.calculateCost(excursion);
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f BYN", cost));
        });

        excursionsData = FXCollections.observableArrayList();
        excursionsTable.setItems(excursionsData);
    }

    private void loadExcursionsData() {
        excursionsData.clear();
        // Преобразуем AbstractExcursion в Excursion для таблицы
        for (AbstractExcursion abstractExcursion : studio.getExcursions()) {
            if (abstractExcursion instanceof Excursion) {
                excursionsData.add((Excursion) abstractExcursion);
            }
        }
        applyFilters(); // Применяем текущие фильтры
    }

    @FXML
    private void handleApplyFilters() {
        applyFilters();
    }

    @FXML
    private void handleResetFilters() {
        dayFilterComboBox.setValue("Все");
        guideFilterComboBox.setValue("Все");
        costLabel.setText("Выберите экскурсию");
        loadExcursionsData(); // Перезагружаем все данные
    }

    private void applyFilters() {
        String dayFilter = dayFilterComboBox.getValue();
        String guideFilter = guideFilterComboBox.getValue();

        ObservableList<Excursion> filteredData = FXCollections.observableArrayList();

        for (Excursion excursion : excursionsData) {
            boolean dayMatch = dayFilter.equals("Все") || excursion.getDayType().equals(dayFilter);
            boolean guideMatch = guideFilter.equals("Все") || excursion.getGuideLevel().equals(guideFilter);

            if (dayMatch && guideMatch) {
                filteredData.add(excursion);
            }
        }

        excursionsTable.setItems(filteredData);
    }

    @FXML
    private void handleSortAscending() {
        // Сортировка по возрастанию через Runnable
        showAlert(AlertType.INFORMATION, "Сортировка", "Сортировка по возрастанию применена");
        // Здесь можно добавить вызов SortTask
    }

    @FXML
    private void handleSortDescending() {
        // Сортировка по убыванию через Thread
        showAlert(AlertType.INFORMATION, "Сортировка", "Сортировка по убыванию применена");
        // Здесь можно добавить вызов SortThread
    }

    @FXML
    private void handleCalculateCost() {
        Excursion selected = excursionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.ERROR, "Ошибка", "Выберите экскурсию для расчета стоимости");
            return;
        }

        double cost = studio.calculateCost(selected);
        costLabel.setText(String.format("Стоимость: %.2f BYN\n%s, %s, %s",
                cost, selected.getPlace(), selected.getDayType(), selected.getTimeOfDay()));
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/user_dashboard.fxml"));
            Stage stage = (Stage) excursionsTable.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось вернуться: " + e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}