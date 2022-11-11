package com.example.wave.spectrumhuman.SideMenu;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.example.wave.spectrumhuman.HomeModule.HomeActivityViewController;
import com.example.wave.spectrumhuman.HomeModule.SideMenuViewController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.LoginModule.Constants;
import com.example.wave.spectrumhuman.Models.DeviceInformation;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.DeviceServerObjects;
import com.example.wave.spectrumhuman.TestModule.FirmwareViewController;
import com.example.wave.spectrumhuman.TestModule.InstructionpageViewController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


/**
 * Created by Reddyjpon 26/09/2017.
 */

public class MyDeviceViewController extends AppCompatActivity {

    RecyclerView addRecyclerView;
    Toolbar toolbar;
    ImageView back, home, add, refresh, butt;
    MyDevice device;
    ImageView imageView;
    int pStatus = 5;
    Handler handler = new Handler();
    Dialog dialog;
    Button btn_next;
    RelativeLayout txt_skip;
    Boolean isFromUrineTest = false;
    DeviceInformation tempSelectedDevice;
    DeviceInformation connectedDevice;
    private Handler testWifihandler;
    private Dialog wifidilog,failurealert;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1000;
    TextView addeddevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydevice);
        WifiController.getInstance().fillContext(this);
        WifiController.getInstance().scanWifiDevices();
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {
            isFromUrineTest = (Boolean) bd.get("isFromTestNow");
        }

        Log.e("isFromTestNow", "" + isFromUrineTest);
        DeviceDataController.getInstance().fetchDeviceInformation();
        ButterKnife.bind(this);
        setToolbar();
        init();

        updateLanguageTexts();

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void init() {
        btn_next = (Button) findViewById(R.id.btn_next);
        txt_skip=(RelativeLayout)findViewById(R.id.txt_skip);
        txt_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InstructionpageViewController.isCircularBackbtnCilcked =false;
                startActivity(new Intent(getApplicationContext(), InstructionpageViewController.class));
            }
        });

        if(!isFromUrineTest)
        {
            txt_skip.setVisibility(View.GONE);
        }

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InstructionpageViewController.isCircularBackbtnCilcked =false;
                DeviceDataController.getInstance().currentDevice = connectedDevice;
                startActivity(new Intent(getApplicationContext(), InstructionpageViewController.class));
            }
        });

        addeddevice = (TextView) findViewById(R.id.addeddevice);

        addRecyclerView = (RecyclerView) findViewById(R.id.recycler_mydevice);
        device = new MyDevice(DeviceDataController.getInstance().allDevices, getApplication());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        addRecyclerView.setLayoutManager(horizontalLayoutManager);
        addRecyclerView.setAdapter(device);


    }

    public void updateLanguageTexts() {
        addeddevice.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADDED_DEVICE_KEY));
        btn_next.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NEXT_KEY));

    }


    protected void onResume() {
        super.onResume();

        if (!PermissionGranted()) {
            requestCoarseLocationPermission();
        } else if (PermissionGranted() || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (device != null) {
                device.notifyDataSetChanged();

            }
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


    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        home = (ImageView) toolbar.findViewById(R.id.home);
        home.setImageResource(R.drawable.ic_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
            }
        });

        back = (ImageView) toolbar.findViewById(R.id.backimage);
        back.setVisibility(View.INVISIBLE);

        refresh = (ImageView) toolbar.findViewById(R.id.refresh);
        refresh.setVisibility(View.INVISIBLE);


        add = (ImageView) toolbar.findViewById(R.id.add);
        add.setVisibility(View.VISIBLE);
        add.setImageResource(R.drawable.ic_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MyDeviceViewController.this, AddDeviceViewController.class);
                intent.putExtra("isFromTestNow", isFromUrineTest);
                startActivity(intent);


            }
        });


        TextView toolbartext = (TextView) toolbar.findViewById(R.id.tool_txt);
        toolbartext.append(getString(R.string.mydevice));
        toolbartext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.MY_DEVICE_KEY));


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
            if (tempSelectedDevice != null) {
                connectionAction(ssid, bssid);
            }
        } else if (connectString.equals("Disconnected")) {
            // Receieved wifi list then refresh My Device list for show connected device info in different color text.
            if (testWifihandler != null) {
                testWifihandler.removeCallbacksAndMessages(null);
            }
            btn_next.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Failed to Connect..", Toast.LENGTH_SHORT).show();
            tempSelectedDevice = null;
            if (device != null) {
                device.notifyDataSetChanged();
            }


        } else if (connectString.equals("WifiList")) {
            // Receieved wifi list then refresh My Device list for show connected device info in different color text.
            if (device != null) {
                device.notifyDataSetChanged();
            }
        } else if (connectString.equals("deviceUnavailable")) {

            // If wifi device is not in range or not avaiable to connect.

            if (testWifihandler != null) {
                testWifihandler.removeCallbacksAndMessages(null);
            }
            btn_next.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Failed to Connect..", Toast.LENGTH_SHORT).show();
            tempSelectedDevice = null;
            if (device != null) {
                device.notifyDataSetChanged();
            }
        }

    }


    private void connectionAction(String ssid, String bssid) {


        Log.e("WifiConnectSSID", ssid);
        Log.e("WifiConnectBSSID", bssid);
        Log.e("WifiConnectSSID1", tempSelectedDevice.getDeviceName());
        Log.e("WifiConnectBSSID1", tempSelectedDevice.getDeviceId());

        // If before selected wifi not empty . compare that wifi with connected wifi and make that as active /
        if (tempSelectedDevice.getDeviceName().equalsIgnoreCase(ssid) && tempSelectedDevice.getDeviceId().equalsIgnoreCase(bssid)) {

            connectedDevice = tempSelectedDevice;

            // Remove Testwifi Handler
            if (testWifihandler != null) {
                testWifihandler.removeCallbacksAndMessages(null);
            }

            // Remove waiting  Handler
            if (handler != null) {
                handler.removeCallbacks(updateTimerThread);
            }
            // Remove waiting  Dialog window
            if (dialog != null) {
                dialog.dismiss();
            }


            if (isFromUrineTest) {
                btn_next.setVisibility(View.VISIBLE);
                if (device != null) {
                    device.notifyDataSetChanged();
                }
            } else {
                DeviceDataController.getInstance().currentDevice = tempSelectedDevice;
                startActivity(new Intent(getApplicationContext(), FirmwareViewController.class));
            }


            Log.e("WifiStatus", "Connected with selected Wifi");


            Log.e("ConnectedNetworkInfo", "" + ssid + " " + bssid);

        }
    }

    public void testWifiConnection() {

        testWifihandler = new Handler();
        testWifihandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // Remove waiting  Handler
                if (handler != null) {
                    handler.removeCallbacks(updateTimerThread);
                }
                // Remove waiting  Dialog window
                if (dialog != null) {
                    dialog.dismiss();
                }

                if (tempSelectedDevice != null && connectedDevice != null) {
                    if (tempSelectedDevice.getDeviceName().equalsIgnoreCase(connectedDevice.getDeviceName()) && tempSelectedDevice.getDeviceId().equalsIgnoreCase(connectedDevice.getDeviceId())) {

                        if (isFromUrineTest) {
                            btn_next.setVisibility(View.VISIBLE);
                            if (device != null) {
                                device.notifyDataSetChanged();
                            }

                        } else {
                            DeviceDataController.getInstance().currentDevice = tempSelectedDevice;
                            startActivity(new Intent(getApplicationContext(), FirmwareViewController.class));
                        }

                    } else {

                        btn_next.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Failed to Connect..", Toast.LENGTH_SHORT).show();
                        tempSelectedDevice = null;
                        if (device != null) {
                            device.notifyDataSetChanged();
                        }


                    }
                } else {

                    btn_next.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Failed to Connect..", Toast.LENGTH_SHORT).show();
                    tempSelectedDevice = null;

                    if (device != null) {
                        device.notifyDataSetChanged();
                    }

                }
                //Not connected with selected Wifi

            }
        }, 15000);


    }


    // Step 1:-
    public class MyDevice extends RecyclerView.Adapter<MyDevice.ViewHolder> {

        // step 3:-
        Context ctx;
        ImageView button;


        public MyDevice(ArrayList<DeviceInformation> arrayList, Context ctx) {
            this.ctx = ctx;
        }

        // step 5:-
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cards_layout, parent, false);


            ViewHolder myViewHolder = new ViewHolder(view, ctx, DeviceDataController.getInstance().allDevices);
            return myViewHolder;


        }

        //step 6:-
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {


            DeviceInformation objDeviceInfo = DeviceDataController.getInstance().allDevices.get(position);

            Log.e("securityType", objDeviceInfo.getSecureType());


            holder.deviceSSID.setText(objDeviceInfo.getDeviceName());
            holder.deviceBSSID.setText(objDeviceInfo.getDeviceId());


            holder.rightArrow.setBackgroundResource(R.drawable.ic_rightarrow);

            holder.checkMarkImage.setBackgroundResource(R.drawable.check);


            if (WifiController.getInstance().checkWifiDeviceisConnectedForSSIDAndBSSID(objDeviceInfo.getDeviceName(), objDeviceInfo.getDeviceId())) {
                holder.deviceSSID.setTextColor(Color.parseColor("#3b5c98"));
                holder.deviceBSSID.setTextColor(Color.parseColor("#3b5c98"));
            } else {
                holder.deviceSSID.setTextColor(Color.BLACK);
                holder.deviceBSSID.setTextColor(Color.BLACK);
            }


            if (isFromUrineTest == true) {
                if (WifiController.getInstance().checkWifiDeviceisConnectedForSSIDAndBSSID(objDeviceInfo.getDeviceName(), objDeviceInfo.getDeviceId())) {

                    holder.checkMarkImage.setVisibility(View.VISIBLE);
                    btn_next.setVisibility(View.VISIBLE);

                } else {
                    holder.deviceSSID.setTextColor(Color.BLACK);
                    holder.checkMarkImage.setVisibility(View.INVISIBLE);
                }
                Log.e("isfromurinetest", "call" + isFromUrineTest);
                holder.rightArrow.setVisibility(View.INVISIBLE);
                holder.rightArrow.setClickable(false);

            } else {
                holder.rightArrow.setVisibility(View.VISIBLE);
                holder.rightArrow.setClickable(true);
                holder.checkMarkImage.setVisibility(View.INVISIBLE);
                holder.checkMarkImage.setClickable(false);
                Log.e("isfromurinetest", "call" + isFromUrineTest);

            }


            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    alertForDeleteDevice(DeviceDataController.getInstance().allDevices.get(position));

                    return true;
                }
            });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tempSelectedDevice = DeviceDataController.getInstance().allDevices.get(position);

                    if (!WifiController.getInstance().checkWifiDeviceisConnectedForSSIDAndBSSID(tempSelectedDevice.getDeviceName(), tempSelectedDevice.getDeviceId())) {
                        if (tempSelectedDevice.getSecureType().equals(Constants.WifiSecureTypes.WPA.toString())) {
                            showAlertForWifiPassword(tempSelectedDevice);
                        } else {
                            showRefreshDialogue();
                            WifiController.getInstance().finallyConnect(tempSelectedDevice.getDeviceName(), "");
                            if (testWifihandler != null) {
                                testWifihandler.removeCallbacksAndMessages(null);
                            }
                            testWifiConnection();
                        }

                    } else {

                        if (!isFromUrineTest)   // selected  Device is already in connection, so directly process to Firmware.
                        {
                            DeviceDataController.getInstance().currentDevice = tempSelectedDevice;
                            startActivity(new Intent(getApplicationContext(), FirmwareViewController.class));
                        }


                    }


                }
            });


        }


        // step 4:-
        @Override
        public int getItemCount() {

            Log.e("listarray", "" + DeviceDataController.getInstance().allDevices.size());
            return DeviceDataController.getInstance().allDevices.size();
        }


        // Step 2:-
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            ImageView checkMarkImage, rightArrow;
            TextView deviceSSID, deviceBSSID;
            ArrayList<DeviceInformation> arrayList = new ArrayList<DeviceInformation>();
            Context ctx;

            public ViewHolder(View itemView, Context ctx, final ArrayList<DeviceInformation> arrayList) {
                super(itemView);

                this.arrayList = arrayList;
                this.ctx = ctx;
                deviceSSID = (TextView) itemView.findViewById(R.id.devicename);
                deviceBSSID = (TextView) itemView.findViewById(R.id.deviceid);
                checkMarkImage = (ImageView) itemView.findViewById(R.id.tick);
                rightArrow = (ImageView) itemView.findViewById(R.id.rightarrow);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {


            }
        }
    }


    private void showAlertForWifiPassword(final DeviceInformation tempSelectedDevice) {
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
        textSSID.setText(tempSelectedDevice.getDeviceName());

        // if button is clicked, connect to the network;
        connectingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkPassword = pass.getText().toString();

                if (checkPassword.length() > 0) {
                    WifiController.getInstance().finallyConnect(tempSelectedDevice.getDeviceName(), checkPassword);
                    showRefreshDialogue();
                    testWifiConnection();
                    wifidilog.dismiss();

                } else {
                    new AlertShowingDialog(MyDeviceViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_THE_DEVICE_PASSWORD_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

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
        dialog.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        imageView = (ImageView) dialog.findViewById(R.id.image_rottate);
        TextView textView = (TextView) dialog.findViewById(R.id.connecting);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));

        handler.postDelayed(updateTimerThread, 100);


    }

    public Runnable updateTimerThread = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            imageView.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.INVISIBLE);

            Log.e("pro", "" + pStatus);
            handler.postDelayed(this, 600);
            RotateAnimation rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(600);
            rotate.setInterpolator(new LinearInterpolator());
            imageView.startAnimation(rotate);
            rotate.setRepeatCount(-1);

        }
    };


    public void alertForDeleteDevice(final DeviceInformation selectedDeviceForDelete) {


        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure to delete record");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do your work here

                if (!isConn()) {
                    Toast.makeText(MyDeviceViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION), Toast.LENGTH_SHORT).show();

                } else {
                    showRefreshDialogue();
                    deleteDeviceDataData(selectedDeviceForDelete);
                }

                dialog.dismiss();

            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alert.show();
    }




    ////////////////////My DEVICE API/////////////////

    private void deleteDeviceDataData(final DeviceInformation selectedDeviceForDelete) {

        Log.e("callmethod", "call");


        DeviceServerObjects requestBody = new DeviceServerObjects();
        requestBody.setUserName(UserDataController.getInstance().currentUser.getUserName());
        Log.e("networkuser",""+UserDataController.getInstance().currentUser.getUserName());
        requestBody.setId(selectedDeviceForDelete.getDeviceId());
        Log.e("networkid",""+selectedDeviceForDelete.getDeviceId());



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<DeviceServerObjects> callable = api.deleteDeviceinfo(requestBody);
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

                        DeviceDataController.getInstance().deleteDeviceData(selectedDeviceForDelete);
                        DeviceDataController.getInstance().fetchDeviceInformation();
                        if (connectedDevice != null) {
                            if (selectedDeviceForDelete.getDeviceName() == connectedDevice.getDeviceName() && (selectedDeviceForDelete.getDeviceId() == connectedDevice.getDeviceId())) {
                                tempSelectedDevice = null;
                                connectedDevice = null;

                            }
                        }
                        device.notifyDataSetChanged();

                    } else if (statusCode.equals("5")) {
                        Log.e("five", "called");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                }

                handler.removeCallbacks(updateTimerThread);
                dialog.cancel();


            }

            @Override
            public void onFailure(Call<DeviceServerObjects> call, Throwable t) {
                handler.removeCallbacks(updateTimerThread);
                dialog.cancel();
                failurealert(selectedDeviceForDelete);

            }
        });

    }


    public void failurealert(final DeviceInformation selectedDeviceForDelete ) {

        Log.e("responsealert", "call");
        failurealert = new Dialog(this);
        failurealert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        failurealert.setCancelable(false);
        failurealert.setCanceledOnTouchOutside(false);
        failurealert.setCancelable(true);
        failurealert.setContentView(R.layout.activity_failurealert);
        failurealert.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        failurealert.show();

        TextView text=(TextView)failurealert.findViewById(R.id.text_error);
        text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ERROR_KEY));

        TextView text1=(TextView)failurealert.findViewById(R.id.requestfail);
        // text1.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ERROR_KEY));

        Button cancel = (Button) failurealert.findViewById(R.id.btn_failurecancel);
        cancel.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys. CANCEL_KEY ));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("cancel","canel");

                failurealert.dismiss();


            }


        });
        Button retry= (Button) failurealert.findViewById(R.id.btn_failureretry);
        // retry.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.Ret ));

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("retry","retry");
                if (isConn()){

                    failurealert.dismiss();
                    showRefreshDialogue();
                    deleteDeviceDataData(selectedDeviceForDelete);


                }else {
                    failurealert.dismiss();
                    new AlertShowingDialog(MyDeviceViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }


        });


    }
    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }



    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}