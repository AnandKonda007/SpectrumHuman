package com.example.wave.spectrumhuman.HomeModule;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;
import com.example.wave.spectrumhuman.DataBase.MemberDataController;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.LoginModule.Constants;
import com.example.wave.spectrumhuman.Models.Member;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.MemberServerOject;
import com.example.wave.spectrumhuman.SideMenu.MemberViewController;
import com.shawnlin.numberpicker.NumberPicker;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by WAVE on 5/25/2017.
 */
public class PersonalInformationViewController extends AppCompatActivity {
    //weeight pickr variables
    NumberPicker numberPickerHeight,measurePicker;
    List<String> feetEnglishArray = new ArrayList<>();
    List<String> feetLanguageArray = new ArrayList<>();

    String[] heightUnitsEnglishArray=new String[2];
    String[] heightUnitsLanguageArray=new String[2];


    String selectedHeightVal="160";
    String tempSelectedHeightVal="160";

    String oldHeightEnglishValue,oldWeightEnglishValue;

    String tempSelectedHeightMeasureUnits =LanguagesKeys.CM_PICKER_KEY;
    String selectedHeightmeasureUnits =tempSelectedHeightMeasureUnits;//selectedHeightmeasureUnits


    List<String> cmStringArray = new ArrayList<>();

    ///
    NumberPicker numberPickerWeight,measurePickerWeight;

    List<String> kgArray;
    List<String> lbsArray;

    String[] weightunitsEnglishArray;
    String[] weightunitsLanguageArray;


    String  tempSelectedweightVal="60";
    String  selectedweightVal="60";


    String tempSelectedWeightUnits = LanguagesKeys.KG_PICKER_KEY;
    String selectedweightMeasureUnits= tempSelectedWeightUnits;


    ////
    RelativeLayout txtAlubm, txtCamera;
    Intent  GalIntent, CropIntent;
    Uri uri;
    public byte[] imageInByte;
    CircleImageView profileImage;
    CircleImageView  beforeImage;
    RadioGroup  radioGroup;
    TextView text_height,text_weight,txt_Birthday,txt_bloodGroup,txt_relation;
    EditText edt_name,edt_email;
    TextView txt_title;

    String[] bloodEnglishStrArray = {"AB+","AB-","A+","A-","B+","B-","O+","O-"};


    int selectdBlood=6,selecedRelation;
    //A+/−, B+/−, AB+/−, or O+/−
    int year,month,day;
    String tempbirthDayObj,profileBase64Obj,oldProfielBase64Obj;
    RadioButton male,female;
    Dialog dialog;
    Member objMember  ;
    Boolean maleSelection = false;
    Boolean oldMaleSection = false;

    SharedPreferences socialMediasettingsPreferences;

    TextView nametext, emailtext, birth, gender, height, weight, bloodgroup;
    Button backbtn, savecontinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        init();

        loadHeightValuesArray();
        loadWeightValuesArray();

        profileImage = (CircleImageView) findViewById(R.id.user);
        radioGroup=(RadioGroup) findViewById(R.id.linear);
        profileImage.setOnClickListener(mProfileListener);
        text_height=(TextView)findViewById(R.id.height) ;
        text_weight=(TextView)findViewById(R.id.weight) ;
        txt_Birthday = (TextView) findViewById(R.id.birth);
        txt_bloodGroup = (TextView) findViewById(R.id.bloodgroup);
        txt_relation = (TextView) findViewById(R.id.txt_relation);
        edt_email=(EditText) findViewById(R.id.email);
        edt_email.setClickable(false);
        edt_email.setFocusable(false);
        edt_name=(EditText) findViewById(R.id.name);
        backbtn=(Button)findViewById(R.id.back);
        txt_title=(TextView)findViewById(R.id.tool_txt);
        savecontinue=(Button)findViewById(R.id.savecontinue);

        female=(RadioButton)findViewById(R.id.female);
        male=(RadioButton)findViewById(R.id.male);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Candara.ttf");
        female.setTypeface(font);
        male.setTypeface(font);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                hideSoftKeyboard(edt_name);
                switch(checkedId){
                    case R.id.male:
                        // do operations specific to this selection
                        maleSelection = true;
                        break;
                    case R.id.female:
                        // do operations specific to this selection
                        maleSelection = false;
                        break;

                }
            }
        });
        loadDefaultImage();
        setMemberData();

        updateLanguageTexts();


    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onPause() {
        super.onPause();

    }
    private void init() {
         nametext =(TextView)findViewById(R.id.name_text);
         emailtext =(TextView)findViewById(R.id.email_txt);
         birth =(TextView)findViewById(R.id.txt_birth);
         gender =(TextView)findViewById(R.id.gender);
         height =(TextView)findViewById(R.id.txt_height);
         weight =(TextView)findViewById(R.id.txt_weight);
         bloodgroup =(TextView)findViewById(R.id.txt_bloodgroup);
    }
    public  void setMemberData(){
           MemberDataController.getInstance().fetchMemberData();
           objMember=MemberDataController.getInstance().getAdminDetails();

        if(objMember != null)
        {
            Log.e("editProfile","call");
            Log.e("EditEmail1",""+objMember.getUser_Id());
            Log.e("EditEmail",""+objMember.getEmail());
            Log.e("bytImage",""+objMember.getMprofilepicturepath());
            txt_title.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.EDIT_PROFILE_TITLE_KEY));
            text_height.setText(""+objMember.getMheight());
            text_weight.setText(""+objMember.getMweight());
            txt_Birthday.setText(""+objMember.getMbirthday());
            txt_bloodGroup.setText(""+objMember.getMbloodgroup());
            txt_relation.setText(""+objMember.getMrelationshipname());
            edt_name.setText(""+objMember.getMname());

            edt_email.setText(""+objMember.getEmail());

            ViewGroup.LayoutParams par1 = edt_email.getLayoutParams();
            par1.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            edt_email.setLayoutParams(par1);

            backbtn.setVisibility(View.VISIBLE);
            tempbirthDayObj = txt_Birthday.getText().toString();
            Log.e("EditMheight",""+objMember.getMheight());
            //
            Log.e("profilPic",""+objMember.getMprofilepicturepath());

            if  (objMember.getMgender().equalsIgnoreCase(LanguagesKeys.PROFILE_MALE_KEY))
            {
                maleSelection = true;
                oldMaleSection = true;

            }

            oldHeightEnglishValue = objMember.getMheight();
            oldWeightEnglishValue = objMember.getMweight();


            tempSelectedHeightVal = objMember.getMheight();
            selectedHeightVal = tempSelectedHeightVal;

            // Load selecetd Language Texts to Textviews. // Height
            if(objMember.getMheight().contains(LanguagesKeys.CM_PICKER_KEY))
            {
                Log.e("EditMheight",""+objMember.getMheight());

                String [] heightStringArray = objMember.getMheight().split(" ");
                tempSelectedHeightVal = heightStringArray[0];
                selectedHeightVal = tempSelectedHeightVal;

                tempSelectedHeightMeasureUnits = LanguagesKeys.CM_PICKER_KEY;
                selectedHeightmeasureUnits = tempSelectedHeightMeasureUnits;

                String  requiredString = selectedHeightVal +" "+ LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CM_PICKER_KEY);
                text_height.setText(requiredString);

            }
            else if (objMember.getMheight().contains(LanguagesKeys.FEET_PICKER_KEY))
            {
                tempSelectedHeightMeasureUnits = LanguagesKeys.FEET_PICKER_KEY;
                selectedHeightmeasureUnits = tempSelectedHeightMeasureUnits;
                tempSelectedHeightVal = objMember.getMheight();
                selectedHeightVal = tempSelectedHeightVal;
                String requiredString = selectedHeightVal;  // For converting Language.
                requiredString = requiredString.replace(LanguagesKeys.FEET_PICKER_KEY,LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FEET_PICKER_KEY));
                requiredString = requiredString.replace(LanguagesKeys.IN_PICKER_KEY,LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.IN_PICKER_KEY));
                text_height.setText(requiredString);

            }

            // Weight
            Log.e("objMemberweight",""+objMember.getMweight());

            String[] weightArray = objMember.getMweight().split(" ");

            tempSelectedweightVal = weightArray[0];
            selectedweightVal = tempSelectedweightVal;

            tempSelectedWeightUnits = weightArray[1];
            selectedweightMeasureUnits = tempSelectedWeightUnits;


            if(selectedweightMeasureUnits.contains(LanguagesKeys.KG_PICKER_KEY))
            {
                text_weight.setText(objMember.getMweight().replace(LanguagesKeys.KG_PICKER_KEY,LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.KG_PICKER_KEY)));

            }
            else if (selectedweightMeasureUnits.contains(LanguagesKeys.LBS_PICKER_KEY))
            {
                text_weight.setText(objMember.getMweight().replace(LanguagesKeys.LBS_PICKER_KEY,LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LBS_PICKER_KEY)));

            }

            profileImage.setImageBitmap(convertByteArrayTOBitmap(objMember.getMprofilepicturepath()));
            beforeImage = profileImage;
            loadEncoded64ImageStringFromBitmap(convertByteArrayTOBitmap(objMember.getMprofilepicturepath()));
            oldProfielBase64Obj = profileBase64Obj;

        }else {
            tempSelectedHeightVal="160";
            selectedHeightVal = tempSelectedHeightVal;

            tempSelectedHeightMeasureUnits =LanguagesKeys.CM_PICKER_KEY;
            selectedHeightmeasureUnits = tempSelectedHeightMeasureUnits;

            tempSelectedweightVal = "60";
            selectedweightVal = tempSelectedweightVal;

            tempSelectedWeightUnits = LanguagesKeys.KG_PICKER_KEY;
            selectedweightMeasureUnits = tempSelectedWeightUnits;

            tempbirthDayObj="1985-01-01";
            Log.e("Profile","call");
            txt_title.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SET_PROFILE_KEY));//SET_PROFILE_KEY
            text_height.setText("");
            text_weight.setText("");
            txt_Birthday.setText("");
            txt_bloodGroup.setText("");
            backbtn.setVisibility(View.INVISIBLE);
            txt_relation.setText("Me");
            edt_email.setText(""+UserDataController.getInstance().currentUser.getUserName());


            if(UserDataController.getInstance().currentUser.getRegisterType() != Constants.RegisterTypes.Manual.toString())
            {
                Log.e("SocialStatus","CalledInside");
                socialMediasettingsPreferences = getSharedPreferences("socialMediaLoginDetails", Context.MODE_PRIVATE);

                String nameString = socialMediasettingsPreferences.getString("name",null);
                String genderString = socialMediasettingsPreferences.getString("gender",null);
                String loginType = socialMediasettingsPreferences.getString("Type", null);
                String imageUriString = socialMediasettingsPreferences.getString("picture", null);

                Log.e("SocialStatus",""+nameString);
                Log.e("SocialStatus",""+genderString);
                Log.e("SocialStatus",""+loginType);
                Log.e("SocialStatus",""+imageUriString);

                // set Name
                if(nameString != null)
                {
                    edt_name.setText(nameString);
                    edt_name.setClickable(false);
                }
                // setGender
                if(genderString != null)
                {
                    if(genderString.equalsIgnoreCase("male"))
                    {
                        maleSelection = true;
                    }
                }


                if (loginType != null) {

                    if (loginType.equalsIgnoreCase(Constants.RegisterTypes.Google.toString())) {
                        if (imageUriString != null) {
                            Uri imageUri = Uri.parse(imageUriString);

                            Glide.with(this)
                                    .asBitmap()
                                    .load(imageUri)
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                            profileImage.setImageBitmap(resource);
                                            loadEncoded64ImageStringFromBitmap(resource);

                                        }
                                    });
                       //     Glide.with(getApplicationContext()).load(imageUri).into(profileImage);



                        }
                    } else if (loginType.equalsIgnoreCase(Constants.RegisterTypes.Facebook.toString())) {

                        if (imageUriString != null) {

                            try {
                                URL img_value = new URL(imageUriString);

                                Glide.with(this)
                                        .asBitmap()
                                        .load(img_value)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                                profileImage.setImageBitmap(resource);
                                                loadEncoded64ImageStringFromBitmap(resource);

                                            }
                                        });
                               // Glide.with(getApplicationContext()).load(img_value).into(profileImage);

                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }


            }else {
                Log.e("manual","call");
            }
        }
        if(maleSelection){

            male.setChecked(true);
            female.setChecked(false);
        }
        else
        {
            female.setChecked(true);
            male.setChecked(false);
        }
    }

    public  boolean checkProfielDataNotChanged(){
        Log.e("checkProfielDataNotChanged",""+selectedHeightVal);


        String compareHeightVal=selectedHeightVal;
        if (selectedHeightmeasureUnits.contains(LanguagesKeys.CM_PICKER_KEY)){
            compareHeightVal=selectedHeightVal+" "+selectedHeightmeasureUnits;
            Log.e("compareHeightVal",""+compareHeightVal);

        }

        Log.e("oldWeightEnglishValue",""+oldWeightEnglishValue);
        Log.e("oldWeightEnglishValue1",""+(selectedweightVal+" "+selectedweightMeasureUnits));

        Log.e("oldHeightEnglishValue",""+oldHeightEnglishValue);
        Log.e("oldHeightEnglishValue1",""+compareHeightVal);



        if (objMember.getMname().trim().equals(edt_name.getText().toString().trim()) && objMember.getMbirthday().equals(txt_Birthday.getText().toString()) &&
                oldHeightEnglishValue.equals(compareHeightVal) &&
                oldWeightEnglishValue.equals((selectedweightVal+" "+selectedweightMeasureUnits)) && oldMaleSection == maleSelection && oldProfielBase64Obj.equals(profileBase64Obj))
        {
            Log.e("checkProfielData","call");
            return  true;
        }
        return  false;
    }

    public  void loadDefaultImage(){

        profileImage.setImageResource( R.drawable.ic_profiledefault);
        Bitmap resource = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profiledefault);
        loadEncoded64ImageStringFromBitmap(resource);

    }


    @OnClick(R.id.back)
    public void back() {
        finish();

    }
    @OnClick(R.id.name)
    public void nameonlick() {
    }
    @OnClick(R.id.email)
    public void email() {

    }
    @OnClick(R.id.birth)
    public void birth() {

        hideSoftKeyboard(edt_name);
        final DatePickerDialog dialog;
        if(txt_Birthday.getText().toString().isEmpty()) {
            Log.e("ifcall","call");
            String[] txtBirthdayArray = tempbirthDayObj.split("-");
            year = Integer.parseInt(txtBirthdayArray[0]);
            month = Integer.parseInt(txtBirthdayArray[1]);
            day = Integer.parseInt(txtBirthdayArray[2]);
            Log.e("textdate","call"+year+"-"+month+"-"+day);
            dialog =
                    new DatePickerDialog(this, null, year, month-1, day);
            dialog.getDatePicker().setMaxDate(new Date().getTime());

            dialog.show();
        }else {
            tempbirthDayObj= txt_Birthday.getText().toString();
            String[] txtBirthdayArray = tempbirthDayObj.split("-");
            year = Integer.parseInt(txtBirthdayArray[0]);
            month = Integer.parseInt(txtBirthdayArray[1]);
            day = Integer.parseInt(txtBirthdayArray[2]);
            Log.e("textdate","call"+year+"-"+month+"-"+day);
            dialog =
                    new DatePickerDialog(this, null, year, month-1, day);
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            dialog.show();
        }
        dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog1, int which) {
                        DatePicker objDatePicker = dialog.getDatePicker();
                        year = objDatePicker.getYear();
                        month=objDatePicker.getMonth();
                        day=objDatePicker.getDayOfMonth();
                        txt_Birthday.setText(year + "-" + (month+1) + "-" + day);
                        Log.e("date",""+txt_Birthday.getText().toString());
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //txt_Birthday.setText(year + "/" + (month) + "/" + day);
                    }
                });
    }
    @OnClick(R.id.txt_relation)
    public void relationship() {
        setPickerType("relationShip");


    }

    @OnClick(R.id.height)
    public void height() {
        setPickerType("height");

    }
    @OnClick(R.id.weight)
    public void weight() {
        setPickerType("weight");

    }

    @OnClick(R.id.bloodgroup)
    public void bloodgroup() {
        setPickerType("bloodGroup");

    }
    @OnClick(R.id.savecontinue)
    public void savecontinue() {
        isAllFeidsHavingText();
    }
    View.OnClickListener mProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(PersonalInformationViewController.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.profile_alert);
            dialog.show();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
            txtCamera = (RelativeLayout) dialog.findViewById(R.id.camera);
            txtAlubm = (RelativeLayout) dialog.findViewById(R.id.album);

            TextView photo=(TextView)dialog.findViewById(R.id.textview);
            TextView camera=(TextView)dialog.findViewById(R.id.txt_camera);
            TextView album=(TextView)dialog.findViewById(R.id.txt_album);
            TextView cancle=(TextView)dialog.findViewById(R.id.txt_cancle);

            photo.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PHOTO_KEY));
            camera.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CAMERA_KEY));
            album.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.GALLERY_KEY));
            cancle.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

            txtCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClickImageFromCamera();
                    dialog.dismiss();
                }
            });
            txtAlubm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetImageFromGallery();
                    dialog.dismiss();
                }
            });

            RelativeLayout cancel = (RelativeLayout) dialog.findViewById(R.id.btn_no);
            cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    dialog.cancel();
                }
            });

        }
    };

    public void setPickerType(String pickerType){
        switch (pickerType){
            case "height":
                setHeightPickerValue(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SET_HEIGHT_KEY),text_height);
                break;
            case "weight":
                setWeightPickerValue(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SET_WEIGHT_KEY),text_weight);
                break;
            case "bloodGroup":
                setBloodPickerValue(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.BLOOD_GROUP_PICKER_TITLE_KEY),txt_bloodGroup, bloodEnglishStrArray);
                break;
            default:{

                }
        }
    }
    public void setHeightPickerValue(String title, final TextView textView){
        final Dialog mod = new Dialog(PersonalInformationViewController.this);
        mod.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mod.setContentView(R.layout.activity_picker_alert);
        TextView txtTitle = (TextView) mod.findViewById(R.id.text_info);
        txtTitle.setText(title);
        mod.show();
        mod.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        numberPicker(mod);

        if (textView.getText().toString().isEmpty()){
            Log.e("tempSelectedHeightVal",""+tempSelectedHeightVal);
            selectedHeightmeasureUnits=tempSelectedHeightMeasureUnits;
            selectedHeightVal=tempSelectedHeightVal;

            Log.e("selectedHeightVal",""+selectedHeightVal);
        }

        if (selectedHeightmeasureUnits.contains(LanguagesKeys.CM_PICKER_KEY)) {

            loadCmArray();

        } else {
            Log.e("feetcall", "call");
            loadfeetArray();
        }

        measurePicker(mod);

        Button ok=(Button)mod.findViewById(R.id.btn_ok);
        Button cancle=(Button)mod.findViewById(R.id.btn_cancel);
        ok.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
        cancle.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mod.cancel();
                selectedHeightmeasureUnits = tempSelectedHeightMeasureUnits;
                Log.e("SelectedMeasureUnit",selectedHeightmeasureUnits);
                if (tempSelectedHeightMeasureUnits.equals(LanguagesKeys.CM_PICKER_KEY)) {
                    selectedHeightVal = tempSelectedHeightVal;
                    selectedHeightmeasureUnits = tempSelectedHeightMeasureUnits;

                    String  requiredString = selectedHeightVal +" "+ LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CM_PICKER_KEY);
                    Log.e("requiredString","call"+requiredString);

                    textView.setText(requiredString);
                }else {
                    selectedHeightVal = tempSelectedHeightVal;
                    selectedHeightmeasureUnits = tempSelectedHeightMeasureUnits;

                    String requiredString = selectedHeightVal;  // For converting Language.
                    requiredString = requiredString.replace(LanguagesKeys.FEET_PICKER_KEY,LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FEET_PICKER_KEY));
                    requiredString = requiredString.replace(LanguagesKeys.IN_PICKER_KEY,LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.IN_PICKER_KEY));
                    text_height.setText(requiredString);
                    Log.e("requiredString","call"+requiredString);
                }
                Log.e("selectedHeightVal",""+textView.getText().toString());

            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempSelectedHeightVal = selectedHeightVal;
                tempSelectedHeightMeasureUnits = selectedHeightmeasureUnits;
                mod.cancel();

            }
        });
    }
    public void setWeightPickerValue(String title,final TextView textView){
        final Dialog mod = new Dialog(PersonalInformationViewController.this);
        mod.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mod.setContentView(R.layout.activity_picker_alert);
        TextView txtTitle = (TextView) mod.findViewById(R.id.text_info);
        txtTitle.setText(title);
        mod.show();
        mod.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        weightnumberPicker(mod);

        if (textView.getText().toString().isEmpty()){
            selectedweightVal=tempSelectedweightVal;
            selectedweightMeasureUnits=tempSelectedWeightUnits;
            Log.e("selectedweightVal",""+selectedweightVal);
        }else /*{
            Log.e("selectedweightValelse",""+selectedweightVal);
            selectedweightVal=tempSelectedweightVal+" "+selectedweightMeasureUnits;

        }*/

            if (selectedweightMeasureUnits.contains(LanguagesKeys.KG_PICKER_KEY)) {
                loadKgArray();
            } else {
                loadlbsArray();
            }

        weightmeasurePicker(mod);

        Button ok=(Button)mod.findViewById(R.id.btn_ok);
        Button cancle=(Button)mod.findViewById(R.id.btn_cancel);
        ok.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
        cancle.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mod.cancel();
                selectedweightVal = tempSelectedweightVal;
                selectedweightMeasureUnits = tempSelectedWeightUnits;
                textView.setText(selectedweightVal + " " + LanguageTextController.getInstance().currentLanguageDictionary.get(selectedweightMeasureUnits));
                Log.e("weighttextView",""+textView.getText().toString());
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempSelectedweightVal = selectedweightVal;
                tempSelectedWeightUnits = selectedweightMeasureUnits;
                mod.cancel();
            }
        });
    }
    public void setBloodPickerValue(String title, final TextView textView, final String[] type){
        final Dialog mod = new Dialog(this);
        mod.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mod.setContentView(R.layout.activity_picker_alert);
        RelativeLayout heightweightPickers=(RelativeLayout)mod.findViewById(R.id.rl_number_picker);
        heightweightPickers.setVisibility(View.INVISIBLE);//invisible height weight pickers
        //
        RelativeLayout bloodPickers=(RelativeLayout)mod.findViewById(R.id.rl_blood);
        bloodPickers.setVisibility(View.VISIBLE);//visible blood pickers
        mod.show();
        mod.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        final NumberPicker numberPicker = (NumberPicker) mod.findViewById(R.id.number_pickerthree);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(type.length-1);
        numberPicker.setDisplayedValues(type);
        numberPicker.setValue(selectdBlood);
        numberPicker.setWrapSelectorWheel(true);
        TextView txtTitle = (TextView) mod.findViewById(R.id.text_info);
        txtTitle.setText(""+title);

        Button ok=(Button)mod.findViewById(R.id.btn_ok);
        Button cancle=(Button)mod.findViewById(R.id.btn_cancel);
        ok.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
        cancle.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mod.cancel();
                selectdBlood = numberPicker.getValue();
                String selecPicker = type[selectdBlood];
                numberPicker.setValue(selectdBlood);
                textView.setText(""+ selecPicker);
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mod.cancel();
            }
        });

    }
    public void isAllFeidsHavingText(){

        if(edt_name.getText().toString().isEmpty()){

            new AlertShowingDialog(PersonalInformationViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_NAME_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

        }else if(edt_email.getText().toString().trim().isEmpty()) {

            new AlertShowingDialog(PersonalInformationViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_EMAIL_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));


        }else if(!isValidEmail(edt_email.getText().toString().trim())){


            new AlertShowingDialog(PersonalInformationViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_VALID_EMAIL_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));


        } else if(txt_Birthday.getText().toString().isEmpty()){
            new AlertShowingDialog(PersonalInformationViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_CHOOSE_DATE_OF_BIRTH_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));


        }else if(txt_relation.getText().toString().isEmpty()){

            new AlertShowingDialog(PersonalInformationViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_CHOOSE_RELATION_SHIP_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

        }else if(text_height.getText().toString().isEmpty()){

            new AlertShowingDialog(PersonalInformationViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_CHOOSE_HEIGHT_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));


        }else if(text_weight.getText().toString().isEmpty()){

            new AlertShowingDialog(PersonalInformationViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_CHOOSE_WEIGHT_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));


        }else if(txt_bloodGroup.getText().toString().isEmpty()){
            new AlertShowingDialog(PersonalInformationViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_CHOOSE_BLOOD_GROUP_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

        }else {

            if (objMember != null)
            {
                if (checkProfielDataNotChanged())
                {
                    startActivity(new Intent(getApplicationContext(),HomeActivityViewController.class));
                    return;
                }
            }
                if (isConn()) {
                    showRefreshDialogue();
                    Long unixTime = System.currentTimeMillis() / 1L;
                    Log.e("time", "call" + String.valueOf(unixTime));
                    String genderText = LanguagesKeys.PROFILE_FEMALE_KEY;
                    if (maleSelection) {
                        genderText = LanguagesKeys.PROFILE_MALE_KEY;
                    }
                    Log.e("selectedHeightValcall",""+selectedHeightVal);

                    if (selectedHeightmeasureUnits.contains(LanguagesKeys.CM_PICKER_KEY)){
                        selectedHeightVal=selectedHeightVal+" "+selectedHeightmeasureUnits;
                        Log.e("selectedHeightValcall",""+selectedHeightVal);
                    }
                    insertPersonInfoToServer(edt_email.getText().toString().trim(), edt_name.getText().toString(), genderText, (selectedHeightVal), (selectedweightVal + " " + selectedweightMeasureUnits),
                            txt_Birthday.getText().toString().trim(), txt_bloodGroup.getText().toString().trim(), String.valueOf(unixTime), profileBase64Obj);
                } else {
                    new AlertShowingDialog(PersonalInformationViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }
            }
    }
    public void failurealert() {

        Log.e("responsealert", "call");
        final Dialog failurealert = new Dialog(this);
        failurealert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        failurealert.setCancelable(false);
        failurealert.setCanceledOnTouchOutside(false);
        failurealert.setCancelable(true);
        failurealert.setContentView(R.layout.activity_failurealert);
        failurealert.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        failurealert.show();

        TextView text=(TextView)failurealert.findViewById(R.id.text_error);
        text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ERROR_KEY));

        TextView text1=(TextView)failurealert.findViewById(R.id.requestfail);
        // text1.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ERROR_KEY));

        Button cancel = (Button) failurealert.findViewById(R.id.btn_failurecancel);
        cancel.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys. CANCEL_KEY ));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("cancel","canel");

                failurealert.dismiss();


            }


        });
        Button retry= (Button) failurealert.findViewById(R.id.btn_failureretry);
        // retry.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.Ret ));

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("retry","retry");
                if (isConn()){

                    failurealert.dismiss();
                    showRefreshDialogue();

                    Long unixTime = System.currentTimeMillis() / 1L;
                    Log.e("time", "call" + String.valueOf(unixTime));
                    String genderText = LanguagesKeys.PROFILE_FEMALE_KEY;
                    if (maleSelection) {
                        genderText = LanguagesKeys.PROFILE_MALE_KEY;
                    }
                    Log.e("selectedHeightValcall",""+selectedHeightVal);

                    if (selectedHeightmeasureUnits.contains(LanguagesKeys.CM_PICKER_KEY)){
                        selectedHeightVal=selectedHeightVal+" "+selectedHeightmeasureUnits;
                        Log.e("selectedHeightValcall",""+selectedHeightVal);
                    }
                    insertPersonInfoToServer(edt_email.getText().toString().trim(), edt_name.getText().toString(), genderText, (selectedHeightVal), (selectedweightVal + " " + selectedweightMeasureUnits),
                            txt_Birthday.getText().toString().trim(), txt_bloodGroup.getText().toString().trim(), String.valueOf(unixTime), profileBase64Obj);

                }else {
                    failurealert.dismiss();
                    new AlertShowingDialog(PersonalInformationViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }


        });


    }
    public void insertPersonInfoToServer(String email, String name, final String gender, final String height, final String weight, String dob, String bloodGroup, String addedTime, String profilePic) {
        Log.e("Heighit","call"+height);
        Log.e("weighit","call"+weight);

        final MemberServerOject requestBody = new MemberServerOject();
        requestBody.setEmail(email);
        requestBody.setUser_Id(UserDataController.getInstance().currentUser.getUserName().trim());
        requestBody.setMname(name);
        requestBody.setMgender(gender);
        requestBody.setMheight(height);
        requestBody.setMweight(weight);
        requestBody.setMbirthday(dob);
        requestBody.setMbloodgroup(bloodGroup);
        requestBody.setTime(addedTime);
        requestBody.setProfilepicturepath(profilePic);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);

        Call<MemberServerOject> callable;

        if(objMember != null)
        {
            callable = api.editPersonalInfo(requestBody);
        }
        else
        {
            callable = api.personalInfo(requestBody);
        }

        callable.enqueue(new Callback<MemberServerOject>() {
            @Override
            public void onResponse(Call<MemberServerOject> call, Response<MemberServerOject> response) {
                Log.e("codefor3","call"+response.body());

                String statusCode = response.body().getResponse();
                String message=response.body().getMessage();
                if(statusCode == null){
                    handler.removeCallbacks(updateTimerThread);
                    dialog.dismiss();
                }else {
                    if(statusCode.equals("3")){
                        Log.e("callresuposecode","call"+response.code());
                        if(objMember != null)
                        {
                              MemberDataController.getInstance().updateMemberData(UserDataController.getInstance().currentUser.getUserName(),"admin",edt_name.getText().toString(),edt_email.getText().toString().trim(),txt_Birthday.getText().toString(),"Me",gender,height,weight, txt_bloodGroup.getText().toString().trim(),imageInByte,objMember.getActiveMember());
                        }
                        else {

                            MemberDataController.getInstance().insertMemberData(UserDataController.getInstance().currentUser.getUserName(),"admin", edt_name.getText().toString(),edt_email.getText().toString().trim(), txt_Birthday.getText().toString(), "Me", gender, height, weight, txt_bloodGroup.getText().toString().trim(), imageInByte, true,true);

                            // Remove stored Shared preferences details
                            if(!UserDataController.getInstance().currentUser.getRegisterType().equals(Constants.RegisterTypes.Manual.toString())) {
                                SharedPreferences.Editor editor = socialMediasettingsPreferences.edit();
                                editor.clear();
                                editor.commit();
                            }
                        }
                        MemberDataController.getInstance().fetchMemberData();
                        EventBus.getDefault().post(new SideMenuViewController.MessageEvent("refreshMemberTable"));
                        startActivity(new Intent(getApplicationContext(),HomeActivityViewController.class));
                    }
                }
                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();

            }
            @Override
            public void onFailure(Call<MemberServerOject> call, Throwable t) {
                //new AlertShowingDialog(PersonalInformationViewController.this, "Try Again");
                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();
                failurealert();
            }
        });

    }
    ImageView imageView;
    Handler handler = new Handler();
    public void showRefreshDialogue(){
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_animate);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        TextView textView=(TextView)dialog.findViewById(R.id.connecting);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));
        imageView=(ImageView)dialog.findViewById(R.id.image_rottate) ;
        handler.postDelayed(updateTimerThread, 100);
    }
    public  Runnable updateTimerThread = new Runnable() {
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

    public void hideSoftKeyboard(EditText edtView)
    {
        Log.e("hideSoftKeyboard", "call");
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtView.getWindowToken(), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    public void ClickImageFromCamera() {

        Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1);

    }

    public void GetImageFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            GalIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 2);
       } else {
            GalIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 2);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {

            ImageCropFunction();

        } else if (requestCode == 2) {

            if (data != null) {
                uri = data.getData();
                //ImageCropFunction();
                decodeUri(uri);

            }
        } else if (requestCode == 1) {

            if (data != null)
            {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap yourImage = bundle.getParcelable("data");
                    // convert bitmap to byte
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    yourImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    imageInByte = stream.toByteArray();
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(imageInByte);
                    Bitmap theImage = BitmapFactory.decodeStream(imageStream);
                    theImage = getResizedBitmap(theImage,200);
                    profileImage.setImageBitmap(theImage);
                    loadEncoded64ImageStringFromBitmap(theImage);

                }
            }


        }
    }

    public void ImageCropFunction() {

        // Image Crop Code
        try {

            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri, "image/*");
            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);
            startActivityForResult(CropIntent, 1);

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();

        }
    }
    public void decodeUri(Uri uri) {
        ParcelFileDescriptor parcelFD = null;
        try {
            parcelFD = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = parcelFD.getFileDescriptor();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = 1024;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageInByte = stream.toByteArray();
            ByteArrayInputStream imageStream = new ByteArrayInputStream(imageInByte);
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            theImage = getResizedBitmap(theImage,200);
            profileImage.setImageBitmap(theImage);
            loadEncoded64ImageStringFromBitmap(theImage);


        }  catch (IOException e) {
            // handle errors
        } finally {
            if (parcelFD != null)
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    // ignored
                }
        }
    }
    public void loadEncoded64ImageStringFromBitmap(Bitmap bitmap) {


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        imageInByte = stream.toByteArray();
        profileBase64Obj = Base64.encodeToString(imageInByte, Base64.NO_WRAP);
        Log.e("base64Image","call"+profileBase64Obj);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    public Bitmap convertByteArrayTOBitmap(byte[] profilePic){
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(profilePic);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;

    }
    public boolean isValidEmail(String target) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(target).matches();
    }
    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }
    private  void  measurePicker(Dialog mod){
        ///mearsurePicker
        measurePicker = (NumberPicker) mod.findViewById(R.id.number_pickertwo);
        measurePicker.setWrapSelectorWheel(true);
        measurePicker.setMaxValue(heightUnitsLanguageArray.length-1);
        measurePicker.setMinValue(0);
        measurePicker.setDisplayedValues(heightUnitsLanguageArray);

        final List<String> objHeightUnitsList = Arrays.asList(heightUnitsEnglishArray);

        int index = objHeightUnitsList.indexOf(selectedHeightmeasureUnits);
        measurePicker.setValue(index);
        measurePicker.setWrapSelectorWheel(true);
        measurePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                String newMeasureString = objHeightUnitsList.get(newVal);
                Log.e("SELECT_HEIGHT", "" + newMeasureString);

                if(!tempSelectedHeightMeasureUnits.equalsIgnoreCase(newMeasureString )) {
                    if (newMeasureString.equalsIgnoreCase(LanguagesKeys.CM_PICKER_KEY)) {

                        tempSelectedHeightMeasureUnits = newMeasureString;
                        tempSelectedHeightVal = convertFeetToCm(tempSelectedHeightVal);
                        Log.e("convertedCMValue",""+tempSelectedHeightVal);

                        loadCmArray();

                    } else if(newMeasureString.equalsIgnoreCase(LanguagesKeys.FEET_PICKER_KEY)) {

                        tempSelectedHeightMeasureUnits = newMeasureString;
                        tempSelectedHeightVal = convertCmToFeet(tempSelectedHeightVal);
                        loadfeetArray();

                    }
                }
            }
        });

    }

    private void numberPicker(Dialog mod){

        numberPickerHeight = (NumberPicker) mod.findViewById(R.id.number_picker);

        if(tempSelectedHeightMeasureUnits.equalsIgnoreCase(LanguagesKeys.CM_PICKER_KEY)) {
            loadCmArray();//cm array
        }
        else
        {
            loadfeetArray(); // Feet array
        }
        numberPickerHeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                if(tempSelectedHeightMeasureUnits.equalsIgnoreCase(LanguagesKeys.CM_PICKER_KEY))
                {
                    tempSelectedHeightVal = cmStringArray.get(newVal);
                }
                else
                {
                    tempSelectedHeightVal = feetEnglishArray.get(newVal);
                }

            }
        });
    }

    private  void loadHeightValuesArray(){


        for(int i=93; i<=243; i++) {
            cmStringArray.add(String.valueOf(i));
        }

        loadFeetArrayInEnglish();
        loadFeetArrayInLanguage();
        loadHeightUnitsArrayInEnglish();
        loadHeightUnitsArrayInLangauage();

        Log.e("cmArray","call"+ cmStringArray);


    }

    public void loadHeightUnitsArrayInEnglish(){
        heightUnitsEnglishArray[0]=LanguagesKeys.CM_PICKER_KEY;
        heightUnitsEnglishArray[1]=LanguagesKeys.FEET_PICKER_KEY;

    }
    public void loadHeightUnitsArrayInLangauage(){
        heightUnitsLanguageArray[0]=LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CM_PICKER_KEY);
        heightUnitsLanguageArray[1]=LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FEET_PICKER_KEY);

    }
    public void loadFeetArrayInEnglish(){
        feetEnglishArray =new ArrayList<String>();
        Log.e("ft","call");
        for (int i = 3; i <= 8; i++)
        {
            if (i == 8)
            {
                String s = i +" "+ LanguagesKeys.FEET_PICKER_KEY +" "+"0"+" "+ LanguagesKeys.IN_PICKER_KEY;
                feetEnglishArray.add(s);
            }
            else {
                for (int j = 0; j <= 11; j++) {
                    String inchString = String.valueOf(j);

                  /*  if (j < 10) {
                        inchString = "0" + inchString;
                    }
*/
                    String s = i +" "+LanguagesKeys.FEET_PICKER_KEY +" "+inchString+" "+LanguagesKeys.IN_PICKER_KEY;
                    feetEnglishArray.add(s);
                }
            }

        }

    }
    public void loadFeetArrayInLanguage(){
        feetLanguageArray =new ArrayList<String>();
        Log.e("ft","call");
        for (int i = 3; i <= 8; i++)
        {
            if (i == 8)
            {
                String s = i +" "+ LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FEET_PICKER_KEY) +" "+"0"+" "+ LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.IN_PICKER_KEY);

               // String s = i +" "+ LanguagesKeys.FEET_PICKER_KEY +" "+"0"+" "+ LanguagesKeys.IN_PICKER_KEY;
                feetLanguageArray.add(s);
            }
            else {
                for (int j = 0; j <= 11; j++) {
                    String inchString = String.valueOf(j);

                    /*if (j < 10) {
                        inchString = "0" + inchString;
                    }
*/
                    String s = i +" "+ LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FEET_PICKER_KEY) +" "+inchString+" "+ LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.IN_PICKER_KEY);

                    //String s = i +" "+LanguagesKeys.FEET_PICKER_KEY +" "+inchString+" "+LanguagesKeys.IN_PICKER_KEY;
                    feetLanguageArray.add(s);
                }
            }

        }

    }
    public String convertCmToFeet(String cmValue){

        String[] cmValueArray = cmValue.split(" ");

        Double cmVal=Double.parseDouble(cmValueArray[0]);
        Log.e("cm","call"+cmVal);
        double inchesValue = cmVal * 0.39370;
        int feetValue  = (int) (inchesValue/12);

        int remainInchesValue = (int) (inchesValue%12);

      /*  String remainInchString = String.valueOf(remainInchesValue);

        if (remainInchesValue>0 && remainInchesValue<10) {
            remainInchString = "0" + remainInchesValue;
        }*/

        String requiredString  = feetValue+" "+LanguagesKeys.FEET_PICKER_KEY+" "+remainInchesValue+" "+LanguagesKeys.IN_PICKER_KEY;

        Log.e("requiredFeetString", requiredString);
        return  requiredString;

    }
    public String convertFeetToCm(String feetValue){
        Log.e("convertFeetToCm",""+feetValue);
        String requiredString = feetValue.replace(LanguagesKeys.FEET_PICKER_KEY,".");
        requiredString = requiredString.replace(LanguagesKeys.IN_PICKER_KEY,"");
        requiredString = requiredString.replace(" ","");

        String[] feetStrArray  = requiredString.split("\\.");

        double totalInches = 0.0 ;
        double feetInches = Double.parseDouble(feetStrArray[0]);
        double inches = Double.parseDouble(feetStrArray[1]);

        Log.e("feetInches",""+feetInches+""+inches);
        totalInches = feetInches*12+inches;

        Double feetDouble= new Double(totalInches*2.54);

        int feetint = (int) Math.round(feetDouble);

        if (feetint < 93)
        {
            feetint = 93;
        }
        Log.e("feetint","call"+feetint);
        String strValue= String.valueOf(feetint);

        return strValue;

    }


    private  void  loadWeightValuesArray(){

        kgArray = new ArrayList<String>();;
        Log.e("kg","call");
        for(int i=29; i<=350; i++) {
            kgArray.add(String.valueOf(i));
        }

        lbsArray=new ArrayList<String>();
        for (int j = 63; j <= 772; j++)
        {
            lbsArray.add(String.valueOf(j));
        }
        loadWeightUnitaArrayInEnglish();
        loadWeightUnitaArrayInLanguage();
        //


    }
    public void loadWeightUnitaArrayInEnglish(){
        weightunitsEnglishArray = new String[2];
        weightunitsEnglishArray[0] = LanguagesKeys.KG_PICKER_KEY;
        weightunitsEnglishArray[1] = LanguagesKeys.LBS_PICKER_KEY;

    }
    public void loadWeightUnitaArrayInLanguage(){
        weightunitsLanguageArray =  new String[2];
        weightunitsLanguageArray[0] = LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.KG_PICKER_KEY);
        weightunitsLanguageArray[1] = LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LBS_PICKER_KEY);

    }
    public void loadKgArray(){
        Log.e("loadKgArray","call");

        numberPickerWeight.setDisplayedValues(null);
        // Get Index of selected kg value
        int index = kgArray.indexOf(tempSelectedweightVal);
        Log.e("tempSelectedweightVal",""+ tempSelectedweightVal);
        Log.e("heightValueInKgsArayIndex",""+ index);

        numberPickerWeight.setMinValue(0);
        numberPickerWeight.setMaxValue(kgArray.size()-1);


        String[] mStringArray = new String[kgArray.size()];
        mStringArray = kgArray.toArray(mStringArray);
        Log.e("mStringArray","call"+mStringArray.toString());
        numberPickerWeight.setDisplayedValues(mStringArray);

        numberPickerWeight.setValue(index);


    }
    public void loadCmArray(){
        Log.e("cmcall", "call");

        numberPickerHeight.setDisplayedValues(null);
        Log.e("heightValueInCm",""+tempSelectedHeightVal);
        Log.e("heightValueInCmLength",""+ cmStringArray.size());
        Log.e("heightValueInCmArayLength",""+ cmStringArray.size());

        int index = cmStringArray.indexOf(tempSelectedHeightVal);
        Log.e("heightValueInCmArayIndex",""+ index);

        numberPickerHeight.setMinValue(0);
        numberPickerHeight.setMaxValue(cmStringArray.size()-1);

        String[] mStringArray = new String[cmStringArray.size()];
        mStringArray = cmStringArray.toArray(mStringArray);

        numberPickerHeight.setDisplayedValues(mStringArray);

        numberPickerHeight.setValue(index);

    }
    public void loadfeetArray(){

        Log.e("FeetValue",""+tempSelectedHeightVal);

        numberPickerHeight.setDisplayedValues(null);
        int index = feetEnglishArray.indexOf(tempSelectedHeightVal);

        numberPickerHeight.setMinValue(0);
        numberPickerHeight.setMaxValue(feetEnglishArray.size()-1);
        String[] mStringArray = new String[feetEnglishArray.size()];

        Log.e("FeetValueindex",""+index);

        mStringArray = feetLanguageArray.toArray(mStringArray);
        numberPickerHeight.setDisplayedValues(mStringArray);

        numberPickerHeight.setValue(index);

    }
    public void loadlbsArray(){

        numberPickerWeight.setDisplayedValues(null);
        Log.e("weightValueInFeet",""+tempSelectedweightVal);

        int index = lbsArray.indexOf(tempSelectedweightVal);

        numberPickerWeight.setMinValue(0);
        numberPickerWeight.setMaxValue(lbsArray.size()-1);

        String[] mStringArray = new String[lbsArray.size()];
        mStringArray = lbsArray.toArray(mStringArray);

        numberPickerWeight.setDisplayedValues(mStringArray);
        numberPickerWeight.setValue(index);


    }
    private  void  weightmeasurePicker(Dialog mod){

        ///mearsurePicker
        measurePickerWeight = (NumberPicker) mod.findViewById(R.id.number_pickertwo);
        measurePickerWeight.setWrapSelectorWheel(true);
        measurePickerWeight.setMaxValue(weightunitsLanguageArray.length-1);
        measurePickerWeight.setMinValue(0);
        measurePickerWeight.setDisplayedValues(weightunitsLanguageArray);
        Log.e("selectedweightMeasureUnits",""+selectedweightMeasureUnits);

        final List<String> objWeightUnitsList = Arrays.asList(weightunitsEnglishArray);
        int index = objWeightUnitsList.indexOf(selectedweightMeasureUnits);
        Log.e("index",""+index);
        measurePickerWeight.setValue(index);
        measurePickerWeight.setWrapSelectorWheel(true);
        measurePickerWeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {


                String newMeasureString = objWeightUnitsList.get(newVal);
                Log.e("SELECT_HEIGHT", "" + newMeasureString);

                if(!tempSelectedWeightUnits.equalsIgnoreCase(newMeasureString )) {
                    if (newMeasureString.equalsIgnoreCase(LanguagesKeys.KG_PICKER_KEY)) {
                        tempSelectedWeightUnits = newMeasureString;
                        tempSelectedweightVal = convertLbsToKg(tempSelectedweightVal);
                        Log.e("convertedCMValue",""+tempSelectedweightVal);
                        Log.e("convertedCMArray",""+ kgArray);
                        loadKgArray();

                    } else if(newMeasureString.equalsIgnoreCase(LanguagesKeys.LBS_PICKER_KEY)) {

                        tempSelectedWeightUnits = newMeasureString;
                        tempSelectedweightVal = convertKgToLbs(tempSelectedweightVal);
                        loadlbsArray();

                    }
                }
            }
        });

    }

    private void weightnumberPicker(Dialog mod){

        numberPickerWeight = (NumberPicker) mod.findViewById(R.id.number_picker);

        if(tempSelectedWeightUnits.equalsIgnoreCase(LanguagesKeys.KG_PICKER_KEY)) {
            Log.e("weightnumberPicker","call");
            loadKgArray();//kg array
        }
        else
        {
            loadlbsArray(); // lbs array
        }
        numberPickerWeight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                if(tempSelectedWeightUnits.equalsIgnoreCase(LanguagesKeys.KG_PICKER_KEY))
                {
                    tempSelectedweightVal = kgArray.get(newVal);
                }
                else
                {
                    tempSelectedweightVal = lbsArray.get(newVal);
                }

            }
        });
    }

    public String convertKgToLbs(String kgValue){
        int kg=Integer.parseInt(kgValue);
        int kgVal= (int) ((kg)*2.2046);
        String kgStr=String.valueOf(kgVal);
        return  kgStr;
    }
    public String convertLbsToKg(String lbsValue){
        Log.e("lbsValue","call"+lbsValue);
        int kg=Integer.parseInt(lbsValue);
        double kgVal=kg*0.453592;
        String kgStr=String.valueOf(Math.round(kgVal));
        Log.e("kgStr","call"+kgStr);
        return kgStr;

    }
    @Override
    public void onBackPressed()
    {    //when click on phone backbutton
        finish();

    }
    public void updateLanguageTexts() {

        nametext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NAME_KEY));

        emailtext.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PROFILE_EMAIL_KEY));

        birth.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PROFILE_BIRTHDATE_KEY));

        gender.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PROFILE_GENDER_KEY));

        height.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PROFILE_HEIGHT_KEY));

        weight.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PROFILE_WEIGHT_KEY));

        bloodgroup.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PROFILE_BLOOD_GROUP_KEY));

        savecontinue.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SAVE_AND_CONTINUE_KEY));

        female.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PROFILE_FEMALE_KEY));

        male.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PROFILE_MALE_KEY));
        }


}
