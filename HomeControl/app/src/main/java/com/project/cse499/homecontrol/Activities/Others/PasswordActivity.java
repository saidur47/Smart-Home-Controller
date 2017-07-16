package com.project.cse499.homecontrol.Activities.Others;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.cse499.homecontrol.Activities.Authentication.MainActivity;
import com.project.cse499.homecontrol.R;
import com.project.cse499.homecontrol.helpers.CommonHelpers;
import com.project.cse499.homecontrol.model.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PasswordActivity extends AppCompatActivity {

    private EditText etUsername,etOldPassword,etNewPassword;
    private Button btnSave,btnCancel;
    private String tokenValue="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        SharedPreferences settings = getSharedPreferences(CommonHelpers.AUTH_TOKEN, 0);
        tokenValue =settings.getString("jwtToken","Nothing Found");

        etUsername = (EditText) findViewById(R.id.etChnUsername);
        etOldPassword = (EditText) findViewById(R.id.etChnOldPass);
        etNewPassword = (EditText) findViewById(R.id.etChnNewPass);
        etUsername.setText("");
        etOldPassword.setText("");
        etNewPassword.setText("");

        btnSave =(Button) findViewById(R.id.btnSaveChange);
        btnCancel=(Button) findViewById(R.id.btnCancelChange);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PasswordActivity.this, LandingActivity.class));
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChangedPassword();
            }
        });
    }
    private void saveChangedPassword(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonHelpers.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        com.project.cse499.homecontrol.APIService service = retrofit.create(com.project.cse499.homecontrol.APIService.class);
        Call<BaseResponse> call = service.changePassword(this.tokenValue,etUsername.getText().toString(),etOldPassword.getText().toString(),etNewPassword.getText().toString());

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse br =  response.body();
                if(!br.hasError()){
                    startActivity(new Intent(PasswordActivity.this, MainActivity.class));
                    finish();
                }else{
                    showMessage(br.getMessage());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                showMessage("Unfortunately No Response");
            }
        });
    }
    private void showMessage(CharSequence msg) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }
}
