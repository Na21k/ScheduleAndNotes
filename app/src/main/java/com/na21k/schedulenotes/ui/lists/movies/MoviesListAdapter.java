package com.na21k.schedulenotes.ui.lists.movies;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.databinding.SimpleListItemBinding;
import com.na21k.schedulenotes.ui.shared.viewHolders.BaseViewHolder;

import java.util.List;

public class MoviesListAdapter extends RecyclerView.Adapter<MoviesListAdapter.MovieViewHolder> {

    private final OnMovieActionRequestedListener mOnMovieActionRequestedListener;
    private List<MoviesListItem> mMovies;

    public MoviesListAdapter(OnMovieActionRequestedListener onMovieActionRequestedListener) {
        this.mOnMovieActionRequestedListener = onMovieActionRequestedListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        SimpleListItemBinding binding = SimpleListItemBinding
                .inflate(inflater, parent, false);

        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        MoviesListItem movie = mMovies.get(position);
        holder.setData(movie);
    }

    @Override
    public int getItemCount() {
        return mMovies != null ? mMovies.size() : 0;
    }

    public void setMovies(List<MoviesListItem> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends BaseViewHolder {

        protected final SimpleListItemBinding mBinding;
        private MoviesListItem mMovie;

        public MovieViewHolder(SimpleListItemBinding binding) {
            super(binding.getRoot(), R.menu.simple_item_long_press_menu, 0);
            mBinding = binding;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_delete_menu_item:
                    mOnMovieActionRequestedListener.onMovieDeletionRequested(mMovie);
                    return true;
                default:
                    return false;
            }
        }

        private void setData(MoviesListItem movie) {
            mMovie = movie;
            mBinding.itemText.setText(movie.getText());

            itemView.setOnClickListener(
                    v -> mOnMovieActionRequestedListener.onMovieUpdateRequested(movie));

            itemView.setOnCreateContextMenuListener(this);
        }
    }

    public interface OnMovieActionRequestedListener {

        void onMovieUpdateRequested(MoviesListItem movie);

        void onMovieDeletionRequested(MoviesListItem movie);
    }
}
