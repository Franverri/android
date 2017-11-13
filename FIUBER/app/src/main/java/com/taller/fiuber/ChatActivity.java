package com.taller.fiuber;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private SharedServer sharedServer;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editorShared;

    private FirebaseListAdapter<ChatMessage> adapter;
    private ListView listView;
    private String nombreUsuario = "";
    private String IDUsuario = "";
    private String tipoUsr = "";

    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sharedServer = new SharedServer();
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Obtengo el usuario logueado
        nombreUsuario = sharedPref.getString("usuario", null);
        IDUsuario = sharedPref.getString("ID", null);
        tipoUsr = sharedPref.getString("tipo",null);

        //find views by Ids
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        input = (EditText) findViewById(R.id.input);
        listView = (ListView) findViewById(R.id.list);

        if(tipoUsr.equals("driver")){
            showAllOldMessages("7", IDUsuario);
        } else {
            showAllOldMessages(IDUsuario, "6");
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensajeIngresado = input.getText().toString();
                if (input.getText().toString().trim().equals("")) {
                    Toast.makeText(ChatActivity.this, "Ingrese el mensaje", Toast.LENGTH_SHORT).show();
                } else {
                    if(tipoUsr.equals("driver")){
                        //Faltaría tener un "pasajeroAsignado" en el sharedPref
                        ponerMensajeFirebase(IDUsuario, "7", IDUsuario, nombreUsuario);
                        enviarNotificacion(nombreUsuario, "7", mensajeIngresado);
                    } else {
                        //Faltaría tener un "pasajeroAsignado" en el sharedPref
                        ponerMensajeFirebase(IDUsuario, IDUsuario, "6", nombreUsuario);
                        enviarNotificacion(nombreUsuario, "6", mensajeIngresado);
                    }
                }
            }
        });

    }

    private void enviarNotificacion(String nombreUsuario, String IDDestino, String mensaje) {

        String body = "{\"to\": \"/topics/" + IDDestino +"\", \"notification\": {\"title\": \"" + nombreUsuario+ "\", \"text\": \"" + mensaje + "\", \"click_action\": \"CHATACTIVITY\" } }";

        enviarPOST("https://fcm.googleapis.com/fcm/send", body, new JSONCallback() {
            @Override
            public void ejecutar(JSONObject respuesta, long codigoServidor) {
                Log.v(TAG, "Codigdo servidor: "+ codigoServidor);
                String str = respuesta.toString();
                Log.v(TAG, "JSON: "+ str);
            }
        });
    }

    private void ponerMensajeFirebase(String idUsuario, String idPasajero, String idChofer, String nombreUsr) {
        FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://fiuber-177714.firebaseio.com/"+idPasajero+idChofer)
                .push()
                .setValue(new ChatMessage(input.getText().toString(),
                        nombreUsr,
                        idUsuario)
                );
        input.setText("");
    }

    private void showAllOldMessages(String idOrigen, String idDestino) {
        adapter = new MessageAdapter(this, ChatMessage.class, R.layout.item_in_message,
                FirebaseDatabase.getInstance().getReferenceFromUrl("https://fiuber-177714.firebaseio.com/"+idOrigen+idDestino));
        listView.setAdapter(adapter);
    }

    public String getLoggedInUserName() {
        return IDUsuario;
    }

    @Override
    public void onBackPressed() {
        if(Objects.equals(sharedPref.getString("tipo", null), "driver")){
            goMainChofer();
        } else {
            goMainPasajero();
        }
    }

    /**
     * Transiciona la APP hacía la pantalla principal para un usuario de tipo pasajero.
     */
    private void goMainPasajero() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Transiciona la APP hacía la pantalla principal para un usuario de tipo chofer.
     */
    private void goMainChofer() {
        Intent intent = new Intent(this, MainChoferActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected void enviarPOST(final String URL, final String body, final JSONCallback callback)
    {
        class POST extends AsyncTask<String,Integer,JSONObject> {

            long codigoServidor;

            protected JSONObject doInBackground(String... params) {

                JSONObject result;

                HttpClient httpClient = new DefaultHttpClient();

                HttpPost post = new HttpPost(URL);

                post.setHeader("Content-type", "application/json");
                post.setHeader("Authorization","key=AAAAIqy7cgs:APA91bFJ1BC7rlvrQKoQNcpubZqxg_jVy1rgSH0pWxGC6Z_yN_RUAmyduc5S9j2xcC7UeLT5fy2L9bm2HGtvzYhn7daWFJgalLBxtz7ID73KprwZhQXBmZcEd05d7k_cXftN_YVifStn");

                try
                {
                    //Configura post para que envie el json.

                    Log.v("InterfazRest", "JSON enviado: "+body);

                    StringEntity entidad = new StringEntity(body);
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
}
