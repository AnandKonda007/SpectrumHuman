package com.example.wave.spectrumhuman.LoginModule;

/**
 * Created by WAVE on 5/29/2017.
 */

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eftimoff.viewpagertransformers.CubeInTransformer;
import com.eftimoff.viewpagertransformers.CubeOutTransformer;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.eftimoff.viewpagertransformers.ZoomInTransformer;
import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;
import com.example.wave.spectrumhuman.DataBase.UrineResultsDataController;
import com.example.wave.spectrumhuman.HomeModule.HomeActivityViewController;
import com.example.wave.spectrumhuman.HomeModule.SideMenuViewController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;
import com.google.gson.Gson;
import com.viewpagerindicator.CirclePageIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class UrineAdvertizeViewController extends AppCompatActivity{
    ViewPager mPager;
   // int currentPage = 0;
    Handler activityHandler = new Handler();
    Dialog activityDialog;
    ImageView imageView;
    RelativeLayout splashScreenLayout, viewPagerLayout;
    SharedPreferences sharedPreferences;
    SlidingImage_Adapter slideAdapter;
    TextView textView;

    SharedPreferences.Editor sharedPreferencesEditor;
    Button start;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indicator);
        ButterKnife.bind(this);
        init();

        sharedPreferences = getApplicationContext().getSharedPreferences("languageInformation", Context.MODE_PRIVATE);


        if (isConn()) {
            showRefreshDialogue();
          //  LanguageTextController.getInstance().loadEnglishText();
            LanguageTextController.getInstance().getLanguages();
            updateLanguageTexts();

        } else {
            loadLanguageTexts();
        }


    }
    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
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

    @OnClick(R.id.btn_start)
    public void movetoLogin() {
        SharedPreferences.Editor editor = getSharedPreferences("AdvertiseInfo", MODE_PRIVATE).edit();
        editor.putString("isStored", "true");
        editor.apply();

        startActivity(new Intent(getApplicationContext(), LoginViewController.class));

    }

    private void init() {

        start = (Button) findViewById(R.id.btn_start);

        viewPagerLayout = (RelativeLayout) findViewById(R.id.viewPagerId);

        mPager = (ViewPager) findViewById(R.id.pager);

        slideAdapter = (new SlidingImage_Adapter(this));
        mPager.setAdapter(slideAdapter);

        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
              //  currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

        viewPagerLayout.setVisibility(View.INVISIBLE);
        splashScreenLayout = (RelativeLayout) findViewById(R.id.splashscreenlayout);
        splashScreenLayout.setVisibility(View.VISIBLE);

    }

    public class SlidingImage_Adapter extends PagerAdapter {
        private Context context;

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        SlidingImage_Adapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return CustomPagerEnum.values().length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];

            LayoutInflater inflater = LayoutInflater.from(context);

            ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayoutResId(), container, false);

            Log.e("resourcePOS",""+position);
            Log.e("resourceID",""+customPagerEnum.getTitleResId());
            Log.e("resourceIDUrine",""+CustomPagerEnum.URINE.getTitleResId());


            TextView testText = layout.findViewById(R.id.testNameID);

            TextView testDecriptionText = layout.findViewById(R.id.testDescriptionID);

            if (customPagerEnum.getTitleResId() == CustomPagerEnum.URINE.getTitleResId()) {
                testText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_URINE_TEST_KEY));
                testDecriptionText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_URINE_TEST_DISCRIPTION_KEY));
            }
            else  if (customPagerEnum.getTitleResId() == CustomPagerEnum.BLOOD.getTitleResId()) {
                testText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_BLOOD_TEST_KEY));
                testDecriptionText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_BLOOD_TEST_DISCRIPTION_KEY));
            }
            else  if (customPagerEnum.getTitleResId() == CustomPagerEnum.TEAR.getTitleResId()) {
                testText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_TEAR_TEST_KEY));
                testDecriptionText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_TEAR_TEST_DISCRIPTION_KEY));
            }
            else  if (customPagerEnum.getTitleResId() == CustomPagerEnum.SALIVA.getTitleResId()) {
                testText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_SALIVA_TEST_KEY));
                testDecriptionText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_SALIVA_TEST_DISCRIPTION_KEY));
            }
            else  if (customPagerEnum.getTitleResId() == CustomPagerEnum.PREGNANCY.getTitleResId()) {
                testText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_PREGENCY_TEST_KEY));
                testDecriptionText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_PREGENCY_TEST_DISCRIPTION_KEY));
            }

            else  if (customPagerEnum.getTitleResId() == CustomPagerEnum.SPERM.getTitleResId()) {
                testText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_SPERM_TEST_KEY));
                testDecriptionText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_SPERM_TEST_DISCRIPTION_KEY));
            }
            else  if (customPagerEnum.getTitleResId() == CustomPagerEnum.WATER.getTitleResId()) {
                testText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_WATER_TEST_KEY));
                testDecriptionText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_WATER_TEST_DISCRIPTION_KEY));
            }
            container.addView(layout);
            return layout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }

    public enum CustomPagerEnum {

        URINE(R.layout.activity_urinetest,0),
        BLOOD(R.layout.activity_bloodtest,1),
        TEAR(R.layout.activity_teartest,2),
        SALIVA(R.layout.activity_salivatest,3),
        PREGNANCY(R.layout.activity_pregnancytest,4),
        SPERM(R.layout.activity_spermtest,5),
        WATER(R.layout.activity_watertest,6);

        private int mTitleResId;
        private int mLayoutResId;


        CustomPagerEnum(int layoutResId, int id) {

            mLayoutResId = layoutResId;
            mTitleResId = id;
        }

        public int getTitleResId() {
            return mTitleResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
        }

    }

    //////////////////////////////Dilog/////////////////////////
    public void showRefreshDialogue() {
        activityDialog = new Dialog(this);
        activityDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activityDialog.setContentView(R.layout.activity_animate);
        activityDialog.setCanceledOnTouchOutside(true);
        activityDialog.setCancelable(true);
        activityDialog.show();
         textView=(TextView)activityDialog.findViewById(R.id.connecting);
        textView.setText("Getting Info...");
        activityDialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        imageView = (ImageView) activityDialog.findViewById(R.id.image_rottate);
        activityHandler.postDelayed(updateTimerThread, 100);
    }

    public Runnable updateTimerThread = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            imageView.setVisibility(View.VISIBLE);
            activityHandler.postDelayed(this, 600);
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

    public void loadLanguageTexts() {

        viewPagerLayout.setVisibility(View.VISIBLE);
        splashScreenLayout.setVisibility(View.GONE);

        if (LanguageTextController.getInstance().allLanguageDictionary.isEmpty()) {
            LanguageTextController.getInstance().loadEnglishText();
        } else {
            String currentLanguagekey = LanguageTextController.getInstance().getCurrentSystemLanguage();

            if (!LanguageTextController.getInstance().allLanguageDictionary.get(currentLanguagekey).isEmpty()) {
                LanguageTextController.getInstance().currentLanguageDictionary = LanguageTextController.getInstance().allLanguageDictionary.get(currentLanguagekey);
                updateLanguageTexts();
            } else {
                LanguageTextController.getInstance().loadEnglishText();
            }

        }

    }


    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SideMenuViewController.MessageEvent event) {
        Log.e("sidemenuMessageevent", "" + event.message);
        String resultData = event.message.trim();

        if (resultData.equals("LanguageAPIResponseSuccess")) {

            viewPagerLayout.setVisibility(View.VISIBLE);
            splashScreenLayout.setVisibility(View.GONE);
            activityHandler.removeCallbacks(updateTimerThread);
            activityDialog.dismiss();
            updateLanguageTexts();
        } else if (resultData.equals("LanguageAPIResponseFailure")) {
            loadLanguageTexts();
            viewPagerLayout.setVisibility(View.VISIBLE);
            splashScreenLayout.setVisibility(View.GONE);
            activityHandler.removeCallbacks(updateTimerThread);
            updateLanguageTexts();
            activityDialog.dismiss();

        }

    }

    public void updateLanguageTexts() {

        Log.e("newText",""+LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_TEST_GET_STARTED_KEY));
        start.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_TEST_GET_STARTED_KEY));
        //textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));
        slideAdapter = (new SlidingImage_Adapter(this));
        mPager.setAdapter(slideAdapter);
        slideAdapter.notifyDataSetChanged();

    }
    @Override
    public void onBackPressed()
    {    //when click on phone backbutton
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
