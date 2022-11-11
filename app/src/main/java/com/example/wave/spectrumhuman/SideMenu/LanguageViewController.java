package com.example.wave.spectrumhuman.SideMenu;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.HomeModule.SideMenuViewController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.LoginModule.LoginViewController;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.LanguageServerObjects;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rise on 09/10/2017.
 */

public class LanguageViewController extends AppCompatActivity  {

    RecyclerView addRecyclerView;
    View view;
    Toolbar toolbar;
    ImageView back, home, add, refresh, imageView;
    int selectedPosition = 5;
    int tempSelectedPosition = 5;
    Language device;
    String selectedLanguageEnglishName, selectedLanguageNativeName, selectedLanguageKey;
    Dialog languagedialog, activityDialog, failurealert;
    Handler handler = new Handler();
    ArrayList<String> languageEnglishNames = new ArrayList();

    TextView tool_txt;
    public  SharedPreferences sharedPreferences;

    public  SharedPreferences.Editor sharedPreferencesEditor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);
        ButterKnife.bind(this);
        setToolbar();

        languageEnglishNames = new ArrayList<String>(LanguageTextController.getInstance().allLanguageDictionary.keySet());

        init();
        sharedPreferences = getApplicationContext().getSharedPreferences("language", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();


        updateLanguageTexts();
    }
    private void init() {

        addRecyclerView = (RecyclerView) findViewById(R.id.recycler_mydevice);
        device = new Language(getApplication());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        addRecyclerView.setLayoutManager(horizontalLayoutManager);
        addRecyclerView.setAdapter(device);

    }


    private void setToolbar() {


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        home = (ImageView) toolbar.findViewById(R.id.home);
        home.setImageResource(R.drawable.ic_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        back = (ImageView) toolbar.findViewById(R.id.backimage);
        back.setVisibility(View.INVISIBLE);

        refresh = (ImageView) toolbar.findViewById(R.id.refresh);
        refresh.setVisibility(View.INVISIBLE);


        add = (ImageView) toolbar.findViewById(R.id.add);
        add.setVisibility(View.INVISIBLE);

        tool_txt = (TextView) toolbar.findViewById(R.id.tool_txt);
        tool_txt.append(getString(R.string.langage));


    }


    public void updateLanguageTexts() {
        tool_txt.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LANGUAGES_TITLE_KEY));
        //   textview.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WOULD_YOU_LIKE_TO_CHANGE_LANGUAGE_ALERT_KEY));

    }

    // Step 1:-
    public class Language extends RecyclerView.Adapter<Language.ViewHolder> {

        // step 3:-
        Context ctx;
        ImageView button;


        public Language(Context ctx) {
            this.ctx = ctx;
        }

        // step 5:-
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.language_customlayout, parent, false);


            ViewHolder myViewHolder = new ViewHolder(view, ctx);
            return myViewHolder;


        }

        //step 6:-
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            ImageView image = (ImageView) holder.itemView.findViewById(R.id.tick);


            String languageKey = languageEnglishNames.get(position);

            HashMap<String, String> objLanguage = LanguageTextController.getInstance().allLanguageDictionary.get(languageKey);


            holder.userName.setText(objLanguage.get(LanguagesKeys.englishLanguageKey));
            holder.userName1.setText(objLanguage.get(LanguagesKeys.originalLanguageKey));


            Log.e("CellLanguage", "" + objLanguage.get(LanguagesKeys.englishLanguageKey));
            Log.e("CellLanguage1", "" + LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.englishLanguageKey));


            if (holder.userName.getText().equals(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.englishLanguageKey))) {
                image.setVisibility(View.VISIBLE);
                Log.e("selectedLanguageEnglishName", "called" + selectedLanguageEnglishName);
                RelativeLayout relative = (RelativeLayout) findViewById(R.id.relative_language);
                //relative.setBackgroundColor(Color.parseColor("#FFFF4F25"));
            } else {
                Log.e("else", "called");
                image.setVisibility(View.INVISIBLE);

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {

                    String languageKey = languageEnglishNames.get(position);

                    HashMap<String, String> objLanguage = LanguageTextController.getInstance().allLanguageDictionary.get(languageKey);

                    String objSelectedLanguageEnglishName = objLanguage.get(LanguagesKeys.englishLanguageKey);


                    if (!objSelectedLanguageEnglishName.equals(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.englishLanguageKey))) {
                        tempSelectedPosition = position;
                        selectedLanguageKey = languageKey;
                        selectedLanguageEnglishName = objLanguage.get(LanguagesKeys.englishLanguageKey);
                        selectedLanguageNativeName = objLanguage.get(LanguagesKeys.originalLanguageKey);
                        showLanguageDialog();

                    }
                }
            });

            if (position == 4 || position == 5) {
                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)
                        holder.layout.getLayoutParams();
                params1.height = 110;
                holder.layout.setLayoutParams(params1);
                Log.e("layout", "call");
            }
        }


        // step 4:-
        @Override
        public int getItemCount() {


            return LanguageTextController.getInstance().allLanguageDictionary.size();
        }


        // Step 2:-
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            TextView userName, userName1;
            ArrayList<String> arrayList = new ArrayList<String>();
            Context ctx;
            RelativeLayout layout;

            public ViewHolder(View itemView, Context ctx) {
                super(itemView);
                this.ctx = ctx;
                userName = (TextView) itemView.findViewById(R.id.devicename);
                userName1 = (TextView) itemView.findViewById(R.id.device);
                layout = (RelativeLayout) itemView.findViewById(R.id.relative_language);

                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {


            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showLanguageDialog() {
        languagedialog = new Dialog(LanguageViewController.this);
        languagedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        languagedialog.setContentView(R.layout.language_alert);
        languagedialog.setCanceledOnTouchOutside(false);
        languagedialog.setCancelable(false);
        languagedialog.create();
        languagedialog.show();
        languagedialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        Button yes = (Button) languagedialog.findViewById(R.id.btn_ok);
        Button no = (Button) languagedialog.findViewById(R.id.btn_cancle);
        TextView text = (TextView) languagedialog.findViewById(R.id.text_name);
        TextView text1 = (TextView) languagedialog.findViewById(R.id.text_name1);
        TextView text2 = (TextView) languagedialog.findViewById(R.id.text_reminder);

        text.setText("" + selectedLanguageEnglishName);
        text1.setText("" + selectedLanguageNativeName);
        yes.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.YES_KEY));
        no.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_KEY));
        text2.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WOULD_YOU_LIKE_TO_CHANGE_LANGUAGE_ALERT_KEY));

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempSelectedPosition = selectedPosition;
                languagedialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languagedialog.dismiss();
                if (isConn()) {
                    showRefreshDialogue();
                    updatelanguages(UserDataController.getInstance().currentUser.getUserName(), selectedLanguageKey);
                } else {

                    Log.e("noconnection", "call");
                    //showRefreshDialogue();
                    loadLanguagesInOffLine();
                }

            }
        });

    }

    public void hideRefreshDialogue() {
        activityDialog.dismiss();
        handler.removeCallbacks(updateTimerThread);
    }

    public void loadLanguagesInOffLine() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                sharedPreferencesEditor.putString("offlineLanguage",selectedLanguageKey);
                sharedPreferencesEditor.commit();

                UserDataController.getInstance().currentUser.setPreferedLanguage(selectedLanguageKey);
                Log.e("currentLanguageDictionary", "" + UserDataController.getInstance().currentUser.getPreferedLanguage());

                LanguageTextController.getInstance().currentLanguageDictionary = LanguageTextController.getInstance().allLanguageDictionary.get(selectedLanguageKey);
                Log.e("currentLanguageDictionary", "" + LanguageTextController.getInstance().currentLanguageDictionary);
                UserDataController.getInstance().updateUserData(UserDataController.getInstance().currentUser);

                device.notifyDataSetChanged();
                updateLanguageTexts();

            }
        }, 2000 * 1);


    }

    public void updatelanguages(String username, String language) {

        LanguageServerObjects requestBody = new LanguageServerObjects();
        requestBody.setLanguage(language);
        requestBody.setUsername(username);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Log.e("ServerApisInterface", "call");


        Call<LanguageServerObjects> callable = api.updatePreferLanguage(requestBody);

        callable.enqueue(new Callback<LanguageServerObjects>() {
            @Override
            public void onResponse(Call<LanguageServerObjects> call, Response<LanguageServerObjects> response) {

                hideRefreshDialogue();
                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();
                Log.e("codefor3", "call" + statusCode);
                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {
                        UserDataController.getInstance().currentUser.setPreferedLanguage(selectedLanguageKey);
                        UserDataController.getInstance().updateUserData(UserDataController.getInstance().currentUser);
                        LanguageTextController.getInstance().currentLanguageDictionary = LanguageTextController.getInstance().allLanguageDictionary.get(selectedLanguageKey);
                        Log.e("currentLanguageDictionary", "" + LanguageTextController.getInstance().currentLanguageDictionary);
                        languagedialog.dismiss();
                        device.notifyDataSetChanged();
                        EventBus.getDefault().post(new SideMenuViewController.MessageEvent("refreshLanguages"));
                        updateLanguageTexts();

                    } else if (statusCode.equals("0")) {


                    }

                }


            }

            @Override
            public void onFailure(Call<LanguageServerObjects> call, Throwable t) {

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

        TextView text = (TextView) failurealert.findViewById(R.id.text_error);
        text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ERROR_KEY));

        TextView text1 = (TextView) failurealert.findViewById(R.id.requestfail);
        // text1.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ERROR_KEY));

        Button cancel = (Button) failurealert.findViewById(R.id.btn_failurecancel);
        cancel.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("cancel", "canel");

                failurealert.dismiss();


            }


        });
        Button retry = (Button) failurealert.findViewById(R.id.btn_failureretry);
        // retry.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.Ret ));

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("retry", "retry");
                if (isConn()) {

                    failurealert.dismiss();
                    showRefreshDialogue();
                    updatelanguages(UserDataController.getInstance().currentUser.getUserName(), selectedLanguageKey);


                } else {
                    failurealert.dismiss();
                    new AlertShowingDialog(LanguageViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }


        });


    }


    //////////////////////////////Dilog/////////////////////////
    public void showRefreshDialogue() {
        activityDialog = new Dialog(this);
        activityDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activityDialog.setContentView(R.layout.activity_animate);
        activityDialog.setCanceledOnTouchOutside(true);
        activityDialog.setCancelable(false);
        activityDialog.show();
        TextView textView = (TextView) activityDialog.findViewById(R.id.connecting);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));

        activityDialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        imageView = (ImageView) activityDialog.findViewById(R.id.image_rottate);
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

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }


    public void onBackPressed() {

        // stop = true;
        //startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));

        finish();
        super.onBackPressed();
    }
}

