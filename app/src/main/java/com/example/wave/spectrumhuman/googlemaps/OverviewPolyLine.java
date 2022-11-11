package com.example.wave.spectrumhuman.googlemaps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jacobliu on 12/25/17.
 */

public class OverviewPolyLine {


    @SerializedName("points")
    @Expose
    private String points;



    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }



}
