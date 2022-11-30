package com.example.repaso1examenfinal.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.repaso1examenfinal.entities.Pelicula;

import java.util.List;
/*
@Dao
public interface PeliculaDao {
    @Query("SELECT * FROM peliculas")
    List<Pelicula> getAll();

    @Query("SELECT * FROM peliculas where id = :id")//el nombre del id debe ser igual en el método y en el Query
    Pelicula find(int id);

    @Insert
    void create(Pelicula pelicula);//para enviar varios usuario podemos usar -> void create(User... user); // y se debe pasar los parametros por coma

    @Update
    void update(Pelicula pelicula);

    @Delete
    void delete(Pelicula pelicula);
}*/
