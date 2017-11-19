package client.playlistview;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import message.Message;
import message.MessageType;
import message.VideoInfo;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

import client.ClientManager;
import javafx.fxml.Initializable;

public class PlayListWindowController implements Initializable {
	private ClientManager clientManager;
	
	@FXML
    private Button insertToListButton;

    @FXML
    private AnchorPane playListPane;

    @FXML
    private ListView<VideoInfo> playListView;

    @FXML
    private TextField insertToListTextField;

    @FXML
    void onInsertToListButton(ActionEvent event) {
    		 String vidURL = insertToListTextField.getText();
    		 System.out.println(vidURL);
    	    	if(vidURL.contains("v=")){
    	    		String[] splittedVidURL = vidURL.split("v=");
    	    		System.out.println(splittedVidURL[0]);
    	    		System.out.println(splittedVidURL[1]);
    	    		Message msg = new Message();
    	    		msg.setType(MessageType.INSERTVID);
    	    		msg.setUser(clientManager.getUser());
    	    		
    	    		if(vidURL.contains("&")){
    	    			splittedVidURL = splittedVidURL[1].split("&");
    	    			msg.setMsg(splittedVidURL[0]);
    	    		}
    	    		else{
    	    			msg.setMsg(splittedVidURL[1]);
    	    		}
    	    		clientManager.sendMessage(msg);
    	    	}
    	    	else if(vidURL.contains("youtu.be/")){
    	    		String[] splittedVidURL = vidURL.split("youtu.be/");
    	    		System.out.println(splittedVidURL[1]);
    	    		Message msg = new Message();
    	    		msg.setType(MessageType.INSERTVID);
    	    		msg.setMsg(splittedVidURL[1]);
    	    		msg.setUser(clientManager.getUser());
    	    		clientManager.sendMessage(msg);
    	    	}
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}
	
	public void initController(final ClientManager clientManager){
		this.clientManager = clientManager;
	}
	
	public void setPlayListView(Vector<VideoInfo> vidQueue){
		//List<String> list = vidList;
		
        ObservableList<VideoInfo> myObservableList = FXCollections.observableList(vidQueue);
        playListView.setItems(myObservableList);
         
        playListView.setCellFactory(new Callback<ListView<VideoInfo>, ListCell<VideoInfo>>(){
 
            @Override
            public ListCell<VideoInfo> call(ListView<VideoInfo> p) {
                 
                ListCell<VideoInfo> cell = new ListCell<VideoInfo>(){
 
                    @Override
                    protected void updateItem(VideoInfo t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getVidId() + "  Requested By : " + t.getUser().getName());
                        }
                    }
 
                };
                 
                return cell;
            }
        });
		
	}

}
