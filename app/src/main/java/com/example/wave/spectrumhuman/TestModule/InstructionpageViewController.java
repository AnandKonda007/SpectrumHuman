package com.example.wave.spectrumhuman.TestModule;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.R;

import java.util.ArrayList;

import static com.example.wave.spectrumhuman.R.id.toolbar;

/**
 * Created by Rise on 20/09/2017.
 */

public class InstructionpageViewController extends AppCompatActivity{
    ImageView image_animate;
    ArrayList<Integer> imageArray=new ArrayList<>();
    TextView skip;
    Toolbar mToolbar;
    ImageView back,back1;
    ImageView add;
    TextView myTextView,stepone;
    int pStatus;
    public static boolean isCircularBackbtnCilcked =false;
    Handler handler = new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructionpage);
        Log.e("Test", "onCreate");
        imageArray.add(R.drawable.ic_boul_1) ;
        imageArray.add(R.drawable.ic_boul_2);
        imageArray.add(R.drawable.ic_boul_3);
        imageArray.add(R.drawable.ic_boul_4);
        imageArray.add(R.drawable.ic_boul_5);
        imageArray.add(R.drawable.ic_boul_6);
        imageArray.add(R.drawable.ic_boul_7);

        image_animate = (ImageView) findViewById(R.id.animation);
        myTextView = (TextView) findViewById(R.id.urinetext);
        stepone=(TextView)findViewById(R.id.stepone);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            myTextView.setText(Html.fromHtml("<font face=\"verdana\">\n" +
                    "<ol>\n" +
                    "  <li>1.&nbsp; Immerse the strip in urine for 1 to <br>&nbsp;&nbsp;&nbsp; 2 seconds and lay the strip flat on <br>&nbsp;&nbsp;&nbsp; a clean,dry and non-absorbent <br>&nbsp;&nbsp;&nbsp; surface.<br></li>\n" +
                    "  <li><font face=\"verdana\">\n" +
                    "</font><br>2.&nbsp; Put the strip into the strip holder. </li>\n" +
                    "</ol>  \n" +
                    "</font>", Html.FROM_HTML_MODE_COMPACT));

        } else {
           /* myTextView.setText(Html.fromHtml(" \"<font face='verdana'><ol><li>Immerse the strip in urine for 1 to 2  seconds and lay the strip flat on a clean, dry and non-absorbent surface.<br>" +
                    "</li><li>Put the strip into the strip holder.</li></ol></font>\""));
          */

            myTextView.setText(Html.fromHtml("<font face=\"verdana\">\n" +
                    "<ol>\n" +
                    "  <li>1.&nbsp; Immerse the strip in urine for 1 to <br>&nbsp;&nbsp;&nbsp; 2 seconds and lay the strip flat on <br>&nbsp;&nbsp;&nbsp; a clean,dry and non-absorbent <br>&nbsp;&nbsp;&nbsp; surface.<br></li>\n" +
                    "  <li><font face=\"verdana\">\n" +
                    "</font><br>2.&nbsp; Put the strip into the strip holder. </li>\n" +
                    "</ol>  \n" +
                    "</font>"));

        }
        //       myTextView.setText(Html.fromHtml("<font face=\"verdana\">\n" + "<ol>\n" + " 1. <li>Immerse the strip in urine for 1 to 2  seconds and lay the strip flat on a clean, dry and non-absorbent surface.<br></li>\n" + " 2. <li>Put the strip into the strip holder. </li>\n" + "</ol>  \n" + "</font>"));
        skip = (TextView) findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(updateTimerThread);
                CircleProgressbarViewController.isBackbtnClicked=false;
                startActivity(new Intent(getApplicationContext(),CircleProgressbarViewController.class));

            }
        });
        setToolbar();
        //handler.postDelayed(updateTimerThread, 1000);

        updateLanguageTexts();
    }
    @Override
    protected void onResume()
    {
        super.onResume();

        if (isCircularBackbtnCilcked){
            image_animate.setVisibility(View.VISIBLE);
            pStatus=0;
            handler.removeCallbacks(updateTimerThread);
        }else {
            Log.e("Test", "OnResumecall");
            pStatus=7;
            image_animate.setVisibility(View.VISIBLE);
            handler.postDelayed(updateTimerThread, 1000);

        }

    }
    @Override
    protected void onStop()
    {
        super.onStop();
        Log.e("Test", "Home button pressed!");
        pStatus=0;
        handler.removeCallbacks(updateTimerThread);
    }

    private void setToolbar() {


        mToolbar = (Toolbar) findViewById(toolbar);
        setSupportActionBar(mToolbar);
        back = (ImageView) mToolbar.findViewById(R.id.back_arraow);
        back1 = (ImageView) mToolbar.findViewById(R.id.backimage);
        back1.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);

        TextView toolbartext = (TextView) mToolbar.findViewById(R.id.tool_txt);

        add=(ImageView) mToolbar.findViewById(R.id.add);
        add.setVisibility(View.INVISIBLE);
        toolbartext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.STRIP_INSTRUCTION_KEY));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                handler.removeCallbacks(updateTimerThread);

            }
        });

    }
    public void updateLanguageTexts() {
        stepone.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.STEP1_KEY));

        myTextView.setText("1.  "+LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.STRIP_INSTRUCTION_DISCRIPTION_FIRST_POINT_KEY)+"\n"+"\n"+"2.  "+LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.STRIP_INSTRUCTION_DISCRIPTION_SECOND_POINT_KEY));

        skip .setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SKIP_BUTTON_KEY));


    }

    public  Runnable updateTimerThread = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            image_animate.setVisibility(View.VISIBLE);
            pStatus -= 1;
            Log.e("pro",""+pStatus);
            image_animate.setBackgroundResource(imageArray.get(pStatus));
            handler.postDelayed(this, 1000);
            if(pStatus==0){
                Log.e("profor100",""+pStatus);
                image_animate.setVisibility(View.VISIBLE);
                pStatus=0;
                handler.removeCallbacks(updateTimerThread);
                startActivity(new Intent(getApplicationContext(),CircleProgressbarViewController.class));

            }
        }
    };
    @Override
    public void onBackPressed() {
        finish();
        handler.removeCallbacks(updateTimerThread);
        super.onBackPressed();
    }
}
