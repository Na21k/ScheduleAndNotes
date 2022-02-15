package com.na21k.schedulenotes.ui.notes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.CategoriesHelper;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.data.models.groupedListModels.GroupedListItemViewHolderBase;
import com.na21k.schedulenotes.data.models.groupedListModels.GroupedListsItem;
import com.na21k.schedulenotes.data.models.groupedListModels.HeaderListItem;
import com.na21k.schedulenotes.data.models.groupedListModels.HeaderViewHolder;
import com.na21k.schedulenotes.data.models.groupedListModels.NotesListItem;
import com.na21k.schedulenotes.databinding.GroupedListHeaderItemBinding;
import com.na21k.schedulenotes.databinding.NotesListItemBinding;
import com.na21k.schedulenotes.exceptions.CouldNotFindColorSetModelException;
import com.na21k.schedulenotes.ui.notes.noteDetails.NoteDetailsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class NotesListAdapter extends RecyclerView.Adapter<GroupedListItemViewHolderBase> {

    private final boolean mIsNightMode;
    private final OnNoteActionRequestedListener mOnNoteActionRequestedListener;
    private List<Category> mCategories = null;
    private List<GroupedListsItem> mNotesAndHeaders;

    public NotesListAdapter(boolean isNightMode,
                            OnNoteActionRequestedListener onNoteActionRequestedListener) {
        mIsNightMode = isNightMode;
        mOnNoteActionRequestedListener = onNoteActionRequestedListener;
    }

    @NonNull
    @Override
    public GroupedListItemViewHolderBase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == GroupedListsItem.HEADER_ITEM) {
            GroupedListHeaderItemBinding binding = GroupedListHeaderItemBinding
                    .inflate(inflater, parent, false);

            return new HeaderViewHolder(binding.getRoot(), binding);
        } else {
            NotesListItemBinding binding = NotesListItemBinding
                    .inflate(inflater, parent, false);

            return new NoteViewHolder(binding.getRoot(), binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GroupedListItemViewHolderBase holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == GroupedListsItem.HEADER_ITEM) {
            HeaderListItem headerItem = (HeaderListItem) mNotesAndHeaders.get(position);
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;

            viewHolder.setHeaderText(headerItem.getHeaderText());
        } else {
            NotesListItem noteItem = (NotesListItem) mNotesAndHeaders.get(position);
            NoteViewHolder viewHolder = (NoteViewHolder) holder;

            try {
                viewHolder.setData(noteItem.getNote());
            } catch (CouldNotFindColorSetModelException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mNotesAndHeaders.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mNotesAndHeaders != null ? mNotesAndHeaders.size() : 0;
    }

    public void setData(TreeMap<Category, List<Note>> groupedNotes) {
        Set<Category> groups = groupedNotes.keySet();
        mCategories = new ArrayList<>(groups);

        List<GroupedListsItem> newNotesAndHeaders = new ArrayList<>();

        for (Category categoryGroup : groups) {
            List<Note> groupNotes = groupedNotes.get(categoryGroup);

            if (groupNotes == null) {
                continue;
            }

            HeaderListItem header = new HeaderListItem(categoryGroup.getTitle());
            newNotesAndHeaders.add(header);

            for (Note note : groupNotes) {
                NotesListItem noteItem = new NotesListItem(note);
                newNotesAndHeaders.add(noteItem);
            }
        }

        mNotesAndHeaders = newNotesAndHeaders;
        notifyDataSetChanged();
    }

    public class NoteViewHolder extends GroupedListItemViewHolderBase
            implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private final NotesListItemBinding mBinding;
        private Note mNote;

        public NoteViewHolder(@NonNull View itemView, NotesListItemBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater inflater = new MenuInflater(v.getContext());
            inflater.inflate(R.menu.note_long_press_menu, menu);
            menu.setHeaderTitle(R.string.note_context_menu_title);

            if (mNote.getCategoryId() == null) {
                menu.removeItem(R.id.note_remove_category_menu_item);
            }

            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                item.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.note_delete_menu_item:
                    mOnNoteActionRequestedListener.onNoteDeletionRequested(mNote);
                    return true;
                case R.id.note_set_category_menu_item:
                    mOnNoteActionRequestedListener.onCategorySelectionRequested(mNote);
                    return true;
                case R.id.note_remove_category_menu_item:
                    mOnNoteActionRequestedListener.onRemoveCategoryRequested(mNote);
                    return true;
                default:
                    return false;
            }
        }

        private void setData(@NonNull Note note) throws CouldNotFindColorSetModelException {
            mNote = note;
            mBinding.noteTitle.setText(note.getTitle());
            mBinding.noteDetails.setText(note.getDetails());

            if (mBinding.noteDetails.getText().toString().isEmpty()) {
                mBinding.noteDetails.setVisibility(View.GONE);
            } else {
                mBinding.noteDetails.setVisibility(View.VISIBLE);
            }

            int backColor = CategoriesHelper
                    .getNoteCategoryColor(itemView.getContext(), note, mCategories, mIsNightMode);

            mBinding.notesListCard.setCardBackgroundColor(backColor);

            itemView.setOnClickListener(v -> {
                Context context = v.getContext();

                if (context != null) {
                    Intent intent = new Intent(context, NoteDetailsActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.NOTE_ID_INTENT_KEY, note.getId());
                    intent.putExtras(bundle);

                    context.startActivity(intent);
                }
            });

            itemView.setOnCreateContextMenuListener(this);
        }
    }

    public interface OnNoteActionRequestedListener {

        void onCategorySelectionRequested(Note note);

        void onNoteDeletionRequested(Note note);

        void onRemoveCategoryRequested(Note note);
    }
}
