package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sun.tools.jstat.Alignment;

import javax.swing.*;

public class MapGUI extends Application {
    private FlowPane mainContainer;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializeMainContainer();
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void initializeMainContainer() {
        VBox mainContainer = new VBox();
//        mainContainer.setOrientation(Orientation.VERTICAL);
        mainContainer.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN, null, null)));
        mainContainer.getChildren().addAll(getMenuBar(), getTopPane(), getBottomPane());
        scene = new Scene(mainContainer, 800, 500);

    }

    private MenuBar getMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem loadItem = new MenuItem("Load Map");
        MenuItem loadPlaces = new MenuItem("Load Places");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");
        menu.getItems().addAll(loadItem, loadPlaces, saveItem, exitItem);
        menuBar.getMenus().add(menu);
        return menuBar;
    }
    private Pane getTopPane() {
        HBox topContainer = new HBox();
        topContainer.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
        topContainer.setMinWidth(800);
        topContainer.setPadding(new Insets(15, 12, 15, 12));
        topContainer.setSpacing(10.0);

        Button newButton = new Button("New");

        VBox radioBox = new VBox();
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton namedRadio = new RadioButton("Named");
        namedRadio.setToggleGroup(toggleGroup);
        RadioButton describedRadio = new RadioButton("Described");
        describedRadio.setToggleGroup(toggleGroup);
        radioBox.getChildren().addAll(namedRadio, describedRadio);

        TextField textField = new TextField();

        Button searchButton = new Button("Search");
        Button hideButton = new Button("Hide");
        Button removeButton = new Button("Remove");
        Button coordinatesButton = new Button("Coordinates");


        topContainer.getChildren().addAll(newButton,
                radioBox,
                textField,
                searchButton,
                hideButton,
                removeButton,
                coordinatesButton
        );

        return topContainer;
    }

    private Pane getBottomPane() {
        HBox bottomBox = new HBox();
        bottomBox.setMinSize(800, 450);
        bottomBox.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));
        return bottomBox;
    }

}

