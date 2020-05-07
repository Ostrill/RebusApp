package com.vladimir.rebusapp.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.vladimir.rebusapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static com.vladimir.rebusapp.utils.Constants.HELP_PRICE;

public class ResetDialogFragment extends DialogFragment {

    public static final String YES_NO_REQUEST_KEY ="yes_no_request";
    public static final String YES_NO_KEY = "yes_no";

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        return inflater.inflate(R.layout.dialog_reset, null);
    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        return new MaterialAlertDialogBuilder(requireContext())
//                .setBackground(getResources().getDrawable(R.color.colorDialogBackground))
//                .setView(R.layout.dialog_reset)
//                .setPositiveButton(R.id.reset_yes_btn, (dialog, which) -> {
//                    Bundle bundle = new Bundle();
//                    bundle.putBoolean(YES_NO_KEY, true);
//                    getParentFragmentManager().setFragmentResult(YES_NO_REQUEST_KEY, bundle);
//                    dismiss();
//                })
//                .setNegativeButton(R.id.reset_no_btn, (dialog, which) -> {
//                    Bundle bundle = new Bundle();
//                    bundle.putBoolean(YES_NO_KEY, true);
//                    getParentFragmentManager().setFragmentResult(YES_NO_REQUEST_KEY, bundle);
//                    dismiss();
//                })
//                .create();
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button positiveButton = view.findViewById(R.id.reset_yes_btn);
        Button negativeButton = view.findViewById(R.id.reset_no_btn);

        positiveButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean(YES_NO_KEY, true);
            getParentFragmentManager().setFragmentResult(YES_NO_REQUEST_KEY, bundle);
            dismiss();
        });

        negativeButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putBoolean(YES_NO_KEY, false);
            getParentFragmentManager().setFragmentResult(YES_NO_REQUEST_KEY, bundle);
            dismiss();
        });
    }
}
