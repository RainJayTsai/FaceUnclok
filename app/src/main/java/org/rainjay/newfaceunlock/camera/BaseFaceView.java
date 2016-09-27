package org.rainjay.newfaceunlock.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.bytedeco.javacpp.presets.opencv_objdetect;
import org.rainjay.newfaceunlock.imageutil.IpUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;

/**
 * Created by RainJay on 2016/9/26.
 */
@SuppressWarnings("deprecation")
public class BaseFaceView extends View implements Camera.PreviewCallback {

    private CvMemStorage storage;
    private CvHaarClassifierCascade classifier;

    public static final int SUBSAMPLING_FACTOR = 4;
    private CvSeq faces;
    private IplImage grayImage = null;


    public BaseFaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        try {
            init(context);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("rainjay", "not found classifier file");
        }
    }
    public BaseFaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        try {
            init(context);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("rainjay", "not found classifier file");
        }
    }


    public BaseFaceView(Context context) {
        super(context);
        try {
            init(context);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("rainjay", "not found classifier file");
        }
    }

    private void init(Context context) throws IOException{
        Log.d("rainjay", "BaseFaceView Construct");
        File classifierFile = Loader.extractResource(getClass(),
                //"/org/bytedeco/javacv/facepreview/haarcascade_frontalface_alt.xml",
                "/org/rainjay/haarcascade_frontalface_alt.xml",
                context.getCacheDir(), "classifier", ".xml");

        /*File classifierFile = new File(context.getCacheDir(),
                "haarcascade_frontalface_alt.xml");*/
        if (classifierFile == null || classifierFile.length() <= 0) {
            throw new IOException("Could not extract the classifier file from Java resource.");
        }

        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);
        classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
        classifierFile.delete();
        if (classifier.isNull()) {
            throw new IOException("Could not load the classifier file.");
        }
        storage = CvMemStorage.create();


    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        try {
            Camera.Size size = camera.getParameters().getPreviewSize();
            Log.d("rainjay", "onPreviewFrame: ");
            processImage(data, size.width, size.height);
            camera.addCallbackBuffer(data);
        } catch (RuntimeException e) {
            // The camera has probably just been released, ignore.
        }


    }
    private int acounter = 0;
    private MatVector tmp = new MatVector(2);
    public void processImage(byte[] data, int width, int height) {
        // First, downsample our image and convert it into a grayscale IplImage
        int f = SUBSAMPLING_FACTOR;
        IplImage transposed = null;
        if (grayImage == null || grayImage.width() != width/f || grayImage.height() != height/f) {
            grayImage = IplImage.create(width/f, height/f, IPL_DEPTH_8U, 1);
            transposed = IplImage.create(height/f, width/f, IPL_DEPTH_8U, 1);
        }
        int imageWidth  = grayImage.width();
        int imageHeight = grayImage.height();
        int dataStride = f*width;
        int imageStride = grayImage.widthStep();
        ByteBuffer imageBuffer = grayImage.createBuffer();
        for (int y = 0; y < imageHeight; y++) {
            int dataLine = y*dataStride;
            int imageLine = y*imageStride;
            for (int x = 0; x < imageWidth; x++) {
                imageBuffer.put(imageLine + x, data[dataLine + f*x]);
            }
        }
        Log.d("rainjay", "processImage: "+ grayImage.height() + " " + grayImage.width());

        cvTranspose(grayImage, transposed);
        cvFlip(transposed, transposed, 0);
        grayImage = transposed;

        if(acounter < 2 ){
            tmp.put(acounter++, new Mat(grayImage));
        }

        cvClearMemStorage(storage);
        faces = cvHaarDetectObjects(grayImage, classifier, storage, 1.1, 3,
                CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH);

        postInvalidate();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(20);

        String s = "FacePreview - This side up.";
        float textWidth = paint.measureText(s);
        canvas.drawText(s, (getWidth()-textWidth)/2, 20, paint);

        if (faces != null) {

            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);
            float scaleX = (float)getWidth()/grayImage.width();
            float scaleY = (float)getHeight()/grayImage.height();
            int total = faces.total();
            Log.d("rainjay", "Find Face " + total);
            for (int i = 0; i < total; i++) {
                CvRect r = new CvRect(cvGetSeqElem(faces, i));
                int x = r.x(), y = r.y(), w = r.width(), h = r.height();
                canvas.drawRect(x*scaleX, y*scaleY, (x+w)*scaleX, (y+h)*scaleY, paint);
            }
        }
    }

    public IplImage captureFace(){
        if( faces.total() == 1)
            return IpUtil.cropFace(grayImage,new CvRect(cvGetSeqElem(faces, 0)));
        else
            return null;
    }
}
