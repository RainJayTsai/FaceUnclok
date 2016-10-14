package org.rainjay.newfaceunlock.imageutil;

import org.bytedeco.javacpp.opencv_core.IplImage;

/**
 * Created by RainJay on 2016/10/14.
 */

public interface FaceRecognition {
    public void execute(IplImage face);
}
