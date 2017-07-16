package com.project.cse499.homecontrol.model;

import com.google.gson.annotations.SerializedName;


public class UpdateDevice {
    @SerializedName("device_name")
    private  String name;
    @SerializedName("device_details")
    private  String details;

}
