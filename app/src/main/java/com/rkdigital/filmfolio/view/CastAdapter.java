package com.rkdigital.filmfolio.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rkdigital.filmfolio.R;
import com.rkdigital.filmfolio.model.MovieDetail;

import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private final List<MovieDetail.Credits.Cast> castList;
    private final Context context;

    public CastAdapter(Context context, List<MovieDetail.Credits.Cast> castList) {
        this.context = context;
        this.castList = castList;
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cast, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        MovieDetail.Credits.Cast cast = castList.get(position);

        holder.tvName.setText(cast.getName());
        holder.tvCharacter.setText(cast.getCharacter());

        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w185" + cast.getProfilePath())
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(holder.ivProfile);
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvName, tvCharacter;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvCharacter = itemView.findViewById(R.id.tvCharacter);
        }
    }
}
