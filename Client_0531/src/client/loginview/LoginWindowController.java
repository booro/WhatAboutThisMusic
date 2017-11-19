/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.loginview;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import client.ClientManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class LoginWindowController implements Initializable {
	@FXML
	private BorderPane borderPane;

	@FXML
	private Button closeButton;

	@FXML
	private ImageView Defaultview;

	@FXML
	private TextField usernameTextfield;

	@FXML
	private Label loginInfoLabel;

	private ClientManager cm;

	double xOffset;
	double yOffset;

	@FXML
	void loginButton(ActionEvent event) {
		String name = usernameTextfield.getText();

		if (name.equals("") || name.startsWith(" ")) {
			loginInfoLabel.setText("이름을 다시 입력하세요");
		} else {
			cm.setUserName(name);
			cm.connect();
		}
	}

	@FXML
	void minimizeWindow(ActionEvent event) {
		Window window = cm.getScene().getWindow();
		if (window instanceof Stage) {
			((Stage) window).setIconified(true);
		}
	}

	@FXML
	void closeSystem(ActionEvent event) {
		Platform.exit();
		System.exit(0);
	}

	public void initManager(final ClientManager clientManager) {
		this.cm = clientManager;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		borderPane.setOnMousePressed(event -> {
			Window window = cm.getScene().getWindow();
			if (window instanceof Stage){
			xOffset = ((Stage) window).getX() - event.getScreenX();
			yOffset = ((Stage) window).getY() - event.getScreenY();
			}
			borderPane.setCursor(Cursor.CLOSED_HAND);
		});

		borderPane.setOnMouseDragged(event -> {
			Window window = cm.getScene().getWindow();
			if (window instanceof Stage){
				((Stage) window).setX(event.getScreenX() + xOffset);
				((Stage) window).setY(event.getScreenY() + yOffset);
			}
		});

		borderPane.setOnMouseReleased(event -> {
			borderPane.setCursor(Cursor.DEFAULT);
		});

		
		int numberOfSquares = 30;
        while (numberOfSquares > 0){
            generateAnimation();
            numberOfSquares--;
        }
	}
	
	public void generateAnimation(){
        Random rand = new Random();
        int sizeOfSqaure = rand.nextInt(50) + 1;
        int speedOfSqaure = rand.nextInt(10) + 5;
        int startXPoint = rand.nextInt(420);
        int startYPoint = rand.nextInt(350);
        int direction = rand.nextInt(5) + 1;

        KeyValue moveXAxis = null;
        KeyValue moveYAxis = null;
        Rectangle r1 = null;

        switch (direction){
            case 1 :
                // MOVE LEFT TO RIGHT
                r1 = new Rectangle(0,startYPoint,sizeOfSqaure,sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), 350 -  sizeOfSqaure);
                break;
            case 2 :
                // MOVE TOP TO BOTTOM
                r1 = new Rectangle(startXPoint,0,sizeOfSqaure,sizeOfSqaure);
                moveYAxis = new KeyValue(r1.yProperty(), 420 - sizeOfSqaure);
                break;
            case 3 :
                // MOVE LEFT TO RIGHT, TOP TO BOTTOM
                r1 = new Rectangle(startXPoint,0,sizeOfSqaure,sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), 350 -  sizeOfSqaure);
                moveYAxis = new KeyValue(r1.yProperty(), 420 - sizeOfSqaure);
                break;
            case 4 :
                // MOVE BOTTOM TO TOP
                r1 = new Rectangle(startXPoint,420-sizeOfSqaure ,sizeOfSqaure,sizeOfSqaure);
                moveYAxis = new KeyValue(r1.xProperty(), 0);
                break;
            case 5 :
                // MOVE RIGHT TO LEFT
                r1 = new Rectangle(420-sizeOfSqaure,startYPoint,sizeOfSqaure,sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), 0);
                break;
            case 6 :
                //MOVE RIGHT TO LEFT, BOTTOM TO TOP
                r1 = new Rectangle(startXPoint,0,sizeOfSqaure,sizeOfSqaure);
                moveXAxis = new KeyValue(r1.xProperty(), 350 -  sizeOfSqaure);
                moveYAxis = new KeyValue(r1.yProperty(), 420 - sizeOfSqaure);
                break;

            default:
                System.out.println("default");
        }

        r1.setFill(Color.web("#F89406"));
        r1.setOpacity(0.1);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(speedOfSqaure * 1000), moveXAxis, moveYAxis);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
        borderPane.getChildren().add(borderPane.getChildren().size()-1,r1);
    }
}
