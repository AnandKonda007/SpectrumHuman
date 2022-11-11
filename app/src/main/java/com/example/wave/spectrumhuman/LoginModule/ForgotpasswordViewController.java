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
import android.util.Log;
import android.util.Patterns;
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
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.Models.User;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.UserServerObject;
import com.example.wave.spectrumhuman.SideMenu.LanguageViewController;

import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rise on 19/09/2017.
 */

public class ForgotpasswordViewController extends AppCompatActivity  {

    EditText userNameTextField, otp;
    String st_emailorphone,RegTime,attempt_time;
    Dialog dialog;
    ImageView imageView;
    Handler handler = new Handler();
    TextView forgottext,emailphone;
    Button getpassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        ButterKnife.bind(this);
        init();

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null)
        {
            String emailString  = (String) bd.get("email");
            userNameTextField.setText(emailString);

        }


        RegTime= String.valueOf(System.currentTimeMillis() / 1L);
        attempt_time=String.valueOf(System.currentTimeMillis() / 1L);

        updateLanguageTexts();

    }
    private void init() {

        userNameTextField = (EditText) findViewById(R.id.edit_phoneoremail);
        forgottext=(TextView)findViewById(R.id.forgottext);
        emailphone=(TextView)findViewById(R.id.textemailphone);
        getpassword=(Button)findViewById(R.id.getpassword);




    }

    public void updateLanguageTexts() {

        forgottext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FORGOT_PASSWORD_TITLE_KEY));
        getpassword.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.GET_PASSWORD_KEY));
        emailphone.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.EMAIL_KEY ));


    }


    @OnClick(R.id.getpassword)
    public void getpasword() {
        mForgotpsw();

    }

    @OnClick(R.id.back)
    public void back() {
        finish();
    }

    public void mForgotpsw() {


        st_emailorphone = userNameTextField.getText().toString().trim();
        // st_password = password.getText().toString();
        String regexStr = "^[0-9]*$+_/!#%&()";


        if (st_emailorphone.length() > 0) {

            if (!st_emailorphone.equals(regexStr) && (st_emailorphone.length() > 13)) {

                if (isConn()) {
                    //progress dialoge
                    showRefreshDialogue();
                    forgetData(st_emailorphone,RegTime);

                } else {
                    new AlertShowingDialog(ForgotpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }
            } else {
                if (isValidEmail(st_emailorphone)) {


                    if (st_emailorphone.length() > 0) {

                        if (isConn()) {
                            //progress dialoge
                           showRefreshDialogue();
                           forgetData(st_emailorphone,RegTime);

                        } else {
                            new AlertShowingDialog(ForgotpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                        }
                    }

                } else {
                    new AlertShowingDialog(ForgotpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_VALID_EMAIL_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }
        } else {

            new AlertShowingDialog(ForgotpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_EMAIL_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

        }


    }


    public boolean isValidEmail(String target) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(target).matches();
    }

    private boolean isValidPhone(String pass) {
        return pass != null && pass.length() == 13;

    }


    ////////////////////////////ForgotAps/////////////////////////////////
    private void forgetData(String email ,String regtime) {

        UserServerObject requestBody = new UserServerObject();

        requestBody.setUserName(email.trim());
        Log.e("ema", "" + email);

        requestBody.setRegisterTime(regtime);
        Log.e("regtime", "" + regtime);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);


        Call<UserServerObject> callable = api.forgot(requestBody);
        callable.enqueue(new Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {
                String statusCode = response.body().getResponse();
                String message=response.body().getMessage();
                Log.e("codefor3","call"+statusCode);
                Log.e("message","call"+message);

                if(!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {
                        alertDialogueForResend();
                    } else if (statusCode.equals("0")) {
                        new AlertShowingDialog(ForgotpasswordViewController.this, message, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                    }
                }

                hideRefreshDialogue();

            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {
                hideRefreshDialogue();
                failurealertForResend();
            }
        });


    }
    /////////////////////////////////////Verify aps/////////////////////////////
    private void VerifyData(String email, String pin, final String attempt_time) {


        UserServerObject requestBody = new UserServerObject();

        requestBody.setUserName(email.trim());
        Log.e("email", "" + email);
        requestBody.setOtpStr(pin.trim());
        Log.e("otp", "" + pin);
        requestBody.setAttempt_time(attempt_time);
        Log.e("attem", "" + attempt_time);



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);

        Call<UserServerObject> callable = api.verify(requestBody);
        callable.enqueue(new Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {
                String statusCode = response.body().getResponse();
                String message=response.body().getMessage();
                Log.e("codefor3","call"+statusCode);
                if(!statusCode.equals(null)){
                    if(statusCode.equals("3")){

                        if(dialog!= null) {
                            dialog.cancel();
                        }
                        // user = new User();
                        User user = new User();
                        user.setUserName(st_emailorphone);

                        Intent intent = new Intent(ForgotpasswordViewController.this, NewpasswordViewController.class);
                        intent.putExtra("email", userNameTextField.getText().toString().trim());

                        startActivity(intent);

                    }else if (statusCode.equals("1"))
                    {
                        new AlertShowingDialog(ForgotpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.YOUR_OTP_GOT_EXPIRED_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    }else if (statusCode.equals("0"))
                    {
                        new AlertShowingDialog(ForgotpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.INVALID_OTP_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    }
                }
                dialog.dismiss();
                handler.removeCallbacks(updateTimerThread);

            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {
                dialog.dismiss();
                handler.removeCallbacks(updateTimerThread);
                failurealertForVerify();
            }
        });


    }

    public void alertDialogueForResend() {
        Log.e("alert", "call");
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.authentication_dialogue);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        TextView text_auth = (TextView)dialog. findViewById(R.id.text_auth);
        text_auth.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_AUTHENTICATION_CODE_KEY));


        final Button verify = (Button) dialog.findViewById(R.id.btn_verify);
        verify.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.VERIFY_KEY));

        otp = (EditText) dialog.findViewById(R.id.ed_code);
        otp.setHint(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_YOUR_OTP_KEY));


        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //   dialog.cancel();
                if (otp.getText().toString().length()>0)
                {
                    if (isConn()){
                        showRefreshDialogue();
                        VerifyData(st_emailorphone, otp.getText().toString(), attempt_time);

                    }else {
                        new AlertShowingDialog(ForgotpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    }
                }else {
                    new AlertShowingDialog(ForgotpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_VERIFICATION_CODE_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }


            }
        });
        Button resend = (Button) dialog.findViewById(R.id.btn_resend);

        resend.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.RE_SEND_KEY));
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.cancel();
                if (isConn()){
                    showRefreshDialogue();
                    forgetData(st_emailorphone,RegTime);
                }else {
                    new AlertShowingDialog(ForgotpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }


        });
        dialog.show();
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }


    //////////////////////////////Dilog/////////////////////////
    public void showRefreshDialogue() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_animate);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        imageView = (ImageView)dialog.findViewById(R.id.image_rottate);
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
            //rotate.setRepeatCount(3);
        }
    };
    public void failurealertForResend() {

        Log.e("responsealert", "call");
     final  Dialog  failurealert = new Dialog(this);
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
                    forgetData(st_emailorphone,RegTime);
                }else {
                    failurealert.dismiss();
                    new AlertShowingDialog(ForgotpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                }
            }


        });


    }

    public void failurealertForVerify() {

        Log.e("responsealert", "call");
        final  Dialog  failurealert = new Dialog(this);
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
                    VerifyData(st_emailorphone, otp.getText().toString(), attempt_time);
                }else {
                    failurealert.dismiss();
                    new AlertShowingDialog(ForgotpasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                }
            }


        });


    }



}