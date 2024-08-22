package com.devduos.focustasks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.DateIntervalInfo;
import android.os.Bundle;
import android.view.View;

public class AppSecondStartingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_second_starting_screen);

        findViewById(R.id.iv_nextBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("infoShown","yes");
                startActivity(intent);
                finishAffinity();
            }
        });
    }
}