package com.vladimir.rebusapp.ui.dialogs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vladimir.rebusapp.R;
import com.vladimir.rebusapp.adapters.HowToPlayViewPagerAdapter;

import java.net.Inet4Address;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class FeedbackDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        return inflater.inflate(R.layout.dialog_feedback, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        view.findViewById(R.id.feedback_close_btn).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.feedback_mail_layout).setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(
                    "mailto:"+getResources().getString(R.string.feedback_mail)
            ));
            requireContext().startActivity(emailIntent);
        });

        view.findViewById(R.id.feedback_vk_layout).setOnClickListener(v -> {
            Intent vkIntent = new Intent(Intent.ACTION_VIEW);
            vkIntent.setData(Uri.parse(getResources().getString(R.string.feedback_vk_full)));
            requireContext().startActivity(vkIntent);
        });
    }

}
