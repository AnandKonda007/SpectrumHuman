package com.example.wave.spectrumhuman.HomeModule;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;
import com.example.wave.spectrumhuman.DataBase.AddDoctorDataController;
import com.example.wave.spectrumhuman.DataBase.MemberDataController;
import com.example.wave.spectrumhuman.DataBase.OfflineDataController;
import com.example.wave.spectrumhuman.DataBase.UrineResultsDataController;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
//import com.example.wave.spectrumhuman.Graph.TabsGraphActivity;
import com.example.wave.spectrumhuman.Graphs.TabsGraphActivity;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.LoginModule.Constants;
import com.example.wave.spectrumhuman.LoginModule.LoginViewController;
import com.example.wave.spectrumhuman.LoginModule.SplashScreenViewController;
import com.example.wave.spectrumhuman.Models.Member;
import com.example.wave.spectrumhuman.Models.UrineresultsModel;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.MemberServerOject;
import com.example.wave.spectrumhuman.SideMenu.AddDoctorViewController;
import com.example.wave.spectrumhuman.SideMenu.MemberViewController;
import com.example.wave.spectrumhuman.TestModule.AnalyzingPageViewController;
import com.example.wave.spectrumhuman.TestModule.ResultPageViewController;
import com.example.wave.spectrumhuman.TestModule.TestNowViewController;
import com.example.wave.spectrumhuman.Graphs.UrineTestDataCreatorController;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.wave.spectrumhuman.Languages.LanguagesKeys.SPECTRUM_TITLE_KEY;

public class HomeActivityViewController extends AppCompatActivity implements SideMenuViewController.FragmentDrawerListener{
    Toolbar toolbar;
    //this is Fragment which handles the Navigationdrawer items.
    SideMenuViewController drawerFragment;
    DrawerLayout drawer;
    ImageView back,img_graph,img_shop;
    FrameLayout frameLayout;
    RecyclerView resultRecyclerView;
    RecentResultsTableViewCell resultsTableViewCell;
    public static int selectedPosition = -1;
    public static boolean isFromHome=false;
    TextView tool_text,recentresults,txt_testnow;
    Dialog dialog,failurealert;
    Member objMember  ;
    //for using graphs Day
    public static Date dateForLandScap;
    public static Calendar calender;
    public static String selectedDateText = "";
    //for week
    public static Calendar WeekStartTime, WeekEndTime,weekCalendar;

    //for year
    public static Calendar calander;
    public static Calendar calanderForYear;

    public static String selectedDateTextYear = "";

    //for month
    public static Calendar monthCalender;
    public static Calendar monthCalenderFormonth;
    public static String selectedDateTextMonth = "";

    SimpleDateFormat simpleDateFormat;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_drawer);
        ButterKnife.bind(this);

        TabsGraphActivity.isFromMonth=false;
        TabsGraphActivity.isFromYear=false;

        init();
        refreshResultsTableView();
        initializeDrawer();
        loadCurrentMemberInfo();

        Log.e("onCreate","call");
        updateLanguageTexts();



        notification();

    }


    private void notification() {

        Log.e("MoveTodoctor",""+SplashScreenViewController.isMoveToDoctorPage);

        if (SplashScreenViewController.isMoveToDoctorPage)
        {
            AddDoctorDataController.getInstance().fetchDoctorInfo();
            Intent intent = new Intent(this, AddDoctorViewController.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            SplashScreenViewController.isMoveToDoctorPage = false;
            startActivity(intent);
        }

    }

    public  void loadLatestRecordForDay(){
        UrineResultsDataController.getInstance().fetchAllUrineResults();
        UrineresultsModel selectedObject =UrineResultsDataController.getInstance().allUrineResults.get(UrineResultsDataController.getInstance().allUrineResults.size()-1);
        String objTimeStamp =selectedObject.getTestedTime();
        long yourmilliseconds = Long.parseLong(objTimeStamp);
        //for day
        calender = Calendar.getInstance();
        Date date = new Date(yourmilliseconds*1000);
        calender.setTime(date);
        dateForLandScap=calender.getTime();

    }
    public void loadLatestRecordForWeek(){
        //for weeek
        WeekStartTime = Calendar.getInstance();
        WeekEndTime = Calendar.getInstance();
        weekCalendar=Calendar.getInstance();

        //For get Week start date from given date
        Calendar c = Calendar.getInstance();
        c.setTime(dateForLandScap);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        c.add(Calendar.DAY_OF_MONTH, -dayOfWeek);
        Date weekStart = c.getTime();

        WeekStartTime.setTime(weekStart);
        Log.e("weekStart","call"+weekStart);

        //for get week end date from given date
    // we do not need the same day a week after, that's why use 6, not 7
        c.add(Calendar.DAY_OF_MONTH, 6);
        Date weekEnd = c.getTime();
        Log.e("weekEnd","call"+weekEnd);
        WeekEndTime.setTime(weekEnd);

        ///for week
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        //HomeActivityViewController.WeekEndTime.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        String EndsWeek=simpleDateFormat.format(HomeActivityViewController.WeekEndTime.getTime());
        selectedDateText=EndsWeek;

        Log.e("selectedDateText","call"+selectedDateText);
        ///
    }
    public void  loadMonthLatestRecord(){
        //fro month
        monthCalender = Calendar.getInstance();
        monthCalenderFormonth=Calendar.getInstance();

        monthCalender.setTime(dateForLandScap);
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        String EndsWeek=simpleDateFormat.format(HomeActivityViewController.monthCalender.getTime());
        selectedDateTextMonth=EndsWeek;
        Log.e("selectedDateTextMonth","call"+selectedDateTextMonth);

    }
    public void  loadYearLatestRecord(){
        UrineResultsDataController.getInstance().fetchAllUrineResults();
        UrineresultsModel selectedObject =UrineResultsDataController.getInstance().allUrineResults.get(UrineResultsDataController.getInstance().allUrineResults.size()-1);
        String objTimeStamp =selectedObject.getTestedTime();
        long yourmilliseconds = Long.parseLong(objTimeStamp);

        calander = Calendar.getInstance();
        calanderForYear=Calendar.getInstance();

        Date date = new Date(yourmilliseconds*1000);
        calander.setTime(date);
    }
    public void refreshResultsTableView(){
        Log.e("refreshResultsTableView","call"+UrineResultsDataController.getInstance().allUrineResults.size());

        UrineResultsDataController.getInstance().fetchAllUrineResults();
        resultRecyclerView =(RecyclerView)findViewById(R.id.recycler_recent_result);
        resultsTableViewCell = new RecentResultsTableViewCell(UrineResultsDataController.getInstance().allUrineResults);
        resultRecyclerView.setAdapter(resultsTableViewCell);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this);
        resultRecyclerView.setLayoutManager(horizontalLayoutManager);
       resultsTableViewCell.notifyDataSetChanged();
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.e("onResume","call");
        // register connection status listener
        EventBus.getDefault().register(this);
        updateLanguageTexts();
        drawerFragment.updateLanguageTexts();
        loadCurrentMemberInfo();
        resultsTableViewCell.notifyDataSetChanged();

    }
    @Override
    public void  onStart(){
        super.onStart();
    }
    @Override
    public void onPause() {
        super.onPause();
       EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();
     //   EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }


    public void init()
    {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=(ImageView)findViewById(R.id.toolbar_icon);//Spectrum
        back.setBackgroundResource(R.drawable.ic_menu);
        tool_text=(TextView)toolbar.findViewById(R.id.toolbar_text);
        ///
        tool_text.setText(LanguagesKeys.SPECTRUM_TITLE_KEY);
       //
        Log.e("tool_text","call"+LanguageTextController.getInstance().currentLanguageDictionary.get(SPECTRUM_TITLE_KEY));
        img_graph=(ImageView)toolbar.findViewById(R.id.img_graph);
        img_graph.setBackgroundResource(R.drawable.ic_graph);
        img_shop=(ImageView)toolbar.findViewById(R.id.img_shop);
        img_shop.setBackgroundResource(R.drawable.ic_cart);
        img_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movetoshopping();
            }
        });
        frameLayout=(FrameLayout)findViewById(R.id.container_body);
         recentresults=(TextView)findViewById(R.id.recentresults);
        txt_testnow=(TextView)findViewById(R.id.txt_testnow);
    }
    //initializing the navigation drawer
    public void initializeDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment = (SideMenuViewController) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, drawer, toolbar);

        drawerFragment.setDrawerListener(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);// for open the side menu
            }
        });
    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(SideMenuViewController.MessageEvent event) {
        Log.e("sidemenuMessageevent", "" + event.message);
        String resultData = event.message.trim();
        if(resultData.equals("refreshMember"))
        {
            Log.e("sidemenuMessageevent", "call" + event.message);
            loadCurrentMemberInfo();
            UrineResultsDataController.getInstance().fetchAllUrineResults();
        } else if(resultData.equals("refreshLanguages"))
        {
            Log.e("LanguageupdateInfo","Called");
            updateLanguageTexts();
            resultsTableViewCell.notifyDataSetChanged();
        }


    }
    public void loadCurrentMemberInfo() {
        Log.e("loadCurrentMemberInfoe","call");
        MemberDataController.getInstance().fetchMemberData();
        objMember=MemberDataController.getInstance().currentMember;
        if (objMember != null)
        {
            Log.e("loadcurrentMember","call"+objMember.getEmail());
            String name = objMember.getMname();
            String age = objMember.getMbirthday();
            String gender = objMember.getMgender();
            String birthdate = age ;
            String[] fullDate = birthdate.split("-");
            int year = Integer.parseInt(fullDate[0]);
            int month = Integer.parseInt(fullDate[1]);
            int day = Integer.parseInt(fullDate[2]);
            String mage=getAge(year,month,day);
            Log.e("age","call" + mage);
            CircleImageView profileButton = (CircleImageView) findViewById(R.id.img_profile);
            TextView memberInfoLabel = (TextView) findViewById(R.id.txt_memberInfoLabel);
            profileButton.invalidate();//
            profileButton.setBackgroundResource(0);
            profileButton.setImageBitmap(null);//for clear circular imageee

            String MemberInfoString = name +" "+"/"+LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.AGE_KEY)+" "+ mage +"/"+" "+ LanguageTextController.getInstance().currentLanguageDictionary.get(gender);
            memberInfoLabel.setText(MemberInfoString);
            profileButton.setImageBitmap(convertByteArrayTOBitmap(objMember.getMprofilepicturepath()));

            UrineResultsDataController.getInstance().fetchAllUrineResults();

            if (UrineResultsDataController.getInstance().allUrineResults.size()>0){
                refreshResultsTableView();
                resultsTableViewCell.notifyDataSetChanged();

            }else {
                Log.e("resultRecyclerView","call");
                resultRecyclerView.setVisibility(View.GONE);
            }
        }
    }
    public Bitmap convertByteArrayTOBitmap(byte[] profilePic){
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(profilePic);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }
    public String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();
        return ageS;
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        drawer.closeDrawer(GravityCompat.END);

    }

    @OnClick(R.id.img_graph)
    public void graphs()
    {
        if (UrineResultsDataController.getInstance().allUrineResults.size()>0){
            loadLatestRecordForDay();
            loadLatestRecordForWeek();
            loadMonthLatestRecord();
            loadYearLatestRecord();
            startActivity(new Intent(getApplicationContext(),TabsGraphActivity.class));
        }else
            {
            Toast.makeText(getApplicationContext(),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DATA_IS_NOT_AVAILABLE_TOAST_KEY),Toast.LENGTH_SHORT).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.img_profile)
    public void setProfileAlert(){
        dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_profie_alert);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        RelativeLayout editmember=(RelativeLayout)dialog.findViewById(R.id.rl_editmember);
        RelativeLayout remove=(RelativeLayout)dialog.findViewById(R.id.rl_remove);
        RelativeLayout cancel=(RelativeLayout)dialog.findViewById(R.id.rl_cancel);
        ///
        TextView editmemberone=(TextView)dialog.findViewById(R.id.txt_editProfile);
        TextView removeone=(TextView)dialog.findViewById(R.id.txt_removeProfile);
        TextView cancelone=(TextView)dialog.findViewById(R.id.txt_cancleProfile);
          //

        editmemberone.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.EDIT_PROFILE_TITLE_KEY));
        removeone.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.REMOVE_MEMBER_KEY));
        cancelone.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

        editmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                Log.e("CurrentMember",""+MemberDataController.getInstance().currentMember);

                if(MemberDataController.getInstance().currentMember.getMrelationshipname().equals("Me")) {

                    Log.e("PersonalInfoStatus","Me Called");
                    Intent intent = new Intent(HomeActivityViewController.this, PersonalInformationViewController.class);
                   // Log.e("AdminDetails",""+MemberDataController.getInstance().getAdminDetails());
                   // intent.putExtra("personalInfo", (Parcelable) MemberDataController.getInstance().getAdminDetails());
                   // startActivity(intent);
                   startActivity(new Intent(getApplicationContext(), PersonalInformationViewController.class));
                }
                else
                {
                    Log.e("PersonalInfoStatus"," outside Me Called");
                    Intent i=new Intent(getApplicationContext(),MemberViewController.class);
                    i.putExtra("fromMembers","1");// 1 means we can edit  members details
                    startActivity(i);
                    //startActivity(new Intent(getApplicationContext(), MemberViewController.class));
                }
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                dialog.cancel();
                if (MemberDataController.getInstance().currentMember.getMrelationshipname().equals("Me")) {
                    new AlertShowingDialog(HomeActivityViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADMIN_DETAILS_CAN_NOT_BE_REMOVED_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                }else {
                    final Dialog removedialog=new Dialog(HomeActivityViewController.this);
                    removedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    removedialog.setContentView(R.layout.activity_removemember_alert);
                    removedialog.setCanceledOnTouchOutside(false);
                    removedialog.setCancelable(false);
                    removedialog.create();
                    removedialog.show();
                    removedialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
                    Button yes=(Button) removedialog.findViewById(R.id.btn_yes);
                    Button no=(Button) removedialog.findViewById(R.id.btn_no);
                    yes.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.YES_KEY));
                    no.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_KEY));

                    TextView removemember=(TextView)removedialog.findViewById(R.id.txt_removeMember) ;
                    TextView alert=(TextView)removedialog.findViewById(R.id.text_reminder) ;
                    alert.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ALERT_KEY));
                    removemember.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DO_YOU_WANT_TO_REMOVE_MEMBER_KEY));


                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removedialog.cancel();
                            if(isConn()){
                                showRefreshDialogue();
                                deleteMemberData();
                            }else {
                                new AlertShowingDialog(getApplicationContext(), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                            }
                        }
                    });
                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removedialog.cancel();
                        }
                    });
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }
    @OnClick(R.id.img_testnow)
    public void moveToTestnowPage(){
        startActivity(new Intent(getApplicationContext(),TestNowViewController.class));
        isFromHome=false;
    }
    @OnClick(R.id.txt_testnow)
    public void moveToTestnow(){
        startActivity(new Intent(getApplicationContext(),TestNowViewController.class));
        isFromHome=false;

    }


    // Step 1:-
    public class RecentResultsTableViewCell extends RecyclerView.Adapter<RecentResultsTableViewCell.ViewHolder>  {
        private List<UrineresultsModel> arrayList;

        // Step 2:-
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            RadioButton radioButton;
            TextView time, date;
            Context ctx;

            public ViewHolder(View itemView) {
                super(itemView);
                radioButton = (RadioButton) itemView.findViewById(R.id.radioButton_check);
                time = (TextView) itemView.findViewById(R.id.txt_time);
                date = (TextView) itemView.findViewById(R.id.txt_date);
                itemView.setOnClickListener(this);
                ///
            }

            @Override
            public void onClick(View v) {

            }

        }
        // step 3:-
        public RecentResultsTableViewCell(List<UrineresultsModel> arrayList) {
            this.arrayList = arrayList;
            Log.e("arraysize",""+this.arrayList.size());
        }

        // step 5:-
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_recentresult_items, parent, false);
            Log.e("arraysize",""+arrayList.size());

            return new ViewHolder(itemView);
        }


        //step 6:-
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position)
        {
            Log.e("onBindViewHolder","called");

            UrineresultsModel objUrineResult = this.arrayList.get(position);

            try {
                String timeString = UrineTestDataCreatorController.getInstance().convertTimestampToDate(objUrineResult.getTestedTime());
                String[] timeArray = timeString.split(";");
                holder.date.setText(timeArray[2]+"\n"+timeArray[timeArray.length-1]);

                if(selectedPosition==position)
                {
                    Log.e("if","called");
                    holder.radioButton.setChecked(true);

                }
                else
                {
                    Log.e("else","called");
                    holder.radioButton.setChecked(false);

                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedPosition = position;
                        Log.e("pos",""+selectedPosition);
                        notifyDataSetChanged();
                        startActivity(new Intent(getApplicationContext(),ResultPageViewController.class));
                        isFromHome=true;

                    }
                });

            } catch (ParseException e) {
                e.printStackTrace();
            }



        }

        // step 4:-
        @Override
        public int getItemCount() {
            Log.e("getItemCount","called"+this.arrayList.size());
            return arrayList.size();

        }

    }
    ///////////
    @Override
    public void onBackPressed()
    {    //when click on phone backbutton
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    //for remove the dialoge when the dialogue window leak features

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }
    public  void movetoshopping(){

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.shopping_url.toString()));
        startActivity(browserIntent);

    }
    ImageView imageView;
    android.os.Handler handler=new android.os.Handler();
    Dialog dialog1;
    public void showRefreshDialogue(){
        dialog1=new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.activity_animate);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.setCancelable(false);
        dialog1.show();
        dialog1.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        TextView textView=(TextView)dialog1.findViewById(R.id.connecting);
        textView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONNECTING_KEY));

        imageView=(ImageView)dialog1.findViewById(R.id.image_rottate) ;
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
    public  void deleteMemberData() {
        Log.e("deleteToServer", "call");
        MemberServerOject requestBody = new MemberServerOject();
        requestBody.setUser_Id(UserDataController.getInstance().currentUser.getUserName());
        requestBody.setMember_id(MemberDataController.getInstance().currentMember.getMember_Id());
        Log.e("Memberid", "call" + requestBody.getMember_id());
        if (MemberDataController.getInstance().currentMember.getMrelationshipname().equals("Me")) {
            handler.removeCallbacks(updateTimerThread);
            dialog1.dismiss();
            //new AlertShowingDialog(HomeActivityViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADMIN_DETAILS_CAN_NOT_BE_REMOVED_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ServerApisInterface.home_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ServerApisInterface api = retrofit.create(ServerApisInterface.class);
            Call<MemberServerOject> callable = api.deleteMemberInfo(requestBody);
            callable.enqueue(new Callback<MemberServerOject>() {
                @Override
                public void onResponse(Call<MemberServerOject> call, Response<MemberServerOject> response) {
                    handler.removeCallbacks(updateTimerThread);
                    dialog1.dismiss();
                    String statusCode = response.body().getResponse();
                    String message = response.body().getMessage();
                    Log.e("deleteMemberData", "call" + statusCode);
                    if (statusCode.equals("3")) {
                        if (MemberDataController.getInstance().deleteMemberData(MemberDataController.getInstance().currentMember)) {
                            MemberDataController.getInstance().refreshActiveMember();
                            loadCurrentMemberInfo();
                            drawerFragment.refreshTableView();
                        }
                    } else if (statusCode == "5") {

                    } else {
                    }
                }

                @Override
                public void onFailure(Call<MemberServerOject> call, Throwable t) {
                    handler.removeCallbacks(updateTimerThread);
                    dialog1.dismiss();
                    failurealert();
                }
            });
        }
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
                    showRefreshDialogue();
                    deleteMemberData();

                }else {
                    failurealert.dismiss();
                    new AlertShowingDialog(HomeActivityViewController.this, LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY),LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }


        });


    }

    public void updateLanguageTexts() {
       // tool_text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SPECTRUM_TITLE_KEY));
        recentresults.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.RECENT_RESULTS_KEY));
        txt_testnow.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.TEST_NOW_KEY));
        //editmemberone.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.EDIT_PROFILE_TITLE_KEY));
       // removeone.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.REMOVE_MEMBER_KEY));
        //cancelone.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));
        //removemember.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DO_YOU_WANT_TO_REMOVE_MEMBER_KEY));

       // txt.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONFIRM_KEY ));

      //  txtView.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADMIN_DETAILS_CAN_NOT_BE_REMOVED_KEY));

    }




  /*  @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(MyFirebaseInstanceIdService.REGISTRATION_SUCCESS));
    }*/

  /*  @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }*/

   /* private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String devId = intent.getStringExtra(MyFirebaseInstanceIdService.REGISTRATION_TOKEN);
          //  txtViewDeviceId.setText(devId);
        }
    };*/


}
