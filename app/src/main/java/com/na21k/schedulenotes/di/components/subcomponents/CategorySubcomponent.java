package com.na21k.schedulenotes.di.components.subcomponents;

import com.na21k.schedulenotes.di.modules.CategoryModule;
import com.na21k.schedulenotes.ui.categories.categoryDetails.CategoryDetailsActivity;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

import dagger.BindsInstance;
import dagger.Module;
import dagger.Subcomponent;

@Subcomponent(modules = CategoryModule.class)
public interface CategorySubcomponent {

    void inject(CategoryDetailsActivity categoryDetailsActivity);

    @Subcomponent.Factory
    interface Factory {

        CategorySubcomponent create(@BindsInstance @CategoryId int categoryId);
    }

    @Module(subcomponents = CategorySubcomponent.class)
    interface InstallationModule {
    }

    @Qualifier
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @interface CategoryId {
    }
}
