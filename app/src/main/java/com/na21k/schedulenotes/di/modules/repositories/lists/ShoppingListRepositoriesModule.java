package com.na21k.schedulenotes.di.modules.repositories.lists;

import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.Repository;
import com.na21k.schedulenotes.repositories.lists.shopping.ShoppingListRepository;
import com.na21k.schedulenotes.repositories.lists.shopping.ShoppingListRepositoryImpl;

import dagger.Binds;
import dagger.Module;

@Module
public interface ShoppingListRepositoriesModule {

    @Binds
    Repository<ShoppingListItem> bindShoppingListItemsRepository(ShoppingListRepositoryImpl shoppingListRepository);

    @Binds
    MutableRepository<ShoppingListItem> bindShoppingListItemsRepositoryMutable(ShoppingListRepositoryImpl shoppingListRepository);

    @Binds
    ShoppingListRepository bindShoppingListRepository(ShoppingListRepositoryImpl shoppingListRepository);

    @Binds
    CanSearchRepository<ShoppingListItem> bindCanSearchShoppingListRepository(ShoppingListRepositoryImpl shoppingListRepository);

    @Binds
    CanClearRepository<ShoppingListItem> bindCanClearShoppingListRepository(ShoppingListRepositoryImpl shoppingListRepository);
}
