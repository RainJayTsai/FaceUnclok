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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestUserPermission requestUserPermission = new RequestUserPermission(this);
        requestUserPermission.verifyCameraPermissions();
        serviceBtn = (Button)findViewById(R.id.start_service_button);
        isServiceRunFlag = isMyServiceRunning(LockScreenService.class);
       if(isServiceRunFlag){
           serviceBtn.setText("Stop Service");
       }
    }

    public void registerOnClick(View view) {
        Log.d("rainjay","start register");
        startActivity(new Intent().setClass(MainActivity.this,RegisterActivity.class));

    }

    public void startServiceOnClick(View view) {
        if(isServiceRunFlag){
            stopService(new Intent(this,LockScreenService.class));
            serviceBtn.setText("Start Service");
        }
        else {
            startService(new Intent(this, LockScreenService.class));
            serviceBtn.setText("Stop Service");
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