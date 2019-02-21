package com.example.usuario.proyecto_combate;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class HiloPantalla extends Thread {

    private SurfaceHolder sh;
    private boolean run;
    static final long FPS = 5;
    private Juego view;

    public HiloPantalla(SurfaceHolder sh, Juego view){
        this.sh = sh;
        this.view = view;
        this.run = false;
    }

    public void setRunning(boolean run){
        this.run = run;
    }

    public void run(){
        Canvas canvas;

        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;

        while(run){ //Habilita la edición multiple
            canvas = null;
            startTime = System.currentTimeMillis();
            try{
                canvas = sh.lockCanvas(null); //Intentamos bloquear el getHolder()

                if(canvas != null) { //Comprueba que el canvas no sea nulo
                    synchronized (sh) { //Solo para nosotros
                        view.postInvalidate(); //Pinta
                    }
                }
            }
            finally{
                if(canvas != null)
                    sh.unlockCanvasAndPost(canvas); //Desbloqueamos el canvas
            }
            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);

            try{
                if(sleepTime > 0){
                    sleep(sleepTime);
                }else{
                    sleep(10);
                }
            }catch (Exception e){}
        }

    }

}
