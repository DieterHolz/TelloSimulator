package tellosimulator.view.controls;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.text.NumberFormat;

public class RoomSizeControls extends GridPane {

    private Label roomWidthLabel;
    private Slider roomWidthSlider;
    private TextField roomWidth;
    private Label roomDepthLabel;
    private Slider roomDepthSlider;
    private TextField roomDepth;
    private Label roomHeightLabel;
    private Slider roomHeightSlider;
    private TextField roomHeight;
    private Label gridSizeLabel;
    private Slider gridSizeSlider;
    private TextField gridSize;

    public RoomSizeControls(){
        initializeSelf();
        initializeParts();
        layoutParts();
        setupBindings();
    }

    private void initializeSelf() {
        setHgap(10);
        setVgap(5);
        setPadding(new Insets(4,4,4,4));
        setMinWidth(300);
    }

    private void initializeParts() {
        roomWidthLabel = new Label("room width in m:");
        roomWidthSlider = new Slider(1, 25, 5);
        roomWidthSlider.setBlockIncrement(0.01);
        roomWidth = new TextField("5");

        roomDepthLabel = new Label("room depth in m:");
        roomDepthSlider = new Slider(1, 25, 5);
        roomDepth = new TextField("5");

        roomHeightLabel = new Label("room height in m:");
        roomHeightSlider = new Slider(1, 25, 3);
        roomHeight = new TextField("3");

        gridSizeLabel = new Label("grid size in cm:");
        gridSizeSlider = new Slider(5, 100, 40);
        gridSize = new TextField("40");
    }

    private void layoutParts() {
        add(roomWidthLabel, 1, 1);
        add(roomWidthSlider, 2, 1);
        add(roomWidth, 3, 1);
        add(roomDepthLabel, 1, 2);
        add(roomDepthSlider, 2, 2);
        add(roomDepth, 3, 2);
        add(roomHeightLabel, 1, 3);
        add(roomHeightSlider, 2, 3);
        add(roomHeight, 3, 3);
        add(gridSizeLabel, 1, 4);
        add(gridSizeSlider, 2, 4);
        add(gridSize, 3, 4);
    }

    private void setupBindings() {
        roomWidth.textProperty().bindBidirectional(roomWidthSlider.valueProperty(), NumberFormat.getNumberInstance());
        roomDepth.textProperty().bindBidirectional(roomDepthSlider.valueProperty(), NumberFormat.getNumberInstance());
        roomHeight.textProperty().bindBidirectional(roomHeightSlider.valueProperty(), NumberFormat.getNumberInstance());
        gridSize.textProperty().bindBidirectional(gridSizeSlider.valueProperty(), NumberFormat.getNumberInstance());
    }


    /* getter and setter*/
    public double getRoomWidth() {
        return roomWidthSlider.getValue();
    }

    public double getRoomDepth() {
        return roomDepthSlider.getValue();
    }

    public double getRoomHeight() {
        return roomHeightSlider.getValue();
    }

    public double getGridSize() {
        return gridSizeSlider.getValue();
    }
}
