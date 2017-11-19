package client.mainview;

import java.net.URL;
import java.util.ResourceBundle;

import client.ClientManager;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import message.Message;
import message.MessageType;
import netscape.javascript.JSObject;
import javafx.fxml.Initializable;

public class MainWindowController implements Initializable {
	ClientManager clientManager;
	WebEngine webEngine;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		System.out.println("초기화중");
		sendMessageTextField.setOnKeyPressed((event) -> {
	        if (event.getCode() == KeyCode.ENTER) {
	        	String chat = sendMessageTextField.getText();
	        	if(!chat.equals("")){
		        	Message msg = new Message();
		        	msg.setUser(clientManager.getUser());
		        	msg.setMsg(chat);
		        	msg.setType(MessageType.CHAT);
		        	clientManager.sendMessage(msg);
		        	sendMessageTextField.clear();
	        	}
	        }
	    });
		System.out.println("초기화 완료");
	}
	
	@FXML
    private Label vidStatusLabel;
	
	@FXML
    private TextArea chatTextArea;

    @FXML
    private Button playListButton;

    @FXML
    private TextField sendMessageTextField;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Button userListButton;

    @FXML
    private Button likeButton;

    @FXML
    private WebView youtubePlayerWebView;

    @FXML
    private Label likeCountLabel;
    
    public void initSessionID(final ClientManager clientManager) {
    	this.clientManager = clientManager;
    	webEngine = youtubePlayerWebView.getEngine();
	    webEngine.getLoadWorker().stateProperty()
	        .addListener((obs, oldValue, newValue) -> {
	          if (newValue == Worker.State.SUCCEEDED) {
	            JSObject jsobj = (JSObject) webEngine.executeScript("window");
	            jsobj.setMember("CLIENT", this);
	          }
	        });
	    
	    URL urlHello = getClass().getResource("vidViewer.html");
      webEngine.load(urlHello.toExternalForm());
    }
    
    public void appendTextToChatTextField(String str){
    	chatTextArea.appendText(str+"\n");
    }

    @FXML
    void onLikeButton(ActionEvent event) {

    }

    @FXML
    void onPlayListButton(ActionEvent event) {
	    clientManager.popPlayListScreen();
    }

    @FXML
    void onUserListButton(ActionEvent event) {

    }
    
    public synchronized void requestInitToServer(){
    	Message msg = new Message();
    	msg.setType(MessageType.REQUESTINIT);
    	clientManager.sendMessage(msg);
    }
    
    public synchronized void initPlayer(String vidId, int currentTime){
    	webEngine.executeScript("Init('" + vidId + "', " + Integer.toString(currentTime+2) + ")");
    }
    
    public synchronized void receivedVid(String vid){
    	System.out.println("[receivedVid]"+vid);
    	webEngine.executeScript("player.cueVideoById('" + vid + "')");
    }
    
    public void sendVidInfo(int length){
    	System.out.println(length);
    	Message msg = new Message();
    	msg.setType(MessageType.VIDLENGTH);
    	msg.setMsg(Integer.toString(length));
    	clientManager.sendMessage(msg);
    }
    
    public void reportToServerError(String vidId){
    	Message msg = new Message();
    	msg.setType(MessageType.REPORTERROR);
    	msg.setMsg(vidId);
    }

}
