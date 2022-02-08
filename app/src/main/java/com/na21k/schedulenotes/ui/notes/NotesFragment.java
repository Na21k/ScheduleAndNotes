package com.na21k.schedulenotes.ui.notes;

import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.UiHelper;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.databinding.NotesFragmentBinding;
import com.na21k.schedulenotes.ui.notes.noteDetails.NoteDetailsActivity;

import java.util.Comparator;
import java.util.List;

public class NotesFragment extends Fragment
        implements NotesListAdapter.OnChooseNoteCategoryListener {

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
                new NotesListAdapter(mViewModel, UiHelper.isInDarkMode(this),
                        this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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

        if (isEnoughData) {
            adapter.setData(notesCache, categoriesCache);
        }
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

                    if (v.canScrollVertically(1)) {
                        mBinding.addNoteFab.show();
                    } else {
                        mBinding.addNoteFab.hide();
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

            mViewModel.getAllCategories().observe(this, categories -> {
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
}
