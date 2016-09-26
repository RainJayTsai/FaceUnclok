package org.rainjay.newfaceunlock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import org.rainjay.newfaceunlock.camera.BaseFaceView;
import org.rainjay.newfaceunlock.camera.CameraSurfaceView;

public class RegisterActivity extends AppCompatActivity {

    private RelativeLayout layout;
    private BaseFaceView baseFaceView;
    private CameraSurfaceView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        layout = (RelativeLayout) findViewById(R.id.activity_register);
        baseFaceView = new BaseFaceView(this);
        preview = new CameraSurfaceView(this,baseFaceView);

        layout.addView(preview);
        layout.addView(baseFaceView);
        //setContentView(layout);



    }
}
