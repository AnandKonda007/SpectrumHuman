package com.example.wave.spectrumhuman.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.wave.spectrumhuman.Models.DeviceInformation;
import com.example.wave.spectrumhuman.Models.DoctorInformationModel;
import com.example.wave.spectrumhuman.Models.Language;
import com.example.wave.spectrumhuman.Models.Member;
import com.example.wave.spectrumhuman.Models.UrineresultsModel;
import com.example.wave.spectrumhuman.Models.User;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends OrmLiteSqliteOpenHelper
{
    // name of the database file for your application --
    private static final String DATABASE_NAME = "spectrumHuman.db";

    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    // the DAO object we use to access the SimpleData table
    private Dao<User, Integer> userDao = null;





    private Dao<Member,Integer> memberDao = null;
    private Dao<DeviceInformation,Integer> deviceInformationdao = null;
    private Dao<Language, Integer> languageDao = null;
    private Dao<UrineresultsModel, Integer> urineresultsModelIntegerDao = null;
    private Dao<DoctorInformationModel,Integer> addDoctorDao =null;


    ConnectionSource objConnectionSource;

    public DataBaseHelper(Context contex) {
        super(contex, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource)
    {
        Log.e("DBStatus","OnCreate"+connectionSource);

            try
            {
                //creating the user table
                TableUtils.createTable(connectionSource,User.class);

                TableUtils.createTable(connectionSource,Member.class);
                TableUtils.createTable(connectionSource,UrineresultsModel.class);
                TableUtils.createTable(connectionSource,Language.class);
                TableUtils.createTable(connectionSource,DeviceInformation.class);
                TableUtils.createTable(connectionSource,DoctorInformationModel.class);

                userDao =   DaoManager.createDao(connectionSource, User.class);


                memberDao = DaoManager.createDao(connectionSource, Member.class);
                urineresultsModelIntegerDao = DaoManager.createDao(connectionSource, UrineresultsModel.class);
                deviceInformationdao =  DaoManager.createDao(connectionSource, DeviceInformation.class);
                addDoctorDao =  DaoManager.createDao(connectionSource, DoctorInformationModel.class);

                Log.e("Member","member table is created");

                objConnectionSource = connectionSource;

            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion)
    {
        Log.e("DBStatus","OnUpgrade"+connectionSource);
        try {
            TableUtils.dropTable(connectionSource,User.class,true);

            TableUtils.dropTable(connectionSource,Member.class,true);
            TableUtils.dropTable(connectionSource,Language.class,true);
            TableUtils.dropTable(connectionSource,DeviceInformation.class,true);
            TableUtils.dropTable(connectionSource,UrineresultsModel.class,true);
            TableUtils.dropTable(connectionSource,DoctorInformationModel.class,true);

            onCreate(database, connectionSource);
            objConnectionSource = connectionSource;
            } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    //user DAO
    public Dao<User, Integer> getUserDao() {
        if (userDao == null) {
            try {
              //  userDao = DaoManager.createDao(objConnectionSource,User.class);
                userDao = getDao(User.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userDao;
    }


    //Member DAO
    public Dao<Member,Integer> getMemberDao()
    {
        if(memberDao == null)
        {
            try
            {
                // memberDao = DaoManager.createDao(objConnectionSource,Member.class);
                 memberDao = getDao(Member.class);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return memberDao;
    }

///////Deviceinformation  Dao
    public Dao<DeviceInformation,Integer> getDeviceInformationdao()
    {
        if(deviceInformationdao == null)
        {
            try
            {
               // deviceInformationdao = DaoManager.createDao(objConnectionSource,DeviceInformation.class);
                deviceInformationdao = getDao(DeviceInformation.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return deviceInformationdao;
    }

///////Language Dao//////
    public Dao<Language, Integer> getLanguageDao() {
        if (languageDao == null) {
            try {
                languageDao = getDao(Language.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return languageDao;
    }
       /////// urine results////////
    public Dao<UrineresultsModel, Integer> getUrineresultsDao() {
        if (urineresultsModelIntegerDao == null) {
            try {
                urineresultsModelIntegerDao = getDao(UrineresultsModel.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return urineresultsModelIntegerDao;
    }
/////////////Add doctor dao

    public Dao<DoctorInformationModel,Integer> getDoctorInformationdao()
    {
        if(addDoctorDao == null)
        {
            try
            {
                addDoctorDao = getDao(DoctorInformationModel.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return addDoctorDao;
    }
}
