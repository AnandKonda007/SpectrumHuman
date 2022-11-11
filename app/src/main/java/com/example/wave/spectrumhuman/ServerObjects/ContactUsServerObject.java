package com.example.wave.spectrumhuman.ServerObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacobliu on 11/1/17.
 */

public class ContactUsServerObject {

    ///////contact us keys///////////////

    @SerializedName("username")
    private String userName;


    @SerializedName("EMail")
    private String EMail;

    @SerializedName("feedback")
    private String feedback;

    @SerializedName("name")
    private String name;

    //
    @SerializedName("response")
    String response;
    //
    @SerializedName("message")
    String message;



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEMail() {
        return EMail;
    }

    public void setEMail(String email) {
        EMail = email;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }




}
