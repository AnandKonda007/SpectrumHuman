package com.example.wave.spectrumhuman.TestModule;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;
import com.example.wave.spectrumhuman.DataBase.MemberDataController;
import com.example.wave.spectrumhuman.DataBase.OfflineDataController;
import com.example.wave.spectrumhuman.DataBase.UrineResultsDataController;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.Graphs.UrineTestDataCreatorController;
import com.example.wave.spectrumhuman.HomeModule.HomeActivityViewController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.LoginModule.Constants;
import com.example.wave.spectrumhuman.LoginModule.LocationTracker;
import com.example.wave.spectrumhuman.Models.UrineresultsModel;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.UrineResultsServerObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rise on 22/09/2017.
 */

public class AnalyzingPageViewController  extends AppCompatActivity {
    Handler timerHandler = new Handler();
    int pStatus;
    ImageView image_animate;
    ArrayList<Integer> imageArray=new ArrayList<>();
    TextView abort,step3;
    Toolbar mToolbar;
    ImageView back,back1;
    ImageView add;
    String strTestId;
    ImageView imageView;
    Dialog urine_dialogue,failurealert;
    Handler urine_handler=new Handler();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationTracker.getInstance().fillContext(getApplicationContext());
        LocationTracker.getInstance().startLocation();

        setContentView(R.layout.activity_analyzing);
        imageArray.add(R.drawable.ic_analyze1) ;
        imageArray.add(R.drawable.ic_analyze2);
        imageArray.add(R.drawable.ic_analyze3);
        imageArray.add(R.drawable.ic_analyze4);
        imageArray.add(R.drawable.ic_analyze1);

        image_animate = (ImageView) findViewById(R.id.animation);
        abort = (TextView) findViewById(R.id.abort);
        abort.setOnClickListener(manimate);
        step3=(TextView) findViewById(R.id.step3);
        setToolbar();
        updateLanguageTexts();
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        pStatus=5;
        image_animate.setVisibility(View.VISIBLE);
        timerHandler.postDelayed(updateTimerThread, 1000);

    }
    @Override
    protected void onStop()
    {
        super.onStop();
        Log.e("Test", "Home button pressed!");
        pStatus=0;
        image_animate.setVisibility(View.GONE);
        timerHandler.removeCallbacks(updateTimerThread);
    }

    View.OnClickListener manimate = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            timerHandler.removeCallbacks(updateTimerThread);
            final Dialog dialog = new Dialog(AnalyzingPageViewController.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.stopanimate_alert);
            dialog.show();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

            TextView  text = (TextView) dialog.findViewById(R.id.text_reminder);
            text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ALERT_KEY));

            TextView  text1 = (TextView) dialog.findViewById(R.id.text);
            text1.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.STOP_ANALYZING_TEST_ALERT_KEY));

            Button no = (Button) dialog.findViewById(R.id.btn_no);
            no.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_KEY));

            Button yes = (Button) dialog.findViewById(R.id.btn_yes);
            yes.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.YES_KEY));


            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    timerHandler.postDelayed(updateTimerThread, 1000);
                }
            });
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    timerHandler.removeCallbacks(updateTimerThread);
                    startActivity(new Intent(getApplicationContext(),HomeActivityViewController.class));

                }
            });
        }


    };
    public void updateLanguageTexts() {
        step3.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.STEP_3_KEY));
        abort.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ABORT_BUTTON_KEY));

    }

    public void setToolbar() {


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar( mToolbar);
        back = (ImageView) mToolbar.findViewById(R.id.back_arraow);
        back1 = (ImageView) mToolbar.findViewById(R.id.backimage);
        back1.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        //
        TextView toolbartext = (TextView)  mToolbar.findViewById(R.id.tool_txt);
        toolbartext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ANALZING_KEY));

        add=(ImageView) mToolbar.findViewById(R.id.add);
        add.setVisibility(View.INVISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CircleProgressbarViewController.isBackbtnClicked=true;
                timerHandler.removeCallbacks(updateTimerThread);
                finish();
            }
        });
    }
    public  Runnable updateTimerThread = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            image_animate.setVisibility(View.VISIBLE);
            image_animate.setBackgroundResource(imageArray.get(pStatus-1));
            timerHandler.postDelayed(this, 1000);
            pStatus = pStatus -1;

            if(pStatus==0){
                image_animate.setVisibility(View.VISIBLE);
                Log.e("profor100",""+pStatus);
                timerHandler.removeCallbacks(updateTimerThread);
                if (isConn()){
                    showRefreshDialogue();
                    sendUrineDataToServer();
                }else {
                    Log.e("noconnection","call");
                  //  showRefreshDialogue();
                    loadResultsInOffLine();
                }
                pStatus=5;
            }
        }
    };
    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }
    public void loadResultsInOffLine(){
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadResultsInOffLineDataTODb();
             //   hideRefreshDialogue();
                startActivity(new Intent(getApplicationContext(),ResultPageViewController.class));

            }
        }, 2000 * 1);
    }
    public void loadResultsInOffLineDataTODb(){

        final UrineResultsServerObject objResultsModel =new UrineResultsServerObject();
        objResultsModel.setRelationName(MemberDataController.getInstance().currentMember.getMname());
        objResultsModel.setRelationType(MemberDataController.getInstance().currentMember.getMrelationshipname());
        objResultsModel.setMail(UserDataController.getInstance().currentUser.getUserName());
        objResultsModel.setMember_id(MemberDataController.getInstance().currentMember.getMember_Id());
        objResultsModel.setRbcValue(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.rbcMinimumValue, Constants.rbcMaximumValue)));
        objResultsModel.setBillirubinValue(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.billirubinMinimumValue,Constants.billirubinMaximumValue)));
        objResultsModel.setUrobiliogen(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.uroboliogenMinimumValue,Constants.uroboliogenMaximumValue)));
        objResultsModel.setKetones(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.ketonesMinimumValue,Constants.ketonesMaximumValue)));
        objResultsModel.setProtein(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.proteinMinimumValue,Constants.proteinMaximumValue)));
        objResultsModel.setNitrite(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.nitriteMinimumValue,Constants.nitriteMaximumValue)));
        objResultsModel.setGlucose(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.glucoseMinimumValue,Constants.glucoseMaximumValue)));
        objResultsModel.setPh(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.phMinimumValue,Constants.phMaximumValue)));
        objResultsModel.setSg(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.sgMinimumValue,Constants.sgMaximumValue)));
        objResultsModel.setLeokocit(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.leucocyteMinimumValue,Constants.leucocyteMaximumValue)));
        objResultsModel.setLat(String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude()));
        objResultsModel.setLang(String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude()));
        objResultsModel.setTestedTime(String.valueOf( System.currentTimeMillis() / 1000L));
        Log.e("setTestedTime","call"+objResultsModel.getTestedTime());
        Log.e("getGlucose","call"+objResultsModel.getGlucose());
        ///

        DecimalFormat dfbilii = new DecimalFormat("#.#",new DecimalFormatSymbols(Locale.ENGLISH));
        DecimalFormat dfnitrite = new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.ENGLISH));
        DecimalFormat  dfsg = new DecimalFormat("#.###",new DecimalFormatSymbols(Locale.ENGLISH));

        objResultsModel.setBillirubinValue(dfbilii.format(Double.parseDouble(objResultsModel.getBillirubinValue())));
        objResultsModel.setUrobiliogen(dfbilii.format(Double.parseDouble(objResultsModel.getUrobiliogen())));
        objResultsModel.setNitrite(dfnitrite.format(Double.parseDouble(objResultsModel.getNitrite())));
        objResultsModel.setPh(dfbilii.format(Double.parseDouble(objResultsModel.getPh())));
        objResultsModel.setSg(dfsg.format(Double.parseDouble(objResultsModel.getSg())));
        //
        String latitude = String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude());
        if (latitude == null) {
            latitude = "0.0";
        }

        String longitude = String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude());
        if (longitude == null) {
            longitude = "0.0";
        }

        UrineResultsDataController.getInstance().
                insertUrineResults(String.valueOf( System.currentTimeMillis() / 1000L),
                        objResultsModel.getMember_id(),
                        objResultsModel.getRelationName(),
                        objResultsModel.getRelationType(),
                        objResultsModel.getTestedTime(),
                        MemberDataController.getInstance().currentMember.getUser_Id(),
                        Integer.parseInt(objResultsModel.getRbcValue()),
                        Double.parseDouble(objResultsModel.getBillirubinValue()),
                        Double.parseDouble(objResultsModel.getUrobiliogen()),
                        Integer.parseInt(objResultsModel.getKetones()),
                        Integer.parseInt(objResultsModel.getProtein()),
                        Double.parseDouble(objResultsModel.getNitrite()),
                        Integer.parseInt(objResultsModel.getGlucose()),
                        Double.parseDouble(objResultsModel.getPh()),
                        Double.parseDouble(objResultsModel.getSg()),
                        Integer.parseInt(objResultsModel.getLeokocit()),
                        false,latitude,longitude
                );
    }

    public void sendUrineDataToServer() {
        Log.e("insertUrineDataToServer","call");
        Log.e("insertcurrentUser",""+UserDataController.getInstance().currentUser.getUserName());
        Log.e("insertUrineDataToServer",""+MemberDataController.getInstance().currentMember.getMember_Id());
        Log.e("UserIdfromanalizing",""+MemberDataController.getInstance().currentMember.getUser_Id());
        Log.e("insertUrineDataToServer",""+MemberDataController.getInstance().currentMember.getMname());

        final UrineResultsServerObject urineResultsServerObject =new UrineResultsServerObject();
        urineResultsServerObject.setRelationName(MemberDataController.getInstance().currentMember.getMname());
        urineResultsServerObject.setRelationType(MemberDataController.getInstance().currentMember.getMrelationshipname());
        urineResultsServerObject.setMail(UserDataController.getInstance().currentUser.getUserName());
        urineResultsServerObject.setMember_id(MemberDataController.getInstance().currentMember.getMember_Id());
        urineResultsServerObject.setRbcValue(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.rbcMinimumValue, Constants.rbcMaximumValue)));
        urineResultsServerObject.setBillirubinValue(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.billirubinMinimumValue,Constants.billirubinMaximumValue)));
        urineResultsServerObject.setUrobiliogen(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.uroboliogenMinimumValue,Constants.uroboliogenMaximumValue)));
        urineResultsServerObject.setKetones(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.ketonesMinimumValue,Constants.ketonesMaximumValue)));
        urineResultsServerObject.setProtein(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.proteinMinimumValue,Constants.proteinMaximumValue)));
        urineResultsServerObject.setNitrite(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.nitriteMinimumValue,Constants.nitriteMaximumValue)));
        urineResultsServerObject.setGlucose(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.glucoseMinimumValue,Constants.glucoseMaximumValue)));
        urineResultsServerObject.setPh(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.phMinimumValue,Constants.phMaximumValue)));
        urineResultsServerObject.setSg(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.sgMinimumValue,Constants.sgMaximumValue)));
        urineResultsServerObject.setLeokocit(String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.leucocyteMinimumValue,Constants.leucocyteMaximumValue)));
        urineResultsServerObject.setLat(String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude()));
        urineResultsServerObject.setLang(String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude()));
        urineResultsServerObject.setTestedTime(String.valueOf( System.currentTimeMillis() / 1000L));
        Log.e("setTestedTime","call"+urineResultsServerObject.getTestedTime());
        Log.e("getGlucose","call"+urineResultsServerObject.getGlucose());
        ///

        DecimalFormat dfbilii = new DecimalFormat("#.#",new DecimalFormatSymbols(Locale.ENGLISH));
        DecimalFormat dfnitrite = new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.ENGLISH));
        DecimalFormat  dfsg = new DecimalFormat("#.###",new DecimalFormatSymbols(Locale.ENGLISH));

        urineResultsServerObject.setBillirubinValue(dfbilii.format(Double.parseDouble(urineResultsServerObject.getBillirubinValue())));
        urineResultsServerObject.setUrobiliogen(dfbilii.format(Double.parseDouble(urineResultsServerObject.getUrobiliogen())));
        urineResultsServerObject.setNitrite(dfnitrite.format(Double.parseDouble(urineResultsServerObject.getNitrite())));
        urineResultsServerObject.setPh(dfbilii.format(Double.parseDouble(urineResultsServerObject.getPh())));
        urineResultsServerObject.setSg(dfsg.format(Double.parseDouble(urineResultsServerObject.getSg())));
        //
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<UrineResultsServerObject> callable=api.urineData(urineResultsServerObject);

        callable.enqueue(new Callback<UrineResultsServerObject>() {
            @Override
            public void onResponse(Call<UrineResultsServerObject> call, Response<UrineResultsServerObject> response) {
                hideRefreshDialogue();
                String statusCode = response.body().getResponse();
                String message=response.body().getMessage();
                Log.e("codefor3","call"+statusCode);
                if(statusCode.equals("3")){
                    strTestId=response.body().getTest_id();
                    Log.e("strTestId","call"+strTestId);
                    insertUrineDataIntoDataBase(urineResultsServerObject);
                    startActivity(new Intent(getApplicationContext(),ResultPageViewController.class));
                }else if(statusCode.equals("5")){
                }else {
                }
            }
            @Override
            public void onFailure(Call<UrineResultsServerObject> call, Throwable t) {
                hideRefreshDialogue();
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
                    sendUrineDataToServer();

                }else {
                    failurealert.dismiss();
                    new AlertShowingDialog(AnalyzingPageViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }


        });


    }



    public void showRefreshDialogue(){
        urine_dialogue=new Dialog(this);
        urine_dialogue.requestWindowFeature(Window.FEATURE_NO_TITLE);
        urine_dialogue.setContentView(R.layout.activity_animate);
        urine_dialogue.setCanceledOnTouchOutside(false);
        //  dialog.create();
        urine_dialogue.show();
        TextView textView=(TextView)urine_dialogue.findViewById(R.id.connecting);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));

        urine_dialogue.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        imageView=(ImageView)urine_dialogue.findViewById(R.id.image_rottate) ;
        urine_handler.postDelayed(updateTimerThreadOne, 100);
    }
    public  Runnable updateTimerThreadOne = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            imageView.setVisibility(View.VISIBLE);
            urine_handler.postDelayed(this, 600);
            RotateAnimation rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(600);
            rotate.setInterpolator(new LinearInterpolator());
            imageView.startAnimation(rotate);
        }
    };
    public void hideRefreshDialogue(){
        urine_dialogue.dismiss();
        urine_handler.removeCallbacks(updateTimerThread);
    }
    public void insertUrineDataIntoDataBase(UrineResultsServerObject urineResultsServerObject){
        UrineresultsModel  urineresultsModel=new UrineresultsModel();
        urineresultsModel.setTest_id(strTestId);
        urineresultsModel.setRelationName(urineResultsServerObject.getRelationName());
        urineresultsModel.setRelationtype(urineResultsServerObject.getRelationType());
        urineresultsModel.setMember_Id(urineResultsServerObject.getMember_id());
        urineresultsModel.setTestedTime(urineResultsServerObject.getTestedTime());

        urineresultsModel.setRbcValue(Integer.parseInt(urineResultsServerObject.getRbcValue()));
        urineresultsModel.setBillirubinValue(Double.parseDouble(urineResultsServerObject.getBillirubinValue()));
        urineresultsModel.setUroboliogenValue(Double.parseDouble(urineResultsServerObject.getUrobiliogen()));
        urineresultsModel.setKetonesValue(Integer.parseInt(urineResultsServerObject.getKetones()));
        urineresultsModel.setProteinValue(Integer.parseInt(urineResultsServerObject.getProtein()));
        urineresultsModel.setNitriteValue(Double.parseDouble(urineResultsServerObject.getNitrite()));
        urineresultsModel.setGlucoseValue(Integer.parseInt(urineResultsServerObject.getGlucose()));
        urineresultsModel.setPhValue(Double.parseDouble(urineResultsServerObject.getPh()));
        urineresultsModel.setSgValue(Double.parseDouble(urineResultsServerObject.getSg()));
        urineresultsModel.setLeucocyteValue(Integer.parseInt(urineResultsServerObject.getLeokocit()));
        /////////////

        String latitude = String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude());
        if (latitude == null) {
            latitude = "0.0";
        }

        String longitude = String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude());
        if (longitude == null) {
            longitude = "0.0";
        }

        UrineResultsDataController.getInstance().
                insertUrineResults("",
                        urineresultsModel.getMember_Id(),
                        urineresultsModel.getRelationName(),
                        urineresultsModel.getRelationtype(),
                        urineresultsModel.getTestedTime(),
                        MemberDataController.getInstance().currentMember.getUser_Id(),
                        urineresultsModel.getRbcValue(),
                        urineresultsModel.getBillirubinValue(),
                        urineresultsModel.getUroboliogenValue(),
                        urineresultsModel.getKetonesValue(),
                        urineresultsModel.getProteinValue(),
                        urineresultsModel.getNitriteValue(),
                        urineresultsModel.getGlucoseValue(),
                        urineresultsModel.getPhValue(),
                        urineresultsModel.getSgValue(),
                        urineresultsModel.getLeucocyteValue(),
                        true,latitude,longitude
                );
        Log.e("currentMember","call"+MemberDataController.getInstance().currentMember.getUser_Id());
    }
    @Override
    public void onBackPressed() {
        timerHandler.removeCallbacks(updateTimerThread);
        CircleProgressbarViewController.isBackbtnClicked=true;
        finish();
        super.onBackPressed();
    }
}

