package com.example.wave.spectrumhuman.LoginModule;

/**
 * Created by jacobliu on 10/29/17.
 */

public class Constants {

    public static int rbcMinimumValue=0;
    public static int rbcMaximumValue=250;
    public static float billirubinMinimumValue=0;
    public static float billirubinMaximumValue=5.0f;
    public static float uroboliogenMinimumValue=0.1f;
    public static float uroboliogenMaximumValue=20.0f;
    public static int ketonesMinimumValue=0;
    public static int ketonesMaximumValue=200;
    public static int proteinMinimumValue=0;
    public static int proteinMaximumValue=1000;
    public static float nitriteMinimumValue=0.01f;
    public static float nitriteMaximumValue=0.10f;
    public static int glucoseMinimumValue=0;
    public static int glucoseMaximumValue=1500;
    public static float phMinimumValue=1f;
    public static float phMaximumValue=12.0f;
    public static float sgMinimumValue=1.00f;
    public static float sgMaximumValue=1.050f;
    public static int leucocyteMinimumValue=0;
    public static int leucocyteMaximumValue=600;



public static   String shopping_url= "http://www.vedaslabs.com/laptops.html";
    public  static String aboutus_url="http://www.vedaslabs.com";
    public enum RegisterTypes {
        Manual ("Manual"),
        Facebook ("Facebook"),
        Google ("Google"),
        Twitter ("Twitter");

        private final String name;

        private RegisterTypes(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }


    public enum WifiSecureTypes {
        OPEN ("OPEN"),
        WPA ("WPA"),
        WPA2 ("WPA2"),
        WEP ("WEP");

        private final String name;

        private WifiSecureTypes(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }



    public enum AppLanguages {

        English("English"),
        Traditional("Traditional"),
        Simplified("Simplified");

        /*name.add("Arabic");
         ("Chinese(Traditional)");
        name.add("Spanish");
        name.add("Chinese(simplified)");
        name.add("English (India)");
        name.add("English (United States)");
        name.add("Freench");
        name.add("German");
        name.add("Greek");
        name.add("Japanese");
        name.add("Portuguese");
        name.add("Russian");*/

        private final String name;

        private AppLanguages(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }
    public enum GooglePlacesTypes {
        dentist("dentist"),
        doctor("doctor"),
        health("health"),
        veterinary_care("veterinary_care"),
        pharmacy("pharmacy"),
        hospital("hospital");

        private final String name;

        private GooglePlacesTypes(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }
}
