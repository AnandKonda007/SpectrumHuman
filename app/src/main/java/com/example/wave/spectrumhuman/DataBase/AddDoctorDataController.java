package com.example.wave.spectrumhuman.DataBase;

import android.util.Log;

import com.example.wave.spectrumhuman.HomeModule.SideMenuViewController;
import com.example.wave.spectrumhuman.Models.DoctorInformationModel;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.greenrobot.eventbus.EventBus;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Rise on 26/12/2017.
 */

public class AddDoctorDataController {


    public ArrayList<DoctorInformationModel> allDoctorData = new ArrayList<DoctorInformationModel>();

    public DoctorInformationModel currentDoctor;

    public static AddDoctorDataController myObj;

    public static AddDoctorDataController getInstance() {
        if (myObj == null) {
            myObj = new AddDoctorDataController();
        }
        return myObj;
    }
    public void fetchDoctorInfo()
    {

        if (UserDataController.getInstance().currentUser != null)
        {
            Log.e("DoctorFetch","call");

            ArrayList<DoctorInformationModel> objDoctorList =new ArrayList<DoctorInformationModel>( UserDataController.getInstance().currentUser.getDoctors());
            if(objDoctorList.size()>0)
            {

                allDoctorData = objDoctorList;
                currentDoctor=objDoctorList.get(0);
            }
        }
    }
    //Insert the Doctor Information
    public void insertDoctorInformation(String addedTime,String address,String doctorid,
                                        String email,String found,String latitude,String longitude,
                                        String name,String phonenumber,String specilization,String doctorStatus ) {
        Log.e("insertAddDoctor","call"+UserDataController.getInstance().currentUser);

        DoctorInformationModel doctorInformation = new DoctorInformationModel(UserDataController.getInstance().currentUser, addedTime,address,doctorid, email,found,latitude,longitude, name,phonenumber,specilization,doctorStatus );

        try {
            UserDataController.getInstance().helper.getDoctorInformationdao().create(doctorInformation);
            Log.e("insertAddDoctor","call"+latitude+"cal"+longitude);

        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("SQLException","call");

        }
    }
    /////Delete Doctor Information
    public void deleteAddDoctorsData(ArrayList<DoctorInformationModel> doctorInfoList )
    {
        Log.e("deleteAddDoctorsData","call");
        try{

            DeleteBuilder<DoctorInformationModel, Integer> deleteBuilder = UserDataController.getInstance().helper.getDoctorInformationdao().deleteBuilder();
            deleteBuilder.delete();
            Log.e("DeleteDoctorsData","delete doctor data");
            currentDoctor = null;

            //  fetchDoctorInfo();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updateAddDoctorInformation(String addedTime,String address,String doctorid,
                                           String email,String found,String latitude,String longitude,
                                           String name,String phonenumber,String specilization,String doctorStatus)
    {
        try
        {
            UpdateBuilder<DoctorInformationModel,Integer> updateBuilder = UserDataController.getInstance().helper.getDoctorInformationdao().updateBuilder();
            updateBuilder.updateColumnValue("addedTime",addedTime);
            updateBuilder.updateColumnValue("address",address);
            updateBuilder.updateColumnValue("email",email);
            updateBuilder.updateColumnValue("found",found);
            updateBuilder.updateColumnValue("latitude",latitude);
            updateBuilder.updateColumnValue("longitude",longitude);
            updateBuilder.updateColumnValue("name",name);
            updateBuilder.updateColumnValue("phonenumber",phonenumber);
            updateBuilder.updateColumnValue("specilization",specilization);
            updateBuilder.updateColumnValue("doctorstatus",doctorStatus);
            updateBuilder.where().eq("doctorid",doctorid);
            updateBuilder.update();
            Log.e("update","update doctor information"+doctorStatus);
            fetchDoctorInfo();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public  void  updateDoctorStatus(String msg) {

        if (UserDataController.getInstance().currentUser != null) {
            if (currentDoctor != null) {
                DoctorInformationModel informationModel = currentDoctor;

                updateAddDoctorInformation(
                        informationModel.getAddedtime(), informationModel.getAddress(),
                        informationModel.getDoctorid(), informationModel.getEmail(), informationModel.getFound(), informationModel.getLatitude(),
                        informationModel.getLongitude(), informationModel.getName(),
                        informationModel.getPhonenumber(), informationModel.getSpecilization(), msg);

                fetchDoctorInfo();
            }
        }

    }

    public void loadNotificationString(String notifictionStr) {
        Log.e("notificationStr", "" + notifictionStr);

        if (notifictionStr != null) {
            String msg = "";
            Log.e("notificationStr", "" + notifictionStr);

            if (notifictionStr.contains("accepted")) {
                msg = "accepted";

            } else if (notifictionStr.contains("rejected")) {
                msg = "rejected";

            } else if (notifictionStr.contains("pending")) {
                msg = "pending";
            }

            Log.e("notification", "" + msg);
            updateDoctorStatus(msg);
            EventBus.getDefault().post(new SideMenuViewController.MessageEvent("refreshDoctorInfo;" + msg));
        }
    }
}
