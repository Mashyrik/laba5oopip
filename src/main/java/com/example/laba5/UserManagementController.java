package com.example.laba5;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserManagementController {

    @FXML private TableView<User> usersTable;

    private UserManager userManager = UserManager.getInstance();
    private ObservableList<User> usersData;

    @FXML
    private void initialize() {
        initializeTable();
        loadUsersData();
    }

    private void initializeTable() {
        TableColumn<User, String> usernameColumn = (TableColumn<User, String>) usersTable.getColumns().get(0);
        TableColumn<User, String> roleColumn = (TableColumn<User, String>) usersTable.getColumns().get(1);
        TableColumn<User, String> statusColumn = (TableColumn<User, String>) usersTable.getColumns().get(2);
        TableColumn<User, String> actionsColumn = (TableColumn<User, String>) usersTable.getColumns().get(3);

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Колонка статуса
        statusColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            String status = user.isBlocked() ? "Заблокирован" : "Активен";
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        // Колонка действий
        actionsColumn.setCellFactory(column -> new javafx.scene.control.TableCell<User, String>() {
            private final javafx.scene.layout.HBox buttonContainer = new javafx.scene.layout.HBox(5);
            private final javafx.scene.control.Button blockButton = new javafx.scene.control.Button("Заблокировать");
            private final javafx.scene.control.Button unblockButton = new javafx.scene.control.Button("Разблокировать");

            {
                blockButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12px;");
                unblockButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 12px;");

                blockButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleBlockUser(user);
                });

                unblockButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleUnblockUser(user);
                });

                buttonContainer.getChildren().addAll(blockButton, unblockButton);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    // Показываем соответствующие кнопки в зависимости от статуса
                    blockButton.setVisible(!user.isBlocked());
                    unblockButton.setVisible(user.isBlocked());
                    setGraphic(buttonContainer);
                }
            }
        });

        usersData = FXCollections.observableArrayList();
        usersTable.setItems(usersData);
    }

    private void loadUsersData() {
        usersData.clear();
        usersData.addAll(userManager.getUsers());
        System.out.println("Загружено пользователей: " + usersData.size());
    }

    private void handleBlockUser(User user) {
        if (userManager.blockUser(user.getUsername())) {
            showAlert(AlertType.INFORMATION, "Успех", "Пользователь " + user.getUsername() + " заблокирован");
            loadUsersData(); // Обновляем таблицу
        } else {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось заблокировать пользователя");
        }
    }

    private void handleUnblockUser(User user) {
        if (userManager.unblockUser(user.getUsername())) {
            showAlert(AlertType.INFORMATION, "Успех", "Пользователь " + user.getUsername() + " разблокирован");
            loadUsersData(); // Обновляем таблицу
        } else {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось разблокировать пользователя");
        }
    }

    @FXML
    private void handleRefresh() {
        loadUsersData();
        showAlert(AlertType.INFORMATION, "Обновление", "Список пользователей обновлен");
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/laba5/admin_dashboard.fxml"));
            Stage stage = (Stage) usersTable.getScene().getWindow();
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