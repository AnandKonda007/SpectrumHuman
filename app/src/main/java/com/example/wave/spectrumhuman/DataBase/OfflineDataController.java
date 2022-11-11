package com.example.wave.spectrumhuman.DataBase;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.wave.spectrumhuman.Alert.AlertShowingDialog;
import com.example.wave.spectrumhuman.Graphs.UrineTestDataCreatorController;
import com.example.wave.spectrumhuman.HomeModule.HomeActivityViewController;
import com.example.wave.spectrumhuman.HomeModule.SideMenuViewController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.LoginModule.Constants;
import com.example.wave.spectrumhuman.LoginModule.LocationTracker;
import com.example.wave.spectrumhuman.Models.Member;
import com.example.wave.spectrumhuman.Models.UrineresultsModel;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.LanguageServerObjects;
import com.example.wave.spectrumhuman.ServerObjects.MemberServerOject;
import com.example.wave.spectrumhuman.ServerObjects.UrineResultsServerObject;


import org.greenrobot.eventbus.EventBus;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by WAVE on 1/24/2018.
 */

public class OfflineDataController
{
    public static OfflineDataController myObj;

    public  Context context;


    public static  OfflineDataController getInstance() {
        if (myObj == null) {
            myObj = new OfflineDataController();
        }

        return myObj;
    }
    public void fillContext(Context context1) {
        context = context1;
    }

    public void sycnOfflineData() {

        updateUrineResultsOfflineData();
        updateMembersActiveOfflineData();
        updateLangugeInOffline();
    }
    public void updateUrineResultsOfflineData()
    {
        Log.e("updateUrineResultsOfflineData","call");
        for (Member member  : MemberDataController.getInstance().allMembers)
        {
            Member objMember=member;
            ArrayList<UrineresultsModel> urineresultsModels = new ArrayList<UrineresultsModel>(objMember.getUrineresultsModels());
             Log.e("urineresultsModels","call"+urineresultsModels.size());

            if (urineresultsModels .size()>0)
            {
                for (UrineresultsModel objTest :urineresultsModels)
                {
                    UrineresultsModel testObj = objTest;
                    Log.e("testObj","call"+testObj.isFromonline);
                    Log.e("testObjName","call"+testObj.getRelationName());
                    if (!testObj.isFromonline)
                    {
                        executeUrineResultsServerAPI(false, testObj);
                        // Offline stored data handle

                    }
                }

            }

        }
    }
    public void executeUrineResultsServerAPI(final boolean isFromOnline, final UrineresultsModel existedResult )
    {

        String currentTimeString = String.valueOf( System.currentTimeMillis() / 1000L);
        String rbcValueString = String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.rbcMinimumValue, Constants.rbcMaximumValue));
        String billirubinValueString= String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.billirubinMinimumValue,Constants.billirubinMaximumValue));
        String uroboliogenValueString =String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.uroboliogenMinimumValue,Constants.uroboliogenMaximumValue));
        String ketonesValueString =String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.ketonesMinimumValue,Constants.ketonesMaximumValue));
        String proteinValueString =String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.proteinMinimumValue,Constants.proteinMaximumValue));
        String nitriteValueString =String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.nitriteMinimumValue,Constants.nitriteMaximumValue));
        String glucoseValueString =String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.glucoseMinimumValue,Constants.glucoseMaximumValue));
        String phValueString =String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.phMinimumValue,Constants.phMaximumValue));
        String sgValueString =String.valueOf(UrineTestDataCreatorController.getInstance().getRandomDoubleValue(Constants.sgMinimumValue,Constants.sgMaximumValue));
        String leucocyteValueString =String.valueOf(UrineTestDataCreatorController.getInstance().getRandomIntValue(Constants.leucocyteMinimumValue,Constants.leucocyteMaximumValue));

        String relationName = MemberDataController.getInstance().currentMember.getMname();
        String relationType =MemberDataController.getInstance().currentMember.getMrelationshipname();
        String memberID=MemberDataController.getInstance().currentMember.getMember_Id();

        DecimalFormat dfbilii = new DecimalFormat("#.#",new DecimalFormatSymbols(Locale.ENGLISH));
        DecimalFormat dfnitrite = new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.ENGLISH));
        DecimalFormat  dfsg = new DecimalFormat("#.###",new DecimalFormatSymbols(Locale.ENGLISH));

        billirubinValueString=dfbilii.format(Double.parseDouble(billirubinValueString));
        uroboliogenValueString=dfbilii.format(Double.parseDouble(uroboliogenValueString));
        nitriteValueString =dfnitrite.format(Double.parseDouble(nitriteValueString));
        phValueString=dfbilii.format(Double.parseDouble(phValueString));
        sgValueString=dfsg.format(Double.parseDouble(sgValueString));
        //
        String latitude = String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude());
        if (latitude == null) {
            latitude = "0.0";
        }

        String longitude = String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude());
        if (longitude == null) {
            longitude = "0.0";
        }

        if(! isFromOnline && existedResult!=null){

            currentTimeString = existedResult.getTestedTime();
            latitude = existedResult.getLatitude();
            longitude = existedResult.getLongitude();
            rbcValueString = String.valueOf(existedResult.getRbcValue());
            billirubinValueString = String.valueOf(existedResult.getBillirubinValue());
            uroboliogenValueString = String.valueOf(existedResult.getUroboliogenValue());
            ketonesValueString = String.valueOf(existedResult.getKetonesValue());
            proteinValueString = String.valueOf(existedResult.getProteinValue());
            nitriteValueString = String.valueOf(existedResult.getNitriteValue());
            glucoseValueString = String.valueOf(existedResult.getGlucoseValue());
            phValueString = String.valueOf(existedResult.getPhValue());
            sgValueString = String.valueOf(existedResult.getSgValue());
            leucocyteValueString = String.valueOf(existedResult.getLeucocyteValue());
            relationName = existedResult.getRelationName();
            relationType = existedResult.getRelationtype();
            memberID = existedResult.getMember_Id();
        }

        final UrineResultsServerObject objResults =new UrineResultsServerObject();
        objResults.setTestedTime(currentTimeString);
        objResults.setLat(latitude);
        objResults.setLang(longitude);
        objResults.setRelationName(relationName);
        objResults.setRelationType(relationType);
        objResults.setMail(UserDataController.getInstance().currentUser.getUserName());
        objResults.setMember_id(memberID);
        objResults.setRbcValue(rbcValueString);
        objResults.setBillirubinValue(billirubinValueString);
        objResults.setUrobiliogen(uroboliogenValueString);
        objResults.setKetones(ketonesValueString);
        objResults.setProtein(proteinValueString);
        objResults.setNitrite(nitriteValueString);
        objResults.setGlucose(glucoseValueString);
        objResults.setPh(phValueString);
        objResults.setSg(sgValueString);
        objResults.setLeokocit(leucocyteValueString);
        Log.e("setTestedTime","call"+objResults.getTestedTime());
        Log.e("getGlucose","call"+objResults.getGlucose());

        //
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<UrineResultsServerObject> callable=api.urineData(objResults);

        callable.enqueue(new Callback<UrineResultsServerObject>() {
            @Override
            public void onResponse(Call<UrineResultsServerObject> call, Response<UrineResultsServerObject> response) {
                // hideRefreshDialogue();
                String statusCode = response.body().getResponse();
                String message=response.body().getMessage();
                Log.e("codefor3offline","call"+statusCode);
                if(statusCode.equals("3")){
                    String strTestId=response.body().getTest_id();

                    if (isFromOnline) {
                        Log.e("isFromOnline","call"+isFromOnline);

                        UrineResultsDataController.getInstance().addUrineResultsForMember(
                                strTestId,
                                objResults.getMember_id(),
                                objResults.getRelationName(),
                                objResults.getRelationType(),
                                objResults.getTestedTime(),
                                MemberDataController.getInstance().currentMember.getUser_Id(),
                                Integer.parseInt(objResults.getRbcValue()),
                                Double.parseDouble(objResults.getBillirubinValue()),
                                Double.parseDouble(objResults.getUrobiliogen()),
                                Integer.parseInt(objResults.getKetones()),
                                Integer.parseInt(objResults.getProtein()),
                                Double.parseDouble(objResults.getNitrite()),
                                Integer.parseInt(objResults.getGlucose()),
                                Double.parseDouble(objResults.getPh()),
                                Double.parseDouble(objResults.getSg()),
                                Integer.parseInt(objResults.getLeokocit()),
                                true, objResults.getLat(), objResults.getLang());
                    }
                    else
                    {
                        Log.e("testObjIn","call"+existedResult.isFromonline);
                        Log.e("testObjNameIn","call"+existedResult.getRelationName());
                        UrineResultsDataController.getInstance().updateUrineResults(
                                strTestId,
                                objResults.getMember_id(),
                                objResults.getRelationName(),
                                objResults.getRelationType(),
                                objResults.getTestedTime(),
                                MemberDataController.getInstance().currentMember.getUser_Id(),
                                Integer.parseInt(objResults.getRbcValue()),
                                Double.parseDouble(objResults.getBillirubinValue()),
                                Double.parseDouble(objResults.getUrobiliogen()),
                                Integer.parseInt(objResults.getKetones()),
                                Integer.parseInt(objResults.getProtein()),
                                Double.parseDouble(objResults.getNitrite()),
                                Integer.parseInt(objResults.getGlucose()),
                                Double.parseDouble(objResults.getPh()),
                                Double.parseDouble(objResults.getSg()),
                                Integer.parseInt(objResults.getLeokocit()),
                                true, objResults.getLat(), objResults.getLang());
                    }

                }
            }
            @Override
            public void onFailure(Call<UrineResultsServerObject> call, Throwable t) {

            }
        });
    }
    public void updateMembersActiveOfflineData()
    {

        for  (Member member : MemberDataController.getInstance().allMembers)
        {

            Member objMember = member;

            if (!objMember.isFromOnline())
            {
                if (objMember.getMrelationshipname().equals("Me"))
                {
                    executeActiveMemberServerofflineAPI(objMember, true);
                }
                else
                {
                    executeActiveMemberServerofflineAPI(objMember,  false);
                }
            }

        }
    }
    private void executeActiveMemberServerofflineAPI(final Member objMember, Boolean isAdmin)
    {


        Log.e("insertMemberoffline","call");
        MemberServerOject requestBody = new MemberServerOject();

        requestBody.setUser_Id(UserDataController.getInstance().currentUser.getUserName().trim());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Call<MemberServerOject> callable;

        // If Admin
        if(isAdmin){
            Log.e("editMemberInfo","call");
            callable=api.InActiveMemberInfo(requestBody);
        }else {
            // If not Admin
            Log.e("addMemberInfo","call");
            callable = api.InActiveAddMemberInfo(requestBody);
            requestBody.setMember_id(objMember.getMember_Id());
        }

        callable.enqueue(new Callback<MemberServerOject>() {
            @Override
            public void onResponse(Call<MemberServerOject> call, Response<MemberServerOject> response) {

                String statusCode = response.body().getResponse();
                String message=response.body().getMessage();
                Log.e("codefor3memberoffline","call"+statusCode);
                if(statusCode.equals("3")){

                    MemberDataController.getInstance().makeThisMemberAsActive(objMember,true);
                    MemberDataController.getInstance().refreshActiveMember();
                    EventBus.getDefault().post(new SideMenuViewController.MessageEvent("refreshMember"));

                    Log.e("pos",""+objMember.getMember_Id());

                }
            }
            @Override
            public void onFailure(Call<MemberServerOject> call, Throwable t) {

            }
        });
    }
    public void updateLangugeInOffline(){

        Log.e("updateLangugeInOffline","call");

        if (LanguageTextController.getInstance().allLanguageDictionary.size() > 0)
        {
            // update current Language with cloud.
            SharedPreferences languageData= context.getSharedPreferences("language",MODE_PRIVATE);
            String selecteLanguageKey = languageData.getString("offlineLanguage",null);
            Log.e("selecteLanguageKey","call"+selecteLanguageKey);


            if (selecteLanguageKey != null){

                executeLanguageUpdateServerAPI(selecteLanguageKey,false);
            }
        }

    }
    public void executeLanguageUpdateServerAPI(final String offLineLanguage,boolean isFromOnline){
        Log.e("languageData","call"+offLineLanguage);
        LanguageServerObjects requestBody = new LanguageServerObjects();
        requestBody.setLanguage(offLineLanguage);
        requestBody.setUsername(UserDataController.getInstance().currentUser.getUserName());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        ServerApisInterface api = retrofit.create(ServerApisInterface.class);
        Log.e("ServerApisInterface", "call");


        Call<LanguageServerObjects> callable = api.updatePreferLanguage(requestBody);

        callable.enqueue(new Callback<LanguageServerObjects>() {
            @Override
            public void onResponse(Call<LanguageServerObjects> call, Response<LanguageServerObjects> response) {

                String statusCode = response.body().getResponse();
                String message = response.body().getMessage();
                Log.e("codefor3language", "call" + statusCode);
                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3"))
                    {
                        UserDataController.getInstance().currentUser.setPreferedLanguage(offLineLanguage);
                        UserDataController.getInstance().updateUserData(UserDataController.getInstance().currentUser);
                        LanguageTextController.getInstance().currentLanguageDictionary = LanguageTextController.getInstance().allLanguageDictionary.get(offLineLanguage);
                        Log.e("currentLanguageDictionary",""+LanguageTextController.getInstance().currentLanguageDictionary);

                    }
                }
            }

            @Override
            public void onFailure(Call<LanguageServerObjects> call, Throwable t) {

            }
        });
    }
}
