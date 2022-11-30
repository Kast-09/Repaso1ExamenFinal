package com.example.repaso1examenfinal.services;

import com.example.repaso1examenfinal.entities.Pelicula;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PeliculaService {
    @GET("peliculas")
    Call<Pelicula> finById(@Path("peliculaId") int id);

    @GET("peliculas")
    Call<List<Pelicula>> getAll();

    @POST("peliculas")
    Call<Pelicula> create(@Body Pelicula pelicula);

    @PUT("peliculas/{id}")
    Call<Pelicula> update(@Path("id") int id, @Body Pelicula pelicula);

    @DELETE("peliculas/{id}")
    Call<Pelicula> delete(@Path("id") int id);

    @GET("/peliculas/{id}/ubicaciones")
    Call<List<Pelicula.Ubicaciones>> getUbicaciones(@Path("id") int id);

    @POST("/peliculas/{id}/ubicaciones")
    Call<Pelicula.Ubicaciones> createUbicacion(@Path("id") int id, @Body Pelicula.Ubicaciones ubicaciones);
}
