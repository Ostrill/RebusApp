package com.vladimir.rebusapp.ui.settings;

import com.vladimir.rebusapp.R;
import com.vladimir.rebusapp.database.rebuses.Rebus;
import com.vladimir.rebusapp.utils.Repository;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    public String getSolvedStatistics() {
        return Repository.getInstance().getSolvedQuantity() + "/" +
                Repository.getInstance().getAvailableRebusesQuantity() + " " +
                Repository.getInstance().getResources().getString(R.string.settings_progress);
    }

}
