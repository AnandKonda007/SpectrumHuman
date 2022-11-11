package com.example.wave.spectrumhuman.Models;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable(tableName = "User")
public class User {
    public User(String userName, String password, String latitude, String longitude, String registerType, String registerTime, Boolean isVerified)
    {
        this.userName = userName;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.register_type = registerType;
        this.registerTime = registerTime;
        this.isVerified = isVerified;
    }
    @DatabaseField(id = true, columnName = "userName")
    private String userName;

    @DatabaseField
    private String password;

    @DatabaseField
    private String latitude;

    @DatabaseField
    private String longitude;

    @DatabaseField
    private String register_type;

    @DatabaseField
    private String registerTime;

    @DatabaseField
    private String registerLocation;

    @DatabaseField
    private Boolean isVerified;

    @DatabaseField(columnName = "preferedLanguage")
    String preferedLanguage;


    @DatabaseField
    private String device;


    @DatabaseField
    private String token;


    ////////////////////////////////////////////////////

    @ForeignCollectionField
    private ForeignCollection<Member> members;

    @ForeignCollectionField
    private ForeignCollection<DeviceInformation> deviceInformations;

    @ForeignCollectionField
    private ForeignCollection<Language> languages;

    @ForeignCollectionField
    private ForeignCollection<DoctorInformationModel> doctors;

    public ForeignCollection<DoctorInformationModel> getDoctors() {
        return doctors;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getRegisterLocation() {
        return registerLocation;
    }


    public void setRegisterLocation(String registerLocation) {
        this.registerLocation = registerLocation;
    }


    public String getRegisterType() {
        return register_type;
    }


    public void setRegisterType(String registerType) {
        this.register_type = registerType;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getRegisterTime() {
        return registerTime;
    }


    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getPreferedLanguage() {
        return preferedLanguage;
    }

    public void setPreferedLanguage(String preferedLanguage) {
        this.preferedLanguage = preferedLanguage;


    }

////////////// deviceiddevicetoken//////////////////
    public String getDevice() {return device;}
    public void setDevice(String device) {this.device = device;}

    public String getToken() {return token;}
    public void setToken(String token) {this.token = token;}


//////// foregin collection  field///////

    public ForeignCollection<Member> getMembers() {
        return members;
    }

    public ForeignCollection<DeviceInformation> getDeviceInformations() {
        return deviceInformations;
    }

    public ForeignCollection<Language> getLanguages() {
        return languages;
    }

    public User() {
    }

}
