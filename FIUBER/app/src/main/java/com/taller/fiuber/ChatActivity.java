package com.taller.fiuber;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                if (input.getText().toString().trim().equals("")) {
                    Toast.makeText(ChatActivity.this, "Ingrese el mensaje", Toast.LENGTH_SHORT).show();
                } else {
                    if(tipoUsr.equals("driver")){
                        //Faltaría tener un "pasajeroAsignado" en el sharedPref
                        ponerMensajeFirebase(IDUsuario, "7", IDUsuario, nombreUsuario);
                    } else {
                        //Faltaría tener un "pasajeroAsignado" en el sharedPref
                        ponerMensajeFirebase(IDUsuario, IDUsuario, "6", nombreUsuario);
                    }
                }
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
}
