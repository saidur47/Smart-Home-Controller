package com.project.cse499.homecontrol.Activities.Others;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;

import com.project.cse499.homecontrol.Activities.Rooms.RoomActivity;
import com.project.cse499.homecontrol.R;

public class SettingsActivity extends AppCompatActivity {

    private Button btnChnPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnChnPassword = (Button) findViewById(R.id.btnChnPassword);
        btnChnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
