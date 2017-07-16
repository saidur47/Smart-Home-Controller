package com.project.cse499.homecontrol.Activities.Others;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.project.cse499.homecontrol.Activities.Authentication.MainActivity;
import com.project.cse499.homecontrol.Activities.Rooms.RoomActivity;
import com.project.cse499.homecontrol.R;
import com.project.cse499.homecontrol.helpers.CommonHelpers;
import com.project.cse499.homecontrol.model.House;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class LandingActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private Button btnGo,btnSettings,btnSignOut;
    private Spinner spinnerHomeList;

    String tokenValue="";

    private int[] houseIDs;
    private ArrayList<String> houseList;
    private ArrayAdapter<String> listAdapter;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        SharedPreferences settings = getSharedPreferences(CommonHelpers.AUTH_TOKEN, 0);
        tokenValue = settings.getString("jwtToken", "");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        spinnerHomeList = (Spinner) findViewById(R.id.btnHomeSelectSpinner);
        btnGo=(Button) findViewById(R.id.btnSetHome);
        btnSettings = (Button) findViewById(R.id.btnShowLog);
        btnSignOut = (Button) findViewById(R.id.btnSignOut);

        houseList = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, houseList);
        spinnerHomeList.setAdapter(listAdapter);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(CommonHelpers.HOUSE, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("HouseName", spinnerHomeList.getSelectedItem().toString());
                editor.putInt("HouseId",houseIDs[spinnerHomeList.getSelectedItemPosition()]);
                editor.commit();
                startActivity(new Intent(LandingActivity.this, RoomActivity.class));
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(CommonHelpers.AUTH_TOKEN, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(LandingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        populateSpinner();
    }
    private void populateSpinner(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonHelpers.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        com.project.cse499.homecontrol.APIService service = retrofit.create(com.project.cse499.homecontrol.APIService.class);
        Call<List<House>> call = service.getListOfHouses(this.tokenValue);
        showpDialog();
        call.enqueue(new Callback<List<House>>() {
            @Override
            public void onResponse(Call<List<House>> call, Response<List<House>> response) {
                List<House> listOfHouses = response.body();
                int c = 0;
                ArrayList<String> ls = new ArrayList<String>();
                houseIDs = new int[listOfHouses.size()];
                while (c < listOfHouses.size()) {
                    houseIDs[c] = listOfHouses.get(c).getHouseId();
                    ls.add(listOfHouses.get(c).getHouseName());
                    c++;
                }

                listAdapter.clear();
                houseList = ls;
                listAdapter.addAll(houseList);
                listAdapter.notifyDataSetChanged();

                hidepDialog();
            }

            @Override
            public void onFailure(Call<List<House>> call, Throwable t) {
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
