package com.taller.fiuber;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService  {
    private static final String TAG = "FirebaseMessagingServic";

    private Context appContext;
    private int action;
    private int notificationID;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    public FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        appContext=getBaseContext();
        sharedPref = appContext.getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Inicializar notification ID
        notificationID = sharedPref.getInt("notificationID", -1);
        if(notificationID == -1){
            notificationID = 0;
            editorShared.putInt("notificationID", notificationID);
            editorShared.apply();
        } else {
            notificationID++;
            editorShared.putInt("notificationID", notificationID);
            editorShared.apply();
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            try {
                JSONObject data = new JSONObject(remoteMessage.getData());
                String strAction = data.getString("action");
                action = Integer.parseInt(strAction);
                String title = data.getString("title");
                String message = data.getString("message");
                //String click_action = data.getString("click_action");
                sendNotification(title, message, action);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Check if message contains a notification payload.
        /*
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle(); //get title
            String message = remoteMessage.getNotification().getBody(); //get message
            String click_action = remoteMessage.getNotification().getClickAction(); //get click_action

            Log.d(TAG, "Message Notification Title       : " + title);
            Log.d(TAG, "Message Notification Body        : " + message);
            Log.d(TAG, "Message Notification click_action: " + click_action);
            Log.d(TAG, "Message Notification action      : " + action);

            sendNotification(title, message,click_action, action);
        }*/
    }

    @Override
    public void onDeletedMessages() {

    }

    private void sendNotification(String title,String messageBody, int action) {
        Intent intent;
        /*
        if(click_action != null){
            if(click_action.equals("CHATACTIVITY")){
                intent = new Intent(this, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            else if(click_action.equals("MAINCHOFERACTIVITY")){
                intent = new Intent(this, MainChoferActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }else{
                intent = new Intent(this, MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        } else {
            intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }*/

        switch (action){
            case 0:
                //Nuevo mensaje de chat (Notificación para ambos - uno o el otro, no simultaneo)
                int cantidadMensajes = sharedPref.getInt("contadorMensajes", -1);
                Log.v(TAG, "CANTIDAD MENSAJES: " + cantidadMensajes);
                if(cantidadMensajes!=-1){
                    cantidadMensajes++;
                } else {
                    cantidadMensajes = 1;
                }
                editorShared.putInt("contadorMensajes", cantidadMensajes);
                editorShared.apply();
                intent = new Intent(this, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case 1:
                //Viaje rechazado (Notificación para el pasajero)
                intent = new Intent(this, MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editorShared.remove("viajeSolicitado");
                editorShared.putBoolean("viajeRechazado", true);
                editorShared.apply();
                break;
            case 2:
                //Viaje aceptado (Notificación para el pasajero)
                intent = new Intent(this, MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editorShared.remove("viajeSolicitado");
                editorShared.putBoolean("viajeAceptado", true);
                editorShared.apply();
                break;
            case 3:
                //Viaje terminado (Notificacion para el chofer)
                intent = new Intent(this, MainChoferActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            case 4:
                //Viaje terminado (Notificacion para el pasajero)
                intent = new Intent(this, MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                editorShared.remove("viajeAceptado");
                editorShared.apply();
                break;
            case 5:
                //Nuevo viaje (Notificación para el chofer)
                intent = new Intent(this, MainChoferActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            default:
                intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.logochico)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Log.v(TAG, "NOTIFICATION ID: " + notificationID);
        notificationManager.notify(notificationID, notificationBuilder.build());
    }
}
