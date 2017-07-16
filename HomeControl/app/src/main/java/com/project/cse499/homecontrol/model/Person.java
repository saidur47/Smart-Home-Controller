package com.project.cse499.homecontrol.model;

import com.google.gson.annotations.SerializedName;

public class Person {
    @SerializedName("name")
    private String name;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("groupid")
    private int groupId;
    @SerializedName("authcode")
    private int authenticationCode;
    public void setName(String value){
        this.name = value;
    }
    public void setUsername(String value){
        this.username = value;
    }
    public void setEmail(String value){
        this.email = value;
    }

    public void setPassword(String value){
        this.password = value;
    }
    public void setGroupId(int value){
        this.groupId = value;
    }
    public void setAuthenticationCode(int value){
        this.authenticationCode = value;
    }

    public  String getName(){
        return this.name;
    }
    public  String getUsername(){
        return this.username;
    }
    public  String getEmail(){
        return this.email;
    }
    public  String getPassword(){
        return this.password;
    }
    public  int getGroupId(){
        return this.groupId;
    }
    public  int getAuthenticationCode(){
        return this.authenticationCode;
    }

}
