package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.BuildConfig;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;
import com.na21k.schedulenotes.databinding.ActivityWordOrPhraseDetailsBinding;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.AttachedImagesListAdapter;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.OnImageActionRequestedListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WordOrPhraseDetailsActivity extends AppCompatActivity
        implements OnImageActionRequestedListener {

    private final AttachedImagesListAdapter mAttachedImagesListAdapter =
            new AttachedImagesListAdapter(this);
    private WordOrPhraseDetailsViewModel mViewModel;
    private ActivityWordOrPhraseDetailsBinding mBinding;
    private LanguagesListItem mItem;
    private ActivityResultLauncher<Intent> mOpenImageActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(WordOrPhraseDetailsViewModel.class);
        mBinding = ActivityWordOrPhraseDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        registerForImageSelectionActivityResult();
        setUpAttachedImagesList();

        if (isEditing()) {
            setTitle(R.string.title_edit_item);

            Bundle bundle = getIntent().getExtras();
            int itemId = bundle.getInt(Constants.LANGUAGES_LIST_ITEM_ID_INTENT_KEY);
            loadItemFromDb(itemId);
        } else {
            setTitle(R.string.title_create_item);
            mBinding.loadingAttachedImagesProgressbar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.word_or_phrase_details_menu, menu);

        if (!isEditing()) {
            menu.removeItem(R.id.menu_delete);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveItem();
                break;
            case R.id.menu_cancel:
                finish();
                break;
            case R.id.menu_delete:
                deleteItem();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpAttachedImagesList() {
        RecyclerView recyclerView = mBinding.includedImagesList.imagesList;
        recyclerView.setAdapter(mAttachedImagesListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadItemFromDb(int itemId) {
        mViewModel.getById(itemId).observe(this, item -> {
            if (item != null) {
                mItem = item;
                mBinding.wordOrPhrase.setText(item.getText());
                mBinding.translation.setText(item.getTranslation());
                mBinding.explanation.setText(item.getExplanation());
                mBinding.usageExample.setText(item.getUsageExampleText());
            }
        });

        mViewModel.getAttachedImagesByItemId(itemId).observe(this, attachedImages -> {
            mViewModel.setAttachedImages(attachedImages);
            mAttachedImagesListAdapter.setData(attachedImages);
            mBinding.loadingAttachedImagesProgressbar.setVisibility(View.GONE);
        });

        if (mViewModel.getAttachedImagesCount() > 0) {
            mAttachedImagesListAdapter.setData(mViewModel.getAttachedImages());
            mBinding.loadingAttachedImagesProgressbar.setVisibility(View.GONE);
        }
    }

    private void saveItem() {
        String wordOrPhrase = mBinding.wordOrPhrase.getText().toString();

        if (wordOrPhrase.isEmpty()) {
            Snackbar.make(mBinding.getRoot(), R.string.specify_word_or_phrase_snackbar, 3000)
                    .show();
            return;
        }

        String translation = mBinding.translation.getText().toString();
        String explanation = mBinding.explanation.getText().toString();
        String usageExample = mBinding.usageExample.getText().toString();

        if (isEditing() && mItem != null) {
            mItem.setText(wordOrPhrase);
            mItem.setTranslation(translation);
            mItem.setExplanation(explanation);
            mItem.setUsageExampleText(usageExample);

            mViewModel.update(mItem);
        } else {
            mViewModel.addNew(new LanguagesListItem(0, wordOrPhrase,
                    translation, explanation, usageExample));
        }

        finish();
    }

    private void deleteItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.list_item_deletion_alert_title);
        builder.setMessage(R.string.list_item_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            mViewModel.delete(mItem);
            finish();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }

    private boolean isEditing() {
        Bundle bundle = getIntent().getExtras();
        return bundle != null;
    }

    private void openImageFile(File imageFile) {
        Uri uri = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".provider", imageFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/jpeg");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public void onImageOpenRequested(LanguagesListItemAttachedImage attachedImage) {
        Bitmap bitmap = attachedImage.getBitmapData();
        File cache = getExternalCacheDir();
        File file = new File(cache, Constants.OPEN_IMAGE_TMP_FILE_NAME);
        FileOutputStream out;

        try {
            file.delete();
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            Snackbar.make(mBinding.getRoot(), R.string.unexpected_error, 3000).show();
            return;
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();

            Snackbar.make(mBinding.getRoot(), R.string.unexpected_error, 3000).show();
            return;
        }

        openImageFile(file);
    }

    @Override
    public void onImageDeletionRequested(LanguagesListItemAttachedImage attachedImage) {
        mViewModel.deleteAttachedImage(attachedImage);
        mAttachedImagesListAdapter.removeItem(attachedImage);
    }

    @Override
    public void onImageAdditionRequested() {
        if (mViewModel.isLoadingAttachedImages() && isEditing()) {
            Snackbar.make(mBinding.getRoot(),
                    R.string.loading_attached_images_snackbar, 3000).show();

            return;
        }

        int currentCount = mViewModel.getAttachedImagesCount();

        if (currentCount >= Constants.ATTACHED_IMAGES_COUNT_LIMIT) {
            String message = getResources().getString(
                    R.string.cant_attach_more_images_snackbar,
                    Constants.ATTACHED_IMAGES_COUNT_LIMIT);

            Snackbar.make(mBinding.getRoot(), message, 3000).show();

            return;
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        String[] mimeTypes = {"image/png", "image/jpeg"};
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        mOpenImageActivityResultLauncher.launch(intent);
    }

    private void registerForImageSelectionActivityResult() {
        mOpenImageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(getApplicationContext(),
                                R.string.importing_image_toast, Toast.LENGTH_SHORT).show();

                        Intent data = result.getData();

                        if (data != null) {
                            Uri uri = data.getData();
                            try {
                                Bitmap bitmap = imageUriToBitmap(uri);
                                LanguagesListItemAttachedImage image =
                                        new LanguagesListItemAttachedImage(0, bitmap);
                                mViewModel.addAttachedImage(image);
                                mAttachedImagesListAdapter.addItem(image);
                            } catch (IOException e) {
                                Toast.makeText(getApplicationContext(),
                                        R.string.importing_image_failed_toast,
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(this, R.string.unexpected_error,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private Bitmap imageUriToBitmap(Uri uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
    }
}
