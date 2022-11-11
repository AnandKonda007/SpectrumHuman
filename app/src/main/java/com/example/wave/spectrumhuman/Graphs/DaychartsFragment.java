package com.example.wave.spectrumhuman.Graphs;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.wave.spectrumhuman.DataBase.MemberDataController;
import com.example.wave.spectrumhuman.DataBase.UserDataController;
import com.example.wave.spectrumhuman.HomeModule.HomeActivityViewController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.Models.UrineresultsModel;
import com.example.wave.spectrumhuman.R;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;

import static android.view.View.GONE;
/**
 * Created by Vedas on 11/10/2016.
 */
public class DaychartsFragment extends Fragment{
    RelativeLayout fourplusView,threePlusView,twoPlusView,plusView,plusMinusView,negativeview;
    RelativeLayout layout;
    int selectedPosition=-1;
    int selectedIndex=0;
    Date selectedDate;
    View view;
    String testerName;
    RecyclerView resultRecyclerView;
    ResultsTableViewCell resultsTableViewCell;
    Button right,left;
    ImageView back;
    Button arrow,downbtn,swiperight,swipeleft;
    LinearLayout sharepage;
    RelativeLayout gluose;
    ArrayList<String> testTypeArray;
    public int position;
    CombinedChart mChart;
     ToggleButton togglebutton;
    //
    ArrayList<String> dayAxiesArray;
    ArrayList<Float> dayValuesArray;
    ArrayList<UrineresultsModel> resultsDayArray;
    String selectedDateText = "";
    boolean isDataAvailable = false;
    TextView testType_txt,txt_current_date,txt_current_user,txt_currentDate;
    RelativeLayout rl_nodata,rl_dates;
    XAxis xAxis;
    boolean state=true;
    UrineresultsModel selectedUrineTestRecord ;
    SimpleDateFormat timeStampFormatter,dateformat, dayStampformatter,hourFormate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_day, container, false);
        Toolbar rlToolBar = (Toolbar)view. findViewById(R.id.toolbar_graph);
        ((AppCompatActivity)getActivity()).setSupportActionBar(rlToolBar);
        mChart = (CombinedChart) view.findViewById(R.id.chart_combine_week);
        xAxis = mChart.getXAxis();
        rl_dates=(RelativeLayout)view.findViewById(R.id.relativeLayout4);
        txt_currentDate = (TextView) view.findViewById(R.id.start_txt_week);
        right=(Button) view.findViewById(R.id.btn_date_right);
        right.setOnClickListener(mright);///
        left=(Button) view.findViewById(R.id.btn_date_left);
        left.setOnClickListener(mleft);///
        back= (ImageView) view.findViewById(R.id.backimage);
        rl_nodata=(RelativeLayout) view.findViewById(R.id.rl_nodata);

        testType_txt = (TextView) view.findViewById(R.id.glucose);
        txt_current_date=(TextView)view.findViewById(R.id.txt_current_date);
        txt_current_user=(TextView)view.findViewById(R.id.txt_current_user);
        togglebutton=(ToggleButton)view.findViewById(R.id.toggle);
        togglebutton.setChecked(TabsGraphActivity.isDayToggleChecked);
        Log.e("toggleButton","call"+togglebutton.isChecked()+""+TabsGraphActivity.isDayToggleChecked);
        ids(view);
        setResultRecyclerViewData();
        testType_txt.setText(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex).toString());
        txt_current_user.setText(MemberDataController.getInstance().currentMember.getMname());
        //
        timeStampFormatter = new SimpleDateFormat("hh:mm:ss a",Locale.ENGLISH);
        hourFormate = new SimpleDateFormat("kk:mm:ss",Locale.ENGLISH);
        dateformat = new SimpleDateFormat("yyyy/MM/dd",Locale.ENGLISH);

        dayStampformatter = new SimpleDateFormat("yyyy/MM/dd hh:mm a",Locale.ENGLISH);
         layout = (RelativeLayout) view.findViewById(R.id.linear_week);
        respondToSwipeGesture();

        //
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Do some stuff
            togglebutton.setVisibility(View.GONE);
            arrow.setBackgroundResource(R.drawable.ic_exitscreen);
            state = false;
            testType_txt.setText(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex).toString());
            layout.setVisibility(View.VISIBLE);
            resultRecyclerView.setVisibility(GONE);
            loadDayData(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex), HomeActivityViewController.dateForLandScap);

        }else {
            loadToggleAction();
            respondToSwipeGesture();
            loadDayData(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex),HomeActivityViewController.dateForLandScap);
            loadFirstRecordData();
            Log.e("protraint", "call");
        }
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                deviceBackKey();
            }
        },200);

        return view;

    }

    public void loadToggleAction(){
        if (TabsGraphActivity.isDayToggleChecked && togglebutton.isChecked()==true){
            layout.setVisibility(View.GONE);
            resultRecyclerView.setVisibility(View.VISIBLE);
            Log.e("toggleli","call"+togglebutton.isChecked()+""+TabsGraphActivity.isDayToggleChecked);
        }else {
            Log.e("togglelistelse","call"+togglebutton.isChecked()+""+TabsGraphActivity.isDayToggleChecked);

        }
    }
    public void setResultRecyclerViewData(){
        testTypeArray = new ArrayList<String>();
        testTypeArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OCCULT_BLOOD_RBC_KEY));
        testTypeArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.BILIRUBIN_VALUE_KEY));
        testTypeArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.UROBILIOGEN_KEY));
        testTypeArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.KETONES_KEY));
        testTypeArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PROTEIN_KEY));
        testTypeArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NITRATE_KEY));
        testTypeArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.GLUCOSE_KEY));
        testTypeArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PH_KEY));
        testTypeArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SG_KEY));
        testTypeArray.add(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LEUKOCYTE_KEY));

        //
        resultRecyclerView = (RecyclerView)view.findViewById(R.id.week_list);
        resultRecyclerView.setNestedScrollingEnabled(false);
        resultsTableViewCell = new ResultsTableViewCell(testTypeArray, getActivity().getApplication());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        resultRecyclerView.setLayoutManager(horizontalLayoutManager);
        resultRecyclerView.setAdapter(resultsTableViewCell);
        resultsTableViewCell.notifyDataSetChanged();


    }
    @Override
    public void onResume(){
        super.onResume();
        loadDayData(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex),HomeActivityViewController.dateForLandScap);
    }
    public  void ids(View view){
        arrow=(Button)view.findViewById(R.id.btn_down);
        downbtn=(Button)view.findViewById(R.id.down_button) ;
        swipeleft=(Button)view.findViewById(R.id.btn_swipe_left);
        swiperight=(Button)view.findViewById(R.id.btn_swipe_right);

        downbtn.setOnClickListener(morientation);
        arrow.setOnClickListener(morientation);
        gluose=(RelativeLayout) view.findViewById(R.id.l1_glucose);
        fourplusView=(RelativeLayout)view.findViewById(R.id.l_red);
        threePlusView=(RelativeLayout)view.findViewById(R.id.orange);
        twoPlusView=(RelativeLayout)view.findViewById(R.id.l_purple);
        plusView=(RelativeLayout)view.findViewById(R.id.l_blue);
        plusMinusView=(RelativeLayout)view.findViewById(R.id.l_lemonyellow);
        negativeview=(RelativeLayout)view.findViewById(R.id.green_view);

        togglebutton.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked)
            {
                if(isChecked) {

                    RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.linear_week);
                    layout.setVisibility(View.GONE);
                    arrow.setVisibility(View.VISIBLE);
                    resultRecyclerView.setVisibility(View.VISIBLE);
                    resultsTableViewCell.notifyDataSetChanged();
                    TabsGraphActivity.isDayToggleChecked=true;
                    toggleButton.setChecked(TabsGraphActivity.isDayToggleChecked);

                    Log.e("toggleChecked","call"+isChecked+""+TabsGraphActivity.isDayToggleChecked);

                }else
                    {

                        RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.linear_week);
                        layout.setVisibility(View.VISIBLE);
                        TabsGraphActivity.isDayToggleChecked=false;
                        toggleButton.setChecked(false);
                        Log.e("issue","call"+TabsGraphActivity.selectedTestTypeRecordIndex);
                        loadDayData(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex),HomeActivityViewController.dateForLandScap);
                        loadFirstRecordData();
                    }
                }

        });
    }
    View.OnClickListener mleft=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            HomeActivityViewController.calender.add(Calendar.DATE, -1);
            selectedDate=HomeActivityViewController.calender.getTime();
            HomeActivityViewController.dateForLandScap=selectedDate;
            loadDayData(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex),selectedDate);
            loadFirstRecordData();
        }
    };
    View.OnClickListener mright=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            HomeActivityViewController.calender.add(Calendar.DATE, 1);
             selectedDate=HomeActivityViewController.calender.getTime();
            HomeActivityViewController.dateForLandScap=selectedDate;
            loadDayData(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex),selectedDate);
            loadFirstRecordData();
        }
    };
   public void respondToSwipeGesture() {
       Log.e("respondToSwipeGesture","call");

       swipeleft.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               previousDateAction();
           }
       });
       swiperight.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               nextDateAction();
           }
       });
       /*new  SwipeDetector(resultRecyclerView).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
           @Override
           public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum swipeType)
           {
               if(swipeType== SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT)
               {
                   Log.e("LEFT_TO_RIGHT","call");
                   previousDateAction();
               }
               if(swipeType== SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT)
               {
                  nextDateAction();
               }
           }
       });*/
       /*new SwipeDetector(rl_nodata).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
           @Override
           public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum swipeType)
           {
               if(swipeType== SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT)
               {
                    //previousDateAction();
               }
               if(swipeType== SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT)
               {
                   Log.e("RIGHT_TO_LEFT","call");
                  // nextDateAction();

               }
           }
       });*/
    }

    View.OnClickListener morientation =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (state) {
                togglebutton.setVisibility(View.GONE);
                arrow.setBackgroundResource(R.drawable.ic_exitscreen);
                Log.e("sate1", "" + state);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                state = false;
                testType_txt.setText(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex));
                layout.setVisibility(View.VISIBLE);
                resultRecyclerView.setVisibility(GONE);
                Log.e("landscap", "call");
                loadDayData(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex),HomeActivityViewController.dateForLandScap);
                String  objDateString  = dateformat.format(HomeActivityViewController.dateForLandScap.getTime());
                Log.e("objDateString", "" + objDateString);

            } else {
                Log.e("sate2", "" + state);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                state = true;
                testType_txt.setText(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex));

            }
        }
    };
   public void previousDateAction()  {
       if (selectedIndex > 0)
       {
           Log.e("selectedIndexoiu","call"+selectedIndex);
           selectedIndex = selectedIndex-1;
           Log.e("selectedIndex","call"+selectedIndex);
           loadTableDataForSelectedObject(resultsDayArray.get(selectedIndex));
           loadAnimatonToRecyclerView();
       }

   }
    public void nextDateAction()  {

        if (selectedIndex < resultsDayArray.size()-1)

        {
            selectedIndex = selectedIndex+1;
            loadTableDataForSelectedObject(resultsDayArray.get(selectedIndex));
            loadAnimatonToRecyclerView();

        }

    }
    public void loadTableDataForSelectedObject(UrineresultsModel urineObject)
    {
        ArrayList<UrineresultsModel> urineresultsArray=new ArrayList<UrineresultsModel>();

       urineresultsArray.add(urineObject);
        UrineresultsModel objUrineResultObject  = UrineTestDataCreatorController.getInstance().getUrineTestDataAvgObject(urineresultsArray);
        selectedUrineTestRecord = objUrineResultObject;
        isDataAvailable =UrineTestDataCreatorController.getInstance().getIsDataAvailable(objUrineResultObject);
        long yourmilliseconds = Long.parseLong(urineObject.getTestedTime());
        Date resultdate = new Date(yourmilliseconds * 1000);
        selectedDateText =hourFormate.format(resultdate);
        txt_current_date.setText(selectedDateText);
        Log.e("resultdate",""+selectedDateText);
        resultsTableViewCell.notifyDataSetChanged();

    }
    public  void  loadFirstRecordData(){
        Log.e("loadFirstRecordData","call");
        testerName = MemberDataController.getInstance().currentMember.getMname();
        /*String s=UrineResultsDataController.getInstance().allUrineResults.get(0).getTestedTime();
        long yourmilliseconds = Long.parseLong(s);
        Date resultdate = new Date(yourmilliseconds*1000);
        Log.e("sample","call"+dateformat.format(resultdate));*/

        if (resultsDayArray.size()>0)
        {
            selectedIndex= resultsDayArray.size()-1;
            Log.e("selectedIndex",""+selectedIndex);
            loadTableDataForSelectedObject(resultsDayArray.get(selectedIndex));
        }
        else
        {
            isDataAvailable = false;
            resultsTableViewCell.notifyDataSetChanged();
        }
        if (isDataAvailable){
            Log.e("isData","call");
            resultRecyclerView.setVisibility(View.VISIBLE);
            resultsTableViewCell.notifyDataSetChanged();
            rl_nodata.setVisibility(View.GONE);

        }else {
            Log.e("isDataAvailableelse","call");
            resultsTableViewCell.notifyDataSetChanged();
        }

    }

    private  void loadDayData(String testType,Date  requiredDate) {
        dayAxiesArray = new ArrayList<String>();
        dayValuesArray = new ArrayList<Float>();
        resultsDayArray = new ArrayList<>();
        mChart.clear();

       String  objDateString  = dateformat.format(requiredDate.getTime());
        Log.e("objDateString", "" + objDateString);
        txt_currentDate.setText(objDateString);

        try {
            resultsDayArray = UrineTestDataCreatorController.getInstance().getFilterArrayForDateString(objDateString);
            Log.e("resultsDayArray", "" + resultsDayArray.size());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (resultsDayArray.size() > 0) {

            for (int i = 0; i < resultsDayArray.size(); i++)
            {
                UrineresultsModel objUrineRecord = resultsDayArray.get(i);
                long yourmilliseconds = Long.parseLong(objUrineRecord.getTestedTime());
                Date resultdate = new Date(yourmilliseconds * 1000);
                dayAxiesArray.add(hourFormate.format(resultdate));//load x-Axies formate

                try {
                    Log.e("resultsDayArray", "" + resultsDayArray);

                    ArrayList<UrineresultsModel> selectedRecordArray = new ArrayList<>();

                    selectedRecordArray.add(objUrineRecord);
                    UrineresultsModel objAvgModel  = UrineTestDataCreatorController.getInstance().getUrineTestDataAvgObject(selectedRecordArray);
                    selectedUrineTestRecord=objUrineRecord;
                    switch (TabsGraphActivity.selectedTestTypeRecordIndex) {
                        case 0:
                            dayValuesArray.add((float)objAvgModel.getRbcValue());
                            Log.e("dayValuesArrayRbc", "" + objAvgModel.getRbcValue());

                            break;
                        case 1:
                            dayValuesArray.add((float) objAvgModel.getBillirubinValue());
                            break;
                        case 2:
                            dayValuesArray.add((float) objAvgModel.getUroboliogenValue());

                            break;

                        case 3:
                            dayValuesArray.add((float) objAvgModel.getKetonesValue());

                            break;

                        case 4:
                            dayValuesArray.add( (float) objAvgModel.getProteinValue());

                            break;

                        case 5:
                            dayValuesArray.add( (float) objAvgModel.getNitriteValue());

                            break;

                        case 6:
                            dayValuesArray.add( (float) objAvgModel.getGlucoseValue());

                            break;

                        case 7:
                            dayValuesArray.add( (float) objAvgModel.getPhValue());

                            break;
                        case 8:
                            dayValuesArray.add( (float) objAvgModel.getSgValue());

                            break;
                        case 9:
                            dayValuesArray.add( (float)objAvgModel.getLeucocyteValue());

                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.e("dayValuesArray",""+ dayValuesArray);
            hideAllIndicationViews();
            setBarGroupChartData(dayValuesArray);
        }else
            {
                mChart.clear();
            }

    }
    public void  setBarGroupChartData( ArrayList<Float> values)
    {

        ArrayList<Double> rangeLines=new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            rangeLines.add(0.0);
        }
        Log.e("ValuesArray",""+values);
        Log.e("rangeLines",""+rangeLines.size());
        switch (TabsGraphActivity.selectedTestTypeRecordIndex) {
            case 0:
                rangeLines = UrineTestDataCreatorController.getInstance().getRangeValuesForRBC();
                break;
            case 1:
                rangeLines = UrineTestDataCreatorController.getInstance().getRangeValuesForBiliruin();
                break;
            case 2:
                rangeLines = UrineTestDataCreatorController.getInstance().getRangeValuesUroBilogen();
                break;
            case 3:
                rangeLines = UrineTestDataCreatorController.getInstance().getRangeValuesKetones();
                break;
            case 4:
                rangeLines = UrineTestDataCreatorController.getInstance().getRangeValuesProtiens();
                break;
            case 5:
                rangeLines = UrineTestDataCreatorController.getInstance().getRangeValuesNitrate();
                break;
            case 6:
                rangeLines = UrineTestDataCreatorController.getInstance().getRangeValuesGlucose();
                break;
            case 7:
                rangeLines = UrineTestDataCreatorController.getInstance().getRangeValuespH();
                break;
            case 8:
                rangeLines = UrineTestDataCreatorController.getInstance().getRangeValuesSG();
                break;
            case 9:
                rangeLines = UrineTestDataCreatorController.getInstance().getRangeValuesWBC();
                break;
            default:
                break;
        }

        Log.e("rangeLines",""+rangeLines);
        int seperateCount=UrineTestDataCreatorController.getInstance().getCountValuesForRanges(rangeLines.get(0),rangeLines.get(1),rangeLines.get(2),rangeLines.get(3),rangeLines.get(4),rangeLines.get(5)) ;


        mChart.getAxisLeft().setAxisMinimum(0);
        mChart.getAxisLeft().setAxisMaximum(260);

       final double rangeValue = 240.0/(double)seperateCount;
        if (seperateCount > 4)
        {
            Log.e("seperateCount>4",""+seperateCount);
            UrineTestDataCreatorController.getInstance().minimalValue = (rangeValue/2)+7;
        }
        else
        {
            Log.e("seperateCount<4",""+seperateCount);
            UrineTestDataCreatorController.getInstance().minimalValue = (rangeValue/2)+5;
        }

        final ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
        final ArrayList<Entry> lineEntries = new ArrayList<Entry>();

        ArrayList<Integer> colorArray = new ArrayList<Integer>();
        ArrayList<Double> colorValue =new ArrayList<>();

        for(int i=0; i< values.size();i++)
        {
            colorValue.add((double) Color.argb((int)1.0,(int)0.15,(int)0.31,(int)0.07));
            colorValue.add(0.0);
            float compareValue = values.get(i);
            Log.e("compareValue",""+compareValue);

            switch (TabsGraphActivity.selectedTestTypeRecordIndex)
            {
                case 0:
                    colorValue  = UrineTestDataCreatorController.getInstance().getRbcColor(compareValue,rangeValue);
                    break;

                case 1:
                    colorValue  =  UrineTestDataCreatorController.getInstance().getBilirubinColor(compareValue,rangeValue);
                    break;

                case 2:
                    colorValue  =  UrineTestDataCreatorController.getInstance().getUrobilinozenColor(compareValue,rangeValue);
                    break;

                case 3:
                    colorValue  =  UrineTestDataCreatorController.getInstance().getKetonesColor(compareValue,rangeValue);
                    break;

                case 4:
                    colorValue  =  UrineTestDataCreatorController.getInstance().getProtineColor(compareValue,rangeValue);
                    break;

                case 5:
                    colorValue  =  UrineTestDataCreatorController.getInstance().getNitriteColor(compareValue,rangeValue);
                    break;

                case 6:
                    colorValue  =  UrineTestDataCreatorController.getInstance().getGlucoseColor(compareValue,rangeValue);
                    break;

                case 7:
                    colorValue  =  UrineTestDataCreatorController.getInstance().getPhColor(compareValue,rangeValue);
                    break;
                case 8:
                    colorValue  =  UrineTestDataCreatorController.getInstance().getSgColor(compareValue,rangeValue);
                    break;
                case 9:
                    colorValue  =  UrineTestDataCreatorController.getInstance().getLeukocyteColor(compareValue,rangeValue);
                    break;
            }
            colorArray.add(colorValue.get(0).intValue());
            Log.e("colorarr",""+colorArray);
           // xAxis.setValueFormatter(weekTextFormatter);
            //BarEntry dataEntry = new BarEntry((float)i,values.get(i));
            BarEntry dataEntry = new BarEntry((float)i,colorValue.get(1).floatValue());
            barEntries.add(dataEntry);

             //Entry objEntry = new Entry((float)i,values.get(i));
            Entry objEntry = new Entry((float)i,colorValue.get(1).floatValue());
            lineEntries.add(objEntry);
        }
        BarDataSet barDataSet = new BarDataSet(barEntries,"");
        barDataSet.setColors(colorArray);
        // Bar Data Creation
        BarData barData = new BarData(barDataSet);
        barData.setValueTextSize(12f);
        barData.setDrawValues(true);
        barData.setBarWidth(0.15f);
        barData.setValueTextColor(Color.parseColor("#274e13"));
        barData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                //rather than diaplaying value show label
                Log.e("getFormattedValue",""+value+""+entry);
                int index= (int) entry.getX();
                String dayText=String.valueOf(dayValuesArray.get(index));
                Log.e("monthschartsweekArray",""+ dayValuesArray);

                switch (TabsGraphActivity.selectedTestTypeRecordIndex) {

                    case 0:
                        dayText = String.valueOf(dayValuesArray.get(index).intValue());
                        break;
                    case 3:
                        dayText = String.valueOf(dayValuesArray.get(index).intValue());
                        break;
                    case 4:
                        dayText = String.valueOf(dayValuesArray.get(index).intValue());
                        break;
                    case 6:
                        dayText = String.valueOf(dayValuesArray.get(index).intValue());
                        break;
                    case 9:
                        dayText = String.valueOf(dayValuesArray.get(index).intValue());
                        break;
                    default:
                        break;
                }

                if(dayValuesArray.get(index) == 0)
                {
                    dayText="";
                }
                return dayText;
            }
        });

        // Line Data Creation

        LineDataSet lineDataSet = new LineDataSet(lineEntries,"");
        LineData lineData = new LineData(lineDataSet);
        lineDataSet.setLineWidth(2.0f);
        lineDataSet.setCircleColor(Color.rgb(0, 0, 0));
        lineDataSet.setFillColor(Color.WHITE);
        lineDataSet.setCircleColorHole(Color.WHITE);
        lineDataSet.setDrawValues(true);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setCircleRadius(5);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
        lineDataSet.setColor(Color.BLACK);
        lineDataSet.setValueTextColor(Color.parseColor("#274e13"));
        ////
        lineData.addDataSet(lineDataSet);
        lineData.setDrawValues(true);
        lineData.setValueTextColor(Color.argb((int) 1.0,(int)0.03,(int)0.32,(int)0.05));
        lineData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                //rather than diaplaying value show label
                Log.e("getFormattedValue",""+value+""+entry);
                int index= (int) entry.getX();
                String dayText=String.valueOf(dayValuesArray.get(index));
                Log.e("monthschartsweekArray",""+ dayValuesArray);

                switch (TabsGraphActivity.selectedTestTypeRecordIndex) {

                    case 0:
                        dayText = String.valueOf(dayValuesArray.get(index).intValue());
                        break;
                    case 3:
                        dayText = String.valueOf(dayValuesArray.get(index).intValue());
                        break;
                    case 4:
                        dayText = String.valueOf(dayValuesArray.get(index).intValue());
                        break;
                    case 6:
                        dayText = String.valueOf(dayValuesArray.get(index).intValue());
                        break;
                    case 9:
                        dayText = String.valueOf(dayValuesArray.get(index).intValue());
                        break;
                    default:
                        break;
                }

                if(dayValuesArray.get(index) == 0)
                {
                    dayText="";
                }
                return dayText;
            }
        });
        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        combinedData.setData(barData);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawLabels(false);
        leftAxis.removeAllLimitLines();
       // mChart.getXAxis().setValueFormatter(xAxis.getValueFormatter());
        ////////////set rage to chart


        mChart.setData(combinedData);
        mChart.setBackgroundColor(Color.WHITE);

        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum((float) (values.size()-0.5));
        xAxis.setGranularity(1f);
        mChart.getXAxis().setLabelRotationAngle(-35f);

        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dayAxiesArray));

        mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setDrawGridLines(false);

        mChart.getDescription().setText("");
        //mChart.getBarData().setBarWidth(0.25f);

        mChart.setDoubleTapToZoomEnabled(false);
        mChart.animateXY(500, 500);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        mChart.getLegend().setEnabled(false);

        Log.e("RangeValue",""+rangeValue);
        //safe line

        float lineValue = 0.0f;
        double normal=rangeLines.get(0);
        Log.e("normalval","call"+normal);
        if(normal>0)
        {
            lineValue= (float) (lineValue+rangeValue);
            LimitLine safelimitLine = new LimitLine(lineValue, "");
            safelimitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            safelimitLine.setLineWidth(3);
            safelimitLine.setLineColor( Color.parseColor("#274e13"));
            safelimitLine.setTextColor( Color.argb((int)1.0,(int)0.15,(int)0.31,(int)0.07));
            mChart.getAxisLeft().addLimitLine(safelimitLine);


        }
        double plusOrMinus=rangeLines.get(1);
        if(plusOrMinus>0){
            lineValue= (float) (lineValue+rangeValue);
            LimitLine limitLine=new LimitLine(lineValue," ");
            limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            limitLine.setLineColor(Color.YELLOW);
            limitLine.setTextColor(Color.YELLOW);
            limitLine.setLineWidth(3);
            mChart.getAxisLeft().addLimitLine(limitLine);




        }
        double plus=rangeLines.get(2);
        if(plus>0){
            lineValue= (float) (lineValue+rangeValue);
            LimitLine limitplus=new LimitLine(lineValue," ");
            limitplus.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            limitplus.setLineColor(Color.parseColor("#3D85C7"));
            limitplus.setTextColor(Color.parseColor("#3D85C7"));
            limitplus.setLineWidth(3);
            mChart.getAxisLeft().addLimitLine(limitplus);




        }
        double plusplus=rangeLines.get(3);
        if(plusplus>0){
            lineValue= (float) (lineValue+rangeValue);
            LimitLine limit2plus=new LimitLine(lineValue," ");
            limit2plus.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            limit2plus.setLineColor(Color.parseColor("#800080"));
            limit2plus.setTextColor(Color.parseColor("#800080"));
            limit2plus.setLineWidth(3);
            mChart.getAxisLeft().addLimitLine(limit2plus);
        }
        double plusplusplus=rangeLines.get(4);
        if(plusplusplus>0){
            lineValue= (float) (lineValue+rangeValue);
            LimitLine limit3plus=new LimitLine(lineValue," ");
            limit3plus.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            limit3plus.setLineColor(Color.parseColor("#FFA500"));
            limit3plus.setTextColor(Color.parseColor("#FFA500"));
            limit3plus.setLineWidth(3);
            mChart.getAxisLeft().addLimitLine(limit3plus);
        }

        double fourplus=rangeLines.get(5);
        if(fourplus>0){
            lineValue= (float) (lineValue+rangeValue);
            LimitLine limit4plus=new LimitLine(lineValue," ");
            limit4plus.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            limit4plus.setLineColor(Color.RED);
            limit4plus.setTextColor(Color.RED);
            limit4plus.setLineWidth(3);
            mChart.getAxisLeft().addLimitLine(limit4plus);

            //loadViews(fourplusView,lineValue);
        }

        final ArrayList<Double> rangeLinesValues = rangeLines;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // this code will be executed after 2 seconds

                float lineValue = 0.0f;
                double normal=rangeLinesValues.get(0);
                Log.e("normalval","call"+normal);
                if(normal>0)
                {
                    lineValue= (float) (lineValue+rangeValue);
                    BarEntry limiteLineEntry=new BarEntry((float) 0, lineValue);
                    Log.e("linefloat",""+limiteLineEntry);
                    MPPointF axisPos=mChart.getPosition(limiteLineEntry,mChart.getAxisLeft().getAxisDependency());
                    Log.e("axisPosnegative",""+(int) axisPos.getY());
                    Log.e("axisPosnegative",""+(int) negativeview.getY());
                    negativeview.setVisibility(View.VISIBLE);
                    //set layout  params
                    RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    relativeParams.setMargins((int) negativeview.getX(),  (int) axisPos.getY()-5, negativeview.getWidth(), negativeview.getHeight());
                    negativeview.setLayoutParams(relativeParams);

                }
                double plusOrMinus=rangeLinesValues.get(1);
                if(plusOrMinus>0){

                    lineValue= (float) (lineValue+rangeValue);
                    BarEntry limiteLineEntry=new BarEntry((float) 0, lineValue);
                    Log.e("linefloatplusMinusView",""+lineValue);

                    MPPointF axisPos=mChart.getPosition(limiteLineEntry,mChart.getAxisLeft().getAxisDependency());
                    plusMinusView.setVisibility(View.VISIBLE);
                    Log.e("axisPosplusorminus",""+(int) axisPos.getY());


                    //  loadViews(plusMinusView,lineValue);

                    RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    relativeParams.setMargins((int) plusMinusView.getX(),  (int) axisPos.getY()-5, plusMinusView.getWidth(), plusMinusView.getHeight());
                    plusMinusView.setLayoutParams(relativeParams);



                }
                double plus=rangeLinesValues.get(2);
                if(plus>0){
                    lineValue= (float) (lineValue+rangeValue);
                    BarEntry limiteLineEntry=new BarEntry((float) 0, lineValue);
                    Log.e("linefloatplusView",""+lineValue);
                    MPPointF axisPos=mChart.getPosition(limiteLineEntry,mChart.getAxisLeft().getAxisDependency());
                    plusView.setVisibility(View.VISIBLE);
                    Log.e("axisPosplus",""+(int) axisPos.getY());


                    RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    relativeParams.setMargins((int) plusView.getX(),  (int) axisPos.getY()-5, plusView.getWidth(), plusView.getHeight());
                    plusView.setLayoutParams(relativeParams);
                    //   loadViews(plusView,lineValue);



                }
                double plusplus=rangeLinesValues.get(3);
                if(plusplus>0){
                    lineValue= (float) (lineValue+rangeValue);
                    BarEntry limiteLineEntry=new BarEntry((float) 0, lineValue);
                    MPPointF axisPos=mChart.getPosition(limiteLineEntry,mChart.getAxisLeft().getAxisDependency());
                    twoPlusView.setVisibility(View.VISIBLE);
                    Log.e("axisPostwoPlusView",""+(int) axisPos.getY());


                    RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    relativeParams.setMargins((int) twoPlusView.getX(),  (int) axisPos.getY()-5, twoPlusView.getWidth(), twoPlusView.getHeight());
                    twoPlusView.setLayoutParams(relativeParams);
                    //  loadViews(twoPlusView,lineValue);



                }
                double plusplusplus=rangeLinesValues.get(4);
                if(plusplusplus>0){
                    lineValue= (float) (lineValue+rangeValue);
                    BarEntry limiteLineEntry=new BarEntry((float) 0, lineValue);
                    MPPointF axisPos=mChart.getPosition(limiteLineEntry,mChart.getAxisLeft().getAxisDependency());
                    threePlusView.setVisibility(View.VISIBLE);
                    Log.e("axisPosthreePlusView",""+(int) axisPos.getY());



                    RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    relativeParams.setMargins((int) threePlusView.getX(),  (int) axisPos.getY()-5, threePlusView.getWidth(), threePlusView.getHeight());
                    threePlusView.setLayoutParams(relativeParams);
                    // loadViews(threePlusView,lineValue);


                }

                double fourplus=rangeLinesValues.get(5);
                if(fourplus>0){
                    lineValue= (float) (lineValue+rangeValue);
                    BarEntry limiteLineEntry=new BarEntry((float) 0, lineValue);
                    Log.e("lineValuefourplusView",""+lineValue);
                    MPPointF axisPos=mChart.getPosition(limiteLineEntry,mChart.getAxisLeft().getAxisDependency());
                    fourplusView.setVisibility(View.VISIBLE);
                    Log.e("axisPosfourplusView",""+(int) axisPos.getY());

                    RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    relativeParams.setMargins((int) fourplusView.getX(),  (int) axisPos.getY()-5, fourplusView.getWidth(), fourplusView.getHeight());
                    fourplusView.setLayoutParams(relativeParams);

                    //loadViews(fourplusView,lineValue);
                }

            }
        }, 500);
        //mChart.invalidate();
        loadListData();

    }
    public void loadListData(){
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index= (int) e.getX();
                Log.e("index","call"+index);
                selectedIndex = index;
                UrineresultsModel objSelectRecord = resultsDayArray.get(selectedIndex);
                loadTableDataForSelectedObject(objSelectRecord);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }
    public void hideAllIndicationViews()  {
        Log.e("hideAllIndicationViews","call");
        fourplusView.setVisibility(View.INVISIBLE);
        threePlusView.setVisibility(View.INVISIBLE);
        twoPlusView.setVisibility(View.INVISIBLE);
        plusView.setVisibility(View.INVISIBLE);
        plusMinusView.setVisibility(View.INVISIBLE);
        negativeview.setVisibility(View.INVISIBLE);
    }
    public static class SwipeDetector implements View.OnTouchListener{

        private int min_distance = 500;
        private float downX, downY, upX, upY;
        private View v;

        private onSwipeEvent swipeEventListener;

        public SwipeDetector(View v){
            this.v=v;
            v.setOnTouchListener(this);
        }

        public void setOnSwipeListener(onSwipeEvent listener)
        {
            try{
                swipeEventListener=listener;
            }
            catch(ClassCastException e)
            {
                Log.e("ClassCastException","please pass SwipeDetector.onSwipeEvent Interface instance",e);
            }
        }


        public void onRightToLeftSwipe(){
            if(swipeEventListener!=null)
                swipeEventListener.SwipeEventDetected(v, SwipeTypeEnum.RIGHT_TO_LEFT);
            else
                Log.e("SwipeDetector error","please pass SwipeDetector.onSwipeEvent Interface instance");
        }

        public void onLeftToRightSwipe(){
            if(swipeEventListener!=null)
                swipeEventListener.SwipeEventDetected(v, SwipeTypeEnum.LEFT_TO_RIGHT);
            else
                Log.e("SwipeDetector error","please pass SwipeDetector.onSwipeEvent Interface instance");
        }
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return true;
                }
                case MotionEvent.ACTION_UP: {
                    upX = event.getX();
                    upY = event.getY();

                    float deltaX = downX - upX;
                    float deltaY = downY - upY;

                    //HORIZONTAL SCROLL
                    if(Math.abs(deltaX) > Math.abs(deltaY))
                    {
                        if(Math.abs(deltaX) > min_distance){
                            // left or right
                            Log.e("delatx","call"+deltaX);
                            Log.e("delaty","call"+deltaY);
                            // left or right
                            if (deltaY > 5) {
                                this.onLeftToRightSwipe();
                                return true;
                            }
                            if (deltaX > 5) {
                                this.onRightToLeftSwipe();
                                return true;
                            }
                        }
                        else {
                            //not long enough swipe...
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
        public interface onSwipeEvent
        {
            public void SwipeEventDetected(View v, SwipeTypeEnum SwipeType);
        }

        public SwipeDetector setMinDistanceInPixels(int min_distance)
        {
            this.min_distance=min_distance;
            return this;
        }
        public enum SwipeTypeEnum
        {
            RIGHT_TO_LEFT,LEFT_TO_RIGHT,TOP_TO_BOTTOM,BOTTOM_TO_TOP
        }
    }
    // Step 1:-

    public class ResultsTableViewCell extends RecyclerView.Adapter<ResultsTableViewCell.ViewHolder>  {
        // step 3:-
        ArrayList<String> arrayList = new ArrayList<>();
        Context ctx;

        public ResultsTableViewCell(ArrayList<String> arrayList, Context ctx) {
            this.ctx = ctx;
            this.arrayList = arrayList;
        }
        // step 5:-
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_list_adapter, parent, false);
            ButterKnife.bind(itemView);
            ViewHolder contactViewHolder = new ViewHolder(itemView, ctx, arrayList);
            return contactViewHolder;
        }
        //step 6:-
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position)
        {
            holder.testName.setText(testTypeArray.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {

                    if(TabsGraphActivity.selectedTestTypeRecordIndex != position )
                    {
                        TabsGraphActivity.selectedTestTypeRecordIndex = position;
                        testType_txt.setText(testTypeArray.get(position).toString());
                        Log.e("tempSelectedPos","call"+TabsGraphActivity.selectedTestTypeRecordIndex);
                        loadDayData(testTypeArray.get(TabsGraphActivity.selectedTestTypeRecordIndex),HomeActivityViewController.dateForLandScap);
                        loadFirstRecordData();
                        notifyDataSetChanged();

                    }else
                    {
                        notifyDataSetChanged();
                    }
                    if(togglebutton.isChecked()==true )
                    {
                        RelativeLayout layout = (RelativeLayout) getActivity().findViewById(R.id.linear_week);
                        layout.setVisibility(View.VISIBLE);
                        mChart.setVisibility(View.VISIBLE);
                        togglebutton.setChecked(false);
                        TabsGraphActivity.isDayToggleChecked=false;
                        Log.e("togglelistelse","call"+togglebutton.isChecked()+""+TabsGraphActivity.isDayToggleChecked);

                    }else {

                    }
                    notifyDataSetChanged();
                }

            });
            if(position==0)
            {
                Log.e("pos0","" + position);
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getRbcText(selectedUrineTestRecord.getRbcValue()));
                String s=holder.testValue.getText().toString();
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                setTestConditionForLable(holder,s);
                holder.txt_discription=(TextView)holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.OCCULT_BLOOD_DESCRIPTION_RESULT_KEY));


            }else if(position==1){
                //Log.e("pos1","" + position);
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getBillirubinText(selectedUrineTestRecord.getBillirubinValue()));
                String s=holder.testValue.getText().toString();
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                setTestConditionForLable(holder,s);
                holder.txt_discription=(TextView)holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.BILLIBURIN_DESCRIPTION_RESULT_KEY));


            }else if(position==2){
                // Log.e("pos2","" + position);
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getUroboliogenText(selectedUrineTestRecord.getUroboliogenValue()));
                String s=holder.testValue.getText().toString();
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                setTestConditionForLable(holder,s);
                holder.txt_discription=(TextView)holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.UROBILLINGEN_DESCRIPTION_RESULT_KEY));


            }else if(position==3){
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getKetonesText(selectedUrineTestRecord.getKetonesValue()));
                String s=holder.testValue.getText().toString();
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                setTestConditionForLable(holder,s);
                holder.txt_discription=(TextView)holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.KETONS_DESCRIPTION_KEY));


            }else if(position==4){
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getProteinText(selectedUrineTestRecord.getProteinValue()));
                String s=holder.testValue.getText().toString();
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                setTestConditionForLable(holder,s);
                holder.txt_discription=(TextView)holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PROTINE_DESCRIPTION_KEY));


            }else if(position==5){
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getNitriteText(selectedUrineTestRecord.getNitriteValue()));
                String s=holder.testValue.getText().toString();
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                setTestConditionForLable(holder,s);
                holder.txt_discription=(TextView)holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NITRATE_DISCRIPTION_KEY));

            }else if(position==6){
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getGlucoseText(selectedUrineTestRecord.getGlucoseValue()));
                String s=holder.testValue.getText().toString();
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                setTestConditionForLable(holder,s);
                holder.txt_discription=(TextView)holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.GLOUCOSE_DESCRIPTION_KEY));

            }else if(position==7){
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getPhText(selectedUrineTestRecord.getPhValue()));
                String s=holder.testValue.getText().toString();
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                setTestConditionForLable(holder,s);
                holder.txt_discription=(TextView)holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.PH_DISCRIPTION_KEY));


            }else if(position==8){
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getSgText(selectedUrineTestRecord.getSgValue()));
                String s=holder.testValue.getText().toString();
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                setTestConditionForLable(holder,s);
                holder.txt_discription=(TextView)holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.SG_DESCRIPTION_KEY));


            }else if(position==9){
                holder.testValue.setText(UrineTestDataCreatorController.getInstance().getLeukocyteText(selectedUrineTestRecord.getLeucocyteValue()));
                String s=holder.testValue.getText().toString();
                holder.txt_bg.setBackgroundColor(changeLableColor(s));
                holder.txt_bg1.setBackgroundColor(changeLableColor(s));
                setTestConditionForLable(holder,s);
                holder.txt_discription=(TextView)holder.itemView.findViewById(R.id.test_discription);
                holder.txt_discription.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LECHOCYTE_DISCRIPTION_KEY));

            }
            if(selectedPosition == position) {
                holder.down.setBackgroundResource(R.drawable.ic_uparrow);
                holder.rlScrollMeg.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.rlScrollMeg.setVisibility(GONE);
                holder.down.setBackgroundResource(R.drawable.ic_downarrow);

            }
            holder.down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedPosition != position) {
                        selectedPosition = position;
                        resultsTableViewCell.notifyDataSetChanged();
                    }
                    else
                    {
                        selectedPosition = -1;
                        resultsTableViewCell.notifyDataSetChanged();

                    }
                }


            });
            //
        }
        // step 4:-
        @Override
        public int getItemCount()
        {
            if (isDataAvailable){
                resultRecyclerView.setVisibility(View.VISIBLE);

                return arrayList.size();
            }else {
                Log.e("noodata","call");
                rl_nodata.setVisibility(View.VISIBLE);
                return 0;
            }
        }
        // Step 2:-
        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView testName, testValue,testCondition,txt_discription;
            ArrayList<String> arrayList = new ArrayList<String>();
            Context ctx;
            ImageView imageView;
            //ScrollView msg;
            Button down;
            TextView txt_bg, txt_bg1;
            RelativeLayout rlScrollMeg;
            public ViewHolder(View itemView, Context ctx, final ArrayList<String> arrayList) {
                super(itemView);
                this.arrayList = arrayList;
                this.ctx = ctx;
                testName = (TextView) itemView.findViewById(R.id.testName);
                testValue = (TextView) itemView.findViewById(R.id.testVal);
                testCondition = (TextView) itemView.findViewById(R.id.testCondition);
                imageView=(ImageView)itemView.findViewById(R.id.img_icon) ;
                down = (Button) itemView.findViewById(R.id.btn_down);
                txt_bg = (TextView) itemView.findViewById(R.id.txt1);
                txt_bg1 = (TextView) itemView.findViewById(R.id.txt2);
                rlScrollMeg = (RelativeLayout) itemView.findViewById(R.id.rl_msg);
                txt_discription=(TextView)itemView.findViewById(R.id.test_discription);
            }
        }
    }
    public int changeLableColor(String condition){
        int color=0;
        String[] val=condition.split("\n");
        color=  UrineTestDataCreatorController.getInstance().getTestColorForLable(val[0]);
        return color;

    }

    public void loadAnimatonToRecyclerView(){
        // for adding animatio to recyclerview
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);
        resultRecyclerView.setLayoutAnimation(animation);

    }
    public void setTestConditionForLable(ResultsTableViewCell.ViewHolder holder , String s){
        if(s.contains("-ve")){
            holder.imageView.setImageResource(R.drawable.ic_happy);
            holder.testCondition.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NORMAL_VALUE_KEY));
            holder.testCondition.setTextColor(Color.parseColor("#3a5693"));
        }else {

            holder.imageView.setImageResource(R.drawable.ic_sad);
            holder.testCondition.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ABNORMAL_VALUE_KEY));
            holder.testCondition.setTextColor(Color.parseColor("#FF0000"));
        }
    }
    public void deviceBackKey()
    {
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {


                    Log.e("gif--","fragment back key is home");
                    getActivity().finish();

                    Intent intent = new Intent(getActivity(),HomeActivityViewController.class);
                    //Create the bundle
                    Bundle bundle = new Bundle();
                    bundle.putString("key", "home");
                    intent.putExtras(bundle);
                    startActivity(intent);

                    return true;
                }
                return false;
            }
        });
    }
}