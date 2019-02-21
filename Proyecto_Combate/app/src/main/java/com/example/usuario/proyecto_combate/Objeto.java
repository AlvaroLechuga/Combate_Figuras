package com.example.usuario.proyecto_combate;

public class Objeto {

    private String nombre;
    private int cantidad;
    private String descripcion;

    public Objeto() {
    }

    public Objeto(String nombre, int cantidad, String descripcion) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre+": "+cantidad+" =>  "+descripcion;
    }
}
