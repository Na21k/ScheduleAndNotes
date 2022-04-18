package com.na21k.schedulenotes.ui.lists.shopping;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Shopping.ShoppingListItem;
import com.na21k.schedulenotes.databinding.ShoppingListItemBinding;
import com.na21k.schedulenotes.ui.shared.viewHolders.BaseViewHolder;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingItemViewHolder> {

    private final OnShoppingItemActionRequestedListener mOnShoppingItemActionRequestedListener;
    private List<ShoppingListItem> mItems;

    public ShoppingListAdapter(OnShoppingItemActionRequestedListener onShoppingItemActionRequestedListener) {
        mOnShoppingItemActionRequestedListener = onShoppingItemActionRequestedListener;
        setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    @NonNull
    @Override
    public ShoppingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ShoppingListItemBinding binding = ShoppingListItemBinding
                .inflate(inflater, parent, false);

        return new ShoppingItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingItemViewHolder holder, int position) {
        ShoppingListItem item = mItems.get(position);
        holder.setData(item);
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    public void setGoods(List<ShoppingListItem> goods) {
        mItems = goods;
        notifyDataSetChanged();
    }

    public class ShoppingItemViewHolder extends BaseViewHolder {

        private final ShoppingListItemBinding mBinding;
        private ShoppingListItem mItem;

        public ShoppingItemViewHolder(ShoppingListItemBinding binding) {
            super(binding.getRoot(), R.menu.simple_item_long_press_menu, 0);
            mBinding = binding;

            itemView.setOnClickListener(v ->
                    mOnShoppingItemActionRequestedListener.onShoppingItemUpdateRequested(mItem));
            mBinding.isChecked.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked != mItem.isChecked()) {
                    mItem.setChecked(isChecked);
                    mOnShoppingItemActionRequestedListener
                            .onShoppingItemSilentUpdateRequested(mItem);
                }
            });
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_delete_menu_item:
                    mOnShoppingItemActionRequestedListener.onShoppingItemDeletionRequested(mItem);
                    return true;
                default:
                    return false;
            }
        }

        private void setData(ShoppingListItem item) {
            mItem = item;
            mBinding.shoppingListItemName.setText(item.getText());
            mBinding.isChecked.setChecked(item.isChecked());

            float price = item.getPrice();
            int priceTextColor;
            int count = item.getCount();

            if (price == 0f) {
                mBinding.shoppingListItemOverallPrice.setText(R.string.price_not_set);
                priceTextColor = ContextCompat.getColor(itemView.getContext(),
                        R.color.warning_text);
            } else {
                mBinding.shoppingListItemOverallPrice.setText(String.valueOf(price * count));
                priceTextColor = ContextCompat.getColor(itemView.getContext(),
                        R.color.price_text_color);
            }

            mBinding.shoppingListItemOverallPrice.setTextColor(priceTextColor);

            String countFormatted = itemView.getContext().getResources()
                    .getString(R.string.count_formatted, count);
            mBinding.shoppingListItemCount.setText(countFormatted);

            updateTextPaintFlags();
            updateCardColor();
        }

        private void updateTextPaintFlags() {
            int currentPaintFlags = mBinding.shoppingListItemName.getPaintFlags();

            if (mItem.isChecked()) {
                mBinding.shoppingListItemName
                        .setPaintFlags(currentPaintFlags | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                mBinding.shoppingListItemName
                        .setPaintFlags(currentPaintFlags & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        private void updateCardColor() {
            int color;

            if (mItem.isChecked()) {
                color = ContextCompat.getColor(itemView.getContext(), R.color.list_item_grayed_out);
            } else {
                color = ContextCompat.getColor(itemView.getContext(), R.color.list_item);
            }

            mBinding.shoppingListItemCard.setCardBackgroundColor(color);
        }
    }

    public interface OnShoppingItemActionRequestedListener {

        void onShoppingItemUpdateRequested(ShoppingListItem item);

        void onShoppingItemSilentUpdateRequested(ShoppingListItem item);

        void onShoppingItemDeletionRequested(ShoppingListItem item);
    }
}
