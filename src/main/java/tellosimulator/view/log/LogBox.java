package tellosimulator.view.log;

import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tellosimulator.log.LogLevel;
import tellosimulator.log.Log;
import tellosimulator.log.Logger;

/**
 * The VBox containing the simulator log with corresponding buttons to control the {@code LogListView}.
 *
 * @see LogListView
 */
public class LogBox extends VBox {
    Log log;
    Logger logger;
    LogListView logListView;

    ChoiceBox<LogLevel> filterLevel;
    ToggleButton showTimestamp;
    ToggleButton tail;
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
                        LogLevel.values()
                )
        );
        filterLevel.getSelectionModel().select(LogLevel.DEBUG);

        showTimestamp = new ToggleButton("Show Timestamp");

        tail = new ToggleButton("Autoscroll to Tail");

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

        controls.setMinHeight(HBox.USE_PREF_SIZE);

        VBox.setVgrow(logListView, Priority.ALWAYS);

        getChildren().addAll(layout);
    }

    private void setupValueChangeListeners() {
    }

    private void setupEventHandlers() {
    }

    private void setupBindings() {
        logListView.filterLevelProperty().bind(filterLevel.getSelectionModel().selectedItemProperty());

        logListView.showTimeStampProperty().bind(showTimestamp.selectedProperty());

        logListView.tailProperty().bind(tail.selectedProperty());

    }
}