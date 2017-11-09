package com.taller.fiuber;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Clase para interactuar con el APP Server mediante llamadas HTTP de GET/POST/PUT que están implementadas
 * en InterfazRest
 */
public class SharedServer extends InterfazRest {

    private static final String TAG = "SharedServer";

    private long id;

    private static final String URLAPIREST = "https://fiuberappserver.herokuapp.com";

    public SharedServer()
    {
        id = 0;
    }

    /**
     * A partir del envío de un JSON conteniendo usuario y contraseña devuelve un token que va a estar
     * asociado a la sesión de dicho usuario. De no existir el usuario o de ser incorrecta la contraseña
     * deberá devolver un código de error.
     */
    public void obtenerToken(String usuario, String contrasena, JSONCallback callback)
    {
        JSONObject json = new JSONObject();

        try {
            json.put("nombreUsuario", usuario);
            json.put("contrasena",contrasena);
            //json.put("tokenFacebook", "tokenFace");
        }
        catch(JSONException e)
        {

        }
        Log.v(TAG, "URL: "+URLAPIREST+"/token");
        String str = json.toString();
        Log.v(TAG, "JSON: "+ str);
        enviarPOST(URLAPIREST+"/token",json,callback);
    }

    /**
     * Envía un JSON con todos los datos del usuario para darlo de alta en la base de datos del Server.
     * Si el nombre de usuario ya existe deberá enviar un código de error.
     */
    public void darAltaUsuario(String tipo, String usuario, String contraseña, String mail, String nombre, String apellido, String cuentaFacebook, String nacionalidad, String fechaNacimiento, JSONCallback callback)
    {
        JSONObject jsonUsuario = new JSONObject();

        try {
            jsonUsuario.put("_ref", "649.7872072956419");
            jsonUsuario.put("type", tipo);
            jsonUsuario.put("username", usuario);
            jsonUsuario.put("password", contraseña);
            //Ver tema facebook cono la API del APP Server
            //jsonUsuario.put("fb",
            //        new JSONArray()
            //                .put(new JSONObject()
            //                        .put("userId", "FACE ID")
            //                        .put("authToken", "FACE TOKEN")
            //                )
            //);
            // ---FIN FACE---
            jsonUsuario.put("firstName", nombre);
            jsonUsuario.put("lastName", apellido);
            jsonUsuario.put("country", "Argentina");
            jsonUsuario.put("email", mail);
            jsonUsuario.put("birthdate", fechaNacimiento);
            jsonUsuario.put("images", "IMAGEN");
        }
        catch(JSONException e)
        {

        }
        Log.v(TAG, "URL: "+URLAPIREST+"/users");
        String str = jsonUsuario.toString();
        Log.v(TAG, "JSON: "+ str);
        enviarPOST(URLAPIREST+"/users",jsonUsuario,callback);
    }

    /**
     * Modifica alguno o todos los datos de un usuario ya existente a partir del envío de un JSON al Server.
     */
    public void modificarUsuario(String idUsr, String tipoUsuario, String usuario, String contraseña, String mail, String nombre, String apellido, String cuentaFacebook, String nacionalidad, String fechaNacimiento, JSONCallback callback)
    {
        JSONObject jsonUsuario = new JSONObject();

        try {
            jsonUsuario.put("type", tipoUsuario);
            jsonUsuario.put("username", usuario);
            jsonUsuario.put("password", contraseña);
            //Ver tema facebook cono la API del APP Server
            //jsonUsuario.put("fb",
            //        new JSONArray()
            //                .put(new JSONObject()
            //                        .put("userId", "FACE ID")
            //                        .put("authToken", "FACE TOKEN")
            //                )
            //);
            // ---FIN FACE---
            jsonUsuario.put("firstName", nombre);
            jsonUsuario.put("lastName", apellido);
            jsonUsuario.put("country", nacionalidad);
            jsonUsuario.put("email", mail);
            jsonUsuario.put("birthdate", fechaNacimiento);
            jsonUsuario.put("image", "IMAGEN");
        }
        catch(JSONException e)
        {

        }
        Log.v(TAG, "URL: "+URLAPIREST+"/users/"+idUsr);
        String str = jsonUsuario.toString();
        Log.v(TAG, "JSON: "+ str);
        enviarPUT(URLAPIREST+"/users/"+idUsr,jsonUsuario,callback);
    }

    /**
     * Obtiene toda la información asociada a un usuario específico que se encuentre almacenada en el
     * servidor.
     */
    public void obtenerDatosUsrServidor(String idUsr, JSONCallback callback)
    {
        Log.v(TAG, "JSON: "+ idUsr);
        enviarGET(URLAPIREST+"/users/"+idUsr,callback);
    }

    public void darAltaAuto(String idUsr, String modelo, String color, String patente, String anio, String estado, String aire, String musica, JSONCallback callback)
    {
        JSONObject jsonAuto = new JSONObject();

        try {
            jsonAuto.put("modelo", modelo);
            jsonAuto.put("color", color);
            jsonAuto.put("patente", patente);
            jsonAuto.put("anio", anio);
            jsonAuto.put("estado", estado);
            jsonAuto.put("aireAcondicionado", aire);
            jsonAuto.put("musica", musica);

        }
        catch(JSONException e)
        {

        }
        Log.v(TAG, "URL: "+URLAPIREST+"/driver/"+idUsr+"/cars");
        String str = jsonAuto.toString();
        Log.v(TAG, "JSON: "+ str);
        enviarPOST(URLAPIREST+"/driver/"+idUsr+"/cars",jsonAuto,callback);
    }

}
