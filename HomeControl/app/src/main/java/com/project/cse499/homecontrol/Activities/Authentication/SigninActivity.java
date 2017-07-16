package com.project.cse499.homecontrol.Activities.Authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.cse499.homecontrol.Activities.Others.LandingActivity;
import com.project.cse499.homecontrol.R;
import com.project.cse499.homecontrol.helpers.*;
import com.project.cse499.homecontrol.model.AuthenticatedUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SigninActivity extends AppCompatActivity {


    EditText etUname ,etPass ;
    private ProgressDialog pDialog;

    private Button btnAuthenticate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        etUname = (EditText) findViewById(R.id.email);
        etPass = (EditText) findViewById(R.id.pass);
        etUname.clearFocus();
        etPass.clearFocus();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        btnAuthenticate = (Button) findViewById(R.id.btnAuthenticate);

        btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAuthenticToken(v);
            }
        });

    }

    private void getAuthenticToken(View v){
        showpDialog();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonHelpers.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        String uname = etUname.getText().toString();
        String upass = etPass.getText().toString();

        com.project.cse499.homecontrol.APIService service = retrofit.create(com.project.cse499.homecontrol.APIService.class);
        Call<AuthenticatedUser> call = service.authenticateUser(uname,upass);
        call.enqueue(new Callback<AuthenticatedUser>() {
            @Override
            public void onResponse(Call<AuthenticatedUser> call, Response<AuthenticatedUser> response) {
                AuthenticatedUser us = response.body();
                SharedPreferences settings = getSharedPreferences(CommonHelpers.AUTH_TOKEN, 0);
                SharedPreferences.Editor editor = settings.edit();
                if(us.getErrorStatus().compareTo(false) == 0 && us.getAuthenticToken().length()>0){
                    editor.putString("jwtToken", "Bearer " + us.getAuthenticToken());
                    editor.commit();
                    startActivity(new Intent(SigninActivity.this, LandingActivity.class));

                }else{
                    editor.putString("jwtToken", "");
                    editor.commit();
                    showErrorMessage();
                }


                hidepDialog();
            }

            @Override
            public void onFailure(Call<AuthenticatedUser> call, Throwable t) {
                hidepDialog();
            }
        });
    }


    private  void showErrorMessage(){
        Context context = getApplicationContext();
        CharSequence text = "Wrong Username or Password";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
