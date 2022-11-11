package com.example.wave.spectrumhuman.ServerObjects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Rise on 26/12/2017.
 */

public class AddDoctorServerObjects {

    @SerializedName("username")
    private String userName;


    @SerializedName("test_id")
    private String testId;

    @SerializedName("member_id")
    private String member_id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("specalization")
    private String specalization;

    @SerializedName("address")
    private String address;

    @SerializedName("addedtime")
    private String addedtime;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("found")
    private String found;

    @SerializedName("doc_id")
   private String doctor_id;

    @SerializedName("invitation")
    private String invitation;
    //

    @SerializedName("response")
    String response;


    //
    @SerializedName("message")
    String message;




    @SerializedName("loc")
    private ArrayList<String> loc;

    public ArrayList<String> getLoc() {
        return loc;
    }

    public void setLoc(ArrayList<String> loc) {
        this.loc = loc;
    }


    public String getInvitation() {
        return invitation;
    }

    public void setInvitation(String invitation) {
        this.invitation = invitation;
    }
    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTestId() {
        return testId;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getSpecalization() {return specalization;}
    public void setSpecalization(String specalization) {this.specalization = specalization;}

    public String getAddress() {return address;}
    public void setAddress(String address) {this.address = address;}

    public String getAddedtime() {return addedtime;}
    public void setAddedtime(String addedtime) {this.addedtime = addedtime;}

    public String getLatitude() {return latitude;}
    public void setLatitude(String latitude) {this.latitude = latitude;}

    public String getLongitude() {return longitude;}
    public void setLongitude(String longitude) {this.longitude = longitude;}

    public String getFound() {return found;}
    public void setFound(String found) {this.found = found;}


    public String getResponse() {return response;}
    public void setResponse(String response) {this.response = response;}

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {this.message = message;}


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
