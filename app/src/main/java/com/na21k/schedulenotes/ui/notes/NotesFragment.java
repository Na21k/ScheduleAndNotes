package com.na21k.schedulenotes.ui.notes;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.data.models.ColorSet;
import com.na21k.schedulenotes.data.models.groupedListModels.GroupedListsItem;
import com.na21k.schedulenotes.databinding.NotesFragmentBinding;
import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.ui.notes.noteDetails.NoteDetailsActivity;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class NotesFragment extends Fragment
        implements NotesListAdapter.OnNoteActionRequestedListener {

    private static final int mLandscapeColumnCount = 2;
    private static final int mPortraitColumnCountTablet = 2;
    private static final int mLandscapeColumnCountTablet = 3;
    private NotesViewModel mViewModel;
    private NotesFragmentBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NotesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = NotesFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NotesListAdapter adapter = setUpRecyclerView();
        setObservers(adapter);
        setListeners();
    }

    private NotesListAdapter setUpRecyclerView() {
        RecyclerView recyclerView = mBinding.includedList.notesList;
        NotesListAdapter adapter =
                new NotesListAdapter(UiHelper.isInDarkMode(this), this);
        recyclerView.setAdapter(adapter);

        int orientation = getResources().getConfiguration().orientation;
        boolean isTablet = UiHelper.isTablet(getResources());

        if (!isTablet && orientation != Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            return adapter;
        }

        int columnCount;

        if (isTablet) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                columnCount = mLandscapeColumnCountTablet;
            } else {
                columnCount = mPortraitColumnCountTablet;
            }
        } else {
            columnCount = mLandscapeColumnCount;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), columnCount);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = adapter.getItemViewType(position);

                if (viewType == GroupedListsItem.HEADER_ITEM) {
                    return columnCount;
                } else {
                    return 1;
                }
            }
        });

        recyclerView.setLayoutManager(layoutManager);

        return adapter;
    }

    private void setObservers(NotesListAdapter adapter) {
        mViewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
            notes.sort(Comparator.comparing(Note::getTitle));
            mViewModel.setNotesCache(notes);
            updateListIfEnoughData(adapter);
        });

        mViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            mViewModel.setCategoriesCache(categories);
            updateListIfEnoughData(adapter);
        });
    }

    private void updateListIfEnoughData(NotesListAdapter adapter) {
        List<Note> notesCache = mViewModel.getNotesCache();
        List<Category> categoriesCache = mViewModel.getCategoriesCache();
        boolean isEnoughData = notesCache != null && categoriesCache != null;

        if (!isEnoughData) {
            return;
        }

        TreeMap<Category, List<Note>> groupedNotes =
                new TreeMap<>(Comparator.comparing(Category::getTitle));

        for (Category category : categoriesCache) {
            List<Note> categoryNotes = notesCache.stream()
                    .filter(note -> note.getCategoryId() != null &&
                            note.getCategoryId().equals(category.getId()))
                    .sorted(Comparator.comparing(Note::getTitle))
                    .collect(Collectors.toList());

            if (!categoryNotes.isEmpty()) {
                groupedNotes.put(category, categoryNotes);
            }
        }

        List<Note> uncategorizedNotes = notesCache.stream()
                .filter(note -> note.getCategoryId() == null).collect(Collectors.toList());

        if (!uncategorizedNotes.isEmpty()) {
            String uncategorizedPlaceholder = getResources()
                    .getString(R.string.uncategorized_notes_category_placeholder);
            Category noCategory = new Category(0, uncategorizedPlaceholder, ColorSet.GRAY);

            groupedNotes.put(noCategory, uncategorizedNotes);
        }

        adapter.setData(groupedNotes);
    }

    private void setListeners() {
        mBinding.addNoteFab.setOnClickListener(v -> newNote());

        mBinding.includedList.notesList.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY <= oldScrollY) {
                        mBinding.addNoteFab.extend();
                    } else {
                        mBinding.addNoteFab.shrink();
                    }

                    if (!v.canScrollVertically(1) && v.canScrollVertically(-1)) {
                        mBinding.addNoteFab.hide();
                    } else {
                        mBinding.addNoteFab.show();
                    }
                });
    }

    private void newNote() {
        Context context = getContext();

        if (context != null) {
            Intent intent = new Intent(context, NoteDetailsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCategorySelectionRequested(Note note) {
        Context context = getContext();

        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_categories_24);
            builder.setTitle(R.string.pick_category_dialog_title);

            mViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
                categories.sort(Comparator.comparing(Category::getTitle));

                ArrayAdapter<Category> adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_list_item_1, categories);
                builder.setAdapter(adapter, (dialog, which) -> {
                    int selectedCategoryId = categories.get(which).getId();
                    note.setCategoryId(selectedCategoryId);
                    mViewModel.updateNote(note);
                });

                builder.show();
            });
        }
    }

    @Override
    public void onNoteDeletionRequested(Note note) {
        Context context = getContext();

        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_delete_24);
            builder.setTitle(R.string.note_deletion_alert_title);
            builder.setMessage(R.string.note_deletion_alert_message);

            builder.setPositiveButton(R.string.delete, (dialog, which) -> {
                mViewModel.deleteNote(note);
                Snackbar.make(mBinding.getRoot(), R.string.note_deleted_snackbar, 3000)
                        .show();
            });
            builder.setNegativeButton(R.string.keep, (dialog, which) -> {
            });

            builder.show();
        }
    }

    @Override
    public void onRemoveCategoryRequested(Note note) {
        note.setCategoryId(null);
        mViewModel.updateNote(note);
    }
}
