package com.example.wave.spectrumhuman.ServerAPIS;

import com.example.wave.spectrumhuman.ServerObjects.AddDoctorServerObjects;
import com.example.wave.spectrumhuman.ServerObjects.ContactUsServerObject;
import com.example.wave.spectrumhuman.ServerObjects.DeviceServerObjects;
import com.example.wave.spectrumhuman.ServerObjects.LanguageServerObjects;
import com.example.wave.spectrumhuman.ServerObjects.MemberServerOject;
import com.example.wave.spectrumhuman.ServerObjects.UrineResultsServerObject;
import com.example.wave.spectrumhuman.ServerObjects.UserServerObject;
import com.example.wave.spectrumhuman.googlemaps.GooglePlaceObject;
import com.example.wave.spectrumhuman.googlemaps.PlaceObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by WAVE on 10/20/2017.
 */

public interface ServerApisInterface {
    public static String home_URL = "http://54.234.239.245/spectrum/";
    public static String home_Image_URL = "http://54.234.239.245";
    String GOOGLE_MAPS_BASE_URL = "https://maps.googleapis.com";


   /* public static String home_URL = "http://117.247.13.135:8080/spectrum/";
    public static String home_Image_URL = "http://117.247.13.135:8080";
*/
    //for register
    @POST("register")
    Call<UserServerObject> register(@Body UserServerObject body);

    ///for verification
    @POST("verify")
    Call<UserServerObject> verify(@Body UserServerObject body);

    //login
    @POST("login")
    Call<UserServerObject> login(@Body UserServerObject body);

    //for personalinfo
    @POST("personalinfo")
    Call<MemberServerOject> personalInfo(@Body MemberServerOject memberBody);

    @PUT("personalinfo")
    Call<MemberServerOject> editPersonalInfo(@Body MemberServerOject memberBody);

    @POST("forgot")
    Call<UserServerObject> forgot(@Body UserServerObject body);

    @POST("newpassword")
    Call<UserServerObject> newpassword(@Body UserServerObject body);

    @POST("changepassword")
    Call<UserServerObject> changepassword(@Body UserServerObject body);


    @POST("feedback")
    Call<ContactUsServerObject> feedback(@Body ContactUsServerObject body);

    @POST("relationship")
    Call<MemberServerOject> addMemberInfo(@Body MemberServerOject body);

    @PUT("relationship")
    Call<MemberServerOject> editMemberInfo(@Body MemberServerOject body);

    @HTTP(method ="DELETE",path ="relationship",hasBody = true)
    Call<MemberServerOject> deleteMemberInfo(@Body MemberServerOject body);


    @POST("active")
    Call<MemberServerOject> ActiveMemberInfo(@Body MemberServerOject body);

    @PUT("active")
    Call<MemberServerOject> InActiveMemberInfo(@Body MemberServerOject body);

    @POST("active")
    Call<MemberServerOject> InActiveAddMemberInfo(@Body MemberServerOject body);

    @POST("deviceinfo")
    Call<DeviceServerObjects> deviceinfo(@Body DeviceServerObjects body);

    @PUT("deviceinfo")
    Call<DeviceServerObjects> editDeviceinfo(@Body DeviceServerObjects body);


    @HTTP(method ="DELETE",path ="deviceinfo",hasBody = true)
    Call<DeviceServerObjects> deleteDeviceinfo(@Body DeviceServerObjects body);

    @POST("urinetest")
    Call<UrineResultsServerObject> urineData(@Body UrineResultsServerObject body);

    @HTTP(method ="DELETE",path ="urinetest",hasBody = true)
    Call<UrineResultsServerObject> deleteurineData(@Body UrineResultsServerObject body);

    @HTTP(method ="GET",path ="lang",hasBody = false)
    Call<LanguageServerObjects> getLanguages();

    @PUT("lang")
    Call<LanguageServerObjects> updatePreferLanguage(@Body LanguageServerObjects body);


////////////////for maps

    @GET("/maps/api/place/nearbysearch/json")
    Call<GooglePlaceObject> getNearbyPlaceswithOutPageToken(@Query("type") String type, @Query("location") String location, @Query("radius") Integer radius, @Query("key") String key);

    @GET("/maps/api/place/nearbysearch/json")
    Call<GooglePlaceObject> getNearbyPlaceswithPageToken(@Query("key") String key, @Query("pagetoken") String pagetoken);


    // http://maps.googleapis.com/maps/api/directions/json?origin=Chicago,IL&destination=Los%20Angeles,CA&sensor=false
    @GET("/maps/api/directions/json")
    Call<GooglePlaceObject> getDirections(@Query("origin") String origin, @Query("destination") String destination,@Query("key") String key,@Query("Mode") String mode);


    @POST("doctor")
    Call<AddDoctorServerObjects> addDoctorData(@Body AddDoctorServerObjects body);

    @POST("doctor")
    Call<AddDoctorServerObjects> editDoctorData(@Body AddDoctorServerObjects body);

    @POST("share")
    Call<AddDoctorServerObjects> share(@Body AddDoctorServerObjects body);

    //for hospital
    @POST("hospital")
    Call<PlaceObject> GetDoctorssInfo(@Body PlaceObject body);

    //for getting doctor status
    @POST("status")
    Call<AddDoctorServerObjects>  getDoctorStatus(@Body AddDoctorServerObjects body);


    //logout
    @POST("logout")
    Call<UserServerObject> logout(@Body UserServerObject body);

 ///
 @GET("/maps/api/place/details/json")
 Call<GooglePlaceObject> getPlaceDetailsWithPlaceId(@Query("key") String key,@Query("placeid") String placeid);

}
