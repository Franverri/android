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
            json.put("contrasena",contrasena);
        }
        catch(JSONException e)
        {

        }
        Log.v("URL", URLAPIREST+"/token");
        String str = json.toString();
        Log.v("JSON", str);
        enviarPOST(URLAPIREST+"/token",json,callback);
    }

    public void darAltaChofer(String usuario, String contraseña, String mail, String nombre, String apellido, String cuentaFacebook, String modeloAuto, String colorAuto, String patenteAuto, String añoAuto, String musicaAuto, String estadoAuto, boolean aireAuto, JSONCallback callback)
    {
        JSONObject jsonUsuario = new JSONObject();
        JSONObject jsonAuto = new JSONObject();

        try {
            jsonUsuario.put("type", "driver");
            jsonUsuario.put("username", usuario);
            jsonUsuario.put("password", contraseña);
            //Ver tema facebook cono la API del APP Server
            jsonUsuario.put("firstName", nombre);
            jsonUsuario.put("lastName", apellido);
            //Ver si es necesaria la nacionalidad (De ser así agregar a la pantalla de registro)
            jsonUsuario.put("email", mail);
            jsonUsuario.put("username", usuario);
            //Ver si es necesaria la fecha de nacimiento (De ser así agregar a la pantalla de registro)
            //Ver tema imagen



        }
        catch(JSONException e)
        {

        }
        enviarPOST(URLAPIREST+"user/",jsonUsuario,callback);
    }

}
