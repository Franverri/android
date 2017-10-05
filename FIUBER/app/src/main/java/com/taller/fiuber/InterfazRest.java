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


    protected void enviarPOST(final String URL, final JSONObject json, final JSONCallback callback)
    {
        class POST extends AsyncTask<String,Integer,JSONObject> {

            long codigoServidor;

            protected JSONObject doInBackground(String... params) {

                JSONObject result;

                HttpClient httpClient = new DefaultHttpClient();

                HttpPost post = new HttpPost(URL);

                post.setHeader("Content-type", "application/json");

                //if(!token.isEmpty())
                //   post.setHeader("Authorization:","Basic "+token);

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

            protected void onPostExecute(JSONObject result) {
                callback.ejecutar(result, codigoServidor);
            }
        }

        POST peticion = new POST();

        peticion.execute();
    }

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
                //    get.setHeader("Authorization:", "Basic " + token);


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

            protected void onPostExecute(JSONObject result) {
                callback.ejecutar(result, codigoServidor);
            }
        }

        GET peticion = new GET();

        peticion.execute();
    }

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

            protected void onPostExecute(JSONObject result) {
                callback.ejecutar(result, codigoServidor);
            }
        }

        PUT peticion = new PUT();

        peticion.execute();
    }
}