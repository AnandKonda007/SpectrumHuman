package com.example.wave.spectrumhuman.TestModule;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wave.spectrumhuman.DataBase.DeviceDataController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Reddyjpon 29/09/2017.
 */

public class FirmwareViewController extends AppCompatActivity{
    RelativeLayout r2;
    ProgressBar progressBar;

    int pStatus = 0;
    Toolbar toolbar;
    ImageView back, home, add, refresh,img_home;
    TextView txt_update,devname,battery,deviceid,firmwear,currentversionTextView,latestVersionTextView;

    private Handler handler = new Handler();
    //TextView percent;
    RelativeLayout currentversion, latestversiion;
    RelativeLayout rl;
    private EditText deviceEdit;
    private TextView deviceId;
    private ImageView batteryImageView;
    Button updatetext;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware);

        ButterKnife.bind(this);
        setToolbar();
        progressBar = (ProgressBar) findViewById(R.id.progressbar_updatedevice);
        percent = (TextView) findViewById(R.id.precenttext);
//        down=(Button)findViewById(R.id.down);
//        up=(Button)findViewById(R.id.up);

        rl = (RelativeLayout) findViewById(R.id.relativehide);
        txt_update=(TextView)findViewById(R.id.update);
        progressBar.setVisibility(View.INVISIBLE);
        txt_update.setVisibility(View.INVISIBLE);
        percent.setVisibility(View.INVISIBLE);

        deviceEdit =(EditText)findViewById(R.id.deviceedittext);

        deviceId =(TextView)findViewById(R.id.deviceeditid);
        batteryImageView =(ImageView) findViewById(R.id.batterypercentage);

        currentversionTextView =  (TextView) findViewById(R.id.currentversionID);
        latestVersionTextView =  (TextView) findViewById(R.id.latestversionID);

        currentversionTextView =  (TextView) findViewById(R.id.currentversionID);
        latestVersionTextView =  (TextView) findViewById(R.id.latestversionID);
        devname=(TextView) findViewById(R.id.devname);
        deviceid=(TextView) findViewById(R.id.device);
        firmwear=(TextView)findViewById(R.id.firmwearupdate);
        battery=(TextView)findViewById(R.id.batteryid);
        updatetext=(Button) findViewById(R.id.btn_update);

        deviceEdit.setText(DeviceDataController.getInstance().currentDevice.getDeviceName());
        deviceId.setText(DeviceDataController.getInstance().currentDevice.getDeviceId());
        currentversionTextView.setText("Current Version : "+DeviceDataController.getInstance().currentDevice.getFirmwareversion());
        deviceEdit.setSelection(deviceEdit.getText().length());


        fillBatteryPercentageImage();
        updateLanguageTexts();
    }
    public void updateLanguageTexts() {

        devname.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DEVICE_NAME_KEY));

        deviceid.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DEVICE_ID_KEY));

        firmwear.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FIRMWARE_UPDATES_KEY));

        battery.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.BATTERY_KEY));

        currentversionTextView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CURRENT_VERSION_KEY));

        latestVersionTextView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LATEST_VERSION_KEY));

        update.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.UPDATE_FIRMAWARE_KEY));

        txt_update.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.UPDATE_FIRMAWARE_KEY));


    }


    @BindView(R.id.precenttext)
    TextView percent;
    @BindView(R.id.btn_update)
    Button update;


    private void setToolbar() {


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        home = (ImageView) toolbar.findViewById(R.id.home);
        home.setVisibility(View.INVISIBLE);


        back = (ImageView) findViewById(R.id.back_arraow);
        img_home = (ImageView)findViewById(R.id.backimage);
        img_home.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        refresh = (ImageView) toolbar.findViewById(R.id.refresh);
        refresh.setVisibility(View.INVISIBLE);


        add = (ImageView) toolbar.findViewById(R.id.add);
        add.setImageResource(R.drawable.ic_add);
        add.setVisibility(View.INVISIBLE);


        TextView toolbartext = (TextView) toolbar.findViewById(R.id.tool_txt);
        toolbartext.append(getString(R.string.mydevice));
        toolbartext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.MY_DEVICE_KEY));


    }


    @BindView(R.id.up)
    Button up;
    @BindView(R.id.down)
    Button down;

    @OnClick(R.id.down)
    public void down() {


        if (down.isClickable()) {
            Log.e("amsamsna", "call");
            down.setVisibility(View.INVISIBLE);
            up.setVisibility(View.VISIBLE);
            rl.setVisibility(View.VISIBLE);
        } else {
            rl.setVisibility(View.INVISIBLE);
        }

    }

    @OnClick(R.id.up)
    public void up() {
        if (up.isClickable()) {
            Log.e("amsamsna", "call");
            down.setVisibility(View.VISIBLE);
            up.setVisibility(View.INVISIBLE);
            rl.setVisibility(View.INVISIBLE);
        } else {
            rl.setVisibility(View.VISIBLE);
        }
    }


    @OnClick(R.id.btn_update)
    public void butupdate() {

        Log.e("Call", "onClick");
        update.setClickable(false);
        final Dialog dialog = new Dialog(FirmwareViewController.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.update_alert);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        TextView  text_alert = (TextView) dialog.findViewById(R.id.textView);
        text_alert.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ALERT_KEY));

        TextView  text = (TextView) dialog.findViewById(R.id.textView1);
        text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DO_YOU_WANT_TO_UPDATE_FIRMWARE_KEY));

        Button btnOkk = (Button) dialog.findViewById(R.id.btn_yes);
        btnOkk.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys. OK_KEY));

        Button canle = (Button) dialog.findViewById(R.id.btn_no);
        canle.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys. CANCEL_KEY));

        btnOkk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                dialog.cancel();
                handler.postDelayed(updateTimerThread, 100);
            }
        });

        canle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.cancel();
                update.setClickable(true);

            }
        });

    }


    public Runnable updateTimerThread = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            percent.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            txt_update.setVisibility(View.VISIBLE);
            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.progress_drawableforupdate);
            progressBar.setProgress(0);   // Main Progress
            progressBar.setSecondaryProgress(0); // Secondary Progress
            progressBar.setMax(100); // Maximum Progress
            progressBar.setProgressDrawable(drawable);
            down.setClickable(false);
            up.setClickable(false);
            pStatus += 1;
            progressBar.setProgress(pStatus);
            String pre = pStatus + "%";
            percent.setText(pre);
            Log.e("pro", "" + pStatus);
            handler.postDelayed(this, 100);
            if (pStatus == 100) {
                Log.e("profor100", "" + pStatus);
                handler.removeCallbacks(updateTimerThread);
                percent.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                txt_update.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FIRMWARE_IS_UPDATED_SUCCESSFULLY), Toast.LENGTH_SHORT).show();
                update.setClickable(true);
                pStatus = 0;
                progressBar.setProgress(0);
                percent.setText("" + pStatus);
                down.setClickable(true);
                up.setClickable(true);
            }
        }
    };


    private  void  fillBatteryPercentageImage(){


        int battery_percentage = Integer.parseInt(DeviceDataController.getInstance().currentDevice.getBatteryPercentage());

        if(battery_percentage > 0 && battery_percentage <=10)
        {
            // zero level batery
            batteryImageView.setImageResource(R.drawable.ic_battery0);
        }

        else if(battery_percentage > 10 && battery_percentage <=20)
        {
            // one level batery
            batteryImageView.setImageResource(R.drawable.ic_battery1);
        }
        else if(battery_percentage > 20 && battery_percentage <=40)
        {
            // two level  batery
            batteryImageView.setImageResource(R.drawable.ic_battery2);
        }
        else if(battery_percentage > 40 && battery_percentage <=60)
        {
            // three level  batery
            batteryImageView.setImageResource(R.drawable.ic_battery3);
        }
        else if(battery_percentage > 60 && battery_percentage <=80)
        {
            // four level batery
            batteryImageView.setImageResource(R.drawable.ic_battery4);
        }
        else if(battery_percentage > 80)
        {
            // full level battery.
            batteryImageView.setImageResource(R.drawable.ic_battery5);
        }


    }

}
