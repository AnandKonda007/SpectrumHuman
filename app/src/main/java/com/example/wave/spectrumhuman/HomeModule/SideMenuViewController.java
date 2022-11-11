package com.example.wave.spectrumhuman.HomeModule;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;
import com.example.wave.spectrumhuman.DataBase.AddDoctorDataController;
import com.example.wave.spectrumhuman.DataBase.DeviceDataController;
import com.example.wave.spectrumhuman.DataBase.MemberDataController;
import com.example.wave.spectrumhuman.DataBase.UrineResultsDataController;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.LoginModule.ChangePasswordViewController;
import com.example.wave.spectrumhuman.LoginModule.Constants;
import com.example.wave.spectrumhuman.LoginModule.LoginViewController;
import com.example.wave.spectrumhuman.Models.Member;
import com.example.wave.spectrumhuman.Network.ConnectionReceiver;
import com.example.wave.spectrumhuman.Network.TestApplication;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.MemberServerOject;
import com.example.wave.spectrumhuman.ServerObjects.UserServerObject;
import com.example.wave.spectrumhuman.SideMenu.AddDoctorViewController;
import com.example.wave.spectrumhuman.SideMenu.FindDoctorsViewController;
import com.example.wave.spectrumhuman.SideMenu.MemberViewController;
import com.example.wave.spectrumhuman.SideMenu.LanguageViewController;
import com.example.wave.spectrumhuman.SideMenu.ContactUsViewController;
import com.example.wave.spectrumhuman.SideMenu.MyDeviceViewController;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 */
public class SideMenuViewController extends Fragment {
    TextView changepassword, textView;
    TextView txt_ag, txt_admin, rl_header;
    View view;
    RelativeLayout rlUserLogin;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerLayout mDrawerLayout;
    FragmentDrawerListener drawerListener;
    View containerView;
    @BindView(R.id.rl_addmember)
    RelativeLayout r1_add;
    LinearLayout main;
    RecyclerView memberRecyclerView;
    AddMemberTableView addMemberTableView;
    int selectedPosition = -1;
    RelativeLayout rl_cgPsw;
    Dialog dialog, failurealert;
    ImageView imageView;
    Handler handler = new Handler();
    TextView members, addmember, mydevice, languages, contactus, aboutus, logout;
    Button ok, cancel;
    TextView adddoctor, finddoctor;
    TelephonyManager telephonyManager;
    Member selectedMember;
     boolean logoutdata = false;

    public SideMenuViewController() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_fragment_drawer_list, container, false);
        ButterKnife.bind(this, view);
        init();
        isUserHavingManualType();

        updateLanguageTexts();
        getDeviceId();
        return view;
    }


    public String getDeviceId() {


        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String id = telephonyManager.getDeviceId();
        Log.e("tellid", "" + telephonyManager.getDeviceId());

        return id;
    }
    public void init() {
        rl_cgPsw = (RelativeLayout) view.findViewById(R.id.rl_cgpsw);

        rlUserLogin = (RelativeLayout) view.findViewById(R.id.rlUserLogin);
        main = (LinearLayout) view.findViewById(R.id.main);
        members = (TextView) view.findViewById(R.id.members);
        addmember = (TextView) view.findViewById(R.id.addmember);
        mydevice = (TextView) view.findViewById(R.id.mydevice);
        changepassword = (TextView) view.findViewById(R.id.changepassword);
        languages = (TextView) view.findViewById(R.id.languages);
        contactus = (TextView) view.findViewById(R.id.contactus);
        aboutus = (TextView) view.findViewById(R.id.about);
        logout = (TextView) view.findViewById(R.id.logout);

        adddoctor = (TextView) view.findViewById(R.id.familydoctor);
        finddoctor = (TextView) view.findViewById(R.id.doctors);


        memberRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_addmember);
        refreshTableView();
        /*Log.e("MemberDataControllerallMembers","call"+MemberDataController.getInstance().allMembers);
        addMemberTableView = new AddMemberTableView(MemberDataController.getInstance().allMembers, getActivity().getApplication());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        memberRecyclerView.setLayoutManager(horizontalLayoutManager);
        memberRecyclerView.setAdapter(addMemberTableView);*/

    }

    @Override
    public void onStart() {
        super.onStart();
        // EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAdminData();
        addMemberTableView.notifyDataSetChanged();
    }

    @OnClick({R.id.language})
    public void language() {
        mDrawerLayout.closeDrawer(containerView);
        startActivity(new Intent(getActivity(), LanguageViewController.class));

    }

    @OnClick({R.id.rl_addfamilydoctor})
    public void addfamilydoctor(View view) {

        mDrawerLayout.closeDrawer(containerView);
        startActivity(new Intent(getActivity(), AddDoctorViewController.class));

    }

    @OnClick({R.id.rl_finddoctors})
    public void finddoctor(View view) {

        mDrawerLayout.closeDrawer(containerView);
        startActivity(new Intent(getActivity(), FindDoctorsViewController.class));

    }

    public void isUserHavingManualType() {
        Log.e("manual", "call");
        UserDataController.getInstance().fetchUserData();
        if (UserDataController.getInstance().allUsers.size() > 0) {
            if (UserDataController.getInstance().currentUser.getRegisterType().equals("Manual")) {
                rl_cgPsw.setVisibility(View.VISIBLE);
            } else {
                rl_cgPsw.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.rlUserLogin})
    public void onClick(View view) {
        mDrawerLayout.closeDrawer(containerView);
        startActivity(new Intent(getActivity(), PersonalInformationViewController.class));

    }

    @OnClick({R.id.rl_mydevice})
    public void onMydevice() {

        mDrawerLayout.closeDrawer(containerView);
        Intent intent = new Intent(new Intent(getActivity(), MyDeviceViewController.class));
        intent.putExtra("isFromTestNow", false);
        startActivity(intent);

    }

    @OnClick({R.id.aboutus})
    public void aboutus() {
        movetoaboutus();

    }

    @OnClick({R.id.rl_addmember})
    public void Click() {
        mDrawerLayout.closeDrawer(containerView);
        Intent i = new Intent(getActivity(), MemberViewController.class);
        i.putExtra("fromMembers", "0");// 0 means we can insert neew members
        startActivity(i);
        //startActivity(new Intent(getActivity(), MemberViewController.class));

    }

    @OnClick({R.id.rl_contactUs})
    public void contactUs() {
        mDrawerLayout.closeDrawer(containerView);
        startActivity(new Intent(getActivity(), ContactUsViewController.class));

    }

    @OnClick({R.id.rl_cgpsw})
    public void ChangePsw() {
        mDrawerLayout.closeDrawer(containerView);
        startActivity(new Intent(getActivity(), ChangePasswordViewController.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.rl_logout)
    public void logout() {
        mDrawerLayout.closeDrawer(containerView);
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_logout_alert);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
//        dialog.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

        Button cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        Button ok = (Button) dialog.findViewById(R.id.btn_ok);
        TextView txt_msg = (TextView) dialog.findViewById(R.id.txt_msg);
        TextView txt_alert = (TextView) dialog.findViewById(R.id.text_reminder);
        txt_alert.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ALERT_KEY));

        txt_msg.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DO_YOU_WANT_TO_LOG_OUT_KEY));

        ok.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.YES_KEY));
        cancle.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_KEY));

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();

            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                if (isConn()){
                    showRefreshDialogue();

                    loadLogoutServerApi();

                }else {
                    new AlertShowingDialog(getActivity(), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));
                }

            }
        });
    }

    public void movetoaboutus() {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.aboutus_url.toString()));
        startActivity(browserIntent);

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        ///for builtin button open close drawayer.
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setTranslationX(slideOffset * drawerView.getWidth());
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();
                //below line used to remove shadow of drawer
                mDrawerLayout.setScrimColor(Color.TRANSPARENT);
            }//this method helps you to aside menu drawer

        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }


    public void loadAdminData() {
        Log.e("loadCurrentMemberInfoe", "call");
        MemberDataController.getInstance().fetchMemberData();
        Member adminMember = MemberDataController.getInstance().getAdminDetails();
        if (adminMember != null) {
            Log.e("loadcurrentMember", "call");
            String name = adminMember.getMname();
            String age = adminMember.getMbirthday();
            String gender = adminMember.getMgender();
            String birthdate = age;
            String[] fullDate = birthdate.split("-");
            int year = Integer.parseInt(fullDate[0]);
            int month = Integer.parseInt(fullDate[1]);
            int day = Integer.parseInt(fullDate[2]);
            String mage = getAge(year, month, day);
            Log.e("age", "call" + mage);
            CircleImageView adminPic = (CircleImageView) getActivity().findViewById(R.id.imageView_admin);
            TextView txt_ag = (TextView) getActivity().findViewById(R.id.txt_age);
            TextView txt_name = (TextView) getActivity().findViewById(R.id.textUsername);
            TextView txt_admin = (TextView) getActivity().findViewById(R.id.textEmail);
            adminPic.setImageBitmap(convertByteArrayTOBitmap(adminMember.getMprofilepicturepath()));
            txt_ag.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.AGE_KEY) + " " + getAge(year, month, day));
            txt_name.setText("" + name);
            txt_admin.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADMIN_KEY));
        }


    }

    public String getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();
        return ageS;
    }


    public Bitmap convertByteArrayTOBitmap(byte[] profilePic) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(profilePic);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }

    // Step 1:-
    public class AddMemberTableView extends RecyclerView.Adapter<AddMemberTableView.ViewHolder> {

        // step 3:-
        ArrayList<Member> arrayList = new ArrayList<>();
        Context ctx;

        public AddMemberTableView(ArrayList<Member> arrayList, Context ctx) {
            this.ctx = ctx;
            this.arrayList = arrayList;
        }

        // step 5:-
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_listitems, parent, false);

            ViewHolder contactViewHolder = new ViewHolder(itemView, ctx, arrayList);
            return contactViewHolder;
        }


        //step 6:-
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            Member objMember = arrayList.get(position);

            holder.userName.setText(objMember.getMname());

            holder.userAdmin.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(objMember.getMrelationshipname()));
            holder.img.setImageBitmap(convertByteArrayTOBitmap(objMember.getMprofilepicturepath()));

            String name = objMember.getMname();
            String age = objMember.getMbirthday();
            String gender = objMember.getMgender();
            String birthdate = age;
            String[] fullDate = birthdate.split("-");
            int year = Integer.parseInt(fullDate[0]);
            int month = Integer.parseInt(fullDate[1]);
            int day = Integer.parseInt(fullDate[2]);
            String mage = getAge(year, month, day);
            holder.userAge.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.AGE_KEY) + " " + mage);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = position;

                    selectedMember = MemberDataController.getInstance().allMembers.get(selectedPosition);


                    if (selectedMember.getMrelationshipname().equals("Me")) {

                        if (MemberDataController.getInstance().currentMember == selectedMember) {
                            mDrawerLayout.closeDrawer(containerView);
                        } else {
                            if (isConn()) {
                                executeServerAPIforMemberUpdate(selectedMember, true);

                            } else {


                                Log.e("offline","call");

                                MemberDataController.getInstance().makeThisMemberAsActive(selectedMember,false);
                                MemberDataController.getInstance().refreshActiveMember();
                                mDrawerLayout.closeDrawer(containerView);
                                EventBus.getDefault().post(new MessageEvent("refreshMember"));
                            }

                        }
                    } else {

                        if (MemberDataController.getInstance().currentMember == selectedMember) {
                            mDrawerLayout.closeDrawer(containerView);
                        } else {

                            if (isConn()) {
                                executeServerAPIforMemberUpdate(selectedMember, false);

                            } else {



                                Log.e("offline","call");

                                MemberDataController.getInstance().makeThisMemberAsActive(selectedMember,false);
                                MemberDataController.getInstance().refreshActiveMember();
                                mDrawerLayout.closeDrawer(containerView);
                                EventBus.getDefault().post(new MessageEvent("refreshMember"));

                            }

                        }

                    }


                }
            });

        }

        // step 4:-
        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        // Step 2:-
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            CircleImageView img;
            TextView userName, userAge, userAdmin;
            ArrayList<Member> arrayList = new ArrayList<Member>();
            Context ctx;

            public ViewHolder(View itemView, Context ctx, final ArrayList<Member> arrayList) {
                super(itemView);

                this.arrayList = arrayList;
                this.ctx = ctx;
                userName = (TextView) itemView.findViewById(R.id.user_name);
                userAge = (TextView) itemView.findViewById(R.id.user_age);
                userAdmin = (TextView) itemView.findViewById(R.id.admin);
                img = (CircleImageView) itemView.findViewById(R.id.profile_image);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {

            }

        }
    }

    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }

    public void showRefreshDialogue() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_animate);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        //  dialog.create();
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(R.id.connecting);
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


    private void executeServerAPIforMemberUpdate(final Member objMember, Boolean isAdmin) {

        showRefreshDialogue();

        Log.e("insertMemberInfoToServer", "call");
        MemberServerOject requestBody = new MemberServerOject();

        requestBody.setUser_Id(UserDataController.getInstance().currentUser.getUserName().trim());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<MemberServerOject> callable;

        // If Admin
        if (isAdmin) {
            Log.e("editMemberInfo", "call");
            callable = api.InActiveMemberInfo(requestBody);
        } else {
            // If not Admin
            Log.e("addMemberInfo", "call");
            callable = api.InActiveAddMemberInfo(requestBody);
            requestBody.setMember_id(objMember.getMember_Id());
        }

        callable.enqueue(new Callback<MemberServerOject>() {
            @Override
            public void onResponse(Call<MemberServerOject> call, Response<MemberServerOject> response) {
                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();
                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();
                Log.e("codefor3", "call" + statusCode);
                if (statusCode.equals("3")) {

                    MemberDataController.getInstance().makeThisMemberAsActive(objMember,true);
                    MemberDataController.getInstance().refreshActiveMember();
                    mDrawerLayout.closeDrawer(containerView);
                    EventBus.getDefault().post(new MessageEvent("refreshMember"));

                   Handler  handler = new Handler();
                   handler.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                          getActivity().startActivity(new Intent(getActivity(), HomeActivityViewController.class));
                           getActivity().overridePendingTransition(0, 0);
                       }
                   },200*1);
                    Log.e("pos", "" + objMember.getMember_Id());


                }
            }

            @Override
            public void onFailure(Call<MemberServerOject> call, Throwable t) {
                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();
                failurealert();
            }
        });

    }

    //for refresh side menu recycler view
    public void refreshTableView() {
        MemberDataController.getInstance().fetchMemberData();
        Log.e("MemberDataControllerall", "call" + MemberDataController.getInstance().allMembers);
        addMemberTableView = new AddMemberTableView(MemberDataController.getInstance().allMembers, getActivity().getApplication());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        memberRecyclerView.setLayoutManager(horizontalLayoutManager);
        memberRecyclerView.setAdapter(addMemberTableView);
        addMemberTableView.notifyDataSetChanged();

    }

    ///////////
    public static class MessageEvent {
        public final String message;

        public MessageEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public void updateLanguageTexts() {

        members.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SIDE_MENU_MEMBERS_TITLE_KEY));

        changepassword.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CHANGE_PASSWORD_KEY));

        addmember.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADD_MEMBER_FOOTER_VIEW_SIDE_MENU_KEY));

        mydevice.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DEVICE_INFORMATION_KEY));

        languages.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LANGUAGES_KEY));

        contactus.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CONTACT_US_KEY));

        aboutus.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ABOUT_US_KEY));

        logout.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LOG_OUT_KEY));


        adddoctor.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FAMILY_DOCTOR));

        finddoctor.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FIND_DOCTOR_KEY));

    }


    ////////////////////////////////////logoutaps/////////////////////////////
    private void loadLogoutServerApi() {

        logoutdata = true;
        SharedPreferences tokenPreferences = getActivity().getSharedPreferences("tokendeviceids", Context.MODE_PRIVATE);

        String tokenId = tokenPreferences.getString("tokenid", null);
        String deviceId = tokenPreferences.getString("deviceid", null);

        Log.e("tokenPreferences", "call " + tokenId + "ids" + deviceId);


        final UserServerObject requestBody = new UserServerObject();
        requestBody.setUserName(UserDataController.getInstance().currentUser.getUserName());
        Log.e("email", "" + UserDataController.getInstance().currentUser.getUserName());

        requestBody.setDeviceid(deviceId);
        Log.e("logtellid", "" + deviceId);

        requestBody.setDeviceToken(tokenId);
        Log.e("logfirebase", "" + tokenId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<UserServerObject> callable = api.logout(requestBody);
        callable.enqueue(new retrofit2.Callback<UserServerObject>() {
            @Override
            public void onResponse(Call<UserServerObject> call, final retrofit2.Response<UserServerObject> response) {
                dialog.dismiss();
                handler.removeCallbacks(updateTimerThread);

                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();

                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {
                        deleteDatabaseData();
                        logoutdata=false;

                    }
                }

            }

            @Override
            public void onFailure(Call<UserServerObject> call, Throwable t) {

                handler.removeCallbacks(updateTimerThread);
                dialog.dismiss();
                failurealert();
            }
        });

    }

    public void failurealert() {

        Log.e("responsealert", "call");
        failurealert = new Dialog(getActivity());
        failurealert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        failurealert.setCancelable(false);
        failurealert.setCanceledOnTouchOutside(false);
        failurealert.setCancelable(true);
        failurealert.setContentView(R.layout.activity_failurealert);
        failurealert.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        failurealert.show();

        TextView text = (TextView) failurealert.findViewById(R.id.text_error);
        text.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ERROR_KEY));

        TextView text1 = (TextView) failurealert.findViewById(R.id.requestfail);
        // text1.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ERROR_KEY));

        Button cancel = (Button) failurealert.findViewById(R.id.btn_failurecancel);
        cancel.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.CANCEL_KEY));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("cancel", "canel");

                failurealert.dismiss();


            }


        });
        Button retry = (Button) failurealert.findViewById(R.id.btn_failureretry);
        // retry.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.Ret ));

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("retry", "retry");

                if (isConn()) {
                    failurealert.dismiss();

                    if (logoutdata == true) {
                        showRefreshDialogue();
                        loadLogoutServerApi();

                    }else {
                        executeServerAPIforMemberUpdate(selectedMember, true);

                    }

                } else {
                    failurealert.dismiss();
                    new AlertShowingDialog(getActivity(), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_INTERNET_CONNECTION_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.WARNING_KEY), LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OK_KEY));

                }

            }


        });


    }


    public void deleteDatabaseData() {
        UserDataController.getInstance().deleteUserData(UserDataController.getInstance().allUsers);
        UserDataController.getInstance().currentUser = null;


        MemberDataController.getInstance().deleteMemberData(MemberDataController.getInstance().allMembers);
        MemberDataController.getInstance().currentMember = null;
        UrineResultsDataController.getInstance().deleteUrineResults(UrineResultsDataController.getInstance().allUrineResults);
        DeviceDataController.getInstance().deleteAllDeviceInformation(DeviceDataController.getInstance().allDevices);
        DeviceDataController.getInstance().currentDevice = null;

        AddDoctorDataController.getInstance().deleteAddDoctorsData(AddDoctorDataController.getInstance().allDoctorData);
        AddDoctorDataController.getInstance().currentDoctor = null;
        ///////////
        SharedPreferences preferences = getActivity().getSharedPreferences("socialMediaLoginDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        startActivity(new Intent(getActivity(), LoginViewController.class));
    }


}
