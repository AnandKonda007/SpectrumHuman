package com.example.wave.spectrumhuman.Graphs;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jacobliu on 4/13/17.
 */

public class WeekFormatter implements IAxisValueFormatter {

     SimpleDateFormat dateFormatter,weekDateFormatter,dayFormatter,axisDateFormatter;
     String  startingDayAxis,endingAxis,stepsStartingAxis,stepsStartingWeekAxis;

   /* var dateFormatter = DateFormatter()
    var weekDateFormatter = DateFormatter()
    var dayFormatter = DateFormatter()*/
   public   String[] XAxisValues ;
    public static ArrayList<Double> valuesArray=new ArrayList<>();

    public  void  loadXAxisValues(Date uvWkStartTime){

        XAxisValues = new  String[7];
        weekDateFormatter = new SimpleDateFormat("MM/dd");
        axisDateFormatter =  new SimpleDateFormat("dd");
        dayFormatter = new SimpleDateFormat("E");

        Calendar processCalenderDate = Calendar.getInstance();
        processCalenderDate.setTime(uvWkStartTime);

        for(int i=0;i<7;i++)
        {
            if(i!=0) {
                processCalenderDate.add(Calendar.DATE, 1);
            }
            startingDayAxis = dayFormatter.format(processCalenderDate.getTime());
            stepsStartingAxis = axisDateFormatter.format(processCalenderDate.getTime());
            stepsStartingWeekAxis = weekDateFormatter.format(processCalenderDate.getTime());

            String xValue;
            if(startingDayAxis.equals("Sun"))
            {
                xValue = stepsStartingWeekAxis.toUpperCase()+","+startingDayAxis;
            }
            else if(Integer.parseInt(stepsStartingAxis) == 1)
            {
                xValue = stepsStartingWeekAxis.toUpperCase()+","+startingDayAxis;
            }
            else
            {
                xValue = stepsStartingAxis.toUpperCase()+","+startingDayAxis;
            }
            Log.e("XAxis", xValue);
            XAxisValues[i]=xValue;

        }

        Log.e("XAxis", String.valueOf(XAxisValues));

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        return XAxisValues[(int) value];
    }


}
