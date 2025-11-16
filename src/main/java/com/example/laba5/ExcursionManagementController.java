package com.example.laba5;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class ExcursionManagementController {

    @FXML private TextField placeField;
    @FXML private ComboBox<String> dayComboBox;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private ComboBox<String> guideComboBox;
    @FXML private TableView<ExcursionTableModel> excursionsTable;
    @FXML private Label excursionCountLabel;

    private ExcursionStudio studio = ExcursionStudio.getInstance();
    private ObservableList<ExcursionTableModel> excursionsData;

    // Модель для таблицы
    public static class ExcursionTableModel {
        private final String place;
        private final String dayType;
        private final String timeOfDay;
        private final String guideLevel;
        private final String cost;

        public ExcursionTableModel(Excursion excursion, double cost) {
            this.place = excursion.getPlace();
            this.dayType = excursion.getDayType();
            this.timeOfDay = excursion.getTimeOfDay();
            this.guideLevel = excursion.getGuideLevel();
            this.cost = String.format("%.2f BYN", cost);
        }

        // Геттеры
        public String getPlace() { return place; }
        public String getDayType() { return dayType; }
        public String getTimeOfDay() { return timeOfDay; }
        public String getGuideLevel() { return guideLevel; }
        public String getCost() { return cost; }
    }

    @FXML
    private void initialize() {
        studio.loadFromFile();
        initializeForm();
        initializeTable();
        loadExcursionsData();
        updateExcursionCount();
    }

    private void initializeForm() {
        // Заполняем комбобоксы
        dayComboBox.setItems(FXCollections.observableArrayList("будни", "выходные"));
        timeComboBox.setItems(FXCollections.observableArrayList("утро", "день", "вечер"));
        guideComboBox.setItems(FXCollections.observableArrayList("слабо", "средне", "высоко"));

        // Устанавливаем плейсхолдеры
        placeField.setPromptText("Введите место экскурсии");
        dayComboBox.setPromptText("Выберите день");
        timeComboBox.setPromptText("Выберите время");
        guideComboBox.setPromptText("Выберите гида");
    }

    private void initializeTable() {
        // Настраиваем колонки таблицы
        TableColumn<ExcursionTableModel, String> placeColumn = (TableColumn<ExcursionTableModel, String>) excursionsTable.getColumns().get(0);
        TableColumn<ExcursionTableModel, String> dayColumn = (TableColumn<ExcursionTableModel, String>) excursionsTable.getColumns().get(1);
        TableColumn<ExcursionTableModel, String> timeColumn = (TableColumn<ExcursionTableModel, String>) excursionsTable.getColumns().get(2);
        TableColumn<ExcursionTableModel, String> guideColumn = (TableColumn<ExcursionTableModel, String>) excursionsTable.getColumns().get(3);
        TableColumn<ExcursionTableModel, String> costColumn = (TableColumn<ExcursionTableModel, String>) excursionsTable.getColumns().get(4);
        TableColumn<ExcursionTableModel, String> actionColumn = (TableColumn<ExcursionTableModel, String>) excursionsTable.getColumns().get(5);

        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("dayType"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeOfDay"));
        guideColumn.setCellValueFactory(new PropertyValueFactory<>("guideLevel"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));

        // Убираем пустой столбец справа
        excursionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Кастомная колонка для действий (ТОЛЬКО cellFactory, НЕТ cellValueFactory)
        actionColumn.setCellFactory(column -> new TableCell<ExcursionTableModel, String>() {
            private final Button deleteButton = new Button("🗑️");

            {
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12px; -fx-pref-width: 60px;");
                deleteButton.setOnAction(event -> {
                    ExcursionTableModel excursionModel = getTableView().getItems().get(getIndex());
                    handleDeleteExcursion(excursionModel);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        excursionsData = FXCollections.observableArrayList();
        excursionsTable.setItems(excursionsData);
    }

    private void loadExcursionsData() {
        excursionsData.clear();
        // Преобразуем AbstractExcursion в ExcursionTableModel для таблицы
        for (AbstractExcursion abstractExcursion : studio.getExcursions()) {
            if (abstractExcursion instanceof Excursion) {
                Excursion excursion = (Excursion) abstractExcursion;
                double cost = studio.calculateCost(excursion);
                excursionsData.add(new ExcursionTableModel(excursion, cost));
            }
        }
        updateExcursionCount();
        System.out.println("Загружено экскурсий в таблицу: " + excursionsData.size());
    }

    private void updateExcursionCount() {
        if (excursionCountLabel != null) {
            excursionCountLabel.setText(String.valueOf(excursionsData.size()));
        }
    }

    @FXML
    private void handleAddExcursion() {
        String place = placeField.getText().trim();
        String day = dayComboBox.getValue();
        String time = timeComboBox.getValue();
        String guide = guideComboBox.getValue();

        // Валидация
        if (place.isEmpty() || day == null || time == null || guide == null) {
            showAlert(AlertType.ERROR, "Ошибка", "Заполните все поля!");
            return;
        }

        if (place.length() < 2) {
            showAlert(AlertType.ERROR, "Ошибка", "Название места должно содержать минимум 2 символа");
            return;
        }

        // Создаем и добавляем экскурсию
        Excursion excursion = new Excursion(place, day, time, guide);
        studio.addExcursion(excursion);

        // Обновляем таблицу
        loadExcursionsData();

        // Очищаем форму
        clearForm();

        showAlert(AlertType.INFORMATION, "Успех", "Экскурсия добавлена!\nСтоимость: " +
                String.format("%.2f BYN", studio.calculateCost(excursion)));
    }

    private void handleDeleteExcursion(ExcursionTableModel excursionModel) {
        int index = findExcursionIndex(excursionModel);
        if (index != -1) {
            studio.removeExcursion(index);
            loadExcursionsData();
            showAlert(AlertType.INFORMATION, "Успех", "Экскурсия удалена!");
        }
    }

    private int findExcursionIndex(ExcursionTableModel target) {
        List<AbstractExcursion> excursions = studio.getExcursions();
        for (int i = 0; i < excursions.size(); i++) {
            AbstractExcursion excursion = excursions.get(i);
            if (excursion instanceof Excursion) {
                Excursion current = (Excursion) excursion;
                if (current.getPlace().equals(target.getPlace()) &&
                        current.getDayType().equals(target.getDayType()) &&
                        current.getTimeOfDay().equals(target.getTimeOfDay()) &&
                        current.getGuideLevel().equals(target.getGuideLevel())) {
                    return i;
                }
            }
        }
        return -1;
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/admin_dashboard.fxml"));
            Stage stage = (Stage) placeField.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось вернуться: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        placeField.clear();
        dayComboBox.setValue(null);
        timeComboBox.setValue(null);
        guideComboBox.setValue(null);
        placeField.requestFocus(); // Фокус на первое поле
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}