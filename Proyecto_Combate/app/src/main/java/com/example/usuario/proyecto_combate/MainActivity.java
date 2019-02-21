package com.example.usuario.proyecto_combate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Botones del menu
    Button btnNuevaPartida;
    Button btnContinuarPartida;
    Button btnCreditos;
    Button btnOpciones;
    Button btnExit;
    Button btnMostrarPuntuaciones;

    Context context = this;
    ArrayAdapter adapter;

    PuntuacionHelper dbHelper;

    List<String> listData;

    String texto = "";

    String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Establecimiento de las propiedades de la ventana(título oculto y movil horizontal
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_main);

        dbHelper = new PuntuacionHelper(getApplicationContext(), "pedra", null, 1);
        listData = new ArrayList<>();

        btnNuevaPartida = findViewById(R.id.btnNuevaPartida);
        btnContinuarPartida = findViewById(R.id.btnContinuar);
        btnCreditos = findViewById(R.id.btnCreditos);
        btnOpciones = findViewById(R.id.btnOpciones);
        btnExit = findViewById(R.id.btnExit);
        btnMostrarPuntuaciones = findViewById(R.id.btnMostrarPuntuaciones);

        btnNuevaPartida.setOnClickListener(onClickListener);
        btnContinuarPartida.setOnClickListener(onClickListener);
        btnCreditos.setOnClickListener(onClickListener);
        btnOpciones.setOnClickListener(onClickListener);
        btnExit.setOnClickListener(onClickListener);
        btnMostrarPuntuaciones.setOnClickListener(onClickListener);
        startService(new Intent(this, ServicioSonido.class));
    }

    //Establecer funcionalidad OnClick de los botones
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btnNuevaPartida:
                    Intent i = new Intent(getApplicationContext(), Enlace.class);
                    stopService(new Intent(getApplicationContext(), ServicioSonido.class));
                    startActivity(i);
                    break;
                case R.id.btnContinuar:
                    Intent intent2 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent2.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                    if (intent2.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent2  , 10);
                    } else {
                        Toast.makeText(getApplicationContext(), "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnCreditos:

                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    View view1 = inflater.inflate(R.layout.creditos, null);

                    Button btnCerrarCreditos = view1.findViewById(R.id.btnCerrarCreditos);

                    final AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setView(view1)
                            .create();
                    alertDialog.show();

                    btnCerrarCreditos.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.cancel();
                        }
                    });

                    break;
                case R.id.btnOpciones:
                    mostrarDialogo();
                    break;

                case R.id.btnExit:
                    System.exit(0);
                    break;

                case R.id.btnMostrarPuntuaciones:

                    Intent intent = new Intent(getApplicationContext(), listaPuntuaciones.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    texto = (result.get(0));
                }

                if(texto.equals("system call")){
                    Intent huella = new Intent(getApplicationContext(), Main2Activity.class);
                    startActivity(huella);
                }
                break;
        }
    }

    private void mostrarDialogo() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Establecer Nombre");
        alertDialog.setMessage("Introduce tu nombre");

        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        nombre = input.getText().toString();
                        SharedPreferences prefs = getSharedPreferences("CombateNombre",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("nombre", nombre);
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
}
