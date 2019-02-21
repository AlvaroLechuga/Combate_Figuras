package com.example.usuario.proyecto_combate;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class listaPuntuaciones extends ListActivity {

    PuntuacionHelper dbHelper;
    List<String> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new PuntuacionHelper(getApplicationContext(), "pedra", null, 1);

        listData = new ArrayList<String>();
        listData = ObtenerPuntuaciones();

        ArrayAdapter<String> listDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listData);
        this.setListAdapter(listDataAdapter);
    }

    private List<String> ObtenerPuntuaciones() {

        listData = new ArrayList<String>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String consulta = "SELECT * FROM puntuacion ORDER BY Puntuacion, Tiempo DESC";

        Cursor cursor = db.rawQuery(consulta, null);
        int cont = 0;
        while (cursor.moveToNext()) {
            listData.add(cursor.getString(0)+" - "+cursor.getInt(1)+ " - "+cursor.getString(2));
            cont++;
            if(cont == 5){
                break;
            }
        }
        return listData;
    }
}
