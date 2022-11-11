package com.example.wave.spectrumhuman.FONTS;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Rise on 12/01/2018.
 */

public class BoldButtonFontStyle   extends Button {
    public BoldButtonFontStyle(Context context) {
        super(context);
        init();
    }

    public BoldButtonFontStyle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoldButtonFontStyle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Candara.ttf");
        setTypeface(tf);
    }
}
