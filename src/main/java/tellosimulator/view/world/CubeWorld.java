/**
 * CubeWorld.java
 *
 * https://github.com/FXyz/FXyz/blob/master/FXyz-Core/src/main/java/org/fxyz3d/scene/CubeWorld.java
 *
 * Copyright (c) 2013-2016, F(X)yz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of F(X)yz, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL F(X)yz BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package tellosimulator.view.world;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;

public class CubeWorld extends Group {
    private Group cubeWorldChildren = new Group();


    private final DoubleProperty roomSizeX = new SimpleDoubleProperty();
    private final DoubleProperty roomSizeY = new SimpleDoubleProperty();
    private final DoubleProperty roomSizeZ = new SimpleDoubleProperty();
    private final DoubleProperty grildLineSpacing = new SimpleDoubleProperty();

    private double gridLinesOpacity = 1.0;
    private double gridPanelsOpacity = 0.25;

    private Color panelWallColor = new Color(47/255, 79/255, 79/255, gridPanelsOpacity);
    private Color panelFloorCeilingColor = new Color(169/255, 169/255, 169/255, gridPanelsOpacity);
    private Color gridLinesWallColor = new Color(47/255, 79/255, 79/255, gridLinesOpacity);
    private Color gridLinesCeilingFloorColor = new Color(211/255, 211/255, 211/255, gridLinesOpacity);

    private final double gridThickness = 0.2;
    private final double wallThickness = 0.1;

    private double cameraRX = 0;
    private double cameraRY = 0;
    private double cameraRZ = 0;

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
        setGrildLineSpacing(spacing);
        init();
    }

    public void init(){
        buildPanels(getRoomSizeX(),getRoomSizeY(),getRoomSizeZ(), wallThickness);
        buildGrids(getRoomSizeX(), getRoomSizeY(), getRoomSizeZ(), getGrildLineSpacing());

        //move CubeWorld
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
        ArrayList cyls = new ArrayList<>();
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
        ArrayList cyls = new ArrayList<>();
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
        ArrayList cyls = new ArrayList<>();
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

    public double getGrildLineSpacing() {
        return grildLineSpacing.get();
    }

    public DoubleProperty grildLineSpacingProperty() {
        return grildLineSpacing;
    }

    public void setGrildLineSpacing(double grildLineSpacing) {
        this.grildLineSpacing.set(grildLineSpacing);
    }
}
