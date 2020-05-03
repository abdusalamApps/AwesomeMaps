package gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

import javafx.collections.FXCollections;
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
    private Scene scene;
    private ImageView imageView = new ImageView();
    private Stage primaryStage;

    private MapData mapData;
    private StackPane mapStackPane;

    private ToggleGroup typeToggleGroup;
    private BlankCategory newCategory;
    private ListView<String> catListView;
    private Button newButton;
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        mapData = new MapData();
        newCategory = new BlankCategory();
        catListView = new ListView<>();
        newButton = new Button("New");
        mapStackPane = new StackPane();

        defaultMapMouseActions();

        catListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            System.out.println(newV);
            if (newV.equals("Bus")) {
                newCategory = new BusCategory();
            } else if (newV.equals("Train")) {
                newCategory = new TrainCategory();
            } else if (newV.equals("Underground")) {
                newCategory = new UndergroundCategory();
            }
        });


        initializeMainContainer();
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void defaultMapMouseActions() {
        mapStackPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                for (Position position : mapData.getAllPlaces().keySet()) {
                    if (Math.abs(position.getX() - e.getX()) < 5 && Math.abs(position.getY() - e.getY()) < 5) {
                        Place place = mapData.getAllPlaces().get(position);
                        if (mapData.getMarked().contains(place)) {
                            mapData.getMarked().remove(place);
                        } else {
                            mapData.markPlace(place);
                        }
                        updateMarkers();
                        break;
                    }
                }

            } else if (e.getButton() == MouseButton.SECONDARY) {
                for (Position position : mapData.getAllPlaces().keySet()) {
                    if (Math.abs(position.getX() - e.getX()) < 5 && Math.abs(position.getY() - e.getY()) < 5) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        Place place = mapData.getAllPlaces().get(position);
                        StringBuilder builder = new StringBuilder();
                        String nameLine = "Name: " + place.getName();
                        builder.append(nameLine).append("\n");
                        String descriptionLine = "Description: ";
                        if (place.getClass().equals(DescribedPlace.class)) {
                            descriptionLine += ((DescribedPlace) place).getDescription();
                            builder.append(descriptionLine);
                        }
                        alert.setContentText(builder.toString());
                        alert.show();
                        break;
                    }
                }

            }
        });
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
        saveItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("(*.places)", "*.places");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(primaryStage);
            try {
                PrintWriter writer = new PrintWriter(file);

                for (Map.Entry<Position, Place> entry : mapData.getAllPlaces().entrySet()) {
                    Place place = entry.getValue();
                    if (place.getClass().equals(NamedPlace.class)) {
                        writer.append("Named").append(",");
                    } else if (place.getClass().equals(DescribedPlace.class)) {
                        writer.append("Described").append(",");
                    }
                    writer.append(place.getCategory().getName()).append(",");
                    writer.append(String.valueOf(entry.getKey().getX())).append(",");
                    writer.append(String.valueOf(entry.getKey().getY())).append(",");
                    writer.append(place.getName());
                    if (place.getClass().equals(NamedPlace.class)) {
                        writer.append("\n");
                    } else if (place.getClass().equals(DescribedPlace.class)) {
                        writer.append(((DescribedPlace) place).getDescription()).append("\n");
                    }
                }
                writer.close();

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> {
            System.exit(0);
        });
        menu.getItems().addAll(loadMapItem, loadPlacesItem, saveItem, exitItem);
        menuBar.getMenus().add(menu);
        return menuBar;
    }

    private void makeNamedPlace(MouseEvent event) {
        Dialog<String> dialog = new Dialog<>();
        HBox hBox = new HBox();
        Label nameLabel = new Label("Name:  ");
        TextField nameField = new TextField();
        hBox.getChildren().addAll(nameLabel, nameField);

        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType okType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.setResultConverter(buttonType -> {
            if (buttonType.equals(okType)) {
                Place newPlace = new NamedPlace(
                        nameField.getText(),
                        newCategory
                );
                mapData.add(event.getX(), event.getY(), newPlace);
                updateMarkers();
            }
            return null;
        });

        dialog.getDialogPane().getButtonTypes().addAll(cancelType, okType);
        dialog.getDialogPane().setContent(hBox);
        dialog.show();
    }

    private void makeDescribedPlace(MouseEvent event) {
        Dialog<String> dialog = new Dialog<>();
        GridPane gridPane = new GridPane();
        Label nameLabel = new Label("Name:  ");
        Label descriptionLabel = new Label("Description:    ");
        TextField nameField = new TextField();
        TextField descriptionField = new TextField();

        gridPane.add(nameLabel, 1, 1);
        gridPane.add(nameField, 2, 1);
        gridPane.add(descriptionLabel, 1, 2);
        gridPane.add(descriptionField, 2, 2);

        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType okType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.setResultConverter(buttonType -> {
            if (buttonType.equals(okType)) {
                Place newPlace = new DescribedPlace(
                        nameField.getText(),
                        newCategory,
                        descriptionField.getText()
                );
                mapData.add(event.getX(), event.getY(), newPlace);
                updateMarkers();
            }
            return null;
        });

        dialog.getDialogPane().getButtonTypes().addAll(cancelType, okType);
        dialog.getDialogPane().setContent(gridPane);
        dialog.show();
    }

    private void makeNewPlace() {
        scene.setCursor(Cursor.CROSSHAIR);
        mapStackPane.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                boolean placeExist = false;
                for (Position position : mapData.getAllPlaces().keySet()) {
                    if (Math.abs(position.getX() - e.getX()) < 5 && Math.abs(position.getY() - e.getY()) < 5) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("det är endast tillåtet med en plats per position ");
                        alert.show();
                        placeExist = true;
                        break;
                    }
                }
                if (!placeExist) {
                    RadioButton selectedToggle = (RadioButton) typeToggleGroup.getSelectedToggle();
                    if (selectedToggle.getText().equals("Named")) {
                        makeNamedPlace(e);
                        scene.setCursor(Cursor.DEFAULT);
                        defaultMapMouseActions();
                    } else if (selectedToggle.getText().equals("Described")) {
                        makeDescribedPlace(e);
                        scene.setCursor(Cursor.DEFAULT);
                        defaultMapMouseActions();
                    }
                }
            }
        });
    }

    private Pane getTopPane() {
        HBox topContainer = new HBox();
//        topContainer.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
        topContainer.setMinWidth(800);
        topContainer.setPadding(new Insets(15, 12, 15, 12));
        topContainer.setSpacing(10.0);

        newButton.setOnAction(e -> {
            makeNewPlace();
        });

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
                String placeByCoordinates = mapData.getPlaceByCoordinates(x, y);
                if (placeByCoordinates.equalsIgnoreCase("not found")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Place Not Found!");
                    alert.setContentText("There is no place in the coordinates you entered.");
                    alert.show();
                } else {
                    updateMarkers();
                }
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
        typeToggleGroup = new ToggleGroup();
        RadioButton namedRadio = new RadioButton("Named");
        namedRadio.setToggleGroup(typeToggleGroup);
        RadioButton describedRadio = new RadioButton("Described");
        describedRadio.setToggleGroup(typeToggleGroup);
        radioBox.getChildren().addAll(namedRadio, describedRadio);

        return radioBox;
    }

    private Pane getBottomPane() {
        HBox bottomBox = new HBox();
        bottomBox.setMinWidth(800);
//        bottomBox.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));

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

        catListView.setPrefSize(60, 80);
        List<String> categories = new ArrayList<>();
        categories.add("Bus");
        categories.add("Underground");
        categories.add("Train");
        catListView.setItems(FXCollections.observableArrayList(categories));

        Button hideCatButton = new Button("Hide Category");
        hideCatButton.setOnAction(e -> {
            mapData.hideCategory(catListView.getSelectionModel().getSelectedItem());
            updateMarkers();
        });

        catListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            mapData.showCategory(newV);
            updateMarkers();
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
    }
}

