package com.example.wave.spectrumhuman.LoginModule;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.wave.spectrumhuman.DataBase.AddDoctorDataController;
import com.example.wave.spectrumhuman.DataBase.DeviceDataController;
import com.example.wave.spectrumhuman.DataBase.MemberDataController;
import com.example.wave.spectrumhuman.DataBase.UrineResultsDataController;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.HomeModule.SideMenuViewController;
import com.example.wave.spectrumhuman.Models.Member;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.ServerObjects.AddDoctorServerObjects;
import com.example.wave.spectrumhuman.ServerObjects.AddMemberServerObject;
import com.example.wave.spectrumhuman.ServerObjects.DeviceServerObjects;
import com.example.wave.spectrumhuman.ServerObjects.MemberServerOject;
import com.example.wave.spectrumhuman.ServerObjects.UrineResultsServerObject;
import com.example.wave.spectrumhuman.ServerObjects.UserServerObject;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
/**
 * Created by WAVE on 11/20/2017.
 */
public class LoginServerObjectDataController {

    public static LoginServerObjectDataController myObj;
    Boolean isAnyActiveMember = false;
    Context context;

    Boolean isMovetoPersonalInfo = false;
    Boolean isMovetoHome = false;


    public static LoginServerObjectDataController getInstance() {
        if (myObj == null) {
            myObj = new LoginServerObjectDataController();
        }
        return myObj;
    }

    public void  fillContent(Context context1)
    {
        context = context1;

    }


    public void processUserData(UserServerObject response){
        // Fetch Personal Information Data
        ArrayList<MemberServerOject> personalInfoList = response.getData();
        // Fetch Members  Information Data
        final ArrayList<AddMemberServerObject>  objMemberList= response.getMembersdata();
        final ArrayList<UrineResultsServerObject> adminUrineResults= response.getUrinedata();

        final ArrayList<DeviceServerObjects> devicesListResults= response.getDevicesData();

        ArrayList<AddDoctorServerObjects> doctorModelArrayList = response.getDoctorData();


        Log.e("MembersArray", ""+objMemberList);
        Log.e("usersArray", ""+personalInfoList);

        Log.e("devicesArray", ""+devicesListResults);

        Log.e("adminUrineResults", ""+response.getUrinedata());

        Log.e("doctorModelArrayList", ""+response.getDoctorData());

        // Process PersonalInformation
        if (personalInfoList.size() > 0) {
            Log.e("personalInfoList.size()", ""+personalInfoList.get(0).getEmail());

            processPersonalInformationData(personalInfoList,adminUrineResults,objMemberList);

        }
        else
        {

            isMovetoPersonalInfo = true;
            //EventBus.getDefault().post(new SideMenuViewController.MessageEvent("moveToPersonalInfo"));
            //return;
        }

        // Process MembersData
        if(objMemberList != null && objMemberList.size()>0) {
            processMembersData(objMemberList);
        }
        else {

            isMovetoHome = true;
         //   EventBus.getDefault().post(new SideMenuViewController.MessageEvent("moveToHome"));
        }

        // Process deviceList
        processDevicesData(devicesListResults);

        //process doctor data
        processDoctorData(doctorModelArrayList);

    }
    public void processDoctorData(ArrayList<AddDoctorServerObjects> doctorArray)
    {
        if (doctorArray.size()>0)
        {
             for (AddDoctorServerObjects objDOctorsInfo : doctorArray)
            {
                AddDoctorDataController.getInstance().insertDoctorInformation(
                        objDOctorsInfo.getAddedtime(),objDOctorsInfo.getAddress(),
                        objDOctorsInfo.getDoctor_id(),objDOctorsInfo.getEmail(),
                        objDOctorsInfo.getFound(),objDOctorsInfo.getLoc().get(0),
                        objDOctorsInfo.getLoc().get(1),objDOctorsInfo.getName(),
                        objDOctorsInfo.getPhone(), objDOctorsInfo.getSpecalization(),
                        objDOctorsInfo.getInvitation());
                Log.e("DoctorInfo", "doctorInserted");

            }

        }

    }
    public  void  processDevicesData(ArrayList<DeviceServerObjects> devicesArray)
    {

        if(devicesArray.size() > 0) {

            for (DeviceServerObjects objDeviceInfo : devicesArray) {

                if (DeviceDataController.getInstance().insertDeviceInformation(objDeviceInfo.getSpectrometername(), objDeviceInfo.getId(), objDeviceInfo.getBattery_percentage(), objDeviceInfo.getSpectrometer_version(), false, objDeviceInfo.getSecureType())) {

                    Log.e("DeviceInfo", "DeviceInserted" + objDeviceInfo.getSpectrometername());

                }

            }
        }
    }
    public  void  processPersonalInformationData(ArrayList<MemberServerOject> personalInfoList, final ArrayList<UrineResultsServerObject> adminUrineResults, final ArrayList<AddMemberServerObject>  objMemberList) {

        final MemberServerOject objMember = personalInfoList.get(0);
        final String urlString = ServerApisInterface.home_Image_URL + personalInfoList.get(0).getProfileUrl();

        Log.e("PersonalURL",urlString);


        // run code
        try {
            URL url = new URL(urlString);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            setPersonalInfoData(objMember, byteArray, adminUrineResults);

            if (objMemberList == null || objMemberList.size() == 0) {
                Log.e("adminUrineResults", "moveToHome");
                isMovetoHome = true;
                Log.e("isMovetoPersonalInfo", "call"+LoginServerObjectDataController.getInstance().isMovetoPersonalInfo);
                Log.e("isMovetoHome", "call"+LoginServerObjectDataController.getInstance().isMovetoHome);

                //   EventBus.getDefault().post(new SideMenuViewController.MessageEvent("moveToHome"));
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    public  void  processMembersData( final ArrayList<AddMemberServerObject>  objMemberList){

        // Process Members Data
        for (int i = 0; i < objMemberList.size(); i++) {

            Boolean isLast = false;

            if (i == objMemberList.size()-1)
            {

                isLast = true;
            }

            insertMemberData(objMemberList.get(i),isLast);
        }

        Member adminMember = MemberDataController.getInstance().getAdminDetails();
        MemberDataController.getInstance().makeThisMemberAsInActive(adminMember);
        MemberDataController.getInstance().fetchMemberData();

        isMovetoHome = true;
        //EventBus.getDefault().post(new SideMenuViewController.MessageEvent("moveToHome"));

    }
    public void  setPersonalInfoData(MemberServerOject serverOject, byte[] byteArray,ArrayList<UrineResultsServerObject> adminUserResults){

        Log.e("adminUrineResults",""+adminUserResults);

        Member member=new Member();
        member.setMname(serverOject.getMname());
        member.setUser_Id(UserDataController.getInstance().currentUser.getUserName());
        member.setEmail(member.getUser_Id());
        member.setMbirthday(serverOject.getMbirthday());
        member.setMrelationshipname("Me");
        member.setMgender(serverOject.getMgender());
        member.setMheight(serverOject.getMheight());
        member.setMweight(serverOject.getMweight());
        member.setMbloodgroup(serverOject.getMbloodgroup());
        member.setMprofilepicturepath(byteArray);
        member.setActiveMember(true);
        member.setMember_Id("admin");

        if (serverOject.getEmail() != null)
        {
            member.setEmail(serverOject.getEmail());
        }

        Log.e("profilePic",""+member.getMprofilepicturepath());

        Log.e("Personal_email",""+member.getEmail());
        Log.e("Personal_ID",""+member.getUser_Id());

        if( MemberDataController.getInstance().insertMemberData(UserDataController.getInstance().currentUser.getUserName(),member.getMember_Id(),member.getMname(),member.getEmail(),
                member.getMbirthday(),member.getMrelationshipname(),member.getMgender(),member.getMheight(),
                member.getMweight(), member.getMbloodgroup(),member.getMprofilepicturepath(),member.getActiveMember(),true))
        {
            // Insert Admin Urine Results Data
            if (adminUserResults != null && adminUserResults.size() > 0) {
                for (int i = 0; i < adminUserResults.size(); i++) {
                    insertUrineData(adminUserResults.get(i),member.getMname(),member.getMrelationshipname(),member.getUser_Id());

                }

            }

        }
    }
    private  void insertMemberData (final AddMemberServerObject addMemberServerOjects, final Boolean isLast){

        Log.e("getUserName", "call" + addMemberServerOjects.getUser_ID());
        Log.e("getMail", "call" + addMemberServerOjects.getMail());
        Log.e("Relationship", "call" + addMemberServerOjects.getRelationship());

        try {
            String memberUrl = ServerApisInterface.home_Image_URL + addMemberServerOjects.getImage();
            Log.e("getUserName", "call" + addMemberServerOjects.getUser_ID());
            Log.e("getMail", "call" + addMemberServerOjects.getMail());
            Log.e("Relationship", "call" + addMemberServerOjects.getRelationship());
            URL url = new URL(memberUrl);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Boolean isActive = false;
            if (addMemberServerOjects.getIsActive().equals("1"))
            {
                isAnyActiveMember = true;
                isActive = true;
            }

            if(MemberDataController.getInstance().insertMemberData(addMemberServerOjects.getUser_ID(),
                    addMemberServerOjects.getMember_id(),
                    addMemberServerOjects.getName(), addMemberServerOjects.getMail(), addMemberServerOjects.getDob(),
                    addMemberServerOjects.getRelationship(), addMemberServerOjects.getGender(),
                    addMemberServerOjects.getHeight(), addMemberServerOjects.getWeight(), addMemberServerOjects.getBlood_group(),
                    byteArray, isActive,true)) {
                Log.e("Member_id", "call" + addMemberServerOjects.getMember_id());
                Log.e("byteArrayMember", "call" + byteArray);
                Log.e("Member_id1", "call" + addMemberServerOjects.getUser_ID());
                //// fetch  urine data fro server
                ArrayList<UrineResultsServerObject> objurineResults = addMemberServerOjects.getUrinedata();

                Log.e("urineResultsMember", "call" + objurineResults);

                if (objurineResults != null && objurineResults.size() > 0) {

                    for (int i = 0; i < objurineResults.size(); i++) {
                        insertUrineData(objurineResults.get(i),addMemberServerOjects.getName(),addMemberServerOjects.getRelationship(),addMemberServerOjects.getMail());
                    }

                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    }
    public void insertUrineData(final UrineResultsServerObject resultsServerObject,String realtionName,String relationType,String relativeEmailId){


        Log.e("urineRelationship", "call" + relationType);
        Log.e("urineRBC", "call" + resultsServerObject.getRbcValue());
        Log.e("urineMember", "call" + resultsServerObject.getMember_id());

        UrineResultsDataController.getInstance().addUrineResultsForMember
                (resultsServerObject.getTest_id(),
                        resultsServerObject.getMember_id(),
                        realtionName,
                        relationType,
                        resultsServerObject.getTestedTime(),
                        relativeEmailId,
                        Integer.parseInt(resultsServerObject.getRbcValue()),
                        Double.parseDouble(resultsServerObject.getBillirubinValue()),
                        Double.parseDouble(resultsServerObject.getUrobiliogen()),
                        Integer.parseInt(resultsServerObject.getKetones()),
                        Integer.parseInt(resultsServerObject.getProtein()),
                        Double.parseDouble( resultsServerObject.getNitrite()),
                        Integer.parseInt(resultsServerObject.getGlucose()),
                        Double.parseDouble(resultsServerObject.getPh()),
                        Double.parseDouble(resultsServerObject.getSg()),
                        Integer.parseInt(resultsServerObject.getLeokocit()),
                        true,resultsServerObject.getLat(),resultsServerObject.getLang());


    }
    /*public void insertUrineData(final UrineResultsServerObject resultsServerObject,String realtionName,String relationType,String relativeEmailId){


        Log.e("urineRelationship", "call" + relationType);
        Log.e("urineRBC", "call" + resultsServerObject.getRbcValue());
        Log.e("urineMember", "call" + resultsServerObject.getMember_id());


        UrineResultsDataController.getInstance().addUrineResultsForMember
                (resultsServerObject.getTest_id(),
                        resultsServerObject.getMember_id(),
                        realtionName,
                        relationType,
                        resultsServerObject.getTestedTime(),
                        relativeEmailId,
                        Integer.parseInt(resultsServerObject.getRbcValue()),
                        Double.parseDouble(resultsServerObject.getBillirubinValue()),
                        Double.parseDouble(resultsServerObject.getUrobiliogen()),
                        Integer.parseInt(resultsServerObject.getKetones()),
                        Integer.parseInt(resultsServerObject.getProtein()),
                        Double.parseDouble( resultsServerObject.getNitrite()),
                        Integer.parseInt(resultsServerObject.getGlucose()),
                        Double.parseDouble(resultsServerObject.getPh()),
                        Double.parseDouble(resultsServerObject.getSg()),
                        Integer.parseInt(resultsServerObject.getLeokocit()));

    }*/


}
