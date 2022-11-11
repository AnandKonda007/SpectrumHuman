package com.example.wave.spectrumhuman.ServerObjects;

import com.example.wave.spectrumhuman.Models.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rise on 08/11/2017.
 */

public class DeviceServerObjects {




    @SerializedName("username")
    private String userName;


    @SerializedName("id")
    private  String id ;

    @SerializedName("Battery_percentage")
    private  String battery_percentage;

    @SerializedName("spectrometer_information")
    private  String spectrometer_information;

    @SerializedName("last_sync")
    private  String last_sync;

    @SerializedName("spectrometer_version")
    private  String spectrometer_version;



    @SerializedName("spectrometer_name")
    private  String spectrometername;

    //
    @SerializedName("response")
    String response;
    //
    @SerializedName("message")
    String message;

    @SerializedName("secureType")
    String secureType;




    public String getSecureType() {
        return secureType;
    }

    public void setSecureType(String secureType) {
        this.secureType = secureType;
    }



    public String getSpectrometername() {
        return spectrometername;
    }

    public void setSpectrometername(String spectrometername) {
        this.spectrometername = spectrometername;
    }

    public String getResponse() {return response;}
    public void setResponse(String response) {this.response = response;}

    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}

    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getBattery_percentage() {return battery_percentage;}
    public void setBattery_percentage(String battery_percentage) {this.battery_percentage = battery_percentage;}

    public String getSpectrometer_information() {return spectrometer_information;}
    public void setSpectrometer_information(String spectrometer_information) {this.spectrometer_information = spectrometer_information;}

    public String getLast_sync() {return last_sync;}
    public void setLast_sync(String last_sync) {this.last_sync = last_sync;}

    public String getSpectrometer_version() {return spectrometer_version;}
    public void setSpectrometer_version(String spectrometer_version) {this.spectrometer_version = spectrometer_version;}
}
