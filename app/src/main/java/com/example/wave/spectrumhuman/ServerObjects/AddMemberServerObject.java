package com.example.wave.spectrumhuman.ServerObjects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by WAVE on 11/2/2017.
 */

public class AddMemberServerObject {
    // ////////////for get add member profilleee pic from server////////////////

    //user_ID
    @SerializedName("user_ID")
    private String user_ID;

    @SerializedName("member_id")
    private String member_id;
    //
    @SerializedName("image")
    private String image;
    //
    @SerializedName("dob")
    private String dob;

    @SerializedName("addedtime")
    private String addedtime;

    @SerializedName("blood_group")
    private String blood_group;

    @SerializedName("gender")
    private String gender;

    @SerializedName("name")
    private String name;

    @SerializedName("relationship")
    private String relationship;

    @SerializedName("weight")
    private String weight;

    @SerializedName("height")
    private String height;

    @SerializedName("email")
    private String mail;

    @SerializedName("isactive")
    private String isActive;


    @SerializedName("Test_Results")
    ArrayList<UrineResultsServerObject> urinedata;  //fetching urine data

    public void setUrinedata(ArrayList<UrineResultsServerObject> urinedata) {
        this.urinedata = urinedata;
    }

    public ArrayList<UrineResultsServerObject> getUrinedata() {
        return urinedata;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }


    public void setHeight(String height) {
        this.height = height;
    }

    public String getHeight() {
        return height;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeight() {
        return weight;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return mail;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDob() {
        return dob;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setAddedtime(String addedtime) {
        this.addedtime = addedtime;
    }

    public String getAddedtime() {
        return addedtime;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
    }

    public String getBlood_group() {
        return blood_group;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_id() {
        return member_id;
    }
}
