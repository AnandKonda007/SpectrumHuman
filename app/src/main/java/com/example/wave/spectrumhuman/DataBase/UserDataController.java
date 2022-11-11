package com.example.wave.spectrumhuman.DataBase;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import com.example.wave.spectrumhuman.Models.User;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by dell on 20-09-2017.
 */

public class UserDataController extends OrmLiteBaseActivity {
    public DataBaseHelper helper;
    public ArrayList<User> allUsers = new ArrayList<>();
    public  User currentUser;
    //public  Context context;
    public static UserDataController myObj;

    public static  UserDataController getInstance() {
        if (myObj == null) {
            myObj = new UserDataController();
        }

        return myObj;
    }


    public void  fillContext(Context context1)
    {

        Log.e("DBStatus","Fill Context Called");
        helper = new DataBaseHelper(context1);
    }
    //insert the userdata into user table
    public void insertUserData(User userdata) {
        try {
            helper.getUserDao().create(userdata);
            fetchUserData();
            Log.e("fetc", ""+allUsers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void getCurrentUser(){
        if(allUsers.size() > 0) {
            for (int i = 0; i < allUsers.size(); i++) {
                User userobj = allUsers.get(i);
                Log.e("curur", "" + userobj);
                Log.e("ActiveUser", userobj.getUserName());
            }
        }
    }
    //Fetching all the user data
    public ArrayList<User> fetchUserData() {
        allUsers = null;
        allUsers = new ArrayList<>();

        try {
            allUsers = (ArrayList<User>) helper.getUserDao().queryForAll();
            if(allUsers.size()>0){
                currentUser = allUsers.get(0);
                Log.e("currentUser","call"+currentUser.getUserName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Log.e("fetching", "user data fectched successfully"+allUsers.size());

        return allUsers;
    }

    //updating the userdata
    public void updateUserData( User user) {
        try {
            UpdateBuilder<User, Integer> updateBuilder = helper.getUserDao().updateBuilder();
            updateBuilder.updateColumnValue("password", user.getPassword());
            updateBuilder.updateColumnValue("isVerified", user.getIsVerified());
            updateBuilder.updateColumnValue("registerTime", user.getRegisterTime());
            updateBuilder.updateColumnValue("register_type", user.getRegisterType());
            updateBuilder.updateColumnValue("latitude", user.getLatitude());
            updateBuilder.updateColumnValue("longitude", user.getLongitude());
            updateBuilder.updateColumnValue("preferedLanguage", user.getPreferedLanguage());
            updateBuilder.where().eq("userName", user.getUserName());
            updateBuilder.update();
            Log.e("update data", "updated the data sucessfully");
            Log.e("new user id", "" + user.getUserName() + "" + user.getPreferedLanguage());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Deleting all users in database
    public void deleteUserData(ArrayList<User> user) {
        try {
            helper.getUserDao().delete(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // first delete the clients that match the city's id
        /*DeleteBuilder db = userdao.deleteBuilder();
        try {
            db.where().eq("userName",user.getUserName());
            userdao.delete(db.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        //Get helper
        /*try {
            DataBaseHelper helper = OpenHelperManager.getHelper(this, User.class);
            Dao dao = helper.getDao(User.class);
            DeleteBuilder<User, Integer> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where().eq("userName", email);
            deleteBuilder.delete();
               Log.e("deleteUser","call");
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    public User getUserObjectForEmail(String emailId){

        if(allUsers.size() > 0 ) {
            for (int l = 0; l < allUsers.size(); l++) {
                User obUser = allUsers.get(l);
                if (obUser.getUserName().equals(emailId)) {
                    return obUser;
                }

            }
        }
        return  null;
    }
}


