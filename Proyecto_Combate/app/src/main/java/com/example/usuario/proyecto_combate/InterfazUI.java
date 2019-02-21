package com.example.usuario.proyecto_combate;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class InterfazUI {

    private Float x;
    private Float y;
    private Float ancho;
    private Float alto;

    public InterfazUI() {

    }

    public InterfazUI(Float x, Float y, Float ancho, Float alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getAncho() {
        return ancho;
    }

    public void setAncho(Float ancho) {
        this.ancho = ancho;
    }

    public Float getAlto() {
        return alto;
    }

    public void setAlto(Float alto) {
        this.alto = alto;
    }

    public void draw(Canvas canvas){
        Paint p= new Paint();
        p.setColor(Color.parseColor("#002451CF"));
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(getX(),getY(),
                getAncho()+ getX() ,getAlto()+getY(),p);
    }

    public boolean comprobarSiTocoDentro(Float x, Float y){

        boolean devolver= false;

        if(x>getX() && x< getX()+getAncho()
                && y>getY() && y<getY()+getAlto()){

            devolver= true;

        }

        return devolver;
    }
}
