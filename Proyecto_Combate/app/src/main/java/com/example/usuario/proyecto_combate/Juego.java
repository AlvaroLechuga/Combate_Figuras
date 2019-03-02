package com.example.usuario.proyecto_combate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Juego extends SurfaceView implements SurfaceHolder.Callback {

    String nombre = "Aeris";

    Random rand = new Random();

    PuntuacionHelper dbHelper;

    private HiloPantalla hiloPantalla;

    ArrayAdapter adapter;

    List<InterfazUI> ui;
    List<Sprite> sprites = new ArrayList<>();
    List<Sprite> enemigos = new ArrayList<>();
    List<Objeto> objetosDisponibles;
    List<Bitmap> interfazUsuario = new ArrayList<>();


    private Paint paintTexto;

    String caso = "";

    int n;
    String decisionEnemigo = "";

    String ocurrido = "";

    Sprite sprite;

    Tiempo tiempo;
    Tiempo tiempoEspera;
    int segundos;
    int a = 0;
    int segundosCombate = 0;

    boolean combate = true;
    boolean win = false;
    boolean loss = false;

    int maxAttack;
    int maxDef;
    int maxHealth;
    int maxAttackEnemy;

    int enemigosDerrotados = 0;

    InterfazUI btnReintentar;
    InterfazUI btnContinuar;

    MediaPlayer sonido;

    Bitmap background;

    public Juego(Context context, String nombre) {
        super(context);

        this.nombre = nombre;

        setBackgroundColor(Color.RED);
        getHolder().addCallback(this);
        dbHelper = new PuntuacionHelper(context, "pedra", null, 1);
        paintTexto = new Paint();

        sonido = MediaPlayer.create(getContext(), R.raw.combate);
        sonido.start();
    }

    @Override
    public void onDraw(final Canvas canvas){
        super.onDraw(canvas);

        Log.i("segundos", String.valueOf(segundosCombate));

        if(!sonido.isPlaying()){
            sonido.start();
        }

        paintTexto.setColor(Color.WHITE);
        paintTexto.setTextSize(60);
        paintTexto.setTextAlign(Paint.Align.CENTER);
        paintTexto.setTypeface(Typeface.create(String.valueOf(R.font.romulus), Typeface.BOLD));
        canvas.drawBitmap(background, 0f, 0f, null);

        if(a == 6){
            canvas.drawText("GAME OVER", getWidth()/2,getHeight()/3, paintTexto);
            btnReintentar.draw(canvas);
            canvas.drawBitmap(interfazUsuario.get(8), btnReintentar.getX(), btnReintentar.getY(), null);
        }

        if(a == 7){
            btnContinuar.draw(canvas);
            canvas.drawBitmap(interfazUsuario.get(9), btnContinuar.getX(), btnReintentar.getY(), null);
        }

        if(combate){

            segundos = tiempo.getSegundos(); //Se obtiene el tiempo de juego

            //Se dibuja toda la interfaz
            for(InterfazUI f: ui){
                f.draw(canvas);
            }

            canvas.drawBitmap(interfazUsuario.get(0), ui.get(0).getX(), ui.get(0).getY(), null);
            canvas.drawBitmap(interfazUsuario.get(1), ui.get(1).getX(), ui.get(1).getY(), null);
            canvas.drawBitmap(interfazUsuario.get(2), ui.get(2).getX(), ui.get(2).getY(), null);
            canvas.drawBitmap(interfazUsuario.get(3), ui.get(3).getX(), ui.get(3).getY(), null);
            canvas.drawBitmap(interfazUsuario.get(4), ui.get(4).getX(), ui.get(4).getY(), null);
            canvas.drawBitmap(interfazUsuario.get(5), ui.get(5).getX(), ui.get(5).getY(), null);
            canvas.drawBitmap(interfazUsuario.get(6), ui.get(6).getX(), ui.get(6).getY(), null);
            canvas.drawBitmap(interfazUsuario.get(7), ui.get(7).getX(), ui.get(7).getY(), null);

            //Se dibujan todos los sprites

            if(!sprites.get(0).isAtaca() && !sprites.get(1).isAtaca()){
                sprites.get(0).onDraw(canvas);
                sprites.get(1).onDraw(canvas);
            }else if(sprites.get(0).isAtaca()){
                sprites.get(0).onDraw(canvas);
            }else if(sprites.get(1).isAtaca()){
                sprites.get(1).onDraw(canvas);
            }

            canvas.drawText(ocurrido, ui.get(0).getAncho()/2, ui.get(0).getAlto()/2, paintTexto);
            canvas.drawText(sprites.get(1).getPersonaje().getNombre(), ui.get(5).getX()+300, ui.get(5).getY()+115, paintTexto); //Se dibuja el nombre del enemigo
            canvas.drawText(sprites.get(0).getPersonaje().getNombre() +"   "+sprites.get(0).getPersonaje().getVida()+" / "+maxHealth , ui.get(6).getX()+400, ui.get(6).getY()+120, paintTexto);

            //En caso de haber pulsado algún botón para hacer algo (Piedra-Papel-Tijeras-Uso de objeto)
            if(!caso.equals("")){
                n = rand.nextInt(3) + 0; //Se genera un caso aleatorio del enemigo

                switch (n){
                    case 0:
                        decisionEnemigo = "tijera";
                        break;
                    case 1:
                        decisionEnemigo = "piedra";
                        break;
                    case 2:
                        decisionEnemigo = "papel";
                        break;
                }
            }

            if(tiempoEspera.getSegundos() != 1){
                segundosCombate = tiempoEspera.getSegundos();
            }

            if(tiempoEspera.getSegundos() == 1){
                tiempoEspera.Detener();
                segundosCombate = 0;
                sprites.get(0).setAtaca(false);
                sprites.get(1).setAtaca(false);
            }

            if(segundosCombate == 0){
                if(!caso.equals("") && !decisionEnemigo.equals("")){
                    if(caso.equals("objeto")){
                        if(a == 0){
                            MostrarListaObjetos();
                            if(a == 1){
                                Combate();
                                tiempoEspera.Contar();
                            }
                        }
                    }else{
                        Combate();
                        tiempoEspera.Contar();
                    }
                }
            }

        }else{
            if(win){
                //Se reestablece las propiedades que han podido ser modificadas por objetos
                canvas.drawText("HAS GANADO", getWidth()/3,getHeight()/3, paintTexto);
                if(sprites.get(0).getPersonaje().getNivel().subirNivel(100)){
                    sprites.get(0).getPersonaje().setAtaque(maxAttack);
                    sprites.get(0).getPersonaje().setDefensa(maxDef);
                    sprites.get(0).getPersonaje().setVida(maxHealth);
                    nuevasPropiedades();
                    sprites.get(0).setAtaca(false);
                    sprites.get(1).setAtaca(false);
                    segundosCombate = 0;
                    tiempoEspera.Detener();
                }

                AddnewItems();
                NuevaPelea();

            }else{
                if(loss){
                    tiempo.Detener();
                    sprites.get(0).setAtaca(false);
                    sprites.get(1).setAtaca(false);
                    segundosCombate = 0;
                    tiempoEspera.Detener();
                    if(a == 5){
                        MostrarDialogo();
                    }
                }
            }
        }
    }

    private void NuevaPelea() {
        sprites.remove(1);
        sprites.add(enemigos.get(rand.nextInt(10)));
    }

    private void MostrarDialogo() {

        a = 6;

        sprites.get(0).getPersonaje().setVida(maxHealth);
        sprites.get(0).getPersonaje().setAtaque(maxAttack);
        sprites.get(0).getPersonaje().setDefensa(maxDef);

        sprites.get(1).getPersonaje().setAtaque(maxAttackEnemy);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Guardar Puntuacion");
        alertDialog.setMessage("Introduce tu nombre");

        final EditText input = new EditText(getContext());
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
                        Insertar();
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        enemigosDerrotados = 0;
                    }
                });

        alertDialog.show();
    }

    private void MostrarListaObjetos() {
        a = 3;
        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, objetosDisponibles);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view1 = inflater.inflate(R.layout.listaobjetos, null);

        final ListView listViewVehiculos = view1.findViewById(R.id.listaObjetos);
        listViewVehiculos.setAdapter(adapter);

        Button btnCerrarObjetos = view1.findViewById(R.id.btnCerrarObjetos);

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(view1)
                .setCancelable(false)
                .create();

        alertDialog.show();

        btnCerrarObjetos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        listViewVehiculos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Objeto objeto = new Objeto();
                objeto.setNombre(objetosDisponibles.get(position).getNombre());
                objeto.setCantidad(objetosDisponibles.get(position).getCantidad());
                objeto.setDescripcion(objetosDisponibles.get(position).getDescripcion());

                if(objeto.getCantidad() != 0){
                    switch (objeto.getNombre()){

                        case "Pocion":
                            if(maxHealth == sprites.get(0).getPersonaje().getVida()){
                                Toast.makeText(getContext(), "Full vida", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }else{
                                for(int i = 0; i<10; i++){
                                    if(maxHealth == sprites.get(0).getPersonaje().getVida()){
                                        objetosDisponibles.get(position).setCantidad(objeto.getCantidad()-1);
                                        alertDialog.dismiss();
                                        break;
                                    }
                                    sprites.get(0).getPersonaje().setVida(sprites.get(0).getPersonaje().getVida()+1);
                                }
                                if(maxHealth > sprites.get(0).getPersonaje().getVida()){
                                    objetosDisponibles.get(position).setCantidad(objeto.getCantidad()-1);
                                }
                            }
                            a = 1;
                            alertDialog.dismiss();
                            break;
                        case "Pocion Mediana":
                            if(maxHealth == sprites.get(0).getPersonaje().getVida()){
                                Toast.makeText(getContext(), "Full vida", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }else{
                                for(int i = 0; i<10; i++){
                                    if(maxHealth == sprites.get(0).getPersonaje().getVida()){
                                        objetosDisponibles.get(position).setCantidad(objeto.getCantidad()-1);
                                        break;
                                    }
                                    sprites.get(0).getPersonaje().setVida(sprites.get(0).getPersonaje().getVida()+1);
                                }
                                if(maxHealth > sprites.get(0).getPersonaje().getVida()){
                                    objetosDisponibles.get(position).setCantidad(objeto.getCantidad()-1);
                                }
                            }
                            a = 1;
                            break;
                        case "Pocion Grande":
                            if(maxHealth == sprites.get(0).getPersonaje().getVida()){
                                Toast.makeText(getContext(), "Full vida", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }else{
                                for(int i = 0; i<10; i++){
                                    if(maxHealth == sprites.get(0).getPersonaje().getVida()){
                                        objetosDisponibles.get(position).setCantidad(objeto.getCantidad()-1);
                                        alertDialog.dismiss();
                                        break;
                                    }
                                    sprites.get(0).getPersonaje().setVida(sprites.get(0).getPersonaje().getVida()+1);
                                }
                                if(maxHealth > sprites.get(0).getPersonaje().getVida()){
                                    objetosDisponibles.get(position).setCantidad(objeto.getCantidad()-1);
                                }
                            }
                            a = 1;
                            alertDialog.dismiss();
                            break;
                        case "Potenciador Daño":
                            sprites.get(0).getPersonaje().setAtaque(sprites.get(0).getPersonaje().getAtaque()+2);
                            a = 1;
                            objetosDisponibles.get(position).setCantidad(objeto.getCantidad()-1);
                            alertDialog.dismiss();
                            break;
                        case "Potenciador Defensa":
                            sprites.get(0).getPersonaje().setDefensa(sprites.get(0).getPersonaje().getDefensa()+3);
                            a = 1;
                            objetosDisponibles.get(position).setCantidad(objeto.getCantidad()-1);
                            alertDialog.dismiss();
                            break;
                    }
                }
            }
        });
    }

    //Una vez se ha seleccionado una opción se comprueba el resultado que tendrá con el enemigo
    private void Combate() {

        double damage = 0;
        double parte1;
        double parte2;
        double parte3;

        if(caso.equals("tijera") && decisionEnemigo.equals("papel")){
            ocurrido = "HAS ATACADO AL ENEMIGO";

            parte1 = 0.01 * 1 * 1 * Math.random() * (30 - 10 + 1) + 10;
            parte2 = (0.2 * sprites.get(0).getPersonaje().getNivel().getLvl() + 1 ) * sprites.get(0).getPersonaje().getAtaque() * 2;
            parte3 = 25 * sprites.get(1).getPersonaje().getDefensa();
            damage = parte1 * ((parte2 / parte3) + 2);

            sprites.get(0).setAtaca(true);
            sprites.get(1).getPersonaje().setVida((int) (sprites.get(1).getPersonaje().getVida() - damage));

        }else if(caso.equals("tijera") && decisionEnemigo.equals("piedra")){
            ocurrido = "EL ENEMIGO SE HA DEFENDIDO";
        }else if(caso.equals("tijera") && decisionEnemigo.equals("tijera")){
            ocurrido = "HABEIS EMPATADO";
        }else if(caso.equals("piedra") && decisionEnemigo.equals("papel")){
            ocurrido = "EL ENEMIGO TE HA ATACADO";

            parte1 = 0.01 * 1 * 1 * Math.random() * (30 - 10 + 1) + 10;
            parte2 = (0.2 * sprites.get(1).getPersonaje().getNivel().getLvl() + 1 ) * sprites.get(1).getPersonaje().getAtaque() * 2;
            parte3 = 25 * sprites.get(0).getPersonaje().getDefensa();
            damage = parte1 * ((parte2 / parte3) + 2);

            sprites.get(1).setAtaca(true);
            sprites.get(0).getPersonaje().setVida((int) (sprites.get(0).getPersonaje().getVida() - damage));

        }else if(caso.equals("piedra") && decisionEnemigo.equals("piedra")){
            ocurrido = "HABEIS EMPATADO";
        }else if(caso.equals("piedra") && decisionEnemigo.equals("tijera")){
            ocurrido="TE HAS DEFENDIDO";
        }else if(caso.equals("papel") && decisionEnemigo.equals("papel")){
            ocurrido = "HABEIS EMPATADO";
        }else if(caso.equals("papel") && decisionEnemigo.equals("piedra")){
            ocurrido = "HAS ATACADO AL ENEMIGO";

            parte1 = 0.01 * 1 * 1 * Math.random() * (30 - 10 + 1) + 10;
            parte2 = (0.2 * sprites.get(0).getPersonaje().getNivel().getLvl() + 1 ) * sprites.get(0).getPersonaje().getAtaque() * 2;
            parte3 = 25 * sprites.get(1).getPersonaje().getDefensa();
            damage = parte1 * ((parte2 / parte3) + 2);

            sprites.get(0).setAtaca(true);
            sprites.get(1).getPersonaje().setVida((int) (sprites.get(1).getPersonaje().getVida() - damage));

        }else if(caso.equals("papel") && decisionEnemigo.equals("tijera")){
            ocurrido = "EL ENEMIGO TE HA ATACADO";

            parte1 = 0.01 * 1 * 1 * Math.random() * (30 - 10 + 1) + 10;
            parte2 = (0.2 * sprites.get(1).getPersonaje().getNivel().getLvl() + 1 ) * sprites.get(1).getPersonaje().getAtaque() * 2;
            parte3 = 25 * sprites.get(0).getPersonaje().getDefensa();
            damage = parte1 * ((parte2 / parte3) + 2);

            sprites.get(1).setAtaca(true);
            sprites.get(0).getPersonaje().setVida((int) (sprites.get(0).getPersonaje().getVida() - damage));

        }else if(caso.equals("objeto") && decisionEnemigo.equals("papel")){
            ocurrido = "EL ENEMIGO TE HA ATACADO";

            parte1 = 0.01 * 1 * 1 * Math.random() * (30 - 10 + 1) + 10;
            parte2 = (0.2 * sprites.get(1).getPersonaje().getNivel().getLvl() + 1 ) * sprites.get(1).getPersonaje().getAtaque() * 2;
            parte3 = 25 * sprites.get(0).getPersonaje().getDefensa();
            damage = parte1 * ((parte2 / parte3) + 2);

            sprites.get(1).setAtaca(true);
            sprites.get(0).getPersonaje().setVida((int) (sprites.get(0).getPersonaje().getVida() - damage));

        }else if(caso.equals("objeto") && decisionEnemigo.equals("piedra")){
            ocurrido = "EL ENEMIGO SE HA DEFENDIDO";

        }else if(caso.equals("objeto") && decisionEnemigo.equals("tijera")){
            ocurrido = "EL ENEMIGO TE HA ATACADO";

            parte1 = 0.01 * 1 * 1 * Math.random() * (30 - 10 + 1) + 10;
            parte2 = (0.2 * sprites.get(1).getPersonaje().getNivel().getLvl() + 1 ) * sprites.get(1).getPersonaje().getAtaque() * 2;
            parte3 = 25 * sprites.get(0).getPersonaje().getDefensa();
            damage = parte1 * ((parte2 / parte3) + 2);

            sprites.get(1).setAtaca(true);
            sprites.get(0).getPersonaje().setVida((int) (sprites.get(0).getPersonaje().getVida() - damage));
        }

        if(sprites.get(0).getPersonaje().getVida() <= 0){
            combate = false;
            loss = true;
            a = 5;
            caso = "";
            decisionEnemigo = "";
        }

        if(sprites.get(1).getPersonaje().getVida() <= 0){
            combate = false;
            win = true;
            enemigosDerrotados++;

            caso = "";
            decisionEnemigo = "";
        }

        if(sprites.get(0).getPersonaje().getVida() > 0 && sprites.get(1).getPersonaje().getVida() > 0){
            caso = "";
            decisionEnemigo = "";
            a = 0;
        }
    }

    //Al subir de nivel se actualizan las propiedades del personaje en cuestión
    private void nuevasPropiedades() {
        sprites.get(0).getPersonaje().setAtaque(sprites.get(0).getPersonaje().getAtaque()+(rand.nextInt(6) + 0));
        sprites.get(0).getPersonaje().setDefensa(sprites.get(0).getPersonaje().getDefensa()+(rand.nextInt(6) + 0));
        sprites.get(0).getPersonaje().setVida(sprites.get(0).getPersonaje().getVida()+(rand.nextInt(16) + 1));
        sprites.get(0).getPersonaje().setVelocidad(sprites.get(0).getPersonaje().getVelocidad()+(rand.nextInt(6) + 0));

        maxAttack = sprites.get(0).getPersonaje().getAtaque();
        maxDef = sprites.get(0).getPersonaje().getDefensa();
        maxHealth = sprites.get(0).getPersonaje().getVida();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN: // move

                if(segundosCombate == 0){
                    if(!sprites.get(0).isAtaca() && !sprites.get(1).isAtaca()){
                        if(ui.get(1).comprobarSiTocoDentro(event.getX(), event.getY())){
                            caso = "tijera";
                        }

                        if(ui.get(2).comprobarSiTocoDentro(event.getX(), event.getY())){
                            caso = "piedra";
                        }

                        if(ui.get(3).comprobarSiTocoDentro(event.getX(), event.getY())){
                            caso = "papel";
                        }

                        if(ui.get(4).comprobarSiTocoDentro(event.getX(), event.getY())){
                            caso = "objeto";
                            a = 0;
                        }
                    }
                }

                if(ui.get(7).comprobarSiTocoDentro(event.getX(), event.getY())){
                    GuardarEstadoySalir();
                }

                if(btnContinuar.comprobarSiTocoDentro(event.getX(), event.getY())){
                    a = 0;
                    combate = true;
                    win = false;
                    loss = false;
                }

                if(btnReintentar.comprobarSiTocoDentro(event.getX(), event.getY())){
                    a = 0;
                    combate = true;
                    win = false;
                    loss = false;
                    segundos = 0;
                    establecerAtributos();
                    tiempo.Contar();
                }
                break;

            case MotionEvent.ACTION_MOVE: //3 mierdas
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }

    private void GuardarEstadoySalir() {
        System.exit(0);
    }

    private boolean Insertar() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try{
            String consulta ="INSERT INTO `puntuacion` (`Nombre`, `Puntuacion`, `Tiempo`) VALUES ('" + nombre + "', '" + enemigosDerrotados + "', '" + segundos + "')";
            db.execSQL(consulta);
            enemigosDerrotados = 0;
            return true;
        }catch (SQLException sqle){
            enemigosDerrotados = 0;
            return false;
        }catch (Exception e){
            enemigosDerrotados = 0;
            return false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        hiloPantalla = new HiloPantalla(getHolder(), this);
        hiloPantalla.setRunning(true);
        hiloPantalla.start(); //Ejecuta el metodo run del hilo

        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        background = background.createScaledBitmap(background, getWidth(), getHeight(), true);

        inicializar();
    }

    private void inicializar() {
        ui = new ArrayList<>();
        objetosDisponibles = new ArrayList<>(); //Se inicializa el ArrayList de los objetos y se establecen
        AddItems();

        //Se establece el ancho y el alto de la pantalla
        float ancho = getWidth();
        float alto = getHeight();

        tiempo = new Tiempo();
        tiempoEspera = new Tiempo();

        //Se inicializa los cuadrados del UI
        ui.add(new InterfazUI(0f, 0f, ancho, alto/6f)); //Barra información
        ui.add(new InterfazUI(1800f, 300f, ancho-1800, alto/8f)); //Atacar
        ui.add(new InterfazUI(1800f, 450f, ancho-1800, alto/8f)); //Defender
        ui.add(new InterfazUI(1800f, 600f, ancho-1800, alto/8f)); //Especial
        ui.add(new InterfazUI(1800f, 750f, ancho-1800, alto/8f)); //Objeto
        ui.add(new InterfazUI(0f, 900f, ancho/3f, alto-900)); //Lista enemigos
        ui.add(new InterfazUI(860f, 900f, ancho-860, alto-900)); //Información aliados
        ui.add(new InterfazUI(150f, 5f, 155f, alto / 6f)); //Salir del juego

        btnReintentar = new InterfazUI(ancho / 2, alto / 2, 300f, 300f);
        btnContinuar = new InterfazUI(ancho / 2, alto / 2, 300f, 300f);

        //Se crean los Bitmap para añadirlo posteriormente a la vista delante de los rectangulos que producen los eventos táctiles
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.backgrounboton);
        bmp = bmp.createScaledBitmap(bmp, Math.round(ui.get(0).getAncho()), Math.round(ui.get(0).getAlto()), true);
        interfazUsuario.add(bmp);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.tijera);
        bmp = bmp.createScaledBitmap(bmp, Math.round(ui.get(1).getAncho()), Math.round(ui.get(1).getAlto()), true);
        interfazUsuario.add(bmp);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.piedra);
        bmp = bmp.createScaledBitmap(bmp, Math.round(ui.get(2).getAncho()), Math.round(ui.get(2).getAlto()), true);
        interfazUsuario.add(bmp);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.papel);
        bmp = bmp.createScaledBitmap(bmp, Math.round(ui.get(3).getAncho()), Math.round(ui.get(3).getAlto()), true);
        interfazUsuario.add(bmp);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.objetos);
        bmp = bmp.createScaledBitmap(bmp, Math.round(ui.get(4).getAncho()), Math.round(ui.get(4).getAlto()), true);
        interfazUsuario.add(bmp);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.informacion);
        bmp = bmp.createScaledBitmap(bmp, Math.round(ui.get(5).getAncho()), Math.round(ui.get(5).getAlto()), true);
        interfazUsuario.add(bmp);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.informacion);
        bmp = bmp.createScaledBitmap(bmp, Math.round(ui.get(6).getAncho()), Math.round(ui.get(6).getAlto()), true);
        interfazUsuario.add(bmp);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.back);
        bmp = bmp.createScaledBitmap(bmp, Math.round(ui.get(7).getAncho()), Math.round(ui.get(7).getAncho()), true);
        interfazUsuario.add(bmp);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.retry);
        bmp = bmp.createScaledBitmap(bmp, Math.round(btnReintentar.getAlto()), Math.round(btnReintentar.getAncho()), true);
        interfazUsuario.add(bmp);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.continuar);
        bmp = bmp.createScaledBitmap(bmp, Math.round(btnContinuar.getAlto()), Math.round(btnReintentar.getAncho()), true);
        interfazUsuario.add(bmp);

        createSprites(); //Se llama a la función que crea los sprites

        tiempo.Contar(); //Empieza a contar el tiempo
    }

    //Función que añade los objetos al ArrayList
    private void AddItems(){
        objetosDisponibles.add(new Objeto("Pocion", 3, "Te cura 10 de vida"));
        objetosDisponibles.add(new Objeto("Pocion Mediana", 1, "Te cura 15 de vida"));
        objetosDisponibles.add(new Objeto("Pocion Grande", 1, "Te cura 20 de vida"));
        objetosDisponibles.add(new Objeto("Potenciador Daño", 1, "Aumenta tu daño base +2"));
        objetosDisponibles.add(new Objeto("Potenciador Defensa", 1, "Aumenta tu defensa base +3"));
    }

    private void AddnewItems(){
        objetosDisponibles.get(0).setCantidad(objetosDisponibles.get(0).getCantidad() + (rand.nextInt(3) + 0) );
        objetosDisponibles.get(1).setCantidad(objetosDisponibles.get(1).getCantidad() + (rand.nextInt(3) + 0) );
        objetosDisponibles.get(2).setCantidad(objetosDisponibles.get(2).getCantidad() + (rand.nextInt(3) + 0) );
        objetosDisponibles.get(3).setCantidad(objetosDisponibles.get(3).getCantidad() + (rand.nextInt(3) + 0) );
        objetosDisponibles.get(4).setCantidad(objetosDisponibles.get(4).getCantidad() + (rand.nextInt(3) + 0) );

        a = 7;
    }

    //Función que crea los Sprites
    private void createSprites(){
        sprites.add(createSprites(R.drawable.protagonista, 1400, 600));

        IniciarEnemigos();

        //sprites.add(createSprites(R.drawable.enemigo5, 50, 600));
        establecerAtributos();
        sprites.add(enemigos.get(rand.nextInt(10)+0));
    }

    //Se crea el Bitmap del sprite en cuestión y se reescala con la pantalla
    private Sprite createSprites(int resource, int x, int y){
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resource);
        bmp = bmp.createScaledBitmap(bmp, (int)(getWidth()*0.4), (int)(getHeight()*0.4), true);
        sprite = new Sprite(this, bmp, x, y);
        return sprite;
    }

    private void IniciarEnemigos(){
        enemigos.add(createSprites(R.drawable.enemigo, 50, 600));
        enemigos.add(createSprites(R.drawable.enemigo2, 50, 600));
        enemigos.add(createSprites(R.drawable.enemigo3, 50, 600));
        enemigos.add(createSprites(R.drawable.enemigo4, 50, 600));
        enemigos.add(createSprites(R.drawable.enemigo5, 50, 600));
        enemigos.add(createSprites(R.drawable.enemigo6, 50, 600));
        enemigos.add(createSprites(R.drawable.enemigo7, 50, 600));
        enemigos.add(createSprites(R.drawable.enemigo8, 50, 600));
        enemigos.add(createSprites(R.drawable.enemigo9, 50, 600));
        enemigos.add(createSprites(R.drawable.enemigo10, 50, 600));
    }

    //Se establecen las propiedades de cada personaje
    private void establecerAtributos() {

        sprites.get(0).getPersonaje().setNombre(nombre);
        sprites.get(0).getPersonaje().setAtaque(3);
        sprites.get(0).getPersonaje().setDefensa(4);
        sprites.get(0).getPersonaje().setVida(150);
        sprites.get(0).getPersonaje().setVelocidad(15);
        sprites.get(0).getPersonaje().getNivel().setLvl(1);
        sprites.get(0).setEnemigo(true);
        maxAttack = sprites.get(0).getPersonaje().getAtaque();
        maxDef = sprites.get(0).getPersonaje().getDefensa();
        maxHealth = sprites.get(0).getPersonaje().getVida();

        enemigos.get(0).getPersonaje().setNombre("???");
        enemigos.get(0).getPersonaje().setAtaque(3);
        enemigos.get(0).getPersonaje().setDefensa(4);
        enemigos.get(0).getPersonaje().setVida(150);
        enemigos.get(0).getPersonaje().setVelocidad(15);
        enemigos.get(0).getPersonaje().getNivel().setLvl(1);
        maxAttackEnemy = enemigos.get(0).getPersonaje().getAtaque();

        enemigos.get(1).getPersonaje().setNombre("???");
        enemigos.get(1).getPersonaje().setAtaque(3);
        enemigos.get(1).getPersonaje().setDefensa(4);
        enemigos.get(1).getPersonaje().setVida(150);
        enemigos.get(1).getPersonaje().setVelocidad(15);
        enemigos.get(1).getPersonaje().getNivel().setLvl(1);
        maxAttackEnemy = enemigos.get(1).getPersonaje().getAtaque();

        enemigos.get(2).getPersonaje().setNombre("???");
        enemigos.get(2).getPersonaje().setAtaque(3);
        enemigos.get(2).getPersonaje().setDefensa(4);
        enemigos.get(2).getPersonaje().setVida(150);
        enemigos.get(2).getPersonaje().setVelocidad(15);
        enemigos.get(2).getPersonaje().getNivel().setLvl(1);
        maxAttackEnemy = enemigos.get(2).getPersonaje().getAtaque();

        enemigos.get(3).getPersonaje().setNombre("???");
        enemigos.get(3).getPersonaje().setAtaque(3);
        enemigos.get(3).getPersonaje().setDefensa(4);
        enemigos.get(3).getPersonaje().setVida(150);
        enemigos.get(3).getPersonaje().setVelocidad(15);
        enemigos.get(3).getPersonaje().getNivel().setLvl(1);
        maxAttackEnemy = enemigos.get(3).getPersonaje().getAtaque();

        enemigos.get(4).getPersonaje().setNombre("???");
        enemigos.get(4).getPersonaje().setAtaque(3);
        enemigos.get(4).getPersonaje().setDefensa(4);
        enemigos.get(4).getPersonaje().setVida(150);
        enemigos.get(4).getPersonaje().setVelocidad(15);
        enemigos.get(4).getPersonaje().getNivel().setLvl(1);
        maxAttackEnemy = enemigos.get(4).getPersonaje().getAtaque();

        enemigos.get(5).getPersonaje().setNombre("???");
        enemigos.get(5).getPersonaje().setAtaque(3);
        enemigos.get(5).getPersonaje().setDefensa(4);
        enemigos.get(5).getPersonaje().setVida(150);
        enemigos.get(5).getPersonaje().setVelocidad(15);
        enemigos.get(5).getPersonaje().getNivel().setLvl(1);
        maxAttackEnemy = enemigos.get(5).getPersonaje().getAtaque();

        enemigos.get(6).getPersonaje().setNombre("???");
        enemigos.get(6).getPersonaje().setAtaque(3);
        enemigos.get(6).getPersonaje().setDefensa(4);
        enemigos.get(6).getPersonaje().setVida(150);
        enemigos.get(6).getPersonaje().setVelocidad(15);
        enemigos.get(6).getPersonaje().getNivel().setLvl(1);
        maxAttackEnemy = enemigos.get(6).getPersonaje().getAtaque();

        enemigos.get(7).getPersonaje().setNombre("???");
        enemigos.get(7).getPersonaje().setAtaque(3);
        enemigos.get(7).getPersonaje().setDefensa(4);
        enemigos.get(7).getPersonaje().setVida(150);
        enemigos.get(7).getPersonaje().setVelocidad(15);
        enemigos.get(7).getPersonaje().getNivel().setLvl(1);
        maxAttackEnemy = enemigos.get(7).getPersonaje().getAtaque();

        enemigos.get(8).getPersonaje().setNombre("???");
        enemigos.get(8).getPersonaje().setAtaque(3);
        enemigos.get(8).getPersonaje().setDefensa(4);
        enemigos.get(8).getPersonaje().setVida(150);
        enemigos.get(8).getPersonaje().setVelocidad(15);
        enemigos.get(8).getPersonaje().getNivel().setLvl(1);
        maxAttackEnemy = enemigos.get(8).getPersonaje().getAtaque();

        enemigos.get(9).getPersonaje().setNombre("???");
        enemigos.get(9).getPersonaje().setAtaque(3);
        enemigos.get(9).getPersonaje().setDefensa(4);
        enemigos.get(9).getPersonaje().setVida(150);
        enemigos.get(9).getPersonaje().setVelocidad(15);
        enemigos.get(9).getPersonaje().getNivel().setLvl(1);
        maxAttackEnemy = enemigos.get(9).getPersonaje().getAtaque();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;
        hiloPantalla.setRunning(false); //Paramos, ya que si está pintando no podemos destruirlo

        while(retry){
            try{
                hiloPantalla.join();
                retry = false;
            }
            catch(InterruptedException e){

            }
        }

    }
}
