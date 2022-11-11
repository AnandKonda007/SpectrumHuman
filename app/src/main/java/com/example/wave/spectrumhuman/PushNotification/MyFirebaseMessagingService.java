package com.example.wave.spectrumhuman.PushNotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.wave.spectrumhuman.DataBase.AddDoctorDataController;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.HomeModule.HomeActivityViewController;
import com.example.wave.spectrumhuman.HomeModule.SideMenuViewController;
import com.example.wave.spectrumhuman.LoginModule.LoginViewController;
import com.example.wave.spectrumhuman.LoginModule.SplashScreenViewController;
import com.example.wave.spectrumhuman.Models.DoctorInformationModel;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.SideMenu.AddDoctorViewController;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

/**
 * Created by Rise on 08/01/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Log.e("FirstCalledInfo", "splash screen");
        Log.e("FCMMESSAGE", "MESSAGE RECEIVED!!" + remoteMessage);

        Log.e(TAG, "FCMMESSAGE1 " + remoteMessage.getData());


        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.

        if (remoteMessage.getData() != null) {
            if (remoteMessage.getData().size() > 0) {
                Map<String, String> data = remoteMessage.getData();
                Log.e("FCMMESSAGEData", "" + data.keySet());
                String title = data.get("title");
                String message = data.get("body");
                handleDataMessage(title, message);
            }

        }

    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            //   NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            //  notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(String title, String message) {
        Log.e(TAG, "push json: " + message);
        Log.e(TAG, "push jsonTitle: " + title);

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            //     LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            AddDoctorDataController.getInstance().loadNotificationString(message);

            // play notification sound
            // NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            //   notificationUtils.playNotificationSound();
        } else {
            // app is in background, show the notification in notification tray

            Intent notificationIntent = new Intent(getApplicationContext(), SplashScreenViewController.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationIntent.putExtra("message", message);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addParentStack(SplashScreenViewController.class);
            stackBuilder.addNextIntent(notificationIntent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            int requestID = (int) System.currentTimeMillis();
            //PendingIntent contentIntent = PendingIntent.getActivity(this, requestID,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            showNotificationMessage(getApplicationContext(), message, notificationIntent, pendingIntent);
        }
    }


    /**
     * Showing notification with text only
     */

    private void showNotificationMessage(Context context, String message, Intent intent, PendingIntent pendinIntent) {
        notificationUtils = new NotificationUtils(context);
        notificationUtils.showNotificationMessage(message, intent, pendinIntent);

    }

    private void showNotificationMessage(Context context, String title, String message, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent, imageUrl);
    }


}







