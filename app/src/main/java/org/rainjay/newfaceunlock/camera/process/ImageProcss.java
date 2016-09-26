package org.rainjay.newfaceunlock.camera.process;

/**
 * Created by RainJay on 2016/9/26.
 */

public interface ImageProcss {
    public byte[] processImage(byte[] data, int width, int height);
}
