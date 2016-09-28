package org.rainjay.newfaceunlock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.rainjay.newfaceunlock.camera.BaseFaceView;
import org.rainjay.newfaceunlock.camera.CameraSurfaceView;
import org.rainjay.newfaceunlock.imageutil.FaceRecognizerSingleton;

public class RecongizerActivity extends Activity {

    private FaceRecognizer faceRecognizer =null;
    private RelativeLayout layout;
    private BaseFaceView baseFaceView;
    private CameraSurfaceView preview;
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recongizer);
        faceRecognizer = FaceRecognizerSingleton.getInstance();

        layout = (RelativeLayout) findViewById(R.id.activity_recongizer);
        baseFaceView = new BaseFaceView(this);
        preview = new CameraSurfaceView(this,baseFaceView);

        layout.addView(preview);
        layout.addView(baseFaceView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag)
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                faceRecognizer();

            }
            protected void faceRecognizer(){
                IplImage face = baseFaceView.captureFace();
                if( face != null){
                    int predict = faceRecognizer.predict(new Mat(face));
                    if (predict == 1){
                        flag = false;
                        finish();
                        int pid = android.os.Process.myPid();
                        android.os.Process.killProcess(pid);
                    }
                    else {
                        Toast.makeText(RecongizerActivity.this,"Login Fail!!",Toast.LENGTH_SHORT).show();
                    }

                }}

            }).start();
    }


    public void btnOnClick(View view) {
        finish();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }
}
