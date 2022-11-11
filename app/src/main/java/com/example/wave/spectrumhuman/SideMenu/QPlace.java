package com.example.wave.spectrumhuman.SideMenu;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Created by navneet on 23/7/16.
 */
public class QPlace {
    private String geometryKey = "geometry";
    private String locationKey = "location";
    private String latitudeKey = "lat";
    private String longitudeKey = "lng";
    private String nameKey = "name";
    private String openingHoursKey = "opening_hours";
    private String openNowKey = "open_now";
    private String vicinityKey = "vicinity";
    private String typesKey = "types";
    private String photosKey = "photos";
    private String  ratingKey = "rating";
    private  String referenceKey="reference";
    private  String nextpageTokenKey="next_page_token";

    private String phoneNumberKey = "formatted_phone_number";
    private String emailKey = "email";
    private String statuskey = "status";
    private String websiteKey = "website";


    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            Log.d("Places", "parse");

            jsonObject = new JSONObject((String) jsonData);
            GetNearbyPlacesData.nextpageToken = jsonObject.getString(nextpageTokenKey);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            Log.d("Places", "parse error");
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        int placesCount = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;
        Log.d("Places", "getPlaces");

        for (int i = 0; i < placesCount; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
                Log.d("Places", "Adding places");

            } catch (JSONException e) {
                Log.d("Places", "Error in Adding places");
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<String, String>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        String email="";
        String phoneNumber="";
        String webSiteUrl="";

        Log.d("getPlace", "Entered");

        try {
            if (!googlePlaceJson.isNull(nameKey)) {
                placeName = googlePlaceJson.getString(nameKey);
            }
            if (!googlePlaceJson.isNull(vicinityKey)) {
                vicinity = googlePlaceJson.getString(vicinityKey);
            }
            latitude = googlePlaceJson.getJSONObject(geometryKey).getJSONObject(locationKey).getString(latitudeKey);
            longitude = googlePlaceJson.getJSONObject(geometryKey).getJSONObject(locationKey).getString(longitudeKey);
            reference = googlePlaceJson.getString(referenceKey);
            phoneNumber=googlePlaceJson.getString(phoneNumberKey);
            webSiteUrl=googlePlaceJson.getString(websiteKey);

            googlePlaceMap.put(nameKey, placeName);
            googlePlaceMap.put(vicinityKey, vicinity);
            googlePlaceMap.put(latitudeKey, latitude);
            googlePlaceMap.put(longitudeKey, longitude);
            googlePlaceMap.put(referenceKey, reference);
            googlePlaceMap.put(phoneNumberKey,phoneNumber);
            googlePlaceMap.put(websiteKey,webSiteUrl);

            Log.d("getPlace", "Putting Places"+googlePlaceMap.get(websiteKey));
        } catch (JSONException e) {
            Log.d("getPlace", "Error");
            e.printStackTrace();
        }
        return googlePlaceMap;
    }
}
