package com.project.cse499.homecontrol.Activities.Devices;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.project.cse499.homecontrol.Activities.Authentication.MainActivity;
import com.project.cse499.homecontrol.Activities.Rooms.RoomActivity;
import com.project.cse499.homecontrol.R;
import com.project.cse499.homecontrol.helpers.CommonHelpers;
import com.project.cse499.homecontrol.model.Device;
import com.project.cse499.homecontrol.model.DevicesSatesUpdate;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DevicesActivity extends AppCompatActivity {

    private String tokenValue="";
    private ProgressDialog pDialog;

    private String roomName;
    private int roomId;
    private int houseId;
    private boolean refreshingDataFromDB=false;
    Button btnRefreshData,btnViewLog,btnSignOut;
    ImageButton btnSwOne,btnSwTwo,btnSwThree,btnSwFour;
    Switch swOne, swTwo, swThree, swFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);


        SharedPreferences settings = getSharedPreferences(CommonHelpers.AUTH_TOKEN, 0);
        tokenValue = settings.getString("jwtToken", "");
        SharedPreferences settingsHouse = getSharedPreferences(CommonHelpers.HOUSE, 0);
        houseId = settingsHouse.getInt("HouseId",0);
        SharedPreferences settingsRoom = getSharedPreferences(CommonHelpers.ROOM, 0);
        roomId = settingsRoom.getInt("RoomId",0);
        roomName=settingsRoom.getString("RoomName","");

        try {
            getSupportActionBar().setTitle(roomName + ": List of Devices");
        }catch (Exception ex){}

        try{
            getActionBar().setTitle(roomName + ": List of Devices");
        }catch (Exception ex){}


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);



        btnRefreshData = (Button) findViewById(R.id.btnRefreshData);
        btnSignOut = (Button) findViewById(R.id.btnSignOutDL);
        btnViewLog  = (Button) findViewById(R.id.btnShowLog);

        btnSwOne = (ImageButton) findViewById(R.id.ibDeviceOne);
        btnSwTwo = (ImageButton) findViewById(R.id.ibDeviceTwo);
        btnSwThree = (ImageButton) findViewById(R.id.ibDeviceThree);
        btnSwFour = (ImageButton) findViewById(R.id.ibDeviceFour);

        swOne = (Switch) findViewById(R.id.swDeviceOne);
        swTwo = (Switch) findViewById(R.id.swDeviceTwo);
        swThree = (Switch) findViewById(R.id.swDeviceThree);
        swFour = (Switch) findViewById(R.id.swDeviceFour);

        btnRefreshData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceDetails();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(CommonHelpers.AUTH_TOKEN, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(DevicesActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnViewLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DevicesActivity.this, DeviceLog.class));
            }
        });

        swOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateDeviceInDB();
            }
        });
        swTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateDeviceInDB();
            }
        });
        swThree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateDeviceInDB();
            }
        });
        swFour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateDeviceInDB();
            }
        });

        btnSwOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(CommonHelpers.DEVICE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("DeviceId", 1);
                editor.commit();
                startActivity(new Intent(DevicesActivity.this, EditDeviceActivity.class));
            }
        });

        btnSwTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(CommonHelpers.DEVICE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("DeviceId", 2);
                editor.commit();
                startActivity(new Intent(DevicesActivity.this, EditDeviceActivity.class));
            }
        });
        btnSwThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(CommonHelpers.DEVICE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("DeviceId",3);
                editor.commit();
                startActivity(new Intent(DevicesActivity.this, EditDeviceActivity.class));
            }
        });
        btnSwFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(CommonHelpers.DEVICE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("DeviceId", 4);
                editor.commit();
                startActivity(new Intent(DevicesActivity.this, EditDeviceActivity.class));
            }
        });

        getDeviceDetails();
    }


    private void getDeviceDetails() {
        showpDialog();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonHelpers.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        com.project.cse499.homecontrol.APIService service = retrofit.create(com.project.cse499.homecontrol.APIService.class);
        Call<List<Device>> call = service.getListOfDevices(this.tokenValue,houseId,roomId);

        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                if(response.body().size() == 4) {
                    List<Device> device = response.body();
                    refreshingDataFromDB = true;

                    boolean setValue = false;
                    setValue = (device.get(0).getSwitchState() < 1) ? true : false;
                    swOne.setChecked(setValue);
                    setValue = (device.get(1).getSwitchState() < 1) ? true : false;
                    swTwo.setChecked(setValue);
                    setValue = (device.get(2).getSwitchState() < 1) ? true : false;
                    swThree.setChecked(setValue);
                    setValue = (device.get(3).getSwitchState() < 1) ? true : false;
                    swFour.setChecked(setValue);

                    swOne.setText(device.get(0).getName());
                    swTwo.setText(device.get(1).getName());
                    swThree.setText(device.get(2).getName());
                    swFour.setText(device.get(3).getName());

                    refreshingDataFromDB = false;
                }else{
                    swOne.setEnabled(false);
                    swTwo.setEnabled(false);
                    swThree.setEnabled(false);
                    swFour.setEnabled(false);
                    btnSwOne.setEnabled(false);
                    btnSwTwo.setEnabled(false);
                    btnSwThree.setEnabled(false);
                    btnSwFour.setEnabled(false);
                }
                hidepDialog();
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
                hidepDialog();
            }
        });

    }

    private void setDeviceDetails() {
        showpDialog();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonHelpers.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        com.project.cse499.homecontrol.APIService service = retrofit.create(com.project.cse499.homecontrol.APIService.class);

        DevicesSatesUpdate device = new DevicesSatesUpdate();

        device.setDeviceOne(swOne.isChecked() ? 0 : 1);
        device.setDeviceTwo(swTwo.isChecked() ? 0 : 1);
        device.setDeviceThree(swThree.isChecked() ? 0 : 1);
        device.setDeviceFour(swFour.isChecked() ? 0 : 1);



        Call<DevicesSatesUpdate> call = service.updateDeviceStates(
                this.tokenValue,
                houseId,
                roomId,
                device.getDeviceOne(),
                device.getDeviceTwo(),
                device.getDeviceThree(),
                device.getDeviceFour()
        );

        call.enqueue(new Callback<DevicesSatesUpdate>() {
            @Override
            public void onResponse(Call<DevicesSatesUpdate> call, Response<DevicesSatesUpdate> response) {
                hidepDialog();
            }

            @Override
            public void onFailure(Call<DevicesSatesUpdate> call, Throwable t) {
                hidepDialog();
            }
        });
    }


    private void updateDeviceInDB() {
        if(!refreshingDataFromDB){
            Context context = getApplicationContext();
            CharSequence text = "Updated Data in DB";
            setDeviceDetails();
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        }
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
