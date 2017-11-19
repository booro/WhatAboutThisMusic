package client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientMain extends Application {
    @Override
    public void start(Stage stage) throws Exception {
    	Scene scene = new Scene(new StackPane());
        
        ClientManager clientManager = new ClientManager(scene);
        clientManager.showLoginScreen();

        stage.getIcons().add(new Image(getClass().getClassLoader().getResource("images/plug.png").toString()));
        stage.setResizable(true);
        stage.setMaxWidth(2000);
        stage.setMaxHeight(2000);
        stage.setScene(scene);
        stage.show();
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

