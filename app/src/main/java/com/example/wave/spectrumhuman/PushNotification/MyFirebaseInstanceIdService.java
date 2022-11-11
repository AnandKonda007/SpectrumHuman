package com.example.wave.spectrumhuman.PushNotification;

import android.util.Log;

import com.example.wave.spectrumhuman.LoginModule.SplashScreenViewController;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Rise on 08/01/2018.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    public static String  refreshedToken,deviceToken;


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("Refreshedtoken", " " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);

        SplashScreenViewController.sharedPreferencesTOkenEditor.putString("tokenid",MyFirebaseInstanceIdService.deviceToken);
        SplashScreenViewController.sharedPreferencesTOkenEditor.commit();
    }



    public static void sendRegistrationToServer(String token) {

        deviceToken=token;
        Log.e("token",""+deviceToken);

    }

}