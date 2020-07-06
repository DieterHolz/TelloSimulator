package tellosimulator.video;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.opencv.global.opencv_imgproc.resize;

public class VideoPublisher extends Thread implements TelloVideoConstants {
    private static final int WEBCAM_DEVICE_INDEX = 0;
    private static boolean running = false;

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        VideoPublisher.running = running;
    }

    public void run(){

        while (running){
            final OpenCVFrameGrabber camGrabber;

            try {
                camGrabber = OpenCVFrameGrabber.createDefault(WEBCAM_DEVICE_INDEX);
                camGrabber.setFrameRate(FRAME_RATE);
                camGrabber.start();

                // see: https://trac.ffmpeg.org/wiki/Encode/H.264
                final FFmpegFrameRecorder frameRecorder = FFmpegFrameRecorder.createDefault(URL, TELLO_CAMERA_WIDTH, TELLO_CAMERA_HEIGHT);
                frameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
                frameRecorder.setFormat("h264");
                frameRecorder.setFrameRate(FRAME_RATE);
                frameRecorder.setGopSize(GOP_LENGTH_IN_FRAMES);
                frameRecorder.setVideoOption("tune", "zerolatency");
                frameRecorder.setVideoOption("preset", "ultrafast");
                frameRecorder.setVideoOption("crf", "28");
                frameRecorder.setVideoOption("threads", "1");
                frameRecorder.start();

                camGrabber.grab();
                long startTime = System.currentTimeMillis();

                Frame capturedFrame;

                final OpenCVFrameConverter.ToMat converter  = new OpenCVFrameConverter.ToMat();
                final Mat                        resizedImg = new Mat(TELLO_CAMERA_WIDTH, TELLO_CAMERA_HEIGHT, 16);

                while ((capturedFrame = camGrabber.grab()) != null) {
                    Mat origImg = converter.convert(capturedFrame);

                    resize(origImg, resizedImg, resizedImg.size());

                    // Create timestamp for this frame
                    long videoTS = 1000 * (System.currentTimeMillis() - startTime);

                    // Check for AV drift
                    if (videoTS > frameRecorder.getTimestamp()) {
                        // We tell the recorder to write this frame at this timestamp
                        frameRecorder.setTimestamp(videoTS);
                    }

                    Frame convertedFrame = converter.convert(resizedImg);

                    // Send the frame
                    frameRecorder.record(convertedFrame);
                }

                frameRecorder.stop();
                camGrabber.stop();

            } catch (FrameGrabber.Exception | Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startPublisher() throws Exception, FrameGrabber.Exception {




    }
}