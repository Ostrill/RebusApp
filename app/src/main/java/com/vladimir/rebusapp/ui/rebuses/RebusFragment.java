package com.vladimir.rebusapp.ui.rebuses;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.vladimir.rebusapp.R;
import com.vladimir.rebusapp.adapters.RebusViewPagerAdapter;
import com.vladimir.rebusapp.ui.dialogs.HelpDialogFragment;
import com.vladimir.rebusapp.ui.main.MainActivity;
import com.vladimir.rebusapp.utils.Repository;

import static com.vladimir.rebusapp.utils.Constants.REBUSES_QUANTITY_FOR_LEVEL;

public class RebusFragment extends Fragment {

    public static final String LEVEL_INDEX = "level_index";
    private static final String TAG = "RebusFragment";

    private RebusViewModel mViewModel;
    private int levelIndex;
    private RebusViewPagerAdapter adapter;
    private ViewPager2 viewPager;
    private HelpDialogFragment helpDialog;
    private MainActivity.OnNavigationClickListener onBackClickListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(RebusViewModel.class);
        return inflater.inflate(R.layout.fragment_rebus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getLevelIndex();

        adapter = new RebusViewPagerAdapter(
                Repository.getInstance().getAllRebuses(),
                Glide.with(this),
                Repository.getLevel(levelIndex),
                mViewModel.getSavedEdits()
        );

        viewPager = view.findViewById(R.id.rebus_view_pager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(Repository.getSubLevel(levelIndex), false);

        view.findViewById(R.id.rebus_back_button).setOnClickListener(v -> {
            if (onBackClickListener != null) onBackClickListener.onClick();
        });
    }

    private void getLevelIndex() {
        Bundle bundle = this.getArguments();
        levelIndex = 0;
        if (bundle != null) {
            levelIndex = bundle.getInt(LEVEL_INDEX, -1);
            Log.d(TAG, "bundle levelIndex is " + levelIndex);
            if (levelIndex < 0) {
                levelIndex = mViewModel.getCurrentLevelIndex();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.setOnAnswerListener(onAnswerListener);
        adapter.setOnEditChangeListener(onEditChangeListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.setOnAnswerListener(null);
        adapter.setOnEditChangeListener(null);
        int firstIndex = Repository.getLevel(levelIndex) * REBUSES_QUANTITY_FOR_LEVEL;
        mViewModel.setCurrentLevelIndex(firstIndex + viewPager.getCurrentItem());
    }

    public void setOnBackClickListener(MainActivity.OnNavigationClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
    }


    private void showHelpDialog(int levelIndex) {
        getParentFragmentManager().setFragmentResultListener(
                HelpDialogFragment.YES_NO_REQUEST_KEY,
                requireActivity(),
                (requestKey, bundle) -> {
                    boolean result = bundle.getBoolean(HelpDialogFragment.YES_NO_KEY);
                    if (result) {
                        Repository.getInstance().buySolution(levelIndex);
//                        adapter.notifyItemChanged(Repository.getSubLevel(levelIndex));
                    }
                }
        );

        if (helpDialog == null) {
            helpDialog = new HelpDialogFragment();
        }
        if (!helpDialog.isAdded()) {
            helpDialog.show(getParentFragmentManager(), null);
        }
    }

    private final RebusViewPagerAdapter.OnEditChangeListener onEditChangeListener = (i, s) ->
        mViewModel.setEditValue(i, s);

    private final RebusViewPagerAdapter.OnAnswerListener onAnswerListener =
            new RebusViewPagerAdapter.OnAnswerListener() {
                @Override
                public boolean onTextSubmit(int levelIndex, String attempt) {
                    if (Repository
                            .getInstance()
                            .getAllRebuses()
                            .get(levelIndex)
                            .answer
                            .toLowerCase()
                            .trim()
                            .equals(attempt.toLowerCase().trim())
                    ) {
                        Repository.getInstance().solveRebus(levelIndex);
                        return true;
                    }
                    return false;
                }

                @Override
                public void onHelpClick(int levelIndex) {
                    Log.d(TAG, "help has been clicked");
                    showHelpDialog(levelIndex);
                }
            };

}
