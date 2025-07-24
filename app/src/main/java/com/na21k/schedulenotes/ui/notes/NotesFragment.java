package com.na21k.schedulenotes.ui.notes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.na21k.schedulenotes.Constants;
import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.ScheduleNotesApplication;
import com.na21k.schedulenotes.data.database.Categories.Category;
import com.na21k.schedulenotes.data.database.Notes.Note;
import com.na21k.schedulenotes.data.models.ColorSet;
import com.na21k.schedulenotes.data.models.groupedListModels.GroupedListsItem;
import com.na21k.schedulenotes.databinding.NotesFragmentBinding;
import com.na21k.schedulenotes.helpers.UiHelper;
import com.na21k.schedulenotes.ui.categories.categoryDetails.CategoryDetailsActivity;
import com.na21k.schedulenotes.ui.notes.noteDetails.NoteDetailsActivity;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class NotesFragment extends Fragment
        implements NotesListAdapter.OnNoteActionRequestedListener, MenuProvider {

    private static final int mLandscapeColumnCount = 2;
    private static final int mPortraitColumnCountTablet = 2;
    private static final int mLandscapeColumnCountTablet = 3;
    private final Observer<List<Note>> mNotesObserver = new Observer<List<Note>>() {
        @Override
        public void onChanged(List<Note> notes) {
            notes.sort(Comparator.comparing(Note::getTitle));
            mViewModel.setNotesCache(notes);
            updateListIfEnoughData();
        }
    };
    @Inject
    protected NotesViewModel.Factory mViewModelFactory;
    private NotesViewModel mViewModel;
    private NotesFragmentBinding mBinding;
    private NotesListAdapter mListAdapter;
    private LiveData<List<Note>> mLastSearchLiveData;
    private boolean isSearchMode = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((ScheduleNotesApplication) context.getApplicationContext())
                .getAppComponent()
                .inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this, mViewModelFactory).get(NotesViewModel.class);
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
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.CREATED);

        mListAdapter = setUpRecyclerView();
        setObservers();
        setListeners();
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.notes_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                isSearchMode = true;
                mBinding.addNoteFab.hide();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                isSearchMode = false;
                mBinding.addNoteFab.show();
                return true;
            }
        });

        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                replaceNotesObserverAccordingToSearchQuery(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    private NotesListAdapter setUpRecyclerView() {
        RecyclerView recyclerView = mBinding.includedList.notesList;
        NotesListAdapter adapter =
                new NotesListAdapter(UiHelper.isInDarkMode(this), this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = UiHelper.getRecyclerViewLayoutManager(requireContext(),
                mLandscapeColumnCountTablet, mPortraitColumnCountTablet, mLandscapeColumnCount,
                GroupedListsItem.HEADER_ITEM, adapter);
        recyclerView.setLayoutManager(layoutManager);

        return adapter;
    }

    private void setObservers() {
        mViewModel.getAllNotes().observe(getViewLifecycleOwner(), mNotesObserver);

        mViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            mViewModel.setCategoriesCache(categories);
            updateListIfEnoughData();
        });
    }

    private void replaceNotesObserverAccordingToSearchQuery(String query) {
        mViewModel.getAllNotes().removeObservers(getViewLifecycleOwner());

        if (mLastSearchLiveData != null) {
            mLastSearchLiveData.removeObservers(getViewLifecycleOwner());
        }

        mLastSearchLiveData = mViewModel.getNotesSearch(query);
        mLastSearchLiveData.observe(getViewLifecycleOwner(), mNotesObserver);
    }

    private void updateListIfEnoughData() {
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

        mListAdapter.setData(groupedNotes);
    }

    private void setListeners() {
        mBinding.addNoteFab.setOnClickListener(v -> newNote());

        mBinding.includedList.notesList.setOnScrollChangeListener(
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (isSearchMode) {
                        return;
                    }

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
    public void onNoteOpenRequested(Note note) {
        Context context = getContext();

        if (context != null) {
            Intent intent = new Intent(context, NoteDetailsActivity.class);

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.NOTE_ID_INTENT_KEY, note.getId());
            intent.putExtras(bundle);

            context.startActivity(intent);
        }
    }

    @Override
    public void onCategorySelectionRequested(Note note) {
        Context context = getContext();

        if (context != null) {
            List<Category> categoriesCache = mViewModel.getCategoriesCache();
            categoriesCache.sort(Comparator.comparing(Category::getTitle));

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_categories_24);
            builder.setTitle(R.string.pick_category_dialog_title);
            builder.setNegativeButton(R.string.cancel, null);
            builder.setPositiveButton(R.string.create_category_dialog_button, (dialog, which) -> {
                Intent intent = new Intent(context, CategoryDetailsActivity.class);
                startActivity(intent);
            });

            ArrayAdapter<Category> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_list_item_1, categoriesCache);
            builder.setAdapter(adapter, (dialog, which) -> {
                int selectedCategoryId = categoriesCache.get(which).getId();
                note.setCategoryId(selectedCategoryId);
                mViewModel.updateNote(note);
            });

            builder.show();
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
                Snackbar.make(mBinding.getRoot(), R.string.note_deleted_snackbar,
                                Constants.UNDO_DELETE_TIMEOUT_MILLIS)
                        .setAction(R.string.undo, v -> mViewModel.createNote(note)).show();
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
