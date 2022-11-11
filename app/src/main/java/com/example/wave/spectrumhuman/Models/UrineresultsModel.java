package com.example.wave.spectrumhuman.Models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by dell on 03-10-2017.
 */

public class UrineresultsModel
{
    public UrineresultsModel(){

    }
    public UrineresultsModel(Member member,String testId,String member_Id,String relation_name, String relationtype, String testdata, String relativeemailid, int rbc, double bilirubin, double urobilinozen, int ketones, int protein, double nitrite, int glucose, double ph, double sg, int leucocyte,boolean isFromonline,String latitude,String longitude) {
        this.relationName = relation_name;
        this.relationtype = relationtype;
        this.testedTime = testdata;
        this.relativeemailid = relativeemailid;
        this.rbcValue = rbc;
        this.billirubinValue = bilirubin;
        this.UroboliogenValue = urobilinozen;
        this.KetonesValue = ketones;
        this.ProteinValue = protein;
        this.NitriteValue = nitrite;
        this.GlucoseValue = glucose;
        this.PhValue = ph;
        this.SgValue = sg;
        this.LeucocyteValue = leucocyte;
        this.member_Id=member_Id;
        this.test_id=testId;
        this.member=member;
        this.isFromonline=isFromonline;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public boolean isFromonline() {
        return isFromonline;
    }

    public void setFromonline(boolean fromonline) {
        isFromonline = fromonline;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public String getRelationName() {
        return relationName;
    }

    public String getRelationtype() {
        return relationtype;
    }

    public void setRelationtype(String relationtype) {
        this.relationtype = relationtype;
    }

    public void setTestedTime(String testedTime) {
        this.testedTime = testedTime;
    }

    public String getTestedTime() {
        return testedTime;
    }

    public String getRelativeemailid() {
        return relativeemailid;
    }

    public void setRelativeemailid(String relativeemailid) {
        this.relativeemailid = relativeemailid;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRbcValue(int rbcValue) {
        this.rbcValue = rbcValue;
    }

    public int getRbcValue() {
        return rbcValue;
    }

    public void setBillirubinValue(double billirubinValue) {
        this.billirubinValue = billirubinValue;
    }

    public double getBillirubinValue() {
        return billirubinValue;
    }

    public void setGlucoseValue(int glucoseValue) {
        GlucoseValue = glucoseValue;
    }

    public int getGlucoseValue() {
        return GlucoseValue;
    }

    public void setKetonesValue(int ketonesValue) {
        KetonesValue = ketonesValue;
    }

    public int getKetonesValue() {
        return KetonesValue;
    }

    public void setLeucocyteValue(int leucocyteValue) {
        LeucocyteValue = leucocyteValue;
    }

    public int getLeucocyteValue() {
        return LeucocyteValue;
    }

    public void setNitriteValue(double nitriteValue) {
        NitriteValue = nitriteValue;
    }

    public double getNitriteValue() {
        return NitriteValue;
    }

    public void setPhValue(double phValue) {
        PhValue = phValue;
    }

    public double getPhValue() {
        return PhValue;
    }

    public void setProteinValue(int proteinValue) {
        ProteinValue = proteinValue;
    }

    public int getProteinValue() {
        return ProteinValue;
    }

    public void setSgValue(double sgValue) {
        SgValue = sgValue;
    }

    public double getSgValue() {
        return SgValue;
    }

    public void setUroboliogenValue(double uroboliogenValue) {
        UroboliogenValue = uroboliogenValue;
    }

    public double getUroboliogenValue() {
        return UroboliogenValue;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    public void setMember_Id(String member_Id) {
        this.member_Id = member_Id;
    }

    public String getMember_Id() {
        return member_Id;
    }

    public String getTest_id() {
        return test_id;
    }

    public void setTest_id(String test_id) {
        this.test_id = test_id;
    }

    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField(columnName = "relationName")
    String relationName;

    @DatabaseField(columnName = "testid")
    String test_id;


    @DatabaseField(columnName = "relationType")
    String relationtype;

    @DatabaseField(columnName = "testedTime")
    String testedTime;

    @DatabaseField(columnName = "relativeEmailId")
    String relativeemailid;

    @DatabaseField(columnName = "rbc")
    int rbcValue;

    @DatabaseField(columnName = "billirubinValue")
    private double billirubinValue;

    @DatabaseField(columnName = "UroboliogenValue")
    double UroboliogenValue;

    @DatabaseField(columnName = "KetonesValue")
    int KetonesValue;

    @DatabaseField(columnName = "ProteinValue")
    int ProteinValue;

    @DatabaseField(columnName = "NitriteValue")
    double NitriteValue;

    @DatabaseField(columnName = "GlucoseValue")
    int GlucoseValue;

    @DatabaseField(columnName = "PhValue")
    double PhValue;

    @DatabaseField(columnName = "SgValue")
    double SgValue;

    @DatabaseField(columnName = "LeucocyteValue")
    int LeucocyteValue;

    @DatabaseField(columnName = "memberId")
    private String member_Id;

    @DatabaseField(columnName = "isfromonline",dataType = DataType.BOOLEAN)
    public boolean isFromonline;

    @DatabaseField(columnName = "longitude")
    private String longitude;

    @DatabaseField(columnName = "latitude")
    private String latitude;

    @DatabaseField(columnName = "member" ,canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Member member;
}
