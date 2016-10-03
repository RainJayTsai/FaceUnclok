package org.rainjay.newfaceunlock;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.rainjay.newfaceunlock.camera.BaseFaceView;
import org.rainjay.newfaceunlock.camera.CameraSurfaceView;
import org.rainjay.newfaceunlock.imageutil.FaceRecognizerSingleton;

import java.io.File;

public class RecongizerActivity extends Activity {

    private FaceRecognizer faceRecognizer =null;
    private RelativeLayout layout;
    private BaseFaceView baseFaceView;
    private CameraSurfaceView preview;
    private boolean flag = true;
    private Handler mhandler;
    private  final  static String TAG = "rainjay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recongizer);


        if( new File(getFilesDir()+FaceRecognizerSingleton.getSaveFileName()).exists()){
            faceRecognizer = FaceRecognizerSingleton.getInstance();
            faceRecognizer.load(getFilesDir()+FaceRecognizerSingleton.getSaveFileName());
            layout = (RelativeLayout) findViewById(R.id.activity_recongizer);
            baseFaceView = new BaseFaceView(this);
            preview = new CameraSurfaceView(this,baseFaceView);

            layout.addView(preview);
            layout.addView(baseFaceView);

            mhandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
//                    startService(new Intent(RecongizerActivity.this, LockScreenService.class));
//                    Intent main = new Intent(RecongizerActivity.this, MainActivity.class);
//                    main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    Bundle bundle = new Bundle();
//                    bundle.putBoolean("EXIT",true);
//                    main.putExtras(bundle);
//                    startActivity(main);
//                    finish();
                    onBackPressed();
                }
            };
        }






    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag)
                    try {
                        Thread.sleep(100);
                        faceRecognizer();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

            }
            protected void faceRecognizer(){
                IplImage face = baseFaceView.captureFace();
                if( face != null){
                    int predict = faceRecognizer.predict(new Mat(face));
                    if (predict == 1){
                        flag = false;
                        Log.d("rainjay", "faceRecognizer: login Success");
                        mhandler.sendMessage(Message.obtain());
                    }
                    else {
                        baseFaceView.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RecongizerActivity.this,"Login Fail!!",Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d("rainjay", "faceRecognizer: Login Fail");
                    }

                }}

        }).start();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: recongition");
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}