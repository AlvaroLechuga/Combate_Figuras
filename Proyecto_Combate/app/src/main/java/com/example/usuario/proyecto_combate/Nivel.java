package com.example.usuario.proyecto_combate;

public class Nivel {

    private int lvl;
    private int exp;
    private int requisitoUp = 200;

    public Nivel() {
    }

    public Nivel(int lvl, int exp, int requisitoUp) {
        this.lvl = lvl;
        this.exp = exp;
        this.requisitoUp = requisitoUp;
    }

    public boolean subirNivel(int expGanar){
        exp += expGanar;
        if(exp >= requisitoUp){
            exp -= requisitoUp;
            lvl++;
            requisitoUp = (requisitoUp * 2);
            return true;
        }else{
            return false;
        }
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }
}
