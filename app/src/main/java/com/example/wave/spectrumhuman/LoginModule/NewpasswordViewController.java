package com.example.wave.spectrumhuman.LoginModule;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.UserServerObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by R on 20/09/2017.
 */

public class NewpasswordViewController extends AppCompatActivity{

    EditText passwordTextField;
    String st_password,st_emailandphone;
    Dialog dialog;
    int passwordNotVisible=1;
    ImageView imageView;
    Handler handler = new Handler();
    TextView newtext,password;
    Button changepassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_newpassword);
       ButterKnife.bind(this);
        init();
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            st_emailandphone = (String) bd.get("email");

        }

        updateLanguageTexts();
    }
    private void init() {

        passwordTextField = (EditText) findViewById(R.id.edit_newpassword);
        newtext=(TextView)findViewById(R.id.forgotpasswordtext);
        password=(TextView)findViewById(R.id.newpasswordtext);
        changepassword=(Button)findViewById(R.id.changepassword);
    }
    public void updateLanguageTexts() {
        newtext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FORGOT_PASSWORD_TITLE_KEY));
        password.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PASSWORD_KEY ));
        changepassword.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CHANGE_PASSWORD_TITLE_KEY ));

    }
    @OnClick (R.id.changepassword)
    public  void newpassword(){
         mConformpassword();


    }
    @OnClick (R.id.back)
    public  void back(){
        startActivity(new Intent(getApplicationContext(),LoginViewController.class));

    }
    @Override
    public void onBackPressed() {    //when click on phone backbutton
        startActivity(new Intent(getApplicationContext(),LoginViewController.class));

    }


    @OnCheckedChanged(R.id.chk)
    public void onChecked(boolean checked){
        if(checked){
            passwordTextField.setTransformationMethod(null);
        }else {
            passwordTextField.setTransformationMethod(new PasswordTransformationMethod());

        }
        // cursor reset his position so we need set position to the end of text
        passwordTextField.setSelection(passwordTextField.getText().length());
    }
    public void mConformpassword() {


        st_password = passwordTextField.getText().toString();

        if (st_password.length() > 0) {
            if (isValidPasword(st_password)) {
                if (isConn()) {
                    //progress dialoge
                    showRefreshDialogue();
                    newpasswordData(st_emailandphone, st_password);

                } else {
                    new AlertShowingDialog(NewpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                }


            } else {
                new AlertShowingDialog(NewpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PASSWORD_NOT_STRONG_ALERT_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

            }
        } else {

            new AlertShowingDialog(NewpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_PASSWORD),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

        }
    }

    private boolean isValidPasword(String password) {
        boolean isValid = false;

        String expression = "^(?=.*[a-z])(?=.*[$@$#!%*?&])[A-Za-z\\d$@$#!%*?&]{8,}";
        CharSequence inputStr = password;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            System.out.println("if");
            isValid = true;
        } else {
            System.out.println("else");
        }
        return isValid;
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }

    /////////////////////////////////////newpasswordaps aps/////////////////////////////
    private void newpasswordData( String email, String password) {


        UserServerObject requestBody = new UserServerObject();

        requestBody.setUserName(email);
        Log.e("email", "" + email);


       requestBody.setPassword(password);
        Log.e("pass", "" + password);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);

        Call<UserServerObject> callable = api.newpassword(requestBody);
        callable.enqueue(new Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {
                String statusCode = response.body().getResponse();
                String message=response.body().getMessage();
                Log.e("codefor3","call"+statusCode);
                Log.e("message","call"+message);

                if(!statusCode.equals(null)){
                    if(statusCode.equals("3")){

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),LoginViewController.class));

                    }
                    else if(statusCode.equals("4")){
                        new AlertShowingDialog(NewpasswordViewController.this, message, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    }
                }
               hideRefreshDialogue();

            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {

               hideRefreshDialogue();
                failurealert();
            }
        });

    }


    //////////////////////////////Dilog/////////////////////////
    public void showRefreshDialogue() {
        int count = 5;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.activity_animate);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        imageView = (ImageView) dialog.findViewById(R.id.image_rottate);
        TextView textView=(TextView)dialog.findViewById(R.id.connecting);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));

        handler.postDelayed(updateTimerThread, 100);
    }
    public void hideRefreshDialogue(){
        dialog.dismiss();
        handler.removeCallbacks(updateTimerThread);
    }
    public Runnable updateTimerThread = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            imageView.setVisibility(View.VISIBLE);
            handler.postDelayed(this, 600);
            RotateAnimation rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(600);
            rotate.setInterpolator(new LinearInterpolator());
            imageView.startAnimation(rotate);
                    }
    };
    public void failurealert() {

        Log.e("responsealert", "call");
       final Dialog failurealert = new Dialog(this);
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

                failurealert.dismiss();


            }


        });
        Button retry= (Button) failurealert.findViewById(R.id.btn_failureretry);
        // retry.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.Ret ));

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isConn()){
                    failurealert.dismiss();
                    showRefreshDialogue();
                    newpasswordData(st_emailandphone, st_password);
                }else {
                    failurealert.dismiss();
                    new AlertShowingDialog(NewpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }
                failurealert.dismiss();

            }


        });


    }



}
