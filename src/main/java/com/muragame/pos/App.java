package com.muragame.pos;

import com.muragame.pos.bridge.JavaBridge;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.net.URL;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            WebView webView = new WebView();
            WebEngine webEngine = webView.getEngine();

            // Enable confirmation handlers
            webEngine.setConfirmHandler(message -> true);

            // Load the HTML file from resource
            URL url = getClass().getResource("/com/muragame/pos/muragamepos_transaksi.html");
            if (url == null) {
                System.err.println("Error: muragamepos_transaksi.html not found in resources!");
                System.exit(1);
            }
            webEngine.load(url.toExternalForm());

            // Create JavaBridge
            JavaBridge bridge = new JavaBridge();

            // Setup bridge between JS and Java
            webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == Worker.State.SUCCEEDED) {
                    JSObject win = (JSObject) webEngine.executeScript("window");
                    win.setMember("javaApp", bridge);
                    System.out.println("JavaBridge successfully injected into WebView!");
                    
                    // Trigger JS initialization now that the bridge is ready
                    webEngine.executeScript("initApp()");
                }
            });

            BorderPane root = new BorderPane();
            root.setCenter(webView);

            // Window dimension matching design
            Scene scene = new Scene(root, 1024, 700);
            
            primaryStage.setTitle("Muragame POS - Kasir Restoran");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
