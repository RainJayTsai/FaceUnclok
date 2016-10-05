package org.rainjay.newfaceunlock.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

import static com.codemonkeylabs.fpslibrary.Foreground.TAG;

/**
 * Created by RainJay on 2016/9/26.
 */

@SuppressWarnings("deprecation")
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Camera.PreviewCallback previewCallback;
    private Camera mCamera = null;
    private SurfaceHolder mHolder;
    private boolean isRunPreview = false;


    public CameraSurfaceView(Context context, Camera.PreviewCallback previewCallback) {
        super(context);
        this.previewCallback = previewCallback;

        this.mHolder = getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

//        int margin = 0;
//        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        lp.setMargins(margin, 0, 0, 0);
//        //this.setLayoutParams(lp);

    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("rainjay","start camera");
        if(mCamera == null){
            mCamera = Camera.open(1);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.set("orientation", "portrait");
            mCamera.setDisplayOrientation(90);
            mCamera.setParameters(parameters);
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                mCamera.release();
                mCamera = null;
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        List<Size> sizeList = parameters.getSupportedPreviewSizes();
        Size optimalSize = getOptimalPreviewSize(sizeList, height, width);
        Log.d("rainjay", "surfaceChanged: W:" + optimalSize.width + " H: " + optimalSize.height);
        parameters.setPreviewSize(optimalSize.width,optimalSize.height);

        mCamera.setParameters(parameters);
        if (previewCallback != null) {
            mCamera.setPreviewCallbackWithBuffer(previewCallback);
            Camera.Size size = parameters.getPreviewSize();
            byte[] data = new byte[size.width*size.height*
                    ImageFormat.getBitsPerPixel(parameters.getPreviewFormat())/8];
            mCamera.addCallbackBuffer(data);
        }
        startPreview();


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed: ");
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public boolean startPreview(){
        if (!isRunPreview && mCamera != null){
            isRunPreview = true;
            mCamera.startPreview();
        }
        return isRunPreview;
    }

    public boolean stopPreview(){
        if (isRunPreview && mCamera != null){
            mCamera.stopPreview();
            isRunPreview = false;
        }
        return isRunPreview;
    }
}
