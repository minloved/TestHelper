package com.demo.ghost;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class GhostEyesActivity extends AppCompatActivity{


    GhostEyesHelper mInstallHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstallHelper = GhostEyesHelper.getGhostInstallHelper(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(intent);
                return;
            } else {
                mInstallHelper.installGhost(getApplicationContext());
                finish();
            }
        } else {
            mInstallHelper.installGhost(getApplicationContext());
            finish();
        }

    }



}
