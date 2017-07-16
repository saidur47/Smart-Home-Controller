package com.project.cse499.homecontrol.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by saif on 2017-02-25.
 */

public class AuthenticatedUser {
    @SerializedName("token")
    private String authenticToken;

    @SerializedName("error")
    private Boolean authenticationStatus;

    public String getAuthenticToken() {
        return this.authenticToken;
    }

    public void setAuthenticToken(String token) {
        this.authenticToken = token;
    }

    public Boolean getErrorStatus() {
        return this.authenticationStatus;
    }

    public void setErrorStatus(Boolean status) {
        this.authenticationStatus = status;
    }

}
