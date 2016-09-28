package org.rainjay.newfaceunlock.imageutil;

import android.util.Log;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;

import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;

/**
 * Created by RainJay on 2016/9/28.
 */

public class FaceRecognizerSingleton {
    private static FaceRecognizer instance = null;

    private FaceRecognizerSingleton(){}

    public static FaceRecognizer getInstance(){
        if (instance == null){
            synchronized (FaceRecognizerSingleton.class){
                if (instance == null){
                    instance = createLBPHFaceRecognizer(1,8,8,8,95);
                    Log.d("rainjay", "getInstance: createLBPHFaceRecognizer");
                }
            }
        }
        return instance;
    }
    public static String getSaveFileName(){
        return "/LBPTrainData.xml";
    }

}
