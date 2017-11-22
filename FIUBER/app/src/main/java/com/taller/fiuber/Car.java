package com.taller.fiuber;

public class Car {

    String modelo;
    Integer imagen;
    String estado;
    String musica;
    String año;
    String idChofer;

    public Car(String modelo, Integer imagen) {
        this.modelo = modelo;
        this.imagen = imagen;
    }

    public Car(String modelo, Integer imagen, String estado, String musica, String año, String idChofer) {
        this.modelo = modelo;
        this.imagen = imagen;
        this.estado = estado;
        this.musica = musica;
        this.año = año;
        this.idChofer = idChofer;
    }

    public String getModelo() {
        return modelo;
    }

    public Integer getImagen() {
        return imagen;
    }

    public String getEstado() {
        return estado;
    }

    public String getMusica() {
        return musica;
    }

    public String getAño() {
        return año;
    }

    public String getIdChofer() {
        return idChofer;
    }
}
