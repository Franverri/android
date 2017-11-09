package com.taller.fiuber;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Encargada de implementar todas las conexiones directas con el APP Server
 */
abstract public class InterfazRest {

    private long id;

    private static final String URLAPIREST = "https://fiuberappserver.herokuapp.com/";

    String token;

    public InterfazRest()
    {
        token = new String("");
    }

    protected void configurarTokenAutenticacion(String token)
    {
        this.token = token;
    }


    /**
     * Envía un JSON con datos a la URL pasada como parámetro (que será la del APP Server) mediante un POST
     * para luego almacenar la respuesta recibida en otro JSON (callback) que finalmente sera procesado.
     */
    protected void enviarPOST(final String URL, final JSONObject json, final JSONCallback callback)
    {
        class POST extends AsyncTask<String,Integer,JSONObject> {

            long codigoServidor;

            protected JSONObject doInBackground(String... params) {

                JSONObject result;

                HttpClient httpClient = new DefaultHttpClient();

                HttpPost post = new HttpPost(URL);

                post.setHeader("Content-type", "application/json");
                if(!token.isEmpty()){
                    post.setHeader("Authorization","Bearer "+token);
                    Log.v("INTERFAZ", "TOKEN: "+ token);
                }


                try
                {
                    //Configura post para que envie el json.

                    Log.v("InterfazRest", "JSON enviado: "+json.toString());

                    StringEntity entidad = new StringEntity(json.toString());
                    post.setEntity(entidad);

                    //Envio y espero la peticion.
                    HttpResponse resp = httpClient.execute(post);
                    String respStr = EntityUtils.toString(resp.getEntity());
                    codigoServidor = resp.getStatusLine().getStatusCode();

                    Log.v("InterfazRest", "Respuesta server: "+respStr);
                    String codStr = Long.toString(codigoServidor);
                    Log.v("InterfazRest", "Codigo server: "+codStr);

                    result = new JSONObject(respStr);
                }
                catch(Exception ex)
                {
                    result = new JSONObject();
                }

                return result;
            }

            /**
             * Método en el cual se procede a ejecutar/procesar la respuesta del APP Server
             */
            protected void onPostExecute(JSONObject result) {
                callback.ejecutar(result, codigoServidor);
            }
        }

        POST peticion = new POST();

        peticion.execute();
    }

    /**
     * Envía un JSON con datos a la URL pasada como parámetro (que será la del APP Server) mediante un GET
     * para luego almacenar la respuesta recibida en otro JSON (callback) que finalmente sera procesado.
     */
    protected void enviarGET(final String URL, final JSONCallback callback)
    {
        class GET extends AsyncTask<String,Integer,JSONObject> {

            long codigoServidor;

            protected JSONObject doInBackground(String... params) {

                JSONObject result;

                HttpClient httpClient = new DefaultHttpClient();

                HttpGet get = new HttpGet(URL);

                get.setHeader("content-type", "application/json");

                //if(!token.isEmpty())
                //    get.setHeader("Authorization:", "Bearer " + token);


                try
                {
                    //Envio y espero la peticion.
                    HttpResponse resp = httpClient.execute(get);
                    String respStr = EntityUtils.toString(resp.getEntity());
                    codigoServidor = resp.getStatusLine().getStatusCode();

                    Log.v("InterfazRest", "Respuesta server: "+respStr);
                    String codStr = Long.toString(codigoServidor);
                    Log.v("InterfazRest", "Codigo server: "+codStr);

                    result = new JSONObject(respStr);
                }
                catch(Exception ex)
                {
                    result = new JSONObject();
                }

                return result;
            }

            /**
             * Método en el cual se procede a ejecutar/procesar la respuesta del APP Server
             */
            protected void onPostExecute(JSONObject result) {
                callback.ejecutar(result, codigoServidor);
            }
        }

        GET peticion = new GET();

        peticion.execute();
    }

    /**
     * Envía un JSON con datos a la URL pasada como parámetro (que será la del APP Server) mediante un PUT
     * para luego almacenar la respuesta recibida en otro JSON (callback) que finalmente sera procesado.
     */
    protected void enviarPUT(final String URL, final JSONObject json, final JSONCallback callback)
    {
        class PUT extends AsyncTask<String,Integer,JSONObject> {

            long codigoServidor;

            protected JSONObject doInBackground(String... params) {

                JSONObject result;

                HttpClient httpClient = new DefaultHttpClient();

                HttpPut put = new HttpPut(URL);

                put.setHeader("content-type", "application/json");

                //if(!token.isEmpty())
                //    put.setHeader("Authorization:", "Basic "+token);

                try
                {
                    //Configura post para que envie el json.
                    StringEntity entidad = new StringEntity(json.toString());
                    put.setEntity(entidad);

                    //Envio y espero la peticion.
                    HttpResponse resp = httpClient.execute(put);
                    String respStr = EntityUtils.toString(resp.getEntity());
                    codigoServidor = resp.getStatusLine().getStatusCode();

                    Log.v("InterfazRest", "Respuesta server: "+respStr);
                    String codStr = Long.toString(codigoServidor);
                    Log.v("InterfazRest", "Codigo server: "+codStr);

                    result = new JSONObject(respStr);
                }
                catch(Exception ex)
                {
                    result = new JSONObject();
                }

                return result;
            }

            /**
             * Método en el cual se procede a ejecutar/procesar la respuesta del APP Server
             */
            protected void onPostExecute(JSONObject result) {
                callback.ejecutar(result, codigoServidor);
            }
        }

        PUT peticion = new PUT();

        peticion.execute();
    }
}