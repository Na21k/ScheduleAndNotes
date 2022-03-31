package com.na21k.schedulenotes.ui.categories;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.models.ColorSet;
import com.na21k.schedulenotes.data.models.ColorSetModel;
import com.na21k.schedulenotes.databinding.CategoriesListItemBinding;
import com.na21k.schedulenotes.helpers.CategoriesHelper;

import java.util.List;

public class CategoriesListAdapter
        extends RecyclerView.Adapter<CategoriesListAdapter.CategoryViewHolder> {

    private final boolean mIsNightMode;
    private final OnCategoryActionRequestedListener mOnCategoryActionRequestedListener;
    private List<Category> mCategories = null;

    public CategoriesListAdapter(boolean isNightMode, OnCategoryActionRequestedListener onCategoryActionRequestedListener) {
        mIsNightMode = isNightMode;
        mOnCategoryActionRequestedListener = onCategoryActionRequestedListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CategoriesListItemBinding binding = CategoriesListItemBinding
                .inflate(inflater, parent, false);

        return new CategoryViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = mCategories.get(position);
        holder.setData(category);
    }

    @Override
    public int getItemCount() {
        return mCategories != null ? mCategories.size() : 0;
    }

    public void setCategories(List<Category> categories) {
        mCategories = categories;
        notifyDataSetChanged();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private final CategoriesListItemBinding mBinding;
        private Category mCategory;

        public CategoryViewHolder(@NonNull View itemView, CategoriesListItemBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater menuInflater = new MenuInflater(v.getContext());
            menuInflater.inflate(R.menu.category_long_press_menu, menu);

            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                item.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.category_delete_menu_item:
                    mOnCategoryActionRequestedListener.onCategoryDeletionRequested(mCategory);
                    return true;
                default:
                    return false;
            }
        }

        private void setData(@NonNull Category category) {
            mCategory = category;
            mBinding.categoryName.setText(category.getTitle());
            mBinding.categoriesListCard.setCardBackgroundColor(getCardColor(category));

            itemView.setOnClickListener(
                    v -> mOnCategoryActionRequestedListener.onCategoryOpenRequested(category));

            itemView.setOnCreateContextMenuListener(this);
        }

        private int getCardColor(Category category) {
            ColorSet categoryColorSet = category.getColorSet();
            List<ColorSetModel> colorSetModels = CategoriesHelper.getCategoriesColorSets(itemView.getContext());
            ColorSetModel categoryColorSetModel = colorSetModels.stream()
                    .filter(colorSetModel -> colorSetModel.getColorSet()
                            .equals(categoryColorSet)).findFirst().orElse(null);

            int res = 0;

            if (categoryColorSetModel != null) {
                res = mIsNightMode ? categoryColorSetModel.getColorNightHex() : categoryColorSetModel.getColorDayHex();
            }

            return res;
        }
    }

    public interface OnCategoryActionRequestedListener {

        void onCategoryOpenRequested(Category category);

        void onCategoryDeletionRequested(Category category);
    }
}
