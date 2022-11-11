package com.example.wave.spectrumhuman.TestModule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.R;

/**
 * Created by Rise on 21/09/2017.
 */

public class CircleProgressbarViewController extends AppCompatActivity{
    ProgressBar progressBar;
    Handler handler = new Handler();
    ImageView back,back1;
    int pStatus = 120;
    Toolbar mToolbar;
    TextView skip;
    ImageView add;
    TextView progresstext, waiting,step2,allreadywaited;
    public static boolean isBackbtnClicked=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circleprogressbar);

        skip = (TextView) findViewById(R.id.circularskip);
        progresstext = (TextView) findViewById(R.id.progress_text);
        waiting=(TextView) findViewById(R.id.waiting);
        step2=(TextView) findViewById(R.id.step2);
        allreadywaited = (TextView) findViewById(R.id.allreadywaited);


        progressBar = (ProgressBar) findViewById(R.id.progressbar_cir);

       skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AnalyzingPageViewController.class));
                Log.e("skiptrue", "call");
                handler.removeCallbacks(updateTimerThread);
                pStatus=0;
                progresstext.setText(""+pStatus);
                progressBar.setProgress(0);
            }
        });

       // handler.postDelayed(updateTimerThread,1000);

        setToolbar();
        updateLanguageTexts();
    }
    @Override
    protected void onResume()
    {
        super.onResume();

        if (isBackbtnClicked){
            progressBar.setVisibility(View.VISIBLE);
            pStatus=120;
            handler.removeCallbacks(updateTimerThread);
        }else {
            Log.e("Test", "OnResumecall");
            pStatus=120;
            progressBar.setVisibility(View.VISIBLE);
            progresstext.setText("120");
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

    public void updateLanguageTexts() {
        step2.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.STEP2_KEY));

        skip.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SKIP_BUTTON_KEY));

        waiting.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WAIT_120_SEC_DISCRIPTION_KEY));

        allreadywaited.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ALERADY_WAITED_20_SEC_SKIP_KEY));

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(updateTimerThread);
        handler.removeCallbacks(null);

    }
    public void setToolbar() {


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar( mToolbar);
        back = (ImageView) mToolbar.findViewById(R.id.back_arraow);
        back1 = (ImageView) mToolbar.findViewById(R.id.backimage);
        back1.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        //
        TextView toolbartext = (TextView)  mToolbar.findViewById(R.id.tool_txt);
        toolbartext.setText("");
        add=(ImageView) mToolbar.findViewById(R.id.add);
        add.setVisibility(View.INVISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                handler.removeCallbacks(updateTimerThread);
                InstructionpageViewController.isCircularBackbtnCilcked =true;
            }
        });
    }

    public  Runnable updateTimerThread = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            progresstext.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(120); // Maximum Progress

            pStatus -= 1;
            progressBar.setProgress(pStatus);
            progresstext.setText(""+pStatus);
            Log.e("pro",""+pStatus);
            handler.postDelayed(this, 100);
            if(pStatus==0){

                startActivity(new Intent(getApplicationContext(),AnalyzingPageViewController.class));
                Log.e("profor100",""+pStatus);
                handler.removeCallbacks(updateTimerThread);
                pStatus=0;
                progressBar.setProgress(0);
                progresstext.setText(""+pStatus);
            }
        }
    };
    @Override
    public void onBackPressed() {
        finish();
        handler.removeCallbacks(updateTimerThread);
        finish();
        InstructionpageViewController.isCircularBackbtnCilcked =true;
        super.onBackPressed();
    }
}

