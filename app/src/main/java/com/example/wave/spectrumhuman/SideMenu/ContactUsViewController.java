package com.example.wave.spectrumhuman.SideMenu;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;
import com.example.wave.spectrumhuman.DataBase.MemberDataController;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.LoginModule.ChangePasswordViewController;
import com.example.wave.spectrumhuman.LoginModule.NewpasswordViewController;
import com.example.wave.spectrumhuman.Models.Member;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.ContactUsServerObject;

import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by REDDYJP on 23/09/2017.
 */

public class ContactUsViewController extends AppCompatActivity  {
    Toolbar toolbar;
    ImageView home;
    ImageView back;
    ImageView add;
    EditText editemail, editusername, editmessage;
    Dialog dialog;
    Member adminMember;
    TextView tool_txt,name,email,message;
    Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);
        ButterKnife.bind(this);
        adminMember = MemberDataController.getInstance().getAdminDetails();
        editemail = (EditText) findViewById(R.id.contactusemail);
        Member adminmember = MemberDataController.getInstance().getAdminDetails();
        editemail.setText("" + adminmember.getEmail());
        editusername = (EditText) findViewById(R.id.editname);
        editmessage = (EditText) findViewById(R.id.editmessage);

        submit=(Button)findViewById(R.id.submit) ;
         name =(TextView)findViewById(R.id.textname);
         email =(TextView)findViewById(R.id.textemail);
         message =(TextView)findViewById(R.id.message);
        editusername.setText(adminmember.getMname());

        setToolbar();
        updateLanguageTexts();
    }
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        home = (ImageView) toolbar.findViewById(R.id.home);
        back = (ImageView) toolbar.findViewById(R.id.backimage);
        back.setVisibility(View.INVISIBLE);

        add = (ImageView) toolbar.findViewById(R.id.add);
        add.setVisibility(View.INVISIBLE);

        home.setImageResource(R.drawable.ic_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("Home", "home");
                finish();
            }
        });
        TextView tool_txt=(TextView) toolbar.findViewById(R.id.tool_txt);
        tool_txt.append(getString(R.string.contactus));
        tool_txt.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONTACT_US_KEY));


    }
    public void updateLanguageTexts() {

        name.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONTACT_NAME_KEY));


        email.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONTACT_EMAIL_KEY));

        message.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FEEDBACK_MESSAGE_KEY));


        submit.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SUBMIT_BUTTON_KEY));



    }



    public void  validations(){


        if(editemail.getText().toString().isEmpty()){

            new AlertShowingDialog(ContactUsViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_EMAIL_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));


        }else if(!isValidEmail(editemail.getText().toString())) {
            new AlertShowingDialog(ContactUsViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_VALID_EMAIL_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

        }else if(editmessage.getText().toString().isEmpty()){

            new AlertShowingDialog(ContactUsViewController.this,LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_GIVE_YOUR_FEEDBACK_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

        }
        else
        {
            if (isConn()){
                showRefreshDialogue();
                contactusAPI();
            }else {
                new AlertShowingDialog(ContactUsViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

            }

        }


    }
    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }
    public boolean isValidEmail(String target) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(target).matches();
    }

    @OnClick(R.id.submit)
    public void action() {

     validations();
    }

    public void contactusAPI() {

        String name  = adminMember.getMname();

        if(!editusername.getText().toString().isEmpty())
        {
            name = editusername.getText().toString();
        }
        ContactUsServerObject requestBody = new ContactUsServerObject();

        requestBody.setUserName(MemberDataController.getInstance().currentMember.getEmail());
        Log.e("user", "" + editemail.getText().toString());

        requestBody.setEMail(editemail.getText().toString());
        Log.e("email", "" + editemail.getText().toString());

        requestBody.setFeedback(editmessage.getText().toString());
        Log.e("feedback", "" + editmessage.getText().toString());

        requestBody.setName(name);
        Log.e("name", "" + name);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<ContactUsServerObject> callable = api.feedback(requestBody);
        callable.enqueue(new Callback<ContactUsServerObject>() {
            @Override
            public void onResponse(Call<ContactUsServerObject> call, Response<ContactUsServerObject> response) {
                String message = response.body().getMessage();
                Log.e("codefor3", "call" + message);
                String statusCode = response.body().getResponse();

                if (statusCode.equals(null)) {
                } else {
                    if (statusCode.equals("3")) {
                        Log.e("callresposecode", "call" + response.code());
                        editmessage.setText("");
                        allertchange(message);

                    } else if (statusCode.equals("5")) {

                    }
                }

                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<ContactUsServerObject> call, Throwable t) {

                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();
                failurealert();
            }
        });

    }


    public void failurealert() {

        Log.e("responsealert", "call");
        final Dialog  failurealert = new Dialog(this);
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
                    contactusAPI();

                }else {
                    failurealert.dismiss();
                    new AlertShowingDialog(ContactUsViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }


        });


    }

    ImageView imageView;
    Handler handler = new Handler();

    public void showRefreshDialogue() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_animate);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        //dialog.create();
        dialog.show();
        TextView textView=(TextView)dialog.findViewById(R.id.connecting);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
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
    public void allertchange(String msg) {

        final Dialog dialog = new Dialog(ContactUsViewController.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.changepsw_alert);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        // verifyDialog.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        TextView success = (TextView) dialog.findViewById(R.id.success);
        success.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SUCCESS_KEY));
        TextView txt_msg = (TextView) dialog.findViewById(R.id.txt_msg);

        txt_msg.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys. FEEDBACK_SUCCESS_ALERT_KEY));

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
                dialog.dismiss();
                finish();

            }
        });


    }

}
