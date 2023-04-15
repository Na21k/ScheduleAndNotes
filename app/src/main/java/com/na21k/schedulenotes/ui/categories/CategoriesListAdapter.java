package com.na21k.schedulenotes.ui.categories;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.databinding.CategoriesListItemBinding;
import com.na21k.schedulenotes.exceptions.CouldNotFindColorSetModelException;
import com.na21k.schedulenotes.helpers.CategoriesHelper;
import com.na21k.schedulenotes.ui.shared.viewHolders.BaseViewHolder;

import java.util.List;

public class CategoriesListAdapter
        extends RecyclerView.Adapter<CategoriesListAdapter.CategoryViewHolder> {

    private final boolean mIsNightMode;
    private final OnCategoryActionRequestedListener mOnCategoryActionRequestedListener;
    private List<Category> mCategories = null;

    public CategoriesListAdapter(boolean isNightMode, OnCategoryActionRequestedListener onCategoryActionRequestedListener) {
        mIsNightMode = isNightMode;
        mOnCategoryActionRequestedListener = onCategoryActionRequestedListener;
        setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CategoriesListItemBinding binding = CategoriesListItemBinding
                .inflate(inflater, parent, false);

        return new CategoryViewHolder(binding);
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

    public class CategoryViewHolder extends BaseViewHolder {

        private final CategoriesListItemBinding mBinding;
        private Category mCategory;

        public CategoryViewHolder(CategoriesListItemBinding binding) {
            super(binding.getRoot(), R.menu.category_long_press_menu, 0);
            mBinding = binding;

            itemView.setOnClickListener(
                    v -> mOnCategoryActionRequestedListener.onCategoryOpenRequested(mCategory));
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

        private void setData(@NonNull Category category) throws CouldNotFindColorSetModelException {
            mCategory = category;
            mBinding.categoryName.setText(category.getTitle());

            int categoryColor = CategoriesHelper.getCategoryColor(
                    itemView.getContext(), category, mIsNightMode);
            mBinding.categoriesListCard.setStrokeColor(categoryColor);

            if (!mIsNightMode) {
                mBinding.categoriesListCard.setCardBackgroundColor(categoryColor);
            }
        }
    }

    public interface OnCategoryActionRequestedListener {

        void onCategoryOpenRequested(Category category);

        void onCategoryDeletionRequested(Category category);
    }
}
