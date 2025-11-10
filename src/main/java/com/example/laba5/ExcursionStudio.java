package com.example.laba5;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;

public class ExcursionStudio implements Serializable {
    private List<AbstractExcursion> excursions;
    private transient Predicate<AbstractExcursion> dayFilter;
    private transient Predicate<AbstractExcursion> guideFilter;
    private boolean lastSortAscending = false;
    private boolean sortApplied = false;

    public ExcursionStudio() {
        excursions = new ArrayList<>();
        dayFilter = e -> true;
        guideFilter = e -> true;
    }

    public void addExcursion(AbstractExcursion excursion) {
        excursions.add(excursion);
        saveToFile();
    }

    public List<AbstractExcursion> getExcursions() {
        return new ArrayList<>(excursions);
    }

    public void setDayFilter(String dayType) {
        if (dayType == null || dayType.isEmpty() || dayType.equalsIgnoreCase("пусто")) {
            dayFilter = e -> true;
        } else {
            dayFilter = e -> e.getDayType().equalsIgnoreCase(dayType);
        }
    }

    public void setGuideFilter(String guideLevel) {
        if (guideLevel == null || guideLevel.isEmpty() || guideLevel.equalsIgnoreCase("пусто")) {
            guideFilter = e -> true;
        } else {
            guideFilter = e -> e.getGuideLevel().equalsIgnoreCase(guideLevel);
        }
    }

    public void resetDayFilter() {
        dayFilter = e -> true;
    }

    public void resetGuideFilter() {
        guideFilter = e -> true;
    }

    public List<AbstractExcursion> getFilteredExcursions() {
        return excursions.stream()
                .filter(dayFilter.and(guideFilter))
                .collect(Collectors.toList());
    }

    public synchronized void applySortedExcursions(List<AbstractExcursion> sortedList, boolean ascending) {
        this.lastSortAscending = ascending;
        this.sortApplied = true;

        List<AbstractExcursion> newExcursions = new ArrayList<>();
        newExcursions.addAll(sortedList);

        List<AbstractExcursion> nonFiltered = excursions.stream()
                .filter(e -> !getFilteredExcursions().contains(e))
                .collect(Collectors.toList());
        newExcursions.addAll(nonFiltered);

        this.excursions = newExcursions;
        saveToFile();

        System.out.println("Сортировка применена к основному списку (" +
                (ascending ? "по возрастанию" : "по убыванию") + ")");
    }

    public void reapplySort() {
        if (sortApplied) {
            List<AbstractExcursion> filtered = getFilteredExcursions();
            List<AbstractExcursion> toSort = new ArrayList<>(filtered);

            Comparator<AbstractExcursion> comparator = Comparator.comparing(AbstractExcursion::getPlace);
            if (!lastSortAscending) {
                comparator = comparator.reversed();
            }

            Collections.sort(toSort, comparator);
            applySortedExcursions(toSort, lastSortAscending);

            System.out.println("Автоматическая пересортировка по " +
                    (lastSortAscending ? "возрастанию" : "убыванию") + " выполнена");
        } else {
            System.out.println("Сортировка еще не выполнялась.");
        }
    }

    public void printExcursions() {
        List<AbstractExcursion> listToPrint = getFilteredExcursions();
        if (listToPrint.isEmpty()) {
            System.out.println("Нет доступных экскурсий.");
        } else {
            System.out.println("\nСписок экскурсий (" + listToPrint.size() + "):");
            for (int i = 0; i < listToPrint.size(); i++) {
                System.out.print((i + 1) + ". ");
                listToPrint.get(i).displayDetails();
            }
        }
    }

    public double calculateCost(AbstractExcursion excursion) {
        double base = 30.0;
        if (excursion.getDayType().equalsIgnoreCase("выходные")) base += 15;
        if (excursion.getTimeOfDay().equalsIgnoreCase("вечер")) base += 10;
        switch (excursion.getGuideLevel().toLowerCase()) {
            case "слабо":
                base += 0;
                break;
            case "средне":
                base += 10;
                break;
            case "высоко":
                base += 20;
                break;
        }
        return base;
    }

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("excursions.dat"))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.out.println("Ошибка сохранения экскурсий: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("excursions.dat"))) {
            ExcursionStudio loaded = (ExcursionStudio) ois.readObject();
            this.excursions = loaded.excursions;
            this.lastSortAscending = loaded.lastSortAscending;
            this.sortApplied = loaded.sortApplied;
            this.dayFilter = e -> true;
            this.guideFilter = e -> true;
        } catch (FileNotFoundException e) {
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка загрузки экскурсий: " + e.getMessage());
        }
    }
}