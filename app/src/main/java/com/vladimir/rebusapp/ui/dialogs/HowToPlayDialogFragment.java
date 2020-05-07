package com.vladimir.rebusapp.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.vladimir.rebusapp.R;
import com.vladimir.rebusapp.adapters.HowToPlayViewPagerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class HowToPlayDialogFragment extends DialogFragment {

    private HowToPlayViewPagerAdapter adapter;
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        return inflater.inflate(R.layout.dialog_how_to_play, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        view.findViewById(R.id.how_to_play_close_btn).setOnClickListener(v -> dismiss());

        adapter = new HowToPlayViewPagerAdapter();
        viewPager = view.findViewById(R.id.how_to_play_view_pager);
        viewPager.setAdapter(adapter);

        ScrollingPagerIndicator indicator = view.findViewById(R.id.how_to_play_pager_indicator);
        indicator.attachToPager(viewPager);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.setOnFinishTrainingClickListener(this::dismiss);
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.setOnFinishTrainingClickListener(null);
    }
}
