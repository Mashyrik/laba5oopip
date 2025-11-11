package com.example.laba5;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExcursionStudio implements Serializable {
    private static ExcursionStudio instance;
    private List<AbstractExcursion> excursions;

    private ExcursionStudio() {
        excursions = new ArrayList<>();
        loadFromFile(); // Загружаем при создании

        // Если файла нет, создаем тестовые экскурсии
        if (excursions.isEmpty()) {
            addExcursion(new Excursion("Минск", "выходные", "утро", "высоко"));
            addExcursion(new Excursion("Брест", "будни", "вечер", "средне"));
            System.out.println("✅ Созданы тестовые экскурсии");
        }
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
        System.out.println("✅ Экскурсия добавлена: " + excursion.getPlace());
    }

    public void removeExcursion(int index) {
        if (index >= 0 && index < excursions.size()) {
            AbstractExcursion removed = excursions.remove(index);
            saveToFile();
            System.out.println("✅ Экскурсия удалена: " + removed.getPlace());
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

    public synchronized void applySortedExcursions(List<AbstractExcursion> sortedList, boolean ascending) {
        this.excursions = new ArrayList<>(sortedList);
        saveToFile();
        System.out.println("✅ Сортировка применена (" + (ascending ? "по возрастанию" : "по убыванию") +
                "), сохранено " + excursions.size() + " экскурсий");
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
            System.out.println("✅ Экскурсии сохранены: " + excursions.size() + " экскурсий");
        } catch (IOException e) {
            System.out.println("❌ Ошибка сохранения экскурсий: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("excursions.dat"))) {
            ExcursionStudio loaded = (ExcursionStudio) ois.readObject();
            this.excursions = loaded.excursions;
            System.out.println("✅ Экскурсии загружены: " + excursions.size() + " экскурсий");
        } catch (FileNotFoundException e) {
            System.out.println("Файл экскурсий не найден, будут созданы тестовые данные");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Ошибка загрузки экскурсий: " + e.getMessage());
        }
    }
}