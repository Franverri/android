package com.taller.fiuber.FirebaseServices;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.taller.fiuber.Config.Config;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FIREBASE";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        handleNotification(remoteMessage.getNotification().getBody());
    }

    private void handleNotification(String body) {
        Log.v(TAG, body);
        Intent pushNotification = new Intent(Config.STR_PUSH);
        pushNotification.putExtra("message", body);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
    }
}