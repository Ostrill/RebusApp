package com.vladimir.rebusapp.ui.levels;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.vladimir.rebusapp.R;
import com.vladimir.rebusapp.adapters.LevelsViewPagerAdapter;
import com.vladimir.rebusapp.ui.main.MainActivity;
import com.vladimir.rebusapp.utils.Repository;

public class LevelsFragment extends Fragment {

    private static final String TAG = "LevelsFragment";

    private LevelsViewModel mViewModel;
    private LevelsViewPagerAdapter adapter;
    private ViewPager2 viewPager;

    private LevelsViewPagerAdapter.OnRebusClickListener onRebusClickListener;
    private MainActivity.OnNavigationClickListener onSettingsClickListener;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        mViewModel = new ViewModelProvider(requireActivity()).get(LevelsViewModel.class);
        return inflater.inflate(R.layout.fragment_levels, container, false);
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = ViewModelProviders.of(this).get(LevelsViewModel.class);
//    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.setOnRebusClickListener(onRebusClickListener);
        viewPager.setCurrentItem(mViewModel.getCurrentLevel(), false);
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.setOnRebusClickListener(null);
        mViewModel.setCurrentLevel(viewPager.getCurrentItem());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new LevelsViewPagerAdapter(
                Repository.getInstance().getAllRebuses(),
                Glide.with(this)
        );

        viewPager = view.findViewById(R.id.levels_view_pager);
        viewPager.setAdapter(adapter);
//
//        ScrollingPagerIndicator pagerIndicator = view.findViewById(R.id.levels_page_indicator);
//        pagerIndicator.attachToPager(viewPager);

        view.findViewById(R.id.levels_settings_button).setOnClickListener(v ->
                onSettingsClickListener.onClick()
        );

    }

    public void setOnRebusClickListener(
            LevelsViewPagerAdapter.OnRebusClickListener onRebusClickListener
    ) {
        this.onRebusClickListener = onRebusClickListener;
    }

    public void setOnSettingsClickListener(
            MainActivity.OnNavigationClickListener onSettingsClickListener
    ) {
        this.onSettingsClickListener = onSettingsClickListener;
    }

    //    private final LevelsViewPagerAdapter.OnRebusClickListener onRebusClickListener = levelIndex -> {
//        if (Repository.getInstance().isLevelUnlocked(levelIndex)) {
//            ((MainActivity)getActivity()).openRebus(levelIndex);
//        }
//    };

}
