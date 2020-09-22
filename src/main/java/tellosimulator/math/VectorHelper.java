package tellosimulator.math;

import javafx.geometry.Point3D;

public class VectorHelper {
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

        if(midPointAB.getZ()+r*directionVectorOfBisectionAB.getZ() == midPointAC.getZ()+s*directionVectorOfBisectionAC.getZ()) {
            System.out.println("schnittpunkt existiert");
        }

        return midPointAB.add(directionVectorOfBisectionAB.multiply(r));
    }

    private static Point3D midPointOfcircumscribedCircle(Point3D a, Point3D b, Point3D c) {

        Point3D midPointAB = a.midpoint(b);
        Point3D midPointAC = a.midpoint(c);

        Point3D directionVectorOfBisectionAB = directionVectorOfBisection(a, b, c);
        Point3D directionVectorOfBisectionAC = directionVectorOfBisection(a, c, b);

        return intersectionPointOf2Vectors(midPointAB, directionVectorOfBisectionAB, midPointAC, directionVectorOfBisectionAC);
    }

    public static double radiusOfcircumscribedCircle(Point3D a, Point3D b, Point3D c) {
        return a.distance(midPointOfcircumscribedCircle(a,b,c));
    }
}
