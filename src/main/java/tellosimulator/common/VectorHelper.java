package tellosimulator.common;

import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;

public class VectorHelper {

    public static Point3D getLeftNormalVector(Point3D vector){
        return getUpwardsNormalVector().crossProduct(vector);
    }

    public static Point3D getRightNormalVector(Point3D vector){
        return vector.crossProduct(getUpwardsNormalVector());
    }

    public static Point3D getUpwardsNormalVector(){
        return new Point3D(0,-1,0);
    }

    public static Point3D getDownwardsNormalVector(){
        return Rotate.Y_AXIS;
    }

    // Source: https://github.com/sergarrido/random/tree/master/circle3d
    public static Point3D midPointOfcircumscribedCircle(Point3D p1, Point3D p2, Point3D p3) {
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

    public static Double radiusOfcircumscribedCircle(Point3D a, Point3D b, Point3D c) {
        Point3D midPoint = midPointOfcircumscribedCircle(a,b,c);
        if (midPoint != null){
            return a.distance(midPoint);
        }
        return null;
    }

    // rotate a vector v around an axis vector for a certain amount/angle (clockwise!)
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

    public static Point3D rotateAroundYAxis(Point3D v, double angle) {
        double x1 = v.getX();
        double z1 = v.getZ();
        double x2 = Math.cos(angle * Math.PI / 180) * x1 - Math.sin(angle * Math.PI / 180) * z1;
        double z2 = Math.sin(angle * Math.PI / 180) * x1 + Math.cos(angle * Math.PI / 180) * z1;
        return new Point3D(x2, v.getY(), z2);
    }
}
