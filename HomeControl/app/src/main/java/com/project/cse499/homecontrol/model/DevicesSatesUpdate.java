package com.project.cse499.homecontrol.model;

import com.google.gson.annotations.SerializedName;

public class DevicesSatesUpdate {
    @SerializedName("device1")
    private int deviceOne;
    @SerializedName("device2")
    private int deviceTwo;
    @SerializedName("device3")
    private int deviceThree;
    @SerializedName("device4")
    private int deviceFour;

    //setters
    public void setDeviceOne(int val){
        this.deviceOne=val;
    }
    public void setDeviceTwo(int val){
        this.deviceTwo=val;
    }
    public void setDeviceThree(int val){
        this.deviceThree=val;
    }
    public void setDeviceFour(int val){
        this.deviceFour=val;
    }

    //getters
    public  int getDeviceOne(){
        return  this.deviceOne;
    }
    public  int getDeviceTwo(){
        return  this.deviceTwo;
    }
    public  int getDeviceThree(){
        return  this.deviceThree;
    }
    public  int getDeviceFour(){
        return  this.deviceFour;
    }

}
