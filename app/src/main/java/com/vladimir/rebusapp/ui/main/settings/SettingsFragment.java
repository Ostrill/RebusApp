package com.vladimir.rebusapp.ui.main.settings;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vladimir.rebusapp.R;
import com.vladimir.rebusapp.ui.dialogs.FeedbackDialogFragment;
import com.vladimir.rebusapp.ui.dialogs.HelpDialogFragment;
import com.vladimir.rebusapp.ui.dialogs.HowToPlayDialogFragment;
import com.vladimir.rebusapp.ui.dialogs.ResetDialogFragment;
import com.vladimir.rebusapp.ui.main.MainActivity;
import com.vladimir.rebusapp.utils.Repository;

public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;
    private MainActivity.OnNavigationClickListener onBackClickListener;

    private ResetDialogFragment resetDialog;
    private HowToPlayDialogFragment howToPlayDialog;
    private FeedbackDialogFragment feedbackDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.settings_back_button).setOnClickListener(v ->
                onBackClickListener.onClick()
        );
        TextView statisticsLabel = view.findViewById(R.id.settings_statistics_label);
        statisticsLabel.setText(mViewModel.getSolvedStatistics());

//        setLanguageSpinner();
        view.findViewById(R.id.settings_reset_button).setOnClickListener(v ->
                showResetDialog());
        view.findViewById(R.id.settings_how_to_play_layout).setOnClickListener(v ->
                showHowToPlayDialog());
        view.findViewById(R.id.settings_feedback_layout).setOnClickListener(v ->
                showFeedbackDialog());

    }

    private void showResetDialog() {
        getParentFragmentManager().setFragmentResultListener(
                HelpDialogFragment.YES_NO_REQUEST_KEY,
                requireActivity(),
                (requestKey, bundle) -> {
                    boolean result = bundle.getBoolean(HelpDialogFragment.YES_NO_KEY);
                    if (result) {
                        Repository.getInstance().resetAllProgress();
                    }
                }
        );

        if (resetDialog == null) {
            resetDialog = new ResetDialogFragment();
        }
        if (!resetDialog.isAdded()) {
            resetDialog.show(getParentFragmentManager(), null);
        }
    }

    private void showHowToPlayDialog() {
        if (howToPlayDialog == null) {
            howToPlayDialog = new HowToPlayDialogFragment();
        }
        if (!howToPlayDialog.isAdded()) {
            howToPlayDialog.show(getParentFragmentManager(), null);
        }
    }

    private void showFeedbackDialog() {
        if (feedbackDialog == null) {
            feedbackDialog = new FeedbackDialogFragment();
        }
        if (!feedbackDialog.isAdded()) {
            feedbackDialog.show(getParentFragmentManager(), null);
        }
    }

//    private void setLanguageSpinner() {
//        Spinner spinner = getView().findViewById(R.id.settings_language_spinner);
//
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                requireContext(),
//                R.array.languages,
//                R.layout.spinner_checked_color
//        );
//        adapter.setDropDownViewResource(R.layout.spinner_text_color);
//        spinner.setAdapter(adapter);
//
//        getView().findViewById(R.id.settings_language_layout)
//                .setOnClickListener(v -> spinner.performClick());
//    }

    public void setOnBackClickListener(MainActivity.OnNavigationClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
    }
}
