package gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.util.Callback;
import model.MapData;
import model.Position;
import model.category.BlankCategory;
import model.category.BusCategory;
import model.category.TrainCategory;
import model.category.UndergroundCategory;
import model.place.DescribedPlace;
import model.place.NamedPlace;
import model.place.Place;


public class MapGUI extends Application {
    private FlowPane mainContainer;
    private Scene scene;
    private ImageView imageView = new ImageView();
    private Stage primaryStage;

    private MapData mapData;
    private StackPane mapStackPane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        mapData = new MapData();


        initializeMainContainer();
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void initializeMainContainer() {
        VBox mainContainer = new VBox();

        mainContainer.getChildren().addAll(getMenuBar(), getTopPane(), getBottomPane());
        scene = new Scene(mainContainer, 800, 500);

    }

    private MenuBar getMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem loadMapItem = new MenuItem("Load Map");

        loadMapItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Map");
            File file = fileChooser.showOpenDialog(primaryStage);
            loadImage(file.getPath());
        });

        MenuItem loadPlacesItem = new MenuItem("Load Places");
        loadPlacesItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Places");
            File file = fileChooser.showOpenDialog(primaryStage);
            mapData.setAllPlaces(loadPlaces(file));
            updateMarkers();
        });

        MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");
        menu.getItems().addAll(loadMapItem, loadPlacesItem, saveItem, exitItem);
        menuBar.getMenus().add(menu);
        return menuBar;
    }

    private Pane getTopPane() {
        HBox topContainer = new HBox();
//        topContainer.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
        topContainer.setMinWidth(800);
        topContainer.setPadding(new Insets(15, 12, 15, 12));
        topContainer.setSpacing(10.0);

        Button newButton = new Button("New");

        TextField textField = new TextField();

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String text = textField.getText();
            mapData.search(text);
            updateMarkers();
        });

        Button hideButton = new Button("Hide");
        hideButton.setOnAction(e -> {
            mapData.hide();
            updateMarkers();
        });

        Button removeButton = new Button("Remove");
        removeButton.setOnAction(event -> {
            mapData.remove();
            updateMarkers();
            System.out.println("-----------All Places after remove-------------");
            mapData.printAllPlaces();
            System.out.println("-----------Marked after remove-------------");
            System.out.println(mapData.getMarked().size());
        });
        Button coordinatesButton = new Button("Coordinates");
        coordinatesButton.setOnAction(e -> {
            showCoordinatesDialog();
        });

        topContainer.getChildren().addAll(newButton,
                getRadioBox(),
                textField,
                searchButton,
                hideButton,
                removeButton,
                coordinatesButton
        );

        return topContainer;
    }

    private void showCoordinatesDialog() {
        Dialog<Place> dialog = new Dialog<>();
        dialog.setTitle("Coordinates");
        dialog.setResizable(true);

        Label xLabel = new Label("X:    ");
        Label yLabel = new Label("Y:    ");
        TextField xField = new TextField();
        makeTextFieldNumberOnly(xField);
        TextField yField = new TextField();
        makeTextFieldNumberOnly(yField);
        GridPane gridPane = new GridPane();
        gridPane.add(xLabel, 1, 1);
        gridPane.add(xField, 2, 1);
        gridPane.add(yLabel, 1, 2);
        gridPane.add(yField, 2, 2);

        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType okType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.setResultConverter(buttonType -> {
            if (buttonType.equals(okType)) {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                mapData.getPlaceByCoordinates(
                        x,
                        y
                );
                updateMarkers();
            }
            return null;
        });

        dialog.getDialogPane().getButtonTypes().addAll(cancelType, okType);
        dialog.getDialogPane().setContent(gridPane);
        dialog.show();
    }

    private void makeTextFieldNumberOnly(TextField textField) {
        // force the field to be numeric only
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

    }

    private Pane getRadioBox() {

        VBox radioBox = new VBox();
        radioBox.setSpacing(10.0);
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton namedRadio = new RadioButton("Named");
        namedRadio.setToggleGroup(toggleGroup);
        RadioButton describedRadio = new RadioButton("Described");
        describedRadio.setToggleGroup(toggleGroup);
        radioBox.getChildren().addAll(namedRadio, describedRadio);

        return radioBox;
    }

    private Pane getBottomPane() {
        HBox bottomBox = new HBox();
        bottomBox.setMinWidth(800);
//        bottomBox.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));
        mapStackPane = new StackPane();

        mapStackPane.getChildren().addAll(imageView);
        bottomBox.getChildren().addAll(mapStackPane, getBottomRightPane());
        return bottomBox;
    }


    private void loadImage(String path) {
        try {
            FileInputStream inputStream = new FileInputStream(path);
            imageView.setImage(new Image(inputStream));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Pane getBottomRightPane() {
        VBox vbox = new VBox();
//        vbox.setBackground(new Background(new BackgroundFill(Color.WHEAT, null, null)));
        vbox.setPadding(new Insets(15, 12, 15, 12));
        vbox.setSpacing(10);
        Label label = new Label("Categories");

        ListView<String> catListView = new ListView<>();
        catListView.setPrefSize(50, 80);
        List<String> categories = new ArrayList<>();
        categories.add("Bus");
        categories.add("Underground");
        categories.add("Train");
        catListView.setItems(FXCollections.observableArrayList(categories));

        Button hideCatButton = new Button("Hide Category");
        hideCatButton.setOnAction(e -> {
            System.out.println(
                    catListView.getSelectionModel().getSelectedItem()
            );
        });
        vbox.getChildren().addAll(label, catListView, hideCatButton);
        return vbox;
    }

    private Map<Position, Place> loadPlaces(File file) {
        Map<Position, Place> placeMap = new HashMap<>();

        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split(",");
                // First part is the type of the place
                String placeType = split[0];
                // Second part is the category
                String cat = split[1];
                // Third part is the x coordinates
                int x = Integer.parseInt(split[2]);
                //  Fourth part is the y coordinates
                int y = Integer.parseInt(split[3]);
                //  Fifth part is the name
                String name = split[4];

                BlankCategory category;

                if (cat.equals("Bus")) {
                    category = new BusCategory();
                } else if (cat.equals("Underground")) {
                    category = new UndergroundCategory();
                } else if (cat.equals("Train")) {
                    category = new TrainCategory();
                } else {
                    category = new BlankCategory();
                }

                Place newPlace;

                if (placeType.equals("Named")) {
                    newPlace = new NamedPlace(
                            name,
                            category
                    );
                } else {
                    String description = split[5];

                    newPlace = new DescribedPlace(
                            name,
                            category,
                            description
                    );
                }

                Position scannedPosition = new Position(x, y);
                placeMap.put(scannedPosition, newPlace);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return placeMap;
    }


    private void updateMarkers() {
        mapStackPane.getChildren().clear();
        mapStackPane.getChildren().add(imageView);
        for (Map.Entry<Position, Place> entry : mapData.getAllPlaces().entrySet()) {
            Polyline polyline = new Polyline();
            polyline.setManaged(false);

            double placeX = entry.getKey().getX();
            double placeY = entry.getKey().getY();
            polyline.getPoints().addAll(
                    placeX - 8, placeY - 8,
                    placeX + 8, placeY - 8,
                    placeX, placeY,
                    placeX - 8, placeY - 8);
            if (mapData.getMarked().contains(entry.getValue())) {
                polyline.setFill(Color.YELLOW);
            } else {
                polyline.setFill(entry.getValue().getCategory().getColor());
            }

            if (!mapData.getHidden().contains(entry.getValue())) {
                mapStackPane.getChildren().add(polyline);
            }

        }
        System.out.println("Stack Pane Children " + mapStackPane.getChildren().size());
    }
}

