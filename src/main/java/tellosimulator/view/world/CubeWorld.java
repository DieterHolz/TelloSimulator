package tellosimulator.view.world;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;

/**
 * A cubic world consisting of six walls. Modified version of the referenced CubeWorld linked as Source.
 *
 * @see <a href="https://github.com/FXyz/FXyz/blob/master/FXyz-Core/src/main/java/org/fxyz3d/scene/CubeWorld.java">Source</a>
 */
public class CubeWorld extends Group {
    private final DoubleProperty roomSizeX = new SimpleDoubleProperty();
    private final DoubleProperty roomSizeY = new SimpleDoubleProperty();
    private final DoubleProperty roomSizeZ = new SimpleDoubleProperty();
    private final DoubleProperty gridLineSpacing = new SimpleDoubleProperty();

    private final double gridLinesOpacity = 1.0;
    private final double gridPanelsOpacity = 0.25;

    private final Color panelWallColor = new Color(47f/255f, 79f/255f, 79f/255f, gridPanelsOpacity);
    private final Color panelFloorCeilingColor = new Color(169f/255f, 169f/255f, 169f/255f, gridPanelsOpacity);
    private final Color gridLinesWallColor = new Color(47f/255f, 79f/255f, 79f/255f, gridLinesOpacity);
    private final Color gridLinesCeilingFloorColor = new Color(211f/255f, 211f/255f, 211f/255f, gridLinesOpacity);

    private final double gridThickness = 0.2;
    private final double wallThickness = 0.1;

    private Box panelFront;
    private Box panelBack;
    private Box panelRight;
    private Box panelLeft;
    private Box panelCeiling;
    private Box panelFloor;

    private Group verticalLinesOfPanelFront      = new Group();
    private Group horizontalLinesOfPanelFront    = new Group();
    private Group verticalLinesOfPanelLeft       = new Group();
    private Group horizontalLinesOfPanelLeft     = new Group();
    private Group zAxisLinesOfPanelFloor         = new Group();
    private Group xAxisLinesOfPanelFloor         = new Group();
    private Group verticalLinesOfPanelBack       = new Group();
    private Group horizontalLinesOfPanelBack     = new Group();
    private Group verticalLinesOfPanelRight      = new Group();
    private Group horizontalLinesOfPanelRight    = new Group();
    private Group zAxisLinesOfPanelCeiling       = new Group();
    private Group xAxisLinesOfPanelCeiling       = new Group();

    public CubeWorld(double sizeX, double sizeY, double sizeZ, double spacing) {
        setRoomSizeX(sizeX);
        setRoomSizeY(sizeY);
        setRoomSizeZ(sizeZ);
        setGridLineSpacing(spacing);
        init();
    }

    public void init(){
        buildPanels(getRoomSizeX(),getRoomSizeY(),getRoomSizeZ(), wallThickness);
        buildGrids(getRoomSizeX(), getRoomSizeY(), getRoomSizeZ(), getGridLineSpacing());

        setTranslateX(-getRoomSizeX()/2);
        setTranslateY(5);
        setTranslateZ(-getRoomSizeZ()/2);
    }

    private void buildPanels(double width, double height, double depth, double wallDepth) {
        panelFront      = preparePanel(width, height, wallDepth, panelWallColor, width/2, -height/2, -wallDepth/2);
        panelBack       = preparePanel(width, height, wallDepth, panelWallColor, width/2, -height/2, depth+wallDepth/2);
        panelRight      = preparePanel(wallDepth, height, depth, panelWallColor, width+wallDepth/2, -height/2, depth/2);
        panelLeft       = preparePanel(wallDepth, height, depth, panelWallColor, 0-wallDepth/2, -height/2, depth/2);
        panelCeiling    = preparePanel(width, wallDepth, depth, panelFloorCeilingColor, width/2, -height-wallDepth/2, depth/2);
        panelFloor      = preparePanel(width, wallDepth, depth, panelFloorCeilingColor, width/2, 0+wallDepth/2, depth/2);

        getChildren().addAll(panelFront, panelBack, panelLeft, panelRight, panelCeiling, panelFloor);
    }

    private void buildGrids(double width, double height, double depth, double spacing) {

        PhongMaterial phongGridLinesWall = new PhongMaterial();
        phongGridLinesWall.setSpecularColor(gridLinesWallColor);
        phongGridLinesWall.setDiffuseColor(gridLinesWallColor);

        PhongMaterial phongGridLinesCeilingFloor = new PhongMaterial();
        phongGridLinesWall.setSpecularColor(gridLinesCeilingFloorColor);
        phongGridLinesWall.setDiffuseColor(gridLinesCeilingFloorColor);

        verticalLinesOfPanelFront   = gridLinesXVariable(width, spacing, height, phongGridLinesWall, -height/2, 0, null);
        verticalLinesOfPanelBack    = gridLinesXVariable(width, spacing, height, phongGridLinesWall, -height/2, depth, null);
        horizontalLinesOfPanelFront = gridLinesYVariable(height, spacing, width, phongGridLinesWall, width/2, 0, Rotate.Z_AXIS);
        horizontalLinesOfPanelBack  = gridLinesYVariable(height, spacing, width, phongGridLinesWall, width/2, depth, Rotate.Z_AXIS);

        verticalLinesOfPanelLeft    = gridLinesZVariable(depth, spacing, height, phongGridLinesWall, 0, -height/2, null);
        verticalLinesOfPanelRight   = gridLinesZVariable(depth, spacing, height, phongGridLinesWall, width, -height/2, null);
        horizontalLinesOfPanelLeft  = gridLinesYVariable(height, spacing, depth, phongGridLinesWall, 0, depth/2, Rotate.X_AXIS);
        horizontalLinesOfPanelRight = gridLinesYVariable(height, spacing, depth, phongGridLinesWall, width, depth/2, Rotate.X_AXIS);

        zAxisLinesOfPanelFloor      = gridLinesXVariable(width, spacing, depth, phongGridLinesCeilingFloor, 0, depth/2, Rotate.X_AXIS);
        zAxisLinesOfPanelCeiling    = gridLinesXVariable(width, spacing, depth, phongGridLinesCeilingFloor, -height, depth/2, Rotate.X_AXIS);
        xAxisLinesOfPanelFloor      = gridLinesZVariable(depth, spacing, width, phongGridLinesCeilingFloor, width/2, 0, Rotate.Z_AXIS);
        xAxisLinesOfPanelCeiling    = gridLinesZVariable(depth, spacing, width, phongGridLinesCeilingFloor, width/2, -height, Rotate.Z_AXIS);

        //Add the sub groups to the parent group
        getChildren().addAll(verticalLinesOfPanelFront, horizontalLinesOfPanelFront, verticalLinesOfPanelBack, horizontalLinesOfPanelBack, verticalLinesOfPanelLeft, verticalLinesOfPanelRight, horizontalLinesOfPanelLeft, horizontalLinesOfPanelRight, zAxisLinesOfPanelFloor, zAxisLinesOfPanelCeiling, xAxisLinesOfPanelFloor, xAxisLinesOfPanelCeiling);

    }

    private Box preparePanel(double xDimension, double yDimension, double zDimension, Color color, double xTranslate, double yTranslate, double zTranslate) {

        Box box = new Box(xDimension, yDimension, zDimension);

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);

        box.setMaterial(material);

        box.translateXProperty().set(xTranslate);
        box.translateYProperty().set(yTranslate);
        box.translateZProperty().set(zTranslate);

        return box;
    }

    private Group gridLinesXVariable(double sizeOfArea, double spacingBetweenLines, double lineLength, PhongMaterial lineMaterial, double translateY, double translateZ, Point3D rotationAxis) {
        ArrayList<Node> cyls = new ArrayList<>();
        for (int i = 0; i <= sizeOfArea; i += spacingBetweenLines) {
            Cylinder cyl = new Cylinder(gridThickness, lineLength);
            cyl.setMaterial(lineMaterial);
            cyl.setTranslateX(i);
            cyl.setTranslateY(translateY);
            cyl.setTranslateZ(translateZ);
            if(rotationAxis != null) {
                cyl.setRotationAxis(rotationAxis);
                cyl.setRotate(90);
            }
            cyls.add(cyl);
        }
        return new Group(cyls);
    }

    private Group gridLinesYVariable(double sizeOfArea, double spacingBetweenLines, double lineLength, PhongMaterial lineMaterial, double translateX, double translateZ, Point3D rotationAxis) {
        ArrayList<Node> cyls = new ArrayList<>();
        for (int i = 0; i <= sizeOfArea; i += spacingBetweenLines) {
            Cylinder cyl = new Cylinder(gridThickness, lineLength);
            cyl.setMaterial(lineMaterial);
            cyl.setTranslateX(translateX);
            cyl.setTranslateY(-i);
            cyl.setTranslateZ(translateZ);
            if(rotationAxis != null) {
                cyl.setRotationAxis(rotationAxis);
                cyl.setRotate(90);
            }
            cyls.add(cyl);
        }
        return new Group(cyls);
    }

    private Group gridLinesZVariable(double sizeOfArea, double spacingBetweenLines, double lineLength, PhongMaterial lineMaterial, double translateX, double translateY, Point3D rotationAxis) {
        ArrayList<Node> cyls = new ArrayList<>();
        for (int i = 0; i <= sizeOfArea; i += spacingBetweenLines) {
            Cylinder cyl = new Cylinder(gridThickness, lineLength);
            cyl.setMaterial(lineMaterial);
            cyl.setTranslateX(translateX);
            cyl.setTranslateY(translateY);
            cyl.setTranslateZ(i);
            if(rotationAxis != null) {
                cyl.setRotationAxis(rotationAxis);
                cyl.setRotate(90);
            }
            cyls.add(cyl);
        }
        return new Group(cyls);
    }

    //getter and setter

    public double getRoomSizeX() {
        return roomSizeX.get();
    }

    public DoubleProperty roomSizeXProperty() {
        return roomSizeX;
    }

    public void setRoomSizeX(double roomSizeX) {
        this.roomSizeX.set(roomSizeX);
    }

    public double getRoomSizeY() {
        return roomSizeY.get();
    }

    public DoubleProperty roomSizeYProperty() {
        return roomSizeY;
    }

    public void setRoomSizeY(double roomSizeY) {
        this.roomSizeY.set(roomSizeY);
    }

    public double getRoomSizeZ() {
        return roomSizeZ.get();
    }

    public DoubleProperty roomSizeZProperty() {
        return roomSizeZ;
    }

    public void setRoomSizeZ(double roomSizeZ) {
        this.roomSizeZ.set(roomSizeZ);
    }

    public double getGridLineSpacing() {
        return gridLineSpacing.get();
    }

    public DoubleProperty gridLineSpacingProperty() {
        return gridLineSpacing;
    }

    public void setGridLineSpacing(double gridLineSpacing) {
        this.gridLineSpacing.set(gridLineSpacing);
    }
}
