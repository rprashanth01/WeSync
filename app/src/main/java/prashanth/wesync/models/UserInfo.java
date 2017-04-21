package prashanth.wesync.models;

import java.util.ArrayList;

/**
 * Created by Abhi on 4/17/2017.
 */

public class UserInfo {

    private String name;
    private String email;
    private String phoneNumber;
    private double latitude;
    private double longitude;
    private ArrayList<String> interests = new ArrayList<String>();

    public UserInfo() {
    }

    public UserInfo(String name, String email, String phoneNumber, double latitude, double longitude, ArrayList<String> interests) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.interests = interests;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }
}
