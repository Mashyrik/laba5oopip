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
    private List<AbstractExcursion> originalOrder;

    private SortTask ascendingTask = null;
    private SortThread descendingThread = null;

    @FXML
    private void initialize() {
        studio.loadFromFile();
        originalOrder = new ArrayList<>(studio.getExcursions());

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
        // УБИРАЕМ КОЛОНКУ СТОИМОСТИ - оставляем только 4 колонки
        TableColumn<Excursion, String> placeColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(0);
        TableColumn<Excursion, String> dayColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(1);
        TableColumn<Excursion, String> timeColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(2);
        TableColumn<Excursion, String> guideColumn = (TableColumn<Excursion, String>) excursionsTable.getColumns().get(3);

        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("dayType"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeOfDay"));
        guideColumn.setCellValueFactory(new PropertyValueFactory<>("guideLevel"));

        // УБИРАЕМ КОЛОНКУ СТОИМОСТИ - она больше не нужна в таблице

        excursionsData = FXCollections.observableArrayList();
        allExcursionsData = FXCollections.observableArrayList();

        excursionsTable.setItems(excursionsData);
    }

    private void loadExcursionsData() {
        allExcursionsData.clear();

        for (AbstractExcursion abstractExcursion : studio.getExcursions()) {
            if (abstractExcursion instanceof Excursion) {
                allExcursionsData.add((Excursion) abstractExcursion);
            }
        }

        excursionsData.setAll(allExcursionsData);
        excursionsTable.refresh();

        System.out.println("✅ Загружено экскурсий: " + excursionsData.size());
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

        restoreOriginalOrder();
        System.out.println("✅ Фильтры сброшены. Восстановлен исходный порядок: " + excursionsData.size() + " экскурсий");
    }

    private void restoreOriginalOrder() {
        studio.applySortedExcursions(new ArrayList<>(originalOrder), true);
        loadExcursionsData();
    }

    private void applyFilters() {
        String dayFilter = dayFilterComboBox.getValue();
        String guideFilter = guideFilterComboBox.getValue();

        if ("Все".equals(dayFilter) && "Все".equals(guideFilter)) {
            excursionsData.setAll(allExcursionsData);
            excursionsTable.refresh();
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

        excursionsData.setAll(filteredData);
        excursionsTable.refresh();
    }

    // СПОСОБ 1: Многопоточная сортировка через Runnable (по возрастанию)
    @FXML
    private void handleSortAscending() {
        List<AbstractExcursion> currentExcursions = getCurrentDisplayedExcursions();
        if (currentExcursions.isEmpty()) {
            showAlert(AlertType.WARNING, "Сортировка", "Нет экскурсий для сортировки");
            return;
        }

        System.out.println("🔸 Многопоточная сортировка по возрастанию (Runnable)");

        ascendingTask = new SortTask(currentExcursions, true, studio);
        Thread runnableThread = new Thread(ascendingTask, "Ascending-Runnable-Thread");
        runnableThread.start();

        new Thread(() -> {
            try {
                runnableThread.join();
                javafx.application.Platform.runLater(() -> {
                    loadExcursionsData();
                    showAlert(AlertType.INFORMATION, "Сортировка",
                            "Многопоточная сортировка по возрастанию завершена!\n" +
                                    "Способ: Runnable\n" +
                                    "Отсортировано " + currentExcursions.size() + " экскурсий");
                    ascendingTask = null;
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // СПОСОБ 2: Многопоточная сортировка через Thread наследование (по убыванию)
    @FXML
    private void handleSortDescending() {
        List<AbstractExcursion> currentExcursions = getCurrentDisplayedExcursions();
        if (currentExcursions.isEmpty()) {
            showAlert(AlertType.WARNING, "Сортировка", "Нет экскурсий для сортировки");
            return;
        }

        System.out.println("🔸 Многопоточная сортировка по убыванию (Thread наследование)");

        descendingThread = new SortThread(currentExcursions, false, studio);
        descendingThread.start();

        new Thread(() -> {
            try {
                descendingThread.join();
                javafx.application.Platform.runLater(() -> {
                    loadExcursionsData();
                    showAlert(AlertType.INFORMATION, "Сортировка",
                            "Многопоточная сортировка по убыванию завершена!\n" +
                                    "Способ: Thread наследование\n" +
                                    "Отсортировано " + currentExcursions.size() + " экскурсий");
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
            System.out.println("Сортировка по возрастанию (Runnable) отменена");
        }

        if (descendingThread != null && descendingThread.isAlive()) {
            descendingThread.cancel();
            anyCancelled = true;
            System.out.println("Сортировка по убыванию (Thread) отменена");
        }

        if (anyCancelled) {
            showAlert(AlertType.INFORMATION, "Отмена сортировки",
                    "Запрос на отмену многопоточных сортировок отправлен");
        } else {
            restoreOriginalOrder();
            showAlert(AlertType.INFORMATION, "Отмена сортировки",
                    "Сортировка отменена. Восстановлен исходный порядок экскурсий");
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

    // Вспомогательный метод для получения текущих отображаемых экскурсий
    private List<AbstractExcursion> getCurrentDisplayedExcursions() {
        List<AbstractExcursion> current = new ArrayList<>();
        for (Excursion excursion : excursionsData) {
            current.add(excursion);
        }
        return current;
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}