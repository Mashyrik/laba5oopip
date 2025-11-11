package com.example.laba5;

import java.util.List;

public class AddExcursionCommand implements AdminCommand {
    private ExcursionStudio studio;
    private AbstractExcursion excursion;

    public AddExcursionCommand(AbstractExcursion excursion) {
        this.studio = ExcursionStudio.getInstance(); // Используем Singleton
        this.excursion = excursion;
    }
    @Override
    public boolean execute() {
        studio.addExcursion(excursion);
        return true;
    }

    @Override
    public void undo() {
        List<AbstractExcursion> excursions = studio.getExcursions();
        if (!excursions.isEmpty()) {
            excursions.remove(excursions.size() - 1);
        }
    }
}