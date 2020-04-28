package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;


public class MapGUI extends Application {
    private FlowPane mainContainer;
    private Scene scene;
    private ImageView imageView = new ImageView();
    private Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        initializeMainContainer();
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void initializeMainContainer() {
        VBox mainContainer = new VBox();
//        mainContainer.setOrientation(Orientation.VERTICAL);
//        mainContainer.setBackground(new Background(new BackgroundFill(Color.ROSYBROWN, null, null)));
        mainContainer.getChildren().addAll(getMenuBar(), getTopPane(), getBottomPane());
        scene = new Scene(mainContainer, 800, 500);

    }

    private MenuBar getMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem loadMapItem = new MenuItem("Load Map");

        loadMapItem.setOnAction(e -> {
            System.out.println("Load Map Item clicked");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Map");
            File file = fileChooser.showOpenDialog(primaryStage);
            loadImage(file.getPath());
        });

        MenuItem loadPlaces = new MenuItem("Load Places");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem exitItem = new MenuItem("Exit");
        menu.getItems().addAll(loadMapItem, loadPlaces, saveItem, exitItem);
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

        VBox radioBox = new VBox();
        radioBox.setSpacing(10.0);
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
        bottomBox.setMinWidth(800);
//        bottomBox.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));
        bottomBox.getChildren().addAll(imageView, getBottomRightPane());
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

}

