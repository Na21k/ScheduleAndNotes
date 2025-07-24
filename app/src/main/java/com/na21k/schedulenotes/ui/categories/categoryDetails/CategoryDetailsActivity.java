package com.na21k.schedulenotes.ui.categories.categoryDetails;

import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.ScheduleNotesApplication;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.models.ColorSet;
import com.na21k.schedulenotes.data.models.ColorSetModel;
import com.na21k.schedulenotes.databinding.ActivityCategoryDetailsBinding;
import com.na21k.schedulenotes.helpers.CategoriesHelper;
import com.na21k.schedulenotes.helpers.UiHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CategoryDetailsActivity extends AppCompatActivity implements Observer<Category> {

    private CategoryDetailsViewModel.Factory mViewModelFactory;
    private CategoryDetailsViewModel mViewModel;
    private ActivityCategoryDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((ScheduleNotesApplication) getApplicationContext())
                .getAppComponent()
                .inject(this);
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, mViewModelFactory)
                .get(CategoryDetailsViewModel.class);
        mBinding = ActivityCategoryDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        handleWindowInsets();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(mViewModel.isEditing()
                ? R.string.title_edit_category
                : R.string.title_create_category);

        renderColorPickerItems();
        observeCategory();
    }

    @Inject
    protected void initViewModelFactory(
            CategoryDetailsViewModel.Factory.AssistedFactory viewModelFactoryAssistedFactory
    ) {
        int categoryId = 0;
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            categoryId = bundle.getInt(Constants.CATEGORY_ID_INTENT_KEY);
        }

        mViewModelFactory = viewModelFactoryAssistedFactory.create(categoryId);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.category_details_menu, menu);

        if (!mViewModel.isEditing()) {
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

    private void handleWindowInsets() {
        UiHelper.handleWindowInsets(getWindow(), mBinding.getRoot(),
                mBinding.container, mBinding.bodyLinearLayout, null, true);
    }

    @Override
    public void onChanged(Category category) {
        if (category == null) {
            //ignore when not editing or during deletion
            return;
        }

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

    private void observeCategory() {
        mViewModel.getCategory().observe(this, this);
    }

    private void saveCategory() {
        Editable editable = mBinding.categoryNameInput.getText();

        if (editable == null || editable.toString().isEmpty()) {
            UiHelper.showSnackbar(mBinding.getRoot(), R.string.specify_category_name_snackbar);

            return;
        }

        ColorSetModel selectedColorSetModel = mBinding.colorSetPicker.getSelectedModel();

        if (selectedColorSetModel == null) {
            throw new IllegalStateException("The selected " + ColorSetModel.class + " is not set");
        }

        ColorSet selectedColorSet = selectedColorSetModel.getColorSet();
        Category category = new Category(0, editable.toString(), selectedColorSet);

        mViewModel.saveCategory(category);
        finish();
    }

    private void deleteCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.category_deletion_alert_title);
        builder.setMessage(R.string.category_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            mViewModel.deleteCategory();
            finish();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }
}
