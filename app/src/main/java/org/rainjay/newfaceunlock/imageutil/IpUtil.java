package org.rainjay.newfaceunlock.imageutil;

import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.*;

/**
 * Created by RainJay on 2016/9/27.
 */
@SuppressWarnings("unused")
public class IpUtil {
    public static IplImage cropFace(IplImage frame, CvRect rect){
        cvSetImageROI(frame, rect);
        IplImage face = cvCreateImage(cvGetSize(frame), frame.depth(), frame.nChannels());
        cvCopy(frame, face);
//        cvResetImageROI(frame);
        return  face;
    }
}
