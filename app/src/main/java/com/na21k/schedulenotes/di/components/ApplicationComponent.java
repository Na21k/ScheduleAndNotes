package com.na21k.schedulenotes.di.components;

import android.app.Application;

import com.na21k.schedulenotes.di.modules.AppModule;
import com.na21k.schedulenotes.di.modules.CategoriesModule;
import com.na21k.schedulenotes.ui.categories.CategoriesFragment;
import com.na21k.schedulenotes.ui.categories.categoryDetails.CategoryDetailsActivity;
import com.na21k.schedulenotes.ui.lists.ListsFragment;
import com.na21k.schedulenotes.ui.lists.languages.LanguagesListActivity;
import com.na21k.schedulenotes.ui.lists.languages.wordOrPhraseDetails.WordOrPhraseDetailsActivity;
import com.na21k.schedulenotes.ui.lists.movies.MoviesListActivity;
import com.na21k.schedulenotes.ui.lists.music.MusicListActivity;
import com.na21k.schedulenotes.ui.lists.shopping.ShoppingListActivity;
import com.na21k.schedulenotes.ui.lists.userDefinedLists.UserDefinedListActivity;
import com.na21k.schedulenotes.ui.notes.NotesFragment;
import com.na21k.schedulenotes.ui.notes.noteDetails.NoteDetailsActivity;

import dagger.BindsInstance;
import dagger.Component;

@Component(
        modules = {
                AppModule.class,
                CategoriesModule.class
        }
)
public interface ApplicationComponent {

    void inject(NoteDetailsActivity noteDetailsActivity);

    void inject(MoviesListActivity moviesListActivity);

    void inject(MusicListActivity musicListActivity);

    void inject(ShoppingListActivity shoppingListActivity);

    void inject(LanguagesListActivity languagesListActivity);

    void inject(WordOrPhraseDetailsActivity wordOrPhraseDetailsActivity);

    void inject(UserDefinedListActivity userDefinedListActivity);

    void inject(CategoryDetailsActivity categoryDetailsActivity);

    void inject(NotesFragment notesFragment);

    void inject(ListsFragment listsFragment);

    void inject(CategoriesFragment categoriesFragment);

    @Component.Factory
    interface Factory {

        ApplicationComponent create(@BindsInstance Application application);
    }
}
