package com.project.cse499.homecontrol.Activities.Devices;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.project.cse499.homecontrol.R;
import com.project.cse499.homecontrol.helpers.CommonHelpers;
import com.project.cse499.homecontrol.model.Device;
import com.project.cse499.homecontrol.model.DeviceLogEntry;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeviceLog extends AppCompatActivity {

    private String tokenValue;
    private int roomId,houseId;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_log);

        SharedPreferences settings = getSharedPreferences(CommonHelpers.AUTH_TOKEN, 0);
        tokenValue = settings.getString("jwtToken", "");
        SharedPreferences settingsHouse = getSharedPreferences(CommonHelpers.HOUSE, 0);
        houseId = settingsHouse.getInt("HouseId",0);
        SharedPreferences settingsRoom = getSharedPreferences(CommonHelpers.ROOM, 0);
        roomId = settingsRoom.getInt("RoomId",0);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        collectLogFile();

    }

    private void collectLogFile(){
        showpDialog();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonHelpers.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        com.project.cse499.homecontrol.APIService service = retrofit.create(com.project.cse499.homecontrol.APIService.class);
        Call<List<DeviceLogEntry>> call = service.getDeviceLog(this.tokenValue,houseId,roomId);

        call.enqueue(new Callback<List<DeviceLogEntry>>() {
            @Override
            public void onResponse(Call<List<DeviceLogEntry>> call, Response<List<DeviceLogEntry>> response) {
                List<DeviceLogEntry> DeviceLogs = response.body();
                populateData(DeviceLogs);
                hidepDialog();
            }

            @Override
            public void onFailure(Call<List<DeviceLogEntry>> call, Throwable t) {
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

    private void populateData( List<DeviceLogEntry> DeviceLogs){
        TableLayout ll = (TableLayout) findViewById(R.id.displayLinear);
        while (ll.getChildCount() > 1) {
            ll.removeViewAt(1);
        }
        int i = 0, rowCount = 1;

        for (i = 0, rowCount = 1; i < DeviceLogs.size(); i++,rowCount++) {
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);
            TextView sensor1 = new TextView(this);
            sensor1.setBackgroundResource(R.drawable.back);
            sensor1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            TextView sensor2 = new TextView(this);
            sensor2.setBackgroundResource(R.drawable.back);
            sensor2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            TextView sensor3 = new TextView(this);
            sensor3.setBackgroundResource(R.drawable.back);
            sensor3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            TextView timespan = new TextView(this);
            timespan.setBackgroundResource(R.drawable.back);
            timespan.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            timespan.setPadding(2, 0, 2, 0);

            sensor1.setText(DeviceLogs.get(i).getDevice());
            sensor2.setText(DeviceLogs.get(i).getUser());
            String status = (DeviceLogs.get(i).getAction()==0)?"Off" : "On";
            sensor3.setText(status);
            timespan.setText(DeviceLogs.get(i).getLogtime());
            row.addView(sensor1);
            row.addView(sensor2);
            row.addView(sensor3);
            row.addView(timespan);
            ll.addView(row, rowCount);

        }
    }


}
