package com.project.cse499.homecontrol.Activities.Rooms;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.project.cse499.homecontrol.Activities.Devices.DevicesActivity;
import com.project.cse499.homecontrol.Activities.Authentication.MainActivity;
import com.project.cse499.homecontrol.R;
import com.project.cse499.homecontrol.helpers.CommonHelpers;
import com.project.cse499.homecontrol.model.Room;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RoomActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private String tokenValue = "";

    private ListView lv;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> roomList;

    private Context context;
    private Button btnSignOut;

    private int[] roomIDs;
    private String houseName;
    private int houseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        SharedPreferences settings = getSharedPreferences(CommonHelpers.AUTH_TOKEN, 0);
        tokenValue = settings.getString("jwtToken", "");
        SharedPreferences settingsHouse = getSharedPreferences(CommonHelpers.HOUSE, 0);
        houseName = settingsHouse.getString("HouseName","");
        houseId = settingsHouse.getInt("HouseId",0);


        try {
            getSupportActionBar().setTitle(houseName + ": List of Rooms");
        }catch (Exception ex){}

        try{
            getActionBar().setTitle(houseName + ": List of Rooms");
        }catch (Exception ex){}

        context = getApplicationContext();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);



        btnSignOut = (Button) findViewById(R.id.btnSignOutLR);
        roomList = new ArrayList<String>();
        lv = (ListView) findViewById(R.id.listOfRooms);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, roomList);

        lv.setAdapter(listAdapter);

        lv.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                SharedPreferences settings = getSharedPreferences(CommonHelpers.ROOM, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("RoomId", roomIDs[position]);
                editor.commit();
                startActivity(new Intent(RoomActivity.this, EditRoomDetails.class));
                return true;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                SharedPreferences settings = getSharedPreferences(CommonHelpers.ROOM, 0);
                SharedPreferences.Editor editor = settings.edit();
                String roomName = (String) parent.getItemAtPosition(position);
                editor.putString("RoomName",roomName);
                editor.putInt("RoomId", roomIDs[position]);
                editor.commit();
                startActivity(new Intent(RoomActivity.this, DevicesActivity.class));
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(CommonHelpers.AUTH_TOKEN, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(RoomActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        getListOfRoom();

    }

    private void getListOfRoom() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonHelpers.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        com.project.cse499.homecontrol.APIService service = retrofit.create(com.project.cse499.homecontrol.APIService.class);
        Call<List<Room>> call = service.getListOfRooms(this.tokenValue,houseId);
        showpDialog();
        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                List<Room> listOfRoom = response.body();
                int c = 0;
                ArrayList<String> ls = new ArrayList<String>();
                roomIDs = new int[listOfRoom.size()];
                while (c < listOfRoom.size()) {
                    roomIDs[c] = listOfRoom.get(c).getRoomId();
                    ls.add(listOfRoom.get(c).getRoomName());
                    c++;
                }

                listAdapter.clear();
                roomList = ls;
                listAdapter.addAll(roomList);
                listAdapter.notifyDataSetChanged();

                hidepDialog();
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
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
