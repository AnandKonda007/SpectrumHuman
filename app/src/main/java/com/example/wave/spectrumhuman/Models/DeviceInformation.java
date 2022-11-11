package com.example.wave.spectrumhuman.Models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by dell on 28-09-2017.
 */

public class DeviceInformation {
    public DeviceInformation(){

    }
    public DeviceInformation(User user,String deviceName, String deviceId, String batteryPercentage, String firmwareversion, Boolean isconnected,String secureType) {
        this.deviceName = deviceName;
        this.deviceId = deviceId;
        this.batteryPercentage = batteryPercentage;
        this.firmwareversion = firmwareversion;
        this.isconnected = isconnected;
        this.user=user;
        this.secureType = secureType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(String batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public String getFirmwareversion() {
        return firmwareversion;
    }

    public void setFirmwareversion(String firmwareversion) {
        this.firmwareversion = firmwareversion;
    }

    public Boolean getIsconnected() {
        return isconnected;
    }

    public void setIsconnected(boolean isconnected) {
        this.isconnected = isconnected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @DatabaseField(generatedId = true)
    int id;

    @DatabaseField(columnName = "deviceName")
    private String deviceName;



    @DatabaseField(columnName = "deviceId")
    private String deviceId;

    @DatabaseField(columnName = "batteryPercentage")
    private String batteryPercentage;

    @DatabaseField(columnName = "firmwareVersion")
    private String firmwareversion;

    @DatabaseField(columnName = "isConnected", dataType = DataType.BOOLEAN)
    private boolean isconnected;

    @DatabaseField(columnName = "secureType")
    private String secureType;

    @DatabaseField(columnName = "user_id", canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private User user;




    public String getSecureType() {
        return secureType;
    }

    public void setSecureType(String secureType) {
        this.secureType = secureType;
    }




    private User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
