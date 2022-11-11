package com.example.wave.spectrumhuman.SideMenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;
import com.example.wave.spectrumhuman.DataBase.DeviceDataController;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.HomeModule.SideMenuViewController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.LoginModule.Constants;
import com.example.wave.spectrumhuman.Models.DeviceInformation;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.DeviceServerObjects;
import com.example.wave.spectrumhuman.TestModule.InstructionpageViewController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


/**
 * Created by Rise on 02/10/2017.
 */
public class AddDeviceViewController extends AppCompatActivity {
    RecyclerView lvWifiDetails;
    View view;
    Toolbar toolbar;
    ImageView back, home, add, refresh, button;
    String selectedSSID = "";
    String selectedBSSID = "";
    String tempselectedSSID = "";
    String tempselectedBSSID = "";
    String secureType = Constants.WifiSecureTypes.OPEN.toString();
    TextView avilabledevices, foravilabledevices, searching;
    DeviceSearch device;
    ImageView imageView, mainImage;
    int pStatus = 5;
    Handler handler = new Handler();
    Dialog dialog, wifidilog, failurealert;
    Button btn_addDevice;
    RelativeLayout rl_search, rl_add;
    Boolean isFirstTimeLoad = false;


    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1000;

    String version, lastsync, spetrometerinformation, Batterypercentage;

    Handler testWifihandler;
    private Boolean isFromUrineTest;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddevice);
        ButterKnife.bind(this);

        WifiController.getInstance().fillContext(this);
        WifiController.getInstance().scanWifiDevices();

        isFirstTimeLoad = true;
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            isFromUrineTest = (Boolean) bd.get("isFromTestNow");

        }


        setToolbar();

        rl_search = (RelativeLayout) findViewById(R.id.relative_search);
        rl_search.setVisibility(View.VISIBLE);
        rl_add = (RelativeLayout) findViewById(R.id.relativeadd);
        rl_add.setVisibility(View.INVISIBLE);
        mainImage = (ImageView) findViewById(R.id.image1);
        btn_addDevice = (Button) findViewById(R.id.btn_adddevice);
        avilabledevices = (TextView) findViewById(R.id.avilabledevices);

        foravilabledevices = (TextView) findViewById(R.id.foravilabledevices);
        searching = (TextView) findViewById(R.id.searching);
        handler.postDelayed(animateTimerThread, 100);


        btn_addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (DeviceDataController.getInstance().isDeviceIDExisted(selectedBSSID)) {
                    DeviceInformation existedDeviceInfo = DeviceDataController.getInstance().getDeviceInfoForId(selectedBSSID);

                    showAlertForExistedDeviceID(existedDeviceInfo);
                    // Device is already stored with Name
                } else {
                    addDeviceData(true);
                    showRefreshDialogue();
                }

            }
        });

        EventBus.getDefault().register(this);
        updateLanguageTexts();
    }

    public void updateLanguageTexts() {
        foravilabledevices.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LOOKING_FOR_AVILABLE_DEVICES_KEY));

        searching.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SEARCHING_KEY));

        btn_addDevice.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADD_DEVICE_KEY));

        avilabledevices.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.AVILABLE_DEVICES_KEY));


    }


    public void showAlertForExistedDeviceID(DeviceInformation existedDeviceInfo) {

        Dialog alertdilog = new Dialog(this);
        alertdilog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertdilog.setContentView(R.layout.stopanimate_alert);
        TextView title = (TextView) alertdilog.findViewById(R.id.text_reminder);
        title.setText("Alert");
        TextView descriptionText = (TextView) alertdilog.findViewById(R.id.text);
        descriptionText.setText("Wifi Device is already stored with name:" + existedDeviceInfo.getDeviceName() + "\n" + "Do you want to overwrite with existed wifi details?");
        Button no = (Button) alertdilog.findViewById(R.id.btn_no);
        Button yes = (Button) alertdilog.findViewById(R.id.btn_yes);


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDeviceData(false);
                showRefreshDialogue();
            }
        });
        alertdilog.show();
    }


    private void setAdapter() {

        lvWifiDetails = (RecyclerView) findViewById(R.id.recycler_mydevice);
        Log.e("WifiDeviceBefore", "" + WifiController.getInstance().wifiDevices.size());
        WifiController.getInstance().filterWifiArrayForStoredWifiNetworks();
        Log.e("WifiDeviceAfter", "" + WifiController.getInstance().wifiDevices.size());
        device = new DeviceSearch(WifiController.getInstance().wifiDevices, getApplication());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        lvWifiDetails.setLayoutManager(horizontalLayoutManager);
        lvWifiDetails.setAdapter(device);

    }


    private void setConnectedWifiDeviceSSIDAndBSSID() {


        WifiInfo info = WifiController.getInstance().mainWifi.getConnectionInfo();

        if (info != null) {
            tempselectedSSID = info.getSSID();
            tempselectedBSSID = info.getBSSID().toString();

            tempselectedSSID = tempselectedSSID.replace("\"", "");
            tempselectedBSSID = tempselectedBSSID.replace("\"", "");

            selectedSSID = tempselectedSSID;
            selectedBSSID = tempselectedBSSID;


            Log.e("firstWifiSSID", selectedSSID);
            Log.e("firstWifiSSID1", selectedBSSID);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SideMenuViewController.MessageEvent event) {
        Log.e("AddDeviceMessageEvent", "" + event.message);
        String resultData = event.message.trim();

        String[] keyArray = resultData.split(";");

        String connectString = keyArray[0];


        if (connectString.equals("Connected")) {
            String[] resultArray = keyArray[1].split(",");
            String ssid = resultArray[0].trim();
            String bssid = resultArray[1].trim();
            connectionAction(ssid, bssid);
        } else if (connectString.equals("Disconnected")) {
            disconnectionAction();

        } else if (connectString.equals("WifiList")) {

            // add your logic here
            if (isFirstTimeLoad) {
                setConnectedWifiDeviceSSIDAndBSSID();
                isFirstTimeLoad = false;
            }
            if (WifiController.getInstance().wifiDevices != null) {
                setAdapter();
            }
        }


    }


    private void disconnectionAction() {

        tempselectedSSID = "";
        tempselectedBSSID = "";
        selectedSSID = tempselectedSSID;
        selectedBSSID = tempselectedBSSID;
        if (WifiController.getInstance().wifiDevices != null) {
            setAdapter();
            device.notifyDataSetChanged();
        }
        isFirstTimeLoad = false;
    }

    private void connectionAction(String ssid, String bssid) {


        Log.e("WifiConnectSSID", ssid);
        Log.e("WifiConnectBSSID", bssid);
        Log.e("WifiConnectSSID1", tempselectedSSID);
        Log.e("WifiConnectBSSID1", tempselectedBSSID);


        tempselectedSSID = tempselectedSSID.replace("\"", "");
        tempselectedBSSID = tempselectedBSSID.replace("\"", "");

        // If before Not selected any wifi , then connected wifi become as active
        if (tempselectedSSID.length() == 0 && tempselectedBSSID.length() == 0) {

            tempselectedSSID = ssid;
            tempselectedBSSID = bssid;

            selectedSSID = tempselectedSSID;
            selectedBSSID = tempselectedBSSID;

            if (device != null) {
                device.notifyDataSetChanged();
            }
            return;

        }


        // If before selected wifi not empty . compare that wifi with connected wifi and make that as active /
        if (tempselectedSSID.equalsIgnoreCase(ssid) && tempselectedBSSID.equalsIgnoreCase(bssid)) {

            selectedSSID = tempselectedSSID;
            selectedBSSID = tempselectedBSSID;


            // Remove Testwifi Handler
            if (testWifihandler != null) {
                handler.removeCallbacksAndMessages(null);
            }

            // Remove waiting  Handler
            if (handler != null) {
                handler.removeCallbacks(updateTimerThread);
            }
            // Remove waiting  Dialog window
            if (dialog != null) {
                dialog.dismiss();
            }


            Log.e("WifiStatus", "Connected with selected Wifi");

            if (device != null) {
                device.notifyDataSetChanged();
            }
            Log.e("ConnectedNetworkInfo", "" + ssid + " " + bssid);

        }
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        home = (ImageView) toolbar.findViewById(R.id.home);
        home.setImageResource(R.drawable.ic_home);

        home.setVisibility(View.INVISIBLE);

        back = (ImageView) toolbar.findViewById(R.id.backimage);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


        refresh = (ImageView) toolbar.findViewById(R.id.refresh);
        refresh.setVisibility(View.VISIBLE);
        add = (ImageView) toolbar.findViewById(R.id.add);
        add.setImageResource(R.drawable.ic_add);
        add.setVisibility(View.INVISIBLE);
        TextView toolbartext = (TextView) toolbar.findViewById(R.id.tool_txt);
        toolbartext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADD_DEVICE_KEY));
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_search.setVisibility(View.VISIBLE);
                handler.postDelayed(animateTimerThread, 100);
                rl_add.setVisibility(View.GONE);
                WifiController.getInstance().scanWifiDevices();

            }
        });

    }
    // Step 1:-
    public class DeviceSearch extends RecyclerView.Adapter<DeviceSearch.ViewHolder> {

        // step 3:-
        List<ScanResult> wifiList = new ArrayList<>();
        Context ctx;
        ImageView button;


        public DeviceSearch(List<ScanResult> wifiList, Context ctx) {
            this.ctx = ctx;
            this.wifiList = wifiList;
        }


        // step 5:-
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.devicecardlayout, parent, false);
            ViewHolder myViewHolder = new ViewHolder(view, ctx, wifiList);


            return myViewHolder;

        }

        //step 6:-
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final ScanResult objResult = wifiList.get(position);

            holder.userName.setText(objResult.SSID);
            holder.userName1.setText(objResult.BSSID);
            String capabilities = objResult.capabilities;
            holder.image1.setBackgroundResource(R.drawable.ic_wifilock);


            if (capabilities.contains(Constants.WifiSecureTypes.WPA.toString())) {
                holder.image1.setVisibility(view.VISIBLE);
            } else {
                holder.image1.setVisibility(view.INVISIBLE);

            }
            holder.image.setBackgroundResource(R.drawable.check);


            Log.e("firstWifiSSID3", selectedSSID);
            Log.e("firstWifiSSID3", selectedBSSID);

            Log.e("firstWifiSSID4", objResult.SSID.replace("\"", ""));
            Log.e("firstWifiSSID4", objResult.BSSID.replace("\"", ""));

            if (selectedSSID.equalsIgnoreCase(objResult.SSID.replace("\"", "")) && selectedBSSID.equalsIgnoreCase(objResult.BSSID.replace("\"", ""))) {
                Log.e("if", "called");
                holder.image.setVisibility(View.VISIBLE);
                btn_addDevice.setVisibility(View.VISIBLE);

                // For first time , to know security type for connected device.
                if (objResult.capabilities.contains(Constants.WifiSecureTypes.WPA.toString())) {
                    secureType = Constants.WifiSecureTypes.WPA.toString();
                } else {
                    secureType = Constants.WifiSecureTypes.OPEN.toString();
                }

            } else {
                Log.e("else", "called");
                holder.image.setVisibility(View.INVISIBLE);


            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //  showRefreshDialogue();
                    // connectionAvailable();
                    btn_addDevice.setVisibility(View.INVISIBLE);

                    ScanResult objSelectedResult = wifiList.get(position);

                    tempselectedSSID = objSelectedResult.SSID;
                    tempselectedBSSID = objSelectedResult.BSSID;

                    Log.e("SelectedSSID", "" + tempselectedSSID);
                    Log.e("SelectedSSIDBConnect", "" + tempselectedBSSID);
                    // selected item
                    if (objSelectedResult.capabilities.contains(Constants.WifiSecureTypes.WPA.toString())) {
                        secureType = Constants.WifiSecureTypes.WPA.toString();
                        showRefreshDialogue();
                        showAlertForWifiPassword(objSelectedResult);
                    } else {
                        secureType = Constants.WifiSecureTypes.OPEN.toString();
                        showRefreshDialogue();
                        WifiController.getInstance().finallyConnect(objSelectedResult.SSID, "");
                        testWifiConnection();

                    }
                }
            });


        }

        // step 4:-
        @Override
        public int getItemCount() {

            Log.e("listarray", "" + wifiList.size());
            return wifiList.size();
        }


        // Step 2:-
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            private final List<ScanResult> wifiList;
            ImageView image;
            ImageView image1;
            TextView userName, userName1;
            ArrayList<String> arrayList = new ArrayList<String>();
            Context ctx;

            public ViewHolder(View itemView, Context ctx, final List<ScanResult> wifiList) {
                super(itemView);

                this.wifiList = wifiList;
                this.ctx = ctx;
                userName = (TextView) itemView.findViewById(R.id.devicename);
                userName1 = (TextView) itemView.findViewById(R.id.device);
                image = (ImageView) itemView.findViewById(R.id.tick);
                image1 = (ImageView) itemView.findViewById(R.id.wifilock);

                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {

            }
        }
    }

    private void showAlertForWifiPassword(final ScanResult objWifiResult) {
        wifidilog = new Dialog(this);
        wifidilog.setContentView(R.layout.connect);
        wifidilog.setTitle("Connect to Network");
        wifidilog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);


        wifidilog.setTitle((LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECT_TO_NETWORK_KEY)));

        TextView textSSID = (TextView) wifidilog.findViewById(R.id.textSSID1);
        Button connectingButton = (Button) wifidilog.findViewById(R.id.connectButton);
        connectingButton.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECT_KEY));

        Button cancelButton = (Button) wifidilog.findViewById(R.id.cancelButton);
        cancelButton.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

        final EditText pass = (EditText) wifidilog.findViewById(R.id.textPassword);

        textSSID.setText(objWifiResult.SSID);

        // if button is clicked, connect to the network;
        connectingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkPassword = pass.getText().toString();

                if (checkPassword.length() > 0) {
                    WifiController.getInstance().finallyConnect(objWifiResult.SSID, checkPassword);
                    showRefreshDialogue();
                    testWifiConnection();
                    wifidilog.dismiss();

                } else {
                    new AlertShowingDialog(AddDeviceViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_THE_DEVICE_PASSWORD_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wifidilog.dismiss();
            }
        });
        wifidilog.show();
    }


    public void showRefreshDialogue() {


        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_animate);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        // dialog.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        TextView textView = (TextView) dialog.findViewById(R.id.connecting);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));

        imageView = (ImageView) dialog.findViewById(R.id.image_rottate);
        handler.postDelayed(updateTimerThread, 100);
    }

    public void testWifiConnection() {

        testWifihandler = new Handler();
        testWifihandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();

                if (tempselectedSSID.equalsIgnoreCase(selectedSSID) && tempselectedBSSID.equalsIgnoreCase(selectedBSSID)) {

                    // Connected succesfully
                    selectedSSID = tempselectedSSID;
                    selectedBSSID = tempselectedBSSID;

                    device.notifyDataSetChanged();
                } else {

                    Toast.makeText(getApplicationContext(), "Failed to Connect..", Toast.LENGTH_SHORT).show();
                    tempselectedBSSID = "";
                    tempselectedSSID = "";
                }
                //Not connected with selected Wifi

            }
        }, 15000);

    }

    public Runnable updateTimerThread = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            imageView.setVisibility(View.VISIBLE);
            pStatus -= 1;
            Log.e("pro", "" + pStatus);
            handler.postDelayed(this, 600);
            RotateAnimation rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(600);
            rotate.setInterpolator(new LinearInterpolator());
            imageView.startAnimation(rotate);
            //rotate.setRepeatCount(3);
            if (pStatus == 0) {
                device.notifyDataSetChanged();
                Log.e("profor100", "" + pStatus);
                imageView.setVisibility(View.VISIBLE);
                pStatus = 5;
                handler.removeCallbacks(updateTimerThread);
                dialog.cancel();

            }

        }
    };
    public Runnable animateTimerThread = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            mainImage.setVisibility(View.VISIBLE);
            pStatus -= 1;
            Log.e("pro", "" + pStatus);
            handler.postDelayed(this, 1000);
            RotateAnimation rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(1000);
            rotate.setInterpolator(new LinearInterpolator());
            mainImage.startAnimation(rotate);
            //rotate.setRepeatCount(3);
            if (pStatus == 0) {
                Log.e("profor100", "" + pStatus);
                pStatus = 5;
                handler.removeCallbacks(animateTimerThread);
                rl_search.setVisibility(View.GONE);
                rl_add.setVisibility(View.VISIBLE);
            }
        }
    };


    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        if (!PermissionGranted()) {
            requestCoarseLocationPermission();
        } else if (PermissionGranted() || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            WifiController.getInstance().scanWifiDevices();
        }
    }


    private void requestCoarseLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }
    }


    private boolean PermissionGranted() {
        boolean isAndroidMOrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        boolean isFineLocationPermissionGranted = isGranted(ACCESS_FINE_LOCATION);
        boolean isCoarseLocationPermissionGranted = isGranted(ACCESS_COARSE_LOCATION);

        return isAndroidMOrHigher && (isFineLocationPermissionGranted
                || isCoarseLocationPermissionGranted);
    }

    private boolean isGranted(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED;

    }


    ////////////////////ADD DEVICE API/////////////////

    private void addDeviceData(final Boolean isInsert) {

        Log.e("callmethod", "call");

        DeviceServerObjects requestBody = new DeviceServerObjects();
        requestBody.setUserName(UserDataController.getInstance().currentUser.getUserName());

        requestBody.setId(selectedBSSID);
        Log.e("id", "" + selectedBSSID);

        requestBody.setBattery_percentage(Batterypercentage = String.valueOf("100"));
        Log.e("percent", "" + Batterypercentage);

        requestBody.setSpectrometer_information(spetrometerinformation = String.valueOf("world smallest spectrometer device"));
        Log.e("inform", "" + spetrometerinformation);

        requestBody.setLast_sync(lastsync = String.valueOf(System.currentTimeMillis() / 1000L));
        Log.e("sync", "" + lastsync);

        requestBody.setSpectrometer_version(version = String.valueOf("V0.1"));
        Log.e("version", "" + version);

        requestBody.setSpectrometername(selectedSSID);
        Log.e("name", "" + selectedSSID);

        requestBody.setSecureType(secureType);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);


        Call<DeviceServerObjects> callable = api.deviceinfo(requestBody);

        if (!isInsert) {
            callable = api.editDeviceinfo(requestBody);
        }

        //final long finalUnixTime = unixTime;
        callable.enqueue(new Callback<DeviceServerObjects>() {
            @Override
            public void onResponse(Call<DeviceServerObjects> call, retrofit2.Response<DeviceServerObjects> response) {
                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();
                Log.e("codefor3", "call" + statusCode);
                if (!statusCode.equals(null)) {
                    Log.e("zero", "called");
                    if (statusCode.equals("3")) {
                        Log.e("three", "called");

                        if (isInsert) {
                            if (DeviceDataController.getInstance().insertDeviceInformation(selectedSSID, selectedBSSID, "100", "V0.1", true, secureType)) {
                                nextPageAction(message);
                            }
                        } else {

                            if (DeviceDataController.getInstance().updateDeviceInformation(selectedSSID, selectedBSSID, "100", "V0.1", true, secureType)) {
                                nextPageAction(message);
                            }

                        }

                    } else if (statusCode.equals("5")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }

                handler.removeCallbacks(updateTimerThread);
                dialog.cancel();


            }

            @Override
            public void onFailure(Call<DeviceServerObjects> call, Throwable t) {
                handler.removeCallbacks(updateTimerThread);
                dialog.cancel();
                failurealert();
            }
        });

    }


    public void failurealert() {

        Log.e("responsealert", "call");
        failurealert = new Dialog(this);
        failurealert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        failurealert.setCancelable(false);
        failurealert.setCanceledOnTouchOutside(false);
        failurealert.setCancelable(true);
        failurealert.setContentView(R.layout.activity_failurealert);
        failurealert.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        failurealert.show();

        TextView text = (TextView) failurealert.findViewById(R.id.text_error);
        text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ERROR_KEY));

        TextView text1 = (TextView) failurealert.findViewById(R.id.requestfail);
        // text1.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ERROR_KEY));

        Button cancel = (Button) failurealert.findViewById(R.id.btn_failurecancel);
        cancel.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("cancel", "canel");

                failurealert.dismiss();


            }


        });
        Button retry = (Button) failurealert.findViewById(R.id.btn_failureretry);
        // retry.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.Ret ));

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("retry", "retry");
                if (isConn()) {

                    failurealert.dismiss();
                    showRefreshDialogue();
                    addDeviceData(true);

                } else {
                    failurealert.dismiss();
                    new AlertShowingDialog(AddDeviceViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }


        });


    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }


    private void nextPageAction(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

        if (isFromUrineTest) {
            startActivity(new Intent(getApplicationContext(), InstructionpageViewController.class));
        } else {
            startActivity(new Intent(getApplicationContext(), MyDeviceViewController.class));
        }

    }

    @Override
    public void onBackPressed() {
        // stop = true;
        //startActivity(new Intent(getApplicationContext(), MyDeviceViewController.class));
        finish();
        super.onBackPressed();
    }
}
