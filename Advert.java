package com.example.task91p;

import java.util.ArrayList;

public class Advert {
    // Custom class to store advert details
    public static ArrayList<Advert> advertList = new ArrayList<>();
    private int id;
    private String type;
    private String title;
    private String description;
    private String date;
    private String location;
    private String latitude;
    private String longitude;
    private int userId;
    private String phone;
    private String deleteFlag;

    public Advert(int id, String type, String title, String description, String date, String location, String latitude, String longitude, int userId, String phone, String deleteFlag){
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.phone = phone;
        this.deleteFlag = deleteFlag;
    }

    public Advert(int id, String type, String title, String description, String date, String location, String latitude, String longitude, int userId, String phone){
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.phone = phone;
        this.deleteFlag = "N";
    }

    public int getId() {return id;}

    public String getType() { return type;}
    public String getTitle() { return title;}
    public String getDescription() {return description;}
    public String getDate() {return date;}
    public String getLocation() { return location;}
    public String getLat() { return latitude;}
    public String getLong() { return longitude;}
    public int getUserId() { return userId;}
    public String getPhone() { return phone;}
    public String getDeleteFlag() {return deleteFlag;}
    public static Advert getTaskById(int id){
        for (Advert advert : advertList){
            if(advert.getId() == id){
                return advert;
            }
        }
        return null;
    }

    public void setType(String type) { this.type = type; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(String date){ this.date = date; }
    public void setLocation(String location){ this.location = location; }
    public void setLat(String latitude){ this.latitude = latitude; }
    public void setLong(String longitude){ this.longitude = longitude; }
    public void setPhone(String phone){ this.phone = phone; }
    public void setDeleteFlag(String deleteFlag) {this.deleteFlag = deleteFlag;}
}
