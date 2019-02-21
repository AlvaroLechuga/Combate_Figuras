package com.example.usuario.proyecto_combate;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Sprite {

    private Personaje personaje;
    private static final int BMP_COLUMS = 3;
    private static final int BMP_ROWS = 4;
    private int x;
    private int y;
    private int xSpeed = 7;
    private int ySpeed = 7;
    private Juego gameView;
    private Bitmap bmp;
    private int currentFrame = 0;
    private int width;
    private int height;
    private boolean ataca;
    private boolean enemigo;
    //direction = 0 up, 1 left, 2 down, 3 right
    //animation = 3 up, 1 left, 0 down, 2 right
    int [] DIRECTION_TO_ANIMATION_MAP = {3, 1, 0, 2};

    public Sprite(Juego gameView, Bitmap bmp, int X, int Y) {
        this.gameView = gameView;
        this.bmp = bmp;
        this.width = bmp.getWidth() / BMP_COLUMS;
        this.height = bmp.getHeight() / BMP_ROWS;
        this.x = X;
        this.y = Y;
        personaje = new Personaje();
    }

    public Sprite() {
    }

    public Personaje getPersonaje() {
        return personaje;
    }

    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;
    }

    public void setAtaca(boolean ataca){
        this.ataca = ataca;
    }

    public boolean isAtaca() {
        return ataca;
    }

    private void update(){
        currentFrame = ++currentFrame % BMP_COLUMS;
    }

    public void onDraw(Canvas canvas){

        update();

        if(enemigo){
            int srcX = currentFrame * width;
            int srcY = 1 * height;
            Rect src  = new Rect(srcX, srcY,  srcX + width, srcY + height);
            Rect dst = new Rect(x, y, x+width, y+height);
            canvas.drawBitmap(bmp, src, dst, null);
        }else{
            int srcX = currentFrame * width;
            int srcY = getAnimationRow() * height;
            Rect src  = new Rect(srcX, srcY,  srcX + width, srcY + height);
            Rect dst = new Rect(x, y, x+width, y+height);
            canvas.drawBitmap(bmp, src, dst, null);
        }

    }

    private int getAnimationRow() {
        double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
        int direction = (int) Math.round(dirDouble) % BMP_ROWS;
        return DIRECTION_TO_ANIMATION_MAP[direction];
    }

    public boolean isEnemigo() {
        return enemigo;
    }

    public void setEnemigo(boolean enemigo) {
        this.enemigo = enemigo;
    }

    public int getY() {
        return y;
    }
}
