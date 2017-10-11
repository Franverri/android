package com.taller.fiuber;

public class Car {

    String modelo;
    Integer imagen;

    public Car(String modelo, Integer imagen) {
        this.modelo = modelo;
        this.imagen = imagen;
    }

    public String getModelo() {
        return modelo;
    }

    public Integer getImagen() {
        return imagen;
    }
}
