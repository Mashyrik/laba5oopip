package com.example.laba5;

import java.util.List;

public class DeleteExcursionCommand implements AdminCommand {
    private int index;
    private AbstractExcursion deletedExcursion;

    public DeleteExcursionCommand(int index) {
        this.index = index;
    }

    @Override
    public boolean execute() {
        List<AbstractExcursion> excursions = ExcursionStudio.getInstance().getExcursions();
        if (index >= 0 && index < excursions.size()) {
            deletedExcursion = excursions.get(index);
            excursions.remove(index);
            ExcursionStudio.getInstance().saveToFile();
            return true;
        }
        return false;
    }

    @Override
    public void undo() {
        if (deletedExcursion != null) {
            ExcursionStudio.getInstance().addExcursion(deletedExcursion);
        }
    }
}