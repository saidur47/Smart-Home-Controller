package com.project.cse499.homecontrol.model;

import com.google.gson.annotations.SerializedName;

public class Device {
    @SerializedName("device_id")
    private int deviceId;
    @SerializedName("room_id")
    private  int roomId;
    @SerializedName("house_id")
    private int houseId;
    @SerializedName("name")
    private String name;
    @SerializedName("details")
    private String details;
    @SerializedName("swtich_state")
    private int switchState;
    @SerializedName("lastchangedby")
    private int lastChangedById;
    @SerializedName("lastchangedfrom")
    private int lastChangedFrom;
    @SerializedName("device_type")
    private int deviceType;

    //setters
    public void setName(String name){
        this.name=name;
    }
    public void setDetails(String details){
        this.details = details;
    }
    public void setSwitchState(int sState){
         this.switchState = sState;
    }
    public void setDeviceType(int dType){
        this.deviceType = dType;
    }

//getters
    public  int getDeviceId(){
        return  this.deviceId;
    }
    public int getRoomId(){
        return this.roomId;
    }
    public int getHouseId(){
        return this.houseId;
    }
    public String getName(){
        return this.name;
    }
    public String getDetails(){
        return this.details;
    }
    public int getSwitchState(){
        return this.switchState;
    }
    public int getLastChangedById(){
        return this.lastChangedById;
    }
    public int getLastChangedFrom(){
        return  this.lastChangedFrom;
    }
    public int getDeviceType(){
        return this.deviceType;
    }


}
