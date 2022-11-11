package com.example.wave.spectrumhuman.googlemaps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacobliu on 12/25/17.
 */

public class Route {


    @SerializedName("overview_polyline")
    @Expose
    private OverviewPolyLine overviewPolyLine;



    @SerializedName("legs")
    @Expose
    private List<Leg> legs = new ArrayList<Leg>();


    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }




    public OverviewPolyLine getOverviewPolyLine() {
        return overviewPolyLine;
    }

    public void setOverviewPolyLine(OverviewPolyLine overviewPolyLine) {
        this.overviewPolyLine = overviewPolyLine;
    }



}
