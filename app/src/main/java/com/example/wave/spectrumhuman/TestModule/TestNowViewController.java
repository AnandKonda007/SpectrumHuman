package com.example.wave.spectrumhuman.TestModule;
/**
 * Created by WAVE on 5/29/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.LoginModule.ForgotpasswordViewController;
import com.example.wave.spectrumhuman.LoginModule.NewpasswordViewController;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class TestNowViewController extends AppCompatActivity{
    // Declare Variables
    ViewPager viewPager;
    PagerAdapter adapter;
    int[] testimg;
    ArrayList<String> testname = new ArrayList<>();
    Toolbar toolbar;
    ImageView back;
    TextView toolText, pleaseselecttest;
    TextView txttestname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testnowmain);
        ButterKnife.bind(this);
        setToolbar();
        setviewPagresItems();
        setViewPager();
        pleaseselecttest = (TextView) findViewById(R.id.pleaseselecttest);
        updateLanguageTexts();
    }
    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolText = (TextView) toolbar.findViewById(R.id.toolbar_text);
        toolText.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.TEST_NOW_KEY));

        //
        back = (ImageView) findViewById(R.id.toolbar_icon);//Spectrum
        back.setBackgroundResource(R.drawable.ic_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("backing", "call");
                finish();
            }
        });
    }

    public void setviewPagresItems() {

        testimg = new int[]{R.drawable.ic_urinetest, R.drawable.ic_bloodtest,
                R.drawable.ic_teartest, R.drawable.ic_salivatest,
                R.drawable.ic_pregnancytest, R.drawable.ic_spermtest, R.drawable.ic_watertest};

        testname.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_URINE_TEST_KEY));

        testname.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_BLOOD_TEST_KEY));

        testname.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_TEAR_TEST_KEY));

        testname.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_SALIVA_TEST_KEY));

        testname.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_PREGENCY_TEST_KEY));

        testname.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_SPERM_TEST_KEY));

        testname.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADVERTISE_WATER_TEST_KEY));


    }

    public void setViewPager() {
        // Locate the ViewPager in viewpager_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager);
        // Pass results to ViewPagerAdapter Class
        adapter = new ViewPagerAdapter(TestNowViewController.this, testimg, testname);
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);
        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(10);
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_FRACTION, 140, r.getDisplayMetrics());
        viewPager.setPageMargin((int) (-0.6 * px));
        //
        viewPager.setOffscreenPageLimit(adapter.getCount());
        //
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                if (position < -1) {
                    page.setAlpha(0);
                } else if (position <= 1) {
                    float scaleFactor = Math.max(0.7f, 1 - Math.abs(position - 0.14285715f));
                    page.setScaleX(scaleFactor);
                    page.setScaleY(scaleFactor);
                    page.setAlpha(scaleFactor);
                } else {
                    page.setAlpha(0);
                }
            }
        });
    }

    public class ViewPagerAdapter extends PagerAdapter {
        // Declare Variables
        Context context;
        ArrayList<String> testname;
        ArrayList<Integer> testbg;
        int[] testimg;
        LayoutInflater inflater;

        public ViewPagerAdapter(Context context, int[] testimg, ArrayList<String> testname) {
            this.context = context;
            this.testimg = testimg;
            this.testname = testname;
        }

        /*@Override
        public float getPageWidth(int position) {
            if (position == 0 || position == 2) {
                return 0.8f;
            }
            return 1f;
        }*/
        @Override
        public float getPageWidth(int position) {
            return 0.9f;
        }

        /*public float getPageHeight(int position) {
            return 1.9f;
        }*/
        @Override
        public int getCount() {
            return testimg.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            // Declare Variables
            TextView txttestname;
            ImageView imgcenter;
            RelativeLayout txtbg = null;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.activity_testitems, container, false);
            imgcenter = (ImageView) itemView.findViewById(R.id.img_cenrer);
            imgcenter.setImageResource(testimg[position]);
            txtbg = (RelativeLayout) itemView.findViewById(R.id.img_bg);
            txttestname = (TextView) itemView.findViewById(R.id.txt_testname);

            txttestname.setText(testname.get(position));
            switch (position) {

                case 0:
                    txtbg.setBackgroundResource(R.drawable.layout_bg);
                    GradientDrawable gd = (GradientDrawable) txtbg.getBackground().getCurrent();
                    gd.setColor(Color.parseColor("#03A89E"));
                    //gd.setStroke(0, Color.parseColor("#03A89E"), 0, 0);
                    //(Color.parseColor("#03A89E"));
                    break;
                case 1:
                    txtbg.setBackgroundResource(R.drawable.layout_bg);
                    GradientDrawable gd1 = (GradientDrawable) txtbg.getBackground().getCurrent();
                    gd1.setColor(Color.parseColor("#EEB4B4"));
                    // txtbg.setBackgroundColor(Color.parseColor("#EEB4B4"));
                    break;
                case 2:
                    txtbg.setBackgroundResource(R.drawable.layout_bg);
                    GradientDrawable gd2 = (GradientDrawable) txtbg.getBackground().getCurrent();
                    gd2.setColor(Color.parseColor("#FFCC33"));
                    //txtbg.setBackgroundColor(Color.parseColor("#FFC125"));
                    break;
                case 3:
                    txtbg.setBackgroundResource(R.drawable.layout_bg);
                    GradientDrawable gd3 = (GradientDrawable) txtbg.getBackground().getCurrent();
                    gd3.setColor(Color.parseColor("#8B864E"));
                    // txtbg.setBackgroundColor(Color.parseColor("#8B864E"));
                    break;
                case 4:
                    txtbg.setBackgroundResource(R.drawable.layout_bg);
                    GradientDrawable gd4 = (GradientDrawable) txtbg.getBackground().getCurrent();
                    gd4.setColor(Color.parseColor("#FF4081"));
                    // txtbg.setBackgroundColor(Color.parseColor("#FF4081"));
                    break;
                case 5:
                    txtbg.setBackgroundResource(R.drawable.layout_bg);
                    GradientDrawable gd5 = (GradientDrawable) txtbg.getBackground().getCurrent();
                    gd5.setColor(Color.parseColor("#00CC99"));
                    //txtbg.setBackgroundColor(Color.parseColor("#00CC99"));
                    break;
                case 6:
                    txtbg.setBackgroundResource(R.drawable.layout_bg);
                    GradientDrawable gd6 = (GradientDrawable) txtbg.getBackground().getCurrent();
                    gd6.setColor(Color.parseColor("#BA55D3"));
                    // txtbg.setBackgroundColor(Color.parseColor("#BA55D3"));
                    break;
                default:
                    break;
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int v = position;
                    Log.e("callpos", "" + v);
                    String testName = testname.get(position);
                    if (position == 0) {
                        Intent intent = new Intent(TestNowViewController.this, UrineTestViewController.class);
                        intent.putExtra("isFromTestNow", true);
                        startActivity(intent);

                    } else {
                        Toast toast = Toast.makeText(context, testName + " " + LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.URINE_TEST_IS_NOT_AVAILABLE_KEY), Toast.LENGTH_SHORT);
                        View view1 = toast.getView();
                        view1.setMinimumWidth(800);
                        view1.setBackgroundResource(R.drawable.layout_toastcornerbg);
                        toast.show();
                    }
                }
            });
            ((ViewPager) container).addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // Remove viewpager_item.xml from ViewPager
            ((ViewPager) container).removeView((RelativeLayout) object);

        }
    }

    public void updateLanguageTexts() {

        pleaseselecttest.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_SELECT_TEST));

    }
}
