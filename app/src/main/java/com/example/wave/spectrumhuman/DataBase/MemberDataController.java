package com.example.wave.spectrumhuman.DataBase;

import android.util.Log;

import com.example.wave.spectrumhuman.Models.Member;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 02-10-2017.
 */

public class MemberDataController
{
    public ArrayList<Member> allMembers = new ArrayList<>();
    public  Member currentMember;
    public  static  MemberDataController myObj;

    public static  MemberDataController getInstance() {
        if (myObj == null) {
            myObj = new MemberDataController();
        }
        return myObj;
    }
    //Inserting member data
    public Boolean insertMemberData(String userID, String memberId,String name,String email,String birthday,
                                 String relationshipname,String gender,String height,String weight,
                                 String bloodgrp,byte[] profilepic,boolean isActiveMember,boolean isFromOnline)
    {
        if(isActiveMember)
        {
            makeAllMembersAsInactive();
        }

        Member memberModel = new Member(UserDataController.getInstance().currentUser,memberId,userID,name,email,birthday,relationshipname,gender,height,weight,bloodgrp,profilepic,isActiveMember,isFromOnline);
            Log.e("insertmember","call"+UserDataController.getInstance().currentUser);
        Log.e("insertmid","call"+memberId);

        try {
            UserDataController.getInstance().helper.getMemberDao().create(memberModel);
            fetchMemberData();
            return  true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return  false;
        //memberdao.create(memberModel);
    }
    public void getCurrentMember(){
        if(allMembers.size() > 0) {
            for (int i = 0; i < allMembers.size(); i++) {
                Member memberObj = allMembers.get(i);
                Log.e("cururMember", "" + memberObj.getMrelationshipname());
                Log.e("ActiveMember", ""+memberObj.getUser_Id());
            }
        }
    }


    public Member getMemberForMemberId(String memberId){
        if(allMembers.size() > 0) {
            for (int i = 0; i < allMembers.size(); i++) {
                Member memberObj = allMembers.get(i);

                if(memberObj.getMember_Id().equals(memberId)){
                    Log.e("meberadmin","call"+memberObj.getMprofilepicturepath());
                    Log.e("meberadmingetMemail","call"+memberObj.getUser_Id());
                    return memberObj;
                }
            }
        }
        return  null;
    }

    public Member getAdminDetails(){
     Log.e("MembersArray",""+allMembers);
        if(allMembers.size() > 0) {
            for (int i = 0; i < allMembers.size(); i++) {
                Member memberObj = allMembers.get(i);
                if(memberObj.getMrelationshipname().equals("Me")){
                    Log.e("meberadmin","call"+memberObj.getMprofilepicturepath());
                    Log.e("meberadmingetMemail","call"+memberObj.getUser_Id());
                    return memberObj;
                }

            }
        }
        return null;
    }
    //Fetching all the member data
    public ArrayList<Member> fetchMemberData() {
        allMembers = new ArrayList<>();
        ArrayList<Member> dbMembers =  new ArrayList<Member>(UserDataController.getInstance().currentUser.getMembers());
        if(dbMembers != null)
        {
            allMembers = dbMembers;
            if(allMembers.size()>0){
                makeCurrentMember();
                Log.e("fetching", "member data fectched successfully"+allMembers.size());
                Log.e("currentMember", "call"+allMembers.size());
            }
        }
        return allMembers;
    }
    public  void  makeCurrentMember(){
        if(allMembers.size() > 0) {
            for (int i = 0; i < allMembers.size(); i++) {
                Member objMember = allMembers.get(i);
                Log.e("curur", "call" + objMember.getActiveMember());
                if (objMember.getActiveMember()){
                    Log.e("cururActive", "" + objMember.getActiveMember());
                    currentMember = objMember;
                }
            }
        }

    }
    public boolean makeThisMemberAsActive(Member objMember,Boolean isFromOnline){
    makeAllMembersAsInactive();
     objMember.setActiveMember(true);
        objMember.setFromOnline(isFromOnline);
    return updateMemberData(objMember);
}
    public boolean makeThisMemberAsInActive(Member objMember){
        makeAllMembersAsInactive();
        objMember.setActiveMember(false);
        return updateMemberData(objMember);
    }

    public void makeAllMembersAsInactive() {
        for (Member obj :allMembers)
        {
            obj.setActiveMember(false);
            updateMemberData(obj);
        }
    }

    public void refreshActiveMember()  {
        for ( Member obj: allMembers)
        {
            if (obj.getActiveMember() == true)
            {
                currentMember = obj;
            }
        }
    }

    //Deleting all users in database
    public void deleteMemberData(List<Member> members_list )
    {
        try{
            UserDataController.getInstance().helper.getMemberDao().delete(members_list);
            Log.e("Delete","delete all members");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public boolean deleteMemberData(Member member )
    {
        try{
            UserDataController.getInstance().helper.getMemberDao().delete(member);
            Log.e("Delete","delete all members");
            return makeThisMemberAsActive(getAdminDetails(),true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateMemberData(Member objMember)
    {
        try
        {
            UpdateBuilder<Member,Integer> updateBuilder = UserDataController.getInstance().helper.getMemberDao().updateBuilder();
            updateBuilder.updateColumnValue("userId",objMember.getUser_Id());
            updateBuilder.updateColumnValue("member_id",objMember.getMember_Id());
            updateBuilder.updateColumnValue("name",objMember.getMname());
            updateBuilder.updateColumnValue("email",objMember.getEmail());
            updateBuilder.updateColumnValue("birthDay",objMember.getMbirthday());
            updateBuilder.updateColumnValue("relationshipName",objMember.getMrelationshipname());
            updateBuilder.updateColumnValue("gender",objMember.getMgender());
            updateBuilder.updateColumnValue("height",objMember.getMheight());
            updateBuilder.updateColumnValue("weight",objMember.getMweight());
            updateBuilder.updateColumnValue("bloodGroup",objMember.getMbloodgroup());
            updateBuilder.updateColumnValue("profilePicture",objMember.getMprofilepicturepath());
            updateBuilder.updateColumnValue("isActiveMember",objMember.getActiveMember());
            updateBuilder.updateColumnValue("isfromonline",objMember.isFromOnline());

            Log.e("update","updatedmember"+objMember.getMember_Id());
            Log.e("update","updatedmemberheight"+objMember.getMheight());

            updateBuilder.where().eq("member_id",objMember.getMember_Id());
            updateBuilder.update();
            Log.e("update","memner data updated sucessfully"+objMember.getMember_Id());
            return  true;
        } catch (SQLException e) {
            e.printStackTrace();
            return  false;
        }
    }
//updating the member data
    public void updateMemberData(String userID,String memberId, String name,String email,String birthday,
                                 String relationshipname,String gender,String height,String weight,
                                 String bloodgrp,byte[] profilepic,boolean isActiveMember)
    {
             try
             {
                 UpdateBuilder<Member,Integer> updateBuilder = UserDataController.getInstance().helper.getMemberDao().updateBuilder();
                 updateBuilder.updateColumnValue("userId",userID);
                 updateBuilder.updateColumnValue("member_id",memberId);
                 updateBuilder.updateColumnValue("name",name);
                 updateBuilder.updateColumnValue("email",email);
                 updateBuilder.updateColumnValue("birthDay",birthday);
                 updateBuilder.updateColumnValue("relationshipName",relationshipname);
                 updateBuilder.updateColumnValue("gender",gender);
                 updateBuilder.updateColumnValue("height",height);
                 updateBuilder.updateColumnValue("weight",weight);
                 updateBuilder.updateColumnValue("bloodGroup",bloodgrp);
                 updateBuilder.updateColumnValue("profilePicture",profilepic);
                 updateBuilder.updateColumnValue("isActiveMember",isActiveMember);
                 Log.e("update","updatedmemberid"+memberId);
                 Log.e("updateemail","updateemail"+email);
                 updateBuilder.where().eq("member_id",memberId);
                 updateBuilder.update();
                 Log.e("update","member data updated sucessfully");
             } catch (SQLException e) {
                 e.printStackTrace();
             }
    }
}
