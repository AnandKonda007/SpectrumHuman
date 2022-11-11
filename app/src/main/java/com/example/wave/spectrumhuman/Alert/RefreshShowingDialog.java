package com.example.wave.spectrumhuman.Alert;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.LoginModule.ChangePasswordViewController;
import com.example.wave.spectrumhuman.LoginModule.LoginServerObjectDataController;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.SideMenu.ContactUsViewController;


/**
 * Created by .
 */
public class RefreshShowingDialog extends Dialog {
    Dialog dialog;
    ImageView imageView;
    Context context;


    public  RefreshShowingDialog(Context context1) {
        super(context1);
        context = context1;
        dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_animate);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        TextView textView=(TextView)dialog.findViewById(R.id.connecting);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        imageView=(ImageView)dialog.findViewById(R.id.image_rottate) ;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
      /*  TextView text=(TextView)dialog.findViewById(R.id.connecting);
        text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));
*/


    }

    public  void  showAlert(){

        if(!((Activity) context).isFinishing()) {

            RotateAnimation rotate = new RotateAnimation(0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);

            rotate.setDuration(600);
            rotate.setRepeatCount(Animation.INFINITE);
            imageView.setAnimation(rotate);
            dialog.show();

        }

    }

    public void hideRefreshDialog(){
        Log.e("hideRefreshDialog", "call");
        dialog.dismiss();
    }
}
