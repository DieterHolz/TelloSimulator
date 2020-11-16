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
    public Group cubeWorldChildren = new Group();

    public double gridLinesOpacity = 1.0;
    public double gridPanelsOpacity = 0.25;
    //Color.DARKSLATEGRAY
    public Color panelWallColor = new Color(0.18431373f, 0.30980393f, 0.30980393f,gridPanelsOpacity);
    //DARKGRAY
    public Color panelFloorCeilingColor = new Color(0.6627451f, 0.6627451f, 0.6627451f,gridPanelsOpacity);
    //Color.DARKSLATEGRAY
    public Color gridLinesWallColor = new Color(0.18431373f, 0.30980393f, 0.30980393f,gridLinesOpacity);
    //Color.LIGHTGRAY;
    public Color gridLinesCeilingFloorColor = new Color(0.827451f, 0.827451f, 0.827451f, gridLinesOpacity);

    private double roomSizeX;
    private double roomSizeY;
    private double roomSizeZ;
    private double gridLineSpacing;
    final double gridThickness = 0.5;

    double cameraRX = 0;
    double cameraRY = 0;
    double cameraRZ = 0;

    public Box panelFront;
    public Box panelBack;
    public Box panelRight;
    public Box panelLeft;
    public Box panelCeiling;
    public Box panelFloor;

    public boolean showPanelFront   = true;
    public boolean showPanelBack    = true;
    public boolean showPanelRight   = true;
    public boolean showPanelLeft    = true;
    public boolean showPanelCeiling = true;
    public boolean showPanelFloor   = true;

    public Group xAxesGroup = new Group();
    public Group yAxesGroup = new Group();
    public Group zAxesGroup = new Group();

    public Group verticalLinesOfPanelFront      = new Group();
    public Group horizontalLinesOfPanelFront    = new Group();
    public Group verticalLinesOfPanelLeft       = new Group();
    public Group horizontalLinesOfPanelLeft     = new Group();
    public Group zAxisLinesOfPanelFloor         = new Group();
    public Group xAxisLinesOfPanelFloor         = new Group();
    public Group verticalLinesOfPanelBack       = new Group();
    public Group horizontalLinesOfPanelBack     = new Group();
    public Group verticalLinesOfPanelRight      = new Group();
    public Group horizontalLinesOfPanelRight    = new Group();
    public Group zAxisLinesOfPanelCeiling       = new Group();
    public Group xAxisLinesOfPanelCeiling       = new Group();

    public boolean showVerticalLinesOfPanelFront    = true;
    public boolean showHorizontalLinesOfPanelFront  = true;
    public boolean showVerticalLinesOfPanelLeft     = true;
    public boolean showHorizontalLinesOfPanelLeft   = true;
    public boolean showZAxisLinesOfPanelFloor       = true;
    public boolean showXAxisLinesOfPanelFloor       = true;
    public boolean showVerticalLinesOfPanelBack     = true;
    public boolean showHorizontalLinesOfPanelBack   = true;
    public boolean showVerticalLinesOfPanelRight    = true;
    public boolean showHorizontalLinesOfPanelRight  = true;
    public boolean showZAxisLinesOfPanelCeiling     = true;
    public boolean showXAxisLinesOfPanelCeiling     = true;

    public CubeWorld(double sizeX, double sizeY, double sizeZ, double spacing) {
        roomSizeX = sizeX;
        roomSizeY = sizeY;
        roomSizeZ = sizeZ;
        gridLineSpacing = spacing;
        init();
    }

    private void init(){

        buildPanels(roomSizeX,roomSizeY,roomSizeZ, 0.1);
        buildGrids(roomSizeX, roomSizeY, roomSizeZ, gridLineSpacing);
        buildEventHandlers();
        getChildren().add(cubeWorldChildren); //Holds ScatterPlot data

        //move CubeWorld
        setTranslateX(-roomSizeX/2);
        setTranslateY(5);
        setTranslateZ(-roomSizeZ/2);
    }

    private void buildPanels(double width, double height, double depth, double wallDepth) {
        panelFront = preparePanel(width, height, wallDepth, panelWallColor, width/2, -height/2, -wallDepth/2);
        panelBack = preparePanel(width, height, wallDepth, panelWallColor, width/2, -height/2, depth+wallDepth/2);
        panelRight = preparePanel(wallDepth, height, depth, panelWallColor, width+wallDepth/2, -height/2, depth/2);
        panelLeft = preparePanel(wallDepth, height, depth, panelWallColor, 0-wallDepth/2, -height/2, depth/2);
        panelCeiling = preparePanel(width, wallDepth, depth, panelFloorCeilingColor, width/2, -height-wallDepth/2, depth/2);
        panelFloor = preparePanel(width, wallDepth, depth, panelFloorCeilingColor, width/2, 0+wallDepth/2, depth/2);

        getChildren().addAll(panelFront, panelBack, panelLeft, panelRight, panelCeiling, panelFloor);
    }

    private void buildGrids(double width, double height, double depth, double spacing) {

        PhongMaterial phongGridLinesWall = new PhongMaterial();
        phongGridLinesWall.setSpecularColor(gridLinesWallColor);
        phongGridLinesWall.setDiffuseColor(gridLinesWallColor);

        PhongMaterial phongGridLinesCeilingFloor = new PhongMaterial();
        phongGridLinesWall.setSpecularColor(gridLinesCeilingFloorColor);
        phongGridLinesWall.setDiffuseColor(gridLinesCeilingFloorColor);

        verticalLinesOfPanelFront = gridLinesXvariable(width, spacing, height, phongGridLinesWall, -height/2, 0, null);
        verticalLinesOfPanelBack = gridLinesXvariable(width, spacing, height, phongGridLinesWall, -height/2, depth, null);
        horizontalLinesOfPanelFront = gridLinesYvariable(height, spacing, width, phongGridLinesWall, width/2, 0, Rotate.Z_AXIS);
        horizontalLinesOfPanelBack = gridLinesYvariable(height, spacing, width, phongGridLinesWall, width/2, depth, Rotate.Z_AXIS);

        verticalLinesOfPanelLeft = gridLinesZvariable(depth, spacing, height, phongGridLinesWall, 0, -height/2, null);
        verticalLinesOfPanelRight = gridLinesZvariable(depth, spacing, height, phongGridLinesWall, width, -height/2, null);
        horizontalLinesOfPanelLeft = gridLinesYvariable(height, spacing, depth, phongGridLinesWall, 0, depth/2, Rotate.X_AXIS);
        horizontalLinesOfPanelRight = gridLinesYvariable(height, spacing, depth, phongGridLinesWall, width, depth/2, Rotate.X_AXIS);

        zAxisLinesOfPanelFloor = gridLinesXvariable(width, spacing, depth, phongGridLinesCeilingFloor, 0, depth/2, Rotate.X_AXIS);
        zAxisLinesOfPanelCeiling = gridLinesXvariable(width, spacing, depth, phongGridLinesCeilingFloor, -height, depth/2, Rotate.X_AXIS);
        xAxisLinesOfPanelFloor = gridLinesZvariable(depth, spacing, width, phongGridLinesCeilingFloor, width/2, 0, Rotate.Z_AXIS);
        xAxisLinesOfPanelCeiling = gridLinesZvariable(depth, spacing, width, phongGridLinesCeilingFloor, width/2, -height, Rotate.Z_AXIS);

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

    private Group gridLinesXvariable(double schleifendurchgaenge, double spacing, double linienlaenge, PhongMaterial phong, double verschiebungY, double verschiebungZ, Point3D rotationAxis) {
        ArrayList cyls = new ArrayList<>();
        for (int i = 0; i <= schleifendurchgaenge; i += spacing) {
            Cylinder cyl = new Cylinder(gridThickness, linienlaenge);
            cyl.setMaterial(phong);
            cyl.setTranslateX(i);
            cyl.setTranslateY(verschiebungY);
            cyl.setTranslateZ(verschiebungZ);
            if(rotationAxis != null) {
                cyl.setRotationAxis(rotationAxis);
                cyl.setRotate(90);
            }
            cyls.add(cyl);
        }
        return new Group(cyls);
    }

    private Group gridLinesYvariable(double schleifendurchgaenge, double spacing, double linienlaenge, PhongMaterial phong, double verschiebungX, double verschiebungZ, Point3D rotationAxis) {
        ArrayList cyls = new ArrayList<>();
        for (int i = 0; i <= schleifendurchgaenge; i += spacing) {
            Cylinder cyl = new Cylinder(gridThickness, linienlaenge);
            cyl.setMaterial(phong);
            cyl.setTranslateX(verschiebungX);
            cyl.setTranslateY(-i);
            cyl.setTranslateZ(verschiebungZ);
            if(rotationAxis != null) {
                cyl.setRotationAxis(rotationAxis);
                cyl.setRotate(90);
            }
            cyls.add(cyl);
        }
        return new Group(cyls);
    }

    private Group gridLinesZvariable(double schleifendurchgaenge, double spacing, double linienlaenge, PhongMaterial phong, double verschiebungX, double verschiebungY, Point3D rotationAxis) {
        ArrayList cyls = new ArrayList<>();
        for (int i = 0; i <= schleifendurchgaenge; i += spacing) {
            Cylinder cyl = new Cylinder(gridThickness, linienlaenge);
            cyl.setMaterial(phong);
            cyl.setTranslateX(verschiebungX);
            cyl.setTranslateY(verschiebungY);
            cyl.setTranslateZ(i);
            if(rotationAxis != null) {
                cyl.setRotationAxis(rotationAxis);
                cyl.setRotate(90);
            }
            cyls.add(cyl);
        }
        return new Group(cyls);
    }


    public void adjustPanelsByPos(double rx, double ry, double rz) {
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
