package com.example.laba5;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExcursionStudio implements Serializable {
    private static ExcursionStudio instance;
    private List<AbstractExcursion> excursions;

    private ExcursionStudio() {
        excursions = new ArrayList<>();
        // Добавим тестовые экскурсии
        addExcursion(new Excursion("Минск", "выходные", "утро", "высоко"));
        addExcursion(new Excursion("Брест", "будни", "вечер", "средне"));
        loadFromFile(); // Загружаем при создании
    }

    public static synchronized ExcursionStudio getInstance() {
        if (instance == null) {
            instance = new ExcursionStudio();
        }
        return instance;
    }

    public void addExcursion(AbstractExcursion excursion) {
        excursions.add(excursion);
        saveToFile();
    }

    public void removeExcursion(int index) {
        if (index >= 0 && index < excursions.size()) {
            excursions.remove(index);
            saveToFile();
        }
    }

    public List<AbstractExcursion> getExcursions() {
        return new ArrayList<>(excursions);
    }

    public double calculateCost(AbstractExcursion excursion) {
        double base = 30.0;
        if (excursion.getDayType().equalsIgnoreCase("выходные")) base += 15;
        if (excursion.getTimeOfDay().equalsIgnoreCase("вечер")) base += 10;
        switch (excursion.getGuideLevel().toLowerCase()) {
            case "слабо": base += 0; break;
            case "средне": base += 10; break;
            case "высоко": base += 20; break;
        }
        return base;
    }

    // Методы для сортировки
    public List<AbstractExcursion> getFilteredExcursions() {
        return new ArrayList<>(excursions);
    }

    public synchronized void applySortedExcursions(List<AbstractExcursion> sortedList, boolean ascending) {
        this.excursions = new ArrayList<>(sortedList);
        saveToFile();
        System.out.println("Сортировка применена (" + (ascending ? "по возрастанию" : "по убыванию") +
                "), сохранено " + excursions.size() + " экскурсий");
    }

    public void reapplySort() {
        saveToFile();
        System.out.println("Пересортировка выполнена");
    }

    // Методы для фильтрации
    public void setDayFilter(String dayType) {
        System.out.println("Фильтр по дню: " + dayType);
    }

    public void setGuideFilter(String guideLevel) {
        System.out.println("Фильтр по гиду: " + guideLevel);
    }

    public void resetDayFilter() {
        System.out.println("Фильтр по дню сброшен");
    }

    public void resetGuideFilter() {
        System.out.println("Фильтр по гиду сброшен");
    }

    public void printExcursions() {
        if (excursions.isEmpty()) {
            System.out.println("Нет доступных экскурсий.");
        } else {
            System.out.println("\nСписок экскурсий (" + excursions.size() + "):");
            for (int i = 0; i < excursions.size(); i++) {
                System.out.print((i + 1) + ". ");
                excursions.get(i).displayDetails();
            }
        }
    }

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("excursions.dat"))) {
            oos.writeObject(this);
            System.out.println("✅ УСПЕШНО сохранено экскурсий: " + excursions.size());
        } catch (IOException e) {
            System.out.println("Ошибка сохранения экскурсий: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("excursions.dat"))) {
            ExcursionStudio loaded = (ExcursionStudio) ois.readObject();
            this.excursions = loaded.excursions;
            System.out.println("✅ Загружено экскурсий из файла: " + excursions.size());
        } catch (FileNotFoundException e) {
            // Файл не существует - это нормально при первом запуске
            System.out.println("Файл экскурсий не найден, используются тестовые данные");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка загрузки экскурсий: " + e.getMessage());
        }
    }
}