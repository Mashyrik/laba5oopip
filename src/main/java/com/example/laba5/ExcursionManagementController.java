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

public class ExcursionManagementController {

    @FXML private TextField placeField;
    @FXML private ComboBox<String> dayComboBox;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private ComboBox<String> guideComboBox;
    @FXML private TableView<Excursion> excursionsTable;

    private ExcursionStudio studio = new ExcursionStudio();
    private ObservableList<Excursion> excursionsData;

    @FXML
    private void initialize() {
        studio.loadFromFile();
        initializeForm();
        initializeTable();
        loadExcursionsData();
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
        TableColumn<Excursion, String> placeColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(0);
        TableColumn<Excursion, String> dayColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(1);
        TableColumn<Excursion, String> timeColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(2);
        TableColumn<Excursion, String> guideColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(3);
        TableColumn<Excursion, String> costColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(4);
        TableColumn<Excursion, String> actionColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(5);

        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("dayType"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeOfDay"));
        guideColumn.setCellValueFactory(new PropertyValueFactory<>("guideLevel"));

        // Кастомная колонка для стоимости
        costColumn.setCellValueFactory(cellData -> {
            Excursion excursion = cellData.getValue();
            double cost = studio.calculateCost(excursion);
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f BYN", cost));
        });

        // Кастомная колонка для действий
        actionColumn.setCellFactory(column -> new javafx.scene.control.TableCell<Excursion, String>() {
            private final Button deleteButton = new Button("Удалить");

            {
                deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    Excursion excursion = getTableView().getItems().get(getIndex());
                    handleDeleteExcursion(excursion);
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
        // Преобразуем AbstractExcursion в Excursion для таблицы
        for (AbstractExcursion abstractExcursion : studio.getExcursions()) {
            if (abstractExcursion instanceof Excursion) {
                excursionsData.add((Excursion) abstractExcursion);
            }
        }
        System.out.println("Загружено экскурсий в таблицу: " + excursionsData.size());
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

    private void handleDeleteExcursion(Excursion excursion) {
        int index = findExcursionIndex(excursion);
        if (index != -1) {
            studio.removeExcursion(index);
            loadExcursionsData();
            showAlert(AlertType.INFORMATION, "Успех", "Экскурсия удалена!");
        }
    }

    private int findExcursionIndex(Excursion target) {
        for (int i = 0; i < studio.getExcursions().size(); i++) {
            AbstractExcursion excursion = studio.getExcursions().get(i);
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
            stage.setScene(new Scene(root, 800, 600));
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось вернуться: " + e.getMessage());
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
        alert.showAndWait();
    }
}