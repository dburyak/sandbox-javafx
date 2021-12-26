package com.dburyak.sandbox.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javafx.beans.binding.Bindings.createStringBinding;
import static javafx.geometry.Orientation.VERTICAL;

public class HelloFx extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        var label = new Label("...");
        var btn = new Button("Toggle");
        var cntProp = new SimpleIntegerProperty();
        btn.setOnAction(a -> {
            print("button press action handler");
            cntProp.set(cntProp.get() + 1);
        });
        label.textProperty().bind(createStringBinding(() -> {
            print("cnt binding triggered");
            return cntProp.getValue().toString();
        }, cntProp));

        var exec = Executors.newScheduledThreadPool(8);
        exec.scheduleAtFixedRate(() -> {
            print("periodic trigger");
            Platform.runLater(() -> {
                cntProp.set(cntProp.get() + 1);
            });
        }, 1_000L, 3_000L, MILLISECONDS);


        primaryStage.setOnCloseRequest(e -> {
            print("shutdown");
            exec.shutdown();
            try {
                exec.awaitTermination(5_000L, MILLISECONDS);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });

        var scene = new Scene(new FlowPane(VERTICAL, label, btn), 640, 480);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private static void print(String msg) {
        System.out.println(Thread.currentThread() + " - " + msg);
    }
}
