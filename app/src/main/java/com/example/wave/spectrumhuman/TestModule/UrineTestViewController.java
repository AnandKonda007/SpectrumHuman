package com.example.wave.spectrumhuman.TestModule;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.SideMenu.MyDeviceViewController;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Rise on 25/09/2017.
 */

public class UrineTestViewController extends AppCompatActivity{

    private ImageView image1;
    private int[] imageArray;
    private int currentIndex;
    private int startIndex;
    private int endIndex;
    Button butt;
    ImageView iv;
    Animator animator;
    boolean state = false;
    int count = 0;
    Toolbar mToolbar;
    ImageView back,back1;
    public boolean isFromtest = false;
    public boolean stop = false;
    ImageView add;
    TextView start;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testnow);
        ButterKnife.bind(this);
        init();
        nextImage();
        updateLanguageTexts();


    }
    private void init() {


        imageArray = new int[4];
        imageArray[0] = R.drawable.circle_2;
        imageArray[1] = R.drawable.circle_3;
        imageArray[2] = R.drawable.circle_4;
        imageArray[3] = R.drawable.circle_1;
        // imageArray[4] = R.drawable.circle_;

        startIndex = 0;
        // endIndex = 15;
        endIndex = 3;
        iv = (ImageView) findViewById(R.id.animation);
        start=(TextView) findViewById(R.id.start);


        setToolbar();
    }
    public void updateLanguageTexts() {
        start.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.START_BUTTON_KEY));

    }
      @OnClick (R.id.animation)
         public  void animation(){

          Intent intent=new Intent(getApplicationContext(),MyDeviceViewController.class);
          intent.putExtra("isFromTestNow", true);
          startActivity(intent);

       }
    public void setToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        back = (ImageView) mToolbar.findViewById(R.id.back_arraow);
        back1 = (ImageView) mToolbar.findViewById(R.id.backimage);
        back1.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        TextView toolbartext = (TextView) mToolbar.findViewById(R.id.tool_txt);
        toolbartext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.URINE_TEST_KEY));

        add=(ImageView) mToolbar.findViewById(R.id.add);
        add.setVisibility(View.INVISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop = true;
                finish();
            }
        });
    }


    public void nextImage() {
        try {

            iv.setImageResource(imageArray[currentIndex]
            );
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        final Animation rotateimage = AnimationUtils.loadAnimation(this, R.anim.animation);
        iv.startAnimation(rotateimage);
        rotateimage.setRepeatCount(Animation.INFINITE);

        currentIndex++;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                iv.clearAnimation();


                if (currentIndex > endIndex) {
                    currentIndex = 0;
                    nextImage();

                    if (isFromtest == false) {
                        Log.e("next", "call");
                        if (stop == false) {
                            // finish();

                            //  Intent intent = new Intent(getApplicationContext(), FragmentMainActivity.class);
                            // startActivity(intent);
                        }
                    }

                } else {
                    nextImage();


                }


            }
        }, 1000);


    }

    @Override
    public void onBackPressed() {
        stop = true;
        // startActivity(new Intent(getApplicationContext(), CircularProressActivity.class));

        finish();
        super.onBackPressed();
    }

}