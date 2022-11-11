package com.example.wave.spectrumhuman.SideMenu;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;


import com.example.wave.spectrumhuman.DataBase.AddDoctorDataController;
import com.example.wave.spectrumhuman.DataBase.MemberDataController;
import com.example.wave.spectrumhuman.DataBase.UrineResultsDataController;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.HomeModule.SideMenuViewController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.LoginModule.Constants;
import com.example.wave.spectrumhuman.LoginModule.LocationTracker;
import com.example.wave.spectrumhuman.Models.DoctorInformationModel;
import com.example.wave.spectrumhuman.PushNotification.MyFirebaseMessagingService;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.AddDoctorServerObjects;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
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
 * Created by Rise on 18/12/2017.
 */
public class AddDoctorViewController extends AppCompatActivity {

    Toolbar toolbar;
    ImageView home;
    ImageView back;
    ImageView add;
    EditText editname, editemail, editphonenumber;
    Dialog dialog;
    TextView textaddress, name, email, phone, specilization, address, editaddress, editspecilization,txt_pickloc;
    Button save, picklocation, picklocation1,downArrow;
    DoctorInformationModel objDoctor;

    RelativeLayout rl_spinnerLayout,maplayout,rl_specialization;

    private static final int PLACE_PICKER_REQUEST = 1;
    String selectedLocationLatitude;
    String selectedLocationLongitude;
    String Notfoundstinrg = "";

    ArrayList<String> specilizationEnglishArray = new ArrayList<>();
    ArrayList<String> specilizationLanguageArray = new ArrayList<>();

    int selectedPosition = -1;
     CircleImageView img_user;
    TextView txt_status;
     ImageView img_status;
    RelativeLayout rl_doctorImg;

    RecyclerView resultRecyclerView;
    ResultsTableViewCell resultsTableViewCell;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddoctor);
        loadSpecilizatioArrayInEnglish();
        loadSpecilizatioArrayInLanguage();

        ButterKnife.bind(this);
        setToolbar();
        init();

        LocationTracker.getInstance().fillContext(getApplicationContext());
        LocationTracker.getInstance().startLocation();

        AddDoctorDataController.getInstance().fetchDoctorInfo();
        setAddDoctorData();
        updateLanguageTexts();

        rl_spinnerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(view,editname);
                resultRecyclerView.setVisibility(View.VISIBLE);
                loadSpecializationTableView();
            }
        });
        rl_specialization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideRecyclerView();
            }
        });

        editemail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.e("onClick","call");
                hideRecyclerView(editemail);

            }
        });
        editphonenumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.e("onClick","call");
                hideRecyclerView(editphonenumber);

            }
        });

        editname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.e("onClick","call");
                hideRecyclerView(editname);

            }
        });

    }
    public void hideSoftKeyboard(View v,EditText edtView)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        Log.e("hideSoftKeyboard", "call");
        imm.hideSoftInputFromWindow(edtView.getWindowToken(), 0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }
    private void init() {
        //  specilizationEnglishArray =new ArrayList<String>();

        save = (Button) findViewById(R.id.submit);
        name = (TextView) findViewById(R.id.textname);
        email = (TextView) findViewById(R.id.textemail);
        phone = (TextView) findViewById(R.id.textphone);
        specilization = (TextView) findViewById(R.id.textspecilization);
        address = (TextView) findViewById(R.id.textaddress);
        picklocation = (Button) findViewById(R.id.picklocation);
        picklocation1 = (Button) findViewById(R.id.picklocation1);

        editname = (EditText) findViewById(R.id.editname);
        editemail = (EditText) findViewById(R.id.contactusemail);
        editphonenumber = (EditText) findViewById(R.id.editphone);
        editaddress = (TextView) findViewById(R.id.edtaddress);
        editspecilization = (TextView) findViewById(R.id.editspecilization);
        txt_pickloc = (TextView) findViewById(R.id.pickloc);

        textaddress=(TextView)findViewById(R.id.textaddress);
         maplayout=(RelativeLayout)findViewById(R.id.addressbox);

        img_status=(ImageView)findViewById(R.id.status);
        img_user=(CircleImageView) findViewById(R.id.img_profile);
        txt_status=(TextView) findViewById(R.id.txt_status);
        downArrow=(Button)findViewById(R.id.spinner1);

         rl_doctorImg=(RelativeLayout)findViewById(R.id.rl_doctorImg) ;
        rl_spinnerLayout=(RelativeLayout)findViewById(R.id.rlfour);
        rl_specialization=(RelativeLayout)findViewById(R.id.rl_specialization);


        picklocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click", "getAddress");
                openLocationPickerActivity();
            }
        });
        picklocation1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("click1", "getAddress");
                openLocationPickerActivity();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SideMenuViewController.MessageEvent event) {
        Log.e("sidemenuMessageevent", "" + event.message);

        String resultData = event.message.trim();

        String[] keyArray = resultData.split(";");

        String keyString = keyArray[0];


        if(keyString.equals("refreshDoctorInfo"))
        {

            String message = keyArray[1];
            updateDoctorStatusUsingMessage(message);

        }

    }

    public void hideRecyclerView(EditText editText){
        if ( editText.isFocusable() )
        {
            Log.e("hideRecyclerView","call");
            downArrow.setBackgroundResource(R.drawable.ic_downarrow);
            hideRecyclerView();
        }
    }
    public void hideRecyclerView(){
        resultRecyclerView.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);
        downArrow.setBackgroundResource(R.drawable.ic_downarrow);
        textaddress.setVisibility(View.VISIBLE);
        maplayout.setVisibility(View.VISIBLE);
    }
    public void  loadSpecializationTableView(){
        if (rl_spinnerLayout.isClickable() ) {
            Log.e("rl_spinnerLayout", "call");
            save.setVisibility(View.GONE);
            textaddress.setVisibility(View.GONE);
            maplayout.setVisibility(View.GONE);
            downArrow.setBackgroundResource(R.drawable.ic_uparrow);
        }
    }
    /////////UPDATELANGUAGES///////////////
    public void updateLanguageTexts() {

        name.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NAME_KEY));
        address.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADDRESS_KEY));

        email.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.EMAIL_KEY));

        phone.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PHONE_KEY));

        specilization.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SPECIALIZATION_KEY));

        save.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SAVE_KEY));

        txt_pickloc.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PICK_LOCATION_KEY));

    }

    public void setAddDoctorData() {
        if (AddDoctorDataController.getInstance().currentDoctor != null)
        {
            objDoctor = AddDoctorDataController.getInstance().currentDoctor;
            txt_pickloc.setVisibility(View.GONE);
            picklocation.setVisibility(View.GONE);
            picklocation1.setVisibility(View.VISIBLE);

            rl_doctorImg.setVisibility(View.VISIBLE);

            Log.e("EditobjDoctor", "call" + objDoctor.getDoctorStatus());
            editname.setText(objDoctor.getName());
            editemail.setText(objDoctor.getEmail());
            editphonenumber.setText(objDoctor.getPhonenumber());
            editspecilization.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(objDoctor.getSpecilization()));
            editaddress.setText(objDoctor.getAddress());
            Notfoundstinrg = objDoctor.getFound();

            selectedPosition = specilizationEnglishArray.indexOf(objDoctor.getSpecilization());
            selectedLocationLatitude = objDoctor.getLatitude();
            selectedLocationLongitude = objDoctor.getLongitude();

            loadDoctorStatusData();

            rl_doctorImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (resultRecyclerView.isShown())
                {
                  hideRecyclerView();
                }
            }
        });

        } else {

            rl_doctorImg.setVisibility(View.GONE);
            Log.e("insertobjDoctor", "call");
            editname.setText("");
            editemail.setText("");
            editphonenumber.setText("");
            editspecilization.setText("");
            editaddress.setText("");
            picklocation.setVisibility(View.VISIBLE);
            picklocation1.setVisibility(View.GONE);
            isLocationHavingDoctor();

        }
    }
  public void loadDoctorStatusData()
  {

      Log.e("loadDoctorStatusData","call"+objDoctor.getDoctorStatus());

       img_user.setImageBitmap(convertByteArrayTOBitmap(MemberDataController.getInstance().currentMember.getMprofilepicturepath()));

      if (objDoctor.getDoctorStatus()!=null)
      {
         updateDoctorStatusUsingMessage(objDoctor.getDoctorStatus());
      }

  }

  public  void  updateDoctorStatusUsingMessage(String status){

      if (status.equals("accepted"))
      {
          img_status.setBackgroundResource(R.drawable.ic_accepted);
          txt_status.setText("Accepted");

      }else if(status.equals("rejected"))
      {
          img_status.setBackgroundResource(R.drawable.ic_rejected);
          txt_status.setText("Rejected");

      }else if(status.equals("pending"))
      {
          img_status.setBackgroundResource(R.drawable.ic_pending);
          txt_status.setText("Pending");
      }
  }


    public Bitmap convertByteArrayTOBitmap(byte[] profilePic){
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(profilePic);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }
    private void openLocationPickerActivity() {

        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(AddDoctorViewController.this), PLACE_PICKER_REQUEST);
            /*if (editaddress.getText().toString().contains(Constants.GooglePlacesTypes.dentist.toString())||editaddress.) {
            }*/
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String placename = String.format("%s", place.getName());

               selectedLocationLatitude = String.valueOf(place.getLatLng().latitude);
               selectedLocationLongitude = String.valueOf(place.getLatLng().longitude);
                String address = String.format("%s", place.getAddress());

                StringBuilder stBuilder = new StringBuilder();
                stBuilder.append(placename);
                stBuilder.append(address);

                stBuilder.append("\n");


                editaddress.setText(stBuilder.toString());
                picklocation.setVisibility(View.GONE);
                editaddress.setVisibility(View.VISIBLE);
                picklocation1.setVisibility(View.VISIBLE);
                txt_pickloc.setVisibility(View.GONE);
                isLocationHavingDoctor();
                Log.e("editaddress", "call" + editaddress.getText().toString());

            } else {
                //new AlertShowingDialog(AddDoctorViewController.this, "Failed to get location");
            }
        }
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        home = (ImageView) toolbar.findViewById(R.id.home);

        back = (ImageView) toolbar.findViewById(R.id.backimage);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        add = (ImageView) toolbar.findViewById(R.id.add);
        add.setVisibility(View.INVISIBLE);

        home.setImageResource(R.drawable.ic_home);
        home.setVisibility(View.INVISIBLE);

        TextView tool_txt = (TextView) toolbar.findViewById(R.id.tool_txt);
        tool_txt.append(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FAMILY_DOCTOR));


        resultRecyclerView =(RecyclerView)findViewById(R.id.rl_spinner);
        resultsTableViewCell = new ResultsTableViewCell(getApplicationContext(),specilizationLanguageArray);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this);
        resultRecyclerView.setLayoutManager(horizontalLayoutManager);
        resultRecyclerView.setAdapter(resultsTableViewCell);
        resultRecyclerView.setHasFixedSize(true);

    }

    @OnClick(R.id.submit)
    public void action() {

        validations();
    }


    public void validations() {

        if (editname.getText().toString().isEmpty()) {

            new AlertShowingDialog(AddDoctorViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_NAME_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

        } else if (editemail.getText().toString().trim().isEmpty()) {

            new AlertShowingDialog(AddDoctorViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_EMAIL_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));


        } else if (!isValidEmail(editemail.getText().toString().trim())) {

            new AlertShowingDialog(AddDoctorViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ENTER_VALID_EMAIL_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));


        } else if (editphonenumber.getText().toString().isEmpty()) {

            new AlertShowingDialog(AddDoctorViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_ENTER_PHONE_NUMBER_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));


        } else if (editspecilization.getText().toString().isEmpty()) {

            new AlertShowingDialog(AddDoctorViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_CHOOSE_SPECILIZATION_OF_DOCTOR_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));


        } else if (editaddress.getText().toString().isEmpty()) {

            new AlertShowingDialog(AddDoctorViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PLEASE_PICK_LOCATION_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

        } else {
            if (isConn()) {
                showRefreshDialogue();
                Log.e("found", "" + Notfoundstinrg);
                addDoctorData();
            } else {
               // new AlertShowingDialog(AddDoctorViewController.this, "No Connection");
                new AlertShowingDialog(AddDoctorViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

            }
        }

    }



    public void isLocationHavingDoctor() {

        if (editaddress.getText().toString().contains(Constants.GooglePlacesTypes.dentist.toString()) || editaddress.getText().toString().contains(Constants.GooglePlacesTypes.doctor.toString()) || editaddress.getText().toString().contains(Constants.GooglePlacesTypes.health.toString()) || editaddress.getText().toString().contains(Constants.GooglePlacesTypes.hospital.toString()) || editaddress.getText().toString().contains(Constants.GooglePlacesTypes.veterinary_care.toString()) || editaddress.getText().toString().contains(Constants.GooglePlacesTypes.pharmacy.toString())) {
            Notfoundstinrg = "Found";

        } else {
            Notfoundstinrg = "Notfound";
        }
    }

    public boolean isValidEmail(String target) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(target).matches();
    }


    ////////////////////////////RegisterAps/////////////////////////////////
    private void addDoctorData() {
        Log.e("callmethod", "call" + Notfoundstinrg);
        final AddDoctorServerObjects requestBody = new AddDoctorServerObjects();
        requestBody.setUserName(UserDataController.getInstance().currentUser.getUserName());
        Log.e("username", "" + UserDataController.getInstance().currentUser.getUserName());

        requestBody.setName(editname.getText().toString().trim());
        Log.e("doctorname", "" + editname.getText().toString().trim());

        requestBody.setEmail(editemail.getText().toString().trim());
        Log.e("doctoremail", "" + editemail.getText().toString().trim());

        requestBody.setPhone(editphonenumber.getText().toString().trim());
        Log.e("phone", "" + editphonenumber.getText().toString().trim());

        String specilizationText = specilizationEnglishArray.get(selectedPosition);
        requestBody.setSpecalization(specilizationText);
        Log.e("spe", "" + specilizationText);

        requestBody.setAddress(editaddress.getText().toString().trim());
        Log.e("addre", "" + editaddress.getText().toString().trim());


        if(selectedLocationLatitude == null || selectedLocationLongitude == null)
        {
            selectedLocationLatitude = String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude());
            selectedLocationLongitude = String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude());

        }
        Log.e("selectedLatitude", "call" + selectedLocationLatitude);
        Log.e("selectedLatitude", "call" + selectedLocationLongitude);

        requestBody.setLatitude(selectedLocationLatitude);
        requestBody.setLongitude(selectedLocationLongitude);
        requestBody.setAddedtime(getCurrentTime());
        requestBody.setFound(Notfoundstinrg);
        Log.e("addrefound", "" + Notfoundstinrg);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);

        Call<AddDoctorServerObjects> callable;

        if (objDoctor != null) {
            Log.e("update", "call" + requestBody.getFound());
            requestBody.setDoctor_id(objDoctor.getDoctorid());
            callable = api.editDoctorData(requestBody);
        } else {
            callable = api.addDoctorData(requestBody);
        }
        //Call<AddDoctorServerObjects> callable = api.addDoctorData(requestBody);
        callable.enqueue(new Callback<AddDoctorServerObjects>() {
            @Override
            public void onResponse(Call<AddDoctorServerObjects> call, Response<AddDoctorServerObjects> response) {
                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();
                Log.e("codefor3", "call" + statusCode);
                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {

                        String doctor_id = response.body().getDoctor_id();
                        Log.e("doctor_id", "call" + doctor_id);
                        if (objDoctor != null) {
                            Log.e("objDoctorgFound", "call" + objDoctor.getDoctorStatus());
                            Log.e("objDoctorgFound", "call" + objDoctor.getDoctorid());
                            AddDoctorDataController.getInstance().updateAddDoctorInformation(
                                    objDoctor.getAddedtime(), editaddress.getText().toString(),
                                    objDoctor.getDoctorid(), editemail.getText().toString(),
                                    Notfoundstinrg, selectedLocationLatitude,
                                    selectedLocationLongitude, editname.getText().toString(),
                                    editphonenumber.getText().toString(),
                                    editspecilization.getText().toString(),objDoctor.getDoctorStatus());
                        } else {
                            AddDoctorDataController.getInstance().insertDoctorInformation(
                                    requestBody.getAddedtime(), requestBody.getAddress(),
                                    doctor_id, requestBody.getEmail(), requestBody.getFound(), requestBody.getLatitude(),
                                    requestBody.getLongitude(), requestBody.getName(),
                                    requestBody.getPhone(), requestBody.getSpecalization(),
                                    "pending");

                        }

                        finish();
                    } else if (statusCode.equals("2"))
                    {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else if (statusCode.equals("0"))
                    {

                    }
                }

                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<AddDoctorServerObjects> call, Throwable t) {

                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();
                failurealert();
            }
        });

    }
    public void failurealert() {

        Log.e("responsealert", "call");
     final Dialog   failurealert = new Dialog(this);
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
                    addDoctorData();

                }else {
                    failurealert.dismiss();
                    new AlertShowingDialog(AddDoctorViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }


        });


    }
    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }

    public String getCurrentTime() {

        long unixTime;
        unixTime = System.currentTimeMillis() / 1L;
        String attempt_time = String.valueOf(System.currentTimeMillis() / 1L);

        Log.e("attem", "" + attempt_time);
        return attempt_time;

    }

    public void loadSpecilizatioArrayInEnglish() {

        specilizationEnglishArray = new ArrayList<String>();
        specilizationEnglishArray.add(LanguagesKeys.ALLERGERSTIC_KEY);
        specilizationEnglishArray.add(LanguagesKeys.ANESTHE_SIOLOGIC_STIC_KEY);
        specilizationEnglishArray.add(LanguagesKeys.ANDROLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.CARDIO_LOGISTIC_KEY);
        specilizationEnglishArray.add(LanguagesKeys.CARDIAC_ELECTROCYCOLOGISTIC_KEY);
        specilizationEnglishArray.add(LanguagesKeys.DERMOTOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.EMERGENCY_MEDICINE_DOCTORS_KEY);
        specilizationEnglishArray.add(LanguagesKeys.ENDOCRINOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.EPIDO_MIALOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.FAMILY_MEDICIAN_PHYSICIAN_KEY);
        specilizationEnglishArray.add(LanguagesKeys.GASTROENTEROLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.GERIASTERIAN_KEY);
        specilizationEnglishArray.add(LanguagesKeys.HYPER_BARIC_PHYSICIAN_KEY);
        specilizationEnglishArray.add(LanguagesKeys.HEMATOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.HEPOTOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.IMMUNOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.INFECTIVE_DIECESASE_SPECIALIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.INTENSIVIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.INTERNEL_MEDICIEN_SPECIALIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.MAXILLO_FACIAL_SURGERION_KEY);
        specilizationEnglishArray.add(LanguagesKeys.MEDICAL_GENESTIC_KEY);
        specilizationEnglishArray.add(LanguagesKeys.NENOTOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.NEPHROLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.NEURO_LOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.NEURO_SURGERION_KEY);
        specilizationEnglishArray.add(LanguagesKeys.NECLEOUR_MEDICINE_SPECIALIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.OBESTERIAN_GYNACOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.OCCUPATIONAL_MEDICIAN_SPECIALIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.ONOCOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.OPTHOMOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.ORTHOPEDIC_SURGERIAN_KEY);
        specilizationEnglishArray.add(LanguagesKeys.OTO_LORINOGOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.PARACITOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.PHOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.PERINOTOLOSTIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.PERIDONTIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.PEDIATRACIAN_KEY);
        specilizationEnglishArray.add(LanguagesKeys.PLASTIC_SURGERION_KEY);
        specilizationEnglishArray.add(LanguagesKeys.PSYCHIARIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.PULMONOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.REDIOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.RHEMPTHOMOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.SLEEP_DOCTOR_KEY);
        specilizationEnglishArray.add(LanguagesKeys.SPINAL_CORD_INGUIRY_SPECIALIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.SPORTS_MEDICAL_SPECIALIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.SURGERION_KEY);
        specilizationEnglishArray.add(LanguagesKeys.THORASIC_SURGIRION_KEY);
        specilizationEnglishArray.add(LanguagesKeys.UROLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.VASUCULAR_SURGRION_KEY);
        specilizationEnglishArray.add(LanguagesKeys.AUDIOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.VETERIANARIAN_KEY);
        specilizationEnglishArray.add(LanguagesKeys.CHIROPRACTOR_KEY);
        specilizationEnglishArray.add(LanguagesKeys.DIAGNOSTACIAN_KEY);
        specilizationEnglishArray.add(LanguagesKeys.MICRO_BIOLOGIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.PALLATIAVE_CARE_SPECIALIST);
        specilizationEnglishArray.add(LanguagesKeys.PHARMACIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.PHYCOTHERIOPIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.PODIOTRIST_KEY);
        specilizationEnglishArray.add(LanguagesKeys.OTHERS_KEY);
    }
    public void loadSpecilizatioArrayInLanguage() {
        specilizationLanguageArray = new ArrayList<String>();
        for (String key:specilizationEnglishArray)
        {
            specilizationLanguageArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(key));

        }
    }
    ImageView imageView;
    Handler handler = new Handler();

    public void showRefreshDialogue() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_animate);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        //dialog.create();
        dialog.show();
        TextView textView=(TextView)dialog.findViewById(R.id.connecting);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        imageView = (ImageView) dialog.findViewById(R.id.image_rottate);
        handler.postDelayed(updateTimerThread, 100);
    }

    public Runnable updateTimerThread = new Runnable() {
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

    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
    // Step 1:-
    public class ResultsTableViewCell extends RecyclerView.Adapter<ResultsTableViewCell.ViewHolder> {

        // step 3:-
        Context ctx;
        ArrayList<String> arrayList=new ArrayList<>();

        public ResultsTableViewCell(Context ctx,ArrayList<String> stringArrayList) {
            this.ctx = ctx;
            this.arrayList=stringArrayList;

        }
        // step 5:-
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_textview, parent, false);
            ViewHolder contactViewHolder = new ViewHolder(itemView, ctx,arrayList);

            return contactViewHolder;
        }
        //step 6:-
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            Log.e("onBindViewHolder", "called");

            holder.txt_specialization.setText(specilizationLanguageArray.get(position));

            if(selectedPosition == position)
            {
                editspecilization.setText(specilizationLanguageArray.get(selectedPosition));
                Log.e("pos", "" + selectedPosition);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(selectedPosition != position) {
                        selectedPosition = position;
                        Log.e("position", "" + specilizationLanguageArray.get(selectedPosition));
                        editspecilization.setText(""+specilizationLanguageArray.get(selectedPosition).toString());
                        hideRecyclerView();
                    }
                    else
                    {
                        selectedPosition = -1;
                    }
                    notifyDataSetChanged();

                }
            });
        }
        // step 4:-
        @Override
        public int getItemCount() {
            return specilizationLanguageArray.size();

        }
        // Step 2:-
        public class ViewHolder extends RecyclerView.ViewHolder {
            Context ctx;
            TextView txt_specialization;
            ArrayList<String> arrayList1 = new ArrayList<String>();

            public ViewHolder(View itemView, Context ctx,ArrayList<String> arrayList) {
                super(itemView);
                this.ctx = ctx;
                this.arrayList1=arrayList;

                txt_specialization=(TextView)itemView.findViewById(R.id.textView1);
                ///
            }
        }
    }
}
