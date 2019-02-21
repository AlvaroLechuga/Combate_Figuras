package com.example.usuario.proyecto_combate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PuntuacionHelper extends SQLiteOpenHelper {


    public PuntuacionHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    String sqlCreate = "CREATE TABLE puntuacion (Nombre VARCHAR, Puntuacion INTEGER, Tiempo VARCHAR)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS puntuacion");
        db.execSQL(sqlCreate);
    }
}
