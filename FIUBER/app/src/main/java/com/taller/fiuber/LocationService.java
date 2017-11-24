package com.taller.fiuber;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    private SharedServer sharedServer;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editorShared;

    private String strID;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.v(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.v(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            double latitud = location.getLatitude();
            double longitud = location.getLongitude();
            Log.v(TAG, "ID enviado: "+ strID);
            sharedServer.modificarPosicionChofer(strID, longitud, latitud, new JSONCallback() {
                @Override
                public void ejecutar(JSONObject respuesta, long codigoServidor) {
                    Log.v(TAG, "Codigo   : " + codigoServidor);
                    Log.v(TAG, "Respuesta: " + respuesta);
                }
            });
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.v(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.v(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.v(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.v(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.v(TAG, "onCreate");

        //Iniciliazación sharedPref
        sharedServer = new SharedServer();
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Almaceno el token del usuario
        //String strToken = sharedPref.getString("token", "noToken");
        //Log.v(TAG, strToken);
        //sharedServer.configurarTokenAutenticacion(strToken);

        //Guardo el ID
        strID = sharedPref.getString("ID", "noID");
        Log.v(TAG, "ID: "+ strID);

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Bundle extras = intent.getExtras();

        if(extras == null) {
            Log.d(TAG, "Service: null");
        } else {
            Log.d(TAG, "Service: not null");
            String strIDChofer = (String) extras.get("IDChofer");
            Log.d(TAG, "ID parametro: "+ strIDChofer);
            strID = strIDChofer;
            String strTokenChofer = (String) extras.get("TokenChofer");
            Log.d(TAG, "Token parametro: "+ strTokenChofer);
            sharedServer.configurarTokenAutenticacion(strTokenChofer);
        }
    }

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        editorShared.clear();
        editorShared.apply();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.v(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
