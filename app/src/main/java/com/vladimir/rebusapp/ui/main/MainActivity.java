package com.vladimir.rebusapp.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vladimir.rebusapp.R;
import com.vladimir.rebusapp.adapters.LevelsViewPagerAdapter;
import com.vladimir.rebusapp.ui.dialogs.HowToPlayDialogFragment;
import com.vladimir.rebusapp.ui.levels.LevelsFragment;
import com.vladimir.rebusapp.ui.rebuses.RebusFragment;
import com.vladimir.rebusapp.ui.settings.SettingsFragment;
import com.vladimir.rebusapp.utils.Repository;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private MainViewModel mViewModel;
    private ProgressBar centerProgressBar;
    private ProgressBar bottomProgressBar;
    private FrameLayout centerProgressLayout;
    private FrameLayout bottomProgressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        TextView scoreLabel = findViewById(R.id.main_score_label);

        centerProgressBar = findViewById(R.id.main_center_progress_bar);
        bottomProgressBar = findViewById(R.id.main_bottom_progress_bar);
        centerProgressLayout = findViewById(R.id.main_center_progress_layout);
        bottomProgressLayout = findViewById(R.id.main_bottom_progress_layout);

        checkFirstLaunch();

        if (Repository.getInstance().isSynchronizationCompleted()) {
            centerProgressLayout.setVisibility(View.GONE);
            bottomProgressLayout.setVisibility(View.GONE);
        }

        restoreFragment();

        Repository
                .getInstance()
                .getGlideLoadingPercentLiveData()
                .observe(this, percent -> {
                    centerProgressBar.setProgress(percent);
                    bottomProgressBar.setProgress(percent);
                });

        Repository.getInstance().getLoadingStateLiveData()
                .observe(this, loadingState -> {
                    showLoadingState(loadingState);
                    scoreLabel.setText(mViewModel.getScore());
                    switch (loadingState) {
                        case Repository.LOADING_DB_COMPLETED:
                            restoreFragment();
                            if (Repository.getInstance().getAvailableRebusesQuantity() == 0) {
                                setCenterLoading();
                            }
                            break;
                        case Repository.LOADING_GLIDE_STARTS:
                            centerProgressBar.setIndeterminate(false);
                            bottomProgressBar.setIndeterminate(false);
                            break;
                        case Repository.LOADING_SYNC_COMPLETED:
//                            mViewModel.setSyncFinished(true);
                            restoreFragment();
                        case Repository.LOADING_SYNC_WITHOUT_RESULT:
//                            if (mViewModel.isSyncFinished()) mViewModel.setLoadingCompleted();
                            hideProgressBars();
                            setPlaceholder();
//                            mViewModel.setSyncFinished(true);
                        default: break;
                    }
                });
    }

    private void checkFirstLaunch() {
        if (!Repository.getInstance().checkFirstLaunch()) {
            HowToPlayDialogFragment howToPlayDialog = new HowToPlayDialogFragment();
            howToPlayDialog.show(getSupportFragmentManager(), null);
            Repository.getInstance().completeFirstLaunch();
        }
    }

    private void hideProgressBars() {
        centerProgressLayout.setVisibility(View.GONE);
        bottomProgressLayout.setVisibility(View.GONE);
        mViewModel.setCenterProgressVisibility(View.GONE);
        mViewModel.setBottomProgressVisibility(View.GONE);
    }

    private void setCenterLoading() {
        if (centerProgressBar.getVisibility() != View.VISIBLE &&
                bottomProgressBar.getVisibility() != View.VISIBLE) return;
        centerProgressLayout.setVisibility(View.VISIBLE);
        bottomProgressLayout.setVisibility(View.GONE);
        centerProgressLayout.bringToFront();
        mViewModel.setCenterProgressVisibility(View.VISIBLE);
        mViewModel.setBottomProgressVisibility(View.GONE);
    }

    private void showLoadingState(int loadingState) {
        String loadingText = "current state is ";
        switch (loadingState) {
            case Repository.LOADING_DB_COMPLETED:
                Log.i(TAG, loadingText + "DB_COMPLETED");
                break;
            case Repository.LOADING_GLIDE_STARTS:
                Log.i(TAG, loadingText + "GLIDE_STARTS");
                break;
            case Repository.LOADING_NOT_STARTED:
                Log.i(TAG, loadingText + "NOT_STARTED");
                break;
            case Repository.LOADING_SYNC_COMPLETED:
                Log.i(TAG, loadingText + "SYNC_COMPLETED");
                break;
            case Repository.LOADING_SYNC_WITHOUT_RESULT:
                Log.i(TAG, loadingText + "SYNC_WITHOUT_RESULT");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mViewModel.getState() == MainViewModel.LEVEL_IS_OPEN) {
            super.onBackPressed();
        } else {
            openLevels();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        mViewModel.setLoadingCompleted();
    }

    private void restoreFragment() {
        switch (mViewModel.getState()) {
            case MainViewModel.REBUS_IS_OPEN:
                openRebus(-1);
                break;
            case MainViewModel.SETTINGS_ARE_OPEN:
                openSettings();
                break;
            default:
                openLevels();
                break;
        }
    }

    private void setPlaceholder() {
        if (Repository.getInstance().getAvailableRebusesQuantity() == 0) {
            findViewById(R.id.main_empty_placeholder).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.main_empty_placeholder).setVisibility(View.GONE);
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }

    public void openFragmentWithoutStack(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void openRebus(int levelIndex) {
        Log.i(TAG, "Rebus #" + Repository.getLevel(levelIndex) + "." + Repository.getSubLevel(levelIndex) + " clicked");
        int availableRebusesQuantity = Repository.getInstance().getAvailableRebusesQuantity();
        if (levelIndex >= availableRebusesQuantity || availableRebusesQuantity == 0) {
            openLevels();
            return;
        }

        RebusFragment rebusFragment = new RebusFragment();
        rebusFragment.setOnBackClickListener(onBackClickListener);

        Bundle bundle = new Bundle();
        bundle.putInt(RebusFragment.LEVEL_INDEX, levelIndex);
        rebusFragment.setArguments(bundle);

        mViewModel.setState(MainViewModel.REBUS_IS_OPEN);
        openFragmentWithoutStack(rebusFragment);
    }

    public void openLevels() {
        LevelsFragment levelsFragment = new LevelsFragment();
        levelsFragment.setOnRebusClickListener(onRebusClickListener);
        levelsFragment.setOnSettingsClickListener(onSettingsClickListener);
        mViewModel.setState(MainViewModel.LEVEL_IS_OPEN);
        openFragmentWithoutStack(levelsFragment);
    }

    public void openSettings() {
        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setOnBackClickListener(onBackClickListener);
        mViewModel.setState(MainViewModel.SETTINGS_ARE_OPEN);
        openFragmentWithoutStack(settingsFragment);
        Log.d(TAG, "#settings has clicked");
    }

    private final OnNavigationClickListener onBackClickListener = this::openLevels;
    private final OnNavigationClickListener onSettingsClickListener = this::openSettings;
    private final LevelsViewPagerAdapter.OnRebusClickListener onRebusClickListener = levelIndex -> {
        if (Repository.getInstance().isLevelUnlocked(levelIndex)) {
            openRebus(levelIndex);
        }
    };

    public interface OnNavigationClickListener {
        void onClick();
    }
}
