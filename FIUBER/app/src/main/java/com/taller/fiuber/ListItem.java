package com.taller.fiuber;

public class ListItem {

    private String head;
    private String desc;
    private String idPasajero;

    public ListItem(String head, String desc, String idPasajero) {
        this.head = head;
        this.desc = desc;
        this.idPasajero = idPasajero;
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
}
