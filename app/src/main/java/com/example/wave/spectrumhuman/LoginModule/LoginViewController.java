package com.example.wave.spectrumhuman.LoginModule;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;
import com.example.wave.spectrumhuman.Alert.RefreshShowingDialog;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.HomeModule.HomeActivityViewController;
import com.example.wave.spectrumhuman.HomeModule.PersonalInformationViewController;
import com.example.wave.spectrumhuman.HomeModule.SideMenuViewController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.Models.User;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.UserServerObject;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Rise on 19/09/2017.
 */

public class LoginViewController extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    EditText userNameTextField, passwordTextField;
    ProgressDialog mProgress;
    int passwordNotVisible = 1;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private int RC_SIGN_IN = 100;
    ImageButton google;
    Button fb,login;
    TwitterLoginButton loginButton;
    TwitterAuthClient mTwitterAuthClient;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    //ProgressDialog progress;
    private ProfileTracker profileTracker;
    private Dialog logindilog, facebookalert, responsealert,failurealert;
    List<String> permissionNeeds = Arrays.asList("user_photos", "email",
            "user_birthday", "public_profile");
    public String socialmediaLoginEmail;
    SharedPreferences sharedPreferences;

    public static RefreshShowingDialog alertDilogue;

    SharedPreferences.Editor sharedPreferencesEditor;

    TextView emailphone, password, or, logintext, connectwith,forgotpassword,signup;

     String st_emailandphone,st_password,email;

    TelephonyManager telephonyManager;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int PERMISSION_REQUEST_CODE = 1;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Twitter.initialize(this);

        alertDilogue=new RefreshShowingDialog(getApplicationContext());

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        sharedPreferences = getApplicationContext().getSharedPreferences("socialMediaLoginDetails", Context.MODE_PRIVATE);

        // GET Latest Location.

        LocationTracker.getInstance().startLocation();
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
//                accessToken = newToken;
                Log.e("sdf", "asdfas" + newToken);
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mProgress = new ProgressDialog(LoginViewController.this);
        mProgress.setMessage("Loading...");
        mProgress.setProgress(Color.RED);
        mProgress.setCancelable(true);
        mProgress.setCanceledOnTouchOutside(false);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("v48TTDxHZhnYsCxZXWYtK8DHJ", "CF2oLh4G3v6avaWldeD60cZtPdtle8Z2uhwK43b5AaYgxG02x5"))
                .debug(true)
                .build();
        Twitter.initialize(config);
        mTwitterAuthClient = new TwitterAuthClient();

        init();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        //google=(ImageButton) findViewById(R.id.google);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

           updateLanguageTexts();

        getDeviceId();

    }
    @Override
    public void onResume() {
        super.onResume();
        // register connection status listener

        EventBus.getDefault().register(this);
        LanguageTextController.getInstance().loadLanguageTexts();
        updateLanguageTexts();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        fb = (Button) findViewById(R.id.fb);
        userNameTextField = (EditText) findViewById(R.id.editText_Email);
        passwordTextField = (EditText) findViewById(R.id.editText_password);

        emailphone = (TextView) findViewById(R.id.emailphone);
        or = (TextView) findViewById(R.id.or);
        password = (TextView) findViewById(R.id.password);
        connectwith = (TextView) findViewById(R.id.text_connect);
        logintext = (TextView) findViewById(R.id.logintext);
        forgotpassword=(TextView)findViewById(R.id.forgotpassword);
        signup=(TextView)findViewById(R.id.signup);
        login=(Button) findViewById(R.id.loginbutton);

        Button twitter_custom_button = (Button) findViewById(R.id.twitter_custom_button);
        Button wechat = (Button) findViewById(R.id.wechat);
        Button wibo = (Button) findViewById(R.id.weibo);
        Button qq = (Button) findViewById(R.id.qq);
        wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CURRENTLY_NOT_AVAILABLE), Toast.LENGTH_SHORT).show();

            }
        });
        wibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CURRENTLY_NOT_AVAILABLE), Toast.LENGTH_SHORT).show();

            }
        });
        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CURRENTLY_NOT_AVAILABLE), Toast.LENGTH_SHORT).show();


            }
        });

        twitter_custom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CURRENTLY_NOT_AVAILABLE), Toast.LENGTH_SHORT).show();

            }

            public void Twitterlogin(TwitterSession session) {
                String username = session.getUserName();
                String userId = String.valueOf(session.getUserId());
                Log.e("userid", "" + userId);

                Log.e("userme", "" + username);
                Intent intent = new Intent(LoginViewController.this, HomeActivityViewController.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline(LoginViewController.this)) {
                    //mProgress.show();
                    if (v == fb) {
                        LoginManager.getInstance().logInWithReadPermissions(LoginViewController.this, Arrays.asList("public_profile", "user_friends", "email"));
                        LoginManager.getInstance().registerCallback(callbackManager, callback);
                    }
                } else {
                    //mProgress.dismiss();
                    showNetworkAlert(LoginViewController.this);
                }
            }
        });

    }
    public void updateLanguageTexts() {

        logintext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LOGIN_KEY));

        emailphone.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.EMAIL_KEY));

        password .setText( LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PASSWORD_KEY));

        or.setText( LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OR_KEY));

        connectwith.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LOGIN_PAGE_CONNECT_WITH_KEY));

        forgotpassword.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys. FORGOT_PASSWORD_KEY));

        signup.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys. LOGIN_PAGE_DONT_HAVE_ACCOUNT_KEY));
        signup.setPaintFlags(signup.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        //signup.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SIGNUP_KEY));

        login.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LOGIN_KEY));

    }
    ImageView imageView;
    android.os.Handler handler=new android.os.Handler();
    Dialog dialog1;
    public void showAlert(){
        dialog1=new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.activity_animate);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.setCancelable(false);
        dialog1.show();
        dialog1.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        TextView textView=(TextView)dialog1.findViewById(R.id.connecting);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));

        imageView=(ImageView)dialog1.findViewById(R.id.image_rottate) ;
        handler.postDelayed(updateTimerThread, 100);
    }
    public  Runnable updateTimerThread = new Runnable() {
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
    public void hideRefreshDialog(){
        Log.e("hideRefreshDialog", "call");
        handler.removeCallbacks(updateTimerThread);
        dialog1.dismiss();
    }
    public String getCurrentTime() {

        long unixTime;
        unixTime = System.currentTimeMillis() / 1L;
        String attempt_time = String.valueOf(System.currentTimeMillis() / 1L);

        Log.e("attem", "" + attempt_time);
        return attempt_time;

    }
    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SideMenuViewController.MessageEvent event) {
        Log.e("sidemenuMessageevent", "" + event.message);
        String resultData = event.message.trim();

        if (resultData.equals("moveToHome")) {

            moveToHome();
        } else if (resultData.equals("moveToPersonalInfo")) {
            moveToPersonalInfo();
        }

    }


    public void moveToHome() {


        Log.e("methodcall", "call");

        hideRefreshDialog();

        startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
    }

    public void moveToPersonalInfo() {
        hideRefreshDialog();
        Log.e("methodnull", "call");
        startActivity(new Intent(getApplicationContext(), PersonalInformationViewController.class));
    }

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.e("FBStatus", "onSuccess Called");

            System.out.println("onSuccess");

            String accessToken = loginResult.getAccessToken()
                    .getToken();
            Log.i("accessToken", accessToken);

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object,
                                                GraphResponse response) {

                            Log.i("LoginActivity",
                                    response.toString());
                            try {
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String email = object.getString("email");
                                String gender = object.getString("gender");


                                if (email != null) {
                                    sharedPreferencesEditor = sharedPreferences.edit();
                                    sharedPreferencesEditor.putString("name", name);
                                    Log.e("username", "" + name);
                                    sharedPreferencesEditor.putString("gender", gender);
                                    String imageURLString = "http://graph.facebook.com/" + id + "/picture?type=large";
                                    sharedPreferencesEditor.putString("picture", imageURLString);
                                    sharedPreferencesEditor.putString("Type", Constants.RegisterTypes.Facebook.toString());
                                    Log.e("gender", "" + gender);
                                    Log.e("gender", "" + imageURLString);
                                    socialmediaLoginEmail = email;
                                    sharedPreferencesEditor.commit();
                                    LoginManager.getInstance().logOut();
                                    mProgress.dismiss();
                                   showAlert();
                                    socialMediaLogin(email, Constants.RegisterTypes.Facebook.toString());
                                } else {
                                    // Show Invalid alert.
                                    //  finish();
                                    Log.e("emailnull","call");
                                    LoginManager.getInstance().logOut();
                                    mProgress.dismiss();
                                    facebookalert();

                                }
                                //  String birthday = object.getString("birthday");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields",
                    "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();


        }

        @Override
        public void onCancel() {
            mProgress.dismiss();

            Log.e("FBStatus", "OnCancel Called");
        }

        @Override
        public void onError(FacebookException e) {
            Log.e("FBStatus", "OnCancel Called" + e);
            mProgress.dismiss();

        }
    };

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void facebookalert() {

        Log.e("facebookalert", "call");
        facebookalert = new Dialog(this);
        facebookalert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        facebookalert.setCancelable(false);
        facebookalert.setCanceledOnTouchOutside(false);
        facebookalert.setCancelable(true);
        facebookalert.setContentView(R.layout.activity_facebookalert);


        TextView text = (TextView) logindilog.findViewById(R.id.text_info);
        text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CAN_NOT_CONNECT_WITH_FACEBOOK_KEY));

        TextView textView1 = (TextView) logindilog.findViewById(R.id.textView1);
        textView1.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ALERT_FACEBOOK_ACCOUNT_NOT_LINK_WITH_EMAIL_KEY));

        final Button cancel = (Button) facebookalert.findViewById(R.id.btn_cancel);
        cancel.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys. CANCEL_KEY ));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                facebookalert.cancel();


            }


        });
        Button signup = (Button) facebookalert.findViewById(R.id.btn_signup);
      signup.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys. CANCEL_KEY ));

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                facebookalert.cancel();
                Intent i = new Intent(getApplicationContext(), RegisterViewController.class);
                startActivity(i);
            }


        });
        facebookalert.show();


    }


    public static void showNetworkAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Network Alert");
        builder.setMessage("Ple" +
                "" +
                "ase check your network connection and try again");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

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

    @BindView(R.id.loginbutton)
    Button button;

    @OnClick(R.id.loginbutton)

    public void login() {
//        userNameTextField.getText().clear();
//        passwordTextField.getText().clear();
        //  loginData(st_emailandphone, st_password);
        mLogin();


    }

    @OnClick(R.id.forgotpassword)
    public void forgot() {
        String emailString = userNameTextField.getText().toString().trim();

        moveToForgetPasswordPage(emailString);


    }

    @OnClick(R.id.signup)
    public void signup() {
        Intent in = new Intent(LoginViewController.this, RegisterViewController.class);
        startActivity(in);
    }

    @OnClick(R.id.google)
    public void google() {
        mProgress.show();
        if (isConn()) {
            mProgress.show();
            signIn();
        } else {
            //new AlertShowingDialog(LoginViewController.this, "No Connection");
         new AlertShowingDialog(LoginViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_CONNECTION),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
           // new AlertShowingDialog(LoginViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.));
            mProgress.dismiss();

        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("requestcode", "" + requestCode + " " + resultCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = result.getStatus().getStatusCode();
            Log.e("statuscode", "calldd" + statusCode);

            handleSignInResult(result);
        } else {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);


        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("handleSignInResult", "" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();
            socialmediaLoginEmail = acct.getEmail();
            String idString = acct.getId();
            String NameString = acct.getDisplayName();
            socialmediaLoginEmail = acct.getEmail();
            // acct.getEmail();
            Log.e("email", ""+socialmediaLoginEmail);
            Log.e("id", ""+idString);
            Log.e("Name", ""+NameString);

            mProgress.dismiss();

            sharedPreferencesEditor = sharedPreferences.edit();
           /* if(acct.getDisplayName() != null)
            {
                sharedPreferencesEditor.putString("name", acct.getDisplayName());
            }
           else
            {
                sharedPreferencesEditor.putString("Name", "");
            }*/
            sharedPreferencesEditor.putString("name", acct.getDisplayName());
            sharedPreferencesEditor.putString("gender", "Female");
            if(acct.getPhotoUrl()==null){
                //set default image
                Log.e("empptyImag", "call"+acct.getPhotoUrl());
            } else {
                sharedPreferencesEditor.putString("picture", acct.getPhotoUrl().toString());
            }

            sharedPreferencesEditor.putString("Type", Constants.RegisterTypes.Google.toString());

            Log.e("username", "" + acct.getDisplayName());
            Log.e("username1", "" + acct.getEmail());
            sharedPreferencesEditor.commit();


            showAlert();
            socialMediaLogin(socialmediaLoginEmail, Constants.RegisterTypes.Google.toString());

        } else {

            mProgress.dismiss();
            Toast.makeText(getApplicationContext(), "Failed login", Toast.LENGTH_SHORT).show();

        }
    }

    public void mLogin() {
      st_emailandphone = userNameTextField.getText().toString().trim();
         st_password = passwordTextField.getText().toString();

        if (st_emailandphone.length() > 0) {

            if (st_password.length() > 0) {

                if (isConn()) {

                    showAlert();

                    loginData(st_emailandphone, st_password);

                } else {
                    new AlertShowingDialog(LoginViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }
            } else {
                new AlertShowingDialog(LoginViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_PASSWORD_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

            }

        } else {

            new AlertShowingDialog(LoginViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_EMAIL_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

        }

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }
///////////////MobileIdGetting//////////////









    public  String getDeviceId(){



        if(checkPermission()){

            telephonyManager    = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String id = telephonyManager.getDeviceId();
            Log.e("tellid",""+telephonyManager.getDeviceId());


            SplashScreenViewController.sharedPreferencesTOkenEditor.putString("deviceid",telephonyManager.getDeviceId());
            SplashScreenViewController.sharedPreferencesTOkenEditor.commit();
            return id;
        } else {

            requestPermission();
        }


        return null;
    }







    /////////////////////////////////



    /////////////////////////////////////Login aps/////////////////////////////
    private void loginData(final String userName, final String password) {

        SharedPreferences tokenPreferences = getSharedPreferences("tokendeviceids", Context.MODE_PRIVATE);

        String tokenId = tokenPreferences.getString("tokenid",null);
        String deviceId = tokenPreferences.getString("deviceid",null);


        final UserServerObject requestBody = new UserServerObject();
        requestBody.setUserName(userName.trim());
        Log.e("email", "" + userName.trim());

        requestBody.setPassword(password.trim());
        Log.e("password", "" + password.trim());

        requestBody.setDeviceid(deviceId);
        Log.e("tellid",""+deviceId);

        requestBody.setDeviceToken(tokenId);
        Log.e("firebase",""+tokenId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<UserServerObject> callable = api.login(requestBody);
        callable.enqueue(new retrofit2.Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, final retrofit2.Response<UserServerObject> response) {

                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();
                if (statusCode.equals(null)) {
                    hideRefreshDialog();
                } else {
                    Log.e("manualrespons","call"+response.body().getResponse());
                    if (statusCode.equals("3")) {

                        if (logindilog != null) {
                            logindilog.cancel();
                        }
                        User user = new User();
                        user.setUserName(userName);
                        user.setLatitude(String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude()));
                        user.setLongitude(String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude()));
                        user.setVerified(true);
                        user.setPassword(password);
                        user.setRegisterType(Constants.RegisterTypes.Manual.toString());
                        String preferredLanguage  = Constants.AppLanguages.English.toString();
                        preferredLanguage = response.body().getLanguage();
                        user.setPreferedLanguage(preferredLanguage);
                        UserDataController.getInstance().insertUserData(user);
                        LanguageTextController.getInstance().loadLanguageTexts();
                        updateLanguageTexts();

                        HandlerThread handlerThread = new HandlerThread("fetchData");
                        handlerThread.start();
                        Handler handler = new Handler(handlerThread.getLooper());


                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                performFetchLoginData(response.body());
                            }
                        });

                    }
                    else if (statusCode.equals("0"))
                    {
                        hideRefreshDialog();
                        new AlertShowingDialog(LoginViewController.this,message,LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    } else if (statusCode.equals("2")) {
                       hideRefreshDialog();
                        new AlertShowingDialog(LoginViewController.this,message,LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    } else if (statusCode.equals("6")) {
                        hideRefreshDialog();
                        responsealert();
                    } else {
                     hideRefreshDialog();
                        new AlertShowingDialog(LoginViewController.this,message,LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    }
                }
            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {

                hideRefreshDialog();

              failurealert(Constants.RegisterTypes.Manual.toString());

               // new AlertShowingDialog(LoginViewController.this, "Failed");
            }
        });

    }


    public  void  performFetchLoginData(final UserServerObject userObject){

        ExecutorService taskExecutor = Executors.newFixedThreadPool(1);
        try
        {
            Runnable backgroundTask = new Runnable() {
                @Override
                public void run() {
                    System.out.println("Hello World");
                    LoginServerObjectDataController.getInstance().processUserData(userObject);
                }

            };

            taskExecutor.submit(backgroundTask);

            //  taskExecutor.execute(handler.post());
            // taskExecutor.execute(new ProcessDataThread());
            taskExecutor.shutdown();
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            Log.e("taskExecutor", "terminated");
            Log.e("isMovetoPersonalInfo", "call"+LoginServerObjectDataController.getInstance().isMovetoPersonalInfo);
            Log.e("isMovetoHome", "call"+LoginServerObjectDataController.getInstance().isMovetoHome);


            if(LoginServerObjectDataController.getInstance().isMovetoPersonalInfo)
            {
                LoginServerObjectDataController.getInstance().isMovetoPersonalInfo = false;
                LoginServerObjectDataController.getInstance().isMovetoHome = false;
                moveToPersonalInfo();
                return;
            }
            else  if (LoginServerObjectDataController.getInstance().isMovetoHome)
            {
                LoginServerObjectDataController.getInstance().isMovetoPersonalInfo = false;
                LoginServerObjectDataController.getInstance().isMovetoHome = false;
                moveToHome();

            }
        } catch (InterruptedException e)
        {

        }

    }

    // Social Media Login API's

    /////g///////////////////////RegisterAps/////////////////////////////////
    private void socialMediaLogin(final String userId, final String regType) {

        SharedPreferences tokenPreferences = getSharedPreferences("tokendeviceids", Context.MODE_PRIVATE);

        String tokenId = tokenPreferences.getString("tokenid",null);
        String deviceId = tokenPreferences.getString("deviceid",null);

        Log.e("callmethod", "call");
        final UserServerObject requestBody = new UserServerObject();
        requestBody.setUserName(userId.trim());
        Log.e("em", "" + userId);

        requestBody.setLatitude(String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude()));
        requestBody.setLongitude(String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude()));

        final String attemptedTime = getCurrentTime();

        requestBody.setRegisterTime(attemptedTime);
        requestBody.setLanguage(LanguageTextController.getInstance().getCurrentSystemLanguage());
        requestBody.setRegister_type(regType);

        Log.e("reg", "" + regType);


        requestBody.setDeviceid(deviceId);
        Log.e("socialmediatellid",""+deviceId);

        requestBody.setDeviceToken(tokenId);
        Log.e("socialmediafirebase",""+tokenId);



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);


        Call<UserServerObject> callable = api.register(requestBody);

        //final long finalUnixTime = unixTime;
        callable.enqueue(new retrofit2.Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, final retrofit2.Response<UserServerObject> response) {
                // verifyDialog.dismiss();
                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();
                Log.e("codefor3", "call" + statusCode);
                if (statusCode.equals(null)) {
                    hideRefreshDialog();
                }else if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {

                        User user = new User();
                        user.setUserName(userId);
                        user.setLatitude(String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude()));
                        user.setLongitude(String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude()));
                        user.setVerified(true);
                        user.setPassword("");
                        user.setRegisterType(regType);
                        String preferredLanguage = response.body().getLanguage();
                        if (preferredLanguage == null || preferredLanguage.isEmpty()) {
                            preferredLanguage = LanguageTextController.getInstance().getCurrentSystemLanguage();
                        }
                        user.setPreferedLanguage(preferredLanguage);
                        UserDataController.getInstance().insertUserData(user);

                        LanguageTextController.getInstance().loadLanguageTexts();
                        updateLanguageTexts();

                        HandlerThread handlerThread = new HandlerThread("fetchData");
                        handlerThread.start();
                        Handler handler = new Handler(handlerThread.getLooper());

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                performFetchLoginData(response.body());
                            }
                        });
                        logOutSocialMediaAccount(regType);
                    } else if (statusCode.equals("5")) {
                        hideRefreshDialog();
                        new AlertShowingDialog(LoginViewController.this, message, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    } else if (statusCode.equals("4")) {
                        hideRefreshDialog();
                        LoginAlertDilog();
                        logOutSocialMediaAccount(regType);

                    }
                }


            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {

                hideRefreshDialog();
                failurealert(regType);

            }
        });

    }
     public void logOutSocialMediaAccount(String regType)
     {
         if (regType.equals(Constants.RegisterTypes.Facebook.toString())) {
             Log.e("logoutfb", "call");
             LoginManager.getInstance().logOut();
         } else if (regType.equals(Constants.RegisterTypes.Google.toString())) {
             Log.e("logoutgoogle", "call");
             Auth.GoogleSignInApi.signOut(mGoogleApiClient);

         }
     }

    public void LoginAlertDilog() {
        Log.e("emailId", socialmediaLoginEmail);
        Log.e("alert", "call");

        logindilog = new Dialog(this);
        logindilog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        logindilog.setCancelable(false);
        logindilog.setCanceledOnTouchOutside(false);
        logindilog.setCancelable(true);
        logindilog.setContentView(R.layout.activity_alertforlogin);
        logindilog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        TextView text_info = (TextView) logindilog.findViewById(R.id.text_info);
        text_info.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LINK_ACCOUNT_TITLE_LABEL_KEY));

        TextView textView1 = (TextView) logindilog.findViewById(R.id.textView1);
        textView1.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LINK_ACCOUNT_ALERT_KEY));


        TextView emailTextView = (TextView) logindilog.findViewById(R.id.emailText);
        final EditText editPassword = (EditText) logindilog.findViewById(R.id.edit_password);
        emailTextView.setText(socialmediaLoginEmail.toString());
        CheckBox check = (CheckBox) logindilog.findViewById(R.id.chk);

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {


                if (checked) {
                    editPassword.setTransformationMethod(null);
                } else {
                    editPassword.setTransformationMethod(new PasswordTransformationMethod());

                }
                // cursor reset his position so we need set position to the end of text
                editPassword.setSelection(editPassword.getText().length());

            }
        });

        ImageView btnclose = (ImageView) logindilog.findViewById(R.id.close);


        btnclose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                logindilog.cancel();


            }
        });
        final Button forget = (Button) logindilog.findViewById(R.id.btn_forgot);
        forget.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FORGOT_PASSWORD_KEY ));

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToForgetPasswordPage(socialmediaLoginEmail);
                logindilog.cancel();


            }


        });
        Button signin = (Button) logindilog.findViewById(R.id.btn_signin);
       signin.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SIGNUP_KEY));

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editPassword.getText().toString().length() > 0) {
                    if (isConn()){
                       showAlert();
                        loginData(socialmediaLoginEmail, editPassword.getText().toString().trim());

                    }else {
                        // Alert for enter password.
                        new AlertShowingDialog(LoginViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                    }
                } else {
                    // Alert for enter password.
                    new AlertShowingDialog(LoginViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_PASSWORD_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                }


            }


        });
        Log.e("Show", "" + logindilog.isShowing());
        logindilog.show();

    }


    public void responsealert() {

        Log.e("responsealert", "call");
        responsealert = new Dialog(this);
        responsealert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        responsealert.setCancelable(false);
        responsealert.setCanceledOnTouchOutside(false);
        responsealert.setCancelable(true);
        responsealert.setContentView(R.layout.activity_responsealert);
        responsealert.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        TextView text=(TextView)responsealert.findViewById(R.id.text_info);
        text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SET_PASSWORD_ALERT_KEY));

        final Button cancel = (Button) responsealert.findViewById(R.id.btn_cancel);
       cancel.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys. CANCEL_KEY ));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                responsealert.cancel();


            }


        });
        Button forgotpassword = (Button) responsealert.findViewById(R.id.btn_forgotpassword);
        forgotpassword.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FORGOT_PASSWORD_KEY ));

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                moveToForgetPasswordPage(userNameTextField.getText().toString().trim());

                responsealert.cancel();

            }


        });
        responsealert.show();


    }



    public void failurealert(final String registerType) {

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
                    showAlert();

                    if (registerType.equals(Constants.RegisterTypes.Manual.toString())){
                        Log.e("manual",""+registerType.equals(Constants.RegisterTypes.Manual.toString()));
                        showAlert();
                        loginData(st_emailandphone, st_password);

                    }else if (registerType.equals(Constants.RegisterTypes.Google.toString())){
                        Log.e("google",""+registerType.equals(Constants.RegisterTypes.Google.toString()));
                        showAlert();
                        socialMediaLogin(socialmediaLoginEmail, Constants.RegisterTypes.Google.toString());

                    }else if (registerType.equals(Constants.RegisterTypes.Facebook.toString())){
                      showAlert();
                        Log.e("facebook",""+registerType.equals(Constants.RegisterTypes.Facebook.toString()));
                        socialMediaLogin(email, Constants.RegisterTypes.Facebook.toString());

                    }
                }else
                    {
                        failurealert.dismiss();
                        new AlertShowingDialog(LoginViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }


        });


    }




    private void moveToForgetPasswordPage(String emailInfo) {

        Intent intent = new Intent(LoginViewController.this, ForgotpasswordViewController.class);
        intent.putExtra("email", emailInfo);
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {    //when click on phone backbutton
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {  // could be in onPause or onStop
        // Picasso.with(this).cancelRequest(target);
        super.onDestroy();
    }



    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;
        }
    }

    private void requestPermission(){

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

               getDeviceId();

                } else {

                  //  Snackbar.make(findViewById(R.id.coordinatorLayout),"Permission Denied, Please allow to proceed !.",Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }




}

