package com.project.cse499.homecontrol.Activities.Rooms;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.cse499.homecontrol.Activities.Devices.DevicesActivity;
import com.project.cse499.homecontrol.Activities.Devices.EditDeviceActivity;
import com.project.cse499.homecontrol.R;
import com.project.cse499.homecontrol.helpers.CommonHelpers;
import com.project.cse499.homecontrol.model.Room;
import com.project.cse499.homecontrol.model.UpdateDevice;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditRoomDetails extends AppCompatActivity {

    private EditText etName,etDetails;
    private Button btnSave,btnCancel;
    private int roomId,houseId;
    private String tokenValue="";
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        SharedPreferences settings = getSharedPreferences(CommonHelpers.AUTH_TOKEN, 0);
        tokenValue =settings.getString("jwtToken","Nothing Found") ;

        SharedPreferences settingsHouse = getSharedPreferences(CommonHelpers.HOUSE, 0);
        houseId = settingsHouse.getInt("HouseId",0);

        SharedPreferences settingsRoom = getSharedPreferences(CommonHelpers.ROOM, 0);
        roomId = settingsRoom.getInt("RoomId",0);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);



        btnSave = (Button) findViewById(R.id.btnSaveRoom);
        btnCancel = (Button) findViewById(R.id.btnCancelRoom);
        etName  = (EditText) findViewById(R.id.etRoomName);
        etDetails = (EditText) findViewById(R.id.etRoomDetails);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRoomDetails();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToRoomList();
            }
        });

        populateRoomDetails();

    }
    private void goBackToRoomList(){
        startActivity(new Intent(EditRoomDetails.this, RoomActivity.class));
        finish();
    }

    private void populateRoomDetails() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonHelpers.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        com.project.cse499.homecontrol.APIService service = retrofit.create(com.project.cse499.homecontrol.APIService.class);
        Call<Room> call = service.getRoomDetails(this.tokenValue,houseId,roomId);
        showpDialog();
        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                Room room = response.body();
                etName.setText(room.getRoomName());
                etDetails.setText(room.getRoomDetails());
                hidepDialog();
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                hidepDialog();
                showMessage("Huge Mistake");
            }
        });
    }

    private void updateRoomDetails(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonHelpers.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        com.project.cse499.homecontrol.APIService service = retrofit.create(com.project.cse499.homecontrol.APIService.class);
        Call<Room> call = service.updateRoomDetails(this.tokenValue,houseId,roomId,etName.getText().toString(),etDetails.getText().toString());

        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                hidepDialog();
                goBackToRoomList();
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
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

    private void showMessage(CharSequence msg) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }
}
