package com.example.wave.spectrumhuman.DataBase;

import android.content.Context;
import android.util.Log;

import com.example.wave.spectrumhuman.Models.Language;
import com.example.wave.spectrumhuman.Models.User;
import com.example.wave.spectrumhuman.SideMenu.LanguageViewController;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 09-10-2017.
 */

public class LanguageDataController extends OrmLiteBaseActivity
{
    private DataBaseHelper helper;
    public Dao languagedao;
    List<User> userList = new ArrayList<>();
    public Dao<User,Integer> userdao;
   public static  LanguageViewController myObj;

    public static LanguageViewController getInstance() {
        if (myObj == null) {
            myObj = new LanguageViewController();
        }
        return myObj;
    }

    public void  fillContext(Context context1)
    {
        helper = new DataBaseHelper(this);

    }


    public DataBaseHelper getHelper()
    {
        return helper;
    }
    //inserting the deviceInformation
    public void insertDeviceInformation(String languagename, String languageenglishname, String ioslink, String androidlink, boolean isselected)
    {
        Language language=new Language(UserDataController.getInstance().currentUser,languagename,languageenglishname,ioslink,androidlink,isselected);

        try
        {
            languagedao.create(language);
            Log.e("device Information","device information inserted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //fetcching all device Information based on current user
    public List<Language> fetchDeviceInformation()
    {
        ForeignCollection<Language> languages = UserDataController.getInstance().currentUser.getLanguages();

        Log.e("call",""+languages);

        return new ArrayList<>(languages);
    }

    //Deleting all languages data
    public void deleteLanguageData(List<Language> languageList)
    {
        try{
              getHelper().getLanguageDao().delete(languageList);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //updating language
    public void updateLanguageData(String langname,String languagename,String language_english_name,String ioslink,String androidlink,boolean is_selected)
    {
            try{
                UpdateBuilder<Language,Integer> updateBuilder = getHelper().getLanguageDao().updateBuilder();
                updateBuilder.updateColumnValue("languageName",languagename);
                updateBuilder.updateColumnValue("languageEnglishName",language_english_name);
                updateBuilder.updateColumnValue("IOSLink",ioslink);
                updateBuilder.updateColumnValue("androidLink",androidlink);
                updateBuilder.updateColumnValue("isSelected",is_selected);
                updateBuilder.where().eq("languageName",langname);
                updateBuilder.update();
                Log.e("updated","updated scucessfully");
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    }


