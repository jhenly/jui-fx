package org.jhenly.juifx;

import java.io.IOException;

import org.jhenly.juifx.control.InsetToggleSwitch;
import org.jhenly.juifx.control.TopPromptTextField;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;


public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }
    
    private static final double ROOT_WIDTH = 500.0;
    private static final double ROOT_HEIGHT = 400.0;

    @Override
    public void start(Stage primaryStage) throws IOException {
        
        TextField tf = new TextField();
        tf.setPromptText("Bla Bla");
        TextField foo = new TextField();
        foo.setPromptText("Foo");
        
        Label text = new Label("Prompt Text");
        Label defaultStub = new Label("default:");
        Label defaultText = new Label("192.168.0.1/28");
        TopPromptTextField tptf = new TopPromptTextField(text, defaultStub, defaultText);
        tptf.setUseDefaultText(true);
        tptf.setUsePromptAsPromptText(true);
        tptf.setDisable(false);
        
        TopPromptTextField second = new TopPromptTextField();
        second.setDisable(false);
        second.setTFPromptText("Foo Bar Baz");
        second.setDefaultStubText("def:");
        
        Pane verticalSpacer = createPane(100.0, 20.0);
        
        InsetToggleSwitch its = new InsetToggleSwitch();

        Pane verticalSpacer2 = createPane(100.0, 20.0);
        
        InsetToggleSwitch its2 = new InsetToggleSwitch("This is a Label");
        its2.setDisable(true);
        VBox container = new VBox(tf, tptf, foo, second, verticalSpacer, its, verticalSpacer2, its2);
        container.setFillWidth(true);
        container.setPadding(new Insets(30.0, 20.0, 30.0, 20.0));
        
        BorderPane root = createBorderPane();

        root.setCenter(container);

        root.setMinWidth(ROOT_WIDTH);
        root.setMinHeight(ROOT_HEIGHT);

        Scene scene = new Scene(root, ROOT_WIDTH, ROOT_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        tptf.setUseDefaultText(false);
        tptf.setUseDefaultText(true);
        
        System.out.println(tptf.getTFPrefColumnCount());
        System.out.println(tptf.getTFPromptText());
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
        return createPane(300.0, 100.0);
    }

    private static Pane createWidthPane() {
        return createPane(100.0, ROOT_WIDTH);
    }
    
    private static Pane createPane(double width, double height) {
        Pane ret = new Pane();
        ret.setMinWidth(width);
        ret.setMinHeight(height);
        return ret;
    }
    
}
