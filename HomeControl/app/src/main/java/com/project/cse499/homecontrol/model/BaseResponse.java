package com.project.cse499.homecontrol.model;

import com.google.gson.annotations.SerializedName;

public class BaseResponse
{
    @SerializedName("error")
    private boolean errorVal;
    @SerializedName("message")
    private String errorMessage;

    public  boolean hasError(){
        return this.errorVal;
    }
    public String getMessage(){
        return this.errorMessage;
    }
}
