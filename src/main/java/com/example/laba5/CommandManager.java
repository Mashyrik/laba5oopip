package com.example.laba5;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CommandManager implements Serializable {
    private static CommandManager instance;
    private List<AdminCommand> commandHistory;
    private List<AdminCommand> undoneCommands;

    private CommandManager() {
        commandHistory = new ArrayList<>();
        undoneCommands = new ArrayList<>();
        loadFromFile(); // Загружаем при создании
    }

    public static synchronized CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
        return instance;
    }

    public void executeCommand(AdminCommand command) {
        if (command.execute()) {
            commandHistory.add(command);
            undoneCommands.clear();
            saveToFile();
            System.out.println("✅ Команда выполнена и сохранена: " + command.getClass().getSimpleName());
        }
    }

    public void undo() {
        if (!commandHistory.isEmpty()) {
            AdminCommand command = commandHistory.remove(commandHistory.size() - 1);
            command.undo();
            undoneCommands.add(command);
            saveToFile();
            System.out.println("✅ Команда отменена: " + command.getClass().getSimpleName());
        }
    }

    public void redo() {
        if (!undoneCommands.isEmpty()) {
            AdminCommand command = undoneCommands.remove(undoneCommands.size() - 1);
            command.execute();
            commandHistory.add(command);
            saveToFile();
            System.out.println("✅ Команда повторена: " + command.getClass().getSimpleName());
        }
    }

    public List<String> getCommandHistory() {
        List<String> history = new ArrayList<>();
        for (AdminCommand cmd : commandHistory) {
            history.add(cmd.getClass().getSimpleName());
        }
        return history;
    }

    public void clearHistory() {
        commandHistory.clear();
        undoneCommands.clear();
        saveToFile();
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("commands.dat"))) {
            oos.writeObject(this);
            System.out.println("✅ История команд сохранена: " + commandHistory.size() + " команд");
        } catch (IOException e) {
            System.out.println("❌ Ошибка сохранения команд: " + e.getMessage());
        }
    }

    private static CommandManager loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("commands.dat"))) {
            CommandManager loaded = (CommandManager) ois.readObject();
            System.out.println("✅ История команд загружена: " + loaded.commandHistory.size() + " команд");
            return loaded;
        } catch (FileNotFoundException e) {
            System.out.println("Файл команд не найден, будет создана новая история");
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Ошибка загрузки команд: " + e.getMessage());
            return null;
        }
    }
}