package com.example.wave.spectrumhuman.HomeModule;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by WAVE on 9/25/2017.
 */

public class TestNowViewController extends AppCompatActivity {
    Toolbar toolbar;
    ImageView back;
    TextView tool_text;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testnowpage);
        ButterKnife.bind(this);
        setToolbar();
    }
    public void setToolbar()
    {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=(ImageView)findViewById(R.id.toolbar_icon);//Spectrum
        back.setBackgroundResource(R.drawable.ic_home);
        tool_text=(TextView)toolbar.findViewById(R.id.toolbar_text);
        tool_text.setText("TestNow");
        tool_text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.TEST_NOW_KEY));

    }
    @OnClick(R.id.toolbar_icon)
    public void moveToHome(){
        finish();
    }
}
