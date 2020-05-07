package com.vladimir.rebusapp.adapters;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.vladimir.rebusapp.R;
import com.vladimir.rebusapp.database.rebuses.Rebus;
import com.vladimir.rebusapp.utils.Repository;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.vladimir.rebusapp.utils.Constants.REBUSES_QUANTITY_FOR_LEVEL;

public class HowToPlayViewPagerAdapter extends RecyclerView.Adapter<HowToPlayViewPagerAdapter.ViewHolder>
{

    private final String TAG = "HowToPlayViewPagerAdapter";
    private Resources resources;

    @Nullable private OnFinishTrainingClickListener onFinishTrainingClickListener;

    public HowToPlayViewPagerAdapter() {
        resources = Repository.getInstance().getResources();
    }

    @NonNull
    @Override
    public HowToPlayViewPagerAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_how_to_play_page, parent, false);
        return new HowToPlayViewPagerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull HowToPlayViewPagerAdapter.ViewHolder holder,
            int position
    ) {
        switch (position) {
            case 0:
                holder.image.setImageResource(R.drawable.img_how_to_play_0);
                holder.text.setText(resources.getString(R.string.how_to_play_text_0));
                holder.layout.setBackgroundResource(R.color.colorHowToPlay0);
                holder.button.setVisibility(View.INVISIBLE);
                break;
            case 1:
                holder.image.setImageResource(R.drawable.img_how_to_play_1);
                holder.text.setText(resources.getString(R.string.how_to_play_text_1));
                holder.layout.setBackgroundResource(R.color.colorHowToPlay1);
                holder.button.setVisibility(View.INVISIBLE);
                break;
            case 2:
                holder.image.setImageResource(R.drawable.img_how_to_play_2);
                holder.text.setText(resources.getString(R.string.how_to_play_text_2));
                holder.layout.setBackgroundResource(R.color.colorHowToPlay2);
                holder.button.setVisibility(View.INVISIBLE);
                break;
            case 3:
                holder.image.setImageResource(R.drawable.img_how_to_play_3);
                holder.text.setText(resources.getString(R.string.how_to_play_text_3));
                holder.layout.setBackgroundResource(R.color.colorHowToPlay3);
                holder.button.setVisibility(View.INVISIBLE);
                break;
            case 4:
                holder.image.setImageResource(R.drawable.img_how_to_play_4);
                holder.text.setText(resources.getString(R.string.how_to_play_text_4));
                holder.layout.setBackgroundResource(R.color.colorHowToPlay4);
                holder.button.setVisibility(View.VISIBLE);
                break;
            default: break;
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public void setOnFinishTrainingClickListener(
            @Nullable OnFinishTrainingClickListener onFinishTrainingClickListener
    ) {
        this.onFinishTrainingClickListener = onFinishTrainingClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        private final TextView text;
        private final LinearLayout layout;
        private final Button button;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image  = itemView.findViewById(R.id.how_to_play_image);
            text   = itemView.findViewById(R.id.how_to_play_text);
            layout = itemView.findViewById(R.id.how_to_play_text_layout);

            button = itemView.findViewById(R.id.how_to_play_finish_btn);
            button.setOnClickListener(v -> {
                if (onFinishTrainingClickListener != null) {
                    onFinishTrainingClickListener.finishTraining();
                }
            });
        }
    }

    public interface OnFinishTrainingClickListener {
        void finishTraining();
    }

}