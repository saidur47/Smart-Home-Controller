package com.project.cse499.homecontrol.model;

import com.google.gson.annotations.SerializedName;

public class DeviceLogEntry {
    @SerializedName("device")
    private String device;
    @SerializedName("user")
    private String user;
    @SerializedName("action")
    private int action;
    @SerializedName("logtime")
    private String logtime;

    public void setDevice(String value){
        this.device = value;
    }
    public void setUser(String value){
        this.user = value;
    }
    public void setAction(int value){
        this.action = value;
    }
    public void setLogtime(String value){
        this.logtime = value;
    }

    public String getDevice(){
        return this.device;
    }

    public String getUser(){
        return this.user;
    }
    public int getAction(){
        return this.action;
    }
    public String getLogtime(){
        return this.logtime;
    }
}
