package com.na21k.schedulenotes.repositories.lists.languages;

import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItem;

import java.util.List;
import java.util.Map;

public interface LanguagesListRepository {

    LiveData<List<LanguagesListItem>> getUnarchived();

    LiveData<List<LanguagesListItem>> getArchived();

    Task<Boolean> isArchiveEmpty();

    Task<Void> setArchived(int itemId, boolean archived);

    Task<Void> addAttachedImage(int itemId, String absoluteSrcFilePath);

    Task<Void> deleteAttachedImage(String absoluteFilePath);

    Task<Map<Integer, Integer>> getItemIdsToAttachedImageCounts();
}
