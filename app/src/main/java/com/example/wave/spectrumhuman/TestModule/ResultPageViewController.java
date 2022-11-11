package com.example.wave.spectrumhuman.TestModule;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;
import com.example.wave.spectrumhuman.Alert.RefreshShowingDialog;
import com.example.wave.spectrumhuman.DataBase.AddDoctorDataController;
import com.example.wave.spectrumhuman.DataBase.MemberDataController;
import com.example.wave.spectrumhuman.DataBase.UrineResultsDataController;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.Graphs.UrineTestDataCreatorController;
import com.example.wave.spectrumhuman.HomeModule.HomeActivityViewController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.Models.UrineresultsModel;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.AddDoctorServerObjects;
import com.example.wave.spectrumhuman.ServerObjects.UrineResultsServerObject;
import com.example.wave.spectrumhuman.SideMenu.AddDeviceViewController;
import com.example.wave.spectrumhuman.SideMenu.AddDoctorViewController;
import com.example.wave.spectrumhuman.SideMenu.ContactUsViewController;
import com.example.wave.spectrumhuman.SideMenu.LanguageViewController;
import com.example.wave.spectrumhuman.SideMenu.MyDeviceViewController;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static java.security.AccessController.getContext;

/**
 * Created by WAVE on 9/29/2017.
 */
public class ResultPageViewController extends AppCompatActivity {
    Button btn_calender, btn_next, btn_previous;
    Toolbar toolbar;
    ImageView back, img_share, deleteButton, img_doctor;
    TextView tool_text, txt_currenrDate, txt_month, txt_name, txt_relation, txt_nodata;
    public int selectedPosition = -1;
    ArrayList<String> testNameArray;
    RecyclerView resultRecyclerView;
    ResultsTableViewCell resultsTableViewCell;
    public static Calendar resultCalendar;
    SimpleDateFormat simpleDateFormat, simpleMonthFormat, dateFormat;
    String currentDate;
    private static final int REQUEST_WRITE_PERMISSION = 56;
    boolean isDataAvailable = false;
    UrineresultsModel selectedUrineTestRecord;
    private int selectedRecordPosition = 0;
    /*@BindView(R.id.rlScrollMeg)
    RelativeLayout rlScrollMeg;*/
    RelativeLayout rl_home, rl_nodata, rl_recycler, relativeshare;
    CircleImageView circleImageView;
    RefreshShowingDialog alertDilogue;
    Dialog failurealert;

    Boolean isShare = false;

    boolean isFromDelete=false;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultpage);
        ButterKnife.bind(this);
        resultCalendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("EEEE,dd", Locale.ENGLISH); //for get Monday ,sunday and dd for date
        simpleMonthFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);//for get,month year
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        alertDilogue = new RefreshShowingDialog(ResultPageViewController.this);
        Log.e("onceate", "call");
        //set current member testNameArray and reelation to textviws

        deleteButton = (ImageView) findViewById(R.id.btn_delete);
        txt_name = (TextView) findViewById(R.id.name);
        txt_relation = (TextView) findViewById(R.id.relation);
        btn_calender = (Button) findViewById(R.id.calender);

        circleImageView = (CircleImageView) findViewById(R.id.img_profile);
        txt_name.setText(MemberDataController.getInstance().currentMember.getMname());
        txt_relation.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(MemberDataController.getInstance().currentMember.getMrelationshipname()));
        circleImageView.setImageBitmap(convertByteArrayTOBitmap(MemberDataController.getInstance().currentMember.getMprofilepicturepath()));
        //
        rl_nodata = (RelativeLayout) findViewById(R.id.rl_nodata);
        txt_nodata = (TextView) findViewById(R.id.txt_nodata);

        rl_recycler = (RelativeLayout) findViewById(R.id.rl_recycler);
        //
        UrineResultsDataController.getInstance().fetchAllUrineResults();
        MemberDataController.getInstance().fetchMemberData();
        UserDataController.getInstance().fetchUserData();
        Log.e("fetchUserData", "call" + UserDataController.getInstance().currentUser.getUserName());
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_previous = (Button) findViewById(R.id.btn_previous);
        Log.e("sizecall", "call" + UrineResultsDataController.getInstance().allUrineResults.size());
        if (UrineResultsDataController.getInstance().allUrineResults.size() == 1) {
            Log.e("zero", "called");
            btn_next.setVisibility(View.GONE);
            btn_previous.setVisibility(View.GONE);
        } else {
            Log.e("zeroelse", "called");
            btn_next.setVisibility(View.VISIBLE);
            btn_previous.setVisibility(View.VISIBLE);
        }
        AddDoctorDataController.getInstance().fetchDoctorInfo();

        setToolbar();
        setResultRecyclerViewData();

        respondToSwipeGesture();
        if (HomeActivityViewController.isFromHome) {
            Log.e("isFromHome", "call" + HomeActivityViewController.isFromHome);
            selectedRecordPosition = HomeActivityViewController.selectedPosition;
            Log.e("selectedRecordPosition", "" + selectedRecordPosition);
        } else {
            Log.e("isFromHomefalse", "call");
            selectedRecordPosition = UrineResultsDataController.getInstance().allUrineResults.size() - 1;

        }
        //////
        if (UrineResultsDataController.getInstance().allUrineResults.size() > 0) {
            loadDataForUrineObject(UrineResultsDataController.getInstance().allUrineResults.get(selectedRecordPosition));
        } else {
            Log.e("donthaveUrineResults", "call");
            rl_recycler.setVisibility(GONE);
            rl_nodata.setVisibility(View.VISIBLE);
            //txt_nodata.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_Da));
            loadDatForDate(resultCalendar);

        }
    }
    public void loadLanguageTextForDateTexts(String date, String month) {
        //For date Lable
        currentDate = date;
        String[] stringArray = currentDate.split(" ");
        Log.e("existeddayName", "call" + stringArray);
        String existeddayName = stringArray[0].trim();
        Log.e("existeddayName", "call" + existeddayName);//ThursDay,14
        /*String existedMonthName=stringArray[3].trim();//for am pm
        Log.e("existedMonthName","call"+existedMonthName);//
*/
        String[] existedDayName = existeddayName.split(",");
        currentDate = currentDate.replace(existedDayName[0], LanguageTextController.getInstance().currentLanguageDictionary.get(existedDayName[0]));
        ///for month label
        String existedMonthName = month;//for am pm
        Log.e("existedMonthName", "call" + existedMonthName);//
        String[] existedMonthArray = existedMonthName.split(" ");
        String monthName = existedMonthArray[0].replace(existedMonthArray[0], LanguageTextController.getInstance().currentLanguageDictionary.get(existedMonthArray[0]) + " " + existedMonthArray[1]);
        //
        txt_currenrDate.setText(currentDate);
        txt_month.setText(monthName);

    }

    public void setResultRecyclerViewData() {
        testNameArray = new ArrayList<String>();

        testNameArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OCCULT_BLOOD_RBC_KEY));
        testNameArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.BILIRUBIN_VALUE_KEY));
        testNameArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.UROBILIOGEN_KEY));
        testNameArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.KETONES_KEY));
        testNameArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PROTEIN_KEY));
        testNameArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NITRATE_KEY));
        testNameArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.GLUCOSE_KEY));
        testNameArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PH_KEY));
        testNameArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SG_KEY));
        testNameArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LEUKOCYTE_KEY));
        //
        resultRecyclerView = (RecyclerView) findViewById(R.id.result_recycler);
        resultRecyclerView.setNestedScrollingEnabled(false);
        resultsTableViewCell = new ResultsTableViewCell(testNameArray, getApplication());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        resultRecyclerView.setLayoutManager(horizontalLayoutManager);
        resultRecyclerView.setAdapter(resultsTableViewCell);
        resultRecyclerView.setMotionEventSplittingEnabled(false);
        resultsTableViewCell.notifyDataSetChanged();
        resultsTableViewCell.notifyItemChanged(selectedPosition);
        txt_currenrDate = (TextView) findViewById(R.id.txt_currentDate);
        txt_month = (TextView) findViewById(R.id.txt_month);
        requestPermissions();
    }

    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back = (ImageView) findViewById(R.id.toolbar_icon);//Spectrum
        back.setBackgroundResource(R.drawable.ic_home);
        tool_text = (TextView) toolbar.findViewById(R.id.toolbar_text);
        tool_text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.TEST_RESULTS_TILE_KEY));
        img_share = (ImageView) toolbar.findViewById(R.id.img_share);
        img_doctor = (ImageView) toolbar.findViewById(R.id.img_doctor);
        img_doctor.setBackgroundResource(R.drawable.ic_doctor);
        relativeshare = (RelativeLayout) toolbar.findViewById(R.id.relativeshare);
        relativeshare.setOnClickListener(mShareListener);
        img_share.setBackgroundResource(R.drawable.ic_share);
        // img_share.setOnClickListener(mShareListener);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
            }
        });
        rl_home = (RelativeLayout) findViewById(R.id.rootView);

        img_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AddDoctorDataController.getInstance().currentDoctor != null) {
                    Log.e("currentDoctor", "call" + AddDoctorDataController.getInstance().currentDoctor.getAddress());
                    checkDoctorsData();
                } else {
                    Log.e("currentDoctor", "call");
                    addDoctorAlert();
                }
            }
        });

    }

    public void checkDoctorsData() {
        final Dialog dialog = new Dialog(ResultPageViewController.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.adddoctor_alert);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.create();
        dialog.show();
        TextView txt_title = (TextView) dialog.findViewById(R.id.text_alert);
        txt_title.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ALERT_KEY));

        TextView text1 = (TextView) dialog.findViewById(R.id.text_reminder);
        //text1.setText(R.string.sharedoctor);
        text1.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DO_YOU_WANT_TO_SHARE_REPORT_DOCTOR_KEY));


        Button yes = (Button) dialog.findViewById(R.id.btn_ok);
        Button no = (Button) dialog.findViewById(R.id.btn_cancle);
        yes.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.YES_KEY));
        no.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_KEY));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);


        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                alertDilogue.showAlert();
                shareDoctorInfoToServer();
            }
        });
    }
    public void addDoctorAlert() {
        final Dialog dialog = new Dialog(ResultPageViewController.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmdoctor_alert);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.create();
        dialog.show();

        TextView txt_title = (TextView) dialog.findViewById(R.id.text_alert);
        txt_title.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ALERT_KEY));

        TextView text1 = (TextView) dialog.findViewById(R.id.text_reminder1);
        text1.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.THERE_IS_NO_DOCTOR_ADDED_YET_KEY));
        //TextView text2 = (TextView)dialog.findViewById(R.id.text_reminder2);
        //text2.setText(R.string.nodoctor1);

        Button yes = (Button) dialog.findViewById(R.id.btn_ok);
        Button no = (Button) dialog.findViewById(R.id.btn_cancle);
        yes.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.YES_KEY));
        no.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_KEY));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                startActivity(new Intent(getApplicationContext(), AddDoctorViewController.class));
                finish();
            }
        });
    }

    public void shareDoctorInfoToServer() {

        Log.e("shareDoctorInfoToServer", "call");

        UrineresultsModel urineresultsModel = new UrineresultsModel();
        AddDoctorServerObjects doctorserverObject = new AddDoctorServerObjects();
        urineresultsModel = UrineResultsDataController.getInstance().allUrineResults.get(selectedRecordPosition);

        doctorserverObject.setUserName(UserDataController.getInstance().currentUser.getUserName());
        doctorserverObject.setTestId(urineresultsModel.getTest_id());
        doctorserverObject.setMember_id(urineresultsModel.getMember_Id());


        Log.e("setUserName", "call" + doctorserverObject.getUserName());
        Log.e("setUserName", "call" + doctorserverObject.getTestId());
        Log.e("setUserName", "call" + doctorserverObject.getMember_id());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<AddDoctorServerObjects> callable = api.share(doctorserverObject);
        callable.enqueue(new Callback<AddDoctorServerObjects>() {
            @Override
            public void onResponse(Call<AddDoctorServerObjects> call, Response<AddDoctorServerObjects> response) {
                alertDilogue.hideRefreshDialog();
                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();
                Log.e("statusCode", "call" + statusCode);
                if (statusCode.equals("3")) {
                    Toast.makeText(getApplicationContext(), LanguageTextController.getInstance().currentLanguageDictionary.get(message), Toast.LENGTH_SHORT).show();
                } else if (statusCode.equals("2")) {
                    Toast.makeText(getApplicationContext(), LanguageTextController.getInstance().currentLanguageDictionary.get(message), Toast.LENGTH_SHORT).show();
                } else if (statusCode.equals("1")) {
                    Toast.makeText(getApplicationContext(), LanguageTextController.getInstance().currentLanguageDictionary.get(message), Toast.LENGTH_SHORT).show();
                } else if (statusCode.equals("0")) {
                    Toast.makeText(getApplicationContext(), LanguageTextController.getInstance().currentLanguageDictionary.get(message), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddDoctorServerObjects> call, Throwable t) {

                alertDilogue.hideRefreshDialog();
                alertDilogue.dismiss();
                failurealert();



            }
        });

    }



    public void failurealert() {

        Log.e("responsealert", "call");
        failurealert = new Dialog(this);
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
                    alertDilogue.showAlert();

                    if (isFromDelete)
                    {
                        deleteUrinedataFromServer();
                    }else {
                        shareDoctorInfoToServer();

                    }

                }else {
                    failurealert.dismiss();
                    new AlertShowingDialog(ResultPageViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }


        });


    }





    @OnClick(R.id.btn_next)
    public void nextButtonAction() {

        if (selectedRecordPosition < UrineResultsDataController.getInstance().allUrineResults.size() - 1) {
            selectedRecordPosition = selectedRecordPosition + 1;
            Log.e("nextselected", "" + selectedRecordPosition);
            loadDataForUrineObject(UrineResultsDataController.getInstance().allUrineResults.get(selectedRecordPosition));
            Log.e("arraysizee", "call" + UrineResultsDataController.getInstance().allUrineResults.size());
            loadAnimatonToRecyclerView();
        }

    }

    @OnClick(R.id.btn_previous)
    public void previousButtonAction() {

        if (selectedRecordPosition > 0) {
            selectedRecordPosition = selectedRecordPosition - 1;
            Log.e("previousselectedRecordP", "" + selectedRecordPosition);
            loadDataForUrineObject(UrineResultsDataController.getInstance().allUrineResults.get(selectedRecordPosition));
            Log.e("arraysizee", "call" + UrineResultsDataController.getInstance().allUrineResults.size());
            loadAnimatonToRecyclerView();
        }

    }

    @OnClick(R.id.btn_delete)
    public void deleteUrineResult() {
        final Dialog dialog = new Dialog(ResultPageViewController.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_deleteresult_alert);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(R.id.txt_message);
        TextView txt_title = (TextView) dialog.findViewById(R.id.text_reminder);

        txt_title.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DELETE_TEST_RESULTS_KEY));
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DO_YOU_WANT_TO_DELETE_TEST_CONDUCTED_AT_KEY) + "\n"
                + txt_currenrDate.getText().toString());
        Button no = (Button) dialog.findViewById(R.id.btn_cancle);
        Button yes = (Button) dialog.findViewById(R.id.btn_ok);
        yes.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DELETE_KEY));
        no.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                if (isConn()){
                    alertDilogue.showAlert();
                    deleteUrinedataFromServer();

                }else {
                    new AlertShowingDialog(getApplicationContext(), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                }

            }
        });
    }

    public void deleteUrinedataFromServer() {
         Log.e("deleteUrinedataFromServer","call");
        UrineresultsModel urineresultsModel = new UrineresultsModel();
        UrineResultsServerObject serverObject = new UrineResultsServerObject();
        urineresultsModel = UrineResultsDataController.getInstance().allUrineResults.get(selectedRecordPosition);

        serverObject.setMember_id(urineresultsModel.getMember_Id());
        serverObject.setMail(UserDataController.getInstance().currentUser.getUserName());
        serverObject.setTest_id(urineresultsModel.getTest_id());
        Log.e("id", "" + serverObject.getMember_id());
        Log.e("userid", "" + UserDataController.getInstance().currentUser.getUserName());
        Log.e("id", "" + serverObject.getMail());
        Log.e("id", "" + serverObject.getTest_id());

        isFromDelete=true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<UrineResultsServerObject> callable = api.deleteurineData(serverObject);
        callable.enqueue(new Callback<UrineResultsServerObject>() {
            @Override
            public void onResponse(Call<UrineResultsServerObject> call, Response<UrineResultsServerObject> response) {

                alertDilogue.hideRefreshDialog();
                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();

                if (statusCode.equals(null)) {
                } else {
                    if (statusCode.equals("3")) {
                        deleteUrineRecordDataFromDB();
                        Log.e("deleteUrin","call"+message);
                        isFromDelete=false;
                    } else if (statusCode.equals("0")) {

                    }
                }
            }

            @Override
            public void onFailure(Call<UrineResultsServerObject> call, Throwable t) {
                alertDilogue.hideRefreshDialog();
                failurealert();
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




    public void deleteUrineRecordDataFromDB() {
        if (UrineResultsDataController.getInstance().deleteurineresultsData(selectedUrineTestRecord)) {
            if (UrineResultsDataController.getInstance().allUrineResults.size() < 2) {
                Log.e("zero", "called");
                btn_next.setVisibility(View.GONE);
                btn_previous.setVisibility(View.GONE);
            } else {
                Log.e("zeroelse", "called");
                btn_next.setVisibility(View.VISIBLE);
                btn_previous.setVisibility(View.VISIBLE);
            }
            if (UrineResultsDataController.getInstance().allUrineResults.size() > 0) {

                if (selectedRecordPosition == 0) {
                    Log.e("selectedRecord00", "call");
                    nextButtonAction();
                } else if (selectedRecordPosition == UrineResultsDataController.getInstance().allUrineResults.size()) {
                    previousButtonAction();
                } else {
                    previousButtonAction();
                }
            } else {
                txt_currenrDate.setText("");
                txt_month.setText("");
                rl_nodata.setVisibility(View.VISIBLE);
                img_share.setVisibility(View.GONE);
                rl_recycler.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(GONE);
                btn_calender.setVisibility(GONE);
                resultsTableViewCell.notifyDataSetChanged();

            }
        }
    }

    @OnClick(R.id.calender)
    public void openCalender() {
        Intent i = new Intent(this, MainActivity.class);
        startActivityForResult(i, 1);
    }

    public void loadDatForDate(Calendar objCalender) {
        selectedRecordPosition = getPositionForDate(dateFormat.format(resultCalendar.getTime()));
        loadDataForUrineObject(UrineResultsDataController.getInstance().allUrineResults.get(selectedRecordPosition));

    }

    public int getPositionForDate(String dateString) {
        for (int i = 0; i < UrineResultsDataController.getInstance().allUrineResults.size(); i++) {
            UrineresultsModel objUrineRecord = UrineResultsDataController.getInstance().allUrineResults.get(i);
            long objTimeStamp = Long.parseLong(objUrineRecord.getTestedTime());
            Date formattedDate = new Date(objTimeStamp * 1000);
            String nowTestDateString = dateFormat.format(formattedDate);
            if (dateString.equals(nowTestDateString)) {
                return UrineResultsDataController.getInstance().allUrineResults.indexOf(objUrineRecord);
            }
            currentDate = simpleMonthFormat.format(resultCalendar.getTime());
            Log.e("currentDate", "call" + currentDate);
        }
        return UrineResultsDataController.getInstance().allUrineResults.size() - 1;
    }

    public void loadDataForUrineObject(UrineresultsModel objUrineResult) {
        selectedUrineTestRecord = objUrineResult;
        try {
            String[] dateArray = UrineTestDataCreatorController.getInstance().convertTimestampToDate(selectedUrineTestRecord.getTestedTime()).split(";");
            loadLanguageTextForDateTexts(dateArray[0], dateArray[1]);
            isDataAvailable = UrineTestDataCreatorController.getInstance().getIsDataAvailable(selectedUrineTestRecord);
            Log.e("isDataAvailable", "" + isDataAvailable);
            resultsTableViewCell.notifyDataSetChanged();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Step 1:-
    public class ResultsTableViewCell extends RecyclerView.Adapter<ResultsTableViewCell.ViewHolder> {
        // step 3:-
        ArrayList<String> arrayList = new ArrayList<>();
        Context ctx;

        public ResultsTableViewCell(ArrayList<String> arrayList, Context ctx) {
            this.ctx = ctx;
            this.arrayList = arrayList;
        }

        // step 5:-
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_result_items, parent, false);
            ButterKnife.bind(itemView);
            ViewHolder contactViewHolder = new ViewHolder(itemView, ctx, arrayList);
            return contactViewHolder;
        }

        //step 6:-
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.testName.setText(testNameArray.get(position));
            if (selectedPosition == position) {
                holder.rlScrollMeg.setVisibility(View.VISIBLE);
                holder.down.setBackgroundResource(R.drawable.ic_uparrow);
            } else {
                holder.rlScrollMeg.setVisibility(View.GONE);
                holder.down.setBackgroundResource(R.drawable.ic_downarrow);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (selectedPosition != position) {
                        selectedPosition = position;
                    } else {
                        selectedPosition = -1;
                    }
                    notifyDataSetChanged();

                }

            });


            if (position == 0) {
                Log.e("pos0","" + position);
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getRbcText(selectedUrineTestRecord.getRbcValue()));
                String s = holder.testValue.getText().toString();
                loadTestConditionLable(holder, s);
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                holder.txt_discription = (TextView) holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OCCULT_BLOOD_DESCRIPTION_RESULT_KEY));
                Log.e("txt_discription","" + holder.txt_discription);

            } else if (position == 1) {
                Log.e("pos1","" + position);

                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getBillirubinText(selectedUrineTestRecord.getBillirubinValue()));
                String s = holder.testValue.getText().toString();
                loadTestConditionLable(holder, s);
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                holder.txt_discription = (TextView) holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.BILLIBURIN_DESCRIPTION_RESULT_KEY));

                // holder.txt_bg.setBackgroundColor(changeLableColor(s));


            } else if (position == 2) {
                Log.e("pos2","" + position);

                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getUroboliogenText(selectedUrineTestRecord.getUroboliogenValue()));
                String s = holder.testValue.getText().toString();
                loadTestConditionLable(holder, s);
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                holder.txt_discription = (TextView) holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.UROBILLINGEN_DESCRIPTION_RESULT_KEY));


            } else if (position == 3) {
                Log.e("pos3","" + position);
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getKetonesText(selectedUrineTestRecord.getKetonesValue()));
                String s = holder.
                        testValue.getText().toString();
                loadTestConditionLable(holder, s);
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                holder.txt_discription = (TextView) holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.KETONS_DESCRIPTION_KEY));
            } else if (position == 4) {
                Log.e("pos4","" + position);
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getProteinText(selectedUrineTestRecord.getProteinValue()));
                String s = holder.testValue.getText().toString();
                loadTestConditionLable(holder, s);
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                holder.txt_discription = (TextView) holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PROTINE_DESCRIPTION_KEY));


            } else if (position == 5) {
                Log.e("pos5","" + position);
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getNitriteText(selectedUrineTestRecord.getNitriteValue()));
                String s = holder.testValue.getText().toString();
                loadTestConditionLable(holder, s);
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                holder.txt_discription = (TextView) holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NITRATE_DISCRIPTION_KEY));


            } else if (position == 6) {
                Log.e("pos6","" + position);
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getGlucoseText(selectedUrineTestRecord.getGlucoseValue()));
                String s = holder.testValue.getText().toString();
                loadTestConditionLable(holder, s);
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));

                holder.txt_discription = (TextView) holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.GLOUCOSE_DESCRIPTION_KEY));


            } else if (position == 7) {
                Log.e("pos7","" + position);
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getPhText(selectedUrineTestRecord.getPhValue()));
                String s = holder.testValue.getText().toString();
                loadTestConditionLable(holder, s);
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_discription = (TextView) holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PH_DISCRIPTION_KEY));


            } else if (position == 8) {
                Log.e("pos8","" + position);
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getSgText(selectedUrineTestRecord.getSgValue()));
                String s = holder.testValue.getText().toString();
                loadTestConditionLable(holder, s);
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                holder.txt_discription = (TextView) holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SG_DESCRIPTION_KEY));


            } else if (position == 9) {
                Log.e("pos9","" + position);
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getLeukocyteText(selectedUrineTestRecord.getLeucocyteValue()));
                String s = holder.testValue.getText().toString();
                loadTestConditionLable(holder, s);
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                holder.txt_discription = (TextView) holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LECHOCYTE_DISCRIPTION_KEY));
            }
        }

        // step 4:-
        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        // Step 2:-
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView testName, testValue, testCondition, txt_discription;
            ArrayList<String> arrayList = new ArrayList<String>();
            Context ctx;
            ImageView imageView;
            //ScrollView msg;
            Button down;
            TextView txt_bg, txt_bg1;
            RelativeLayout rlScrollMeg;

            public ViewHolder(View itemView, Context ctx, final ArrayList<String> arrayList) {
                super(itemView);
                this.arrayList = arrayList;
                this.ctx = ctx;
                testName = (TextView) itemView.findViewById(R.id.testName);
                testValue = (TextView) itemView.findViewById(R.id.testVal);
                testCondition = (TextView) itemView.findViewById(R.id.testCondition);
                imageView = (ImageView) itemView.findViewById(R.id.img_icon);
                down = (Button) itemView.findViewById(R.id.btn_down);
                txt_bg = (TextView) itemView.findViewById(R.id.txt1);
                txt_bg1 = (TextView) itemView.findViewById(R.id.txt2);
                //  rlScrollMeg1 = (NestedScrollView) itemView.findViewById(R.id.rl_msg1);
                rlScrollMeg = (RelativeLayout) itemView.findViewById(R.id.rl_msg);

                txt_discription = (TextView) itemView.findViewById(R.id.test_discription);
                //txt_bg1 = (View) itemView.findViewById(R.id.view_bg1);
            }
        }
    }

    public Bitmap convertByteArrayTOBitmap(byte[] profilePic) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(profilePic);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;

    }

    View.OnClickListener mShareListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isShare = true) {
                img_share.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                deleteButton.setVisibility(View.GONE);
                relativeshare.setEnabled(false);

                View viewScreen = rl_home.getRootView();
                viewScreen.buildDrawingCache();
                viewScreen.setDrawingCacheEnabled(true);
                viewScreen.destroyDrawingCache();
                Bitmap screenshot1 = Bitmap.createBitmap(viewScreen.getWidth(), viewScreen.getHeight(), Bitmap.Config.RGB_565);
                viewScreen.draw(new Canvas(screenshot1));
                File mfile2 = savebitmap2(screenshot1);
                final Uri screenshotUri = Uri.fromFile(mfile2);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Spectrum Human Test Result");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Name:" + " " + MemberDataController.getInstance().currentMember.getMname() + "\n Tested time:" + txt_currenrDate.getText() + " " + txt_month.getText());
                shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_satus)));
                img_share.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
               // relativeshare.setEnabled(true);
            } else {
                isShare = false;
                relativeshare.setEnabled(true);

            }
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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomeActivityViewController.class));
        finish();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                Date selectedDateOject = null;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                try {
                    selectedDateOject = df.parse(strEditText);
                    resultCalendar.setTime(selectedDateOject);
                    loadDatForDate(resultCalendar);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.e("onActivityResult", "call" + strEditText);

            }
        }
    }

    public void respondToSwipeGesture() {
        Log.e("respondToSwipeGesture", "call");
        new SwipeDetector(resultRecyclerView).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum swipeType) {
                if (swipeType == SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT) {
                    previousButtonAction();
                }
                if (swipeType == SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT) {
                    Log.e("RIGHT_TO_LEFT", "call");
                    nextButtonAction();
                }
            }
        });
        /*new SwipeDetector(rl_swipe).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum swipeType)
            {
                if(swipeType==SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT)
                {

                    previousButtonAction();
                }
                if(swipeType==SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT)
                {
                    Log.e("RIGHT_TO_LEFT","call");
                    nextButtonAction();

                }
            }
        });*/
    }

    public static class SwipeDetector implements View.OnTouchListener {

        private int min_distance = 100;
        private float downX, downY, upX, upY;
        private View v;

        private onSwipeEvent swipeEventListener;


        public SwipeDetector(View v) {
            this.v = v;
            v.setOnTouchListener(this);
        }

        public void setOnSwipeListener(onSwipeEvent listener) {
            try {
                swipeEventListener = listener;
            } catch (ClassCastException e) {
                Log.e("ClassCastException", "please pass SwipeDetector.onSwipeEvent Interface instance", e);
            }
        }


        public void onRightToLeftSwipe() {
            if (swipeEventListener != null)
                swipeEventListener.SwipeEventDetected(v, SwipeTypeEnum.RIGHT_TO_LEFT);
            else
                Log.e("SwipeDetector error", "please pass SwipeDetector.onSwipeEvent Interface instance");
        }

        public void onLeftToRightSwipe() {
            if (swipeEventListener != null)
                swipeEventListener.SwipeEventDetected(v, SwipeTypeEnum.LEFT_TO_RIGHT);
            else
                Log.e("SwipeDetector error", "please pass SwipeDetector.onSwipeEvent Interface instance");
        }

        public void onTopToBottomSwipe() {
            if (swipeEventListener != null)
                swipeEventListener.SwipeEventDetected(v, SwipeTypeEnum.TOP_TO_BOTTOM);
            else
                Log.e("SwipeDetector error", "please pass SwipeDetector.onSwipeEvent Interface instance");
        }

        public void onBottomToTopSwipe() {
            if (swipeEventListener != null)
                swipeEventListener.SwipeEventDetected(v, SwipeTypeEnum.BOTTOM_TO_TOP);
            else
                Log.e("SwipeDetector error", "please pass SwipeDetector.onSwipeEvent Interface instance");
        }

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return true;
                }
                case MotionEvent.ACTION_UP: {
                    upX = event.getX();
                    upY = event.getY();

                    float deltaX = downX - upX;
                    float deltaY = downY - upY;

                    //HORIZONTAL SCROLL
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        if (Math.abs(deltaX) > min_distance) {
                            // left or right
                            if (deltaY > 5) {
                                this.onLeftToRightSwipe();
                                return true;
                            }
                            if (deltaX > 5) {
                                this.onRightToLeftSwipe();
                                return true;
                            }
                        } else {
                            //not long enough swipe...
                            return false;
                        }
                    }
                    //VERTICAL SCROLL
                    else {
                        if (Math.abs(deltaY) > min_distance) {
                            // top or down
                            if (deltaY < 0) {
                                this.onTopToBottomSwipe();
                                return true;
                            }
                            if (deltaY > 0) {
                                this.onBottomToTopSwipe();
                                return true;
                            }
                        } else {
                            //not long enough swipe...
                            return false;
                        }
                    }

                    return true;
                }
            }
            return false;
        }

        public interface onSwipeEvent {
            public void SwipeEventDetected(View v, SwipeTypeEnum SwipeType);
        }

        public SwipeDetector setMinDistanceInPixels(int min_distance) {
            this.min_distance = min_distance;
            return this;
        }

        public enum SwipeTypeEnum {
            RIGHT_TO_LEFT, LEFT_TO_RIGHT, TOP_TO_BOTTOM, BOTTOM_TO_TOP
        }

    }

    public void loadAnimatonToRecyclerView() {
        // for adding animatio to recyclerview
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getApplicationContext(), resId);
        resultRecyclerView.setLayoutAnimation(animation);

    }

    private void loadTestConditionLable(ResultsTableViewCell.ViewHolder holder, String value) {
        if (value.contains("-ve")) {
            holder.imageView.setImageResource(R.drawable.ic_happy);
            holder.testCondition.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NORMAL_VALUE_KEY));
            holder.testCondition.setTextColor(Color.parseColor("#3a5693"));
        } else {

            holder.imageView.setImageResource(R.drawable.ic_sad);
            holder.testCondition.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ABNORMAL_VALUE_KEY));
            holder.testCondition.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    public int changeLableColor(String condition) {
        int color = 0;
        String[] val = condition.split("\n");
        color = UrineTestDataCreatorController.getInstance().getTestColorForLable(val[0]);
        return color;

    }
}
