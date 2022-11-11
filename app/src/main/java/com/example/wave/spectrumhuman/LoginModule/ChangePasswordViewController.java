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


import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.HomeModule.HomeActivityViewController;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Rise on 20/09/2017.
 */

public class ChangePasswordViewController extends AppCompatActivity {

    Button home;
    EditText oldPasswordTextField, newPasswordTextField;

    String st_emailandphone, st_password, st_newpassword;

    private int passwordNotVisible = 1;
    Dialog dialog,failurealert;
    Handler handler = new Handler();
    UserDataController controller;
    ImageView imageView;

    TextView old, newpassword, change;
    Button done;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        ButterKnife.bind(this);
        init();


        UserDataController.getInstance().fetchUserData();

        st_emailandphone = UserDataController.getInstance().currentUser.getUserName();
        Log.e("gmai", "" + st_emailandphone);

//        st_password = UserDataController.getInstance().currentUser.getPassword();
//        Log.e("pass", "" + st_password);


        home = (Button) findViewById(R.id.home_button);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateLanguageTexts();
    }
    private void init() {


        oldPasswordTextField = (EditText) findViewById(R.id.edit_oldpassword);
        newPasswordTextField = (EditText) findViewById(R.id.edit_newpassword);

        old = (TextView) findViewById(R.id.oldpasswordtext);
        newpassword = (TextView) findViewById(R.id.newpasswordtext);
        change = (TextView) findViewById(R.id.changepasswordtext);
        done = (Button) findViewById(R.id.done);


    }


    public void updateLanguageTexts() {
        change.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CHANGE_PASSWORD_TITLE_KEY));


        old.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OLD_PASSWORD_KEY));

        newpassword.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NEW_PASSWORD_KEY));


        done.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DONE_BUTTON_KEY));


    }

    @OnCheckedChanged(R.id.chk)
    public void onChecked(boolean checked){
        if(checked){
            oldPasswordTextField.setTransformationMethod(null);
        }else {
            oldPasswordTextField.setTransformationMethod(new PasswordTransformationMethod());

        }
        // cursor reset his position so we need set position to the end of text
        oldPasswordTextField.setSelection(oldPasswordTextField.getText().length());
    }

    @OnCheckedChanged(R.id.chk1)
    public void onCheck(boolean checked){
        if(checked){
            newPasswordTextField.setTransformationMethod(null);
        }else {
            newPasswordTextField.setTransformationMethod(new PasswordTransformationMethod());

        }
        // cursor reset his position so we need set position to the end of text
        newPasswordTextField.setSelection(newPasswordTextField.getText().length());
    }


    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.done)
    public void done() {

        mChange();

    }

    @OnClick(R.id.home_button)
    public void home() {
        home.setBackgroundResource(R.drawable.ic_home);

    }

    public void mChange() {

        st_password = oldPasswordTextField.getText().toString();
        st_newpassword = newPasswordTextField.getText().toString();

        if (st_password.length() > 0) {
                if (st_newpassword.length() > 0) {
                    if (isValidPasword(st_newpassword)) {
                        if (!st_password.equals(st_newpassword)){
                            if (isConn()) {
                                //progress dialoge
                                showRefreshDialogue();
                                changepassword(st_emailandphone, st_password, st_newpassword);

                            } else {
                                new AlertShowingDialog(ChangePasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                            }


                        }else {
                            new AlertShowingDialog(ChangePasswordViewController.this,LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ALERT_PASSWORD_MUST_BE_DIFFERT_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                        }
                    } else {
                        new AlertShowingDialog(ChangePasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PASSWORD_NOT_STRONG_ALERT_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    }
                } else {
                    new AlertShowingDialog(ChangePasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_NEW_PASSWORD_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }
        } else {
            new AlertShowingDialog(ChangePasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_THE_PASSWORD_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

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

       /* password = name.getText().toString();
        Log.e("pass", "" + password123);*/
        return isValid;
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }

    /////////////////////////////////////Change aps/////////////////////////////
    private void changepassword(String email, String password, String newpassword) {

        UserServerObject requestBody = new UserServerObject();
        requestBody.setUserName(email);
        Log.e("emaill", "" + email);
        requestBody.setCurrentpassword(password);
        Log.e("pass", "" + password);
        requestBody.setNewpassword(newpassword);
        Log.e("newpassword", "" + newpassword);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<UserServerObject> callable = api.changepassword(requestBody);
        callable.enqueue(new retrofit2.Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, retrofit2.Response<UserServerObject> response) {

                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();
                Log.e("codefor3", "call" + statusCode +""+message);
                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {

                        allertchange();
                        UserDataController.getInstance().currentUser.setPassword(st_newpassword);
                        UserDataController.getInstance().updateUserData(UserDataController.getInstance().currentUser);

                    } else if (statusCode.equals("0")) {
                        new AlertShowingDialog(ChangePasswordViewController.this,LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CURRENT_PASSWORD_IS_WRONG_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    }
                    else if (statusCode.equals("5")) {
                        new AlertShowingDialog(ChangePasswordViewController.this,message, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    }
                }

                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();


            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {

                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();
                failurealert();
            }
        });

    }


    //////////////////////////////Dilog/////////////////////////
    public void showRefreshDialogue() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_animate);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        TextView textView=(TextView)dialog.findViewById(R.id.connecting);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));

        imageView = (ImageView) dialog.findViewById(R.id.image_rottate);
        handler.postDelayed(updateTimerThread, 100);
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


    public void allertchange() {


        final Dialog dialog = new Dialog(ChangePasswordViewController.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.changepsw_alert);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        TextView success = (TextView) dialog.findViewById(R.id.success);
        success.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SUCCESS_KEY));
        //YOUR_PASSWORD_HAS_BEEN_CHANGED_KEY
        // verifyDialog.create();
        TextView txt_msg = (TextView) dialog.findViewById(R.id.txt_msg);
        txt_msg.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.YOUR_PASSWORD_HAS_BEEN_CHANGED_KEY));

        Button ok = (Button) dialog.findViewById(R.id.btn_ok);
        ok.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

        ImageView cancle = (ImageView) dialog.findViewById(R.id.btn_cancle);

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
                dialog.dismiss();
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
                    changepassword(st_emailandphone, st_password, st_newpassword);
                }else {
                    failurealert.dismiss();

                    new AlertShowingDialog(ChangePasswordViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }



            }


        });


    }


}
