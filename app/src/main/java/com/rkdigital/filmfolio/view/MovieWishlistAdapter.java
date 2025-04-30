package com.rkdigital.filmfolio.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.rkdigital.filmfolio.MovieAbout;
import com.rkdigital.filmfolio.R;


import com.rkdigital.filmfolio.databinding.MovieWishlistItemBinding;
import com.rkdigital.filmfolio.model.WishlistMovie;

import java.util.ArrayList;
import java.util.List;

public class MovieWishlistAdapter extends RecyclerView.Adapter<MovieWishlistAdapter.MovieViewHolder> {
    private Context context;
    private List<WishlistMovie> movies = new ArrayList<>();

    // Simplified constructor
    public MovieWishlistAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MovieWishlistItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.movie_wishlist_item,
                parent,
                false
        );
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        WishlistMovie movie = movies.get(position);
        holder.binding.setMovie(movie);

        // Execute pending bindings immediately for smooth scrolling
//        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void updateList(List<WishlistMovie> newMovies) {
        this.movies.clear();
        if (newMovies != null) {
            this.movies.addAll(newMovies);
        }
        notifyDataSetChanged();
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder {
        private MovieWishlistItemBinding binding;

        public MovieViewHolder(MovieWishlistItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        WishlistMovie movie = movies.get(position);
                        Intent intent = new Intent(context, MovieAbout.class);
                        intent.putExtra("movie_id", movie.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
