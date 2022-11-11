package com.example.wave.spectrumhuman.DataBase;

import android.util.Log;

import com.example.wave.spectrumhuman.Models.DeviceInformation;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 28-09-2017.
 */

public class DeviceDataController
{

    public ArrayList<DeviceInformation> allDevices = new ArrayList<>();
    public  DeviceInformation currentDevice;

    public static DeviceDataController myObj;

    public static DeviceDataController getInstance() {
        if (myObj == null) {
            myObj = new DeviceDataController();
        }
        return myObj;
    }

    //Fetching all the member data
    public ArrayList<DeviceInformation> fetchDeviceInformation() {
        allDevices = new ArrayList<>();
        ArrayList<DeviceInformation> dbDevices =  new ArrayList<DeviceInformation>(UserDataController.getInstance().currentUser.getDeviceInformations());
        if(dbDevices != null)
        {
            allDevices = dbDevices;
            getConnectedDeviceAsCurrentDevice();
        }

        return allDevices;
    }

    public void  getConnectedDeviceAsCurrentDevice(){

        for(DeviceInformation objDeviceInfo:allDevices)
        {

            if(objDeviceInfo.getIsconnected() == true)
            {
                currentDevice = objDeviceInfo;
            }

        }

    }

  //inserting the deviceInformation
   public boolean insertDeviceInformation(String deviceName,String deviceId,String batteryPercentage,String firmwareversion,boolean isconnected,String secureType) {
       DeviceInformation deviceInformation1 = new DeviceInformation(UserDataController.getInstance().currentUser, deviceName, deviceId, batteryPercentage, firmwareversion, isconnected,secureType);

       try {
           UserDataController.getInstance().helper.getDeviceInformationdao().create(deviceInformation1);
           Log.e("device Information", "device information inserted");
           fetchDeviceInformation();
           return true;
       } catch (SQLException e) {
           e.printStackTrace();
           return false;
       }

   }


    //Deleting all devices  in database
    public void deleteAllDeviceInformation(List<DeviceInformation> devices_list )
    {
        try{
            UserDataController.getInstance().helper.getDeviceInformationdao().delete(devices_list);
            Log.e("Delete","delete all members");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public boolean deleteDeviceData(DeviceInformation deviceInfo )
    {
        try{
            UserDataController.getInstance().helper.getDeviceInformationdao().delete(deviceInfo);
            Log.e("Delete","delete all members");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public  boolean isDeviceIDExisted(String deviceId){

        for (DeviceInformation objDeviceInfo:allDevices){

            if(objDeviceInfo.getDeviceId().equals(deviceId))
            {
                return  true;
            }
        }

        return  false;

    }

    public  DeviceInformation getDeviceInfoForId(String deviceId)
    {
        for (DeviceInformation objDeviceInfo:allDevices){

            if(objDeviceInfo.getDeviceId().equals(deviceId))
            {
                return  objDeviceInfo;
            }
        }

        return  null;

    }



  //update Device Information
    public Boolean updateDeviceInformation(String deviceName,String deviceId,String batteryPercentage,String firmwareversion,boolean isconnected,String secureType)
    {
        try
        {
            UpdateBuilder<DeviceInformation,Integer> updateBuilder = UserDataController.getInstance().helper.getDeviceInformationdao().updateBuilder();
            updateBuilder.updateColumnValue("deviceName",deviceName);
            updateBuilder.updateColumnValue("deviceId",deviceId);
            updateBuilder.updateColumnValue("batteryPercentage",batteryPercentage);
            updateBuilder.updateColumnValue("firmwareVersion",firmwareversion);
            updateBuilder.updateColumnValue("isConnected",isconnected);
            updateBuilder.updateColumnValue("secureType",secureType);
            updateBuilder.where().eq("deviceId",deviceId);
            updateBuilder.update();
            fetchDeviceInformation();
            Log.e("update","update device information");
            return  true;
        } catch (SQLException e) {
            e.printStackTrace();
            return  false;
        }

    }

}
