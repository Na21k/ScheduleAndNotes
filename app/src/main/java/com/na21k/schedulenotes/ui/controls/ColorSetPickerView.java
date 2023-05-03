package com.na21k.schedulenotes.ui.controls;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.na21k.schedulenotes.data.models.ColorSetModel;
import com.na21k.schedulenotes.databinding.ColorPickerItemBinding;

import java.util.ArrayList;
import java.util.List;

public class ColorSetPickerView extends HorizontalScrollView {

    private List<ColorSetModel> mModels;
    private ColorSetModel mSelectedModel;
    private LinearLayoutCompat mRootLinearLayout;

    public ColorSetPickerView(Context context) {
        super(context);
        init();
    }

    public ColorSetPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorSetPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mRootLinearLayout = new LinearLayoutCompat(getContext());
        addView(mRootLinearLayout);
        ViewGroup.LayoutParams layoutParams = mRootLinearLayout.getLayoutParams();
        layoutParams.width = layoutParams.height = WRAP_CONTENT;
    }

    @Nullable
    public List<ColorSetModel> getModels() {
        return mModels;
    }

    public void setModels(@NonNull ArrayList<ColorSetModel> models,
                          @NonNull ColorSetModel defaultSelection) throws IllegalArgumentException {
        if (models.contains(null)) {
            throw new IllegalArgumentException("No null elements are allowed");
        }

        if (!models.contains(defaultSelection)) {
            throw new IllegalArgumentException("models must contain defaultSelection");
        }

        mModels = models;
        mSelectedModel = defaultSelection;
        mRootLinearLayout.removeAllViews();

        for (ColorSetModel model : models) {
            ColorSetPickerItemView itemView = createItemView(model, defaultSelection);
            mRootLinearLayout.addView(itemView);
        }
    }

    @Nullable
    public ColorSetModel getSelectedModel() {
        return mSelectedModel;
    }

    public void setSelectedModel(@Nullable ColorSetModel selectedModel)
            throws IllegalStateException, IllegalArgumentException {
        if (mModels == null) {
            throw new IllegalStateException();
        }

        if (!mModels.contains(selectedModel)) {
            throw new IllegalArgumentException();
        }

        mSelectedModel = selectedModel;

        mRootLinearLayout.dispatchSetSelected(false);
        getItemViewByModel(selectedModel).setSelected(true);
    }

    @NonNull
    private ColorSetPickerItemView createItemView(@NonNull ColorSetModel model,
                                                  @NonNull ColorSetModel defaultSelection) {
        ColorSetPickerItemView res = new ColorSetPickerItemView(getContext());

        res.setModel(model);
        res.setOnClickListener(v -> {
            mRootLinearLayout.dispatchSetSelected(false);
            res.setSelected(true);
            mSelectedModel = model;
        });

        if (model == defaultSelection) {
            res.setSelected(true);
        }

        return res;
    }

    @NonNull
    private View getItemViewByModel(ColorSetModel colorSetModel) {
        if (mModels == null) {
            throw new IllegalStateException();
        }

        int position = mModels.indexOf(colorSetModel);

        if (position == -1) {
            throw new IllegalArgumentException();
        }

        View res = mRootLinearLayout.getChildAt(position);

        if (res == null) {
            throw new IllegalStateException();
        }

        return res;
    }

    private static class ColorSetPickerItemView extends FrameLayout {

        private ColorPickerItemBinding mBinding;

        public ColorSetPickerItemView(Context context) {
            super(context);
            init();
        }

        public ColorSetPickerItemView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ColorSetPickerItemView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init() {
            LayoutParams layoutParams = generateDefaultLayoutParams();
            layoutParams.width = layoutParams.height = LayoutParams.WRAP_CONTENT;
            setLayoutParams(layoutParams);

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mBinding = ColorPickerItemBinding.inflate(inflater, this, true);
        }

        public void setModel(@NonNull ColorSetModel model) {
            mBinding.colorPickerItemLightColorCard.setCardBackgroundColor(model.getColorDayHex());
            mBinding.colorPickerItemDarkColorCard.setCardBackgroundColor(model.getColorNightHex());
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            mBinding.colorPickerItemImageWhenSelected.setVisibility(selected ? VISIBLE : INVISIBLE);
        }
    }
}
