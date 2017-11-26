package com.taller.fiuber;

public class ListItem {

    private String head;
    private String desc;
    private String idPasajero;
    private String idViaje;

    public ListItem(String head, String desc, String idPasajero, String idViaje) {
        this.head = head;
        this.desc = desc;
        this.idPasajero = idPasajero;
        this.idViaje = idViaje;
    }

    public String getHead() {
        return head;
    }

    public String getDesc() {
        return desc;
    }

    public String getIdPasajero() {
        return idPasajero;
    }

    public String getIdViaje() {
        return idViaje;
    }
}
