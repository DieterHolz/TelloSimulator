package tellosimulator.common;

import javafx.geometry.Point3D;
import tellosimulator.controller.DroneController;

import java.math.BigDecimal;

public class VectorHelper {

    public static Point3D getLeftNormalVector(DroneController droneController){
        //TODO: calculate the vector pointing -90°(left) from the current orientation on the xz-plane
        return getUpwardsNormalVector().crossProduct(droneController.getCurrentOrientation());
    }

    public static Point3D getRightNormalVector(DroneController droneController){
        //TODO: calculate the vector pointing +90°(right) from the current orientation on the xz-plane
        return droneController.getCurrentOrientation().crossProduct(getUpwardsNormalVector());
    }

    public static Point3D getUpwardsNormalVector(){
        return new Point3D(0,-1,0);
    }

    public static Point3D getDownwardsNormalVector(){
        return new Point3D(0,1,0);
    }

    private static Point3D normalVectorOfAPlane(Point3D point1, Point3D point2, Point3D point3) {
        return ((point2.subtract(point1)).crossProduct(point3.subtract(point1)));
    }

    private static Point3D directionVectorOfBisection(Point3D point1, Point3D point2, Point3D point3) {
        return (point2.subtract(point1)).crossProduct(normalVectorOfAPlane(point2, point1, point3));
    }

    private static Point3D intersectionPointOf2Vectors(Point3D midPointAB, Point3D directionVectorOfBisectionAB, Point3D midPointAC, Point3D directionVectorOfBisectionAC) {

        double d = directionVectorOfBisectionAB.getX();
        double e = -directionVectorOfBisectionAC.getX();
        double h = midPointAC.getX()-midPointAB.getX();

        double f = directionVectorOfBisectionAB.getY();
        double g = -directionVectorOfBisectionAC.getY();
        double i = midPointAC.getY()-midPointAB.getY();

        double det = ((d) * (g) - (e) * (f));  //instead of 1/
        double r = ((g) * (h) - (e) * (i)) / det;
        double s = ((d) * (i) - (f) * (h)) / det;

        //TODO: this check does not work properly for some values (see Test curveInvalidRange())
        // check if intersection point exists
        BigDecimal foo = new BigDecimal(midPointAB.getZ() + r * directionVectorOfBisectionAB.getZ());
        BigDecimal bar = new BigDecimal(midPointAC.getZ() + s * directionVectorOfBisectionAC.getZ());
        if(foo.compareTo(bar) == 0) {
            //schnittpunkt existiert
        }
        return midPointAB.add(directionVectorOfBisectionAB.multiply(r));

    }

    public static Point3D midPointOfcircumscribedCircle(Point3D a, Point3D b, Point3D c) {

        Point3D midPointAB = a.midpoint(b);
        Point3D midPointAC = a.midpoint(c);

        Point3D directionVectorOfBisectionAB = directionVectorOfBisection(a, b, c);
        Point3D directionVectorOfBisectionAC = directionVectorOfBisection(a, c, b);

        return intersectionPointOf2Vectors(midPointAB, directionVectorOfBisectionAB, midPointAC, directionVectorOfBisectionAC);
    }

    public static double radiusOfcircumscribedCircle(Point3D a, Point3D b, Point3D c) {
        return a.distance(midPointOfcircumscribedCircle(a,b,c));
    }

    // https://stackoverflow.com/questions/31225062/rotating-a-vector-by-angle-and-axis-in-java
    public static Point3D rotateVector(Point3D v, Point3D axis, double angle){
        double xPrime = axis.getX()*(axis.getX()*v.getX() + axis.getY()*v.getY() + axis.getZ()*v.getZ())*(1d - Math.cos(angle))
                + v.getX()*Math.cos(angle)
                + (-axis.getZ()*v.getY() + axis.getY()*v.getZ())*Math.sin(angle);
        double yPrime = axis.getY()*(axis.getX()*v.getX() + axis.getY()*v.getY() + axis.getZ()*v.getZ())*(1d - Math.cos(angle))
                + v.getY()*Math.cos(angle)
                + (axis.getZ()*v.getX() - axis.getX()*v.getZ())*Math.sin(angle);
        double zPrime = axis.getZ()*(axis.getX()*v.getX() + axis.getY()*v.getY() + axis.getZ()*v.getZ())*(1d - Math.cos(angle))
                + v.getZ()*Math.cos(angle)
                + (-axis.getY()*v.getX() + axis.getX()*v.getY())*Math.sin(angle);
        return new Point3D(xPrime, yPrime, zPrime);
    }
}
