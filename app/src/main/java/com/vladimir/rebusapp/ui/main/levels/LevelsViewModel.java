package com.vladimir.rebusapp.ui.main.levels;

import androidx.lifecycle.ViewModel;

public class LevelsViewModel extends ViewModel {

    private int currentLevel;

    public LevelsViewModel() {
        super();
        currentLevel = 0;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }
}
