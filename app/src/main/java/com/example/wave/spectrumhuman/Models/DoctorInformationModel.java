package com.example.wave.spectrumhuman.Models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Rise on 26/12/2017.
 */

public class DoctorInformationModel {
public DoctorInformationModel(){

}

    public DoctorInformationModel(User user,String addedTime, String address, String doctorId,
                                  String email, String found, String latitude, String longitude,
                                  String name, String phonenumber, String specilization ,String doctorStatus){

        this.user=user;
        this.addedtime=addedTime;
        this.address=address;
        this.doctorid=doctorId;
        this.email=email;
        this.found=found;
        this.latitude=latitude;
        this.longitude=longitude;
        this.name=name;
        this.phonenumber=phonenumber;
        this.specilization=specilization;
        this.doctorStatus=doctorStatus;

    }
    @DatabaseField(columnName = "doctorstatus")
    private String doctorStatus;

    @DatabaseField(columnName = "addedtime")
    private String addedtime;

    @DatabaseField(columnName = "address")
    private String address;

    @DatabaseField(columnName = "doctorid")
    private String doctorid;

    @DatabaseField(columnName = "email")
    private String email;

    @DatabaseField(columnName = "found")
    private String found;

    @DatabaseField(columnName = "latitude")
    private String latitude;

    @DatabaseField(columnName = "longitude")
    private String longitude;

    @DatabaseField(columnName = "name")
    private String name;

    @DatabaseField(columnName = "phonenumber")
    private String phonenumber;


    @DatabaseField(columnName = "specilization")
    private String specilization;


    @DatabaseField(columnName = "user_id", canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getAddedtime() {
        return addedtime;
    }

    public void setAddedtime(String addedtime) {
        this.addedtime = addedtime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDoctorid() {
        return doctorid;
    }

    public void setDoctorid(String doctorid) {
        this.doctorid = doctorid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFound() {
        return found;
    }

    public void setFound(String found) {
        this.found = found;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getSpecilization() {
        return specilization;}

    public void setSpecilization(String specilization) {this.specilization = specilization;}

    public String getDoctorStatus() {
        return doctorStatus;
    }

    public void setDoctorStatus(String doctorStatus) {
        this.doctorStatus = doctorStatus;
    }



}
