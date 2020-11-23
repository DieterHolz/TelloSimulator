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

    private double gridLinesOpacity = 1.0;
    private double gridPanelsOpacity = 0.25;

    private Color panelWallColor = new Color(47/255, 79/255, 79/255, gridPanelsOpacity);
    private Color panelFloorCeilingColor = new Color(169/255, 169/255, 169/255, gridPanelsOpacity);
    private Color gridLinesWallColor = new Color(47/255, 79/255, 79/255, gridLinesOpacity);
    private Color gridLinesCeilingFloorColor = new Color(211/255, 211/255, 211/255, gridLinesOpacity);

    private double roomSizeX;
    private double roomSizeY;
    private double roomSizeZ;
    private double gridLineSpacing;
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

    private boolean showPanelFront   = true;
    private boolean showPanelBack    = true;
    private boolean showPanelRight   = true;
    private boolean showPanelLeft    = true;
    private boolean showPanelCeiling = true;
    private boolean showPanelFloor   = true;

    private Group xAxesGroup = new Group();
    private Group yAxesGroup = new Group();
    private Group zAxesGroup = new Group();

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

    private boolean showVerticalLinesOfPanelFront    = true;
    private boolean showHorizontalLinesOfPanelFront  = true;
    private boolean showVerticalLinesOfPanelLeft     = true;
    private boolean showHorizontalLinesOfPanelLeft   = true;
    private boolean showZAxisLinesOfPanelFloor       = true;
    private boolean showXAxisLinesOfPanelFloor       = true;
    private boolean showVerticalLinesOfPanelBack     = true;
    private boolean showHorizontalLinesOfPanelBack   = true;
    private boolean showVerticalLinesOfPanelRight    = true;
    private boolean showHorizontalLinesOfPanelRight  = true;
    private boolean showZAxisLinesOfPanelCeiling     = true;
    private boolean showXAxisLinesOfPanelCeiling     = true;

    public CubeWorld(double sizeX, double sizeY, double sizeZ, double spacing) {
        roomSizeX = sizeX;
        roomSizeY = sizeY;
        roomSizeZ = sizeZ;
        gridLineSpacing = spacing;
        init();
    }

    private void init(){

        buildPanels(roomSizeX,roomSizeY,roomSizeZ, wallThickness);
        buildGrids(roomSizeX, roomSizeY, roomSizeZ, gridLineSpacing);
        buildEventHandlers();
        getChildren().add(cubeWorldChildren); //Holds ScatterPlot data

        //move CubeWorld
        setTranslateX(-roomSizeX/2);
        setTranslateY(5);
        setTranslateZ(-roomSizeZ/2);
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


    private void adjustPanelsByPos(double rx, double ry, double rz) {
        cameraRX = rx;
        cameraRY = ry;
        cameraRZ = rz;

        if (-85 < ry && ry < 85) {
            panelFront.setVisible(false);
            verticalLinesOfPanelFront.setVisible(false);
            horizontalLinesOfPanelFront.setVisible(false);
        } else {
            if(showPanelFront)
                panelFront.setVisible(true);
            if(showVerticalLinesOfPanelFront)
                verticalLinesOfPanelFront.setVisible(true);
            if(showHorizontalLinesOfPanelFront)
                horizontalLinesOfPanelFront.setVisible(true);
        }
        if ((95 < ry && ry < 180) || (-180 < ry && ry < -95)) {
            panelBack.setVisible(false);
            verticalLinesOfPanelBack.setVisible(false);
            horizontalLinesOfPanelBack.setVisible(false);

        } else {
            if(showPanelFront)
                panelBack.setVisible(true);
            if(showVerticalLinesOfPanelBack)
                verticalLinesOfPanelBack.setVisible(true);
            if(showHorizontalLinesOfPanelBack)
                horizontalLinesOfPanelBack.setVisible(true);
        }

        if (5 < ry && ry < 175) {
            panelRight.setVisible(false);
            verticalLinesOfPanelLeft.setVisible(false);
            horizontalLinesOfPanelLeft.setVisible(false);
        } else {
            if(showPanelRight)
                panelRight.setVisible(true);
            if(showVerticalLinesOfPanelLeft)
                verticalLinesOfPanelLeft.setVisible(true);
            if(showHorizontalLinesOfPanelLeft)
                horizontalLinesOfPanelLeft.setVisible(true);
        }
        if (-175 < ry && ry < -5) {
            panelLeft.setVisible(false);
            verticalLinesOfPanelRight.setVisible(false);
            horizontalLinesOfPanelRight.setVisible(false);
        } else {
            if(showPanelLeft)
                panelLeft.setVisible(true);
            if(showVerticalLinesOfPanelRight)
                verticalLinesOfPanelRight.setVisible(true);
            if(showHorizontalLinesOfPanelRight)
                horizontalLinesOfPanelRight.setVisible(true);
        }

        if (rx > 0) {
            panelCeiling.setVisible(false);
            zAxisLinesOfPanelFloor.setVisible(false);
            xAxisLinesOfPanelFloor.setVisible(false);
        } else {
            if(showPanelCeiling)
                panelCeiling.setVisible(true);
            if(showZAxisLinesOfPanelFloor)
                zAxisLinesOfPanelFloor.setVisible(true);
            if(showXAxisLinesOfPanelFloor)
                xAxisLinesOfPanelFloor.setVisible(true);
        }
        if (rx < 0) {
            panelFloor.setVisible(false);
            zAxisLinesOfPanelCeiling.setVisible(false);
            xAxisLinesOfPanelCeiling.setVisible(false);
        } else {
            if(showPanelFloor)
                panelFloor.setVisible(true);
            if(showZAxisLinesOfPanelCeiling)
                zAxisLinesOfPanelCeiling.setVisible(true);
            if(showXAxisLinesOfPanelCeiling)
                xAxisLinesOfPanelCeiling.setVisible(true);
        }
    }

    private void buildEventHandlers() {
        xAxesGroup.setOnMouseEntered((MouseEvent t) -> {
            panelFront.setVisible(true);
            panelBack.setVisible(true);
            t.consume();
        });
        xAxesGroup.setOnMouseExited((MouseEvent t) -> {
            adjustPanelsByPos(cameraRX, cameraRY, cameraRZ);
            t.consume();
        });
        yAxesGroup.setOnMouseEntered((MouseEvent t) -> {
            panelRight.setVisible(true);
            panelLeft.setVisible(true);
            t.consume();
        });
        yAxesGroup.setOnMouseExited((MouseEvent t) -> {
            adjustPanelsByPos(cameraRX, cameraRY, cameraRZ);
            t.consume();
        });
        zAxesGroup.setOnMouseEntered((MouseEvent t) -> {
            panelCeiling.setVisible(true);
            panelFloor.setVisible(true);
            t.consume();
        });
        zAxesGroup.setOnMouseExited((MouseEvent t) -> {
            adjustPanelsByPos(cameraRX, cameraRY, cameraRZ);
            t.consume();
        });
    }
}
