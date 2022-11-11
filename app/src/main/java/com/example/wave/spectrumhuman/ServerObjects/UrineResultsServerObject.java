package com.example.wave.spectrumhuman.ServerObjects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WAVE on 11/13/2017.
 */

public class UrineResultsServerObject {
    @SerializedName("username")
    private  String Mail;

    @SerializedName("rbcValue")
    private String rbcValue;

    @SerializedName("billirubinValue")
    private String billirubinValue;

    @SerializedName("urobiliogen")
    private String urobiliogen;

    @SerializedName("ketones")
    private String ketones;

    @SerializedName("protein")
    private String protein;

    @SerializedName("nitrite")
    private String nitrite;

    @SerializedName("glucose")
    private String glucose;

    @SerializedName("ph")
    private String ph;

    @SerializedName("sg")
    private String sg;

    @SerializedName("leokocit")
    private String leokocit;

    @SerializedName("relationName")
    private String relationName;

    @SerializedName("relationType")
    private String relationType;

    @SerializedName("testedTime")
    private String testedTime;

    @SerializedName("latitude")
    private String lat;

    @SerializedName("longitude")
    private String lang;

    @SerializedName("response")
    String response;
    //
    @SerializedName("message")
    String message;

    @SerializedName("member_id")
    String member_id;

    @SerializedName("test_id")
    String test_id;


    public String getTest_id() {
        return test_id;
    }

    public void setTest_id(String test_id) {
        this.test_id = test_id;
    }




    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getMember_id() {
        return member_id;
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

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getPh() {
        return ph;
    }

    public void setLeokocit(String leokocit) {
        this.leokocit = leokocit;
    }

    public String getLeokocit() {
        return leokocit;
    }

    public void setSg(String sg) {
        this.sg = sg;
    }

    public String getSg() {
        return sg;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getLang() {
        return lang;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLat() {
        return lat;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setTestedTime(String testedTime) {
        this.testedTime = testedTime;
    }

    public String getTestedTime() {
        return testedTime;
    }

    public void setNitrite(String nitrite) {
        this.nitrite = nitrite;
    }

    public String getNitrite() {
        return nitrite;
    }

    public void setGlucose(String glucose) {
        this.glucose = glucose;
    }

    public String getGlucose() {
        return glucose;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getProtein() {
        return protein;
    }

    public void setKetones(String ketones) {
        this.ketones = ketones;
    }

    public String getKetones() {
        return ketones;
    }

    public void setUrobiliogen(String urobiliogen) {
        this.urobiliogen = urobiliogen;
    }

    public String getUrobiliogen() {
        return urobiliogen;
    }

    public void setBillirubinValue(String billirubinValue) {
        this.billirubinValue = billirubinValue;
    }

    public String getBillirubinValue() {
        return billirubinValue;
    }

    public void setRbcValue(String rbcValue) {
        this.rbcValue = rbcValue;
    }

    public String getRbcValue() {
        return rbcValue;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public String getMail() {
        return Mail;
    }
}
