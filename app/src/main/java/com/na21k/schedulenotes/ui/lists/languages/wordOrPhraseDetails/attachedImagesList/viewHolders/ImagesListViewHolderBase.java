package com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.viewHolders;

import android.view.View;

import androidx.annotation.NonNull;

import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.attachedImagesList.OnImageActionRequestedListener;
import com.na21k.schedulenotes.ui.shared.viewHolders.BaseViewHolder;

public abstract class ImagesListViewHolderBase extends BaseViewHolder {

    protected final OnImageActionRequestedListener mActionRequestedListener;

    public ImagesListViewHolderBase(@NonNull View itemView, int contextMenuRes,
                                    int contextMenuHeaderRes,
                                    OnImageActionRequestedListener actionRequestedListener) {
        super(itemView, contextMenuRes, contextMenuHeaderRes);
        mActionRequestedListener = actionRequestedListener;
    }
}
