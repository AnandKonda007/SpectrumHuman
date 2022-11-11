package com.example.wave.spectrumhuman.ServerObjects;

import com.example.wave.spectrumhuman.Models.Member;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Rise on 31/10/2017.
 */

public class UserServerObject {


    @SerializedName("prefer_language")
    private String language;

    @SerializedName("attempt_time")
    String attempt_time;

    @SerializedName("username")
    private String userName;

    @SerializedName("password")
    private String password;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("register_type")
    private String register_type;

    @SerializedName("register_time")
    private String registerTime;

    @SerializedName("personal_data")       // fetching personal info data
            ArrayList<MemberServerOject> data;
    //
    @SerializedName("members_data")
    ArrayList<AddMemberServerObject> membersdata;  //fetching members data
    //
    @SerializedName("response")
    String response;
    //
    @SerializedName("message")
    String message;

    @SerializedName("result")

    String result;
    public String getResult() {
        return result;
    }

    @SerializedName("deviceid")
    String deviceid;

    @SerializedName("deviceToken")
    String deviceToken;



    public String getLinked() {
        return linked;
    }

    public void setLinked(String linked) {
        this.linked = linked;
    }

    @SerializedName("Linked")
    private String linked;

    @SerializedName("Currentpassword")
    private String Currentpassword;

    @SerializedName("Newpassword")
    private String Newpassword;

    @SerializedName("otp")
    private  String otpStr;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @SerializedName("to")
    String to;



    @SerializedName("Test_Results")
    ArrayList<UrineResultsServerObject> urinedata;  //fetching urine data

    @SerializedName("deviceinfo")
    ArrayList<DeviceServerObjects> devicesData;  //fetching members data

    @SerializedName("doctor")
    ArrayList<AddDoctorServerObjects> doctorData;  //fetching doctor data

    public void setDoctorData(ArrayList<AddDoctorServerObjects> doctorData) {
        this.doctorData = doctorData;
    }

    public ArrayList<AddDoctorServerObjects> getDoctorData() {
        return doctorData;
    }

    public ArrayList<DeviceServerObjects> getDevicesData() {
        return devicesData;
    }

    public void setDevicesData(ArrayList<DeviceServerObjects> devicesData) {
        this.devicesData = devicesData;
    }



    public ArrayList<UrineResultsServerObject> getUrinedata() {
        return urinedata;
    }

    public void setUrinedata(ArrayList<UrineResultsServerObject> urinedata) {
        this.urinedata = urinedata;
    }




    public String getOtpStr() {return otpStr;}
    public void setOtpStr(String otpStr) {this.otpStr = otpStr;}

    public ArrayList<MemberServerOject> getData() {return data;}
    public void setData(ArrayList<MemberServerOject> data) {this.data = data;}



    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getRegister_type() {return register_type;}
    public void setRegister_type(String register_type) {this.register_type = register_type;}

    public String getRegisterTime() {return registerTime;}
    public void setRegisterTime(String registerTime) {this.registerTime = registerTime;}



    public void setLongitude(String longitude) {this.longitude = longitude;}
    public String getLongitude() {return longitude;}

    public String getLatitude() {return latitude;}
    public void setLatitude(String latitude) {this.latitude = latitude;}

    public String getNewpassword() {return Newpassword;}
    public void setNewpassword(String newpassword) {Newpassword = newpassword;}

    public String getCurrentpassword() {return Currentpassword;}
    public void setCurrentpassword(String currentpassword) {Currentpassword = currentpassword;}

    public void setAttempt_time(String attempt_time) {
        this.attempt_time = attempt_time;
    }
    public String getAttempt_time() {
        return attempt_time;
    }




    public void setResponse(String response) {
        this.response = response;
    }
    public String getResponse() {
        return response;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public ArrayList<AddMemberServerObject> getMembersdata() {
        return membersdata;
    }

    public void setMembersdata(ArrayList<AddMemberServerObject> membersdata) {
        this.membersdata = membersdata;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
