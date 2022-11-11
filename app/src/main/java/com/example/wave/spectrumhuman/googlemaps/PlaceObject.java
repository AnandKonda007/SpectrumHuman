package com.example.wave.spectrumhuman.googlemaps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacobliu on 1/5/18.
 */

public class PlaceObject {



    @SerializedName("locations")
    @Expose
    private ArrayList<PlaceResult> locations = new ArrayList<PlaceResult>();



    @SerializedName("response")
    @Expose
    private String response;


    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;


    @SerializedName("radius")
    private int radius;



    public ArrayList<PlaceResult> getLocations() {
        return locations;
    }

    public String getResponse() {
        return response;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }



    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
