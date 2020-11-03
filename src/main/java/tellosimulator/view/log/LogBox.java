package tellosimulator.view.log;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tellosimulator.log.Level;
import tellosimulator.log.Log;
import tellosimulator.log.Logger;

public class LogBox extends VBox {
    Log log;
    Logger logger;
    LogListView logListView;

    ChoiceBox<Level> filterLevel;
    ToggleButton showTimestamp;
    ToggleButton tail;
    Slider rate;
    Label rateLabel;
    VBox rateLayout;
    HBox controls;
    VBox layout;

    public LogBox(Log log) {
        this.log = log;
        initializeParts();
        layoutParts();
        setupValueChangeListeners();
        setupEventHandlers();
        setupBindings();
    }

    private void initializeParts() {
        logger = new Logger(log, "main");
        logListView = new LogListView(logger);

        filterLevel = new ChoiceBox<>(
                FXCollections.observableArrayList(
                        Level.values()
                )
        );
        filterLevel.getSelectionModel().select(Level.DEBUG);

        showTimestamp = new ToggleButton("Show Timestamp");

        tail = new ToggleButton("Autoscroll to Tail");

        rate = new Slider(0.1, 60, 60);

        rateLabel = new Label();
        rateLabel.setStyle("-fx-font-family: monospace;");

        rateLayout = new VBox(rate, rateLabel);

        controls = new HBox(
                10,
                filterLevel,
                showTimestamp,
                tail
        );

        layout = new VBox(
                10,
                controls,
                logListView
        );
    }

    private void layoutParts() {
        logListView.setPrefWidth(400);
        logListView.setPrefHeight(200);

        rateLayout.setAlignment(Pos.CENTER);

        controls.setMinHeight(HBox.USE_PREF_SIZE);

        VBox.setVgrow(logListView, Priority.ALWAYS);

        getChildren().addAll(layout);
    }

    private void setupValueChangeListeners() {

    }

    private void setupEventHandlers() {

    }

    private void setupBindings() {
        logListView.filterLevelProperty().bind(
                filterLevel.getSelectionModel().selectedItemProperty()
        );

        logListView.showTimeStampProperty().bind(showTimestamp.selectedProperty());

        logListView.tailProperty().bind(tail.selectedProperty());

        logListView.refreshRateProperty().bind(rate.valueProperty());

        rateLabel.textProperty().bind(Bindings.format("Update: %.2f fps", rate.valueProperty()));

    }
}
