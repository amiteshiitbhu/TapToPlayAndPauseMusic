package com.visioapps.tappingapp;

import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private SideTapManager sideTapManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sideTapManager = new SideTapManager(this);
        registerReceiver(sideTapManager, SideTapManager.getFilter());

    }

    @Override
    protected void onStop() {
        super.onStop();
        sideTapManager.onSuspendInfrastructure();
//        unregisterReceiver(sideTapManager);
    }
}
