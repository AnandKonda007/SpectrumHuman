package com.example.wave.spectrumhuman.Models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

public class Member {
    //for both personalinfo and Member except memberId
    public Member(User user,String member_Id,String userId, String mname, String email, String mbirthday, String mrelationshipname, String mgender, String mheight, String mweight, String mbloodgroup, byte[] mprofilepicturepath,boolean isActiveMember,boolean isFromOnline) {

        this.mname = mname;
        this.user_Id = userId;
        this.member_Id=member_Id;
        this.email = email;
        this.mbirthday = mbirthday;
        this.mrelationshipname = mrelationshipname;
        this.mgender = mgender;
        this.mheight = mheight;
        this.mweight = mweight;
        this.mbloodgroup = mbloodgroup;
        this.mprofilepicturepath = mprofilepicturepath;
        this.user = user;
        this.isActiveMember=isActiveMember;
        this.isFromOnline=isFromOnline;
    }

    public Member() {

    }

    public void setActiveMember(boolean activeMember) {
        isActiveMember = activeMember;
    }

    public boolean getActiveMember() {
        return isActiveMember;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(String user_Id) {
        this.user_Id = user_Id;
    }

    public String getMbirthday() {
        return mbirthday;
    }

    public void setMbirthday(String mbirthday) {
        this.mbirthday = mbirthday;
    }

    public String getMrelationshipname() {
        return mrelationshipname;
    }

    public void setMrelationshipname(String mrelationshipname) {
        this.mrelationshipname = mrelationshipname;
    }

    public String getMgender() {
        return mgender;
    }

    public void setMgender(String mgender) {
        this.mgender = mgender;
    }

    public String getMheight() {
        return mheight;
    }

    public void setMheight(String mheight) {
        this.mheight = mheight;
    }

    public String getMweight() {
        return mweight;
    }

    public void setMweight(String mweight) {
        this.mweight = mweight;
    }

    public String getMbloodgroup() {
        return mbloodgroup;
    }

    public void setMbloodgroup(String mbloodgroup) {
        this.mbloodgroup = mbloodgroup;
    }

    public void setMprofilepicturepath(byte[] mprofilepicturepath) {
        this.mprofilepicturepath = mprofilepicturepath;
    }

    public boolean isFromOnline() {
        return isFromOnline;
    }

    public void setFromOnline(boolean fromOnline) {
        isFromOnline = fromOnline;
    }

    public byte[] getMprofilepicturepath() {
        return mprofilepicturepath;
    }

    public void setMemberid(int memberid) {
        this.memberid = memberid;
    }

    public int getMemberid() {
        return memberid;
    }

    @DatabaseField(generatedId = true)
    private int memberid;

    @DatabaseField(columnName = "isfromonline",dataType = DataType.BOOLEAN)
     boolean isFromOnline;

    @DatabaseField(columnName = "name")
    private String mname;

    //
    @DatabaseField(columnName = "userId")
    private String user_Id;
    //
    @DatabaseField(columnName = "email")
    private String email;

    @DatabaseField(columnName = "birthDay")
    private String mbirthday;

    @DatabaseField(columnName = "relationshipName")
    private String mrelationshipname;

    @DatabaseField(columnName = "gender")
    private String mgender;

    @DatabaseField(columnName = "height")
    private String mheight;

    @DatabaseField(columnName = "weight")
    private String mweight;

    //
    @DatabaseField(columnName = "bloodGroup")
    private String mbloodgroup;
    //
    @DatabaseField(columnName = "profilePicture",dataType = DataType.BYTE_ARRAY)
    private byte[] mprofilepicturepath;

    //get image from server


    @DatabaseField(columnName = "isActiveMember",dataType = DataType.BOOLEAN)
    boolean isActiveMember;
    //

    //
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }


    private User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @DatabaseField(columnName = "user_id", canBeNull = false, foreign = true, foreignAutoRefresh = true)
    public User user;

    @ForeignCollectionField
    private ForeignCollection<UrineresultsModel> urineresultsModels;

    public ForeignCollection<UrineresultsModel> getUrineresultsModels() {
        return urineresultsModels;
    }
    //////// for Members info only
    @SerializedName("member_id")
    @DatabaseField(columnName = "member_id")
    private String member_Id;


    public void setMember_Id(String member_Id) {
        this.member_Id = member_Id;
    }

    public String getMember_Id() {
        return member_Id;
    }
}
