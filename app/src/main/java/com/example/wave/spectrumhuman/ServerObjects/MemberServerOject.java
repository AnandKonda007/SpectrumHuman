package com.example.wave.spectrumhuman.ServerObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rise on 31/10/2017.
 */

public class MemberServerOject {
    @SerializedName("Name")
    private String mname;

    @SerializedName("username")
    private String user_Id;

    @SerializedName("member_id")
    private String member_id;

    @SerializedName("Email")
    private String email;

    @SerializedName("DOB")
    private String mbirthday;

    @SerializedName("Gender")
    private String mgender;


    @SerializedName("Height")
    private String mheight;

    @SerializedName("Weight")
    private String mweight;


    @SerializedName("Blood_Group")
    private String mbloodgroup;


    @SerializedName("personInfo_img")
    private String profilepicturepath;

    //get image from server
    @SerializedName("profilepic")
    private String profileUrl;

    @SerializedName("AddedTime")
    private String time;

    //
    @SerializedName("response")
    private String response;
    //
    @SerializedName("message")
    private  String message;
    //

    @SerializedName("Relationship")
    private String mrelationshipname;

    public String getMrelationshipname() {return mrelationshipname;}

    public void setMrelationshipname(String mrelationshipname) {this.mrelationshipname = mrelationshipname;}

    public String getMember_id() {return member_id;}

    public void setMember_id(String memberid) {this.member_id = memberid;}

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
    public String getProfileUrl() {
        return profileUrl;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setResponse(String response) {
        this.response = response;
    }
    public String getResponse() {
        return response;
    }

    public void setProfilepicturepath(String profilepicturepath) {this.profilepicturepath = profilepicturepath;}
    public String getProfilepicturepath() {
        return profilepicturepath;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getTime() {
        return time;
    }

    public String getMname() {return mname;}
    public void setMname(String mname) {this.mname = mname;}


    public String getUser_Id() {return user_Id;}
    public void setUser_Id(String user_Id) {this.user_Id = user_Id;}


    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getMbirthday() {return mbirthday;}
    public void setMbirthday(String mbirthday) {this.mbirthday = mbirthday;}

    public String getMgender() {return mgender;}
    public void setMgender(String mgender) {this.mgender = mgender;}

    public String getMheight() {return mheight;}
    public void setMheight(String mheight) {this.mheight = mheight;}

    public String getMweight() {return mweight;}
    public void setMweight(String mweight) {this.mweight = mweight;}

    public String getMbloodgroup() {return mbloodgroup;}
    public void setMbloodgroup(String mbloodgroup) {this.mbloodgroup = mbloodgroup;}

}
