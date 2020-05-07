package com.vladimir.rebusapp.ui.main;

import android.view.View;

import com.vladimir.rebusapp.utils.Repository;

import androidx.lifecycle.ViewModel;
import androidx.viewpager2.widget.ViewPager2;

public class MainViewModel extends ViewModel {

    public static final int LEVEL_IS_OPEN = 0;
    public static final int REBUS_IS_OPEN = 1;
    public static final int SETTINGS_ARE_OPEN = 2;

    private int state;
    private boolean isSyncFinished;
    private int centerProgressVisibility;
    private int bottomProgressVisibility;

    public MainViewModel() {
        super();
        state = LEVEL_IS_OPEN;
        isSyncFinished = false;
        centerProgressVisibility = View.GONE;
        bottomProgressVisibility = View.VISIBLE;
    }

    public boolean isSyncFinished() {
        return isSyncFinished;
    }

    public void setSyncFinished(boolean syncFinished) {
        isSyncFinished = syncFinished;
    }

    public int getCenterProgressVisibility() {
        return centerProgressVisibility;
    }

    public void setCenterProgressVisibility(int centerProgressVisibility) {
        this.centerProgressVisibility = centerProgressVisibility;
    }

    public int getBottomProgressVisibility() {
        return bottomProgressVisibility;
    }

    public void setBottomProgressVisibility(int bottomProgressVisibility) {
        this.bottomProgressVisibility = bottomProgressVisibility;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getScore() {
        return Integer.toString(Repository.getInstance().getScore());
    }

    public void setLoadingCompleted() {
        Repository.getInstance().completeLoading();
    }
}
