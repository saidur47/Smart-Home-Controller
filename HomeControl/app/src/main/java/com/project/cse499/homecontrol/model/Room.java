package com.project.cse499.homecontrol.model;

import com.google.gson.annotations.SerializedName;

public class Room {

    @SerializedName("room_id")
    private int roomId;

    @SerializedName("name")
    private String roomName;

    @SerializedName("details")
    private String roomDetails;

    public int getRoomId(){
        return this.roomId;
    }

    public String getRoomName(){
        return this.roomName;
    }

    public void setRoomName(String roomName){
        this.roomName = roomName;
    }

    public String getRoomDetails(){
        return  this.roomDetails;
    }

    public void setRoomDetails(String roomDetails){
        this.roomDetails = roomDetails;
    }
}
