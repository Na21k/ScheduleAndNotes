package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
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

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.na21k.schedulenotes.BuildConfig;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.ScheduleNotesApplication;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.databinding.ActivityWordOrPhraseDetailsBinding;
import com.na21k.schedulenotes.di.modules.FilePathsModule;
import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.AttachedImagesListAdapter;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.OnImageActionRequestedListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.NotDirectoryException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class WordOrPhraseDetailsActivity extends AppCompatActivity
        implements OnImageActionRequestedListener {

    private final AttachedImagesListAdapter mAttachedImagesListAdapter =
            new AttachedImagesListAdapter(this);
    private WordOrPhraseDetailsViewModel.Factory mViewModelFactory;
    private WordOrPhraseDetailsViewModel mViewModel;
    private ActivityWordOrPhraseDetailsBinding mBinding;
    private ActivityResultLauncher<Intent> mOpenImageActivityResultLauncher;
    private String mAttachedImagesAbsoluteDirPath;
    private String mAttachedImagesSelectedForAdditionAbsoluteTmpDirPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((ScheduleNotesApplication) getApplicationContext())
                .getAppComponent()
                .inject(this);
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this, mViewModelFactory)
                .get(WordOrPhraseDetailsViewModel.class);
        mBinding = ActivityWordOrPhraseDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        handleWindowInsets();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        registerForImageSelectionActivityResult();
        setUpAttachedImagesList();

        setTitle(mViewModel.isEditing()
                ? R.string.title_edit_item
                : R.string.title_create_item);

        if (mViewModel.isEditing()) {
            observe();
        } else {
            mBinding.loadingAttachedImagesProgressbar.setVisibility(View.GONE);
        }
    }

    @Inject
    protected void initViewModelFactory(
            WordOrPhraseDetailsViewModel.Factory.AssistedFactory viewModelFactoryAssistedFactory
    ) {
        int itemId = 0;
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            itemId = bundle.getInt(Constants.LANGUAGES_LIST_ITEM_ID_INTENT_KEY);
        }

        mViewModelFactory = viewModelFactoryAssistedFactory.create(itemId);
    }

    @Inject
    protected void initPaths(
            @FilePathsModule.LanguagesListAttachedImagesAbsoluteFolderPath
            String attachedImagesAbsoluteDirPath,
            @FilePathsModule.LanguagesListAttachedImagesSelectedForAdditionTmpFolderPath
            String attachedImagesSelectedForAdditionAbsoluteTmpDirPath
    ) {
        mAttachedImagesAbsoluteDirPath = attachedImagesAbsoluteDirPath;
        mAttachedImagesSelectedForAdditionAbsoluteTmpDirPath
                = attachedImagesSelectedForAdditionAbsoluteTmpDirPath;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.word_or_phrase_details_menu, menu);

        if (!mViewModel.isEditing()) {
            menu.removeItem(R.id.menu_delete);
            menu.removeItem(R.id.menu_archive);
        }

        if (isOpenFromArchive()) {
            menu.removeItem(R.id.menu_archive);
        } else {
            menu.removeItem(R.id.menu_unarchive);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                saveItem();
                break;
            case R.id.menu_archive:
                archiveItem();
                break;
            case R.id.menu_unarchive:
                unarchiveItem();
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

    private void handleWindowInsets() {
        UiHelper.handleWindowInsets(getWindow(), mBinding.getRoot(),
                mBinding.container, mBinding.includedImagesList.imagesList, null,
                true);
    }

    private void setUpAttachedImagesList() {
        RecyclerView recyclerView = mBinding.includedImagesList.imagesList;
        recyclerView.setAdapter(mAttachedImagesListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (mViewModel.trackedAttachedImagesSetExternally()) {
            //the ViewModel already has attached images cached
            //after a configuration change
            mAttachedImagesListAdapter.setData(mViewModel.getTrackedAttachedImages());
            mBinding.loadingAttachedImagesProgressbar.setVisibility(View.GONE);
        }
    }

    private void observe() {
        mViewModel.getItem().observe(this, item -> {
            if (item == null) {
                finish();
                return;
            }

            mBinding.wordOrPhrase.setText(item.getText());
            mBinding.transcription.setText(item.getTranscription());
            mBinding.translation.setText(item.getTranslation());
            mBinding.explanation.setText(item.getExplanation());
            mBinding.usageExample.setText(item.getUsageExampleText());

            //not after a configuration change
            if (!mViewModel.trackedAttachedImagesSetExternally()) {
                loadAttachedImages(item)
                        .addOnSuccessListener(attachedImages -> {
                            mViewModel.setTrackedAttachedImages(attachedImages);
                            mAttachedImagesListAdapter.setData(attachedImages);
                            mBinding.loadingAttachedImagesProgressbar.setVisibility(View.GONE);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(
                                    this,
                                    R.string.unexpected_error,
                                    Toast.LENGTH_LONG
                            ).show();
                            e.printStackTrace();
                        });
            }
        });
    }

    private Task<List<String>> loadAttachedImages(LanguagesListItem languagesListItem) {
        TaskCompletionSource<List<String>> source = new TaskCompletionSource<>();

        new Thread(() -> {
            try {
                List<String> attachedImages = languagesListItem.getAttachedImagesPaths(
                        mAttachedImagesAbsoluteDirPath
                );
                source.setResult(attachedImages);
            } catch (NotDirectoryException e) {
                source.setException(e);
            }
        }).start();

        return source.getTask();
    }

    private void saveItem() {
        Editable wordOrPhraseEditable = mBinding.wordOrPhrase.getText();
        String wordOrPhrase;

        if (wordOrPhraseEditable == null ||
                (wordOrPhrase = wordOrPhraseEditable.toString()).isEmpty()) {
            UiHelper.showSnackbar(mBinding.getRoot(), R.string.specify_word_or_phrase_snackbar);

            return;
        }

        Editable transcriptionEditable = mBinding.transcription.getText();
        Editable translationEditable = mBinding.translation.getText();
        Editable explanationEditable = mBinding.explanation.getText();
        Editable usageExampleEditable = mBinding.usageExample.getText();

        if (transcriptionEditable == null || translationEditable == null ||
                explanationEditable == null || usageExampleEditable == null) {
            UiHelper.showSnackbar(mBinding.getRoot(), R.string.unexpected_error);

            return;
        }

        String transcription = transcriptionEditable.toString();
        String translation = translationEditable.toString();
        String explanation = explanationEditable.toString();
        String usageExample = usageExampleEditable.toString();

        LanguagesListItem item = new LanguagesListItem(
                0, wordOrPhrase, transcription, translation, explanation, usageExample);
        item.setArchived(isOpenFromArchive());

        mViewModel.save(item);
        finish();
    }

    private void archiveItem() {
        mViewModel.isArchiveEmpty().addOnSuccessListener(isEmpty -> {
            if (!isEmpty) {
                mViewModel.setArchived(true);
                finish();

                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_archive_24);
            builder.setTitle(R.string.first_time_archive_explanation_alert_title);
            builder.setMessage(R.string.first_time_archive_explanation_alert_message);

            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                mViewModel.setArchived(true);
                finish();
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            });

            builder.show();
        });
    }

    private void unarchiveItem() {
        mViewModel.setArchived(false);
        finish();
    }

    private void deleteItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_24);
        builder.setTitle(R.string.list_item_deletion_alert_title);
        builder.setMessage(R.string.list_item_deletion_alert_message);

        builder.setPositiveButton(R.string.delete, (dialog, which) -> {
            mViewModel.delete();
            finish();
        });
        builder.setNegativeButton(R.string.keep, (dialog, which) -> {
        });

        builder.show();
    }

    private boolean isOpenFromArchive() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            return bundle.getBoolean(Constants.IS_OPEN_FROM_ARCHIVE_INTENT_KEY);
        } else {
            return false;
        }
    }

    private void openImageFile(File imageFile) {
        Uri uri = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".provider", imageFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public void onImageOpenRequested(File attachedImage) {
        openImageFile(attachedImage);
    }

    @Override
    public void onImageDeletionRequested(File attachedImage) {
        mViewModel.trackDeleteAttachedImage(attachedImage.getPath());
        mAttachedImagesListAdapter.removeItem(attachedImage.getPath());
    }

    @Override
    public void onImageAdditionRequested() {
        if (mViewModel.isEditing() && !mViewModel.trackedAttachedImagesSetExternally()) {
            UiHelper.showSnackbar(mBinding.getRoot(), R.string.loading_attached_images_snackbar);

            return;
        }

        int currentCount = mViewModel.getTrackedAttachedImagesCount();

        if (currentCount >= Constants.ATTACHED_IMAGES_COUNT_LIMIT) {
            String message = getResources().getString(
                    R.string.cant_attach_more_images_snackbar,
                    Constants.ATTACHED_IMAGES_COUNT_LIMIT);

            UiHelper.showSnackbar(mBinding.getRoot(), message);

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
                                File image = copyFromUriToTmpFileBlocking(uri);
                                mViewModel.trackAddAttachedImage(image.getPath());
                                mAttachedImagesListAdapter.addItem(image.getPath());
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

    private File copyFromUriToTmpFileBlocking(Uri uri) throws IOException {
        String tmpDirPath = mAttachedImagesSelectedForAdditionAbsoluteTmpDirPath;
        File tmpDir = new File(tmpDirPath);

        int newAttachedImageTmpFileName = 1;

        if (tmpDir.exists()) {
            int maxName = Arrays.stream(tmpDir.list())
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0);
            newAttachedImageTmpFileName = maxName + 1;
        }

        String newAttachedImageTmpFilePath = tmpDirPath + '/' + newAttachedImageTmpFileName;
        File newAttachedImageTmpFile = new File(newAttachedImageTmpFilePath);

        try (InputStream in = getContentResolver().openInputStream(uri);
             OutputStream out = new FileOutputStream(newAttachedImageTmpFile)) {
            byte[] buffer = new byte[8192]; //8 MiB
            int read;

            while (true) {
                read = in.read(buffer);

                if (read != -1) out.write(buffer, 0, read);
                else break;
            }
        }

        return newAttachedImageTmpFile;
    }
}
