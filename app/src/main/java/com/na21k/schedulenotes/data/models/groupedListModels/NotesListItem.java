package com.na21k.schedulenotes.data.models.groupedListModels;

import com.na21k.schedulenotes.data.database.Notes.Note;

public class NotesListItem extends GroupedListsItem {

    private final Note mNote;

    public NotesListItem(Note note) {
        mNote = note;
    }

    public Note getNote() {
        return mNote;
    }

    @Override
    public int getType() {
        return NON_HEADER_ITEM;
    }
}
