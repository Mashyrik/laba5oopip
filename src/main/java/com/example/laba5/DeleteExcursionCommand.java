package com.example.laba5;

import java.util.List;

public class DeleteExcursionCommand implements AdminCommand {
    private ExcursionStudio studio;
    private int index;
    private AbstractExcursion deletedExcursion;

    public DeleteExcursionCommand(int index) {
        this.studio = ExcursionStudio.getInstance(); // Используем Singleton
        this.index = index;
    }

    @Override
    public boolean execute() {
        List<AbstractExcursion> excursions = studio.getExcursions();
        if (index >= 0 && index < excursions.size()) {
            deletedExcursion = excursions.get(index);
            excursions.remove(index);
            return true;
        }
        return false;
    }

    @Override
    public void undo() {
        if (deletedExcursion != null) {
            studio.addExcursion(deletedExcursion);
        }
    }
}