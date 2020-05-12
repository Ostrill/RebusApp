package com.vladimir.rebusapp.adapters;

import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.vladimir.rebusapp.R;
import com.vladimir.rebusapp.database.tablerebuses.Rebus;
import com.vladimir.rebusapp.utils.Repository;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static com.vladimir.rebusapp.utils.Constants.REBUSES_QUANTITY_FOR_LEVEL;

public class RebusViewPagerAdapter extends RecyclerView.Adapter<RebusViewPagerAdapter.ViewHolder>
{
    private final String TAG = "RebusViewPagerAdapter";

    private List<Rebus> mList;
    private int mLevel;
    private RequestManager mGlideRequestManager;
    private Resources mResources;
    private ArrayList<String> mSavedEdits;

//    @Nullable
//    private RebusViewPagerAdapter.OnRebusClickListener onRebusClickListener;
    @Nullable private OnAnswerListener mOnAnswerListener;
    @Nullable private OnEditChangeListener mOnEditChangeListener;

    public RebusViewPagerAdapter(
            List<Rebus> list,
            RequestManager glideRequestManager,
            int level,
            ArrayList<String> savedEdits
//            Resources resources
    ) {
        mResources = Repository.getInstance().getResources();
        mLevel = level;
        mGlideRequestManager = glideRequestManager;
        mList = new ArrayList<>();
        if (list != null) {
            mList = list;
        }
        mSavedEdits = savedEdits;
    }

    @NonNull
    @Override
    public RebusViewPagerAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_rebus_page, parent, false);

        return new RebusViewPagerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RebusViewPagerAdapter.ViewHolder holder,
            int position
    ) {
        int currentIndex = mLevel * REBUSES_QUANTITY_FOR_LEVEL + position;

        holder.rebusImage.setTag(currentIndex);
        mGlideRequestManager
                .load(Repository
                        .getInstance()
                        .getStorageRef()
                        .child(mList.get(currentIndex).rebusId + ".png"))
                .placeholder(R.drawable.ic_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.rebusImage);

        for (int i = 0; i < mList.get(currentIndex).difficulty; ++i) {
//            holder.stars[i].setImageDrawable(holder.stars[0].getDrawable());
            holder.stars[i].setImageResource(R.drawable.ic_star_full);
        }

        if (mList.get(currentIndex).score != 0) {
            holder.setSolved();
        } else {
            holder.setUnsolved();
        }

        holder.answerEdit.setText(mSavedEdits.get(currentIndex));

        Log.d(TAG, "#binding, currentIndex = " + currentIndex + ", rebus: " + mList.get(currentIndex).answer + " [" + mList.get(currentIndex).score + "]");
    }

    @Override
    public int getItemCount() {
        return REBUSES_QUANTITY_FOR_LEVEL;
    }

//    public void setOnRebusClickListener(@Nullable LevelsViewPagerAdapter.OnRebusClickListener onRebusClickListener) {
//        this.onRebusClickListener = onRebusClickListener;
//    }


    public void setOnAnswerListener(@Nullable OnAnswerListener onAnswerListener) {
        mOnAnswerListener = onAnswerListener;
    }

    public void setOnEditChangeListener(@Nullable OnEditChangeListener onEditChangeListener) {
        mOnEditChangeListener = onEditChangeListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextInputEditText answerEdit;
        private final TextInputLayout answerInputLayout;
        private final ImageView rebusImage;
        private final ImageView helpButton;
        private final ImageView[] stars;
        private final TextView answerLabel;
        private final LinearLayout editLayout;
        private final LinearLayout labelLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            answerEdit  = itemView.findViewById(R.id.rebus_page_edit);
            rebusImage  = itemView.findViewById(R.id.rebus_page_image);
            helpButton  = itemView.findViewById(R.id.rebus_page_help_btn);
            answerLabel = itemView.findViewById(R.id.rebus_page_answer_label);
            labelLayout = itemView.findViewById(R.id.rebus_page_answer_label_layout);
            editLayout  = itemView.findViewById(R.id.rebus_page_answer_edit_layout);
            answerInputLayout = itemView.findViewById(R.id.rebus_page_answer_input_layout);
            stars = new ImageView[] {
                    itemView.findViewById(R.id.rebus_page_star_1),
                    itemView.findViewById(R.id.rebus_page_star_2),
                    itemView.findViewById(R.id.rebus_page_star_3),
                    itemView.findViewById(R.id.rebus_page_star_4),
                    itemView.findViewById(R.id.rebus_page_star_5)
            };

            answerEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (answerInputLayout.getBoxBackgroundColor() !=
                            mResources.getColor(R.color.colorEditBox)
                    ) {
                        answerInputLayout.setBoxBackgroundColor(
                                mResources.getColor(R.color.colorEditBox));
                    }
                    if (mOnEditChangeListener != null) {
                        mOnEditChangeListener.onChange((int)rebusImage.getTag(), s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            answerEdit.setOnEditorActionListener((v, actionId, event) -> {
                if ((event != null &&
                        (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE)
                ) {
                    if (mOnAnswerListener != null) {
                        if (mOnAnswerListener.onTextSubmit(
                                (int)rebusImage.getTag(),
                                answerEdit.getText().toString())
                        ) {
                            setSolved();
                        } else {
                            answerInputLayout.setBoxBackgroundColor(
                                    mResources.getColor(R.color.colorEditBoxError));
                            return true;
                        }
                    }
                }
                return false;
            });

            helpButton.setOnClickListener(v -> {
                if (mOnAnswerListener != null) {
                    mOnAnswerListener.onHelpClick((int)rebusImage.getTag());
                }
            });
        }

        void setSolved() {
            answerEdit.clearFocus();
            answerLabel.setText(mList.get((int)rebusImage.getTag()).answer);
            labelLayout.setVisibility(View.VISIBLE);
            editLayout.setVisibility(View.GONE);
        }

        void setUnsolved() {
            answerEdit.clearFocus();
            labelLayout.setVisibility(View.GONE);
            editLayout.setVisibility(View.VISIBLE);
        }
    }

    public interface OnAnswerListener {
        boolean onTextSubmit(int levelIndex, String attempt);
        void onHelpClick(int levelIndex);
    }

    public interface OnEditChangeListener {
        void onChange(int levelIndex, String text);
    }

}
