package com.vladimir.rebusapp.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vladimir.rebusapp.R;
import com.vladimir.rebusapp.utils.Repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static com.vladimir.rebusapp.utils.Constants.HELP_PRICE;

public class HelpDialogFragment extends DialogFragment {

    public static final String YES_NO_REQUEST_KEY ="yes_no_request";
    public static final String YES_NO_KEY = "yes_no";

    private Button positiveButton;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        return inflater.inflate(R.layout.dialog_help, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        positiveButton = view.findViewById(R.id.help_yes_btn);
        Button negativeButton = view.findViewById(R.id.help_no_btn);

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

        TextView priceLabel = view.findViewById(R.id.help_price_label);
        priceLabel.setText(Integer.toString(HELP_PRICE));
    }

    @Override
    public void onResume() {
        super.onResume();
        positiveButton.setEnabled(Repository.getInstance().getScore() >= HELP_PRICE);
    }
}
