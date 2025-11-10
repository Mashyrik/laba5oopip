package com.example.laba5;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class SortThread extends Thread {
    private final List<AbstractExcursion> originalList;
    private final boolean ascending;
    private final ExcursionStudio studio;
    private volatile boolean cancelled = false;

    public SortThread(List<AbstractExcursion> excursions, boolean ascending, ExcursionStudio studio) {
        super("Descending-Thread-Subclass");
        this.originalList = new ArrayList<>(excursions);
        this.ascending = ascending;
        this.studio = studio;
    }

    public void cancel() {
        cancelled = true;
    }

    @Override
    public void run() {
        System.out.println(getName() + ": Начало сортировки через Thread наследование...");

        try {
            if (cancelled || isInterrupted()) {
                System.out.println(getName() + ": Сортировка отменена до начала выполнения");
                return;
            }

            List<AbstractExcursion> toSort = new ArrayList<>(originalList);
            Comparator<AbstractExcursion> comparator = Comparator.comparing(AbstractExcursion::getPlace);
            if (!ascending) {
                comparator = comparator.reversed();
            }

            if (cancelled || isInterrupted()) {
                System.out.println(getName() + ": Сортировка отменена перед выполнением");
                return;
            }

            Collections.sort(toSort, comparator);

            if (cancelled || isInterrupted()) {
                System.out.println(getName() + ": Сортировка отменена после выполнения");
                return;
            }

            studio.applySortedExcursions(toSort, ascending);
            System.out.println(getName() + ": Сортировка через Thread наследование завершена");

        } catch (Exception e) {
            System.out.println(getName() + ": Ошибка в Thread потоке: " + e.getMessage());
        }
    }
}