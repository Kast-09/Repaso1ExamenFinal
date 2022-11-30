package com.example.repaso1examenfinal.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.repaso1examenfinal.DetalleActivity;
import com.example.repaso1examenfinal.R;
import com.example.repaso1examenfinal.entities.Pelicula;
import com.example.repaso1examenfinal.services.PeliculaService;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PeliculaAdapter extends RecyclerView.Adapter {

    List<Pelicula> data;

    public PeliculaAdapter(List<Pelicula> data){
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View itemView = inflater.inflate(R.layout.item_pelicula, parent,false);

        return new PeliculasViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Pelicula pelicula = data.get(position);

        TextView tvTitulo = holder.itemView.findViewById(R.id.tvTitulo);
        tvTitulo.setText(data.get(position).titulo);

        TextView tvSinopsis = holder.itemView.findViewById(R.id.tvSinopsis);
        tvSinopsis.setText(data.get(position).sinopsis);

        ImageView ivPelicula = holder.itemView.findViewById(R.id.ivPelicula);
        Picasso.get().load(data.get(position).imagen).into(ivPelicula);

        ivPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), DetalleActivity.class);
                intent.putExtra("PELICULA_DATA", new Gson().toJson(pelicula));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class PeliculasViewHolder extends RecyclerView.ViewHolder{
        public PeliculasViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
