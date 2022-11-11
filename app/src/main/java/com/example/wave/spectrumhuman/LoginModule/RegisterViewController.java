package com.example.wave.spectrumhuman.LoginModule;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.method.PasswordTransformationMethod;
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
import android.widget.Toast;


import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.HomeModule.PersonalInformationViewController;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by Rise on 19/09/2017.
 */

public class RegisterViewController extends AppCompatActivity  {

    EditText userNameTextField, passwordTextField, confirmPasswordTextField, otp, email, pass;
    public String st_emailorphone, st_psw, st_confirm_psw;
    int passwordNotVisible = 1;
    Dialog verifyDialog, dialog;
    TextView registertext, emailphone, passwordtext, confom;
    ImageView imageView;
    Handler handler = new Handler();
    Button signup;
    TelephonyManager telephonyManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        ButterKnife.bind(this);
        LocationTracker.getInstance().fillContext(getApplicationContext());
        LocationTracker.getInstance().startLocation();
        updateLanguageTexts();
    }

    public String getDeviceId() {


        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String id = telephonyManager.getDeviceId();
        Log.e("tellid", "" + telephonyManager.getDeviceId());

        return id;
    }
    ///////////// insilizing//////////
    private void init() {

        userNameTextField = (EditText) findViewById(R.id.phoneoremail);
        passwordTextField = (EditText) findViewById(R.id.password);
        confirmPasswordTextField = (EditText) findViewById(R.id.conformpassword);

        registertext = (TextView) findViewById(R.id.registertext);
        emailphone = (TextView) findViewById(R.id.textemailphone);
        passwordtext = (TextView) findViewById(R.id.textpassword);
        confom = (TextView) findViewById(R.id.textconformpassword);
        signup = (Button) findViewById(R.id.signinbutton);


    }

    public void updateLanguageTexts() {


        registertext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.REGISTER_KEY));

        emailphone.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.EMAIL_KEY));

        passwordtext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PASSWORD_KEY));

        confom.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_PASSWORD_KEY));

        signup.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SIGNUP_KEY));


    }

    ///////////////////// onclick signin button///////////////
    @BindView(R.id.signinbutton)
    Button button;

    @OnClick(R.id.signinbutton)
    public void sign() {
        signupAction();
        // alertDialogueForResend();
        // showRefreshDialogue();

    }

    @OnClick(R.id.back)
    public void back() {

        finish();

    }


    @OnCheckedChanged(R.id.chk)
    public void onChecked(boolean checked) {
        if (checked) {
            passwordTextField.setTransformationMethod(null);
        } else {
            passwordTextField.setTransformationMethod(new PasswordTransformationMethod());

        }
        // cursor reset his position so we need set position to the end of text
        passwordTextField.setSelection(passwordTextField.getText().length());
    }


    @OnCheckedChanged(R.id.chk1)
    public void onCheck(boolean checked) {
        if (checked) {
            confirmPasswordTextField.setTransformationMethod(null);
        } else {
            confirmPasswordTextField.setTransformationMethod(new PasswordTransformationMethod());

        }
        // cursor reset his position so we need set position to the end of text
        confirmPasswordTextField.setSelection(confirmPasswordTextField.getText().length());
    }

    //////////////////////// Validations for////////////
    public void signupAction() {
        st_emailorphone = userNameTextField.getText().toString();
        st_psw = passwordTextField.getText().toString();
        st_confirm_psw = confirmPasswordTextField.getText().toString();
        String regexStr = "^[0-9]*$+_/!#%&()";
        if (st_emailorphone.length() > 0) {
            if (st_emailorphone.equals(regexStr) && (st_emailorphone.length() > 9)) {
                if (st_psw.length() > 0) {
                    if (isValidPasword(st_psw)) {
                        if (st_confirm_psw.length() > 0) {
                            if (st_psw.equals(st_confirm_psw)) {
                                if (isConn()) {
                                    //progress dialoge
                                    showRefreshDialogue();
                                    registerData();

                                } else {
                                    new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                                }

                            } else {
                                new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_PASSWORD_SAME_AS_ABOVE_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                            }

                        } else {
                            new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_PASSWORD_CAN_NOT_EMPTY_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                        }

                    } else {
                        new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PASSWORD_NOT_STRONG_ALERT_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    }
                } else {
                    new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_PASSWORD), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }
            } else {
                if (isValidEmail(st_emailorphone)) {
                    if (st_psw.length() > 0) {
                        if (isValidPasword(st_psw)) {
                            if (st_confirm_psw.length() > 0) {
                                if (st_psw.equals(st_confirm_psw)) {
                                    if (isConn()) {
                                        //progress dialoge
                                        showRefreshDialogue();
                                        registerData();
                                    } else {

                                        new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                                    }


                                } else {
                                    new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_PASSWORD_SAME_AS_ABOVE_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                                }

                            } else {

                                new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_PASSWORD_CAN_NOT_EMPTY_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                            }

                        } else {


                            new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PASSWORD_NOT_STRONG_ALERT_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                        }

                    } else {
                        new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_PASSWORD), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));


                    }

                } else {

                    new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_VALID_EMAIL_ID_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }
        } else {

            new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_EMAIL_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

        }


    }

    public boolean isValidEmail(String target) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(target).matches();
    }

    // Validate password
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

    ////////////////////////////RegisterAps/////////////////////////////////
    private void registerData() {
        Log.e("callmethod", "call");
        UserServerObject requestBody = new UserServerObject();
        requestBody.setUserName(st_emailorphone.trim());
        requestBody.setPassword(st_psw.trim());
        Log.e("em", "" + st_emailorphone);
        requestBody.setLatitude(String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude()));
        requestBody.setLongitude(String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude()));
        requestBody.setRegisterTime(getCurrentTime());
        requestBody.setRegister_type(Constants.RegisterTypes.Manual.toString());
        requestBody.setLanguage(LanguageTextController.getInstance().getCurrentSystemLanguage());

        Log.e("reg", "" + requestBody.getLatitude());


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);


        Call<UserServerObject> callable = api.register(requestBody);
        //final long finalUnixTime = unixTime;
        callable.enqueue(new Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {
                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();
                Log.e("codefor3", "call" + statusCode);
                Log.e("message", "call" + message);

                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {
                        alertDialogueForResend();
                    } else if (statusCode.equals("5")) {
                        new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.USER_ALREADY_HAVE_ACCOUNT_PLEASE_LINK), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    } else if (statusCode.equals("6")) {
                        setPasswordAlert();
                    } else {
                    }
                }

                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {

                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();
                failureRegisteralert();
            }
        });

    }


    public void setPasswordAlert() {

        Log.e("responsealert", "call");
        final Dialog registeralert = new Dialog(this);
        registeralert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        registeralert.setCancelable(false);
        registeralert.setCanceledOnTouchOutside(false);
        registeralert.setCancelable(true);
        registeralert.setContentView(R.layout.activity_registeralert);

        registeralert.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        TextView text = (TextView) registeralert.findViewById(R.id.text_info);
        text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CREATE_PASSWORD));

        TextView textView1 = (TextView) registeralert.findViewById(R.id.textView1);
        textView1.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ALERT_ALREADY_HAVE_ACCOUNT_IN_SOCIAL_MEDIA_KEY));

        EditText editText = (EditText) registeralert.findViewById(R.id.textViewemail);
        editText.setText(userNameTextField.getText().toString().trim());
        Log.e("edituser", "" + userNameTextField.getText().toString().trim());

        final Button cancel = (Button) registeralert.findViewById(R.id.btn_cancelregister);
        cancel.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registeralert.cancel();


            }


        });
        Button forgotpassword = (Button) registeralert.findViewById(R.id.btn_forgot);


        forgotpassword.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FORGOT_PASSWORD_KEY));
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                moveToForgetPasswordPage(userNameTextField.getText().toString().trim());

                registeralert.cancel();

                userNameTextField.getText().clear();
                passwordTextField.getText().clear();
                confirmPasswordTextField.getText().clear();

            }


        });
        registeralert.show();


    }

    private void moveToForgetPasswordPage(String emailInfo) {

        Intent intent = new Intent(RegisterViewController.this, ForgotpasswordViewController.class);
        intent.putExtra("email", emailInfo);
        startActivity(intent);

    }

    /////////////////////////////////////Verify aps/////////////////////////////
    private void VerifyData(String email, String pin, final String password, final String attempt_time) {
        String to = "verify";

        SharedPreferences tokenPreferences = getSharedPreferences("tokendeviceids", Context.MODE_PRIVATE);

        String tokenId = tokenPreferences.getString("tokenid", null);
        String deviceId = tokenPreferences.getString("deviceid", null);

        UserServerObject requestBody = new UserServerObject();
        requestBody.setUserName(email.trim());
        Log.e("em", "" + email);
        requestBody.setOtpStr(pin.trim());
        Log.e("otp", "" + pin);
        requestBody.setTo(to);

        requestBody.setDeviceid(deviceId);
        Log.e("verifytellid", "" + deviceId);

        requestBody.setDeviceToken(tokenId);
        Log.e("verifyfirebase", "" + tokenId);


        requestBody.setAttempt_time(attempt_time);
        Log.e("attem", "" + attempt_time);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<UserServerObject> callable = api.verify(requestBody);
        //final long finalUnixTime = unixTime;
        callable.enqueue(new Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {
                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();
                Log.e("codefor3", "call" + statusCode);
                Log.e("codefor3message", "call" + message);

                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {

                        String latitude = String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude());
                        if (latitude == null) {
                            latitude = "0.0";
                        }
                        String longitude = String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude());
                        if (latitude == null) {
                            longitude = "0.0";
                        }
                        User user = new User();
                        user.setUserName(st_emailorphone.trim());
                        //user.setAttempt_time(attempt_time);
                        user.setLatitude(String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude()));
                        user.setLongitude(String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude()));
                        user.setVerified(true);
                        user.setPassword(st_psw);
                        user.setRegisterType(Constants.RegisterTypes.Manual.toString());
                        String preferredLanguage = LanguageTextController.getInstance().getCurrentSystemLanguage();
                        user.setPreferedLanguage(preferredLanguage);

                        UserDataController.getInstance().insertUserData(user);
                        startActivity(new Intent(getApplicationContext(), PersonalInformationViewController.class));

                        Toast.makeText(getApplicationContext(), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.REGISTRATION_SUCCESS_KEY), Toast.LENGTH_SHORT).show();
                    } else if (statusCode.equals("1"))
                    {
                        new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.YOUR_OTP_GOT_EXPIRED_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    }else if (statusCode.equals("0"))
                    {
                        new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.INVALID_OTP_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    }
                }

                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();


            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {

                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();
                failureVerifyalert();
            }
        });



    }


    //////////////////////////////Dilog/////////////////////////
    public void showRefreshDialogue() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_animate);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        imageView = (ImageView) dialog.findViewById(R.id.image_rottate);
        TextView textView = (TextView) dialog.findViewById(R.id.connecting);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));

        handler.postDelayed(updateTimerThread, 100);
    }
    public void hideRefreshDialogue(){

        handler.removeCallbacks(updateTimerThread);
        dialog.dismiss();
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

    public void alertDialogueForResend() {
        Log.e("alert", "call");
        verifyDialog = new Dialog(this);
        verifyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        verifyDialog.setCancelable(false);
        verifyDialog.setCanceledOnTouchOutside(false);
        verifyDialog.setCancelable(true);
        verifyDialog.setContentView(R.layout.authentication_dialogue);
        verifyDialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        otp = (EditText) verifyDialog.findViewById(R.id.ed_code);
        otp.setHint(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_YOUR_OTP_KEY));

        TextView text_auth = (TextView) verifyDialog.findViewById(R.id.text_auth);
        text_auth.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_AUTHENTICATION_CODE_KEY));


        final Button verify = (Button) verifyDialog.findViewById(R.id.btn_verify);
        verify.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.VERIFY_KEY));

        Button resend = (Button) verifyDialog.findViewById(R.id.btn_resend);
        resend.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.RE_SEND_KEY));

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (otp.getText().toString().length() > 0) {

                    if (isConn()){
                        showRefreshDialogue();
                        VerifyData(st_emailorphone, otp.getText().toString(), st_psw, getCurrentTime());

                    }else {
                        new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                    }

                } else {
                    new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_VERIFICATION_CODE_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }


        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConn()){
                    showRefreshDialogue();
                    registerData();
                }else {
                    new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                }

            }
        });
        verifyDialog.show();


    }

    public String getCurrentTime() {

        long unixTime;
        unixTime = System.currentTimeMillis() / 1L;
        String attempt_time = String.valueOf(System.currentTimeMillis() / 1L);

        Log.e("attem", "" + attempt_time);
        return attempt_time;

    }

    public void failureRegisteralert() {

        Log.e("responsealert", "call");
        final Dialog  failurealert = new Dialog(this);
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
                failurealert.dismiss();

            }


        });
        Button retry = (Button) failurealert.findViewById(R.id.btn_failureretry);
        // retry.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.Ret ));
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConn()){
                    failurealert.dismiss();
                    showRefreshDialogue();
                    registerData();
                }else {
                    failurealert.dismiss();
                    new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }
            }


        });
    }
    public void failureVerifyalert() {

        Log.e("responsealert", "call");
        final Dialog  failurealert = new Dialog(this);
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
                failurealert.dismiss();

            }


        });
        Button retry = (Button) failurealert.findViewById(R.id.btn_failureretry);
        // retry.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.Ret ));
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConn()){
                    failurealert.dismiss();
                    showRefreshDialogue();
                    VerifyData(st_emailorphone, otp.getText().toString(), st_psw, getCurrentTime());
                }else {
                    failurealert.dismiss();
                    new AlertShowingDialog(RegisterViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }
            }


        });
    }
}
