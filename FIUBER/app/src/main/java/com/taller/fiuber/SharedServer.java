package com.taller.fiuber;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SharedServer extends InterfazRest {

    private static final String TAG = "SharedServer";

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
            json.put("contrasena",contrasena);
        }
        catch(JSONException e)
        {

        }
        Log.v(TAG, "URL: "+URLAPIREST+"/token");
        String str = json.toString();
        Log.v(TAG, "JSON: "+ str);
        enviarPOST(URLAPIREST+"/token",json,callback);
    }

    public void darAltaUsuario(String tipo, String usuario, String contraseña, String mail, String nombre, String apellido, String cuentaFacebook, String nacionalidad, String fechaNacimiento, JSONCallback callback)
    {
        JSONObject jsonUsuario = new JSONObject();

        try {
            jsonUsuario.put("type", tipo);
            jsonUsuario.put("username", usuario);
            jsonUsuario.put("password", contraseña);
            //Ver tema facebook cono la API del APP Server
            jsonUsuario.put("fb",
                    new JSONArray()
                            .put(new JSONObject()
                                    .put("userId", "FACE ID")
                                    .put("authToken", "FACE TOKEN")
                            )
            );
            // ---FIN FACE---
            jsonUsuario.put("firstName", nombre);
            jsonUsuario.put("lastName", apellido);
            jsonUsuario.put("country", "Argentina");
            jsonUsuario.put("email", mail);
            jsonUsuario.put("birthdate", fechaNacimiento);
            //jsonUsuario.put("image", "IMAGEN");
        }
        catch(JSONException e)
        {

        }
        Log.v(TAG, "URL: "+URLAPIREST+"/users");
        String str = jsonUsuario.toString();
        Log.v(TAG, "JSON: "+ str);
        enviarPOST(URLAPIREST+"/users",jsonUsuario,callback);
    }

}
