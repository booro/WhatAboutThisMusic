package message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class Message implements Serializable {
	private static final long serialVersionUID = 8556754544103328489L;
	
	private User user;
    private MessageType type;
    private String msg;
	private Vector<VideoInfo> queue;
    private ArrayList<User> users;
    private VideoInfo vid;

	public Message() {
    }
    
    public Message(User user, MessageType type, String msg) {
    	this.user = user;
    	this.type = type;
    	this.msg = msg;
    }
    
    public Message(MessageType type, String msg) {
    	this.type = type;
    	this.msg = msg;
    }
    
    public Vector<VideoInfo> getQueue() {
		return queue;
	}

	public void setQueue(Vector<VideoInfo> queue) {
		this.queue = queue;
	}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMsg() {

        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
    
    public VideoInfo getVid() {
		return vid;
	}

	public void setVid(VideoInfo vid) {
		this.vid = vid;
	}

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
