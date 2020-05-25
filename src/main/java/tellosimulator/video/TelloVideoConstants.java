package tellosimulator.video;

public interface TelloVideoConstants {

    int    TELLO_CAMERA_WIDTH  = 960;
    int    TELLO_CAMERA_HEIGHT = 720;
    String TELLO_IP            = "127.0.0.1";
    String PORT                = "11111";
    String URL                 = "udp://@" + TELLO_IP + ":" + PORT;

    int FRAME_RATE           = 12;  // das senkt die CPU-Last
    int GOP_LENGTH_IN_FRAMES = FRAME_RATE * 2;
}