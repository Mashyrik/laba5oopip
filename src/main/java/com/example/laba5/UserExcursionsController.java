package com.example.laba5;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class UserExcursionsController {

    @FXML private FlowPane excursionsContainer;
    @FXML private ComboBox<String> dayFilterComboBox;
    @FXML private ComboBox<String> guideFilterComboBox;
    @FXML private Label costLabel;

    private ExcursionStudio studio = ExcursionStudio.getInstance();
    private ObservableList<Excursion> excursionsData;
    private ObservableList<Excursion> allExcursionsData;
    private List<AbstractExcursion> originalOrder;

    private SortTask ascendingTask = null;
    private SortThread descendingThread = null;
    private Excursion selectedExcursion = null;

    @FXML
    private void initialize() {
        studio.loadFromFile();
        originalOrder = new ArrayList<>(studio.getExcursions());

        initializeFilters();
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

    private void loadExcursionsData() {
        allExcursionsData = FXCollections.observableArrayList();
        excursionsData = FXCollections.observableArrayList();

        // Загружаем все экскурсии
        for (AbstractExcursion abstractExcursion : studio.getExcursions()) {
            if (abstractExcursion instanceof Excursion) {
                allExcursionsData.add((Excursion) abstractExcursion);
            }
        }

        excursionsData.setAll(allExcursionsData);
        displayExcursions();
    }

    private void displayExcursions() {
        excursionsContainer.getChildren().clear();
        selectedExcursion = null;
        costLabel.setText("Выберите экскурсию из списка");

        for (Excursion excursion : excursionsData) {
            VBox excursionCard = createExcursionCard(excursion);
            excursionsContainer.getChildren().add(excursionCard);
        }
    }

    private VBox createExcursionCard(Excursion excursion) {
        VBox card = new VBox();
        card.getStyleClass().add("form-container");
        card.setStyle("-fx-pref-width: 260px; -fx-pref-height: 170px; -fx-padding: 15px; -fx-spacing: 10px; -fx-cursor: hand; -fx-alignment: center;");

        // Заголовок карточки
        Label titleLabel = new Label("🚗 " + excursion.getPlace());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-text-alignment: center;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(240);
        titleLabel.setAlignment(javafx.geometry.Pos.CENTER);

        // Детали экскурсии
        Label dayLabel = new Label("📅 " + excursion.getDayType());
        dayLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
        dayLabel.setAlignment(javafx.geometry.Pos.CENTER);

        Label timeLabel = new Label("⏰ " + excursion.getTimeOfDay());
        timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
        timeLabel.setAlignment(javafx.geometry.Pos.CENTER);

        Label guideLabel = new Label("👨‍💼 " + excursion.getGuideLevel());
        guideLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
        guideLabel.setAlignment(javafx.geometry.Pos.CENTER);

        // Стоимость (будет рассчитана при выборе)
        Label costHintLabel = new Label("💵 Нажмите для расчета");
        costHintLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3498db; -fx-font-style: italic; -fx-text-alignment: center;");
        costHintLabel.setAlignment(javafx.geometry.Pos.CENTER);

        card.getChildren().addAll(titleLabel, dayLabel, timeLabel, guideLabel, costHintLabel);

        // Обработчик клика
        card.setOnMouseClicked(event -> {
            // Сбрасываем выделение у всех карточек
            for (var child : excursionsContainer.getChildren()) {
                if (child instanceof VBox) {
                    child.setStyle("-fx-pref-width: 260px; -fx-pref-height: 170px; -fx-padding: 15px; -fx-spacing: 10px; -fx-cursor: hand; -fx-alignment: center; -fx-background-color: white;");
                }
            }

            // Выделяем выбранную карточку
            card.setStyle("-fx-pref-width: 260px; -fx-pref-height: 170px; -fx-padding: 15px; -fx-spacing: 10px; -fx-cursor: hand; -fx-alignment: center; -fx-background-color: #e3f2fd; -fx-border-color: #3498db; -fx-border-width: 2px;");

            selectedExcursion = excursion;
            costLabel.setText("Выбрана экскурсия: " + excursion.getPlace() + "\nНажмите 'Рассчитать стоимость'");
        });

        return card;
    }

    @FXML
    private void handleApplyFilters() {
        applyFilters();
    }

    @FXML
    private void handleResetFilters() {
        dayFilterComboBox.setValue("Все");
        guideFilterComboBox.setValue("Все");
        costLabel.setText("Выберите экскурсию из списка");

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
        } else {
            ObservableList<Excursion> filteredData = FXCollections.observableArrayList();
            for (Excursion excursion : allExcursionsData) {
                boolean dayMatch = "Все".equals(dayFilter) || excursion.getDayType().equals(dayFilter);
                boolean guideMatch = "Все".equals(guideFilter) || excursion.getGuideLevel().equals(guideFilter);

                if (dayMatch && guideMatch) {
                    filteredData.add(excursion);
                }
            }
            excursionsData.setAll(filteredData);
        }

        displayExcursions();
        System.out.println("✅ Применены фильтры. Показано: " + excursionsData.size() + " экскурсий");
    }

    @FXML
    private void handleSortAscending() {
        List<AbstractExcursion> currentExcursions = new ArrayList<>(excursionsData);
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

    @FXML
    private void handleSortDescending() {
        List<AbstractExcursion> currentExcursions = new ArrayList<>(excursionsData);
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
        if (selectedExcursion == null) {
            showAlert(AlertType.ERROR, "Ошибка", "Выберите экскурсию для расчета стоимости");
            return;
        }

        double cost = studio.calculateCost(selectedExcursion);
        costLabel.setText(String.format("💰 Стоимость: %.2f BYN\n\n📍 %s\n📅 %s\n⏰ %s\n👨‍💼 %s",
                cost, selectedExcursion.getPlace(), selectedExcursion.getDayType(),
                selectedExcursion.getTimeOfDay(), selectedExcursion.getGuideLevel()));
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/user_dashboard.fxml"));
            Stage stage = (Stage) excursionsContainer.getScene().getWindow();
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