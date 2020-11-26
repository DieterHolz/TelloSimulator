package tellosimulator.common;

import javafx.geometry.Point3D;

/**
 * A static helper class for frequently used vector calculations.
 */
public class VectorHelper {

    /**
     * Computes the cross product of the vector pointing upwards (0, -1, 0) and the specific input vector.
     * @param vector input vector
     * @return the vector pointing left of the input vector, parallel to the flying plane
     */
    public static Point3D getLeftNormalVector(Point3D vector){
        return getUpwardsNormalVector().crossProduct(vector);
    }

    /**
     * Computes the cross product of the the specific input vector and vector pointing upwards (0, -1, 0) .
     * @param vector input vector
     * @return the vector pointing right of the input vector, parallel to the flying plane
     */
    public static Point3D getRightNormalVector(Point3D vector){
        return vector.crossProduct(getUpwardsNormalVector());
    }

    /**
     * Convenience method to access the vector pointing straight upwards.
     * (equals the negative y-axis in the JavaFX coordinate system).
     * @return the normalized vector pointing upwards
     */
    public static Point3D getUpwardsNormalVector(){
        return new Point3D(0,-1,0);
    }

    /**
     * Convenience method to access the vector pointing straight downwards
     * (equals y-axis in the JavaFX coordinate system).
     * @return the normalized vector pointing upwards
     */
    public static Point3D getDownwardsNormalVector(){
        return new Point3D(0,1,0);
    }

    /**
     * Computes the center point of a circle given three points in 3D space.
     * @param p1 Point 1
     * @param p2 Point 2
     * @param p3 Point 3
     * @return the center point of the circle, or {@code null} if the given points are collinear.
     * @see <a href="https://github.com/sergarrido/random/tree/master/circle3d">Source</a>
     */
    public static Point3D getCenterOfCircle(Point3D p1, Point3D p2, Point3D p3) {
        // Estimate v1 and v2
        Point3D v1 = p2.subtract(p1);
        Point3D v2 = p3.subtract(p1);
        // Estimate dot products
        double v11 = v1.dotProduct(v1);
        double v22 = v2.dotProduct(v2);
        double v12 = v1.dotProduct(v2);
        // Estimate scalars k1 and k2
        double divider = (v11 * v22 - v12 * v12);
        if (divider == 0) {
            // no circle construction possible, points are collinear
            return null;
        }
        double base = 0.5/divider;
        double k1 = base * v22 * (v11 - v12);
        double k2 = base * v11 * (v22 - v12);
        // Estimate circle center
        Point3D center = p1.add((v1.multiply(k1))).add((v2.multiply(k2)));
        return center;
    }

    /**
     * Computes the radius of a circle given three points in 3D space.
     * @param p1 Point 1
     * @param p2 Point 2
     * @param p3 Point 3
     * @return the radius of the circle, or {@code null} if the given points are collinear.
     */
    public static Double getRadiusOfCircle(Point3D p1, Point3D p2, Point3D p3) {
        Point3D center = getCenterOfCircle(p1,p2,p3);
        if (center != null){
            return p1.distance(center);
        }
        return null;
    }

    /**
     * Rotates a vector around a given axis by a certain angle. Rotation is clockwise to the axis (viewed from above).
     * @param v the vector to be rotated
     * @param axis the axis to be rotated around
     * @param angle how much the vector should be rotated clockwise
     * @return the rotated vector
     */
    public static Point3D rotateVector(Point3D v, Point3D axis, double angle){
        double xRotated = axis.getX()*(axis.getX()*v.getX() + axis.getY()*v.getY() + axis.getZ()*v.getZ())*(1d - Math.cos(angle))
                + v.getX()*Math.cos(angle)
                + (-axis.getZ()*v.getY() + axis.getY()*v.getZ())*Math.sin(angle);
        double yRotated = axis.getY()*(axis.getX()*v.getX() + axis.getY()*v.getY() + axis.getZ()*v.getZ())*(1d - Math.cos(angle))
                + v.getY()*Math.cos(angle)
                + (axis.getZ()*v.getX() - axis.getX()*v.getZ())*Math.sin(angle);
        double zRotated = axis.getZ()*(axis.getX()*v.getX() + axis.getY()*v.getY() + axis.getZ()*v.getZ())*(1d - Math.cos(angle))
                + v.getZ()*Math.cos(angle)
                + (-axis.getY()*v.getX() + axis.getX()*v.getY())*Math.sin(angle);
        return new Point3D(xRotated, yRotated, zRotated);
    }

    /**
     * Rotates a vector around the y-axis by a certain angle. Rotation is clockwise to the y-axis (viewed from above).
     * @param v the vector to be rotated
     * @param angle how much the vector should be rotated clockwise
     * @return the rotated vector
     */
    public static Point3D rotateAroundYAxis(Point3D v, double angle) {
        double x1 = v.getX();
        double z1 = v.getZ();
        double x2 = Math.cos(angle * Math.PI / 180) * x1 - Math.sin(angle * Math.PI / 180) * z1;
        double z2 = Math.sin(angle * Math.PI / 180) * x1 + Math.cos(angle * Math.PI / 180) * z1;
        return new Point3D(x2, v.getY(), z2);
    }
}
