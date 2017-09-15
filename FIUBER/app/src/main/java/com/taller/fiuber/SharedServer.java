package com.taller.fiuber;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SharedServer extends InterfazRest {

    private long id;

    private static final String URLAPIREST = "https://fiuberappserver.herokuapp.com";

    public SharedServer()
    {
        id = 0;
    }

    public void obtenerToken(String usuario, String contrasena, JSONCallback callback)
    {
        JSONObject json = new JSONObject();

        try {
            json.put("nombreUsuario", usuario);
            json.put("contraseña",contrasena);
        }
        catch(JSONException e)
        {

        }
        Log.v("URL", URLAPIREST+"/token");
        String str = json.toString();
        Log.v("JSON", str);
        enviarPOST(URLAPIREST+"/token",json,callback);
    }

}
