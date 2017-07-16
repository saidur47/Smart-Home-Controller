package com.project.cse499.homecontrol.model;

import com.google.gson.annotations.SerializedName;

public class House {

    @SerializedName("house_id")
    private int houseId;

    @SerializedName("name")
    private String houseName;

    @SerializedName("details")
    private String houseDetails;



    public int getHouseId(){
        return this.houseId;
    }

    public String getHouseName(){
        return this.houseName;
    }

    public void setHouseName(String houseName){
        this.houseName = houseName;
    }
    public String getHouseDetails(){
        return  this.houseDetails;
    }

    public void setRoomDetails(String houseDetails){
        this.houseDetails = houseDetails;
    }
}
