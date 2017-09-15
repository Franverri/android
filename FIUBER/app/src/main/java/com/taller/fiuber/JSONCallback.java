package com.taller.fiuber;

import org.json.JSONObject;

public abstract class JSONCallback {
    public abstract void ejecutar(JSONObject respuesta, long codigoServidor);
}