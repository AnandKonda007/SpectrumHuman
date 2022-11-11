package com.example.wave.spectrumhuman.SideMenu;

import android.app.Activity;
import android.util.Log;

import com.example.wave.spectrumhuman.HomeModule.SideMenuViewController;
import com.example.wave.spectrumhuman.ServerAPIS.ServerApisInterface;
import com.example.wave.spectrumhuman.googlemaps.Geometry;
import com.example.wave.spectrumhuman.googlemaps.GooglePlaceObject;
import com.example.wave.spectrumhuman.googlemaps.Leg;
import com.example.wave.spectrumhuman.googlemaps.Location;
import com.example.wave.spectrumhuman.googlemaps.OverviewPolyLine;
import com.example.wave.spectrumhuman.googlemaps.PlaceObject;
import com.example.wave.spectrumhuman.googlemaps.PlaceResult;
import com.example.wave.spectrumhuman.googlemaps.Result;
import com.example.wave.spectrumhuman.googlemaps.Route;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Rise on 22/12/2017.
 */
public class GooglePlacesFinder extends Activity {

    // public static String searchApiHost = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static String API_KEY = "AIzaSyD564JwZOMcP_eWOMOj5SuveJVdSQCcA_w";

    public List<Result> placesArray = new ArrayList<Result>();

    public String nextPageToken;

    public static GooglePlacesFinder myObj;

    public String status;

    public boolean isLoading = false;

    public OverviewPolyLine overviewPolyline;
    public Route selectedRoute;

    public String encodedRoutePath;
    public String distanceString;
    public String etaTimeString;

    public static GooglePlacesFinder getInstance() {
        if (myObj == null) {
            myObj = new GooglePlacesFinder();
        }
        return myObj;
    }
    public void getPlacesInformation(String type, double latitude, double longitude, int radius) {

        //    String finalURL = searchApiHost+"Key="+API_KEY+"&radius="+radius+"&type="+type+"&location="+latitude+","+longitude;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.GOOGLE_MAPS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApisInterface service = retrofit.create(ServerApisInterface.class);

        String location = String.valueOf(latitude) + "," + String.valueOf(longitude);


        if (nextPageToken == null) {

            service.getNearbyPlaceswithOutPageToken(type, location, radius, API_KEY).enqueue(new Callback<GooglePlaceObject>() {

                @Override
                public void onResponse(Call<GooglePlaceObject> call, Response<GooglePlaceObject> response) {
                    isLoading = false;
                    try {

                        status = response.body().getStatus();
                        Log.e("status", status);
                        if (status.equals("OK")) {
                            Log.e("nextToken", ""+response.body().getNextPageToken());

                            nextPageToken = response.body().getNextPageToken();

                            placesArray.clear();

                            placesArray = response.body().getResults();

                            for(Result objResult:placesArray)
                            {
                                objResult.setFromGoogle(true);
                            }
                            EventBus.getDefault().post(new SideMenuViewController.MessageEvent("GooglePlacesSuccess"));

                            Log.e("Results", "" + placesArray);

                        }


                    } catch (Exception e) {
                        Log.d("onResponse", "There is an error");
                        EventBus.getDefault().post(new SideMenuViewController.MessageEvent("GooglePlacesFailure"));
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GooglePlaceObject> call, Throwable t) {

                    EventBus.getDefault().post(new SideMenuViewController.MessageEvent("GooglePlacesFailure"));


                }

            });
        } else {

            service.getNearbyPlaceswithPageToken(API_KEY, nextPageToken).enqueue(new Callback<GooglePlaceObject>() {
                @Override
                public void onResponse(Call<GooglePlaceObject> call, Response<GooglePlaceObject> response) {
                    isLoading = false;
                    try {

                        status = response.body().getStatus();
                        Log.e("status", status);
                        if (status.equals("OK")) {
                            Log.e("nextToken", response.body().getNextPageToken());


                            nextPageToken = response.body().getNextPageToken();
                            placesArray.addAll(response.body().getResults());

                            for(Result objResult:placesArray)
                            {
                                objResult.setFromGoogle(true);
                            }

                            EventBus.getDefault().post(new SideMenuViewController.MessageEvent("GooglePlacesSuccess"));


                            Log.e("ResultsElse", "" + placesArray);

                        }

                    } catch (Exception e) {
                        Log.d("onResponse", "There is an error");
                        EventBus.getDefault().post(new SideMenuViewController.MessageEvent("GooglePlacesFailure"));
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GooglePlaceObject> call, Throwable t) {
                    EventBus.getDefault().post(new SideMenuViewController.MessageEvent("GooglePlacesFailure"));


                }

            });
        }
    }

    public void getPlacesInformationFromOurCloud(double latitude, double longitude, int radius) {

        final PlaceObject requestBody = new PlaceObject();

        requestBody.setLatitude(String.valueOf(latitude));
        requestBody.setLongitude(String.valueOf(longitude));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.home_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ServerApisInterface api = retrofit.create(ServerApisInterface.class);


        Call<PlaceObject> callable = api.GetDoctorssInfo(requestBody);

        //final long finalUnixTime = unixTime;
        callable.enqueue(new Callback<PlaceObject>() {
            @Override
            public void onResponse(Call<PlaceObject> call, final Response<PlaceObject> response) {
                // verifyDialog.dismiss();
                FindDoctorsViewController.dialog.dismiss();

                String statusCode = response.body().getResponse();
                Log.e("codefor3placesArray", "call" + statusCode);
                if (!statusCode.equals(null)) {
                    if (statusCode.equals("3")) {

                        ArrayList<PlaceResult> placeList = response.body().getLocations();

                        Log.e("placesArray", "" + placeList.size());

                        for (PlaceResult objResult : placeList) {
                            Result obj = new Result();
                            obj.setName(objResult.getName());
                            obj.setVicinity(objResult.getAddress());
                            obj.setEmail(objResult.getEmail());
                            obj.setPhone(objResult.getPhone());
                            obj.setSpecilization(objResult.getSpecialization());
                            Log.e("ResultArray", "" + obj.getName());
                            Geometry geometry = new Geometry();
                            Location objLoc = new Location();
                            objLoc.setLat(Double.parseDouble(objResult.getLoc().get(0)));
                            objLoc.setLng(Double.parseDouble(objResult.getLoc().get(1)));
                            geometry.setLocation(objLoc);
                            obj.setGeometry(geometry);
                            obj.setFromGoogle(false);

                            placesArray.add(0, obj);
                        }

                        EventBus.getDefault().post(new SideMenuViewController.MessageEvent("PlacesSuccess"));


                    } else {

                        EventBus.getDefault().post(new SideMenuViewController.MessageEvent("PlacesFailure"));

                        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    }


                }
            }

            @Override
            public void onFailure(Call<PlaceObject> call, Throwable t) {

                EventBus.getDefault().post(new SideMenuViewController.MessageEvent("PlacesFailure"));

            }
        });


    }

    public static String googlePhotoURL(String photoReference, int maxWidth) {

        String googlePhotosHost = "https://maps.googleapis.com/maps/api/place/photo";

        String finalString = googlePhotosHost + "?maxwidth=" + maxWidth + "&key=" + API_KEY + "&photoreference=" + photoReference;
        //  return URL.init(string: "\(googlePhotosHost)?maxwidth=\(maxWidth)&key=\(AppDelegate.googlePlacesAPIKey)&photoreference=\(photoReference)")
        return finalString;
    }
    public boolean canLoadMore() {

        if (status.equals("OK") && nextPageToken != null && nextPageToken.toString().length() > 0) {
            return true;
        } else {
            return false;
        }
    }
    public void getDirectionsInformation(String origin, String destination) {
        // Example :  http://maps.googleapis.com/maps/api/directions/json?origin=Chicago,IL&destination=Los%20Angeles,CA&sensor=false
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.GOOGLE_MAPS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.e("originLocation", "call" +origin+"destination"+destination);

        ServerApisInterface service = retrofit.create(ServerApisInterface.class);
        String mode = "driving";
        service.getDirections(origin, destination, API_KEY, mode).enqueue(new Callback<GooglePlaceObject>() {

            @Override
            public void onResponse(Call<GooglePlaceObject> call, Response<GooglePlaceObject> response) {
                isLoading = false;
                try {

                    status = response.body().getStatus();
                    Log.e("status", status);
                    if (status.equals("OK")) {
                        Log.e("firstRoute", "" + response.body().getRoutes().get(0));

                        if (response.body().getRoutes().size() > 0) {
                            selectedRoute = response.body().getRoutes().get(0);

                            overviewPolyline = selectedRoute.getOverviewPolyLine();

                            encodedRoutePath = selectedRoute.getOverviewPolyLine().getPoints();

                            fillDistanceAndTime();
                            EventBus.getDefault().post(new SideMenuViewController.MessageEvent("GoogleRoutesSuccess"));
                        }
                    } else {
                        Log.e("RoutesInfo", "No Routes Found");
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    EventBus.getDefault().post(new SideMenuViewController.MessageEvent("GoogleRoutesFailure"));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GooglePlaceObject> call, Throwable t) {

                EventBus.getDefault().post(new SideMenuViewController.MessageEvent("GoogleRoutesFailure"));

            }
        });
    }
    public BlockingQueue<Result> getPlaceDetails(final Result objResult)
    {
        final  BlockingQueue<Result> blockingQueue = new ArrayBlockingQueue<Result>(1);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ServerApisInterface.GOOGLE_MAPS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApisInterface service = retrofit.create(ServerApisInterface.class);
        service.getPlaceDetailsWithPlaceId(API_KEY,objResult.getPlaceId()).enqueue(new Callback<GooglePlaceObject>() {
            @Override
            public void onResponse(Call<GooglePlaceObject> call, Response<GooglePlaceObject> response) {

                if (response.isSuccessful()) {
                    if (response.body().getResult() != null) {
                        objResult.setFormattedPhoneNumber(response.body().getResult().getFormattedPhoneNumber());
                        objResult.setOpen_now(response.body().getResult().getOpen_now());
                        objResult.setWebsite(response.body().getResult().getWebsite());
                        Log.e("FormattedPhoneNumber", "call" + response.body().getResult().getFormattedPhoneNumber());
                        Log.e("setOpen_now", "call" + response.body().getResult().getOpen_now());
                        Log.e("setWebsite", "call" +  response.body().getResult().getWebsite());
                        Log.e("setWebsite", "call" +  response.body().getResult().getPlaceId());
                        blockingQueue.add(objResult);

                    }
                }
            }
            @Override
            public void onFailure(Call<GooglePlaceObject> call, Throwable t) {
            }
        });

        return blockingQueue;

    }


    public void fillDistanceAndTime() {

        int totalDistance = 0;
        int totalSeconds = 0;

        for (Leg objleg : selectedRoute.getLegs()) {


            totalDistance = totalDistance + objleg.getDistance().getValue();
            totalSeconds = totalSeconds + objleg.getDuration().getValue();
            Log.e("objleg", "call" + totalDistance+"callll"+totalSeconds);

        }

        double distanceInKm = totalDistance / 1000;
        double t = totalDistance - (distanceInKm * 1000);

        Log.e("meters", "" + t);

        distanceString = String.format("%.1f Km", distanceInKm);
        Log.e("distanceString", "call" + distanceString);


        int mins = totalSeconds / 60;
        int hours = mins / 60;
        int days = hours / 24;
        int remainingHours = hours % 24;
        int remainingMins = mins % 60;
        int remainingSecs = totalSeconds % 60;


        if (days > 0) {
            etaTimeString = String.valueOf(days) + "\tday," + String.valueOf(remainingHours) + "\thour, " + String.valueOf(remainingMins) + "\tmins, " + String.valueOf(remainingSecs) + "\t secs.";

        } else if (hours > 0) {

            etaTimeString = String.valueOf(remainingHours) + "\thour, " + String.valueOf(remainingMins) + "\t mins, " + String.valueOf(remainingSecs) + "\t secs.";

        } else if (mins > 0) {

            etaTimeString = String.valueOf(remainingMins) + "\t mins, " + String.valueOf(remainingSecs) + "\t secs.";

        }
        if (totalDistance < 1000) {
            distanceString = String.format("%.1f meters", t);
        } else {

            distanceString = String.format("%.1f Km", distanceInKm);
        }

        Log.e("totalDistance", "call" + distanceString);

    }


}



