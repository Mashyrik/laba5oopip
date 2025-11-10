package com.example.laba5;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class SortTask implements Runnable {
    private final List<AbstractExcursion> originalList;
    private final boolean ascending;
    private final ExcursionStudio studio;
    private volatile boolean cancelled = false;

    public SortTask(List<AbstractExcursion> excursions, boolean ascending, ExcursionStudio studio) {
        this.originalList = new ArrayList<>(excursions);
        this.ascending = ascending;
        this.studio = studio;
    }

    public void cancel() {
        cancelled = true;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + ": Начало сортировки через Runnable...");

        try {
            if (cancelled || Thread.interrupted()) {
                System.out.println(threadName + ": Сортировка отменена до начала выполнения");
                return;
            }

            List<AbstractExcursion> toSort = new ArrayList<>(originalList);
            Comparator<AbstractExcursion> comparator = Comparator.comparing(AbstractExcursion::getPlace);
            if (!ascending) {
                comparator = comparator.reversed();
            }

            if (cancelled || Thread.interrupted()) {
                System.out.println(threadName + ": Сортировка отменена перед выполнением");
                return;
            }

            Collections.sort(toSort, comparator);

            if (cancelled || Thread.interrupted()) {
                System.out.println(threadName + ": Сортировка отменена после выполнения");
                return;
            }

            studio.applySortedExcursions(toSort, ascending);
            System.out.println(threadName + ": Сортировка через Runnable завершена");

        } catch (Exception e) {
            System.out.println(threadName + ": Ошибка в Runnable потоке: " + e.getMessage());
        }
    }
}