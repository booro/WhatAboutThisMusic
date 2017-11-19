package client.signinview;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import javafx.fxml.Initializable;

public class SigninWindowController implements Initializable {

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@FXML
    private TextField signin_PasswordTextField;

    @FXML
    private Button completeSigninButton;

    @FXML
    private AnchorPane signinPane;

    @FXML
    private TextField signin_IdTextField;

    @FXML
    void onCompleteSigninButton(ActionEvent event) {

    }

}
