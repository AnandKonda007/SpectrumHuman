package com.example.wave.spectrumhuman.SideMenu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.wave.spectrumhuman.DataBase.DeviceDataController;
import com.example.wave.spectrumhuman.HomeModule.SideMenuViewController;
import com.example.wave.spectrumhuman.Models.DeviceInformation;

import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by jacobliu on 11/15/17.
 */

public class WifiController  {


    public static WifiController myObj;
    Context context;


    List<ScanResult> wifiDevices;
    public WifiManager mainWifi;


    public static WifiController getInstance() {
        if (myObj == null) {
            myObj = new WifiController();

        }
        return myObj;
    }

    public  void  fillContext(Context context1)
    {
        context = context1;

    }


    public void scanWifiDevices() {

        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        context.getApplicationContext().registerReceiver(mWifiScanReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        context.getApplicationContext().registerReceiver(connectionChangedReceiver, new IntentFilter(
                WifiManager.NETWORK_STATE_CHANGED_ACTION));

        mainWifi.startScan();

    }


    private final BroadcastReceiver connectionChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {

          if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Log.e("ConncetionChanged", "called  "+ mainWifi.isWifiEnabled());
                if (mainWifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                    NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (ConnectivityManager.TYPE_WIFI == netInfo.getType()) {

                        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                        boolean connected = networkInfo.isConnected();
                        if (connected) {
                            WifiInfo info = mainWifi.getConnectionInfo();

                            if(info !=null && info.getSSID()!= null && info.getBSSID()!= null) {
                                String ssid = info.getSSID().toString();
                                String bssid = info.getBSSID().toString();

                                ssid = ssid.replace("\"", "");
                                bssid = bssid.replace("\"", "");

                                EventBus.getDefault().post(new SideMenuViewController.MessageEvent("Connected;" + ssid + "," + bssid));
                            }
                        }

                    } else {
                        EventBus.getDefault().post(new SideMenuViewController.MessageEvent("Disconnected; "));

                    }

                }
                else if (mainWifi.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {

                    if(wifiDevices != null) {
                        wifiDevices.clear();
                    }
                    EventBus.getDefault().post(new SideMenuViewController.MessageEvent("Disconnected; "));
                }

            }

        }
    };



    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                wifiDevices = mainWifi.getScanResults();

                EventBus.getDefault().post(new SideMenuViewController.MessageEvent("WifiList; "));

            }
        }
    };




    public   void filterWifiArrayForStoredWifiNetworks(){

        if(wifiDevices!= null) {
            Iterator<ScanResult> iter = wifiDevices.iterator();

            while (iter.hasNext()) {
                ScanResult objResult = iter.next();

                if (isContainResult(objResult)) {
                    iter.remove();
                }

            }
        }

    }

    private  boolean isContainResult(ScanResult objResult)
    {
        String deviceName = objResult.SSID;
        String deviceId = objResult.BSSID;

        deviceName =  deviceName.replace("\"","");
        deviceId = deviceId.replace("\"","");

        for (DeviceInformation objDeviceInfo:DeviceDataController.getInstance().allDevices)
        {
            Log.e("DeviceInfoCheck",objDeviceInfo.getDeviceId());
            Log.e("DeviceInfoCheck1",deviceId);

            if(objDeviceInfo.getDeviceName().equals(deviceName)&& objDeviceInfo.getDeviceId().equals(deviceId))
            {
                return  true;
            }
        }

        return  false;
    }


    public void finallyConnect(String ssid, String passkey) {
        Log.i(TAG, "* connectToAP");

        WifiConfiguration wifiConfiguration = new WifiConfiguration();

        String networkSSID = ssid;
        String networkPass = passkey;

        Log.d(TAG, "# password " + networkPass);
       if(wifiDevices != null) {
           for (ScanResult result : wifiDevices) {
               if (result.SSID.equals(networkSSID)) {

                   String securityMode = getScanResultSecurity(result);

                   if (securityMode.equalsIgnoreCase("OPEN")) {

                       wifiConfiguration.SSID = "\"" + networkSSID + "\"";
                       wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                       int res = mainWifi.addNetwork(wifiConfiguration);
                       Log.d(TAG, "# add Network returned " + res);

                       boolean b = mainWifi.enableNetwork(res, true);
                       Log.d(TAG, "# enableNetwork returned " + b);

                       mainWifi.setWifiEnabled(true);

                   } else if (securityMode.equalsIgnoreCase("WEP")) {

                       wifiConfiguration.SSID = "\"" + networkSSID + "\"";
                       wifiConfiguration.wepKeys[0] = "\"" + networkPass + "\"";
                       wifiConfiguration.wepTxKeyIndex = 0;
                       wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                       wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                       int res = mainWifi.addNetwork(wifiConfiguration);
                       Log.d(TAG, "### 1 ### add Network returned " + res);

                       boolean b = mainWifi.enableNetwork(res, true);
                       Log.d(TAG, "# enableNetwork returned " + b);

                       mainWifi.setWifiEnabled(true);
                   }

                   wifiConfiguration.SSID = "\"" + networkSSID + "\"";
                   wifiConfiguration.preSharedKey = "\"" + networkPass + "\"";
                   wifiConfiguration.hiddenSSID = true;
                   wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
                   wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                   wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                   wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                   wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                   wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                   wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                   wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

                   int res = mainWifi.addNetwork(wifiConfiguration);
                   Log.d(TAG, "### 2 ### add Network returned " + res);

                   mainWifi.enableNetwork(res, true);

                   boolean changeHappen = mainWifi.saveConfiguration();

                   if (res != -1 && changeHappen) {
                       Log.d(TAG, "### Change happen");


                   } else {
                       Log.d(TAG, "*** Change NOT happen");
                   }

                   mainWifi.setWifiEnabled(true);
                   List<WifiConfiguration> list = mainWifi.getConfiguredNetworks();
                   for (WifiConfiguration i : list) {
                       if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                           mainWifi.disconnect();
                           mainWifi.enableNetwork(i.networkId, true);
                           mainWifi.reconnect();
                           mainWifi.addNetwork(wifiConfiguration);

                           break;
                       }
                   }
               }
           }
       }
       else
       {
           EventBus.getDefault().post(new SideMenuViewController.MessageEvent("deviceUnavailable; "));
       }
    }


    public String getScanResultSecurity(ScanResult scanResult) {
        Log.i(TAG, "* getScanResultSecurity");

        final String cap = scanResult.capabilities;
        final String[] securityModes = {"WEP", "PSK", "EAP"};

        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i];
            }
        }

        return "OPEN";
    }

    public   Boolean checkWifiDeviceisConnectedForSSIDAndBSSID(String ssid, String bssid){

        if(mainWifi != null) {
        WifiInfo info = mainWifi.getConnectionInfo ();

            String connectedSSID = info.getSSID();
            String connectedBSSID = info.getBSSID();

            if (connectedSSID != null && connectedBSSID != null) {
                connectedSSID = connectedSSID.replace("\"", "");
                connectedBSSID = connectedBSSID.replace("\"", "");

                if(ssid.equals(connectedSSID)&& bssid.equals(connectedBSSID))
                {
                    return  true;
                }
            }

            return  false;


        }
        return  false;

    }


}
