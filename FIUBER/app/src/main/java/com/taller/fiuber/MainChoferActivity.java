package com.taller.fiuber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StreamDownloadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Pantalla principal para los usuarios de tipo chofer en la cual se van a mostrar todos los viajes
 * disponibles dandole la opción al chofer de aceptar o rechazar cada uno de ellos.
 */
public class MainChoferActivity extends AppCompatActivity {

    private static final String TAG = "MainChoferActivity";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItem> listItems;

    SharedServer sharedServer;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;
    NavigationView navigationView;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private BadgeDrawerArrowDrawable badgeDrawable;

    private Intent intentLocalizacion;

    private String usrID;
    private String IDPasajeroSeleccionado;
    private String IDViajeSeleccionado;

    private boolean viajeRechazado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Sacarle la barra de notificaciones
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_chofer);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                IDPasajeroSeleccionado = null;
                IDViajeSeleccionado = null;
                viajeRechazado = false;
            } else {
                IDPasajeroSeleccionado = extras.getString("IDPasajeroSeleccionado");
                IDViajeSeleccionado = extras.getString("IDViajeSeleccionado");
                viajeRechazado = extras.getBoolean("Rechazado");
            }
        } else {
            IDPasajeroSeleccionado = (String) savedInstanceState.getSerializable("IDPasajeroSeleccionado");
            IDViajeSeleccionado = (String) savedInstanceState.getSerializable("IDViajeSeleccionado");
            viajeRechazado = (boolean) savedInstanceState.getSerializable("Rechazado");
        }
        Log.v(TAG, "ID PASAJERO SELECCIONADO: "+ IDPasajeroSeleccionado);
        Log.v(TAG, "ID  VIAJE   SELECCIONADO: "+ IDViajeSeleccionado);

        //Iniciliazación sharedPref
        sharedServer = new SharedServer();
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //editorShared.clear();
        //editorShared.apply();

        //Almaceno el token/ID del usuario
        String strToken = sharedPref.getString("token", "noToken");
        Log.v(TAG, strToken);
        sharedServer.configurarTokenAutenticacion(strToken);
        usrID = sharedPref.getString("ID", "noID");

        //Almaceno el ID del pasajero seleccionado
        if((IDPasajeroSeleccionado != null)&&(IDViajeSeleccionado != null)){
            Log.v(TAG, "Rechazado: " + viajeRechazado);
            if(!viajeRechazado){
                Log.v(TAG, "Viaje aceptado");
                editorShared.putBoolean("viajeAceptado", true);
                editorShared.putString("pasajeroSeleccionado", IDPasajeroSeleccionado);
                editorShared.putString("viajeSeleccionado", IDViajeSeleccionado);
                editorShared.apply();
                aceptarViaje(usrID, IDViajeSeleccionado);
            } else {
                Log.v(TAG, "Viaje rechazado");
                rechazarViaje(usrID, IDViajeSeleccionado);
            }
        }

        //Comenzar servicio de localizacion
        intentLocalizacion = new Intent(this, LocationService.class);
        intentLocalizacion.putExtra("IDChofer", usrID);
        intentLocalizacion.putExtra("TokenChofer", strToken);
        startService(intentLocalizacion);

        //Suscribirse a un tópico de notificaciones
        //FirebaseMessaging.getInstance().subscribeToTopic("NEWSCHOFER");
        String idUser = sharedPref.getString("ID", "noID");
        FirebaseMessaging.getInstance().subscribeToTopic(idUser);

        //Prueba contador notificaciones
        //editorShared.putInt("mensajes", 10);
        //editorShared.apply();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();

        //Obtengo viajes
        boolean viajeEnCurso = sharedPref.getBoolean("viajeAceptado", false);
        if(viajeEnCurso){
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main_chofer);
            layout.setBackgroundResource(R.drawable.fondomaincurso);
        } else {
            obtenerPosiblesViajes();
        }

        /*
        for (int i = 0; i<10; i++){
            ListItem listItem = new ListItem("Heading"+(i+1), "Descripción");
            listItems.add(listItem);
        }*/

        adapter = new MyAdapter(listItems, this, IDPasajeroSeleccionado);
        recyclerView.setAdapter(adapter);

        //Menu de navegación lateral
        configurarMenuLateral();

        //Configuracion cantidad mensajes
        int cantMensajes = sharedPref.getInt("contadorMensajes", -1);
        Log.v(TAG, "Cantidad mensajes: "+ cantMensajes);
        if(cantMensajes!=-1){
            //setNavItemCount(R.id.nav_chat, 10);
            setNavItemCount(R.id.nav_chofer_chat, cantMensajes);
        } else {
            setNavItemCount(R.id.nav_chofer_chat, 0);
        }

        //Obtener autos
        obtenerAutos(usrID);
    }

    private void aceptarViaje(String idPasajeroSeleccionado, String idViajeSeleccionado) {
        sharedServer.aceptarViaje(idPasajeroSeleccionado, idViajeSeleccionado, new JSONCallback() {
            @Override
            public void ejecutar(JSONObject respuesta, long codigoServidor) {
                Log.v(TAG, "Respuesta aceptar viaje: "+respuesta);
                Log.v(TAG, "Codigo aceptar viaje   : "+codigoServidor);
            }
        });
    }

    private void rechazarViaje(String idPasajeroSeleccionado, String idViajeSeleccionado) {
        sharedServer.rechazarViaje(idPasajeroSeleccionado, idViajeSeleccionado, new JSONCallback() {
            @Override
            public void ejecutar(JSONObject respuesta, long codigoServidor) {
                Log.v(TAG, "Respuesta rechazar viaje: "+respuesta);
                Log.v(TAG, "Codigo rechazar viaje   : "+codigoServidor);
            }
        });
    }

    private void obtenerAutos(String strIDusr) {
        sharedServer.obtenerAutos(strIDusr, new JSONCallback() {
            @Override
            public void ejecutar(JSONObject respuesta, long codigoServidor) {
                Log.v(TAG, "Respuesta: "+ respuesta);
                String strAutos = "";

                Iterator<?> keys = respuesta.keys();
                while(keys.hasNext()){
                    String key = (String)keys.next();
                    try {
                        String modelo = respuesta.getJSONObject(key).optString("modelo");
                        strAutos = strAutos + modelo + ",";
                        String idAuto = respuesta.getJSONObject(key).optString("id");
                        Log.v(TAG, "Claves   :"+ respuesta.getJSONObject(key).optString("modelo"));
                        Log.v(TAG, "Claves   :"+ respuesta.getJSONObject(key).optString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                editorShared.putString("Autos", strAutos);
                Log.v(TAG, "Autos   :"+ strAutos);
                editorShared.apply();
            }
        });
    }

    private void procesarViajes() {
        String viajesGuardados = sharedPref.getString("posiblesViajes","noViajes");
        String[] listaViajes = viajesGuardados.split(";");
        for (String viaje : listaViajes) {
            String[] viajes = viaje.split(",");
            int metros = Integer.parseInt(viajes[2]);
            float km = (float) metros / 1000;
            ListItem listItem = new ListItem(viajes[1], viajes[0]+"$  ---  "+km+" km", viajes[3], viajes[4]);
            listItems.add(listItem);
            adapter.notifyDataSetChanged();
        }
    }

    private void obtenerPosiblesViajes() {
        sharedServer.obtenerViajes(usrID, new JSONCallback() {
            @Override
            public void ejecutar(JSONObject respuesta, long codigoServidor) {
                String strViajes = "";
                Log.v(TAG, "Respuesta Viajes: "+respuesta);
                Log.v(TAG, "Codigo Viajes   : "+codigoServidor);

                Iterator<?> keys = respuesta.keys();
                while(keys.hasNext()){
                    String key = (String)keys.next();
                    try {
                        String costo = respuesta.getJSONObject(key).optString("costo");
                        strViajes = strViajes + costo + ",";
                        String nombreUsuario = respuesta.getJSONObject(key).getJSONObject("datosPasajero").optString("nombreUsuario");
                        strViajes = strViajes + nombreUsuario + ",";
                        String distancia = respuesta.getJSONObject(key).getJSONObject("ruta").optString("distancia");
                        strViajes = strViajes + distancia + ",";
                        String idUsuario = respuesta.getJSONObject(key).getJSONObject("datosPasajero").optString("idPasajero");
                        strViajes = strViajes + idUsuario + ",";
                        String idViaje = respuesta.getJSONObject(key).optString("idViaje");
                        strViajes = strViajes + idViaje + ";";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(strViajes.isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.sin_viajes, Toast.LENGTH_SHORT).show();
                } else {
                    editorShared.putString("posiblesViajes", strViajes);
                    Log.v(TAG, "Viajes   :"+ strViajes);
                    editorShared.apply();
                    procesarViajes();
                }
            }
        });
    }

    private void setNavItemCount(int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView();
        view.setText(count > 0 ? String.valueOf(count) : null);
    }

    private void configurarNotificaciones(){

        badgeDrawable.setText("");

        //Para cuando no hay notificaciones
        //int cantMsj = sharedPref.getInt("mensajes", -1);
        int cantMsj = sharedPref.getInt("contadorMensajes", -1);
        Log.v(TAG, "Mensajes: "+cantMsj);
        badgeDrawable.setEnabled(false);
        if(cantMsj == -1){
            badgeDrawable.setEnabled(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Configura el el menu lateral desplegable y identifica que acción realizar al clickear cada uno
     * de los botones que lo componen.
     */
    public void configurarMenuLateral(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutChofer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        badgeDrawable = new BadgeDrawerArrowDrawable(getSupportActionBar().getThemedContext());

        toggle.setDrawerArrowDrawable(badgeDrawable);
        badgeDrawable.setText("");
        configurarNotificaciones();

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.nav_menu_chofer);

        //Esconder el boton de mensaje hasta que se confirme un viaje
        hideChat();

        if(sharedPref.getBoolean("viajeAceptado", false)){
            showChat();
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_chofer_account:
                        Log.v(TAG, "Perfil clikeado");
                        goProfile();
                        return true;
                    case R.id.nav_chofer_settings:
                        Log.v(TAG, "Configuración clikeado");
                        return true;
                    case R.id.nav_chofer_logout:
                        LoginManager.getInstance().logOut();
                        Log.v(TAG, "Cerrar sesión clikeado");
                        boolean viajeEnCurso = sharedPref.getBoolean("viajeAceptado", false);
                        if(!viajeEnCurso){
                            editorShared.clear();
                            editorShared.apply();
                            stopService(intentLocalizacion);
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(usrID);
                            goLogin();
                        } else {
                            Toast.makeText(getApplicationContext(), "Viaje en curso", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.nav_chofer_autos:
                        Log.v(TAG, "Autos clikeado");
                        String autosAlmacenados = sharedPref.getString("Autos", "");
                        if(autosAlmacenados.isEmpty()){
                            goAñadirAuto();
                        } else {
                            goCars();
                        }
                        return true;
                    case R.id.nav_chofer_chat:
                        editorShared.putInt("mensajes", 0);
                        editorShared.apply();
                        configurarNotificaciones();
                        setNavItemCount(R.id.nav_chofer_chat, 0);
                        goChat();
                        return true;
                }
                return false;
            }
        });
    }

    private void hideChat()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_chofer_chat).setVisible(false);
    }

    private void showChat()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_chofer_chat).setVisible(true);
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void goCars() {
        Intent intent = new Intent(this, CarsActivity.class);
        startActivity(intent);
    }

    private void goAñadirAuto() {
        Intent intent = new Intent(this, RegisterCarActivity.class);
        startActivity(intent);
    }

    private void goChat() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}
