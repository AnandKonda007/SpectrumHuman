package com.example.wave.spectrumhuman.googlemaps;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jacobliu on 1/5/18.
 */

public class PlaceResult {

    private  String  address;
    private  String  name;
    private  String  email;
    private  String  specialization;
    private  String  phone;
    private ArrayList<String> loc;

    public ArrayList<String> getLoc() {
        return loc;
    }

    public void setLoc(ArrayList<String> loc) {
        this.loc = loc;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}

