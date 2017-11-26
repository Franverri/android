package com.taller.fiuber;

import android.*;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Pantalla principal para los usuarios de tipo pasajero en la cual se puede solicitar un viaje indicando
 * dirección de origen y de destino. La ruta se mostrará en el mapa de la pantalla.
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private Button btnFindPath;
    private Button btnConfirmarViaje;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private String strOrigen;
    private String strDestino;
    private String duracionViaje;
    private String kilometrosViaje;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button btnUbicacion;
    private double latitud;
    private double longitud;
    private boolean rutaBuscada;

    SharedServer sharedServer;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;
    NavigationView navigationView;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private BadgeDrawerArrowDrawable badgeDrawable;

    private String IDUsr;
    ProgressDialog myDialog;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Sacarle la barra de notificaciones
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_maps);

        //Iniciliazación sharedPref
        sharedServer = new SharedServer();
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //inicializo bool
        rutaBuscada = false;

        //Almaceno el token del usuario
        String strToken = sharedPref.getString("token", "noToken");
        sharedServer.configurarTokenAutenticacion(strToken);

        //Suscribirse a un tópico de notificaciones
        FirebaseMessaging.getInstance().subscribeToTopic("NEWSPASAJERO");
        IDUsr = sharedPref.getString("ID", "noID");
        FirebaseMessaging.getInstance().subscribeToTopic(IDUsr);

        //Prueba contador notificaciones
        editorShared.putInt("mensajes", 10);
        editorShared.apply();

        //Veo si el viaje esta pedido
        verificarPedidoViaje();

        //Menu de navegación lateral
        configurarMenuLateral();

        //CAMBIAR ESTA HARDCODEADO
        setNavItemCount(R.id.nav_chat, 10);

        //Permisos de localizacion
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (permissionGranted) {
            Log.v(TAG, "Ya tiene los permisos de localización necesarios");
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            Log.v(TAG, "Se le otrogaron los permisos de localización necesarios");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnFindPath = (Button) findViewById(R.id.btnFindPath);

        //AUTO Complete Origen
        PlaceAutocompleteFragment autocompleteFragmentOrigen = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_origen);
        autocompleteFragmentOrigen.setHint("Ingrese la dirección de origen");

        autocompleteFragmentOrigen.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.v(TAG, "Origen: " + place.getName());
                Log.v(TAG, "Address: " + place.getAddress());
                strOrigen = place.getAddress().toString();
                LatLng queriedLocation = place.getLatLng();
                Log.v(TAG, "Latitude is : " + queriedLocation.latitude);
                Log.v(TAG, "Longitude is: " + queriedLocation.longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.e(TAG, "An error occurred: " + status);
            }
        });

        //AUTO Complete Destino
        PlaceAutocompleteFragment autocompleteFragmentDestino = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_destino);
        autocompleteFragmentDestino.setHint("Ingrese la dirección de destino");

        autocompleteFragmentDestino.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.v(TAG, "Destino: " + place.getName());
                Log.v(TAG, "Address: " + place.getAddress());
                strDestino = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.e(TAG, "An error occurred: " + status);
            }
        });

        //Click en el boton de "Confirmar viaje"
        configurarConfirmarViaje();

        //Click en el boton de "Buscar Ruta"
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editorShared.putString("origen", strOrigen);
                editorShared.putString("destino", strDestino);
                editorShared.apply();
                sendRequest(strOrigen, strDestino);
            }
        });

        //Mi ubicación
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                LatLng ubicacion = new LatLng(latitud, longitud);
                Log.v(TAG, "Latitud: "+location.getLatitude() + " Longitud: "+ location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15));
                sharedServer.modificarPosicionPasajero(IDUsr, longitud, latitud, new JSONCallback() {
                    @Override
                    public void ejecutar(JSONObject respuesta, long codigoServidor) {
                        Log.v(TAG, "Codigo   : "+ codigoServidor);
                        Log.v(TAG, "respuesta: "+ respuesta);
                    }
                });
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.INTERNET
            }, 10);
            return;
        }
    }

    private void verificarPedidoViaje() {
        boolean viajeSolicitado = sharedPref.getBoolean("viajeSolicitado", false);
        if(viajeSolicitado){
            myDialog = new ProgressDialog(MapsActivity.this);
            myDialog.setMessage("Esperando respuesta del chofer...");
            myDialog.setCancelable(false);
            myDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //CANCELAR VIAJE DESDE EL APP Server
                    dialog.dismiss();
                }
            });
            myDialog.show();
        }
    }

    private void hideChat()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_chat).setVisible(false);
    }

    private void showChat()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_chat).setVisible(true);
    }

    private void setNavItemCount(@IdRes int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView();
        view.setText(count > 0 ? String.valueOf(count) : null);
    }

    private void configurarConfirmarViaje() {
        btnConfirmarViaje = (Button) findViewById(R.id.btnConfirmarViaje);
        btnConfirmarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rutaBuscada){
                    sharedServer.obtenerChoferesCercanos(longitud, latitud, new JSONCallback() {
                        @Override
                        public void ejecutar(JSONObject respuesta, long codigoServidor) {
                            String strChoferes = "";
                            Log.v(TAG, "------CHOFERES CERCANOS-------");
                            Log.v(TAG, "Codigo   : "+ codigoServidor);
                            Log.v(TAG, "respuesta: "+ respuesta);

                            Iterator<?> keys = respuesta.keys();
                            while(keys.hasNext()){
                                String key = (String)keys.next();
                                try {
                                    String id = respuesta.getJSONObject(key).optString("id");
                                    strChoferes = strChoferes + id + ",";
                                    String modelo = respuesta.getJSONObject(key).getJSONObject("perfil").optString("modelo");
                                    strChoferes = strChoferes + modelo + ",";
                                    String estado = respuesta.getJSONObject(key).getJSONObject("perfil").optString("estado");
                                    strChoferes = strChoferes + estado + ",";
                                    String musica = respuesta.getJSONObject(key).getJSONObject("perfil").optString("musica");
                                    strChoferes = strChoferes + musica + ",";
                                    String año = respuesta.getJSONObject(key).getJSONObject("perfil").optString("anio");
                                    strChoferes = strChoferes + año + ";";
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(strChoferes.isEmpty()){
                                Toast.makeText(getApplicationContext(), R.string.no_choferes, Toast.LENGTH_SHORT).show();
                            } else {
                                editorShared.putString("choferesCercanos", strChoferes);
                                Log.v(TAG, "Choferes   :"+ strChoferes);
                                editorShared.apply();
                                goSeleccionarChofer();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Seleccione el viaje primero", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void configurarNotificaciones(){

        badgeDrawable.setText("");

        //Para cuando no hay notificaciones
        int cantMsj = sharedPref.getInt("mensajes", -1);
        Log.v(TAG, "Mensajes: "+cantMsj);
        if(cantMsj == 0){
            badgeDrawable.setEnabled(false);
        }

    }

    /**
     * Configura el el menu lateral desplegable y identifica que acción realizar al clickear cada uno
     * de los botones que lo componen.
     */
    public void configurarMenuLateral(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        badgeDrawable = new BadgeDrawerArrowDrawable(getSupportActionBar().getThemedContext());

        toggle.setDrawerArrowDrawable(badgeDrawable);
        badgeDrawable.setText("");
        configurarNotificaciones();

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.nav_menu);

        //Esconder el boton de mensaje hasta que se confirme un viaje
        hideChat();

        if(sharedPref.getBoolean("viajeAceptado", false)){
            showChat();
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_account:
                        Log.v(TAG, "Perfil clikeado");
                        goProfile();
                        return true;
                    case R.id.nav_settings:
                        Log.v(TAG, "Configuración clikeado");
                        return true;
                    case R.id.nav_logout:
                        LoginManager.getInstance().logOut();
                        Log.v(TAG, "Cerrar sesión clikeado");
                        editorShared.clear();
                        editorShared.apply();
                        goLogin();
                        return true;
                    case R.id.nav_chat:
                        editorShared.putInt("mensajes", 0);
                        editorShared.apply();
                        configurarNotificaciones();
                        setNavItemCount(R.id.nav_chat, 0);
                        goChat();
                        return true;
                    case R.id.nav_payment:
                        goPayment();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendRequest(String origin, String destination) {
        origin = strOrigen;
        destination = strDestino;
        Log.v(TAG, "Origen : " + origin);
        Log.v(TAG, "Destino: " + destination);

        if (origin == null) {
            Toast.makeText(this, "Ingrese la dirección de origen", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination == null) {
            Toast.makeText(this, "Ingrese la dirección de destino", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Log.v(TAG, "ORIGEN: " + origin + " DESTINO: " + destination);
            rutaBuscada = true;
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * Se llamará cuando el mapa se encuentre listo para ser utilizado y se lo configra para que muestre
     * los botones y realicé las acciones deseadas (Botones de zoom, de ubicación, obtener las actualizaciones
     * de la posición, entre otros).
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.v(TAG, "Problema con los permisos de localización");
            return;
        }
        mMap.setMyLocationEnabled(true);
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);

        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

        latitud = -34.617335;
        longitud = -58.368231;
        LatLng ubicacion = new LatLng(latitud, longitud);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15));
    }

    /**
     * Muestra un loader indicando que se está buscando la ruta entre los puntos indicados
     */
    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Por favor espere.",
                "Buscando la mejor ruta.", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    /**
     * Marca en el mapa la ruta e indica también la duración de viaje junto con la distancia en km.
     * Adicionalmente se obtiene la hora actual.
     */
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        //Obtengo la hora actual
        int horaActual = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutosActuales = Calendar.getInstance().get(Calendar.MINUTE);
        Log.v(TAG, "Hora actual     : "+ horaActual);
        Log.v(TAG, "Minutos actuales: "+ minutosActuales);


        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            duracionViaje = route.duration.text;
            Log.v(TAG,  "Duración viaje  : "+duracionViaje);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
            kilometrosViaje = route.distance.text;
            Log.v(TAG,  "Kilómetros viaje: "+ kilometrosViaje);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    /**
     * Transiciona la APP hacía la pantalla de login de usuarios.
     */
    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Transiciona la APP hacía la pantalla de prefil del usuario.
     */
    private void goProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Transiciona la APP hacía la pantalla de selección de chofer.
     */
    private void goSeleccionarChofer() {
        Intent intent = new Intent(this, ChoferSelection.class);
        startActivity(intent);
    }

    private void goChat() {
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    private void goPayment() {
        Intent intent = new Intent(this, RegisterPayment.class);
        startActivity(intent);
    }
}
