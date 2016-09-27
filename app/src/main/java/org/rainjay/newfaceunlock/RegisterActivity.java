package org.rainjay.newfaceunlock;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.rainjay.newfaceunlock.camera.BaseFaceView;
import org.rainjay.newfaceunlock.camera.CameraSurfaceView;

import java.nio.IntBuffer;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgproc.CV_GRAY2RGBA;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;

public class RegisterActivity extends AppCompatActivity {

    private RelativeLayout layout;
    private BaseFaceView baseFaceView;
    private CameraSurfaceView preview;
    private final  static String TAG = "rainjay";
    private FaceRecognizer faceRecognizer = null;
    private MatVector trainImages = null;
    private Mat trainLabel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);
        faceRecognizer = createLBPHFaceRecognizer(1,8,8,8,95);

    }

    private int takeNum = 0;
    private IntBuffer labelsBuf = null;
    public void goRegisterCamera(View view) {
        EditText numberText = (EditText)findViewById(R.id.editText);

        try {
            takeNum = Integer.valueOf(numberText.getText().toString());
        } catch (NumberFormatException e) {
            takeNum = 5;
        }
        if( takeNum > 0)
            trainImages = new MatVector(takeNum);
            trainLabel = new Mat(takeNum,1,CV_32SC1);
            labelsBuf = trainLabel.createBuffer();
            createCameraView();
    }

    private  int count = 0;
    @SuppressWarnings("deprecation")
    public void takePhoto(View view) {
        IplImage face = baseFaceView.captureFace();
        if( face != null) {
            if(takeNum >= 1){
                takeNum--;
                trainImages.put(count,new Mat(face));
                labelsBuf.put(count, 1);
                count++;
            }
            if( takeNum == 1){
                this.destoryCamereView();
                faceRecognizer.train(trainImages,trainLabel);
            }
        }

    }

    private void createCameraView(){
        ((Button)findViewById(R.id.takePhotoButton)).setVisibility(View.VISIBLE);
        ((Button)findViewById(R.id.startbutton)).setVisibility(View.GONE);
        layout = (RelativeLayout) findViewById(R.id.activity_register);
        baseFaceView = new BaseFaceView(this);
        preview = new CameraSurfaceView(this,baseFaceView);

        layout.addView(preview);
        layout.addView(baseFaceView);
    }

    private  void destoryCamereView(){
        layout.removeView(preview);
        layout.removeView(baseFaceView);
        ((Button)findViewById(R.id.takePhotoButton)).setVisibility(View.GONE);
        ((Button)findViewById(R.id.startbutton)).setVisibility(View.VISIBLE);
    }

    private  void showTakePhoto(IplImage face){
        Bitmap bmp = Bitmap.createBitmap(face.width(), face.height(), Config.ARGB_8888);
        IplImage temp = cvCreateImage(cvGetSize(face), IPL_DEPTH_8U, 4);
        cvCvtColor(face, temp , CV_GRAY2RGBA);
        bmp.copyPixelsFromBuffer(temp.createBuffer());
        ImageView image = (ImageView) findViewById(R.id.faceimage);
        image.setImageBitmap(bmp);
    }
}
