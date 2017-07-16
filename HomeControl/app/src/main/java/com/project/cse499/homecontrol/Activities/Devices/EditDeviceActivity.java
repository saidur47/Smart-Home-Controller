package com.project.cse499.homecontrol.Activities.Devices;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.project.cse499.homecontrol.R;
import com.project.cse499.homecontrol.helpers.CommonHelpers;
import com.project.cse499.homecontrol.model.Device;
import com.project.cse499.homecontrol.model.UpdateDevice;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditDeviceActivity extends AppCompatActivity {

    private EditText etName,etDetails;
    private Button btnSave,btnCancel;
    private int roomId,houseId,deviceId;
    private String tokenValue="",roomName="";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_device);


//        Bundle bundle = getIntent().getExtras();
//        roomName = bundle.getString("RoomName");
//        roomId = bundle.getInt("RoomId");
//        houseId = bundle.getInt("HouseId");
//        deviceId = bundle.getInt("DeviceId");


        SharedPreferences settings = getSharedPreferences(CommonHelpers.AUTH_TOKEN, 0);
        tokenValue = settings.getString("jwtToken", "");
        SharedPreferences settingsHouse = getSharedPreferences(CommonHelpers.HOUSE, 0);
        houseId = settingsHouse.getInt("HouseId",0);
        SharedPreferences settingsRoom = getSharedPreferences(CommonHelpers.ROOM, 0);
        roomId = settingsRoom.getInt("RoomId",0);
        SharedPreferences settingsDevice= getSharedPreferences(CommonHelpers.DEVICE, 0);
        deviceId = settingsDevice.getInt("DeviceId",0);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);


        btnSave = (Button) findViewById(R.id.btnSaveDevice);
        btnCancel = (Button) findViewById(R.id.btnCancelDevice);
        etName  = (EditText) findViewById(R.id.etDeviceName);
        etDetails = (EditText) findViewById(R.id.etDeviceDetails);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDeviceDetails();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToDeviceList();
            }
        });

        populateDeviceDetails();
    }

    private void goBackToDeviceList(){
        startActivity(new Intent(EditDeviceActivity.this, DevicesActivity.class));
        finish();
    }
    private void populateDeviceDetails(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonHelpers.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        com.project.cse499.homecontrol.APIService service = retrofit.create(com.project.cse499.homecontrol.APIService.class);
        Call<Device> call = service.getDeviceDetails(this.tokenValue,houseId,roomId,deviceId);

        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                Device device = response.body();
                etName.setText(device.getName());
                etDetails.setText(device.getDetails());
                hidepDialog();
            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {
                hidepDialog();
            }
        });
    }
    private void updateDeviceDetails(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonHelpers.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        com.project.cse499.homecontrol.APIService service = retrofit.create(com.project.cse499.homecontrol.APIService.class);
        Call<UpdateDevice> call = service.updateDeviceDetails(this.tokenValue,houseId,roomId,deviceId,etName.getText().toString(),etDetails.getText().toString());

        call.enqueue(new Callback<UpdateDevice>() {
            @Override
            public void onResponse(Call<UpdateDevice> call, Response<UpdateDevice> response) {
                hidepDialog();
                goBackToDeviceList();
            }

            @Override
            public void onFailure(Call<UpdateDevice> call, Throwable t) {
                hidepDialog();
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
}
