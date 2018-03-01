package edu.dartmouth.cs.moodyalarm;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

public class PopupActivity extends AppCompatActivity {

    public static Alarmhandler alarm = new Alarmhandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        alarm.start_alert(this);
    }

    @Override
    public void onBackPressed() {
        alarm.stop_alert(this);
        super.onBackPressed();
        finishActivity(0);
    }
}
