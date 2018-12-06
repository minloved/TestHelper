package com.demo.notification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.demo.R;

public class NotificationSenderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_sender_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        NotificationUtil.showNotification(this);
    }

    public void onClick2(View v) {
        NotificationUtil.showNotificationProgress(this);
    }

    public void onClick3(View v) {
        NotificationUtil.showFullScreen(this);
    }

    public void onClick4(View v) {
        NotificationUtil.shwoNotify(this);
    }

    public void onClick5(View v){
        NotificationUtil.showBigPictureStyle(this);
    }

    public void onClick6(View v){
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                NotificationUtil.showNotification(NotificationSenderActivity.this);
            }
        },2000);
    }
}
