package com.project.cse499.homecontrol.Activities.Authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.cse499.homecontrol.R;
import com.project.cse499.homecontrol.helpers.CommonHelpers;
import com.project.cse499.homecontrol.model.BaseResponse;
import com.project.cse499.homecontrol.model.Person;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private Button btnRegister,btnCancel;
    private EditText etName,etUsername, etMail,etPassword,etGroupId,etAuthCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        btnCancel = (Button) findViewById(R.id.btnCancelRegister);
        btnRegister  =(Button) findViewById(R.id.btnRegisterUser);

        etName = (EditText) findViewById(R.id.etName);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etMail = (EditText) findViewById(R.id.etMail);
        etPassword= (EditText) findViewById(R.id.etPassword);
        etGroupId = (EditText) findViewById(R.id.etGroupId);
        etAuthCode = (EditText) findViewById(R.id.etAuthCode);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectFormData();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                finish();
            }
        });
    }
    private void collectFormData(){
        String name = etName.getText().toString();
        String username = etUsername.getText().toString();
        String email = etMail.getText().toString();
        String password = etPassword.getText().toString();
        String strAuthCode = etAuthCode.getText().toString();
        int groupId=-1,authCode=-1;
        boolean hasError=false;

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            hasError = true;
            showMessage("Malformed Email address");
        }

        try {
            groupId = Integer.parseInt(etGroupId.getText().toString());
        }catch (Exception ex){
            hasError = true;
            showMessage("Group Id has to be digit Only");
        }

        try{
            authCode = Integer.parseInt(etAuthCode.getText().toString());
        }catch (Exception ex){
            hasError = true;
            showMessage("Authentication code has to be digit Only");
        }

        if(!hasError){
            Person person = new Person();
            person.setName(name);
            person.setUsername(username);
            person.setEmail(email);;
            person.setPassword(password);
            person.setGroupId(groupId);
            person.setAuthenticationCode(authCode);
            registerNewUser(person);
        }
    }


    private void registerNewUser(Person person){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonHelpers.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        com.project.cse499.homecontrol.APIService service = retrofit.create(com.project.cse499.homecontrol.APIService.class);
        Call<BaseResponse> call = service.registerUser(person.getName(),person.getUsername(),person.getEmail(),person.getPassword(),person.getGroupId(),person.getAuthenticationCode());
        showpDialog();
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                BaseResponse br = response.body();
                if(br.hasError()){
                    showMessage(br.getMessage());
                }else{
                    startActivity(new Intent(RegisterActivity.this,RegisterSuccess.class));
                    finish();
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                hidepDialog();
                showMessage("Huge Mistake");
            }
        });
    }
    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void showMessage(CharSequence msg) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }
}
