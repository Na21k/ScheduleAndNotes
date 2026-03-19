package com.na21k.schedulenotes.repositories.lists.languages;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.na21k.schedulenotes.data.database.BaseDao;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemDao;
import com.na21k.schedulenotes.di.modules.FilePathsModule;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanProvideRandomRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import javax.inject.Inject;

public class LanguagesListRepositoryImpl extends MutableRepository<LanguagesListItem>
        implements LanguagesListRepository,
        CanSearchRepository<LanguagesListItem>, CanClearRepository<LanguagesListItem>,
        CanProvideRandomRepository<LanguagesListItem> {

    private final LanguagesListItemDao mLanguagesListItemDao = db.languagesListItemDao();
    private final String mLanguagesListAttachedImagesAbsoluteFolderPath;

    @Inject
    public LanguagesListRepositoryImpl(
            @NonNull Context context,
            @FilePathsModule.LanguagesListAttachedImagesAbsoluteFolderPath String languagesListAttachedImagesAbsoluteFolderPath
    ) {
        super(context);
        mLanguagesListAttachedImagesAbsoluteFolderPath = languagesListAttachedImagesAbsoluteFolderPath;
    }

    @Override
    protected BaseDao<LanguagesListItem> getDao() {
        return mLanguagesListItemDao;
    }

    @Override
    public Task<Void> delete(int itemId) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            deleteItemAttachedImagesBlocking(itemId);
            source.setResult(null);
        }).start();

        return Tasks.whenAll(source.getTask(), super.delete(itemId));
    }

    @Override
    public void deleteBlocking(LanguagesListItem item) {
        deleteItemAttachedImagesBlocking(item.getId());
        super.deleteBlocking(item);
    }

    private void deleteItemAttachedImagesBlocking(int itemId) {
        String itemImagesDirPath = String.format(
                Locale.US,
                "%s/%d",
                mLanguagesListAttachedImagesAbsoluteFolderPath,
                itemId
        );

        File itemImagesDir = new File(itemImagesDirPath);

        //TODO: make sure shis throws no exceptions when the folder does not exist
        try (Stream<Path> paths = Files.walk(itemImagesDir.toPath())) {
            paths.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LiveData<List<LanguagesListItem>> getUnarchived() {
        return mLanguagesListItemDao.getUnarchived();
    }

    @Override
    public LiveData<List<LanguagesListItem>> getArchived() {
        return mLanguagesListItemDao.getArchived();
    }

    @Override
    public Task<Boolean> isArchiveEmpty() {
        TaskCompletionSource<Boolean> source = new TaskCompletionSource<>();

        new Thread(() -> {
            boolean isEmpty = mLanguagesListItemDao.isArchiveEmptyBlocking();
            source.setResult(isEmpty);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Void> setArchived(int itemId, boolean archived) {
        TaskCompletionSource<Void> source = new TaskCompletionSource<>();

        new Thread(() -> {
            mLanguagesListItemDao.setArchived(itemId, archived);
            source.setResult(null);
        }).start();

        return source.getTask();
    }

    @Override
    public Task<Map<Integer, Integer>> getItemIdsToAttachedImageCounts() {
        TaskCompletionSource<Map<Integer, Integer>> source = new TaskCompletionSource<>();

        new Thread(() -> {
            Map<Integer, Integer> resMap = new HashMap<>(100);
            File imagesDir = new File(mLanguagesListAttachedImagesAbsoluteFolderPath);

            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(imagesDir.toPath())) {
                dirStream.iterator()
                        .forEachRemaining(itemDirPath -> {
                            File itemDir = itemDirPath.toFile();
                            Integer itemId = Integer.valueOf(itemDir.getName());

                            String[] itemImages = itemDir.list();
                            Integer itemImagesCount = itemImages != null
                                    ? itemImages.length
                                    : 0;

                            resMap.put(itemId, itemImagesCount);
                        });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            source.setResult(resMap);
        }).start();

        return source.getTask();
    }

    @Override
    public LiveData<List<LanguagesListItem>> getSearch(String query) {
        return mLanguagesListItemDao.search(query);
    }

    @Override
    public void clearBlocking() {
        mLanguagesListItemDao.deleteAll();
    }

    @Override
    public LanguagesListItem getRandomBlocking() {
        return mLanguagesListItemDao.getRandomBlocking();
    }
}
