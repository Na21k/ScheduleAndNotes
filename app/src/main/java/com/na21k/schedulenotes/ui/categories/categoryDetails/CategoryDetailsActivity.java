package com.na21k.schedulenotes.ui.categories.categoryDetails;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.models.ColorSet;
import com.na21k.schedulenotes.data.models.ColorSetModel;
import com.na21k.schedulenotes.databinding.ActivityCategoryDetailsBinding;
import com.na21k.schedulenotes.helpers.CategoriesHelper;
import com.na21k.schedulenotes.helpers.UiHelper;

import java.util.ArrayList;
import java.util.List;

public class CategoryDetailsActivity extends AppCompatActivity implements Observer<Category> {

    private CategoryDetailsViewModel mViewModel;
    private ActivityCategoryDetailsBinding mBinding;
    private int mMostRecentBottomInset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(CategoryDetailsViewModel.class);
        mBinding = ActivityCategoryDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        makeNavBarLookNice();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        renderColorPickerItems();

        if (isEditing()) {
            setTitle(R.string.title_edit_category);

            Bundle bundle = getIntent().getExtras();
            int categoryId = bundle.getInt(Constants.CATEGORY_ID_INTENT_KEY);
            loadCategoryFromDb(categoryId);
        } else {
            setTitle(R.string.title_create_category);
            mBinding.colorSetPicker.setSelectedModel(mViewModel.getDefaultColorSetModel());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
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

    private void makeNavBarLookNice() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.getRoot(), (v, insets) -> {
            Insets i = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());

            mBinding.container.setPadding(i.left, i.top, i.right, 0);
            mBinding.bodyLinearLayout.setPadding(0, 0, 0, i.bottom);

            mMostRecentBottomInset = i.bottom;

            return WindowInsetsCompat.CONSUMED;
        });
    }

    @Override
    public void onChanged(Category category) {
        mBinding.categoryNameInput.setText(category.getTitle());

        List<ColorSetModel> models = mViewModel.getColorSetModels();
        ColorSetModel model = CategoriesHelper
                .getColorSetModelByColorSet(models, category.getColorSet());

        mBinding.colorSetPicker.setSelectedModel(model);
    }

    private void renderColorPickerItems() {
        mBinding.colorSetPicker.setModels(new ArrayList<>(mViewModel.getColorSetModels()),
                mViewModel.getDefaultColorSetModel());
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

        if (editable == null || editable.toString().isEmpty()) {
            UiHelper.showSnackbar(this, mBinding.getRoot(),
                    R.string.specify_category_name_snackbar, mMostRecentBottomInset);

            return;
        }

        ColorSetModel selectedColorSetModel = mBinding.colorSetPicker.getSelectedModel();

        if (selectedColorSetModel == null) {
            throw new IllegalStateException("The selected " + ColorSetModel.class + " is not set");
        }

        ColorSet selectedColorSet = selectedColorSetModel.getColorSet();
        Category category = new Category(0, editable.toString(), selectedColorSet);

        if (isEditing()) {
            mViewModel.updateCurrentCategory(category);
        } else {
            mViewModel.createCategory(category);
        }

        finish();
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
