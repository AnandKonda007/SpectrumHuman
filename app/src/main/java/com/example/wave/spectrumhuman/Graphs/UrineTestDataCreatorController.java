package com.example.wave.spectrumhuman.Graphs;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.wave.spectrumhuman.DataBase.MemberDataController;
import com.example.wave.spectrumhuman.DataBase.UrineResultsDataController;
import com.example.wave.spectrumhuman.Models.UrineresultsModel;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.SideMenu.MemberViewController;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
/**
 * Created by WAVE on 11/13/2017.
 */
public class UrineTestDataCreatorController extends Activity {
    public  double minimalValue= 20.0;
    public static UrineTestDataCreatorController myObj;
    ////
    public static  UrineTestDataCreatorController getInstance() {
        if (myObj == null) {
            myObj = new UrineTestDataCreatorController();
        }
        return myObj;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);

    }
    public  static int getRandomIntValue( int min , int max){
        Random randomNumber=new Random();
        int lower  = min;
        int upper = max;
        int randomnum=randomNumber.nextInt((upper - lower) + lower);
        Log.e("randomnum",""+randomnum);

        return randomnum;
    }
    public  static double getRandomDoubleValue(float firstNum,float secondNum)
    {
        Random randomNdouble=new Random();
        double f=randomNdouble.nextFloat()*(secondNum-firstNum)+firstNum;
        Log.e("randomdouble",""+f);
        return f;
    }
    public  String convertTimestampToDate(String stringData) throws ParseException {

        long yourmilliseconds = Long.parseLong(stringData);
        Log.e("yourmilliseconds",""+yourmilliseconds);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("EEEE,dd | hh:mm:ss a",Locale.ENGLISH);
        SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM yyyy",Locale.ENGLISH);
        SimpleDateFormat weekmonthtimeFormatter = new SimpleDateFormat("MMM dd yyyy",Locale.ENGLISH);
        SimpleDateFormat formatter= new SimpleDateFormat("hh:mm a",Locale.ENGLISH);
        //
        Date resultdate = new Date(yourmilliseconds*1000);

        String weekString = weekFormatter.format(resultdate);
        String monthString = monthFormatter.format(resultdate);
        String combineString=weekmonthtimeFormatter.format(resultdate);
        String timeString = formatter.format(resultdate );
        return  weekString+";"+monthString+";"+combineString+";"+timeString;

    }
    public static class UrineNameComparator implements Comparator<UrineresultsModel>
    {
        @Override
        public int compare(UrineresultsModel u1, UrineresultsModel u2) {
            return u2.getTestedTime().compareTo(u1.getTestedTime());
        }
    }
    public  ArrayList getFilterArrayForDateString(String date) throws ParseException {
        ArrayList<UrineresultsModel> filterDateList=new ArrayList<>();
        UrineresultsModel urineTestModel;
        for(int i=0; i < UrineResultsDataController.getInstance().allUrineResults.size(); i++)
        {
            Log.e("filterListinresult",""+UrineResultsDataController.getInstance().allUrineResults.get(i).getTestedTime());
            urineTestModel =UrineResultsDataController.getInstance().allUrineResults.get(i);
            String convertDate=convertTimestampTodate(urineTestModel.getTestedTime());
            Log.e("convertDateinresult",""+convertDate);
            if(date.equals(convertDate))
            {
                filterDateList.add(urineTestModel);
            }
        }
        Log.e("fitedr",""+filterDateList.size());

        return filterDateList;
    }
    public  boolean getIsDataAvailable(UrineresultsModel objRecord) {

        if (objRecord.getRbcValue()>0 || objRecord.getBillirubinValue()>0 || objRecord.getUroboliogenValue()>0 || objRecord.getKetonesValue()> 0 || objRecord.getProteinValue() > 0 || objRecord.getNitriteValue()>0 || objRecord.getGlucoseValue()> 0 || objRecord.getPhValue()> 0 || objRecord.getSgValue()> 0 || objRecord.getLeucocyteValue() > 0)
        {
            return true;
        }
        return false;
    }
    public  UrineresultsModel getUrineTestDataAvgObject(ArrayList<UrineresultsModel> objects){
        UrineresultsModel averageUrineTestObj = new UrineresultsModel();
        averageUrineTestObj.setRbcValue(0);
        averageUrineTestObj.setBillirubinValue(0.0);
        averageUrineTestObj.setUroboliogenValue(0.0);
        averageUrineTestObj.setKetonesValue(0);
        averageUrineTestObj.setProteinValue(0);
        averageUrineTestObj.setNitriteValue(0.00);
        averageUrineTestObj.setGlucoseValue(0);
        averageUrineTestObj.setPhValue(0.0);
        averageUrineTestObj.setSgValue(0.000);
        averageUrineTestObj.setLeucocyteValue(0);
        ////
        if(objects.size()>0)
        {
            averageUrineTestObj.setRelationName(objects.get(0).getRelationName());
            averageUrineTestObj.setRelationtype(objects.get(0).getRelationtype());
            for(UrineresultsModel model : objects)
            {
                UrineresultsModel  objUrineRecord  = model;
                averageUrineTestObj.setRbcValue( averageUrineTestObj.getRbcValue()+ objUrineRecord.getRbcValue());
                averageUrineTestObj.setBillirubinValue( averageUrineTestObj.getBillirubinValue()+ objUrineRecord.getBillirubinValue());
                averageUrineTestObj.setUroboliogenValue( averageUrineTestObj.getUroboliogenValue()+ objUrineRecord.getUroboliogenValue());
                averageUrineTestObj.setKetonesValue( averageUrineTestObj.getKetonesValue()+ objUrineRecord.getKetonesValue());
                averageUrineTestObj.setProteinValue( averageUrineTestObj.getProteinValue()+ objUrineRecord.getProteinValue());
                averageUrineTestObj.setNitriteValue( averageUrineTestObj.getNitriteValue()+ objUrineRecord.getNitriteValue());
                averageUrineTestObj.setGlucoseValue( averageUrineTestObj.getGlucoseValue()+ objUrineRecord.getGlucoseValue());
                averageUrineTestObj.setPhValue( averageUrineTestObj.getPhValue()+ objUrineRecord.getPhValue());
                averageUrineTestObj.setSgValue( averageUrineTestObj.getSgValue()+ objUrineRecord.getSgValue());
                averageUrineTestObj.setLeucocyteValue( averageUrineTestObj.getLeucocyteValue()+ objUrineRecord.getLeucocyteValue());
            }
            ////
            averageUrineTestObj.setRbcValue(averageUrineTestObj.getRbcValue()/(objects.size()));
            averageUrineTestObj.setBillirubinValue(averageUrineTestObj.getBillirubinValue()/(double) (objects.size()));
            averageUrineTestObj.setUroboliogenValue(averageUrineTestObj.getUroboliogenValue()/(double)(objects.size()));
            averageUrineTestObj.setKetonesValue(averageUrineTestObj.getKetonesValue()/(objects.size()));
            averageUrineTestObj.setProteinValue(averageUrineTestObj.getProteinValue()/(objects.size()));
            averageUrineTestObj.setNitriteValue(averageUrineTestObj.getNitriteValue()/(double)(objects.size()));
            averageUrineTestObj.setGlucoseValue(averageUrineTestObj.getGlucoseValue()/(objects.size()));
            averageUrineTestObj.setPhValue(averageUrineTestObj.getPhValue()/(double)(objects.size()));
            averageUrineTestObj.setSgValue(averageUrineTestObj.getSgValue()/(double)(objects.size()));
            averageUrineTestObj.setLeucocyteValue(averageUrineTestObj.getLeucocyteValue()/(objects.size()));
            Log.e("getRelativename","call"+averageUrineTestObj.getBillirubinValue());
            Log.e("getRelativeType","call"+averageUrineTestObj.getRelationtype());


            DecimalFormat dfbilii = new DecimalFormat("#.#",new DecimalFormatSymbols(Locale.ENGLISH));
            DecimalFormat dfnitrite = new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.ENGLISH));
            DecimalFormat  dfsg = new DecimalFormat("#.###",new DecimalFormatSymbols(Locale.ENGLISH));

             Log.e("bilruban","call"+Double.parseDouble(dfbilii.format(averageUrineTestObj.getBillirubinValue())));
            averageUrineTestObj.setBillirubinValue(Double.parseDouble(dfbilii.format(averageUrineTestObj.getBillirubinValue())));
            averageUrineTestObj.setUroboliogenValue(Double.parseDouble(dfbilii.format(averageUrineTestObj.getUroboliogenValue())));
            averageUrineTestObj.setNitriteValue(Double.parseDouble(dfnitrite.format(averageUrineTestObj.getNitriteValue())));
            averageUrineTestObj.setPhValue(Double.parseDouble(dfbilii.format(averageUrineTestObj.getPhValue())));
            averageUrineTestObj.setSgValue(Double.parseDouble(dfsg.format(averageUrineTestObj.getSgValue())));

            Log.e("sgval","call"+averageUrineTestObj.getSgValue());
        }
        return averageUrineTestObj;
    }
    public  String getRbcText(int value){

        if (value >= 0 && value <= 7)
        {
            return "-ve"+"\n"+value;
        }
        else if (value >= 8 && value <= 29)
        {
            return "+"+"\n"+value;
        }

        else if (value >= 30 && value <= 140)
        {
            return "++"+"\n"+value;
        }
        else if (value <= 141)
        {
            return "+++"+"\n"+value;
        }
        else
        {
            return "++++"+"\n"+value;
        }
    }
    public  String getBillirubinText(double value){
        if(value>=0 && value<=0.4){

            return "-ve"+"\n"+value;

        }else if(value>=0.5 && value<=0.7){

            return "+"+"\n"+value;

        }else if(value>=0.8 && value<=1.9){

            return "++"+"\n"+value;

        }/*else if(value<=2.0){

            return "+++"+"\n"+value;
        }*/
        else {
            return "+++"+"\n"+value;
        }
    }
    public  String getUroboliogenText(double value){
        if(value>=0.1 && value<=1.9){

            return "-ve"+"\n"+value;

        }else if(value>=2.0 && value<=2.9){

            return "+"+"\n"+value;

        }else if(value>=3.0 && value<=6.5){

            return "++"+"\n"+value;

        }else if(value>=6.6 && value<=10.5){

            return "+++"+"\n"+value;

        }/*else if(value<=10.6){

            return "++++"+"\n"+value;
        }*/else {

            return "++++"+"\n"+value;

        }
    }
    public  String getKetonesText(int value){
        if(value>=0 && value<=4){

            return "-ve"+"\n"+value;
        }else if(value>=5 && value <=7){

            return "+/-"+"\n"+value;
        }else if(value>=8 && value<=29){

            return "+"+"\n"+value;
        }else if(value>=30 && value<=74){

            return "++"+"\n"+value;
        }/*else if(value<=75){

            return "+++"+"\n"+value;
        }*/else {

            return "+++"+"\n"+value;
        }
    }
    public  String getProteinText(int value){
        if(value>= 0&& value<= 29){

            return "-ve"+"\n"+value;
        }else if(value>=30 && value<=60){

            return "+/-"+"\n"+value;
        }else if(value>=61 && value<=80){

            return "+"+"\n"+value;
        }else if(value>=81 && value<=200){

            return "++"+"\n"+value;
        }else if(value>=201 && value<=600){

            return "+++"+"\n"+value;
        }/*else if(value<=601){

            return "++++"+"\n"+value;
        }*/else {
            return "++++"+"\n"+value;

        }

    }
    public  String getNitriteText(double value){
        if(value>=0 && value<=0.04){

            return "-ve"+"\n"+value;
        }/*else if(value<=0.05){

            return "+"+"\n"+value;
        }*/else {
            return "+"+"\n"+value;

        }

    }
    public  String getGlucoseText(int value){
        if(value>=0 && value<=49){

            return "-ve"+"\n"+value;
        }else if(value>=50 && value<=175){

            return "+/-"+"\n"+value;
        }else if(value>=176 && value<=375){

            return "+"+"\n"+value;
        }else if(value>=376 && value<=750){

            return "++"+"\n"+value;
        }else if(value>=751 && value<=1499){

            return "+++"+"\n"+value;
        }/*else if(value<=1500){

            return "++++"+"\n"+value;
        }*/else {
            return "++++"+"\n"+value;

        }
    }
    public  String getPhText(double value){
        if(value>=0 && value<=4.4){
            return "-ve"+"\n"+value;
        }else if(value>=4.5 && value<=6.0){

            return "+/-"+"\n"+value;
        }else if(value>=6.1 && value<=7.5){

            return "+"+"\n"+value;
        }else if(value>=7.6 && value<=8.5){

            return "++"+"\n"+value;
        }else if(value<= 8.6){

            return "+++"+"\n"+value;
        }else {
            return "++++"+"\n"+value;

        }
    }
    public  String getSgText(double value){
        if(value>0 && value<= 1.002){

            return "-ve"+"\n"+value;
        }else if(value>= 1.003&& value<=1.007){

            return "+/-"+"\n"+value;
        }else if(value>=1.008 && value<=1.014){

            return "+"+"\n"+value;
        }else if(value>=1.015 && value<=1.022){

            return "++"+"\n"+value;
        }else if(value>=1.023 && value<=1.029){

            return "+++"+"\n"+value;
        }/*else if(value<=1.030){

            return "++++"+"\n"+value;
        }*/else {
            return "++++"+"\n"+value;

        }
    }
    public   String  getLeukocyteText(int value){
        if(value>=0 && value<=17){

            return "-ve"+"\n"+value;
        }else if(value>=18 && value<=60){

            return "+"+"\n"+value;
        }else if(value>=61 && value<=300){

            return "++"+"\n"+value;
        }else if(value<=301){

            return "+++"+"\n"+value;
        }else {
            return "++++"+"\n"+value;

        }
    }
    public  int getCountValuesForRanges(double green,double yellow,double cyan,double purple,double orange,double red)
    {

        int seperateValue = 0;

        if (green > 0)
        {
            seperateValue = seperateValue + 1;
        }
        if (yellow > 0)
        {
            seperateValue = seperateValue + 1;
        }
        if (cyan > 0)
        {
            seperateValue = seperateValue + 1;
        }
        if (purple > 0)
        {
            seperateValue = seperateValue + 1;
        }
        if (orange > 0)
        {
            seperateValue = seperateValue + 1;
        }
        if (red > 0)
        {
            seperateValue = seperateValue + 1;
        }

        return seperateValue;

    }
    public  ArrayList<Double> getRangeValuesForRBC()
    {
        ArrayList<Double> rbcList=new ArrayList<>();
        rbcList.add(1d);
        rbcList.add(0d);
        rbcList.add(1d);
        rbcList.add(1d);
        rbcList.add(1d);
        rbcList.add(0d);

        return rbcList;
    }
    public  ArrayList<Double> getRangeValuesForBiliruin()
    {
        ArrayList<Double> biliruinList=new ArrayList<>();
        biliruinList.add(1d);
        biliruinList.add(0d);
        biliruinList.add(1d);
        biliruinList.add(1d);
        biliruinList.add(1d);
        biliruinList.add(1d);

        return biliruinList;
    }
    public  ArrayList<Double> getRangeValuesUroBilogen()
    {
        ArrayList<Double> UroBilogen=new ArrayList<>();
        UroBilogen.add(1d);
        UroBilogen.add(0d);
        UroBilogen.add(1d);
        UroBilogen.add(1d);
        UroBilogen.add(1d);
        UroBilogen.add(1d);

        return UroBilogen;
    }
    public  ArrayList<Double> getRangeValuesKetones()
    {
        ArrayList<Double> Ketones=new ArrayList<>();
        Ketones.add(1d);
        Ketones.add(1d);
        Ketones.add(1d);
        Ketones.add(1d);
        Ketones.add(1d);
        Ketones.add(0d);
        return Ketones;
    }
    public  ArrayList<Double> getRangeValuesProtiens()
    {
        ArrayList<Double> Protiens=new ArrayList<>();
        Protiens.add(1d);
        Protiens.add(1d);
        Protiens.add(1d);
        Protiens.add(1d);
        Protiens.add(1d);
        Protiens.add(1d);

        return Protiens;
    }
    public  ArrayList<Double> getRangeValuesNitrate()
    {
        ArrayList<Double> Nitrate=new ArrayList<>();
        Nitrate.add(1d);
        Nitrate.add(0d);
        Nitrate.add(1d);
        Nitrate.add(0d);
        Nitrate.add(0d);
        Nitrate.add(0d);

        return Nitrate;
    }
    public  ArrayList<Double> getRangeValuesGlucose()
    {
        ArrayList<Double> Glucose=new ArrayList<>();
        Glucose.add(1d);
        Glucose.add(1d);
        Glucose.add(1d);
        Glucose.add(1d);
        Glucose.add(1d);
        Glucose.add(1d);

        return Glucose;
    }
    public  ArrayList<Double> getRangeValuespH()
    {
        ArrayList<Double> pH=new ArrayList<>();
        pH.add(1d);
        pH.add(1d);
        pH.add(1d);
        pH.add(1d);
        pH.add(1d);
        pH.add(0d);
        return pH;
    }
    public  ArrayList<Double> getRangeValuesSG()
    {
        ArrayList<Double> SG=new ArrayList<>();
        SG.add(1d);
        SG.add(1d);
        SG.add(1d);
        SG.add(1d);
        SG.add(1d);
        SG.add(1d);

        return SG;
    }
    public  ArrayList<Double> getRangeValuesWBC()
    {
        //(17,0,60,300,0)
        ArrayList<Double> WBC=new ArrayList<>();
        WBC.add(1d);
        WBC.add(0d);
        WBC.add(1d);
        WBC.add(1d);
        WBC.add(1d);
        WBC.add(0d);
        return WBC;
    }
    /////////////////get colors for tests
    public  ArrayList<Double> getRbcColor(double value,double seperatorValue)
    {
        ArrayList<Double> rbccolorList=new ArrayList<>();
        double partValue = (seperatorValue)/4;

        if (value == 0)
        {
            rbccolorList.add((double) Color.TRANSPARENT);
            rbccolorList.add(0.0);

            return rbccolorList;
        }
        else if( value>0 && value <= 7)
        {
            if (value > 5)
            {
                // rbccolorList.add((double)Color.argb((int) 1.0,(int)0.15,(int)0.31,(int)0.07));
                rbccolorList.add((double)Color.parseColor("#274E13"));
                rbccolorList.add(seperatorValue);
                return rbccolorList;

            }
            else if( value>2)
            {
                //rbccolorList.add((double)Color.argb((int) 1.0,(int)0.15,(int)0.31,(int)0.07));

                rbccolorList.add((double)Color.parseColor("#274E13"));
                rbccolorList.add(seperatorValue-partValue*2);
                return rbccolorList;
            }
            else
            {
                // rbccolorList.add((double)Color.argb((int) 1.0,(int)0.15,(int)0.31,(int)0.07));

                rbccolorList.add((double)Color.parseColor("#274E13"));
                rbccolorList.add(seperatorValue-partValue*3);
                return rbccolorList;
            }
            //  return (UIColor(red: 0.15, green: 0.31, blue: 0.07, alpha: 1.0),seperatorValue-minimalValue)
        }
        else if (value >= 8 && value <= 29)
        {
            if (value > 22)//3D85C7
            {
                //rbccolorList.add((double)Color.argb((int) 1.0,(int)0.24,(int)0.52,(int)0.78));

                rbccolorList.add((double)Color.parseColor("#3D85C7"));
                rbccolorList.add(seperatorValue*2);
                return rbccolorList;
            }
            else if (value > 15)
            {
                rbccolorList.add((double)Color.parseColor("#3D85C7"));
                rbccolorList.add(seperatorValue*2-partValue*2);
                return rbccolorList;
            }
            else
            {
                rbccolorList.add((double)Color.parseColor("#3D85C7"));
                rbccolorList.add(seperatorValue*2-partValue*3);
                return rbccolorList;
            }

            // return (UIColor(red: 0.24, green: 0.52, blue: 0.78, alpha: 1.0), seperatorValue*2-minimalValue)
        }

        else if (value >= 30 && value <= 140)
        {

            if(value > 104)
            {
                rbccolorList.add((double) Color.parseColor("#800080"));
                rbccolorList.add(seperatorValue*3);
                return rbccolorList;
            }
            else if (value > 68)
            {
                rbccolorList.add((double) Color.parseColor("#800080"));
                rbccolorList.add(seperatorValue*3-partValue*2);
                return rbccolorList;
            }
            else
            {
                rbccolorList.add((double) Color.parseColor("#800080"));
                rbccolorList.add(seperatorValue*3-partValue*3);
                return rbccolorList;
            }


            //  return (UIColor.purple,seperatorValue*3-minimalValue)
        }
        else
        {
            if (value > 140)
            {
                rbccolorList.add((double) Color.parseColor("#FFA500"));
                rbccolorList.add(seperatorValue*4);
                return rbccolorList;
            }
            else if (value > 200)
            {
                rbccolorList.add((double) Color.parseColor("#FFA500"));
                rbccolorList.add(seperatorValue*4-partValue*2);
                return rbccolorList;
            }
            else
            {
                rbccolorList.add((double) Color.parseColor("#FFA500"));
                rbccolorList.add(seperatorValue*4-partValue*3);
                return rbccolorList;
            }

            // return (UIColor.orange,seperatorValue*4-minimalValue)
        }

    }
    public static ArrayList<Double> getBilirubinColor(double value,double seperatorValue)
    {
        ArrayList<Double> bilirubinList=new ArrayList<>();
        double partValue=(seperatorValue)/4;
        if (value == 0)
        {
            bilirubinList.add((double)Color.TRANSPARENT);
            bilirubinList.add(0.0);
            return  bilirubinList;
        }
        else if (value > 0 && value <= 0.4)
        {
            /*bilirubinList.add((double)Color.argb((int)1.0,(int)0.15,(int)0.31,(int)0.07));
            bilirubinList.add(seperatorValue-minimalValue);
            return  bilirubinList;*/
            if (value > 0.3)
            {
                bilirubinList.add((double)Color.parseColor("#274E13"));
                bilirubinList.add(seperatorValue);
                return  bilirubinList;
            }
            else if (value > 0.2)
            {
                bilirubinList.add((double)Color.parseColor("#274E13"));
                bilirubinList.add(seperatorValue-partValue*2);
                return  bilirubinList;
            }
            else
            {
                bilirubinList.add((double)Color.parseColor("#274E13"));
                bilirubinList.add(seperatorValue-partValue*3);
                return  bilirubinList;
            }
        }
        else if (value >= 0.5 && value <= 0.7)
        {
            /*bilirubinList.add((double)Color.argb((int)1.0,(int)0.24,(int)0.52,(int)0.78));
            bilirubinList.add(seperatorValue*2-minimalValue);
            return  bilirubinList;*/
            if (value > 0.6)
            {
                bilirubinList.add((double)Color.parseColor("#3D85C7"));
                bilirubinList.add(seperatorValue*2);
                return  bilirubinList;
            }
            else
            {
                bilirubinList.add((double)Color.parseColor("#3D85C7"));
                bilirubinList.add(seperatorValue*2-partValue*2);
                return  bilirubinList;
            }


        }

        else if (value >= 0.8 && value <= 1.9)
        {
            if (value > 1.6)
            {
                bilirubinList.add((double)Color.parseColor("#800080"));
                bilirubinList.add(seperatorValue*3);
                return  bilirubinList;
            }
            else if (value > 1.3)
            {
                bilirubinList.add((double)Color.parseColor("#800080"));
                bilirubinList.add(seperatorValue*3-partValue*2);
                return  bilirubinList;
            }
            else
            {
                bilirubinList.add((double)Color.parseColor("#800080"));
                bilirubinList.add(seperatorValue*3-partValue*3);
                return  bilirubinList;
            }

            /*bilirubinList.add((double)Color.parseColor("#800080"));
            bilirubinList.add(seperatorValue*3-minimalValue);
            return  bilirubinList;*/
        }
        else
        {
            if (value > 2.0)
            {
                bilirubinList.add((double)Color.parseColor("#FFA500"));
                bilirubinList.add(seperatorValue*4);
                return  bilirubinList;
            }
            else
            {
                bilirubinList.add((double)Color.parseColor("#FFA500"));
                bilirubinList.add(seperatorValue*4-partValue*2);
                return  bilirubinList;
            }
           /* bilirubinList.add((double)Color.parseColor("#FFA500"));
            bilirubinList.add(seperatorValue*4-minimalValue);
            return  bilirubinList;*/
        }


    }
    public  ArrayList<Double> getUrobilinozenColor(double value,double seperatorValue)
    {
        ArrayList<Double> urobilinozenList=new ArrayList<>();
        double partValue=seperatorValue/4;
        if (value == 0)
        {
            urobilinozenList.add((double)Color.TRANSPARENT);
            urobilinozenList.add(0.0);
            return  urobilinozenList;
        }
        else if (value >= 0.1 && value <= 1.9)
        {
            if (value > 1.0)
            {
                urobilinozenList.add((double)Color.parseColor("#274E13"));
                urobilinozenList.add(seperatorValue);
                return  urobilinozenList;
            }
            else
            {
                urobilinozenList.add((double)Color.parseColor("#274E13"));
                urobilinozenList.add(seperatorValue-partValue*3);
                return  urobilinozenList;
            }
            /*urobilinozenList.add((double)Color.argb((int) 1.0,(int)0.15,(int)0.31,(int)0.07));
            urobilinozenList.add(seperatorValue-minimalValue);
            return  urobilinozenList;*/
        }
        else if (value >= 2.0 && value <= 2.9)
        {
           /* urobilinozenList.add((double)Color.parseColor("#3D85C7"));
            urobilinozenList.add(seperatorValue*2-minimalValue);
            return  urobilinozenList;*/
            if (value > 2.5)
            {
                urobilinozenList.add((double)Color.parseColor("#3D85C7"));
                urobilinozenList.add(seperatorValue*2);
                return  urobilinozenList;
            }
            else
            {
                urobilinozenList.add((double)Color.parseColor("#3D85C7"));
                urobilinozenList.add(seperatorValue*2-partValue*3);
                return  urobilinozenList;
            }
        }
        else if (value >= 3.0 && value <= 6.5)
        {
            if (value > 4.5)
            {
                urobilinozenList.add((double)Color.parseColor("#800080"));
                urobilinozenList.add(seperatorValue*3);
                return urobilinozenList;
            } else
            {
                urobilinozenList.add((double)Color.parseColor("#800080"));
                urobilinozenList.add(seperatorValue*3-partValue*3);
                return urobilinozenList;
            }
        }
        else if (value >= 6.6 && value <= 10.5)
        {
            /*urobilinozenList.add((double)Color.parseColor("#FFA500"));
            urobilinozenList.add(seperatorValue*4-minimalValue);
            return  urobilinozenList;*/
            if (value > 8.5)
            {
                urobilinozenList.add((double)Color.parseColor("#FFA500"));
                urobilinozenList.add(seperatorValue*4);
                return  urobilinozenList;
            }
            else
            {
                urobilinozenList.add((double)Color.parseColor("#FFA500"));
                urobilinozenList.add(seperatorValue*4-partValue*3);
                return  urobilinozenList;
            }
        }
        else
        {
            /*urobilinozenList.add((double)Color.RED);
            urobilinozenList.add(seperatorValue*5-minimalValue);
            return  urobilinozenList;*/
            if (value > 9.0)
            {
                urobilinozenList.add((double)Color.RED);
                urobilinozenList.add(seperatorValue*5);
                return  urobilinozenList;
            } else
            {
                urobilinozenList.add((double)Color.RED);
                urobilinozenList.add(seperatorValue*5-partValue*3);
                return  urobilinozenList;
            }


        }


    }
    public  ArrayList<Double> getKetonesColor(double value,double seperatorValue) {
        ArrayList<Double> ColorList = new ArrayList<>();
        double partValue = (seperatorValue)/4;

        if (value == 0) {
            ColorList.add((double) Color.TRANSPARENT);
            ColorList.add(0.0);
            return ColorList;

        } else if (value > 0 && value <= 4) {
           /*ColorList.add((double) Color.argb((int) 1.0, (int) 0.15, (int) 0.31, (int) 0.07));
           ColorList.add(seperatorValue - minimalValue);
           return ColorList;*/
            if (value > 3)
            {
                // ColorList.add((double) Color.argb((int) 1.0, (int) 0.15, (int) 0.31, (int) 0.07));
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue);
                return ColorList;
            }
            else if (value > 2)
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue-partValue*2);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue-partValue*3);
                return ColorList;
            }

        } else if (value >= 5 && value <= 7) {
           /*ColorList.add((double) Color.YELLOW);
           ColorList.add(seperatorValue * 2 - minimalValue);
           return ColorList;*/
            if (value > 6)
            {
                ColorList.add((double) Color.YELLOW);
                ColorList.add(seperatorValue * 2);
                return ColorList;
            }
            else
            {
                ColorList.add((double) Color.YELLOW);
                ColorList.add(seperatorValue * 2 - partValue*2);
                return ColorList;
            }
        } else if (value >= 8 && value <= 29) {
           /*ColorList.add((double) Color.argb((int) 1.0, (int) 0.24, (int) 0.52, (int) 0.78));
           ColorList.add(seperatorValue * 3 - minimalValue);
           return ColorList;*/
            if (value > 22)
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue * 3);
                return ColorList;
            }
            else if (value > 15)
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue * 3 - partValue*2);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue * 3 -partValue*3);
                return ColorList;
            }
        } else if (value >= 30 && value <= 74) {
          /* ColorList.add((double) Color.parseColor("#800080"));
           ColorList.add(seperatorValue * 4 - minimalValue);
           return ColorList;*/
            if (value > 60)
            {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4);
                return ColorList;
            }
            else if (value > 46)
            {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4 - partValue*2);
                return ColorList;
            }
            else
            {
                ColorList.add((double) Color.parseColor("#800080"));
                ColorList.add(seperatorValue * 4 - partValue*3);
                return ColorList;
            }
        } else {
           /*ColorList.add((double) Color.parseColor("#FFA500"));
           ColorList.add(seperatorValue * 5 - minimalValue);
           return ColorList;*/
            if (value > 90)
            {
                ColorList.add((double) Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue * 5);
                return ColorList;
            }
            else
            {
                ColorList.add((double) Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue * 5 - partValue*2);
                return ColorList;
            }
        }
    }
    public  ArrayList<Double> getProtineColor(double value,double seperatorValue)
    {
        ArrayList<Double> ColorList=new ArrayList<>();
        double partValue=seperatorValue/4;
        if (value == 0)
        {
            ColorList.add((double)Color.TRANSPARENT);
            ColorList.add(0.0);
            return ColorList;
        }
        else if (value > 0 && value <= 29)
        {
                /*ColorList.add((double)Color.argb((int) 1.0,(int)0.15,(int)0.31,(int)0.07));
                ColorList.add(seperatorValue-minimalValue);
                return ColorList;*/
            if (value > 15)
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue-partValue*3);
                return ColorList;
            }
        }
        else if (value >= 30 && value <= 60)
        {
                /*ColorList.add((double)Color.YELLOW);///////////////////////////////
                ColorList.add(seperatorValue*2-minimalValue);
                return ColorList;*/
            if (value > 55)
            {
                ColorList.add((double)Color.YELLOW);
                ColorList.add(seperatorValue*2);
                return ColorList;

            }
            else
            {
                ColorList.add((double)Color.YELLOW);
                ColorList.add(seperatorValue*2-partValue*2);
                return ColorList;
            }
        }
        else if (value >= 61 && value <= 80)
        {
                /*ColorList.add((double)Color.argb((int) 1.0,(int)0.24,(int)0.52,(int)0.78));
                ColorList.add(seperatorValue*3-minimalValue);
                return ColorList;*/
            if (value > 70)
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*3);
                return ColorList;
            }

            else
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*3-partValue*3);
                return ColorList;
            }
        }

        else if (value >= 81 && value <= 200)
        {
               /* ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*4-minimalValue);
                return ColorList;*/
            if (value > 140)
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*4);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*4-partValue*3);
                return ColorList;
            }
        }
        else if (value >= 201 && value <= 600)
        {
                /*ColorList.add((double)Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue*5-minimalValue);
                return ColorList;*/
            if (value > 400)
            {
                ColorList.add((double)Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue*5);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue*5-partValue*3);
                return ColorList;
            }
        }
        else
        {
               /* ColorList.add((double)Color.RED);
                ColorList.add(seperatorValue*6-minimalValue);
                return ColorList;*/
            if (value > 700)
            {
                ColorList.add((double)Color.RED);
                ColorList.add(seperatorValue*6);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.RED);
                ColorList.add(seperatorValue*6-partValue*3);
                return ColorList;
            }
        }

    }
    public  ArrayList<Double> getNitriteColor(double value,double seperatorValue)
    {
        ArrayList<Double> ColorList=new ArrayList<>();
        double partValue=(seperatorValue)/4;

        if (value == 0)
        {
            ColorList.add((double)Color.TRANSPARENT);
            ColorList.add(0.0);
            return ColorList;
        }
        else if (value > 0 && value <= 0.04)
        {
           /* ColorList.add((double)Color.argb((int)1.0,(int)0.15,(int)0.31,(int)0.07));
            ColorList.add(seperatorValue-minimalValue);
            return ColorList;*/
            if(value > 0.03)
            {
                //ColorList.add((double)Color.argb((int)1.0,(int)0.15,(int)0.31,(int)0.07));
                ColorList.add((double)Color.parseColor("#274E13"));

                ColorList.add(seperatorValue);
                return ColorList;
            }
            else if (value > 0.02)
            {
                //ColorList.add((double)Color.argb((int)1.0,(int)0.15,(int)0.31,(int)0.07));
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue-partValue*2);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#274E13"));

                //ColorList.add((double)Color.argb((int)1.0,(int)0.15,(int)0.31,(int)0.07));
                ColorList.add(seperatorValue-partValue*3);
                return ColorList;
            }
        }
        else  {
            /*ColorList.add((double)Color.argb((int)1.0,(int)0.24,(int)0.52,(int)0.78));
            ColorList.add(seperatorValue*2-minimalValue);
            return ColorList;*/
            if (value > 0.05)
            {
                //ColorList.add((double)Color.argb((int)1.0,(int)0.24,(int)0.52,(int)0.78));
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*2);
                return ColorList;
            }
            else
            {
                // ColorList.add((double)Color.argb((int)1.0,(int)0.24,(int)0.52,(int)0.78));
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*2-partValue*2);
                return ColorList;
            }
        }

    }

    public  ArrayList<Double> getGlucoseColor(double value,double seperatorValue)
    {
        ArrayList<Double> ColorList=new ArrayList<>();
        double partValue = (seperatorValue)/4;

        if (value == 0)
        {
            ColorList.add((double)Color.TRANSPARENT);
            ColorList.add(0.0);
            return ColorList;

        }
        else if (value > 0 && value <= 49)
        {
            /*ColorList.add((double)Color.argb((int) 1.0,(int) 0.15,(int) 0.31,(int) 0.07));
            ColorList.add(seperatorValue-minimalValue);
            return ColorList;*/
            if (value > 25)
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue-partValue*3);
                return ColorList;
            }
        }
        else if (value >= 50 && value <= 175)
        {
            /*ColorList.add((double)Color.YELLOW);
            ColorList.add(seperatorValue*2-minimalValue);
            return ColorList;*/
            if (value > 110)
            {
                ColorList.add((double)Color.YELLOW);
                ColorList.add(seperatorValue*2);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.YELLOW);
                ColorList.add(seperatorValue*2-partValue*3);
                return ColorList;
            }
        }
        else if(value >= 176 && value <= 375)
        {
           /* ColorList.add((double)Color.argb((int)1.0,(int)0.24,(int)0.52,(int)0.78));
            ColorList.add(seperatorValue*3-minimalValue);
            return ColorList;*/
            if (value > 275)
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*3);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*3-partValue*3);
                return ColorList;
            }
        }

        else if (value >= 376 && value <= 750)
        {
            /*ColorList.add((double)Color.parseColor("#800080"));
            ColorList.add(seperatorValue*4-minimalValue);
            return ColorList;*/
            if (value > 563)
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*4);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*4-partValue*3);
                return ColorList;
            }
        }
        else if (value >= 751 && value <= 1499)
        {
            /*ColorList.add((double)Color.parseColor("#FFA500"));
            ColorList.add(seperatorValue*5-minimalValue);
            return ColorList;*/
            if (value > 1125)
            {
                ColorList.add((double)Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue*5);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue*5-partValue*3);
                return ColorList;
            }

        }
        else
        {
           /* ColorList.add((double)Color.RED);
            ColorList.add(seperatorValue*6-minimalValue);
            return ColorList;*/
            if (value > 1600)
            {
                ColorList.add((double)Color.RED);
                ColorList.add(seperatorValue*6);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.RED);
                ColorList.add(seperatorValue*6-partValue*3);
                return ColorList;
            }
        }

    }
    /*public static ArrayList<Double> getPhColor(double value,double seperatorValue)
    {
        ArrayList<Double> ColorList=new ArrayList<>();
       double partValue = (seperatorValue)/4;
        if (value == 0)
        {
            ColorList.add((double)Color.TRANSPARENT);
            ColorList.add(0.0);
            return ColorList;

        }
        else if (value > 0 && value <= 4.4)
        {
           *//* ColorList.add((double)Color.argb((int) 1.0,(int)0.15,(int)0.31,(int)0.07));
            ColorList.add(seperatorValue-minimalValue);
            return ColorList;*//*
            if (value > 3.0)
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue);
                return ColorList;
            }
            else if (value > 1.5)
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue-partValue*2);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue-partValue*3);
                return ColorList;
            }
        }
        else if (value >= 4.5 && value <= 6.0)
        {
            *//*ColorList.add((double)Color.YELLOW);
            ColorList.add(seperatorValue*2-minimalValue);
            return ColorList;*//*
            if (value > 5.5)
            {
                ColorList.add((double)Color.YELLOW);
                ColorList.add(seperatorValue*2);
                return ColorList;
            }
            else if (value > 5.0)
            {
                ColorList.add((double)Color.YELLOW);
                ColorList.add(seperatorValue*2-partValue*2);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.YELLOW);
                ColorList.add(seperatorValue*2-partValue*3);
                return ColorList;
            }
        }
        else if (value >= 6.1 && value <= 7.5)
        {
           *//* ColorList.add((double)Color.argb((int) 1.0,(int)0.24,(int)0.78,(int)0.78));
            ColorList.add(seperatorValue*3-minimalValue);
            return ColorList;*//*
            if (value > 7.0)
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*3);
                return ColorList;
            }
            else if (value > 6.5)
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*3-partValue*2);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*3-partValue*3);
                return ColorList;
            }
        }

        else if (value >= 7.6 && value <= 8.5)
        {
            *//*ColorList.add((double)Color.parseColor("#800080"));
            ColorList.add(seperatorValue*4-minimalValue);
            return ColorList;*//*
            if (value > 8.0)
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*4);
                return ColorList;
            }
            else if (value > 6.5)
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*4-partValue*2);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*4-partValue*3);
                return ColorList;
            }

        }else
        {
            *//*ColorList.add((double)Color.parseColor("#FFA500"));
            ColorList.add(seperatorValue*5-minimalValue);
            return ColorList;*//*
            if (value > 9.5)
            {
                ColorList.add((double)Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue*5);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue*5-partValue*5);
                return ColorList;
            }
        }
    }*/
    public  ArrayList<Double> getPhColor(double value,double seperatorValue)
    {
        ArrayList<Double> ColorList=new ArrayList<>();
        double partValue = (seperatorValue)/4;
        if (value == 0)
        {
            ColorList.add((double)Color.TRANSPARENT);
            ColorList.add(0.0);
            return ColorList;

        }
        else if (value > 0 && value <= 4.4)
        {
           /* ColorList.add((double)Color.argb((int) 1.0,(int)0.15,(int)0.31,(int)0.07));
            ColorList.add(seperatorValue-minimalValue);
            return ColorList;*/
            if (value > 3.0)
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue);
                return ColorList;
            }
            else if (value > 1.5)
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue-partValue*2.5);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue-partValue*3);
                return ColorList;
            }
        }
        else if (value >= 4.5 && value <= 6.0)
        {
            /*ColorList.add((double)Color.YELLOW);
            ColorList.add(seperatorValue*2-minimalValue);
            return ColorList;*/
            if (value > 5.5)
            {
                ColorList.add((double)Color.YELLOW);
                ColorList.add(seperatorValue*2);
                return ColorList;
            }
            else if (value > 5.0)
            {
                ColorList.add((double)Color.YELLOW);
                ColorList.add(seperatorValue*2-partValue*2.5);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.YELLOW);
                ColorList.add(seperatorValue*2-partValue*3);
                return ColorList;
            }
        }
        else if (value >= 6.1 && value <= 7.5)
        {
           /* ColorList.add((double)Color.argb((int) 1.0,(int)0.24,(int)0.78,(int)0.78));
            ColorList.add(seperatorValue*3-minimalValue);
            return ColorList;*/
            if (value > 7.0)
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*3);
                return ColorList;
            }
            else if (value > 6.5)
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*3-partValue*2.5);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*3-partValue*3);
                return ColorList;
            }
        }

        else if (value >= 7.6 && value <= 8.5)
        {
            /*ColorList.add((double)Color.parseColor("#800080"));
            ColorList.add(seperatorValue*4-minimalValue);
            return ColorList;*/
            if (value > 8.0)
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*4);
                return ColorList;
            }
            else if (value > 6.5)
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*4-partValue*2.5);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*4-partValue*3);
                return ColorList;
            }

        }else
        {
            /*ColorList.add((double)Color.parseColor("#FFA500"));
            ColorList.add(seperatorValue*5-minimalValue);
            return ColorList;*/
            if (value > 9.5)
            {
                ColorList.add((double)Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue*5);
                return ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue*5-partValue*2.5);
                return ColorList;
            }
        }
    }
    public  ArrayList<Double> getSgColor(double value,double seperatorValue)
    {
        ArrayList<Double> ColorList=new ArrayList<>();
        double partValue = (seperatorValue)/4;
        if(value == 0)
        {
            ColorList.add((double)Color.TRANSPARENT);
            ColorList.add(0.0);
            return  ColorList;        }
        else if (value > 0 && value <= 1.002)
        {
           /* ColorList.add((double)Color.argb((int) 1.0,(int)0.15,(int)0.31,(int)0.007));
            ColorList.add(seperatorValue-minimalValue);
            return  ColorList;*/
            if (value > 0.006)
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue);
                return  ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue-partValue*3);
                return  ColorList;
            }
        }
        else if (value >= 1.003 && value <= 1.007)
        {
            /*ColorList.add((double)Color.YELLOW);
            ColorList.add(seperatorValue*2-minimalValue);
            return  ColorList;*/
            if (value > 1.005)
            {
                ColorList.add((double)Color.YELLOW);
                ColorList.add(seperatorValue*2);
                return  ColorList;
            }
            else
            {
                ColorList.add((double)Color.YELLOW);
                ColorList.add(seperatorValue*2-partValue*3);
                return  ColorList;
            }
        }
        else if (value >= 1.008 && value <= 1.014)
        {
           /* ColorList.add((double)Color.argb((int)1.0,(int)0.24,(int)0.52,(int)0.78));
            ColorList.add(seperatorValue*3-minimalValue);
            return  ColorList;*/
            if (value > 1.010)
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*3);
                return  ColorList;
            } else
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*3-partValue*3);
                return  ColorList;
            }
        }

        else if (value >= 1.015 && value <= 1.022)
        {
            /*ColorList.add((double)Color.parseColor("#800080"));
            ColorList.add(seperatorValue*4-minimalValue);
            return  ColorList;*/
            if (value > 1.018)
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*4);
                return  ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*4-partValue*3);
                return  ColorList;

            }
        }
        else if (value >= 1.023 && value <= 1.029)
        {
            /*ColorList.add((double)Color.parseColor(" #FFA500"));
            ColorList.add(seperatorValue*5-minimalValue);
            return  ColorList;*/
            if (value > 1.024)
            {
                ColorList.add((double)Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue*5);
                return  ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue*5-partValue*3);
                return  ColorList;

            }

        }
        else
        {
            /*ColorList.add((double)Color.RED);
            ColorList.add(seperatorValue*6-minimalValue);
            return  ColorList;*/
            if (value > 1.027)
            {
                ColorList.add((double)Color.RED);
                ColorList.add(seperatorValue*6);
                return  ColorList;
            }
            else
            {
                ColorList.add((double)Color.RED);
                ColorList.add(seperatorValue*6-partValue*3);
                return  ColorList;

            }

        }
    }
    public  ArrayList<Double> getLeukocyteColor(double value,double seperatorValue)
    {
        ArrayList<Double> ColorList=new ArrayList<>();
        double partValue = (seperatorValue)/4;
        if (value == 0)
        {
            ColorList.add((double)Color.TRANSPARENT);
            ColorList.add(0.0);
            return  ColorList;
        }
        else if(value > 0 && value <= 17)
        {
            /*ColorList.add((double)Color.argb((int)1.0,(int)0.15,(int)0.31,(int)0.07));
            ColorList.add(seperatorValue-minimalValue);
            return  ColorList;*/
            if (value > 12)
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue);
                return  ColorList;
            }
            else if(value > 8)
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue-partValue*2);
                return  ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#274E13"));
                ColorList.add(seperatorValue-partValue*3);
                return  ColorList;

            }
        }
        else if (value >= 18 && value <= 60)
        {
            /*ColorList.add((double)Color.argb((int)1.0,(int)0.24,(int)0.52,(int)0.78));
            ColorList.add(seperatorValue*2-minimalValue);
            return  ColorList;*/
            if (value > 45)
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*2);
                return  ColorList;
            }
            else if (value > 30)
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*2-partValue*2);
                return  ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#3D85C7"));
                ColorList.add(seperatorValue*2-partValue*3);
                return  ColorList;

            }
        }

        else if (value >= 61 && value <= 300)
        {
            /*ColorList.add((double)Color.parseColor("#800080"));
            ColorList.add(seperatorValue*3-minimalValue);
            return  ColorList;*/
            if (value > 220)
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*3);
                return  ColorList;
            }
            else if (value > 140)
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*3-partValue*2);
                return  ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#800080"));
                ColorList.add(seperatorValue*3-partValue*3);
                return  ColorList;

            }

        }
        else
        {
            /*ColorList.add((double)Color.parseColor("#FFA500"));
            ColorList.add(seperatorValue*4-minimalValue);
            return  ColorList;*/
            if (value > 400)
            {
                ColorList.add((double)Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue*4);
                return  ColorList;
            }
            else
            {
                ColorList.add((double)Color.parseColor("#FFA500"));
                ColorList.add(seperatorValue*4-partValue*2);
                return  ColorList;
            }
        }
    }
    public int getTestColorForLable(String value){
        int color=0;
        if(value.equals("-ve")){
            color=Color.parseColor("#274E13");

        }else if (value.equals("+/-")){

            color=Color.parseColor("#FFFF66");

        }else if (value.equals("+")){

            color=Color.parseColor("#3D85C7");

        } else if (value.equals("++")){

            color=Color.parseColor("#800080");

        } else if (value.equals("+++")){

            color=Color.parseColor("#FFA500");

        }else if (value.equals("++++")){

            color=Color.parseColor("#ff0012");
        }
        return color;
    }
    public  String convertTimestampTodate(String stringData)
            throws ParseException {
        long yourmilliseconds = Long.parseLong(stringData);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("yyyy/MM/dd",Locale.ENGLISH);
        Date resultdate = new Date(yourmilliseconds*1000);
        String weekString = weekFormatter.format(resultdate);
        return weekString;
    }
    public static String convertTimestampToMonth(String stringData)
            throws ParseException {
        long yourmilliseconds = Long.parseLong(stringData);
        SimpleDateFormat weekFormatter = new SimpleDateFormat("yyyy/MM",Locale.ENGLISH);
        Date resultdate = new Date(yourmilliseconds*1000);
        String monthString = weekFormatter.format(resultdate);
        return monthString;
    }
    public static ArrayList getFilterArrayForMonthString(String monthStr) throws ParseException {
        ArrayList<UrineresultsModel> filterMonthList=new ArrayList<>();
        for(int i=0; i < UrineResultsDataController.getInstance().allUrineResults.size(); i++)
        {
            UrineresultsModel urineTestModel =UrineResultsDataController.getInstance().allUrineResults.get(i);
            String ss=convertTimestampToMonth(urineTestModel.getTestedTime());
            Log.e("onvertmonthDate",""+ss);//yyyy/mm
            if(ss.equals(monthStr))
            {
                filterMonthList.add(urineTestModel);
                Log.e("filterMonthList","call");

            }
            Log.e("filterMonthList",""+filterMonthList.size());

        }
        return filterMonthList;
    }
}