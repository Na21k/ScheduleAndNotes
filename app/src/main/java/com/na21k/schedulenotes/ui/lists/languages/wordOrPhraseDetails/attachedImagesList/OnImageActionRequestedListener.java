package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList;

import com.na21k.schedulenotes.data.database.Lists.Languages.LanguagesListItemAttachedImage;

public interface OnImageActionRequestedListener {

    void onImageOpenRequested(LanguagesListItemAttachedImage attachedImage);

    void onImageDeletionRequested(LanguagesListItemAttachedImage attachedImage);

    void onImageAdditionRequested();
}
