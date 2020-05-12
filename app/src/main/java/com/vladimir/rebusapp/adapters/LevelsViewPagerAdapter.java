package com.vladimir.rebusapp.adapters;

import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.vladimir.rebusapp.R;
import com.vladimir.rebusapp.database.tablerebuses.Rebus;
import com.vladimir.rebusapp.utils.Repository;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.vladimir.rebusapp.utils.Constants.REBUSES_QUANTITY_FOR_LEVEL;

public class LevelsViewPagerAdapter
        extends RecyclerView.Adapter<LevelsViewPagerAdapter.ViewHolder>
{

    private final String TAG = "LevelsViewPagerAdapter";
    private List<Rebus> list;
    private RequestManager glideRequestManager;
    private Resources resources;

    @Nullable private OnRebusClickListener onRebusClickListener;

    public LevelsViewPagerAdapter(
            List<Rebus> list,
            RequestManager glideRequestManager
//            Resources resources
    ) {
//        this.resources = resources;
        resources = Repository.getInstance().getResources();
        this.glideRequestManager = glideRequestManager;
        this.list = new ArrayList<>();
        if (list != null) {
            this.list = list;
        }
    }

    @NonNull
    @Override
    public LevelsViewPagerAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_level_page, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull LevelsViewPagerAdapter.ViewHolder holder,
            int position
    ) {
        int firstIndex = position * REBUSES_QUANTITY_FOR_LEVEL;
        boolean isLevelUnlocked = Repository.getInstance().isLevelUnlocked(firstIndex);

        for (int i = firstIndex; i < firstIndex + REBUSES_QUANTITY_FOR_LEVEL; ++i) {
            Log.i(TAG, "#binding, rebus " + i);
            int current = i - firstIndex;
            holder.rebuses[current].setTag(i);
            RequestBuilder builder =
                    glideRequestManager
                            .load(Repository
                                    .getInstance()
                                    .getStorageRef()
                                    .child(list.get(i).rebusId + ".png"))
                            .placeholder(R.drawable.ic_placeholder)
                            .transition(DrawableTransitionOptions.withCrossFade());

            holder.doneImages[current].setVisibility(View.INVISIBLE);

            if (!isLevelUnlocked) {
                builder.transform(new BlurTransformation(25, 3));
                holder.rebuses[current].setColorFilter(
                        Color.argb(200,0,0,0));
            } else {
                holder.rebuses[current].setColorFilter(null);
                if (list.get(i).score != 0) {
                    builder.transform(new BlurTransformation(25, 1));
                    holder.rebuses[current].setColorFilter(
                            Color.argb(100, 0, 0, 0));
                    holder.doneImages[current].setVisibility(View.VISIBLE);
                    holder.doneImages[current].bringToFront();
                } else {
                    holder.rebuses[current].setColorFilter(null);
                }
            }

            builder.into(holder.rebuses[i - firstIndex]);
        }

        if (!isLevelUnlocked) {
            String lockLabelText =
                    Repository.getInstance().levelsToUnlock(firstIndex) + " " +
                    resources.getString(R.string.rebuses_for_unlock);
            holder.lockLabel.setText(lockLabelText);
            holder.lockLayout.setVisibility(View.VISIBLE);
            holder.lockLayout.bringToFront();
            Log.i(TAG, "blur on " + (position+1) + " level");
        } else {
            holder.lockLayout.setVisibility(View.GONE);
        }

        String levelLabelText = resources.getString(R.string.level) + " " + (position + 1);
        holder.label.setText(levelLabelText);
    }

    @Override
    public int getItemCount() {
        return list.size() / REBUSES_QUANTITY_FOR_LEVEL;
    }

    public void setOnRebusClickListener(@Nullable OnRebusClickListener onRebusClickListener) {
        this.onRebusClickListener = onRebusClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView label;
        private final ImageView[] rebuses;
        private final ImageView[] doneImages;
        private final TableLayout tableLayout;
        private final ConstraintLayout constraintLayout;
        private final LinearLayout lockLayout;
        private final TextView lockLabel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tableLayout = itemView.findViewById(R.id.level_page_table_layout);
            constraintLayout = itemView.findViewById(R.id.level_page_constraint_layout);

            label = itemView.findViewById(R.id.level_page_label);

            lockLayout = itemView.findViewById(R.id.level_page_lock_layout);
            lockLabel  = itemView.findViewById(R.id.level_page_lock_label);

            rebuses = new ImageView[]{
                    itemView.findViewById(R.id.level_page_rebus_1),
                    itemView.findViewById(R.id.level_page_rebus_2),
                    itemView.findViewById(R.id.level_page_rebus_3),
                    itemView.findViewById(R.id.level_page_rebus_4),
                    itemView.findViewById(R.id.level_page_rebus_5),
                    itemView.findViewById(R.id.level_page_rebus_6),
                    itemView.findViewById(R.id.level_page_rebus_7),
                    itemView.findViewById(R.id.level_page_rebus_8)
            };

            doneImages = new ImageView[]{
                    itemView.findViewById(R.id.level_page_done_image_1),
                    itemView.findViewById(R.id.level_page_done_image_2),
                    itemView.findViewById(R.id.level_page_done_image_3),
                    itemView.findViewById(R.id.level_page_done_image_4),
                    itemView.findViewById(R.id.level_page_done_image_5),
                    itemView.findViewById(R.id.level_page_done_image_6),
                    itemView.findViewById(R.id.level_page_done_image_7),
                    itemView.findViewById(R.id.level_page_done_image_8)
            };

            for (ImageView rebus : rebuses) {
                if (onRebusClickListener != null) {
                    rebus.setOnClickListener(v ->
                            onRebusClickListener.onClick((int)rebus.getTag()));
                }
            }
        }
    }

    public interface OnRebusClickListener {
        void onClick(int levelIndex);
    }

}
