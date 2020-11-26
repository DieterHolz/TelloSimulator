package tellosimulator.common;

/**
 * A static helper class to access frequently used default values.
 */
public class DefaultValueHelper {

    /**
     * Specifies the drones takeoff distance.
     */
    public static final int TAKEOFF_DISTANCE = 30;

    /**
     * Specifies the drones default speed when no other value is set.
     */
    public static final int DEFAULT_SPEED = 10;

    /**
     * The standard acceleration due to Earth's gravity in cm/s2.
     */
    public static final double DEFAULT_FALL_ACCELERATION = 980.665;

    /**
     * Specifies the drones turn duration for a 360 degree yaw in ms.
     */
    public static final int TURN_DURATION = 2000;

    /**
     * Specifies the drones flip duration for a 360 degree flip in ms.
     */
    public static final int FLIP_DURATION = 1500;

    /**
     * Specifies the drones estimated battery lifetime in ms.
     */
    public static final int BATTERY_LIFETIME = 1200000;
}
