package com.project.cse499.homecontrol.Activities.Authentication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.project.cse499.homecontrol.R;

public class RegisterSuccess extends AppCompatActivity {

    private Button btnContinueLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_succes);

        btnContinueLogin =(Button) findViewById(R.id.btnContinueLogin);
        btnContinueLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterSuccess.this,SigninActivity.class));
                finish();
            }
        });
    }
}
