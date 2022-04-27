package com.na21k.schedulenotes.ui.categories.categoryDetails;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.models.ColorSet;
import com.na21k.schedulenotes.data.models.ColorSetModel;
import com.na21k.schedulenotes.databinding.ActivityCategoryDetailsBinding;
import com.na21k.schedulenotes.databinding.ColorPickerItemBinding;
import com.na21k.schedulenotes.helpers.CategoriesHelper;

import java.util.List;

public class CategoryDetailsActivity extends AppCompatActivity
        implements Observer<Category>, View.OnClickListener {

    private CategoryDetailsViewModel mViewModel;
    private ActivityCategoryDetailsBinding mBinding;
    private ColorSet mSelectedColorSet;
    private ColorSetModel mSelectedColorSetModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(CategoryDetailsViewModel.class);
        mBinding = ActivityCategoryDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (isEditing()) {
            setTitle(R.string.title_edit_category);

            Bundle bundle = getIntent().getExtras();
            int categoryId = bundle.getInt(Constants.CATEGORY_ID_INTENT_KEY);
            loadCategoryFromDb(categoryId);
        } else {
            setTitle(R.string.title_create_category);
            mSelectedColorSet = ColorSet.GRAY;
            rerenderColorPickerItems();
        }
    }

    @Override
    public void onClick(View v) {
        ColorSetModel model = (ColorSetModel) v.getTag();
        mSelectedColorSet = model.getColorSet();
        setSelectedColorPickerItem(v);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_details_menu, menu);

        if (!isEditing()) {
            menu.removeItem(R.id.menu_delete);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveCategory();
                break;
            case R.id.menu_cancel:
                finish();
                break;
            case R.id.menu_delete:
                deleteCategory();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onChanged(Category category) {
        mBinding.categoryNameInput.setText(category.getTitle());
        mSelectedColorSet = category.getColorSet();
        rerenderColorPickerItems();
    }

    private void rerenderColorPickerItems() {
        List<ColorSetModel> models = CategoriesHelper.getCategoriesColorSets(this);
        LinearLayout variantsLayout = mBinding.colorVariants;
        variantsLayout.removeAllViews();

        for (ColorSetModel model : models) {
            ColorPickerItemBinding itemBinding = ColorPickerItemBinding.inflate(getLayoutInflater());
            itemBinding.colorPickerItemLightColorCard.setCardBackgroundColor(model.getColorDayHex());
            itemBinding.colorPickerItemDarkColorCard.setCardBackgroundColor(model.getColorNightHex());

            if (model.getColorSet() != mSelectedColorSet) {
                itemBinding.colorPickerItemImageWhenSelected.setVisibility(View.INVISIBLE);
            } else {
                mSelectedColorSetModel = model;
            }

            View view = itemBinding.getRoot();
            view.setTag(model);

            view.setOnClickListener(this);

            variantsLayout.addView(view);
        }
    }

    private void setSelectedColorPickerItem(View newSelectionView) {
        View selectedOptionView = mBinding.colorVariants.findViewWithTag(mSelectedColorSetModel);
        ColorPickerItemBinding selectedOptionBinding = ColorPickerItemBinding.bind(selectedOptionView);
        selectedOptionBinding.colorPickerItemImageWhenSelected.setVisibility(View.INVISIBLE);

        ColorPickerItemBinding newSelectionBinding = ColorPickerItemBinding.bind(newSelectionView);
        newSelectionBinding.colorPickerItemImageWhenSelected.setVisibility(View.VISIBLE);

        ColorSetModel newSelectionModel = (ColorSetModel) newSelectionView.getTag();
        mSelectedColorSet = newSelectionModel.getColorSet();
        mSelectedColorSetModel = newSelectionModel;
    }

    private boolean isEditing() {
        Bundle bundle = getIntent().getExtras();
        return bundle != null;
    }

    private void loadCategoryFromDb(int categoryId) {
        mViewModel.getCategory(categoryId).observe(this, this);
    }

    private void saveCategory() {
        Editable editable = mBinding.categoryNameInput.getText();

        if (editable != null && !editable.toString().isEmpty()) {
            Category category = new Category(0, editable.toString(), mSelectedColorSet);

            if (isEditing()) {
                mViewModel.updateCurrentCategory(category);
            } else {
                mViewModel.createCategory(category);
            }

            finish();
        } else {
            Snackbar.make(mBinding.getRoot(), R.string.specify_category_name_snackbar, 3000)
                    .show();
        }
    }

    private void deleteCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.category_deletion_alert_title);
        builder.setMessage(R.string.category_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            LiveData<Category> category = mViewModel.getCurrentCategory();

            if (category != null) {
                category.removeObserver(this);
                mViewModel.deleteCurrentCategory();
                finish();
            }
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }
}
