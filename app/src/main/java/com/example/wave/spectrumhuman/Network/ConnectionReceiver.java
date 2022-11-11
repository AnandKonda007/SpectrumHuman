package com.example.wave.spectrumhuman.Network;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.wave.spectrumhuman.DataBase.OfflineDataController;
import com.example.wave.spectrumhuman.R;

/**
 * Created by WAVE on 12/7/2017.
 */

public class ConnectionReceiver extends BroadcastReceiver{

    public static ConnectionReceiverListener connectionReceiverListener;

    public ConnectionReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.e("BroadcastReceiver","call");
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (connectionReceiverListener != null) {
            Log.e("notnull","call");
            connectionReceiverListener.onNetworkConnectionChanged(isConnected);
        }
        if (isConnected){
            Log.e("ConnectionReceiver","call"+isConnected);
            OfflineDataController.getInstance().sycnOfflineData();
            /*Toast toast=  Toast.makeText(context, "Net is Connected", Toast.LENGTH_SHORT);
            View view1 = toast.getView();
            view1.setMinimumWidth(500);
            view1.setBackgroundColor(Color.parseColor("#233760"));
            view1.setBackgroundResource(R.drawable.layout_toastcornerbg);
            toast.show();*/

        }else {
            Toast toast = Toast.makeText(context, "Check Internet Connection", Toast.LENGTH_SHORT);
            View view1 = toast.getView();
            view1.setBackgroundColor(Color.parseColor("#FF0012"));
            view1.setMinimumWidth(650);
            view1.setBackgroundResource(R.drawable.layout_toastbg);
            toast.show();
    }
    }
    public interface ConnectionReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}

