package com.example.repaso1examenfinal.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

//@Entity(tableName = "peliculas")
public class Pelicula {
    //@PrimaryKey(autoGenerate = true)
    public int id;
    //@ColumnInfo(name = "titulo")
    public String titulo;
    //@ColumnInfo(name = "sinopsis")
    public String sinopsis;
    //@ColumnInfo(name = "imagen")
    public String imagen;
    public Ubicaciones ubicaciones;
    /*@ColumnInfo(name = "latitud")
    public Double latitud;
    @ColumnInfo(name = "longitud")
    public Double longitud;*/
    public static class Ubicaciones{
        public int id;
        public int peliculaId;
        //@ColumnInfo(name = "latitud")
        public Double latitud;
        public Double longitud;
    }
}
