package tellosimulator.view.layout;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import tellosimulator.view.controls.RoomSizeControls;

import java.io.IOException;

public class SettingsPane extends BorderPane {

    private ParentPane parent;

    private RoomSizeControls roomSizeControls;
    private Button nextScreenButton;


    public SettingsPane(ParentPane parent) {
        this.parent = parent;

        initializeParts();
        layoutParts();
        setupValueChangedListeners();
        setupBindings();
    }

    private void initializeParts() {

        roomSizeControls = new RoomSizeControls();

        nextScreenButton = new Button("save and load simulator");
        nextScreenButton.setOnAction(event -> {
            try {
                parent.initializeSimulatorScreen(roomSizeControls.getRoomWidth(), roomSizeControls.getRoomDepth(), roomSizeControls.getRoomHeight(), roomSizeControls.getGridSize());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void layoutParts() {
        setPadding(new Insets(10));
        setCenter(roomSizeControls);
        setBottom(nextScreenButton);
    }

    private void setupValueChangedListeners() {
    }

    private void setupBindings() {
    }
}
