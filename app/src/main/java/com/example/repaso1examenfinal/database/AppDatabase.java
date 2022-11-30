package com.example.repaso1examenfinal.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

//import com.example.repaso1examenfinal.daos.PeliculaDao;
import com.example.repaso1examenfinal.entities.Pelicula;
/*
@Database(entities = {Pelicula.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract PeliculaDao peliculaDao();

    public static AppDatabase getInstance(Context context){
        return Room.databaseBuilder(context, AppDatabase.class, "RepasoExamen")
                .allowMainThreadQueries()
                .build();
    }
}*/
