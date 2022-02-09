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
import com.na21k.schedulenotes.databinding.NotesListItemBinding;
import com.na21k.schedulenotes.exceptions.CouldNotFindColorSetModelException;
import com.na21k.schedulenotes.ui.notes.noteDetails.NoteDetailsActivity;

import java.util.List;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NoteViewHolder> {

    private final boolean mIsNightMode;
    private final OnNoteActionRequestedListener mOnNoteActionRequestedListener;
    private List<Note> mNotes = null;
    private List<Category> mCategories = null;

    public NotesListAdapter(boolean isNightMode,
                            OnNoteActionRequestedListener onNoteActionRequestedListener) {
        mIsNightMode = isNightMode;
        mOnNoteActionRequestedListener = onNoteActionRequestedListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        NotesListItemBinding binding = NotesListItemBinding
                .inflate(inflater, parent, false);

        return new NoteViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = mNotes.get(position);

        try {
            holder.setData(note);
        } catch (CouldNotFindColorSetModelException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mNotes != null ? mNotes.size() : 0;
    }

    public void setData(List<Note> notes, List<Category> categories) {
        mNotes = notes;
        mCategories = categories;
        notifyDataSetChanged();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder
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
