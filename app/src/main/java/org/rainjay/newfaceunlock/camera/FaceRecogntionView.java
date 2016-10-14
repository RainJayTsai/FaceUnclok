package org.rainjay.newfaceunlock.camera;

import android.content.Context;
import android.util.AttributeSet;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.rainjay.newfaceunlock.imageutil.FaceRecogntion;

/**
 * Created by RainJay on 2016/10/14.
 */

public class FaceRecogntionView extends BaseFaceView {

    private FaceRecogntion activity;

    public FaceRecogntionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceRecogntionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FaceRecogntionView(Context context) {
        super(context);
    }

    @Override
    public IplImage processImage(byte[] data, int width, int height) {
        IplImage face = super.processImage(data, width, height);
        if( face != null){
            activity.execute(face);
        }
        return face;

    }
}
