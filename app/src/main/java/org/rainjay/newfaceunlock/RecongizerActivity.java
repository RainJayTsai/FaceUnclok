package org.rainjay.newfaceunlock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.codemonkeylabs.fpslibrary.TinyDancer;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.rainjay.newfaceunlock.camera.CameraSurfaceView;
import org.rainjay.newfaceunlock.camera.FaceRecogntionView;
import org.rainjay.newfaceunlock.imageutil.FaceRecognition;
import org.rainjay.newfaceunlock.imageutil.FaceRecognizerSingleton;
import org.rainjay.newfaceunlock.service.LockScreenService;

import java.io.File;

public class RecongizerActivity extends Activity implements FaceRecognition {

    private FaceRecognizer faceRecognizer =null;
    private RelativeLayout layout;
    private FaceRecogntionView baseFaceView;
    private CameraSurfaceView preview;
    private boolean flag = true;
    private Handler mhandler;
    private  final  static String TAG = "rainjay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recongizer);


        if( new File(getFilesDir()+FaceRecognizerSingleton.getSaveFileName()).exists()){
            layout = (RelativeLayout) findViewById(R.id.activity_recongizer);
            baseFaceView = new FaceRecogntionView(this);
            baseFaceView.setActivity(this);
            preview = new CameraSurfaceView(this,baseFaceView);
            layout.addView(preview);
            layout.addView(baseFaceView);
            faceRecognizer = FaceRecognizerSingleton.getInstance();
            faceRecognizer.load(getFilesDir()+FaceRecognizerSingleton.getSaveFileName());
            mhandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    startService(new Intent(RecongizerActivity.this, LockScreenService.class));
                    baseFaceView.setVisibility(View.INVISIBLE);
//                    Intent main = new Intent(RecongizerActivity.this, MainActivity.class);
//                    main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    Bundle bundle = new Bundle();
//                    bundle.putBoolean("EXIT",true);
//                    main.putExtras(bundle);
//                    startActivity(main);
//                    finish();
                    onBackPressed();
                    finish();
                }
            };
        }

                TinyDancer.create()
                .redFlagPercentage(.1f) // set red indicator for 10%
                .startingGravity(Gravity.TOP)
                .startingXPosition(15)
                .startingYPosition(15)
                .show(this);




    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: recognition");
        flag = true;
        baseFaceView.setVisibility(View.VISIBLE);
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: recongition");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        startService(new Intent(RecongizerActivity.this,LockScreenService.class));
//                    }
//                });
//            }
//        }).start();
        moveTaskToBack(true);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        //finish();
        startService(new Intent(RecongizerActivity.this,LockScreenService.class));
        //android.os.Process.killProcess(android.os.Process.myPid());
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: recognition");
        preview.startPreview();
        super.onResume();
    }

    @Override
    protected void onPause(){
        Log.d(TAG, "onPause: recognition");
        preview.stopPreview();
        flag =false;
        super.onPause();

    }

    private Toast toast = null;
    private int count = 0;

    @Override
    public void execute(IplImage face) {
        int predict = faceRecognizer.predict(new Mat(face));
        Log.d(TAG, "faceRecognizer: predict:"+ predict);
        if (predict == 1){
            flag = false;
            if(toast != null){
                toast.cancel();
            }
            if( count != 0){
                Toast.makeText(RecongizerActivity.this, "Login Success!! Try: " + count, Toast.LENGTH_SHORT).show();
                count = 0;
            }else {
                Toast.makeText(RecongizerActivity.this, "Login Success!!", Toast.LENGTH_SHORT).show();
            }
            mhandler.sendMessage(Message.obtain());

        }
        else {
            baseFaceView.post(new Runnable() {
                @Override
                public void run() {
                    if(toast != null)toast.cancel();
                    toast = Toast.makeText(RecongizerActivity.this, "Login Fail!! " + count++ , Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }
}
