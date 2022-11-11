package com.example.wave.spectrumhuman.Graphs;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wave.spectrumhuman.DataBase.UrineResultsDataController;
import com.example.wave.spectrumhuman.HomeModule.HomeActivityViewController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
public class TabsGraphActivity extends AppCompatActivity  {
    ViewPagerAdapter adapter;
    TabLayout tabLayout;
    String key = null;
    ImageView img_share,back,back1;
    RelativeLayout sharepage;
    private static final int REQUEST_WRITE_PERMISSION = 56;
    public  static int selectedTestTypeRecordIndex=0;
    //
    public static  boolean isFromMonth =false;
    public static  boolean isFromYear =false;
    public static  boolean isFromWeek =false;
    //
    public static  boolean isDayToggleChecked =false;
    public static  boolean isMonthToggleChecked =false;
    public static  boolean isWeekToggleChecked =false;
    public static  boolean isYearToggleChecked =false;
    ///
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maingraph);
        //
        UrineResultsDataController.getInstance().fetchAllUrineResults();
        setTollBar();
        requestPermissions();
        try {
            Bundle bundle = getIntent().getExtras();
            key = bundle.getString("key");

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (savedInstanceState == null) {
            if (key == null) {
                // Select this one
                Fragment fragment = new DaychartsFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
            } else if (key.equals("month")) {

                Fragment fragment = new MonthchartsFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
            } else if (key.equals("year")) {
                Fragment fragment = new YearchartsFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
            }else if (key.equals("week")) {
                Fragment fragment = new WeekchartsFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
            }
        }
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DaychartsFragment());
        adapter.addFragment(new WeekchartsFragment());
        adapter.addFragment(new MonthchartsFragment());
        adapter.addFragment(new YearchartsFragment());
        tabLayout.setTabsFromPagerAdapter(adapter);
        tabLayout.getTabAt(0).select();

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            if (i == 0) {
                TextView imageView = new TextView(getApplicationContext());
                tabLayout.getTabAt(i).setCustomView(imageView);
                imageView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DAY_KEY));
                imageView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                imageView.setTextColor(Color.parseColor("#000000"));
                imageView.setTextSize(15);


            } else if (i == 1) {
                TextView imageView = new TextView(getApplicationContext());
                tabLayout.getTabAt(i).setCustomView(imageView);
                imageView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WEEK_KEY));
                imageView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                imageView.setTextColor(Color.parseColor("#000000"));
                imageView.setTextSize(15);
            } else if (i == 2) {
                TextView imageView = new TextView(getApplicationContext());
                tabLayout.getTabAt(i).setCustomView(imageView);
                imageView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.MONTH_KEY));
                imageView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                imageView.setTextColor(Color.parseColor("#000000"));
                imageView.setTextSize(15);
            } else if (i == 3) {
                TextView imageView = new TextView(getApplicationContext());
                tabLayout.getTabAt(i).setCustomView(imageView);
                imageView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.YEAR_KEY));
                imageView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                imageView.setTextColor(Color.parseColor("#000000"));
                imageView.setTextSize(15);
            }

        }
        // Select this one
        DaychartsFragment fragment = (DaychartsFragment) adapter.getItem(0);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, "day");
        fragmentTransaction.commitAllowingStateLoss();
        tabLayout.getTabAt(0).select();

        if (isFromWeek){
            // Select this one
            WeekchartsFragment fragment1 = (WeekchartsFragment) adapter.getItem(1);
            FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.frame, fragment1, "week");
            fragmentTransaction1.commitAllowingStateLoss();
            tabLayout.getTabAt(1).select();

        }
        if (isFromMonth){
            // Select this one
            MonthchartsFragment fragment1 = (MonthchartsFragment) adapter.getItem(2);
            FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.frame, fragment1, "month");
            fragmentTransaction1.commitAllowingStateLoss();
            tabLayout.getTabAt(2).select();

        }
        if (isFromYear){
            // Select this one
            YearchartsFragment fragment1 = (YearchartsFragment) adapter.getItem(3);
            FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.frame, fragment1, "year");
            fragmentTransaction1.commitAllowingStateLoss();
            tabLayout.getTabAt(3).select();

        }
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("Position", String.valueOf(tab.getPosition()));

                if (tab.getPosition() == 0) {
                    Log.e("PositionInside", String.valueOf(tab.getPosition()));
                    DaychartsFragment fragment = (DaychartsFragment) adapter.getItem(0);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment, "day");
                    fragmentTransaction.commitAllowingStateLoss();
                    isFromMonth =false;
                    isFromYear=false;
                    isFromWeek=false;
                } else if (tab.getPosition() == 1)
                {
                    Log.e("PositionInside", String.valueOf(tab.getPosition()));
                    WeekchartsFragment fragment = (WeekchartsFragment) adapter.getItem(1);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment, "week");
                    fragmentTransaction.commitAllowingStateLoss();
                    isFromMonth =false;
                    isFromYear=false;

                }else if (tab.getPosition() == 2) {
                    Log.e("PositionInside", String.valueOf(tab.getPosition()));
                    MonthchartsFragment fragment = (MonthchartsFragment) adapter.getItem(2);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment, "month");
                    fragmentTransaction.commitAllowingStateLoss();
                    isFromYear=false;
                    isFromWeek=false;

                } else if (tab.getPosition() == 3) {
                    Log.e("PositionInside", String.valueOf(tab.getPosition()));
                    YearchartsFragment fragment = (YearchartsFragment) adapter.getItem(3);
                    FragmentTransaction fragmentTransaction =getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment, "year");
                    fragmentTransaction.commitAllowingStateLoss();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    isFromMonth =false;
                    isFromWeek=false;
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        //private final List<String> mFragmentTitleList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                Log.e("fragment", "position 0");
            }
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);

        }

    }
    public void setTollBar()
    {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharepage = (RelativeLayout) findViewById(R.id.rl_home);

        back = (ImageView) findViewById(R.id.back_arraow);
        back1 = (ImageView)findViewById(R.id.backimage);
        back1.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        TextView textView=(TextView)findViewById(R.id.tool_txt);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PAST_RESULTS_KEY));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //for day
                HomeActivityViewController.calender = Calendar.getInstance();
                HomeActivityViewController.dateForLandScap=HomeActivityViewController.calender.getTime();
                //
                //for weeek
                HomeActivityViewController.WeekStartTime = Calendar.getInstance();
                HomeActivityViewController.WeekEndTime = Calendar.getInstance();

                //for year
                HomeActivityViewController.calander = Calendar.getInstance();
                //fro month
                HomeActivityViewController.monthCalender = Calendar.getInstance();
            }
        });
        img_share=(ImageView)findViewById(R.id.img_share);
        img_share.setBackgroundResource(R.drawable.ic_share);
        img_share.setOnClickListener(mShareListener);
    }
    View.OnClickListener mShareListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            img_share.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            View viewScreen = sharepage.getRootView();
            viewScreen.buildDrawingCache();
            viewScreen.setDrawingCacheEnabled(true);
            viewScreen.destroyDrawingCache();
            Bitmap screenshot1 = Bitmap.createBitmap(viewScreen.getWidth(), viewScreen.getHeight(), Bitmap.Config.RGB_565);
            viewScreen.draw(new Canvas(screenshot1));
            File mfile2 = savebitmap2(screenshot1);
            final Uri screenshotUri = Uri.fromFile(mfile2);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Spectrum Human");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "" + getResources().getString(R.string.sms_body));
            shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_satus)));
             ///
            img_share.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
        }
    };
    /**
     * Called when take the screen shot
     */

    private File savebitmap2(Bitmap bmp) {
        String temp = "UrineResultHistory";

        OutputStream outStream = null;
        String path = Environment.getExternalStorageDirectory()
                .toString();
        new File(path + "/SplashItTemp2").mkdirs();
        File file = new File(path + "/SplashItTemp2", temp + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(path + "/SplashItTemp2", temp + ".png");
        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }
    //

    private void requestPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        } else {

        }

    }
    @Override
    public void onBackPressed()
    {
        //when click on phone backbutton
        //for day
        HomeActivityViewController.calender = Calendar.getInstance();
        HomeActivityViewController.dateForLandScap=HomeActivityViewController.calender.getTime();
        //
        //for weeek
        HomeActivityViewController.WeekStartTime = Calendar.getInstance();
        HomeActivityViewController.WeekEndTime = Calendar.getInstance();

        //for year
        HomeActivityViewController.calander = Calendar.getInstance();
        //fro month
        HomeActivityViewController.monthCalender = Calendar.getInstance();

    }
}
