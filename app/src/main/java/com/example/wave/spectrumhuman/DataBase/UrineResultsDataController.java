package com.example.wave.spectrumhuman.DataBase;
import android.content.Context;
import android.util.Log;

import com.example.wave.spectrumhuman.LoginModule.UrineAdvertizeViewController;
import com.example.wave.spectrumhuman.Models.Member;
import com.example.wave.spectrumhuman.Models.UrineresultsModel;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * Created by dell on 03-10-2017.
 */
public class UrineResultsDataController
{
    public ArrayList<UrineresultsModel> allUrineResults = new ArrayList<>();
    public static UrineResultsDataController myObj;

    public static UrineResultsDataController getInstance() {
        if (myObj == null) {
            myObj = new UrineResultsDataController();
        }
        return myObj;
    }
    //Inserting member data
    public void insertUrineResults(String testid,String member_Id,String relation_name, String relationtype, String testdate, String relativeemailid, int rbc, double bilirubin, double urobilinozen, int ketones, int protein, double nitrite, int glucose, double ph, double sg, int leucocyte, boolean isFromonline,String latitude,String longitude)
    {

        UrineresultsModel urineresultsModel = new UrineresultsModel(MemberDataController.getInstance().currentMember,testid,member_Id,relation_name,relationtype,testdate,relativeemailid,rbc,bilirubin,urobilinozen,ketones,protein,nitrite,glucose,ph,sg,leucocyte,isFromonline,latitude,longitude);
        Log.e("gtmember","call"+MemberDataController.getInstance().currentMember);
        Log.e("insertmid","call"+member_Id);
        try {
            UserDataController.getInstance().helper.getUrineresultsDao().create(urineresultsModel);
            Log.e("relation_name","call"+isFromonline);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addUrineResultsForMember(String testid,String member_Id,String relation_name, String relationtype, String testdate, String relativeemailid, int rbc, double bilirubin, double urobilinozen, int ketones, int protein, double nitrite, int glucose, double ph, double sg, int leucocyte,boolean isFromonline,String latitude,String longitude)
    {

        Member objMember = MemberDataController.getInstance().getMemberForMemberId(member_Id);
        if(objMember != null) {
            UrineresultsModel urineresultsModel = new UrineresultsModel(objMember, testid, member_Id, relation_name, relationtype, testdate, relativeemailid, rbc, bilirubin, urobilinozen, ketones, protein, nitrite, glucose, ph, sg, leucocyte,isFromonline,latitude,longitude);
            Log.e("insertmid", "call" + member_Id);
            try {
                UserDataController.getInstance().helper.getUrineresultsDao().create(urineresultsModel);
                Log.e("relation_name", "call" + relation_name);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<UrineresultsModel> fetchAllUrineResults() {

        allUrineResults = new ArrayList<UrineresultsModel>();
        if (MemberDataController.getInstance().currentMember != null) {
            ArrayList<UrineresultsModel> urineresultsModels = new ArrayList<UrineresultsModel>(MemberDataController.getInstance().currentMember.getUrineresultsModels());
            Log.e("call", "" + urineresultsModels);
            if (urineresultsModels != null) {
                allUrineResults = urineresultsModels;
                if (allUrineResults.size() > 0) {
                    Log.e("fetching", "urine fectched successfully" + allUrineResults.size());
                    Log.e("urine", "call" + allUrineResults.size());
                    allUrineResults = sortUrineResultsBasedOnTime(allUrineResults);
                }
            }
        }
            return allUrineResults;
        }

    public ArrayList<UrineresultsModel> sortUrineResultsBasedOnTime(ArrayList<UrineresultsModel> urineResults){
        Collections.sort(urineResults, new Comparator<UrineresultsModel>() {
            @Override
            public int compare(UrineresultsModel s1, UrineresultsModel s2) {
                return s1.getTestedTime().compareTo(s2.getTestedTime());
            }
        });
        return urineResults;

    }
    public boolean deleteurineresultsData(UrineresultsModel urineresultsModel )
    {
        try{
            UserDataController.getInstance().helper.getUrineresultsDao().delete(urineresultsModel);
            Log.e("Delete","delete all urineresultsModel");
            fetchAllUrineResults();
            Log.e("deleteUrineRecord","call"+UrineResultsDataController.getInstance().allUrineResults.size());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    //Deleting All urine results
    public void deleteUrineResults(List<UrineresultsModel> results_list )
    {
        try{
            UserDataController.getInstance().helper.getUrineresultsDao().delete(results_list);
            Log.e("deleted","deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUrineResults(String testid,String member_Id,String relation_name, String relationtype, String testdate, String relativeemailid, int rbc, double bilirubin, double urobilinozen, int ketones, int protein, double nitrite, int glucose, double ph, double sg, int leucocyte,boolean isFromonline,String latitude,String longitude)

    {
            try
            {
                UpdateBuilder<UrineresultsModel,Integer> updateBuilder =UserDataController.getInstance().helper.getUrineresultsDao().updateBuilder();
                updateBuilder.updateColumnValue("relationName",relation_name);
                updateBuilder.updateColumnValue("relationType",relationtype);
                updateBuilder.updateColumnValue("testid",testid);
                updateBuilder.updateColumnValue("relativeEmailId",relativeemailid);
                updateBuilder.updateColumnValue("rbc",rbc);
                updateBuilder.updateColumnValue("billirubinValue",bilirubin);
                updateBuilder.updateColumnValue("UroboliogenValue",urobilinozen);
                updateBuilder.updateColumnValue("KetonesValue",ketones);
                updateBuilder.updateColumnValue("ProteinValue",protein);
                updateBuilder.updateColumnValue("NitriteValue",nitrite);
                updateBuilder.updateColumnValue("GlucoseValue",glucose);
                updateBuilder.updateColumnValue("PhValue",ph);
                updateBuilder.updateColumnValue("SgValue",sg);
                updateBuilder.updateColumnValue("LeucocyteValue",leucocyte);
                updateBuilder.updateColumnValue("memberId",member_Id);
                updateBuilder.updateColumnValue("testedTime",testdate);
                updateBuilder.updateColumnValue("latitude",latitude);
                updateBuilder.updateColumnValue("longitude",longitude);
                updateBuilder.updateColumnValue("isfromonline",isFromonline);

                updateBuilder.where().eq("testedTime",testdate);
                updateBuilder.update();
                Log.e("update","urine results updated sucessfully");

            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
/*//get all members
    public ArrayList<UrineresultsModel> fetchAllUrineResults()
    {
        ForeignCollection<UrineresultsModel> urineresultsModels = MemberDataController.getInstance().currentMember.getUrineresultsModels();
        Log.e("call",""+urineresultsModels);
        return new ArrayList<>(urineresultsModels);

    }*/
}
