package com.example.wave.spectrumhuman.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by WAVE on 10/25/2017.
 */
public class ResponseModel {
    @SerializedName("response")
    String response;
    //
    @SerializedName("message")
    String message;


    @SerializedName("personal_data")
    ArrayList<Member> data;
    //


    public ArrayList<Member> getData() {
        return data;
    }

    public void setData(ArrayList<Member> data) {
        this.data = data;
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



}
