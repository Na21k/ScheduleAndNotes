package com.na21k.schedulenotes.ui.lists.movies;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.data.database.Lists.Movies.MoviesListItem;
import com.na21k.schedulenotes.databinding.MoviesListItemBinding;

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
        MoviesListItemBinding binding = MoviesListItemBinding
                .inflate(inflater, parent, false);

        return new MovieViewHolder(binding.getRoot(), binding);
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

    public class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private final MoviesListItemBinding mBinding;
        private MoviesListItem mMovie;

        public MovieViewHolder(@NonNull View itemView, MoviesListItemBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater menuInflater = new MenuInflater(v.getContext());
            menuInflater.inflate(R.menu.movie_long_press_menu, menu);

            for (int i = 0; i < menu.size(); i++) {
                MenuItem item = menu.getItem(i);
                item.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.movie_delete_menu_item:
                    mOnMovieActionRequestedListener.onMovieDeletionRequested(mMovie);
                    return true;
                default:
                    return false;
            }
        }

        private void setData(MoviesListItem movie) {
            mMovie = movie;
            mBinding.movieName.setText(movie.getText());

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
