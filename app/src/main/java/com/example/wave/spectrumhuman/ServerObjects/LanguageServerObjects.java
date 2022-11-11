package com.example.wave.spectrumhuman.ServerObjects;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rise on 30/11/2017.
 */

public class LanguageServerObjects {


    @SerializedName("prefer_language")
    private String language;


    @SerializedName("username")
    private  String username;



    @SerializedName("languages")
    private HashMap<String,HashMap> languages;



    @SerializedName("response")
    String response;
    //
    @SerializedName("message")
    String message;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getResponse() {return response;}

    public void setResponse(String response) {this.response = response;}

    public String getMessage() {return message;}

    public void setMessage(String message) {this.message = message;}

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    public HashMap<String,HashMap> getLanguages() {return languages;
    }

    public void setLanguages(HashMap languages) {this.languages = languages;
    }



}
