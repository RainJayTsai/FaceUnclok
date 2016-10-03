package org.rainjay.newfaceunlock;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.rainjay.newfaceunlock.service.LockScreenService;
import org.rainjay.newfaceunlock.service.RequestUserPermission;

public class MainActivity extends AppCompatActivity {

    private Button serviceBtn = null;
    private boolean isServiceRunFlag = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("rainjay", "onDestroy: Main");
        //android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("rainjay", "onBackPressed: Main");
        if(isServiceRunFlag){
            startService(new Intent(this,LockScreenService.class));

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("rainjay", "onCreate: Main");

        RequestUserPermission requestUserPermission = new RequestUserPermission(this);
        requestUserPermission.verifyCameraPermissions();
        serviceBtn = (Button)findViewById(R.id.start_service_button);
        isServiceRunFlag = isMyServiceRunning(LockScreenService.class);
        if(isServiceRunFlag){
            serviceBtn.setText("Stop Service");
        }
//        TinyDancer.create()
//                .redFlagPercentage(.1f) // set red indicator for 10%
//                .startingGravity(Gravity.TOP)
//                .startingXPosition(15)
//                .startingYPosition(15)
//                .show(this);
    }

    public void registerOnClick(View view) {
        Log.d("rainjay","start register");
        startActivity(new Intent().setClass(MainActivity.this,RegisterActivity.class));

    }

    public void startServiceOnClick(View view) {
        if(isServiceRunFlag){
            stopService(new Intent(this,LockScreenService.class));
            serviceBtn.setText("Start Service");
            isServiceRunFlag = false;
        }
        else {
            startService(new Intent(this, LockScreenService.class));
            serviceBtn.setText("Stop Service");
            isServiceRunFlag = true;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void testOnClick(View view) {
        startActivity(new Intent().setClass(MainActivity.this,RecongizerActivity.class));
    }
}