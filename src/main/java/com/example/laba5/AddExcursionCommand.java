package com.example.laba5;

import java.util.List;

public class AddExcursionCommand implements AdminCommand {
    private AbstractExcursion excursion;

    public AddExcursionCommand(AbstractExcursion excursion) {
        this.excursion = excursion;
    }

    @Override
    public boolean execute() {
        ExcursionStudio.getInstance().addExcursion(excursion);
        return true;
    }

    @Override
    public void undo() {
        List<AbstractExcursion> excursions = ExcursionStudio.getInstance().getExcursions();
        if (!excursions.isEmpty()) {
            excursions.remove(excursions.size() - 1);
            ExcursionStudio.getInstance().saveToFile();
        }
    }
}