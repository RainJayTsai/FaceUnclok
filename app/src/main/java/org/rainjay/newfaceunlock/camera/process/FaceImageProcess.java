package org.rainjay.newfaceunlock.camera.process;

/**
 * Created by RainJay on 2016/9/26.
 */

public class FaceImageProcess implements ImageProcss{
    protected ImageProcss imageProcss;

    public FaceImageProcess(ImageProcss imageProcss) {
        this.imageProcss = imageProcss;
    }

    @Override
    public byte[] processImage(byte[] data, int width, int height) {
        return null;
    }
}
