package com.vladimir.rebusapp.ui.rebuses;

import com.vladimir.rebusapp.database.rebuses.Rebus;
import com.vladimir.rebusapp.utils.Repository;

import java.util.ArrayList;

import androidx.lifecycle.ViewModel;

public class RebusViewModel extends ViewModel {

    private int mCurrentLevelIndex;
    private ArrayList<String> mSavedEdits;

    public RebusViewModel() {
        super();
        mCurrentLevelIndex = 0;
        mSavedEdits = new ArrayList<>();
        for (Rebus ignored : Repository.getInstance().getAllRebuses()) mSavedEdits.add("");
    }

    public int getCurrentLevelIndex() {
        return mCurrentLevelIndex;
    }

    public void setCurrentLevelIndex(int currentLevelIndex) {
        this.mCurrentLevelIndex = currentLevelIndex;
    }

    public ArrayList<String> getSavedEdits() {
        return mSavedEdits;
    }

    public void setEditValue(int levelIndex, String text) {
        mSavedEdits.set(levelIndex, text);
    }


}
