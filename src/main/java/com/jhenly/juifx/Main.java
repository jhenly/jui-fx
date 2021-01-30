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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;


public class Main extends Application {
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private static final double ROOT_WIDTH = 500.0;
    private static final double ROOT_HEIGHT = 400.0;
    
    private static final List<String> RAND_STRS = List.of("Foo", "Bazzar", "Random", "A", "whats up?");
    
    private static ChangeListener<String> change;
    
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        
        HBox center = new HBox();
        center.setFillHeight(true);
        center.setPadding(new Insets(0.0, 0.0, 0.0, 0.0));
        center.setSpacing(10.0);
        
        VBox tfAndTSVBox = createTFAndTSVBox();
        VBox btnAndCBVBox = createBTNAndCBVBox();
        
        HBox.setHgrow(tfAndTSVBox, Priority.ALWAYS);
        HBox.setHgrow(btnAndCBVBox, Priority.ALWAYS);
        
        center.getChildren().addAll(tfAndTSVBox, btnAndCBVBox);
        
        BorderPane root = createBorderPane();
        root.setCenter(center);
        
        root.setMinWidth(ROOT_WIDTH);
        root.setMinHeight(ROOT_HEIGHT);
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("main.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.show();
        
        
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
