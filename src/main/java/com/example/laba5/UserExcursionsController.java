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
import java.util.ArrayList;
import java.util.List;

public class UserExcursionsController {

    @FXML private TableView<Excursion> excursionsTable;
    @FXML private ComboBox<String> dayFilterComboBox;
    @FXML private ComboBox<String> guideFilterComboBox;
    @FXML private Label costLabel;

    private ExcursionStudio studio = ExcursionStudio.getInstance();
    private ObservableList<Excursion> excursionsData;
    private ObservableList<Excursion> allExcursionsData;

    private SortTask ascendingTask = null;
    private SortThread descendingThread = null;

    @FXML
    private void initialize() {
        studio.loadFromFile();
        initializeFilters();
        initializeTable();
        loadExcursionsData();
    }

    private void initializeFilters() {
        dayFilterComboBox.setItems(FXCollections.observableArrayList("Все", "будни", "выходные"));
        guideFilterComboBox.setItems(FXCollections.observableArrayList("Все", "слабо", "средне", "высоко"));

        dayFilterComboBox.setValue("Все");
        guideFilterComboBox.setValue("Все");

        dayFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        guideFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void initializeTable() {
        TableColumn<Excursion, String> placeColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(0);
        TableColumn<Excursion, String> dayColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(1);
        TableColumn<Excursion, String> timeColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(2);
        TableColumn<Excursion, String> guideColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(3);
        TableColumn<Excursion, String> costColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(4);

        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("dayType"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeOfDay"));
        guideColumn.setCellValueFactory(new PropertyValueFactory<>("guideLevel"));

        costColumn.setCellValueFactory(cellData -> {
            Excursion excursion = cellData.getValue();
            double cost = studio.calculateCost(excursion);
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f BYN", cost));
        });

        excursionsData = FXCollections.observableArrayList();
        allExcursionsData = FXCollections.observableArrayList();

        // Устанавливаем данные в таблицу
        excursionsTable.setItems(excursionsData);

        System.out.println("✅ Таблица инициализирована");
    }

    private void loadExcursionsData() {
        allExcursionsData.clear();

        // Загружаем все экскурсии
        for (AbstractExcursion abstractExcursion : studio.getExcursions()) {
            if (abstractExcursion instanceof Excursion) {
                allExcursionsData.add((Excursion) abstractExcursion);
            }
        }

        // ОБНОВЛЯЕМ ОСНОВНУЮ КОЛЛЕКЦИЮ
        excursionsData.setAll(allExcursionsData);

        // ПРИНУДИТЕЛЬНО ОБНОВЛЯЕМ ТАБЛИЦУ
        excursionsTable.refresh();

        System.out.println("✅ Загружено экскурсий: " + excursionsData.size());
        System.out.println("✅ Данные установлены в таблицу");
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

        // ВОССТАНАВЛИВАЕМ ВСЕ ДАННЫЕ И ОБНОВЛЯЕМ ТАБЛИЦУ
        excursionsData.setAll(allExcursionsData);
        excursionsTable.refresh();
        System.out.println("✅ Фильтры сброшены. Показано: " + excursionsData.size() + " экскурсий");
    }

    private void applyFilters() {
        String dayFilter = dayFilterComboBox.getValue();
        String guideFilter = guideFilterComboBox.getValue();

        if ("Все".equals(dayFilter) && "Все".equals(guideFilter)) {
            excursionsData.setAll(allExcursionsData);
            excursionsTable.refresh();
            System.out.println("✅ Показаны все экскурсии: " + excursionsData.size());
            return;
        }

        ObservableList<Excursion> filteredData = FXCollections.observableArrayList();

        for (Excursion excursion : allExcursionsData) {
            boolean dayMatch = "Все".equals(dayFilter) || excursion.getDayType().equals(dayFilter);
            boolean guideMatch = "Все".equals(guideFilter) || excursion.getGuideLevel().equals(guideFilter);

            if (dayMatch && guideMatch) {
                filteredData.add(excursion);
            }
        }

        // УСТАНАВЛИВАЕМ ОТФИЛЬТРОВАННЫЕ ДАННЫЕ И ОБНОВЛЯЕМ
        excursionsData.setAll(filteredData);
        excursionsTable.refresh();

        System.out.println("✅ Применены фильтры. Показано: " + filteredData.size() + " экскурсий");
    }

    @FXML
    private void handleSortAscending() {
        List<AbstractExcursion> filtered = getCurrentFilteredExcursions();
        if (filtered.isEmpty()) {
            showAlert(AlertType.WARNING, "Сортировка", "Нет экскурсий для сортировки");
            return;
        }

        System.out.println("Сортировка по возрастанию для " + filtered.size() + " экскурсий");

        ascendingTask = new SortTask(filtered, true, studio);
        Thread runnableThread = new Thread(ascendingTask, "Ascending-Runnable-Thread");
        runnableThread.start();

        new Thread(() -> {
            try {
                runnableThread.join();
                javafx.application.Platform.runLater(() -> {
                    // ПЕРЕЗАГРУЖАЕМ ДАННЫЕ ПОСЛЕ СОРТИРОВКИ
                    studio.loadFromFile();
                    loadExcursionsData();
                    showAlert(AlertType.INFORMATION, "Сортировка",
                            "Сортировка по возрастанию завершена!\nОтсортировано " + filtered.size() + " экскурсий");
                    ascendingTask = null;
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleSortDescending() {
        List<AbstractExcursion> filtered = getCurrentFilteredExcursions();
        if (filtered.isEmpty()) {
            showAlert(AlertType.WARNING, "Сортировка", "Нет экскурсий для сортировки");
            return;
        }

        System.out.println("Сортировка по убыванию для " + filtered.size() + " экскурсий");

        descendingThread = new SortThread(filtered, false, studio);
        descendingThread.start();

        new Thread(() -> {
            try {
                descendingThread.join();
                javafx.application.Platform.runLater(() -> {
                    // ПЕРЕЗАГРУЖАЕМ ДАННЫЕ ПОСЛЕ СОРТИРОВКИ
                    studio.loadFromFile();
                    loadExcursionsData();
                    showAlert(AlertType.INFORMATION, "Сортировка",
                            "Сортировка по убыванию завершена!\nОтсортировано " + filtered.size() + " экскурсий");
                    descendingThread = null;
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleCancelSort() {
        boolean anyCancelled = false;

        if (ascendingTask != null) {
            ascendingTask.cancel();
            anyCancelled = true;
            System.out.println("Сортировка по возрастанию отменена");
        }

        if (descendingThread != null && descendingThread.isAlive()) {
            descendingThread.cancel();
            anyCancelled = true;
            System.out.println("Сортировка по убыванию отменена");
        }

        if (anyCancelled) {
            showAlert(AlertType.INFORMATION, "Отмена сортировки",
                    "Запрос на отмену всех сортировок отправлен");
        } else {
            showAlert(AlertType.INFORMATION, "Отмена сортировки",
                    "Нет активных сортировок для отмены");
        }
    }

    @FXML
    private void handleCalculateCost() {
        Excursion selected = excursionsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.ERROR, "Ошибка", "Выберите экскурсию для расчета стоимости");
            return;
        }

        double cost = studio.calculateCost(selected);
        costLabel.setText(String.format("Стоимость: %.2f BYN\n%s, %s, %s, %s",
                cost, selected.getPlace(), selected.getDayType(),
                selected.getTimeOfDay(), selected.getGuideLevel()));
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

    private List<AbstractExcursion> getCurrentFilteredExcursions() {
        List<AbstractExcursion> filtered = new ArrayList<>();
        for (AbstractExcursion abstractExcursion : studio.getExcursions()) {
            if (abstractExcursion instanceof Excursion) {
                Excursion excursion = (Excursion) abstractExcursion;

                String dayFilter = dayFilterComboBox.getValue();
                String guideFilter = guideFilterComboBox.getValue();

                boolean dayMatch = "Все".equals(dayFilter) || excursion.getDayType().equals(dayFilter);
                boolean guideMatch = "Все".equals(guideFilter) || excursion.getGuideLevel().equals(guideFilter);

                if (dayMatch && guideMatch) {
                    filtered.add(excursion);
                }
            }
        }
        return filtered;
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}