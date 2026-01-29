package com.na21k.schedulenotes.repositories.lists.languages.attachedImages;

import androidx.lifecycle.LiveData;

import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;

import java.util.List;

public interface LanguagesListAttachedImagesRepository {

    LiveData<List<LanguagesListItemAttachedImage>> getByListItemId(int listItemId);

    List<LanguagesListItemAttachedImage> getByListItemIdBlocking(int listItemId);

    LiveData<List<Integer>> getAllListItemIds();
}
