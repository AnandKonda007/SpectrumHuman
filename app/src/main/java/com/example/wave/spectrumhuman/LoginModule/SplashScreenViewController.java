package com.example.wave.spectrumhuman.LoginModule;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.example.wave.spectrumhuman.DataBase.AddDoctorDataController;
import com.example.wave.spectrumhuman.DataBase.MemberDataController;
import com.example.wave.spectrumhuman.DataBase.OfflineDataController;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.HomeModule.HomeActivityViewController;
import com.example.wave.spectrumhuman.HomeModule.PersonalInformationViewController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Models.DoctorInformationModel;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.PushNotification.NotificationUtils;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.AddDoctorServerObjects;
import com.example.wave.spectrumhuman.SideMenu.AddDoctorViewController;
import com.example.wave.spectrumhuman.SideMenu.FindDoctorsViewController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.ByteArrayInputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rise on 19/09/2017.
 */

public class SplashScreenViewController extends AppCompatActivity  {
    Handler handler;
    public static SharedPreferences sharedPreferencesTOken;
    public static SharedPreferences.Editor sharedPreferencesTOkenEditor;
    public static Boolean isMoveToDoctorPage;
    private static final int REQUEST = 112;
    Context mContext = this;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load offline data controller contect
        OfflineDataController.getInstance().fillContext(getApplicationContext());

        isMoveToDoctorPage = false;
        setContentView(R.layout.activity_splashscreen);

        sharedPreferencesTOken = getApplicationContext().getSharedPreferences("tokendeviceids", Context.MODE_PRIVATE);
        sharedPreferencesTOkenEditor = sharedPreferencesTOken.edit();


        Bundle extras = getIntent().getExtras();

        Log.e("FirstCalledInfo", "" + getIntent().getExtras());

        if (extras != null) {
            String message = extras.getString("message");
            Log.e("MessageInBundle", "" + message);
            if (message != null) {

                AddDoctorDataController.getInstance().fetchDoctorInfo();
                AddDoctorDataController.getInstance().loadNotificationString(message);
                isMoveToDoctorPage = true;

            } else {
                isMoveToDoctorPage = false;
            }

        }


        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            Log.e("Version", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.M");
            String[] PERMISSIONS = {
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.ACCESS_WIFI_STATE,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };


            if (!hasPermissions(mContext, PERMISSIONS)) {
                Log.d("TAG", "@@@ IN IF hasPermissions");
                enableLoc();
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST);
            } else {
                Log.d("TAG", "@@@ IN ELSE hasPermissions");
                //enableLoc();
                checker();
            }
        } else {
            Log.d("TAG", "@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
           // enableLoc();
            checker();

        }
    }


    public void checkAppFlow() {


        //Get User Location
        LocationTracker.getInstance().fillContext(getApplicationContext());
        LocationTracker.getInstance().startLocation();
        // GET USer Informaion.
        UserDataController.getInstance();
        UserDataController.getInstance().fillContext(getApplicationContext());
        UserDataController.getInstance().fetchUserData();
        Log.e("Size", String.valueOf(UserDataController.getInstance().allUsers));

        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (UserDataController.getInstance().allUsers.size() > 0) {
                    LanguageTextController.getInstance().loadLanguageTexts();
                    //GET Members INFORMATION.
                    MemberDataController.getInstance();
                    MemberDataController.getInstance().fetchMemberData();
                    AddDoctorDataController.getInstance().fetchDoctorInfo();

                    moveToNextPages();
                } else {
                    SharedPreferences prefs = getSharedPreferences("AdvertiseInfo", MODE_PRIVATE);
                    String restoredValue = prefs.getString("isStored", null);
                    if (restoredValue == null || restoredValue.isEmpty()) {
                        startActivity(new Intent(getApplicationContext(), UrineAdvertizeViewController.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), LoginViewController.class));
                    }
                }

            }

        }, 3000 * 1);
    }


    private void moveToNextPages() {

        Log.e("moveToNextPages", "call" + MemberDataController.getInstance().allMembers.size());
        if (MemberDataController.getInstance().allMembers.size() > 0) {

            if (AddDoctorDataController.getInstance().allDoctorData.size() > 0) {
                loadUpdateDoctorStatus();
            }
            startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));

        } else {
            startActivity(new Intent(getApplicationContext(), PersonalInformationViewController.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }


    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();

    }
    public void loadUpdateDoctorStatus() {
        Log.e("loadUpdateDoctorStatus", "call");
        final AddDoctorServerObjects requestBody = new AddDoctorServerObjects();
        final DoctorInformationModel serverObjects = AddDoctorDataController.getInstance().currentDoctor;

        requestBody.setUserName(UserDataController.getInstance().currentUser.getUserName());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);

        Call<AddDoctorServerObjects> callable = api.getDoctorStatus(requestBody);
        callable.enqueue(new Callback<AddDoctorServerObjects>() {
            @Override
            public void onResponse(Call<AddDoctorServerObjects> call, Response<AddDoctorServerObjects> response) {
                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();
                Log.e("codefor3Doctorstaus", "call" + statusCode);
                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {
                        Log.e("Doctorstaus", "call" + response.body().getInvitation());
                        Log.e("Doctorstaus", "call" + serverObjects.getDoctorid());
                        AddDoctorDataController.getInstance().updateAddDoctorInformation(
                                serverObjects.getAddedtime(), serverObjects.getAddress(),
                                serverObjects.getDoctorid(), serverObjects.getEmail(), serverObjects.getFound(), serverObjects.getLatitude(),
                                serverObjects.getLongitude(), serverObjects.getName(),
                                serverObjects.getPhonenumber(), serverObjects.getSpecilization(), response.body().getInvitation());
                    }
                }
            }

            @Override
            public void onFailure(Call<AddDoctorServerObjects> call, Throwable t) {

            }
        });


    }


    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout r = (RelativeLayout) findViewById(R.id.relativesplash);
        r.clearAnimation();
        r.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.imageview);
        iv.clearAnimation();
        iv.startAnimation(anim);

    }
////////////Multiple Premession and alerts//////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "@@@ PERMISSIONS grant");
                    // callNextActivity();
                      checkAppFlow();
                      StartAnimations();
                } else {
                    Log.d("TAG", "@@@ PERMISSIONS Denied");
                    Toast.makeText(mContext, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void checker() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            StartAnimations();
            checkAppFlow();

            //Toast.makeText(mContext, "active", Toast.LENGTH_SHORT).show();

        } else {
            enableLoc();
        }
        // Toast.makeText(mContext, "no", Toast.LENGTH_SHORT).show();
        //return  true;
    }


    //// Below 6.0.1 Laction can be on /////
    private void enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                            //StartAnimations();
                            //checkAppFlow();


                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true); //this is the key ingredient

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {


                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(SplashScreenViewController.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_CANCELED: {
                        // The user was asked to change settings, but chose not to
                        finish();
                        break;
                    }
                    case Activity.RESULT_OK: {
                        // The user was asked to change settings, but chose not to
                        StartAnimations();
                        checkAppFlow();

                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }

    }


    @Override
    public void onBackPressed()
    {    //when click on phone backbutton
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
