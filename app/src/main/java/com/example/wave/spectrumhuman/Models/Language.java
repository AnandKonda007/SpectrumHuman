package com.example.wave.spectrumhuman.Models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by dell on 03-10-2017.
 */

public class Language
{


  @DatabaseField(generatedId = true)
    int id;

    @DatabaseField(columnName = "languageName")
    String languagename;

    @DatabaseField(columnName = "languageEnglishName")
    String languageenglishname;

    @DatabaseField(columnName = "IOSLink")
    String ioslink;

    @DatabaseField(columnName = "androidLink")
    String androidlink;

    public Language() {
    }

    public Language(User user,String languagename, String languageenglishname, String ioslink, String androidlink, boolean isselected) {
        this.languagename = languagename;
        this.languageenglishname = languageenglishname;
        this.ioslink = ioslink;
        this.androidlink = androidlink;
        this.isselected = isselected;
        this.user=user;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguagename() {
        return languagename;
    }

    public void setLanguagename(String languagename) {
        this.languagename = languagename;
    }

    public String getLanguageenglishname() {
        return languageenglishname;
    }

    public void setLanguageenglishname(String languageenglishname) {
        this.languageenglishname = languageenglishname;
    }

    public String getIoslink() {
        return ioslink;
    }

    public void setIoslink(String ioslink) {
        this.ioslink = ioslink;
    }

    public String getAndroidlink() {
        return androidlink;
    }

    public void setAndroidlink(String androidlink) {
        this.androidlink = androidlink;
    }

    public boolean getisselected() {
        return isselected;
    }

    public void setIsselected(boolean isselected) {
        this.isselected = isselected;
    }

    @DatabaseField(columnName = "isSelected",dataType = DataType.BOOLEAN)
    boolean isselected;

    @DatabaseField(columnName = "user_id", canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private User user;


    private User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



}
