package com.jhenly.juifx;

import java.io.IOException;
import java.util.List;

import com.jhenly.juifx.control.FillButton;
import com.jhenly.juifx.control.InsetToggleSwitch;
import com.jhenly.juifx.control.SelectableButton;
import com.jhenly.juifx.control.TopPromptTextField;
import com.jhenly.juifx.layout.SelectHBox;
import com.jhenly.juifx.layout.SelectVBox;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;


public class Main extends Application {
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private static final double ROOT_WIDTH = 700.0;
    private static final double ROOT_HEIGHT = 600.0;
    
    private static TopPromptTextField one;
    private static TopPromptTextField twio;
    
    private static final List<String> RAND_STRS = List.of("Foo", "Bazzar", "Random", "A", "whats up?");
    
    private static ChangeListener<String> change;
    
    private static HBox createTPTFs() {
        
        VBox left = new VBox();
        left.getStyleClass().add("left");
        FillButton up = new FillButton("Fill'er Up!");
        up.getStyleClass().add("up");
        
//        SelectableButton down = new SelectableButton("Fill'er down!");
        Button down = new Button("Fill'er down!");
        down.getStyleClass().add("down");
        
        InsetToggleSwitch under = new InsetToggleSwitch();
        under.getStyleClass().add("under");
        
        Label text = new Label("Prompt Text");
        Label defaultStub = new Label("default:");
        Label defaultText = new Label("192.168.0.1/28");
        one = new TopPromptTextField(text, defaultStub, defaultText);
        one.setUseDefaultValue(true);
        one.setUsePromptAsPromptText(true);
        one.setDisable(false);
        one.getStyleClass().addAll("tp", "one");
        // VBox.setVgrow(one, Priority.ALWAYS);
        
        twio = new TopPromptTextField(new Label("Prompt Text"), new Label("default:"), new Label("192.168.0.1/28"));
        twio.setUseDefaultValue(false);
        twio.setUsePromptAsPromptText(true);
        twio.setDisable(false);
        twio.getStyleClass().addAll("tp", "twio");
        
        left.getChildren().addAll(up, one, down, /* twio, */ under);
        
        TopPromptTextField two = new TopPromptTextField();
        two.setDisable(false);
        two.setTFPromptText("Foo Bar Baz");
        two.setDefaultStubText("def:");
        two.getStyleClass().addAll("tp", "two");
        
        
        Label a = new Label("Prompt Text");
        Label b = new Label("default:");
        Label c = new Label("192.168.0.1/28");
        
        a.getStyleClass().add("aaa");
        b.getStyleClass().add("bbb");
        c.getStyleClass().add("ccc");
        
        VBox right = new VBox(a, /* two, */ b, c);
        
        
        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);
        HBox ret = new HBox(20.0, left, right);
        ret.setFillHeight(true);
        
        ret.getStyleClass().add("ret");
        ret.setMinSize(400.0, 400.0);
        return ret;
    }
    
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        VBox main = new VBox();
        main.setFillWidth(true);
        main.setSpacing(10.0);
        HBox topHbox = createTopHbox();
        VBox.setVgrow(topHbox, Priority.ALWAYS);
        
        HBox center = new HBox();
        center.setFillHeight(true);
        center.setPadding(new Insets(0.0, 0.0, 0.0, 0.0));
        center.setSpacing(10.0);
        
        VBox tfAndTSVBox = createTFAndTSVBox();
        VBox btnAndCBVBox = createBTNAndCBVBox();
        
        HBox.setHgrow(tfAndTSVBox, Priority.ALWAYS);
        HBox.setHgrow(btnAndCBVBox, Priority.ALWAYS);
        
        center.getChildren().addAll(tfAndTSVBox, btnAndCBVBox);
        VBox.setVgrow(center, Priority.ALWAYS);
        
        main.getChildren().addAll(topHbox, center);
        
        BorderPane root = createBorderPane();
        root.setMinWidth(ROOT_WIDTH);
        root.setMinHeight(ROOT_HEIGHT);
        
//        root.setCenter(main);
        root.setCenter(createTPTFs());
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        System.out.println(" :: ONE HEIGHT :: one.getHeight() = " + one.getHeight());
        // System.out.println(" :: TWIO HEIGHT :: twio.getHeight() = " +
        // twio.getHeight());
    }
    
    
    private static HBox createTopHbox() {
        SelectHBox shb = new SelectHBox();
        shb.getStyleClass().add("top-hbox");
        shb.setMinWidth(200);
        shb.setMinHeight(200);
        shb.setSpacing(20);
        
        FillButton fb1 = new FillButton();
        fb1.getStyleClass().addAll("top-button");
        StackPane sp1 = new StackPane();
        Region fb1r = new Region();
        fb1r.getStyleClass().addAll("icon", "icon-cogs");
        sp1.getChildren().add(fb1r);
        fb1.setGraphic(sp1);
        
        FillButton fb2 = new FillButton();
        fb2.getStyleClass().addAll("top-button");
        Region fb2r = new Region();
        fb2r.getStyleClass().addAll("icon", "icon-file-excel");
        fb2.setGraphic(fb2r);
        
        shb.getChildren().addAll(fb1, fb2);
        shb.setOpacity(1);
        
        return shb;
    }
    
    /** Creates the Button and ComboBox VBox. */
    private static VBox createBTNAndCBVBox() {
        SelectVBox container = new SelectVBox();
        
        Button plainBtn = new Button("Plane Button");
        container.getChildren().add(plainBtn);
        
        SelectableButton selectableButton = new SelectableButton("Selectable Button");
        container.getChildren().add(selectableButton);
        
        FillButton fillButton = new FillButton("Fill Button");
        container.getChildren().add(fillButton);
        
        SelectHBox subContainer = new SelectHBox(10.0, new FillButton("One"), createFourFiveSixSelectVbox(),
            new FillButton("Two"), new FillButton("Three"), createSevenEightNineSelectVbox());
        
        container.getChildren().add(subContainer);
        
        ComboBox<String> plainCBox = new ComboBox<>();
        container.getChildren().add(plainCBox);
        
        plainCBox.getItems().addAll(RAND_STRS);
        
        container.setFillWidth(true);
        container.setSpacing(20.0);
        container.setPadding(new Insets(30.0, 20.0, 30.0, 20.0));
        return container;
    }
    
    private static HBox createFourFiveSixSelectVbox() {
        Button four = new Button("Four Plain (Clear)");
        SelectableButton[] fiveSix = { new SelectableButton("Five"), new SelectableButton("Six") };
        
        SelectVBox svbox = new SelectVBox(10, four, fiveSix[0], fiveSix[1]);
        
        four.setOnAction(event -> svbox.clearSelected());
        
        return new HBox(svbox);
    }
    
    private static HBox createSevenEightNineSelectVbox() {
        SelectableButton[] sevenNine = { new SelectableButton("Seven"), new SelectableButton("Nine") };
        Button eight = new Button("Eight Plain (Clear)");
        
        SelectVBox svbox = new SelectVBox(10, sevenNine[0], eight, sevenNine[1]);
        
        eight.setOnAction(event -> svbox.clearSelected());
        
        return new HBox(svbox);
    }
    
    /** Creates the Textfield and ToggleSwitch VBox. */
    private static VBox createTFAndTSVBox() {
        TextField tf = new TextField();
        tf.setPromptText("Bla Bla");
        TextField foo = new TextField();
        foo.setPromptText("Foo");
        
        Label text = new Label("Prompt Text");
        Label defaultStub = new Label("default:");
        Label defaultText = new Label("192.168.0.1/28");
        TopPromptTextField tptf = new TopPromptTextField(text, defaultStub, defaultText);
        tptf.setUseDefaultValue(true);
        tptf.setUsePromptAsPromptText(true);
        tptf.setDisable(false);
        tptf.getStyleClass().add("tptf");
        
        TopPromptTextField second = new TopPromptTextField();
        second.setDisable(false);
        second.setTFPromptText("Foo Bar Baz");
        second.setDefaultStubText("def:");
        
        Pane verticalSpacer = createPane(100.0, 20.0);
        
        InsetToggleSwitch its = new InsetToggleSwitch("Enable Text Fields");
        its.setPadding(new Insets(0, 0, 0, -40));
        
        Pane verticalSpacer2 = createPane(100.0, 20.0);
        
        InsetToggleSwitch its2 = new InsetToggleSwitch("Enable Foo");
        its2.setPadding(new Insets(0, 0, 0, -20));
        
        
        second.tfEditableProperty().bind(its2.selectedProperty().not());
        
        VBox container = new VBox(its, verticalSpacer, tf, tptf, its2, verticalSpacer2, foo, second);
        container.setFillWidth(true);
        container.setPadding(new Insets(30.0, 20.0, 30.0, 20.0));
        
        tptf.setUseDefaultValue(false);
        tptf.setUseDefaultValue(true);
        
        change = (ob, o, n) -> {
            foo.setText(n);
        };
        
        // its.selectedProperty().addListener(e -> { if (!its.isSelected()) {
        // its2.setSelected(false); } });
        
        tf.disableProperty().bind(its.selectedProperty().not());
        tptf.disableProperty().bind(its.selectedProperty().not());
        its2.disableProperty().bind(its.selectedProperty().not());
        
        foo.disableProperty().bind(its2.selectedProperty().not());
        second.disableProperty().bind(its2.selectedProperty().not());
        
        tptf.tfTextProperty().addListener(change);
        
        tptf.onTFActionProperty().set(e -> tf.setText(tptf.getTFText()));
        
        tf.setOnAction(action -> second.setTFText(tf.getText()));
        
        return container;
    }
    
    private static BorderPane createBorderPane() {
        BorderPane bp = new BorderPane();
        bp.setPrefHeight(ROOT_HEIGHT);
        bp.setPrefWidth(ROOT_WIDTH);
        bp.setBackground(new Background(new BackgroundFill(Paint.valueOf("#f4f4f6"), null, null)));
        
        bp.setRight(createHeightPane());
        bp.setLeft(createHeightPane());
        bp.setTop(createWidthPane());
        bp.setBottom(createWidthPane());
        
        return bp;
    }
    
    private static Pane createHeightPane() {
        return createPane(100.0, 300.0);
    }
    
    private static Pane createWidthPane() {
        return createPane(ROOT_WIDTH, 100.0);
    }
    
    private static Pane createPane(double width, double height) {
        Pane ret = new Pane();
        ret.setMinWidth(width);
        ret.setMinHeight(height);
        return ret;
    }
    
}
