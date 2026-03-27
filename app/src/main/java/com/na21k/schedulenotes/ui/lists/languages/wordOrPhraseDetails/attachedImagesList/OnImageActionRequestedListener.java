package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList;

import java.io.File;

public interface OnImageActionRequestedListener {

    void onImageOpenRequested(File attachedImage);

    void onImageDeletionRequested(File attachedImage);

    void onImageAdditionRequested();
}
