package com.example.nabil.holidays;

/**
 * Created by Nabil on 8/1/2016.
 */
public class Holiday {

    private String name ="default value";
    private String date ="default value";
    private String observed ="default value";
    private String pub ="default value";

    public Holiday() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getObserved() {
        return observed;
    }

    public void setObserved(String observed) {
        this.observed = observed;
    }

    public String getPub() {
        return pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }
}
